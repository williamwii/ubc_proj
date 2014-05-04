#include "pointcanvas.h"
#include "bezier.h"
#include "vector.h"

#ifdef __APPLE__
#  include <GLUT/glut.h>
#else
#  include <GL/glut.h>
#endif

// Global window properties
unsigned int windowWidth, windowHeight;

// Global canvas on which points are drawn and manipulated
PointCanvas pointCanvas;

// Global curve manager for creating and drawing Bezier curves
CurveManager curveManager;


// Menu identifiers
enum MenuItem 
{
    MENU_EXIT,
    MENU_CLEAR,
    MENU_C0,
    MENU_C1,
    MENU_BASIC,
    MENU_SUBDIVISION,
    MENU_PIECEWISE
};


// Callback to draw to the screen
void draw()
{
    // Clear buffers
    glClearColor(0.9, 0.9, 0.9, 0.9);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // Generic 2D orthographic transform
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    gluOrtho2D(0.0, 1.0, 0.0, 1.0);
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();

    // Draw all the points
    const std::vector<Vector2D>& points = pointCanvas.getPoints();
    glColor3f(0.0, 0.0, 0.0);
    glPointSize(5);
    glBegin(GL_POINTS);
    for (size_t i = 0; i < points.size(); ++i)
    {
        glVertex2f(points[i][0], points[i][1]);
    }
    glEnd();

    // Update current set of points.
    curveManager.updatePoints(&points);

    // Draw the curves
    curveManager.drawCurves();

    // Swap buffers to display to screen
    glutSwapBuffers();
}


// Callback for key presses
void keyboard(unsigned char c, int x, int y)
{
    switch(c)
    {
    case 27: // ESC
    case 'q':
        exit(0);
        break;
    case '>':
        curveManager.increaseSubdivision();
        break;
    case '<':
        curveManager.decreaseSubdivision();
        break;
    case '1':
        curveManager.setDegree(1);
        break;
    case '2':
        curveManager.setDegree(2);
        break;
    case '3':
        curveManager.setDegree(3);
        break;
    case '4':
        curveManager.setDegree(4);
        break;
    case '5':
        curveManager.setDegree(5);
        break;
    case '6':
        curveManager.setDegree(6);
        break;
    case '7':
        curveManager.setDegree(7);
        break;
    case '8':
        curveManager.setDegree(8);
        break;
    case '9':
        curveManager.setDegree(9);
        break;
    }

    glutPostRedisplay();
}


// Callback for mouse button presses
void mouse(int button, int state, int x, int y)
{
    bool redraw = false;

    float mouseX = (float)x/(windowWidth-1);
    float mouseY = 1.0 - (float)y/(windowHeight-1);

    if (state)
    {
        redraw |= pointCanvas.mouseUp(button, mouseX, mouseY);
    }
    else
    {
        redraw |= pointCanvas.mouseDown(button, mouseX, mouseY);
    }

    if (redraw)
    {
        glutPostRedisplay();
    }
}


// Callback for mouse movements
void motion(int x, int y)
{
    bool redraw = false;

    float mouseX = (float)x/(windowWidth-1);
    float mouseY = 1.0 - (float)y/(windowHeight-1);

    redraw |= pointCanvas.mouseMove(mouseX, mouseY);

    if (redraw)
    {
        glutPostRedisplay();
    }
}


// Callback for changes to the shape and size of the window
void reshape(int w, int h)
{
    windowWidth = w;
    windowHeight = h;
    glViewport(0, 0, w, h);
    glutPostRedisplay();
}


// Callback for menu buttons
void menu(int which)
{
    switch(which)
    {
    case MENU_BASIC:
        curveManager.setCurveMode(BASIC_MODE);
        break;
    case MENU_SUBDIVISION:
        curveManager.setCurveMode(SUBDIVISION_MODE);
        break;
    case MENU_PIECEWISE:
        curveManager.setCurveMode(PIECEWISE_MODE);
        break;
    case MENU_C0:
        curveManager.setContinuityMode(C0_MODE);
        break;
    case MENU_C1:
        curveManager.setContinuityMode(C1_MODE);
        break;
    case MENU_CLEAR: 
        pointCanvas.clearPoints();
        break;
    case MENU_EXIT: 
        exit(0);
        break;
    }

    glutPostRedisplay();
}


int main(int argc, char* argv[])
{
    // Initialize window in GLUT
    glutInit(&argc, argv);
    glutInitWindowSize(800, 600);
    glutInitDisplayMode(GLUT_DEPTH | GLUT_RGBA | GLUT_DOUBLE);
    glutCreateWindow("Bezier");

    // Register GLUT callbacks
    glutDisplayFunc(draw);
    glutKeyboardFunc(keyboard);
    glutMouseFunc(mouse);
    glutMotionFunc(motion);
    glutReshapeFunc(reshape);

    // Add menus for state selection
    int modeMenu = glutCreateMenu(menu); // Menu for program mode
    glutAddMenuEntry("Basic", MENU_BASIC);
    glutAddMenuEntry("Subdivision", MENU_SUBDIVISION);
    glutAddMenuEntry("Piecewise", MENU_PIECEWISE);
    int contMenu = glutCreateMenu(menu); // Menu for continuity mode
    glutAddMenuEntry("C0", MENU_C0);
    glutAddMenuEntry("C1", MENU_C1);
    glutCreateMenu(menu); // Main menu
    glutAddSubMenu("Mode", modeMenu);
    glutAddSubMenu("Continuity", contMenu);
    glutAddMenuEntry("Clear Points", MENU_CLEAR);
    glutAddMenuEntry("Exit", MENU_EXIT);
    glutAttachMenu(GLUT_RIGHT_BUTTON);

    // Begin GLUT main loop
    glutMainLoop();
}

