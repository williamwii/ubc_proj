#ifndef CAMERA_H
#define CAMERA_H

#include "linalg.hpp"


class Camera {
public:
    
    // Camera location (x,y,z)
    Vector position;

    // LookAt point [center]
    Vector center;

    // LookAt up vector
    Vector up;

    // Vertical field of view
    double fovy;

    // Aspect ratio, i.e. width:height
    double aspect;

    // Near clipping plane
    double zNear;

    // Far clipping plane
    double zFar;
};


#endif
