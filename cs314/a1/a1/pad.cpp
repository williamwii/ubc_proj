#include "pad.hpp"
#include "utils.hpp"

#ifdef __APPLE__
#  include <GLUT/glut.h>
#else
#  include <GL/glut.h>
#endif


Pad::Pad() : x_(0), y_(5), width_(300.0), height_(20.0) {}


void Pad::move(double x, double y) {
    // Move the pad by the given amount.
    x_ += x;
    y_ += y;

    // Make sure we don't go beyond the edges of the screen.
    double viewport[4];
    glGetDoublev(GL_VIEWPORT, viewport);
    double window_w = viewport[2];
    double window_h = viewport[3];

    if (x_ < width_/2) {
        x_ = width_/2;
    } else if (x_ > window_w-(width_/2)) {
        x_ = window_w-(width_/2);
    }
    if (y_ < height_/2) {
        y_ = height_/2;
    } else if (y_ > window_h-(height_/2)) {
        y_ = window_h-(height_/2);
    }
}


void Pad::getPosition(double &x, double &y) const {
    x = x_;
    y = y_;
}


double Pad::getWidth() const {
    return width_;
}


double Pad::getTop() const {
    return y_ + height_ / 2.0;
}


void Pad::grow() {
    width_ += 10;
}


void Pad::shrink() {
    // Don't let it get smaller than 10.
    width_ = MAX(10, width_ - 10);
}


int Pad::setupLight(int lightNo, float *lightColour) {
    // @@@@@
    // Set up landing pad lights here.
    // NOTE: This function must return the number of lights it enables,
	// so that the calling code knows which lights to disable.
    // @@@@@
    
    for (int i=0; i<5; i++) {
        glEnable(GL_LIGHT2 + i);
        glLightfv(GL_LIGHT2 + i, GL_DIFFUSE, lightColour);
        
        float x = x_ - width_ / 2 + i * width_ / 4.0;
        float height = height_ / 2;
        float light_positions[4] = {
            x, height, 0, 1,
        };
        glLightfv(GL_LIGHT2 + i, GL_POSITION, light_positions);
    }
	return 5;
}


void Pad::draw() {
    // Draw the landing pad.
    glColor3f(1, 1, 1);

    glPushMatrix();
    
    glTranslatef(x_, y_, 0);
    glScalef(width_, height_, 1);
    glNormal3f(0, 0, 1);

    glBegin(GL_QUADS);
    glVertex3f(-0.5, -0.5, 0);
    glVertex3f(-0.5, 0.5, 0);
    glVertex3f(0.5, 0.5, 0);
    glVertex3f(0.5, -0.5, 0);
    glEnd();
    
    glPopMatrix();
}


bool Pad::contactSphere(double x, double y, double r, double & p) const {
    if (y < y_ + height_ / 2.0 + r && // Below top edge.
        y > y_ - height_ / 2.0 - r && // Above bottom edge.
        x < x_ + width_  / 2.0     && // Left of right edge.
        x > x_ - width_  / 2.0        // Right of left edge.
    ) {
        p = 0.5 + (x - x_) / width_;
        return true;
    }
    return false;
}


