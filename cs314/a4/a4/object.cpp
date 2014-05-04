#include <cmath>
#include <cfloat>

#include "object.hpp"
#include "linalg.hpp"


void Object::setup_transform(Matrix const &m) {
    transform = m;
    m.invert(i_transform);
    n_transform = i_transform.transpose();
}


bool Object::intersect(Ray ray, Intersection &hit) const {

    // inv_transform.print(true); std::cout << std::endl;

    // Assert the correct values of the W coords of the origin and direction.
    // You can comment this out if you take great care to construct the rays
    // properly.
    ray.origin[3] = 1;
    ray.direction[3] = 0;

    Ray local_ray(i_transform * ray.origin, i_transform * ray.direction);

    if (localIntersect(local_ray, hit)) {
        // Assert correct values of W.
        hit.position[3] = 1;
        hit.normal[3] = 0;
        // Transform intersection coordinates into global coordinates.
        hit.position = transform * hit.position;
        hit.normal = (n_transform * hit.normal).normalized();
        return true;
    }
    return false;
}


bool Sphere::localIntersect(Ray const &ray, Intersection &hit) const {
    // @@@@@ YOUR CODE HERE
//    return false;
    
    Vector v = ray.direction;
    Vector p = ray.origin;
    double a = v.dot(v);
    double b = 2 * p.dot(v);
    double c = p.dot(p) - radius * radius;
    double discriminant = b*b - 4*a*c;
    
    if (discriminant >= 0) {
        double t0 = (-b - sqrt(discriminant)) / (2.0 * a);
        double t1 = (-b + sqrt(discriminant)) / (2.0 * a);
        if (t0 > t1) {
            double temp = t0;
            t0 = t1;
            t1 = temp;
        }
        
        if (t0 >= 0) {
            hit.depth = t0;
            hit.position = p + hit.depth * v;
            hit.normal = hit.position.normalized();
            return true;
        }
    }
    return false;
}


bool 
Plane::localIntersect(Ray const &ray, Intersection &hit) const {
    // @@@@@ YOUR CODE HERE
    Vector v = ray.direction;
    Vector p = ray.origin;
    
    double t = - p[2] / v[2];
    if (t >= 0) {
        hit.depth = t;
        hit.position = p + t * v;
        hit.normal = Vector(0, 0, 1);
        
        return true;
    }
    return false;
}


bool Conic::localIntersect(Ray const &ray, Intersection &hit) const {
    // @@@@@ YOUR CODE HERE (optional)
    return false;
}


