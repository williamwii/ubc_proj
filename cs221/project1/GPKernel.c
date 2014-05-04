/* 221 STUDENTS: NO NEED TO UNDERSTAND ANYTHING IN THIS FILE. */

/**************************************************************************/
/*
 * CSE 142 Graphics Package
 * Port to X windows.  
 * Implementation Status:  Incomplete
 * This file contains all of the X specific calls.
 *
 * Author:  Ben Dugan
 * Modified: Jim Fix
 */
/**************************************************************************/

#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/Xos.h>
#include <X11/Xatom.h>
#include <X11/keysym.h>

#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>

#include "GPKernel.h"

/**************************************************************************/


/* 
 * Global Vars.
 */


struct timeval time0;


static GC gc;
static Visual *visual;
static Window window;
static Display *dpy;
static int screen;
static Pixmap pix;
static int window_x;
static int window_y;

/*
 * colorTable maps GP142 color indeces -> X color names, which are 
 * entries in the X color database
 */

static char *colorTable[MAX_COLORS] = 
{ "black", "white", "red", "green", "blue", "yellow", "magenta", "cyan", 
    "purple", "navy", "LightSlateBlue", "DarkSlateBlue", "turquoise",
    "orange", "brown", "pink", "gray80", "gold", "peach puff", "ForestGreen",
    "SeaGreen", "DarkOliveGreen", "gray40", "gray90" };

/*
 * pixTable maps GP142 color indeces -> X colormap pixel values
 */

unsigned long  pixTable[MAX_COLORS];


/*
 * fontTable maps GP142 point values -> X font names
 */

static char *fontTable[25] =
{ "6x10", "6x10", "6x10", "6x10", "6x10", "6x10", "6x10", "6x10", "6x10", 
    "6x10", "6x10", "6x10", "6x12", "6x13", "7x14", "9x15", "9x15", "9x15", 
    "9x15", "9x15", "10x20", "10x20", "10x20", "12x24", "12x24" };


/**************************************************************************/

/*
 * Random helper routines.
 */

/*
 * This code is due to:  Mark Lillibridge
 *
 * Resolve_Color: This routine takes a color name and returns the pixel #
 *                that when used in the window w will be of color name.
 *                (WARNING:  The colormap of w MAY be modified! )
 *                If colors are run out of, only the first n colors will be
 *                as correct as the hardware can make them where n depends
 *                on the display.  This routine does not require wind to
 *                be defined.
 */


unsigned long Resolve_Color(Window w, char *name) {
  XColor c_rgb, c_actual, c;
  Colormap colormap;
  XWindowAttributes wind_info;
  
  if (!strcmp(name, "white"))
    name="#ffffffffffff";
  if (!strcmp(name, "black"))
    name="#000000000000";
  
  XGetWindowAttributes(dpy, w, &wind_info);
  colormap = wind_info.colormap;
  
  if (!XParseColor(dpy, colormap, name, &c))
    fprintf(stderr, "%s: Bad color name\n", name);
  else if (!XAllocColor(dpy, colormap, &c))
    fprintf(stderr, "%s: Couldn't allocate color\n", name);
  return(c.pixel);
}

/*
 * setUpcolors initializes the pixTable with X color map pixel values 
 * corresponding to GP142 color indeces
 */

int setUpColors () {
  int i;
  for (i=0; i<MAX_COLORS; i++) {
    pixTable[i] = Resolve_Color(window, colorTable[i]);
  }
}


/*
 * setPenColor:  helper routine to change the "pen" color.  It really just
 * changes the foreground color of the graphics context
 */

int setPenColor(int color) {
  XGCValues values;
  values.foreground = pixTable[color];
  XChangeGC(dpy, gc, GCForeground, &values);
}


long delta (struct timeval t0, struct timeval t1)
{
  return ((1000000 * (t1.tv_sec - t0.tv_sec)) + (t1.tv_usec - t0.tv_usec));
}



/*
 * Jim: No longer work since WINDOW_DIM_ unused.  Also, I don't use these
 * functions anywhere
 */
void cartesianToWindow(int *x, int *y) {
  *x = *x + WINDOW_DIM_W;
  *y = -*y + WINDOW_DIM_H;
}

void windowToCartesian(int *x, int *y) {
  *x = *x - WINDOW_DIM_W;
  *y = -(*y - WINDOW_DIM_H);
}


/*
 * drawing Routines:  drawX draws and object of type X...  it assumes that
 * the pen color has been set, deals with newlines properly now... ugh.
 */

int drawText(int x, int y, int point, char *str) {
  XFontStruct *f;
  int height, len, first, i;
  f = XLoadQueryFont(dpy, fontTable[point]);
  if (f) {
    XSetFont(dpy, gc, f->fid);
    height = f->ascent + f->descent;
  }
  len = strlen(str);
  first = 0;
  for (i=0; i<len; i++) {
    if (*(str+i) == '\n' || i == len-1) {
      XDrawString(dpy, pix, gc, x, y, str+first, i-first+1);
      first = i+1;
      y = y+height;
    }
  }
}  
  
int drawPixel(int x, int y) {
  XDrawPoint(dpy, pix, gc, x, y);
}

int drawLine(int x1, int y1, int x2, int y2, int width) {
  XSetLineAttributes(dpy, gc, width, LineSolid, CapButt, JoinRound);
  XDrawLine(dpy, pix, gc, x1, y1, x2, y2);
}

int drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int width){
  XSetLineAttributes(dpy, gc, width, LineSolid, CapButt, JoinRound);
  if (width) {
    XDrawLine(dpy, pix, gc, x1, y1, x2, y2);
    XDrawLine(dpy, pix, gc, x2, y2, x3, y3);
    XDrawLine(dpy, pix, gc, x3, y3, x1, y1);
  }
  else {
    XPoint points[3];
    points[0].x = x1;
    points[0].y = y1;
    points[1].x = x2;
    points[1].y = y2;
    points[2].x = x3;
    points[2].y = y3;
    XFillPolygon(dpy, pix, gc, points, 3, Convex, CoordModeOrigin);
  }
}

int drawRect(int x1, int y1, int w, int h, int width) {
  XSetLineAttributes(dpy, gc, width, LineSolid, CapButt, JoinRound);
  if (width)
    XDrawRectangle(dpy, pix, gc, x1, y1, w, h);
  else
    XFillRectangle(dpy, pix, gc, x1, y1, w, h);
}

int drawOval(int x1, int y1, int w, int h, int width) {
  XSetLineAttributes(dpy, gc, width, LineSolid, CapButt, JoinRound);
  if (width)
    XDrawArc(dpy, pix, gc, x1, y1, w, h, 0, 360*64);
  else
    XFillArc(dpy, pix, gc, x1, y1, w, h, 0, 360*64);
}


int flush() {
  XCopyArea(dpy, pix, window, gc, 0, 0,  window_x, window_y, 0 ,0);
  XFlush(dpy);
}

int clear() {
  setPenColor(WHITE);
  drawRect(0, 0,  window_x, window_y, 0);
/***
  For some reason, this doesn't work:
  XClearArea(dpy, window, 0, 0, window_x, window_y, False);
***/
  XFlush(dpy);
}  

/**************************************************************************/

/*
 * getNextEvent:  not quite fully implemented yet, doesn't do CTL-C yet...
 */

int getNextEvent(eventRecord *event) {
  XEvent report;
  int bufsize = 20;
  int charcount;
  char buffer[20];
  KeySym keysym;
  XComposeStatus compose;
  struct timeval time1;
  struct timezone tz;
  int dpy_fd, nfound;
  fd_set rd_mask;


/* check for exposures */

  if (XCheckWindowEvent(dpy, window, ExposureMask, &report)) {
    if (report.type == Expose) 
      return EXPOSE;
  } 
  time1.tv_sec = 0;
  time1.tv_usec = 100000;
  FD_ZERO(&rd_mask);
  dpy_fd = ConnectionNumber(dpy);
  FD_SET(dpy_fd, &rd_mask);
  nfound = select(dpy_fd+1, &rd_mask, NULL, NULL, &time1);
  if (nfound) {
    if (XCheckWindowEvent(dpy, window, 
			  ButtonPressMask|KeyPressMask,
			  &report)) {
      switch (report.type) {
      case KeyPress:
	/*
	fprintf(stderr, ".Key\n");
	*/
	charcount = XLookupString((XKeyEvent*)(&report), buffer, bufsize, 
				  &keysym, &compose);
	if (charcount) {
	  /*  fprintf(stderr, "*** %s\n", buffer);*/
	  event->type = KEYPRESS;
	  event->ch = buffer[0];
	  return KEYPRESS;
	}
	else {
	  /*
	  fprintf(stderr, "*** Unprintable %x ***\n", keysym);
	  */
	  break;
	}
      case ButtonPress:
	/*
	fprintf(stderr, ".Button\n");
	*/
	event->type = MOUSE;
	event->x = report.xbutton.x;
	event->y = report.xbutton.y;
	/* windowToCartesian(&(event->x), &(event->y));*/
	/*
	fprintf(stderr, "%d %d\n", event->x, event->y);
	*/
	return MOUSE;
      default:
	/*
	fprintf(stderr, ".Strange\n");
	fprintf(stderr, "Strange Event\n");
	*/
	event->type = PERIODIC;
	return PERIODIC;
      }
    }
  }
  event->type = PERIODIC;
  return PERIODIC;
}



void initWindow (Window *win, GC *gc, Visual **v, char *title, int w, int h)
{
  XGCValues values;

  window_x = w;
  window_y = h;
  
  *win = XCreateSimpleWindow(dpy, RootWindow(dpy, screen),
                           0, 0, w, h, 5, 
                           BlackPixel(dpy, screen),
                           WhitePixel(dpy, screen));
  /* new */
  pix = XCreatePixmap(dpy, *win, w, h, DefaultDepth(dpy,screen));
  *gc = XCreateGC(dpy, pix /* used to be *win */, 0, &values);
  XStoreName(dpy, *win, title);
  *v = DefaultVisual(dpy, screen);
  XSetClipMask(dpy, *gc, None);
  clear();
  XGetGCValues(dpy, *gc, 
	       GCFunction|GCPlaneMask|GCForeground|GCBackground|GCLineWidth|
	       GCLineStyle|GCClipXOrigin|GCClipYOrigin, &values);
}
  
int openPackage (int width, int height) {
  struct timezone tz;
  dpy = XOpenDisplay(NULL);
  if (dpy == NULL) {
    fprintf(stderr, "Unable to open display\n");
    exit(0);
  }
  screen = DefaultScreen(dpy);
  initWindow(&window, &gc, &visual, "Test", width,height);
  XSelectInput(dpy, window, ExposureMask|ButtonPressMask|KeyPressMask);
  XMapWindow(dpy, window);
  XFlush(dpy);
  setUpColors();
  gettimeofday(&time0, &tz);
  clear();
  return 1;
}


/*
 * close:  this should do some more cleanup...
 */

int closePackage (void) {
  return 1;
}



