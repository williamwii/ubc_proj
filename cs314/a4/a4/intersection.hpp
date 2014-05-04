#ifndef INTERSECTION_H
#define INTERSECTION_H

#include "linalg.hpp"


// A class to encapsulate all of the information relating to a ray intersection.
class Intersection {
public:
    // How far along the ray the intersection occurred.
    double depth;

    // Location of the intersection.
    Vector position;

    // Surface normal at the point of intersection.
    Vector normal;
};


#endif
