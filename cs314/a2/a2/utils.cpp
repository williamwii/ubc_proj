#include <cstdlib>
#include <iostream>
#include <fstream>
#include <vector>
#include <list>
#include <sstream>

#ifdef __APPLE__
#  include <GLUT/glut.h>
#else
#  include <GL/glut.h>
#endif

#include "utils.hpp"


void drawAxis() {
    glColor3f(0, 0, 1);
    glutSolidCone(0.1, 1, 8, 2);
    glPushMatrix();
    glRotatef(90, 0, 1, 0);
    glColor3f(1, 0, 0);
    glutSolidCone(0.1, 1, 8, 2);
    glPopMatrix();
    glPushMatrix();
    glRotatef(-90, 1, 0, 0);
    glColor3f(0, 1, 0);
    glutSolidCone(0.1, 1, 8, 2);
    glPopMatrix();
}


void drawCuboid(double x, double y, double z) {

    // @@@@@ Add your own code to draw a cuboid with the given dimensions.
    glPushMatrix();
    glScaled(x, y, z);
    glutSolidCube(1);
    glPopMatrix();
}


