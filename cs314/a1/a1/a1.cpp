#include <cstdlib>
#include <cmath>
#include <ctime>
#include <iostream>
#include <list>
#include "rocket.hpp"
#include "pad.hpp"
#include "utils.hpp"

#ifdef __APPLE__
#  include <GLUT/glut.h>
#else
#  include <GL/glut.h>
#endif


// Positioning state.
int window_w, window_h;

// The balls and paddle.
Rocket rocket;
Pad pad;

// Direction of travel of landing pad.
bool pad_moving_right = false;

// @@@@@
// Put any other global state variables you need here.
// For example, keyboard and mouse state.
// @@@@@

// Rotation
int rotation_old_x = NULL;

void setup_lighting() {
    
    // Turn on lighting, and two local lights.
    glEnable(GL_LIGHTING);
    glEnable(GL_LIGHT0);
    glEnable(GL_LIGHT1);
    glEnable(GL_COLOR_MATERIAL);
    
    // Set the intensity of the global ambient light.
    float ambient[] = {0.3, 0.3, 0.3, 1.0};
    glLightModelfv(GL_LIGHT_MODEL_AMBIENT, ambient);
    
    // Set up the diffuse intensities of the local light source.
    float diffuse[][4] = {
        0.8, 0.8, 0.7, 1,
        0.2, 0.2, 0.3, 1,
    };
    glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuse[0]);
    glLightfv(GL_LIGHT1, GL_DIFFUSE, diffuse[1]);
    
    // Move the light near the top corner of the window.
    float light_positions[][4] = {
        -0.5, 1, 1, 0, // From above-left, directional
        2, -5, 0, 0, // From below, directional
    };
    glLightfv(GL_LIGHT0, GL_POSITION, light_positions[0]);
    glLightfv(GL_LIGHT1, GL_POSITION, light_positions[1]);
}


// Called by GLUT at the start of the session, and when the window is reshaped.
void reshape(int w, int h) {
    window_w = w;
    window_h = h;
    glViewport(0, 0, w, h);
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    // Z range must be big enough to encompass balls and paddle.
    glOrtho(0, w, 0, h, -50, 50);
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
    
    setup_lighting();
    
    rocket.reset();
}


// Called by GLUT when you move the mouse.
void mouse_move(int x, int y) {
    // @@@@@
    // Use mouse movements to rotate the rocket.
    // Compare the current and previous mouse coordinates and use
    // the displacement to set the amount of rotation.
    // @@@@@

    if (!rotation_old_x)
        rotation_old_x = x;
    
    int dis = x - rotation_old_x;
    rocket.rotate(dis);
    rotation_old_x = x;
}


// Called by GLUT when you press a key on the keyboard.
void keyboard(unsigned char key, int x, int y) {
    
    // Reset the rocket by pressing 'r'.
    // Grow or shrink the landing pad with the '>' and '<' keys.
    switch(key) {
        case 'r':
            rocket.reset();
            break;
        case '>':
            pad.grow();
            break;
        case '<':
            pad.shrink();
            break;
            
        // @@@@@
        // Toggle between advanced mode and basic mode here by detecting
        // presses of the 'a' and 'b' keys.
        // You can also detect any other key press events here,
        // and use them however you like.
        // @@@@@
            
        case 'g':
            rocket.setGravity(!rocket.getGravity());
            break;
        case 'a':
            rocket.setAdvanced();
            break;
        case 'b':
            rocket.setBasic();
            break;
    }
}


// Called by GLUT when you press a special key on the keyboard.
void keyboardSpecial(int key, int x, int y) {
    // @@@@@
    // Detect arrow key presses here.
    // @@@@@
    
    rocket.update_direction(key, true);
}


// Called by GLUT when you release a special key on the keyboard.
void keyboardSpecialUp(int key, int x, int y) {
    // @@@@@
    // Detect arrow key releases here.
    // @@@@@
    
    rocket.update_direction(key, false);
}


// Called by GLUT when we need to redraw the screen.
void display(void) {
    
    // Clear the buffer we will draw into.
    glClearColor(0, 0, 0, 1);
    glClear(GL_COLOR_BUFFER_BIT);
    
    // Initialize the modelview matrix.
    glLoadIdentity();
    
    // Setup lights in the scene.
    int lightNo = 2;
    if (rocket.hasLanded()) {
        float lightColour[4];
        if (rocket.isAlive()) {
            lightColour[0] = 1; lightColour[1] = 1; lightColour[2] = 1; lightColour[3] = 1;
        }
        else {
            lightColour[0] = 1; lightColour[1] = 0; lightColour[2] = 0; lightColour[3] = 1;

        }
        lightNo += pad.setupLight(lightNo, lightColour);
    }
    
    // Disable all remaining lights.
    for (int i = lightNo; i < GL_MAX_LIGHTS; ++i) {
        glDisable(GL_LIGHT0 + i);
    }
    
    // Draw a background grid.
    glNormal3f(0, 0, 1);
    const int res = 40;
    for (int x = 0; x <= window_w; x += res) {
        glBegin(GL_QUAD_STRIP);
        for (int y = 0; y <= window_h; y+= res) {
            double gradient = (double)y/(double)window_h;
            glColor3f(gradient*0.6+0.1, gradient*0.6+0.1, gradient*0.8+0.2);
            glVertex3f(x, y, -20);
            glVertex3f(x+res, y, -20);
        }
        glEnd();
    }
    
    // Draw the landing pad.
    pad.draw();
    
    // Draw the rocket.
    rocket.draw();
    
    // Make the buffer we just drew into visible.
    glutSwapBuffers();
}


// Called by GLUT on a timer for every frame of animation.
// We are responsible for setting the argument that is passed to this function.
void animate(int last_frame = 0) {
    
    // @@@@@
    // Control the movement of the rocket here based on arrow key states.
    // @@@@@
    
    
    // Move the landing pad around randomly.
    if (!rocket.hasLanded()) {
        static const double PAD_SPEED = 2.0;
        double x, y;
        pad.getPosition(x, y);
        double halfWidth = pad.getWidth()/2;
        if (x <= halfWidth) {
            pad_moving_right = true;
        } else if (x >= window_w-halfWidth) {
            pad_moving_right = false;
        }
        if (randf() < 0.02) {
            pad_moving_right = !pad_moving_right;
        }
        
        if (pad_moving_right) {
            pad.move(PAD_SPEED, 0);
        } else {
            pad.move(-PAD_SPEED, 0);
        }
    }
    
    rocket.move();
    
    // Allow the rocket to update its properties.
    // Pass in the landing pad in order to check for collisions.
    rocket.update(pad);
    
    // Signal that the window should be redrawn.
    glutPostRedisplay();
    
    // Schedule the next frame.
    int current_time = glutGet(GLUT_ELAPSED_TIME);
    int next_frame = last_frame + 1000 / 30;
    glutTimerFunc(MAX(0, next_frame - current_time), animate, current_time);
}


int main(int argc, char *argv[]) {
    
    // Initialize random seed.
    srand(time(NULL));
    
    // Initialize GLUT and open a window.
    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_RGB | GLUT_DOUBLE);
    glutInitWindowSize(800, 600);
    glutCreateWindow(argv[0]);
    
    // Register a bunch of callbacks for GLUT events.
    glutDisplayFunc(display);
    glutReshapeFunc(reshape);
    glutMotionFunc(mouse_move);
    glutPassiveMotionFunc(mouse_move);
    glutKeyboardFunc(keyboard);
    glutSpecialFunc(keyboardSpecial);
    glutSpecialUpFunc(keyboardSpecialUp);
    
    // Schedule the first animation callback ASAP.
    glutTimerFunc(0, animate, 0);
    
    // Pass control to GLUT.
    glutMainLoop();
    
    return 0;
}

