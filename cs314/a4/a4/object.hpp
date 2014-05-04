#ifndef OBJECT_H
#define OBJECT_H

#include "ray.hpp"
#include "material.hpp"
#include "intersection.hpp"
#include "linalg.hpp"


// Abstract base object class
class Object {
public:

    Matrix transform;   // Transformation from global to object space.
    Matrix i_transform; // Transformation from object to global space.
    Matrix n_transform; // Trasnformation to global space for normals.

    // Sets up the 3 transformations from the given global-to-object transform.
    void setup_transform(Matrix const &m);

    // Intersect the object with the given ray in global space.
    // Returns true if there was an intersection, hit is updated with params.
    bool intersect(Ray ray, Intersection &hit) const;

    // Intersect the object with the given ray in object space.
    // This function is specific to each object subtype.
    // Returns true if there was an intersection, hit is updated with params.
    virtual bool localIntersect(Ray const &ray, Intersection &hit) const = 0;

    Material material; // This object's material.
};


// A sphere centred around the local origin with a certain radius.
class Sphere : public Object {
public:
    double radius;
    
    bool localIntersect(Ray const &ray, Intersection &hit) const;
};


// A plane at the origin using Z+ as the normal in object space.
class Plane : public Object {
public:
    bool localIntersect(Ray const &ray, Intersection &hit) const;
};


// A conic about the Z+ axis, bounded along Z by zMin and zMax, 
// with radii radius1 and radius2.
class Conic : public Object {
public:
    double radius1, radius2;
    double zMin, zMax;

    bool localIntersect(Ray const &ray, Intersection &hit) const;
};


#endif
