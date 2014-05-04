/* 221 STUDENTS: NO NEED TO UNDERSTAND ANYTHING IN THIS FILE. */

#ifndef _GPKERNEL_H
#define _GPKERNEL_H

/* #include "GP142.h" */


#define TRUE         1
#define FALSE        0
#define OBUFFERLEN   512
#define OLISTMAXLEN  5000

/* unused*/
#define WINDOW_DIM_W 320
#define WINDOW_DIM_H 320

/* kernel events */

#define MOUSE    1
#define KEYPRESS 2
#define PERIODIC 3
#define EXIT     4
#define EXPOSE   5

#define PIXEL    0
#define LINE     1
#define RECT     2
#define OVAL     3
#define TEXT     4
#define TRIANGLE 5

/* color palette */
#define MAX_COLORS  	24

#define BLACK       	0
#define WHITE       	1
#define RED         	2
#define GREEN       	3
#define BLUE        	4
#define YELLOW      	5
#define MAGENTA     	6
#define CYAN        	7
#define PURPLE			8
#define NAVY_BLUE		9
#define DUSTY_PLUM		10
#define ICE_BLUE		11
#define TURQUOISE		12
#define ORANGE			13
#define BROWN			14
#define PINK			15
#define CHALK			16
#define GOLD			17
#define PEACH			18
#define FOREST_GREEN	19
#define SEA_GREEN		20
#define OLIVE			21
#define MED_GRAY		22
#define LT_GRAY			23

typedef struct {
  int type;
  int x;
  int y;
  int ch;
} eventRecord;


/* drawing primitives */

int drawText(int x, int y, int point, char *str);
int drawPixel(int x, int y);
int drawLine(int x1, int y1, int x2, int y2, int width);
int drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int width);
int drawRect(int x1, int y1, int w, int h, int width);
int drawOval(int x1, int y1, int w, int h, int width);
int setPenColor(int color);

/* window routines */

int clear();
int flush();

/* event access */

int getNextEvent(eventRecord *event);

/* open and close */

int openPackage(int width, int height);
int closePackage(void);

/* utility */
/* Jim: removed
void cartesianToWindow(int *x, int *y);
void windowToCartesian(int *x, int *y);
*/






#endif
