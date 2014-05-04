#ifndef MESH_H
#define MESH_H


#include <iostream>
#include <string>
#include <sstream>
#include <vector>
#include <map>

#include "linalg.hpp"
#include "object.hpp"


// A class to represent a single vertex of a polygon. The ints stored within
// are indices into the positions/texCoords/normals/colors vectors of the
// Object that it belongs to.
class Vertex {
public:

    // Indices into positions, texCoods, normals, and colors vectors.
    int pi, ti, ni, ci;

    Vertex() : pi(-1), ti(-1), ni(-1), ci(-1) {}

    Vertex(int pi, int ti, int ni, int ci) :
        pi(pi),
        ti(ti),
        ni(ni),
        ci(ci)
        {}
};


class Triangle {
public:
    Vertex v[3];

    Triangle(Vertex const &v0, Vertex const &v1, Vertex const &v2);

    Vertex& operator[](int i) { return v[i]; }
    const Vertex& operator[](int i) const { return v[i]; }
};


class Mesh : public Object {
public:


    // Storage for positions/texCoords/normals/colors. Looked up by index.
    std::vector<Vector> positions;
    std::vector<Vector> texCoords;
    std::vector<Vector> normals;
    std::vector<Vector> colors;

    // Triangles are a triplet of vertices.
    std::vector<Triangle> triangles;

    // Bounding box
    Vector bboxMin, bboxMax;

    // Read OBJ data from a given file.
    bool readOBJ(std::string const &filename);

    // Construct bounding box of vertex positions.
    // This must be called after mesh data has been initialized and before
    // raytracing begins.
    void updateBBox();
    
    // Intersections!
    bool localIntersect(Ray const &ray, Intersection &hit) const;

private:
    // Compute the result of the implicit line equation in 2D 
    // for a given point and a line with the given endpoints.
    double implicitLineEquation(double p_x, double p_y,
                                double e1_x, double e1_y,
                                double e2_x, double e2_y) const;

    // Find the intersection point between the given ray and mesh triangle.
    // Return true iff an intersection exists, and fill the hit data
    // structure with information about it.
    bool intersectTriangle(Ray const &ray, 
                           Triangle const &tri, 
                           Intersection &hit) const;

};


#endif

