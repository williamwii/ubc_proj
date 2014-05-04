
#define _USE_MATH_DEFINES
#include <cmath>
#include <cstdlib>
#include <iostream>

#ifdef __APPLE__
#include <GLUT/glut.h>
#else
#include <GL/glut.h>
#endif

#include "subdivision.h"
#include "trimesh.h"
#include "linalg.h"

//
// Global variables
//

TriMesh  controlMesh;
TriMesh* mesh;

double near, far;
double angleX = 0.0;
double angleY = 0.0;
double aspect = 1.0;
int xPos, yPos;
int mouseButton;
bool lighting = true;
bool drawVertices = false;
bool drawEdges = true;
bool drawFaces = true;

Subdivision subdivision;

int subdivisionDepth = 0;
int subdivisionDepthOld = 0;
const int SUBDIVISION_MAX = 5;

int winWidth, winHeight;

Vector objCenter;
GLfloat viewDistance;

GLfloat lightDir[4] = {M_SQRT1_2, 0.0, -M_SQRT1_2, 0.0};

GLfloat lightColor[4] = {1.0, 0.2, 0.2, 0.0};
GLfloat surfColor[4] =  {1.0, 1.0, 1.0, 0.0};
GLfloat ambColor[4] =   {0.9, 0.9, 0.9, 0.0};
GLfloat black[4] =      {0.0, 0.0, 0.0, 0.0};
GLfloat exponent = 128;


void usage(const char* progname)
{
    std::cerr << progname << " <geometry.OBJ>" << std::endl;
    exit(1);
}


void init(const char* geofile)
{
    glEnable(GL_DEPTH_TEST);
    glFrontFace(GL_CCW);
    glDepthFunc(GL_LEQUAL);
    glPointSize(2.0);
    glPolygonOffset(0.5, 0.0);

    // Load mesh and get bounding box for an estimate of a good view
    Vector min, max;
    bool success = controlMesh.loadFromOBJ(geofile);
    if (!success)
    {
        std::cerr << "Error loading mesh from OBJ file." << std::endl;
        exit(2);
    }
    mesh = &controlMesh;
    controlMesh.getBoundingBox(min, max);
    objCenter = (min+max)/2.0;
    float diameter = (max-min).length();
    // assuming a 45 degree field of view, 3*radiu of a bounding sphere
    // is a good viewing distance
    viewDistance = 1.5*diameter;
    near = viewDistance - 1.3*diameter;
    far = viewDistance + 3.5*diameter;
    
    // Initialize subdivision surfaces data structure
    subdivision.initialize(&controlMesh);

    // Lighting and shadng settings
    lighting = true;
    glLightfv(GL_LIGHT0, GL_DIFFUSE, ambColor);
    glLightfv(GL_LIGHT0, GL_SPECULAR, lightColor);
    glLightfv(GL_LIGHT0, GL_AMBIENT, ambColor);
    glEnable(GL_LIGHT0);
    glEnable(GL_LIGHTING);

    glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT, black);
    glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, black);
    glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, surfColor);
    glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, exponent);
    glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
    glEnable(GL_COLOR_MATERIAL);

    glLightModelf(GL_LIGHT_MODEL_TWO_SIDE, 1.0);
    glLightModelfv(GL_LIGHT_MODEL_AMBIENT, black);
}


void redraw()
{
    // clear buffers
    glClearColor(0.9, 0.9, 0.9, 0.9);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // a generic perspective transform
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    gluPerspective(45, aspect, near, far);
    // rotate geometry
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
    gluLookAt(0.0, 0.0, viewDistance, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

    // update light direction
    glLightfv(GL_LIGHT0, GL_POSITION, lightDir);

    glRotated(angleY, 0.0, 1.0, 0.0);
    glRotated(angleX, 1.0, 0.0, 0.0);

    // move object center to origin
    glTranslatef(-objCenter[0], -objCenter[1], -objCenter[2]);

    // Update subidivision level, if applicable
    if (subdivisionDepth != subdivisionDepthOld)
    {
        mesh = subdivision.subdivide(subdivisionDepth);
        subdivisionDepthOld = subdivisionDepth;
    }

    if (mesh == NULL)
    {
        glutSwapBuffers();
        return;
    }

    // Draw trimesh faces
    if (drawFaces)
    {
        if (lighting)
        {
            glEnable(GL_LIGHTING);
        }
        else
        {
            glDisable(GL_LIGHTING);
        }
        glEnable(GL_POLYGON_OFFSET_FILL);

        // draw face geometry
        glColor3f(0.7, 0.7, 0.5);
        glBegin(GL_TRIANGLES);
        for (size_t i = 0; i < mesh->getFaces().size(); ++i)
        {
            const TriMesh::Face* f = mesh->getFaces()[i];
            glNormal3fv(f->computeNormal());
            glVertex3fv(f->vertex(0)->pos());
            glVertex3fv(f->vertex(1)->pos());
            glVertex3fv(f->vertex(2)->pos());
        }
        glEnd();

        glDisable(GL_POLYGON_OFFSET_FILL);
        glDisable(GL_LIGHTING);
    }

    // Draw trimesh vertices
    if (drawVertices)
    {
        glColor3f(0.0, 0.0, 1.0);
        glBegin(GL_POINTS);
        for (size_t i = 0; i < mesh->getVertices().size(); ++i)
        {
            glVertex3fv(mesh->getVertices()[i]->pos());
        }
        glEnd();
    }

    // Draw trimesh edges
    if (drawEdges)
    {
        glColor3f(1.0, 0.0, 0.0);
        glBegin(GL_LINES);
        for (size_t i = 0; i < mesh->getEdges().size(); ++i)
        {
            const TriMesh::HalfEdge* e = mesh->getEdges()[i];
            glVertex3fv(e->origin()->pos());
            glVertex3fv(e->next()->origin()->pos());
        }
        glEnd();
    }

    glutSwapBuffers();
}


void reshape(int w, int h)
{
    winWidth = w;
    winHeight = h;
    glViewport(0, 0, w, h);
    aspect = (double)w/h;
    glutPostRedisplay();
}


void keyboard(unsigned char c, int x, int y)
{
    bool redraw = false;

    switch(c)
    {
    case 'w': { // Toggle wireframe mode
        int mode[2];
        glGetIntegerv(GL_POLYGON_MODE, mode);
        if (mode[0] == GL_FILL)
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        else
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        redraw = true;
        } break;
    case 'l': // Toggle lighting
        lighting = !lighting;
        redraw = true;
        break;
    case 'v': // Toggle drawing vertices
        drawVertices = !drawVertices;
        redraw = true;
        break;
    case 'e': // Toggle drawing edge
        drawEdges = !drawEdges;
        redraw = true;
        break;
    case 'f': // Toggle drawing faces
        drawFaces = !drawFaces;
        redraw = true;
        break;
    case '<': // Reduce subdivision level
        if (subdivisionDepth > 0)
        {
            subdivisionDepth--;
            redraw = true;
        }
        break;
    case '>': // Increase subdivision level
        if (subdivisionDepth < SUBDIVISION_MAX)
        {
            subdivisionDepth++;
            redraw = true;
        }
        break;
    case 27:
    case 'q':
        exit(0);
        break;
    }
    
    if (redraw)
    {
        glutPostRedisplay();
    }
}


void mouse(int button, int state, int x, int y)
{
    xPos = x; yPos = y;
    mouseButton = button;
}


void move(int x, int y)
{
    bool redraw = false;

    switch(mouseButton)
    {
    case 0: // Rotate geometry
        angleY += (x-xPos)/2.0;
        angleX += (y-yPos)/2.0;
        xPos = x; yPos = y;

        while (angleX > 360.0) angleX -= 360.0;
        while (angleX < 0.0)   angleX += 360.0;
        while (angleY > 360.0) angleY -= 360.0;
        while (angleY < 0.0)   angleY += 360.0;
        
        redraw = true;
        break;

    case 2: // Zoom geometry
        viewDistance += (y-yPos)/20.0;
        xPos = x; yPos = y;
        if (viewDistance < 0.0) viewDistance = 0.0;

        redraw = true;
        break;
    }

    if (redraw)
    {
        glutPostRedisplay();
    }
}


int main(int argc, char* argv[])
{
    // Initialize GLUT window
    glutInit(&argc, argv);
    glutInitWindowSize(800, 600);
    glutInitDisplayMode(GLUT_DEPTH | GLUT_RGBA | GLUT_DOUBLE);
    glutCreateWindow("Mesh Viewer");

    // Process arguments
    int i;
    for (i = 1; i < argc; ++i)
    {
        if (argv[i][0] != '-')
        {
            break;
        }
		/*
        switch (argv[i][1])
        {
        default:
            usage(argv[0]);
        }
		*/
    }
    if (i == argc-1)
    {
        // Initialize with first argument, the name of the geometry file.
        init(argv[argc-1]);
    }
    else
    {
        usage(argv[0]);
    }

    // Register callbacks
    glutDisplayFunc(redraw);
    glutReshapeFunc(reshape);
    glutKeyboardFunc(keyboard);
    glutMouseFunc(mouse);
    glutMotionFunc(move);


    // Start main loop
    glutMainLoop();
}

