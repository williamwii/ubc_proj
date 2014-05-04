#ifndef PAD_H
#define PAD_H


class Pad {
    
    // Position of the centre of the landing pad.
    double x_, y_;
    
    // Dimensions of the landing pad.
    double width_, height_;

public:
    // Landing pad constructor; initializes everything to constant values.
    Pad();

    // Move the landing pad by the given amount in each coordinate direction.
    void move(double x, double y);

    // Stores the x- and y-position of the pad in the input variables x and y.
    void getPosition(double &x, double &y) const;

    // Returns the width of the landing pad.
    double getWidth() const;
    
    // Returns the y value of the top of the landing pad.
    double getTop() const;

    // Slightly grow/shrink the width of the landing pad.
    void grow();
    void shrink();
    
    // Initializes lights on the landing pad.
    // Returns the number of lights enabled.
    int setupLight(int lightNo, float *lightColour);

    // Draw the landing pad.
    void draw();
    
    // Determines if a sphere of a given radius centered at the given
    // coordinates would contact the landing pad. 
    // If it does, p is set to be the position on the paddle where contact 
    // occured from 0 (far left) to 1 (far right), and true is returned.
    // It will return false if contact did not occur.
    bool contactSphere(double x, double y, double r, double & p) const;
};


#endif
