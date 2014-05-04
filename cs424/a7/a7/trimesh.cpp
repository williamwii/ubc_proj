#include "trimesh.h"

#include "loadobj.h"
#include <iostream>


//
// TriMesh public functions
//

TriMesh::TriMesh(const TriMesh& mesh)
{
    // Copy vertices
    std::map<const Vertex*,Vertex*> vertexMap;
    for (size_t i = 0; i < mesh.getVertices().size(); ++i)
    {
        Vertex* oldVert = mesh.getVertices()[i];
        Vertex* newVert = addVertex(oldVert->pos());
        vertexMap[oldVert] = newVert;
    }
    // Copy faces
    for (size_t i = 0; i < mesh.getFaces().size(); ++i)
    {
        Vertex* v1 = vertexMap[mesh.getFaces()[i]->vertex(0)];
        Vertex* v2 = vertexMap[mesh.getFaces()[i]->vertex(1)];
        Vertex* v3 = vertexMap[mesh.getFaces()[i]->vertex(2)];
        addFace(v1, v2, v3);
    }
}


TriMesh::~TriMesh()
{
    clear();
}


void TriMesh::clear()
{
    edgeMap.clear();
    while (!vertices.empty())
    {
        Vertex* v = vertices.back();
        vertices.pop_back();
        delete v;
    }
    while (!edges.empty())
    {
        HalfEdge* e = edges.back();
        edges.pop_back();
        delete e;
    }
    while (!faces.empty())
    {
        Face* f = faces.back();
        faces.pop_back();
        delete f;
    }
}


bool TriMesh::loadFromOBJ(const char* filename)
{
    std::vector<Vector> positions;
    std::vector< std::vector<int> > indices;
    bool success = read_objfile(positions, indices, filename);

    if (!success)
    {
        std::cerr << "Error reading OBJ file: " << filename << std::endl;
        return false;
    }

    clear();

    std::cout << "OBJ file read successfully, building mesh..." << std::flush;

    // Add vertices to our trimesh.
    Vertex** v = new Vertex*[positions.size()];
    for (unsigned int i = 0; i < positions.size(); ++i)
    {
        v[i] = addVertex(positions[i]);
    }

    // Add triangles to our trimesh.
    for (unsigned int i = 0; i < indices.size(); ++i)
    {
        addFace(v[indices[i][0]],
                v[indices[i][1]],
                v[indices[i][2]]);
    }

    std::cout << " mesh built!" << std::endl;

    delete[] v;

    return true;
}


TriMesh::Vertex* TriMesh::addVertex(const Vector& pos) 
{
    vertices.push_back(new Vertex(pos));
    return vertices.back();
}


TriMesh::Face* TriMesh::addFace(Vertex* v1, 
                                Vertex* v2, 
                                Vertex* v3)
{
    // Generate edges between vertices.
    HalfEdge* e1 = findEdge(v1,v2);
    if (e1 == NULL) { e1 = addEdge(v1,v2); }
    HalfEdge* e2 = findEdge(v2,v3);
    if (e2 == NULL) { e2 = addEdge(v2,v3); }
    HalfEdge* e3 = findEdge(v3,v1);
    if (e3 == NULL) { e3 = addEdge(v3,v1); }

    return addFace(e1, e2, e3);
}


TriMesh::Face* TriMesh::addFace(HalfEdge* e1,
                                HalfEdge* e2,
                                HalfEdge* e3)
{
    // Add the face to the trimesh.
    faces.push_back(new Face());
    Face* f = faces.back();

    // Initialize face-edge relationship.
    f->e = e1;
    
    // Initialize edge-face relationship.
    e1->f = e2->f = e3->f = f;

    // Connect edge cycle around face.
    e1->n = e2; e2->p = e1;
    e2->n = e3; e3->p = e2;
    e3->n = e1; e1->p = e3;

    return f;
}


void TriMesh::getBoundingBox(Vector& min, Vector& max) const
{
    if (vertices.size() == 0) return;

    min = max = vertices[0]->p;
    for (size_t i = 1; i < vertices.size(); ++i)
    {
        for (int j = 0; j < 3; ++j)
        {
            if (min[j] > vertices[i]->p[j])
            {
                min[j] = vertices[i]->p[j];
            }
            if (max[j] < vertices[i]->p[j])
            {
                max[j] = vertices[i]->p[j];
            }
        }
    }
}


TriMesh::HalfEdge* TriMesh::addEdge(Vertex* v1, Vertex* v2)
{
    edges.push_back(new HalfEdge());
    HalfEdge* e = edges.back();

    edgeMap[std::make_pair(v1, v2)] = e;

    // Associate edge with its origin vertex
    e->v = v1;
    if (v1->e == NULL) { v1->e = e; }

    // Associate edge with its twin, if it exists
    e->t = findEdge(v2, v1);
    if (e->t != NULL) { e->t->t = e; }

    return e;
}


TriMesh::HalfEdge* TriMesh::findEdge(const Vertex* v1, const Vertex* v2)
{
    std::map<std::pair<const Vertex*, const Vertex*>, HalfEdge*>::iterator itr 
        = edgeMap.find(std::make_pair(v1, v2));
    if (itr != edgeMap.end())
    {
        return itr->second;
    }
    return NULL;
}

