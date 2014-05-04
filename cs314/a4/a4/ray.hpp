#ifndef RAY_H
#define RAY_H

#include "linalg.hpp"


// A class to encapsulate a ray.
class Ray {
public:

    Ray(double x, double y, double z, double dx, double dy, double dz) :
        origin(x, y, z), direction(dx, dy, dz) {}

    Ray(Vector const &origin_, Vector const &direction_) :
        origin(origin_), direction(direction_) 
        { origin[3] = 1; direction[3] = 1; }

    Vector origin;    // Ray origin
    Vector direction; // Ray direction
};


#endif
