#include <cmath>
#include <time.h>
#include "rocket.hpp"
#include "pad.hpp"
#include "utils.hpp"

#ifdef __APPLE__
#  include <GLUT/glut.h>
#else
#  include <GL/glut.h>
#endif

#define GRAVITY 5.0f / 60.0f;

using namespace std;

Rocket::Rocket() 
    : x_(100.0), y_(100.0), rotation_(0.0),
      length_(90.0), width_(45.0), isAlive_(true), hasLanded_(false) {

    // Rocket colour defaults to red.
    colour_[0] = 1.0; colour_[1] = 0.0; colour_[2] = 0.0; colour_[3] = 1.0;
    
    // Init direction and speed
    moving_up_ = false; moving_left_ = false; moving_down_ = false; moving_right_ = false;
    up_speed_ = 0; left_speed_ = 0; down_speed_ = 0; right_speed_ = 0;
          
    // Basic mode by default
    is_advanced_mode_ = false;
          
    // By default, disable gravity
    gravity_enabled_ = false;
}


void Rocket::reset() {
    isAlive_ = true;
    hasLanded_ = false;
    rotation_ = 0.0;

    // Position rocket in centre of viewport.
    double viewport[4];
    glGetDoublev(GL_VIEWPORT, viewport);
    double window_w = viewport[2];
    double window_h = viewport[3];

    x_ = window_w / 2;
    y_ = window_h / 2;
    
    colour_[0] = 1.0; colour_[1] = 0.0; colour_[2] = 0.0; colour_[3] = 1.0;
    moving_up_ = false; moving_left_ = false; moving_down_ = false; moving_right_ = false;
    up_speed_ = 0; left_speed_ = 0; down_speed_ = 0; right_speed_ = 0;
    
    // Note: not resetting mode nor gravity
    }


bool Rocket::isAlive() const {
    return isAlive_;
}


bool Rocket::hasLanded() const {
    return hasLanded_;
}


void Rocket::get_direction_(double &x, double &y) {
    if (moving_up_) {
        if (is_advanced_mode_) {
            if (up_speed_ < 1) {
                up_speed_ = 1;
            }
            
            up_speed_ *= 1.15;
            if (up_speed_ > 10) {
                up_speed_ = 10;
            }
        }
        else {
            up_speed_ = 5;
        }
    }
    else {
        if (is_advanced_mode_) {
            up_speed_ /= 1.1;
            if (up_speed_ < 0.01) {
                up_speed_ = 0;
            }
        }
        else {
            up_speed_ = 0;
        }
    }
    
    if (moving_left_) {
        if (is_advanced_mode_) {
            if (left_speed_ < 1) {
                left_speed_ = 1;
            }
            
            left_speed_ *= 1.15;
            if (left_speed_ > 10) {
                left_speed_ = 10;
            }
        }
        else {
            left_speed_ = 5;
        }
    }
    else {
        if (is_advanced_mode_) {
            left_speed_ /= 1.1;
            if (left_speed_ < 0.01) {
                left_speed_ = 0;
            }
        }
        else {
            left_speed_ = 0;
        }
    }
    
    if (moving_down_) {
        if (is_advanced_mode_) {
            if (down_speed_ < 1) {
                down_speed_ = 1;
            }
            
            down_speed_ *= 1.15;
            if (down_speed_ > 10) {
                down_speed_ = 10;
            }
        }
        else {
            down_speed_ = 5;
        }
    }
    else {
        if (is_advanced_mode_) {
            down_speed_ /= 1.1;
            if (down_speed_ < 0.01) {
                down_speed_ = 0;
            }
        }
        else {
            down_speed_ = 0;
        }
    }
    
    if (moving_right_) {
        if (is_advanced_mode_) {
            if (right_speed_ < 1) {
                right_speed_ = 1;
            }
            right_speed_ *= 1.15;
            if (right_speed_ > 10) {
                right_speed_ = 10;
            }
        }
        else {
            right_speed_ = 5;
        }
    }
    else {
        if (is_advanced_mode_) {
            right_speed_ /= 1.1;
            if (right_speed_ < 0.01) {
                right_speed_ = 0;
            }
        }
        else {
            right_speed_ = 0;
        }
    }
    
    x = right_speed_ - left_speed_;
    y = up_speed_ - down_speed_;
}

double Rocket::get_rotated_angle_(double x, double y) {
    double alpha;
    if (x > 0) {
        alpha = atan(y/x);
    }
    else if (x < 0) {
        alpha = atan(y/x);
        if (y >= 0) {
            alpha += M_PI;
        }
        else {
            alpha -= M_PI;
        }
    }
    else {
        if (y > 0) {
            alpha = M_PI_2;
        }
        else if (y < 0) {
            alpha = - M_PI_2;
        }
        else {
            alpha = 0;
        }
    }
    
    return alpha;
}

// Move in direction of rotation
void Rocket::move() {
    if (!isAlive_) {
        return;
    }
    
    double x, y;
    get_direction_(x, y);
    
    double r = sqrt(pow(x, 2) + pow(y, 2));
    double alpha = get_rotated_angle_(x, y) + M_PI;
    
    if (!hasLanded_) {
        double newX = r * cos(alpha) * cos(rotation_*M_PI/180.0) - r * sin(alpha) * sin(rotation_*M_PI/180.0);
        x_ += is_advanced_mode_ ? newX : x;
    }
    double newY = r * sin(alpha) * cos(rotation_*M_PI/180.0) + r * cos(alpha) * sin(rotation_*M_PI/180.0);
    double viewport[4];
    glGetDoublev(GL_VIEWPORT, viewport);
    double window_h = viewport[3];
    double gravity = (window_h - y_) / 50.0 * GRAVITY;
    y_ += is_advanced_mode_ ? newY : y;
    
    if (gravity_enabled_) {
        y_ -= gravity;
    }
}

void Rocket::update_direction(int dir, bool is_moving) {
    switch (dir) {
        case GLUT_KEY_UP:
            moving_up_ = is_moving;
            break;
        case GLUT_KEY_LEFT:
            moving_left_ = is_moving;
            break;
        case GLUT_KEY_DOWN:
            moving_down_ = is_moving;
            break;
        case GLUT_KEY_RIGHT:
            moving_right_ = is_moving;
            break;
        default:
            break;
    }
}

void Rocket::rotate(double angle) {
    if (isAlive_ && !hasLanded_) {
    	rotation_ += angle;
    }
}

void Rocket::setAdvanced() {
    is_advanced_mode_ = true;
}

void Rocket::setBasic() {
    is_advanced_mode_ = false;
}

void Rocket::setGravity(bool enable) {
    gravity_enabled_ = enable;
}

bool Rocket:: getGravity() {
    return gravity_enabled_;
}

void Rocket::setRandomColour() {
    // @@@@@
    // Give the rocket a random colour here.
    // @@@@@
    
    srand(time(NULL));
    for (int i=0; i<4; i++) {
        float colour = (rand() % 10) / 10.0;
        colour_[i] = colour;
    }
}


void Rocket::update(Pad &pad) {

    if (!isAlive_) {
        return;
    }   
 
    // Get window width.
    double viewport[4];
    glGetDoublev(GL_VIEWPORT, viewport);
    double window_w = viewport[2];
    double window_h = viewport[3];
    
    // Evaluate trig quantities based on rocket's rotation.
    double sinR = sin(rotation_*M_PI/180.0);
    double cosR = cos(rotation_*M_PI/180.0);
    double offset = length_/4.0;
    double vRadius = width_/2.0 + fabs(cosR)*offset;
    double hRadius = width_/2.0 + fabs(sinR)*offset;  

    // If the rocket is moving, mark it as not landed.
    if (y_ > vRadius + pad.getTop()) {
        hasLanded_ = false;
    }

    // Impact with the top wall.
    if (y_ > window_h - vRadius) {
        y_ = window_h - vRadius;
        if (is_advanced_mode_) {
            double x, y;
            get_direction_(x, y);
            double alpha = get_rotated_angle_(x, y) + M_PI;
            double r = sqrt(pow(x, 2) + pow(y, 2));
            double newX = r * cos(alpha) * cos(rotation_*M_PI/180.0) - r * sin(alpha) * sin(rotation_*M_PI/180.0);
            double newY = r * sin(alpha) * cos(rotation_*M_PI/180.0) + r * cos(alpha) * sin(rotation_*M_PI/180.0);
            alpha = get_rotated_angle_(newX, newY);
            
            rotation_ -= 2 * alpha * 180 / M_PI;
        }
    }

    // Falling off the bottom.
    else if (y_ < -vRadius) {
        isAlive_ = false;
    }
 
    // Impact with left wall.
    if (x_ < hRadius) {
        x_ = hRadius;
        if (is_advanced_mode_) {
            double x, y;
            get_direction_(x, y);
            double alpha = get_rotated_angle_(x, y) + M_PI;
            double r = sqrt(pow(x, 2) + pow(y, 2));
            double newX = r * cos(alpha) * cos(rotation_*M_PI/180.0) - r * sin(alpha) * sin(rotation_*M_PI/180.0);
            double newY = r * sin(alpha) * cos(rotation_*M_PI/180.0) + r * cos(alpha) * sin(rotation_*M_PI/180.0);
            alpha = get_rotated_angle_(newX, newY);
            
            rotation_ -= 2 * (alpha - M_PI_2) * 180 / M_PI;
        }
    }
    
    // Impact with right wall.
    else if (x_ > window_w - hRadius) {
        x_ = window_w - hRadius;
        if (is_advanced_mode_) {
            double x, y;
            get_direction_(x, y);
            double alpha = get_rotated_angle_(x, y) + M_PI;
            double r = sqrt(pow(x, 2) + pow(y, 2));
            double newX = r * cos(alpha) * cos(rotation_*M_PI/180.0) - r * sin(alpha) * sin(rotation_*M_PI/180.0);
            double newY = r * sin(alpha) * cos(rotation_*M_PI/180.0) + r * cos(alpha) * sin(rotation_*M_PI/180.0);
            alpha = get_rotated_angle_(newX, newY);
            
            rotation_ += 2 * (M_PI_2 - alpha) * 180 / M_PI;
        }
    }
    
    bool justLanded = !hasLanded_;

	//
    // Impact with landing pad.
    // We use two spheres as collision geometry.
	//

    // First sphere is the nose of the rocket.
    double impactPos;
    bool impact = pad.contactSphere(x_+sinR*offset, y_-cosR*offset, 
                                     width_/2.0, impactPos);
    if (impact) {
        y_ = vRadius + pad.getTop();
        hasLanded_ = true;
    }

    // Second sphere is base of rocket.
    impact = pad.contactSphere(x_-sinR*offset, y_+cosR*offset, 
                                   width_/2.0, impactPos);
    if (impact) {
        y_ = vRadius + pad.getTop();
        hasLanded_ = true;
    }

    // If we landed on the pad this frame, change to a random colour.
    if (hasLanded_ && justLanded) {
        if (is_advanced_mode_) {
            // Check if rocket is in proper position
            double alpha = get_rotated_angle_(0, 1) + M_PI;
            double r = 1;
            double newX = r * cos(alpha) * cos(rotation_*M_PI/180.0) - r * sin(alpha) * sin(rotation_*M_PI/180.0);
            double newY = r * sin(alpha) * cos(rotation_*M_PI/180.0) + r * cos(alpha) * sin(rotation_*M_PI/180.0);
            alpha = get_rotated_angle_(newX, newY);
            
            // Successful landing
            if ((alpha > M_PI_4) && (alpha < 3*M_PI_4)) {
                setRandomColour();
            }
            // Bad landing
            else {
                isAlive_ = false;
            }
        }
        else {
            setRandomColour();
        }
    }
}


void Rocket::draw() {

    if (!isAlive_) {
        if (hasLanded_) {
            glPushMatrix();
            glTranslatef(x_, y_, 0);
            glRotatef(rotation_, 0, 0, 1);
            
            double radius = width_/2.0 - 5.0;
            
            // Rocket body
            glColor4fv(colour_);
            glPushMatrix();
            glRotatef(45, 0, 0, 1);
            glTranslatef(0, -10, 0);
            glBegin(GL_TRIANGLE_STRIP);
            for (int i = 0; i < 12; ++i) {
                double angle = (double)i * M_PI / 12.0;
                glNormal3f(cos(angle), 0, sin(angle));
                glVertex3f(cos(angle)*radius, length_/2, sin(angle)*radius);
                glVertex3f(cos(angle)*radius, -length_/2+15.0, sin(angle)*radius);
            }
            glEnd();
            glPopMatrix();
            
            // Rocket nose
            const float white[] = {1.0, 1.0, 1.0, 1.0};
            glColor4fv(white);
            glPushMatrix();
            glRotatef(90.0, 0, 0, 1);
            glRotatef(70.0, 1, 0, 0);
            glTranslatef(0, 0, length_/2-15.0);
            glutSolidCone(radius, length_/3, 12, 4);
            glPopMatrix();
            
            // Rocket fins
            glColor4fv(white);
            glNormal3f(0, 0, 1);
            glPushMatrix();
            glTranslatef(30, 50, 0);
            glRotatef(123, 0, 0, 1);
            glBegin(GL_TRIANGLES);
            glVertex3f(-radius, 0, 0);
            glVertex3f(-radius-10.0, length_*0.5, 0);
            glVertex3f(-radius, length_*0.5, 0);
            glEnd();
            glPopMatrix();
            
            glPushMatrix();
            glTranslatef(39, 83, 0);
            glRotatef(88, 0, 0, 1);
            glBegin(GL_TRIANGLES);
            glVertex3f(radius, 0, 0);
            glVertex3f(radius+10.0, length_*0.5, 0);
            glVertex3f(radius, length_*0.5, 0);
            glEnd();
            glPopMatrix();
            
            glPopMatrix();
        }
        return;
    }   
 
    // Setup transformation matrix for the rocket.
    glPushMatrix();

    // @@@@@
	// There are several different tasks to complete here:
    // - Use glTranslate to set the position of the rocket.
    // - Use glRotate to set the rotation of the rocket.
	// - Set the colour of the rocket.
    // @@@@@
    glTranslatef(x_, y_, 0);
    glRotatef(rotation_, 0, 0, 1);

    // We are now in a coordinate system with 0,0,0 at the centre of the rocket.
    // DRAW IT!
    double radius = width_/2.0 - 5.0;

    // Rocket body
    glColor4fv(colour_);
    glPushMatrix();
    glBegin(GL_TRIANGLE_STRIP);
    for (int i = 0; i < 12; ++i) {
        double angle = (double)i * M_PI / 12.0;
        glNormal3f(cos(angle), 0, sin(angle));
        glVertex3f(cos(angle)*radius, length_/2, sin(angle)*radius);
        glVertex3f(cos(angle)*radius, -length_/2+15.0, sin(angle)*radius);
    }
    glEnd();
    glPopMatrix();

    // Rocket nose
    const float white[] = {1.0, 1.0, 1.0, 1.0};
    glColor4fv(white);
    glPushMatrix();
    glRotatef(90.0, 1, 0, 0);
    glTranslatef(0, 0, length_/2-15.0);
    glutSolidCone(radius, length_/3, 12, 4);
    glPopMatrix();

    // Rocket fins
    glColor4fv(white);
    glNormal3f(0, 0, 1);
    glBegin(GL_TRIANGLES);
    glVertex3f(-radius, 0, 0);
    glVertex3f(-radius-10.0, length_*0.5, 0);
    glVertex3f(-radius, length_*0.5, 0);
    glVertex3f(radius, 0, 0);
    glVertex3f(radius+10.0, length_*0.5, 0);
    glVertex3f(radius, length_*0.5, 0);
    glEnd();
    
    // Restore the transformation matrix.
    glPopMatrix();
}


