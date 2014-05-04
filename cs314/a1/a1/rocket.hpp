#ifndef ROCKET_H 
#define ROCKET_H

class Pad; // Forward declaration


class Rocket {

private: 
    // Location of the centre of the rocket.
    double x_, y_;

    // Rotation of the rocket in degrees (around the Z-axis).
    double rotation_;
    
    // The surface colour of the rocket.
    float colour_[4];
   
    // Size of the rocket; generally constant.
    double length_, width_;
    
    // Tracks if the rocket is still alive; will be removed when not alive.
    bool isAlive_;

    // Tracks if the rocket has landed.
    bool hasLanded_;
    
    // Rocket's speed and direction
    bool moving_up_, moving_left_, moving_down_, moving_right_;
    double up_speed_, left_speed_, down_speed_, right_speed_;
    
    // Advanced mode?
    bool is_advanced_mode_;
    
    // Gravity
    bool gravity_enabled_;

    
    void get_direction_(double &x, double &y);
    
    double get_rotated_angle_(double x, double y);
    
public:

    // Constructor: initializes default values
    Rocket();   
    
    // Resets the rocket to its initial state.
    void reset();
 
    // Returns if the rocket is still alive.
    bool isAlive() const;

    // Returns if the rocket has landed.
    bool hasLanded() const;

    // Move the rocket by the given amount in each coordinate direction.
    void move();

    // Update rocket direction
    void update_direction(int dir, bool is_moving);
    
    // Rotates the rocket by the given angle (in degrees).
    void rotate(double angle);
    
    // Set as advanced mode
    void setAdvanced();
    
    // Set as basic mode
    void setBasic();
    
    // Change gravity setting
    void setGravity(bool enable);
    
    // Get gravity
    bool getGravity();

    // Changes the colour of the rocket body to a random colour.
    void setRandomColour();
    
    // Performs physics updates, colliding with the given landing pad.
    void update(Pad &pad);
    
    // Draw the rocket.
    void draw();
    
};


#endif
