#ifndef _SUBDIVISION_H_
#define _SUBDIVISION_H_

#include "trimesh.h"
#include "linalg.h"
#include <vector>


//
// A class for performing subdivision operations on a mesh.
//
class Subdivision {

public:

    // Default constructor
    Subdivision();

    // Destructor
    ~Subdivision();

    // Initializes this subdivision object with a mesh to use as 
    // the control mesh (ie: subdivision level 0).
    void initialize(TriMesh* controlMesh);

    // Subdivides the control mesh to the given subdivision level.
    // Returns a pointer to the subdivided mesh.
    TriMesh* subdivide(int level);  

private:

    // YOUR CODE HERE
    TriMesh *mesh;
    std::map<int, TriMesh*> subdivisions; // a map from level to the subdivied mesh
    
    void splitEdge(TriMesh::HalfEdge* he, TriMesh* M);
    void cutACorner(TriMesh::Face* f, TriMesh* M, std::vector<TriMesh::Vertex*> *newVertices);
    
    std::vector<TriMesh::Vertex*> findNeigbours(TriMesh::Vertex *v, TriMesh *mesh);
};

#endif
