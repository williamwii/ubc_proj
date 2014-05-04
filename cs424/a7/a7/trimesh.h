#ifndef _TRIMESH_H_
#define _TRIMESH_H_

#include "linalg.h"
#include <cassert>
#include <cstdlib>
#include <vector>
#include <map>

class TriMesh
{
public:
    // Forward declarations of nested classes
    class Vertex;
    class HalfEdge;
    class Face;

    //
    // Class defining a vertex of a TriMesh.
    //
    class Vertex
    {
    public:
        // Returns the position of the vertex.
        inline Vector& pos() { return p; }
        inline const Vector& pos() const { return p; }

        // Returns the half-edge associated with this vertex.
        inline HalfEdge*& edge() { return e; }
        inline HalfEdge*  edge() const { return e; }

        bool isNew; // A flag indicate if the vertex is new
    private:
        friend class TriMesh;

        // Private constructors
        // Vertex objects can only be constructed by TriMesh class.
        Vertex() : e(NULL), isNew(false) {}
        Vertex(const Vector& pos) : p(pos), e(NULL), isNew(false) {}
        ~Vertex() {}
        
        // The position of the vertex.
        Vector p;

        // The first half-edge originating from this vertex.
        HalfEdge* e;
    };


    //
    // Class defining a half-edge of a TriMesh.
    //
    class HalfEdge
    {
    public:
        // Returns the twin half-edge to this one.
        inline HalfEdge*& twin() { return t; }
        inline HalfEdge*  twin() const { return t; }

        // Returns the half-edge preceding this one.
        inline HalfEdge*& prev() { return p; }
        inline HalfEdge*  prev() const { return p; }

        // Returns the half-edge following this one.
        inline HalfEdge*& next() { return n; }
        inline HalfEdge*  next() const { return n; }

        // Returns the vertex from which this half-edge originates.
        inline Vertex*& origin() { return v; }
        inline Vertex*  origin() const { return v; }

        // Returns the face incident to this half-edge.
        inline Face*& face() { return f; }
        inline Face*  face() const { return f; }

        bool splited; // a flag indicate if the edge is new
    private:
        friend class TriMesh;

        // Private constructor/destructor
        // HalfEdge objects can only be constructed by TriMesh class.
        HalfEdge() : v(NULL), t(NULL), p(NULL), n(NULL), f(NULL), splited(false) {}
        ~HalfEdge() {}

        Vertex*   v; // The vertex from which this half-edge originates
        HalfEdge* t; // The twin (or dual) half-edge to this one
        HalfEdge* p; // The previous half-edge before this one
        HalfEdge* n; // The next half-edge after this one
        Face*     f; // The mesh face incident to this half-edge
    };

    
    //
    // Class defining a face of a TriMesh.
    //
    class Face
    {
    public:
        // Returns the half-edge associated with this face.
        inline HalfEdge*& edge() { return e; }
        inline HalfEdge*  edge() const { return e; }

        // Returns one of the three vertices that define this face.
        inline const Vertex* vertex(int index) const {
            if (e != NULL) {
                switch (index) {
                    case 0: return e->origin();
                    case 1: return e->next()->origin();
                    case 2: return e->prev()->origin();
                    default: assert(index >= 0 && index <= 2);
                }
            }
            return NULL;
        }

        inline Vector computeNormal() const {
            const Vector& v0 = vertex(0)->pos();
            const Vector& v1 = vertex(1)->pos();
            const Vector& v2 = vertex(2)->pos();
            return cross(v2-v0,v1-v0).normalize();
        }

    private:
        friend class TriMesh;

        // Private constructor
        // Face objects can only be constructed by TriMesh class.
        Face() : e(NULL) {}
        ~Face() {}

        // The first half-edge adjacent to this face.
        HalfEdge* e;
    };


    //
    // TriMesh public functions
    //

    // Default constructor
    TriMesh() {}

    // Copy constructor
    TriMesh(const TriMesh& mesh);

    // Destructor
    ~TriMesh();


    // Clear the contents of the mesh.
    void clear();

    // Read in the given .OBJ file and use it to initialize this TriMesh.
    bool loadFromOBJ(const char* filename);

    
    // Add a new vertex to the TriMesh at the given position.
    // Returns a pointer to the newly added vertex.
    Vertex* addVertex(const Vector& pos);

    // Add a new half-edge to the TriMesh between the two given vertices.
    // The given vertices must already have been added to the mesh.
    // v1 will be set as the origin of the edge, and if an edge can
    // be found from v2 to v1, it will be set as the twin of the new edge.
    // Returns a pointer to the newly added half-edge.
    HalfEdge* addEdge(Vertex* v1, Vertex* v2);

    // Add a new face to the TriMesh, consisting of three given vertices.
    // The given vertices must already have been added to the mesh.
    // Half-edges will be found or created linking the given vertices in order
    // (ie: v1-v2-v3).  All vertex-edge and edge-face pointers will be updated.
    // Returns a pointer to the newly created face.
    Face* addFace(Vertex* v1, Vertex*v2, Vertex* v3);

    // Add a new face to the TriMesh, defined by the three given half-edges.
    // The given half-edges must already have been added to the mesh.
    // All edge-face pointers will be updated by this function.
    // Returns a pointer to the newly created face.
    Face* addFace(HalfEdge* e1, HalfEdge* e2, HalfEdge* e3);


    // Compute the bounding box of the mesh.
    void getBoundingBox(Vector& min, Vector& max) const;

    // Returns the vertices of the mesh.
    const std::vector<Vertex*>& getVertices() const { return vertices; }

    // Returns the half-edges of the mesh.
    const std::vector<HalfEdge*>& getEdges() const { return edges; }

    // Returns the faces of the mesh.
    const std::vector<Face*>& getFaces() const { return faces; }


private:
    std::vector<Vertex*> vertices;
    std::vector<HalfEdge*> edges;
    std::vector<Face*> faces;

    // This is only used when builiding the mesh
    std::map<std::pair<const Vertex*, const Vertex*>, HalfEdge*> edgeMap;

    HalfEdge* findEdge(const Vertex* v1, const Vertex* v2);
};

#endif

