#include <cstdlib>
#include <cmath>
#include <iostream>
#include <list>
#include <vector>

#ifdef __APPLE__
#  include <GLUT/glut.h>
#else
#  include <GL/glut.h>
#endif

#include "robot.hpp"
#include "pose.hpp"
#include "utils.hpp"


// Global state.
int windowWidth, windowHeight;
int mouseX, mouseY;
double viewTheta = -30;
double viewPhi = 30;
int frameRate = 1;
bool playing = true;

// The robot!
Robot robot;


// One-time configuration of many OpenGL directives.
void initOpenGL() {
    
    // Set clearing values.
    glClearColor(0, 0, 0, 1);
    glClearDepth(1);
    
    // Turn on depth testing.
    glEnable(GL_DEPTH_TEST);
    
    // Turn on blending (for floor).
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    
    // Turn on normal normalization (for non-uniform scaling of primitives).
    glEnable(GL_NORMALIZE);
    
    // Turn on lighting.
    glEnable(GL_LIGHTING);
    glEnable(GL_LIGHT0);
    glEnable(GL_COLOR_MATERIAL);
    
    // Set the intensity of the global ambient light.
    float ambient[] = {0.05, 0.05, 0.15, 1.0};
    glLightModelfv(GL_LIGHT_MODEL_AMBIENT, ambient);
    
    // Set up the diffuse intensities of the directional light.
    float diffuse[] = {0.9, 0.9, 0.8, 1.0}; // yellow for the sun
    glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuse);
}


// Called per frame to setup lighting for that frame.
void setupLighting() {
    // w=0 -> direction at infinity.
    float direction[] = {2, 5, 1, 0}; 
    glLightfv(GL_LIGHT0, GL_POSITION, direction);
}


// Called by GLUT at the start of the session, and when the window is reshaped.
void reshape(int w, int h) {
    windowWidth = w;
    windowHeight = h;
    glViewport(0, 0, w, h);
}


// Called by GLUT when you move the mouse.
void mouseMove(int x, int y) {
    // GLUT Y coords are inverted.
    y = windowHeight - y;
    
    // Calculate change from last known mouse positon.
    int dx = x - mouseX;
    int dy = y - mouseY;
    
    // Update viewing angles.
    viewTheta = int(viewTheta + 360 + float(dx) / 2) % 360;
    viewPhi = std::min(90.0, std::max(-90.0, viewPhi - dy));
    
    // Remember mouse coords for next time.
    mouseX = x;
    mouseY = y;
}


// Called by GLUT when you click the mouse.
// Simply saving mouse coords.
void mouseClick(int button, int state, int x, int y) {
    // GLUT Y coords are inverted.
    y = windowHeight - y;
    mouseX = x;
    mouseY = y;
}


// Called by GLUT when you press a key on the keyboard.
void keyboard(unsigned char key, int x, int y) {
    switch (key) {
        case 27 : // escape
        case 'q':
            exit(0);
            break; // You never know... ;)
        case '>':
            frameRate = std::min(128, frameRate * 2);
            break;
        case '<':
            frameRate = std::max(1, frameRate / 2);
            break;
        case 'r':
        case ' ':
            playing = !playing;
            break;
        case 'k':
            robot.moveToNextPose();
            break;
        case 'j':
            robot.moveToPrevPose();
            break;
        case '.':
            robot.incrementPoseIndex(0.1);
            break;
        case ',':
            robot.incrementPoseIndex(-0.1);
            break;
        case 's':
            writePoseFile("dump.txt", robot.getPose());
            break;
        default:
            if (key >= '1' && key <= '9') {
                robot.setPoseIndex(key - '1');
            } else {
                std::cerr << "unhandled key '" << key << "' (" << int(key) << ")" << std::endl;
            }
			break;
    }
}





// Called by GLUT when we need to redraw the screen.
void display(void) {
    
    // Clear the buffer we will draw into.
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    
    // Setup camera projection.
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    gluPerspective(50.0, double(windowWidth) / double(windowHeight), 0.1, 25);
    
    // Setup camera position/orientation.
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
    gluLookAt(
        0, 2.5, 10, // eye
        0, 2.5, 0,  // centre
        0, 1  , 0   // up
    );
    glRotated(viewPhi, 1, 0, 0);
    glRotated(viewTheta, 0, 1, 0);
    
    // Must be done after the view is rotated as the lights are stored in
    // eye coords when glLight call is made.
    setupLighting();
    
    // For debugging; show us what the current transformation matrix looks like.
    drawAxis();
    
    // Draw the robot!
    robot.draw();
    
    // Draw a floor. Since it is transparent, we need to do it AFTER all of
    // the opaque objects.
    for (int x = -5; x < 5; x++) {
        for (int y = -5; y < 5; y++) {
            glColor4f(1, 1, 1, (x + y) % 2 ? 0.75 : 0.5);
            glNormal3f(0, 1, 0);
            glBegin(GL_POLYGON);
            glVertex3f(x    , 0, y    );
            glVertex3f(x + 1, 0, y    );
            glVertex3f(x + 1, 0, y + 1);
            glVertex3f(x    , 0, y + 1);
            glEnd();
        }
    }
    
    // Make the buffer we just drew into visible.
    glutSwapBuffers();
}


// Called by GLUT on a timer for every frame of animation.
// We are responsible for setting the argument that is passed to this function.
void animate(int lastFrame = 0) {
    
    int currentTime = glutGet(GLUT_ELAPSED_TIME);
    int elapsedTime = currentTime - lastFrame;
    
    if (playing) {
        robot.incrementPoseIndex(frameRate * double(elapsedTime) / 1000);
    }
    
    // Signal that the window should be redrawn.
    glutPostRedisplay();
    
    // Schedule the next frame.
    int nextFrame = lastFrame + 1000 / 30;
    glutTimerFunc(std::max(0, nextFrame - currentTime), animate, currentTime);

}


int main(int argc, char *argv[]) {

    // Initialize GLUT and open a window.
    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_RGBA | GLUT_DOUBLE | GLUT_DEPTH);
    glutInitWindowSize(800, 600);
    glutCreateWindow(argv[0]);
    
    // Register a bunch of callbacks for GLUT events.
    glutDisplayFunc(display);
    glutReshapeFunc(reshape);
    glutMotionFunc(mouseMove);
    glutMouseFunc(mouseClick);
    glutKeyboardFunc(keyboard);
    
    // Setup OpenGL
    initOpenGL();
    
    // Initialize our robot.
    if (argc > 1) {
        std::vector<Pose> poses;
        readPoseFile(argv[1], poses);
        robot.setPoses(poses);
    }
    
    // Schedule the first animation callback ASAP.
    glutTimerFunc(0, animate, 0);
    
    // Pass control to GLUT.
    glutMainLoop();
    
    // Will never be reached.
    return 0;
}
