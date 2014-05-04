#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <vector>
#include <map>
#include <cfloat>
#include <cmath>


#include "linalg.hpp"
#include "object.hpp"
#include "mesh.hpp"
#include "ray.hpp"
#include "intersection.hpp"


Triangle::Triangle(Vertex const &v0, Vertex const &v1, Vertex const &v2) {
    v[0] = v0;
    v[1] = v1;
    v[2] = v2;
}


bool Mesh::readOBJ(std::string const &filename) {
    
    // Try to open the file.
    std::ifstream file(filename.c_str());
    if (!file.good()) {
        std::cerr << "Unable to open OBJ file \"" << filename << "\"" << std::endl;
        return false;
    }

    // Keep fetching op codes and processing them. We will assume that there
    // is one operation per line.
    while (file.good()) {

        std::string opString;
        std::getline(file, opString);

        std::stringstream opStream(opString);
        std::string opCode;
        opStream >> opCode;

        // Skip blank lines and comments
        if (!opCode.size() || opCode[0] == '#') {
            continue;
        }

        // Ignore groups.
        if (opCode[0] == 'g') {
            std::cerr << "ignored OBJ opCode '" << opCode << "'" << std::endl;
        
        // Vertex data.
        } else if (opCode[0] == 'v') {

            // Read in up to 4 doubles.
            Vector vec;
            for (int i = 0; opStream.good() && i < 4; i++) {
                opStream >> vec[i];
            }

            // Store this data in the right location.
            switch (opCode.size() > 1 ? opCode[1] : 'v') {
                case 'v':
                    positions.push_back(vec);
                    break;
                case 't':
                    texCoords.push_back(vec);
                    break;
                case 'n':
                    normals.push_back(vec);
                    break;
                case 'c':
                    colors.push_back(vec);
                    break;
                default:
                    std::cerr << "unknown vertex type '" << opCode << "'" << std::endl;
                    break;
            }

        // A polygon (or face).
        } else if (opCode == "f") {
            std::vector<Vertex> polygon;
            // Limit to 4 as we only can handle triangles and quads.
            for (int i = 0; opStream.good() && i < 4; i++) {

                // Retrieve a full vertex specification.
                std::string vertexString;
                opStream >> vertexString;

                if (!vertexString.size()) {
                    break;
                }

                // Parse the vertex into a set of indices for position,
                // texCoord, normal, and colour, respectively.
                std::stringstream vertexStream(vertexString);
                std::vector<int> indices;                
                for (int j = 0; vertexStream.good() && j < 4; j++) {
                    // Skip slashes.
                    if (vertexStream.peek() == '/') {
                        vertexStream.ignore(1);
                    }
                    int index;
                    if (vertexStream >> index)
                        indices.push_back(index);
                }

                // Turn this into a real Vertex, and append it to the polygon.
                if (indices.size()) {
                    indices.resize(4, 0);
                    polygon.push_back(Vertex(
                        indices[0] - 1,
                        indices[1] - 1,
                        indices[2] - 1,
                        indices[3] - 1
                    ));
                }
                
            }

            // Only accept triangles...
            if (polygon.size() == 3) {
                triangles.push_back(Triangle(polygon[0], 
                                             polygon[1], 
                                             polygon[2]));
            // ...and quads...
            } else if (polygon.size() == 4 ) {
                // ...but break them into triangles.
                triangles.push_back(Triangle(polygon[0],
                                             polygon[1],
                                             polygon[2]));
                triangles.push_back(Triangle(polygon[0],
                                             polygon[2],
                                             polygon[3]));
            }

        // Any other opcodes get ignored.
        } else {
            std::cerr << "unknown opCode '" << opCode << "'" << std::endl;
        }
    }

    updateBBox();

    return true;

}


void Mesh::updateBBox() {
    bboxMin = Vector(DBL_MAX, DBL_MAX, DBL_MAX);
    bboxMax = Vector(-DBL_MAX, -DBL_MAX, -DBL_MAX);
    for (std::vector<Vector>::iterator pItr = positions.begin();
         pItr != positions.end(); ++pItr) {
        Vector const& p = *pItr;
        if (p[0] < bboxMin[0]) bboxMin[0] = p[0];
        if (p[0] > bboxMax[0]) bboxMax[0] = p[0];
        if (p[1] < bboxMin[1]) bboxMin[1] = p[1];
        if (p[1] > bboxMax[1]) bboxMax[1] = p[1];
        if (p[2] < bboxMin[2]) bboxMin[2] = p[2];
        if (p[2] > bboxMax[2]) bboxMax[2] = p[2];
    }
}


bool Mesh::localIntersect(Ray const &ray, Intersection &hit) const {

    // Bounding box check
    double tNear = -DBL_MAX, tFar = DBL_MAX;
    for (int i = 0; i < 3; i++) {
        if (ray.direction[i] == 0.0) {
            if (ray.origin[i] < bboxMin[i] || ray.origin[i] > bboxMax[i]) {
                // Ray parallel to bounding box plane and outside of box!
                return false;
            }
            // Ray parallel to bounding box plane and inside box: continue;
        } else {
            double t1 = (bboxMin[i] - ray.origin[i]) / ray.direction[i];
            double t2 = (bboxMax[i] - ray.origin[i]) / ray.direction[i];
            if (t1 > t2) std::swap(t1,t2); // Ensure t1 <= t2

            if (t1 > tNear) tNear = t1; // We want the furthest tNear
            if (t2 < tFar) tFar = t2; // We want the closest tFar

            if (tNear > tFar) return false; // Ray misses the bounding box.
            if (tFar < 0) return false; // Bounding box is behind the ray.
        }
    }
    // If we made it this far, the ray does intersect the bounding box.

    // The ray hits the bounding box, so check each triangle.
    hit.depth = DBL_MAX;
    bool isHit = false;
    for (size_t tri_i = 0; tri_i < triangles.size(); tri_i++) {
        Triangle const &tri = triangles[tri_i];

        if (intersectTriangle(ray, tri, hit)) {
            isHit = true;
        }
    }
    return isHit;
}


double Mesh::implicitLineEquation(double p_x, double p_y,
                                  double e1_x, double e1_y,
                                  double e2_x, double e2_y) const
{
    return (e2_y - e1_y)*(p_x - e1_x) - (e2_x - e1_x)*(p_y - e1_y);
}


bool Mesh::intersectTriangle(Ray const &ray,
                             Triangle const &tri,
                             Intersection &hit) const
{
    // Extract vertex positions from the mesh data.
    Vector const &p0 = positions[tri[0].pi];
    Vector const &p1 = positions[tri[1].pi];
    Vector const &p2 = positions[tri[2].pi];

    // @@@@@ YOUR CODE HERE
    // Decide whether ray intersects the triangle (p0,p1,p2).
    // If so, fill in intersection information in hit and return true.
    // You may find it useful to use the routine implicitLineEquation()
    // to compute the result of the implicit line equation in 2D.
    //
    // NOTE: hit.depth is the current closest intersection depth, so don't
    // accept any intersection that happens further away than that.

    Vector v = ray.direction;
    Vector p = ray.origin;
    Vector n = (p1 - p0).cross((p2 - p1)).normalized();
//    if (n[2] < 0) {
//        n = (p2 - p1).cross((p1 - p0)).normalized();
//    }
    
    double t = (p0 - p).dot(n) / v.dot(n);

    if (t >= 0 && t < hit.depth) {
        // Sort vertices in order
        Vector vectors[3];
        vectors[0] = p0;
        vectors[1] = p1;
        vectors[2] = p2;
        for (int i=1;i<3;i++) {
            Vector v = vectors[i];
            if (v[1] >= vectors[0][1]) {
                if (v[1] == vectors[0][1]) {
                    if (v[0] < vectors[0][0]) {
                        Vector temp = vectors[0];
                        vectors[0] = v;
                        vectors[i] = temp;
                    }
                }
                else if (v[1] > vectors[0][1]) {
                    Vector temp = vectors[0];
                    vectors[0] = v;
                    vectors[i] = temp;
                }
            }
        }
        if (vectors[2][0] <= vectors[1][0]) {
            if (vectors[2][0] == vectors[1][0]) {
                if (vectors[2][1] < vectors[1][1]) {
                    Vector temp = vectors[1];
                    vectors[1] = vectors[2];
                    vectors[2] = temp;
                }
            }
            else if (vectors[2][0] < vectors[1][0]) {
                Vector temp = vectors[1];
                vectors[1] = vectors[2];
                vectors[2] = temp;
            }
        }
        
        double x = p[0] + t * v[0];
        double y = p[1] + t * v[1];
        double z = p[2] + t * v[2];
        
        Vector u = vectors[1] - vectors[0];
        Vector v = vectors[2] - vectors[0];
        Vector w = Vector(x, y, z) - vectors[0];
        double area = pow(u.dot(v), 2) - u.dot(u) * v.dot(v);
        double bary1 = (u.dot(v) * w.dot(v) - v.dot(v) * w.dot(u)) / area;
        double bary2 = (u.dot(v) * w.dot(u) - u.dot(u) * w.dot(v)) / area;
        
        if (bary1 >= 0 && bary2 >= 0 && (bary1 + bary2) <= 1) {
            hit.depth = t;
            hit.position = Vector(x, y, z);
            hit.normal = n;
            
            return true;
        }
    }
    
    return false;
    
}


