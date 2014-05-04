#include "subdivision.h"
#include <cstdlib>
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <algorithm>

using namespace std;
//
// Public functions
//

Subdivision::Subdivision()
{
    // Default Constructor
    
    // YOUR CODE HERE
}


Subdivision::~Subdivision()
{
    // Destructor
    
    // YOUR CODE HERE
}


void Subdivision::initialize(TriMesh* controlMesh)
{
    // Initializes this subdivision object with a mesh to use as
    // the control mesh (ie: subdivision level 0).
    
    // YOUR CODE HERE
    mesh = new typename TriMesh::TriMesh(*controlMesh);
    subdivisions[0] = mesh;
}


TriMesh* Subdivision::subdivide(int level)
{
    // Subdivides the control mesh to the given subdivision level.
    // Returns a pointer to the subdivided mesh.
    
    // HINT: Create a new subdivision mesh for each subdivision level and
    // store it in memory for later.
    // If the calling code asks for a level that has already been computed,
    // just return the pre-computed mesh!
    
    //    return NULL; // REPLACE THIS!
    
    // YOUR CODE HERE
    if (subdivisions[level])
        return subdivisions[level];
    
    // Find the last calculated subdivision
    int cal_level = 0;
    TriMesh *m = NULL;
    for (int i=level-1; i>=0; i--) {
        if (subdivisions[i]) {
            cal_level = i;
            m = subdivisions[i];
            break;
        }
    }
    
    for (int i=cal_level+1; i<=level; i++) {
        TriMesh *new_mesh = new typename TriMesh::TriMesh(*subdivisions[i - 1]);
        
        // Split edges
        std::vector<TriMesh::HalfEdge*> mesh_edges = new_mesh->getEdges();
        size_t es = mesh_edges.size();
        for (int j=0;j<es;j++) {
            if (!(mesh_edges[j]->splited)) {
                splitEdge(mesh_edges[j], new_mesh);
            }
        }
        
        // Update posistions
        std::vector<TriMesh::Vertex*> mesh_vertices = new_mesh->getVertices();
        size_t vs = mesh_vertices.size();
        std::vector<Vector> newPositions;
        for (int j=0; j<vs; j++) {
            Vector newPos;
            TriMesh::Vertex *v = mesh_vertices[j];
            if (!v->isNew) {
                std::vector<TriMesh::Vertex*> neighbours = findNeigbours(v, new_mesh);
                size_t ns = neighbours.size();
                //                double beta = (5.0 / 8.0 - pow(3.0 / 8.0 + 0.25 * cos(2 * M_PI / (double)ns), 2.0)) / (double)ns;
                double a = (40.0 - pow((3.0 + 2.0 * cos(2.0 * M_PI / ns)), 2.0)) / 64.0;
                newPos = (1 - a) * v->pos();
                for (int k=0; k<ns; k++) {
                    newPos += (a / (double)ns) * neighbours[k]->pos();
                }
            }
            else {
                TriMesh::Vertex *n1 = v->edge()->next()->origin();
                TriMesh::Vertex *n2 = v->edge()->prev()->origin();
                TriMesh::Vertex *f1 = v->edge()->next()->next()->next()->origin();
                TriMesh::Vertex *f2 = v->edge()->twin()->prev()->prev()->origin();
                
                newPos = (3 * (n1->pos() + n2->pos()) + f1->pos() + f2->pos()) / 8.0;
            }
            
            newPositions.push_back(newPos);
        }
        for (int j=0; j<vs; j++) {
            mesh_vertices[j]->pos() = newPositions[j];
        }
        
        // Cut corners
        std::vector<TriMesh::Face*> mesh_faces = new_mesh->getFaces();
        size_t fs = mesh_faces.size();
        for (int j=0; j<fs; j++) {
            TriMesh::Face *f = mesh_faces[j];
            if (f->edge()->next()->next()->next() == f->edge())
                continue;
            
            std::vector<TriMesh::Vertex*> newVertices;
            for (int k=0; k<3; k++) {
                cutACorner(f, new_mesh, &newVertices);
            }
            TriMesh::Vertex *v1 = newVertices[0];
            TriMesh::Vertex *v2 = newVertices[1];
            TriMesh::Vertex *v3 = newVertices[2];
            TriMesh::HalfEdge *e1 = new_mesh->addEdge(v1, v2);
            e1->splited = true;
            TriMesh::HalfEdge *e2 = new_mesh->addEdge(v2, v3);
            e2->splited = true;
            TriMesh::HalfEdge *e3 = new_mesh->addEdge(v3, v1);
            e3->splited = true;
            new_mesh->addFace(e1, e2, e3);
        }
        
        subdivisions[i] = new_mesh;
    }
    return subdivisions[level];
}



//
// Private Functions
//


// YOUR CODE HERE

void Subdivision::splitEdge(TriMesh::HalfEdge* he, TriMesh* M)
{
    TriMesh::Vertex *origin = he->origin();
    TriMesh::HalfEdge *he_twin = he->twin();
    
    // add new vertext v to M
    Vector s = origin->pos();
    Vector t = he->next()->origin()->pos();
    Vector pos((s[0] + t[0]) / 2.0,
               (s[1] + t[1]) / 2.0,
               (s[2] + t[2]) / 2.0);
    TriMesh::Vertex *v = M->addVertex(pos);
    v->isNew = true; // mark v as new
    
    // add 2 new halfedges to M
    TriMesh::HalfEdge *new_he_prev = M->addEdge(origin, v);
    TriMesh::HalfEdge *new_he_twin = M->addEdge(he->next()->origin(), v);
    
    new_he_prev->prev() = he->prev();
    new_he_prev->next() = he;
    new_he_prev->twin() = he_twin;
    he->prev()->next() = new_he_prev;
    if (he->origin()->edge() == he) {
        he->origin()->edge() = new_he_prev;
    }
    
    new_he_twin->prev() = he_twin->prev();
    new_he_twin->next() = he_twin;
    new_he_twin->twin() = he;
    he_twin->prev()->next() = new_he_twin;
    if (he_twin->origin()->edge() == he_twin) {
        he_twin->origin()->edge() = new_he_twin;
    }
    
    he_twin->prev() = new_he_twin;
    he_twin->twin() = new_he_prev;
    he_twin->origin() = v;
    
    he->prev() = new_he_prev;
    he->twin() = new_he_twin;
    he->origin() = v;
    
    he->splited = true; // make he, he.twin and the new halfedges as already splited
    he_twin->splited = true;
    new_he_prev->splited = true;
    new_he_twin->splited = true;
    
    v->edge() = he;
}

void Subdivision::cutACorner(TriMesh::Face* f, TriMesh* M, std::vector<TriMesh::Vertex*> *newVertices)
{
    TriMesh::HalfEdge *e1 = f->edge();
    TriMesh::HalfEdge *e2 = e1->next();
    TriMesh::HalfEdge *e3 = e2->next();
    TriMesh::Vertex *v1 = e1->origin();
    TriMesh::Vertex *v3 = e3->origin();
    
    newVertices->push_back(v3);
    
    
    // add halfedge h to M and mark as splited
    TriMesh::HalfEdge *h = M->addEdge(v3, v1);
    h->splited = true;
    
    // add a new face to M
    M->addFace(e1, e2, h);
    
    // update f.he()
    f->edge() = e3;
}


std::vector<TriMesh::Vertex*> Subdivision::findNeigbours(TriMesh::Vertex *v, TriMesh *mesh) {
    std::vector<TriMesh::Vertex*> neigbours;
    std::vector<TriMesh::HalfEdge*> edges = mesh->getEdges();
    for (int i=0; i<edges.size(); i++) {
        if (edges[i]->origin() == v) {
            if (std::find(neigbours.begin(), neigbours.end(), edges[i]->next()->origin()) == neigbours.end()) {
                neigbours.push_back(edges[i]->next()->origin());
            }
        }
        else if (edges[i]->next()->origin() == v) {
            if (std::find(neigbours.begin(), neigbours.end(), edges[i]->origin()) == neigbours.end()) {
                neigbours.push_back(edges[i]->origin());
            }
        }
    }
    
    return neigbours;
}

