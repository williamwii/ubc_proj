#ifndef UTILS_H
#define UTILS_H


// Draw a set of coloured cones representing the current local coord system.
// X -> red, Y -> green, Z -> blue.
// NOTE: This can be very handy for debugging transformations!
void drawAxis();


// Draw a cuboid with the given dimensions, centered at the origin.
void drawCuboid(double x, double y, double z);


#endif
