/////////////////////////////////////////////////////////////
// FILE:      mygl.cpp
// CONTAINS:  your implementations of various GL functions
////////////////////////////////////////////////////////////.


#include <cstdlib>
#include <cfloat>
#include <iostream>
#include <cmath>
#include <vector>

#ifdef __APPLE__
#  include <GLUT/glut.h>
#else
#  include <GL/glut.h>
#endif

#include "image.hpp"
#include "mygl.hpp"

using namespace std;

// Flags that are defined/set in a3.cpp.
extern bool perspectiveCorrectTextures;
extern bool gridIsVisible;
extern bool drawAsPoints;

// The dimensions of the virtual window we are drawing into.
int virtualWidth;
int virtualHeight;

// The current projection and model-view matrices.
Matrix projectionMatrix;
Matrix modelViewMatrix;

// Current color, texture, and texture coordinates.
Vector currentColor;
Image *currentTexture;
double currentTextureCoord[2];
Vector currentNormal;


// A vertex with the vector and color
struct ownVertex {
    Vector vector;
    Vector color;
    double texture[2];
};

// Current shape
int currentShape;
std::vector<ownVertex> vertices;
Matrix lookAtMatrix;

void _drawLines();
void _drawTriangles();

// A class to simplify lookup of two-dimensional zBuffer data from an array
// of doubles. This zBuffer MUST have reshape(...) called before use.
class ZBuffer {
private:

    int height_, size_, allocated_;
    double *data_;

public:

    ZBuffer() : allocated_(0), data_(NULL) {}

    // Reshape the zBuffer because the window was reshaped.
    void reshape(int w, int h) {
        height_ = h;
        size_ = w * h;
        // Only need to bother reallocating the array when we need a larger
        // array.
        if (size_ > allocated_) {
            delete [] data_;
            allocated_ = 4 * size_;
            data_ = new double[allocated_];
        }
    }

    // Clear out all of the depth values.
    void clear() {
        for (int i = 0; i < size_; i++) {
            data_[i] = DBL_MAX;
        }
    }

    // Get a pointer to the given row of depth data, which can be indexed
    // again to get/set depth values.
    // E.g.: `zBuffer[x][y] = newDepthValue`.
    double* operator[](int index) {
        return data_ + index * height_;
    }

} zBuffer;

// A function to set a pixel value on the screen. This is the entry point that
// you MUST use to draw to the screen.
void setPixel(int x, int y, double r, double g, double b)
{
    if (x < 0 || x >= virtualWidth || y < 0 || y >= virtualHeight) {
        std::cerr << "attempting to set a pixel that is off-screen;" << x << ", " << y << std::endl;
        return;
    }
    glColor3d(r, g, b);
    glBegin(GL_POINTS);
    glVertex2i(x, y);
    glEnd();
}


// Draws the virtual pixel grid.
void drawPixelGrid() {

    // Dark gray.
    glColor4d(0.15, 0.15, 0.15, 1.0);

    // Draw vertical grid lines.
    for (float x = -0.5; x <= virtualWidth; x++) {
        glBegin(GL_LINES);
        glVertex3f(x, -0.5, 1.0);
        glVertex3f(x, virtualHeight + 0.5, 1.0);
        glEnd();
    }

    // Draw horizontal grid lines.
    for (float y = -0.5; y <= virtualHeight; y++) {
        glBegin(GL_LINES);
        glVertex3f(-0.5, y, 1.0);
        glVertex3f(virtualWidth + 0.5, y, 1.0);
        glEnd();
    }
}


void myViewport(int w, int h) {
    virtualWidth = w;
    virtualHeight = h;
    zBuffer.reshape(w, h);
}


void myClear() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    if (gridIsVisible) {
        drawPixelGrid();
    }
    zBuffer.clear();
}


void myLoadIdentity() {
    modelViewMatrix = Matrix::identity();
    projectionMatrix = Matrix::identity();
}


void myBindTexture(char const *name) {
    if (name) {
        currentTexture = &Image::fromFile(name);
    } else {
        currentTexture = NULL;
    }
}


void myBegin(int type) {
    // @@@@ YOUR CODE HERE
    switch (type) {
        case GL_LINES:
        case GL_TRIANGLES:
        case GL_POLYGON:
            currentShape = type;
            break;
        default:
            break;
    }
}


void myColor(double r, double g, double b) {
    // @@@@ YOUR CODE HERE
    currentColor = Vector(r,g,b,1);
}


void myVertex(double x, double y, double z) {
    // @@@@ YOUR CODE HERE
    ownVertex vertex;
    vertex.vector = Vector(x, y, z, 1);
    vertex.color = currentColor;
    vertex.texture[0] = currentTextureCoord[0];
    vertex.texture[1] = currentTextureCoord[1];
    vertices.push_back(vertex);
}


void myEnd() {
    // @@@@ YOUR CODE HERE
    Matrix viewPort(
        virtualWidth/2, 0, 0, 0,
        0, virtualHeight/2, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1
    );
    for (int i=0;i<vertices.size();i++) {
        Vector vector = projectionMatrix * modelViewMatrix * vertices[i].vector;
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
//        vector[3] /= vector[3];
        vector[0] = (vector[0] + 1) * virtualWidth / 2,
        vector[1] = (vector[1] + 1) * virtualHeight / 2,
        vertices[i].vector = vector;
        if (drawAsPoints) {
            switch (currentShape) {
                case GL_LINES:
                    if (i%2==0) {
                        setPixel(vertices[i].vector[0], vertices[i].vector[1], vertices[i].color[0], vertices[i].color[1], vertices[i].color[2]);
                    }
                    break;
                case GL_TRIANGLES:
                case GL_POLYGON:
                    setPixel(vertices[i].vector[0], vertices[i].vector[1], vertices[i].color[0], vertices[i].color[1], vertices[i].color[2]);
                    break;
            }
        }
    }

    if(!drawAsPoints) {
        switch (currentShape) {
            case GL_LINES:
                _drawLines();
                break;
                
            case GL_TRIANGLES:
                _drawTriangles();
                break;
                
            case GL_POLYGON:
                double x = 0;
                double y = 0;
                double z = 0;
                double r = 0;
                double g = 0;
                double b = 0;
                for (int i=0;i<vertices.size();i++) {
                    x += vertices[i].vector[0];
                    y += vertices[i].vector[1];
                    z += vertices[i].vector[2];
                    r += vertices[i].color[0];
                    g += vertices[i].color[1];
                    b += vertices[i].color[2];
                }
                Vector mid(x/vertices.size(), y/vertices.size(), z/vertices.size());
                ownVertex v;
                v.vector = mid;
                Vector color(r/vertices.size(), g/vertices.size(), b/vertices.size());
                v.color = color;
                vector<ownVertex> tempVs;
                for (int i=0;i<vertices.size();i++) {
                    tempVs.push_back(v);
                    tempVs.push_back(vertices[i%vertices.size()]);
                    tempVs.push_back(vertices[(i+1)%vertices.size()]);
                }
                vertices = tempVs;
                _drawTriangles();
                break;
        }
    }
    vertices.clear();
}


void _drawLines() {
    for (int i=0;i<vertices.size()/2;i++) {
        int index = i * 2;
        if ((index+1) < vertices.size()) {
            ownVertex v1 = vertices[index];
            ownVertex v2 = vertices[index+1];
            double m = (v2.vector[1] - v1.vector[1]) / (v2.vector[0] - v1.vector[0]);
            if ( abs(m) < 1 && !(v2.vector[0] - v1.vector[0])==0 ) {
                if (v1.vector[0] > v2.vector[0]) {
                    ownVertex temp = v1;
                    v1 = v2;
                    v2 = temp;
                }
                
                double sx = v1.vector[0]; double sy = v1.vector[1];// double sz = v1.vector[2];
                double dx = v2.vector[0]; double dy = v2.vector[1];// double dz = v2.vector[2];
                double cr = v1.color[0]; double cg = v1.color[1]; double cb = v1.color[2];           // Source Colors
                double dcr = v2.color[0]; double dcg = v2.color[1]; double dcb = v2.color[2];        // Destination Colors
                m = (dy - sy) / (dx - sx);                   // Slope
                double d = sqrt(pow(dy - sy, 2) + pow(dx - sx, 2)); // Distance
                
                for (int j=0;j<(dx-sx);j++) {
                    double x = sx + j;
                    double y = sy + j * m;
                    double percentage = sqrt(pow(y - sy, 2) + pow(x - sx, 2)) / d;
                    double r = cr * percentage + dcr * (1-percentage);
                    double g = cg * percentage + dcg * (1-percentage);
                    double b = cb * percentage + dcb * (1-percentage);
                    if ((x >= 0) && (x <= virtualWidth) && (y >= 0 ) && (y <= virtualHeight)) {
                        setPixel(x, y, r, g, b);
                    }
                }
            }
            else {
                if (v1.vector[1] > v2.vector[1]) {
                    ownVertex temp = v1;
                    v1 = v2;
                    v2 = temp;
                }
                
                double sx = v1.vector[0]; double sy = v1.vector[1];// double sz = v1.vector[2];
                double dx = v2.vector[0]; double dy = v2.vector[1];// double dz = v2.vector[2];
                double cr = v2.color[0]; double cg = v2.color[1]; double cb = v2.color[2];           // Source Colors
                double dcr = v1.color[0]; double dcg = v1.color[1]; double dcb = v1.color[2];        // Destination Colors
                m = (dx - sx) / (dy - sy);                   // Slope
                double d = sqrt(pow(dy - sy, 2) + pow(dx - sx, 2)); // Distance
                
                for (int j=0;j<(dy-sy);j++) {
                    double y = sy + j;
                    double x = sx + j * m;
                    double percentage = sqrt(pow(y - sy, 2) + pow(x - sx, 2)) / d;
                    double r = cr * percentage + dcr * (1-percentage);
                    double g = cg * percentage + dcg * (1-percentage);
                    double b = cb * percentage + dcb * (1-percentage);
                    if ((x >= 0) && (x <= virtualWidth) && (y >= 0 ) && (y <= virtualHeight))
                        setPixel(x, y, r, g, b);
                }
            }
        }
    }
}

void _drawTriangles() {
    for (int i=0;i<vertices.size()/3;i++) {
        int index = i * 3;
        if ((index + 2) < vertices.size()) {
            ownVertex sortedVertices[3];
            sortedVertices[0] = vertices[index];
            sortedVertices[1] = vertices[index+1];
            sortedVertices[2] = vertices[index+2];
            
            for (int j=0;j<3;j++) {
                int minIndex = j;
                double x = sortedVertices[j].vector[0];
                double minY = sortedVertices[j].vector[1];
                for (int k=j+1;k<3;k++) {
                    if (minY >= sortedVertices[k].vector[1]) {
                        if (minY == sortedVertices[k].vector[1]) {
                            if (x > sortedVertices[k].vector[0]) {
                                minIndex = k;
                                minY = sortedVertices[k].vector[1];
                                x = sortedVertices[k].vector[0];
                            }
                        }
                        else {
                            minIndex = k;
                            minY = sortedVertices[k].vector[1];
                            x = sortedVertices[k].vector[0];
                        }
                    }
                }
                ownVertex temp = sortedVertices[j];
                sortedVertices[j] = sortedVertices[minIndex];
                sortedVertices[minIndex] = temp;
            }
            double startY = sortedVertices[0].vector[1];
            double endY = sortedVertices[2].vector[1];
            
            ownVertex topV = sortedVertices[2];
            ownVertex leftV, rightV;
            if (sortedVertices[0].vector[0] > sortedVertices[1].vector[0]) {
                rightV = sortedVertices[0];
                leftV = sortedVertices[1];
            }
            else if (sortedVertices[0].vector[0] == sortedVertices[1].vector[0]) {
                if (topV.vector[0] < sortedVertices[0].vector[0]) {
                    if (sortedVertices[0].vector[1] > sortedVertices[1].vector[1]) {
                        rightV = sortedVertices[0];
                        leftV = sortedVertices[1];
                    }
                    else {
                        leftV = sortedVertices[0];
                        rightV = sortedVertices[1];
                    }
                }
                else {
                    if (sortedVertices[0].vector[1] > sortedVertices[1].vector[1]) {
                        leftV = sortedVertices[0];
                        rightV = sortedVertices[1];
                    }
                    else {
                        rightV = sortedVertices[0];
                        leftV = sortedVertices[1];
                    }
                }
            }
            else {
                leftV = sortedVertices[0];
                rightV = sortedVertices[1];
            }
            
            double leftM = (topV.vector[0] - leftV.vector[0]) / (topV.vector[1] - leftV.vector[1]);
            double rightM = (topV.vector[0] - rightV.vector[0]) / (topV.vector[1] - rightV.vector[1]);
            for (int j=0;j<(endY-startY);j++) {
                double y = endY - j;
                
                double startX;
                if (y > leftV.vector[1]) {
                    startX = topV.vector[0] - j * leftM;
                }
                else {
                    leftM = (rightV.vector[0] - leftV.vector[0]) / (rightV.vector[1] - leftV.vector[1]);
                    startX = leftV.vector[0] - (j - topV.vector[1]  + leftV.vector[1]) * leftM;
                }
                double endX;
                if (y > rightV.vector[1]) {
                    endX = topV.vector[0] - j * rightM;
                }
                else {
                    rightM = (leftV.vector[0] - rightV.vector[0]) / (leftV.vector[1] - rightV.vector[1]);
                    endX = rightV.vector[0] - (j - topV.vector[1] + rightV.vector[1]) * rightM;
                }
                
                if (endX < startX) {
                    double temp = endX;
                    endX = startX;
                    startX = temp;
                }
                for (int x=startX;x<endX;x++) {
                    Vector p(x,y,0);
                    
                    double area = (topV.vector - leftV.vector).cross(rightV.vector - leftV.vector).length();
                    double rightA = (topV.vector - p).cross(leftV.vector - p).length();
                    double leftA = (topV.vector - p).cross(rightV.vector - p).length();
                    double topA = (leftV.vector - p).cross(rightV.vector - p).length();
                    
                    double rightB = rightA / area;
                    double leftB = leftA / area;
                    double topB = topA / area;
                    
                    double z = rightV.vector[2] * rightB + topV.vector[2] * topB + leftV.vector[2] * leftB;
                    
                    double r, g, b;
                    if (currentTexture==NULL) {
                        r = rightV.color[0] * rightB + topV.color[0] * topB + leftV.color[0] * leftB;
                        g = rightV.color[1] * rightB + topV.color[1] * topB + leftV.color[1] * leftB;
                        b = rightV.color[2] * rightB + topV.color[2] * topB + leftV.color[2] * leftB;
                    }
                    else {
                        double textureU, textureV;
                        if (perspectiveCorrectTextures) {
                            double sum = 1 / topV.vector[3] * topB + 1 / leftV.vector[3] * leftB + 1 / rightV.vector[3] * rightB;
                            textureU = topV.texture[0] / topV.vector[3] * topB + leftV.texture[0] / leftV.vector[3] * leftB + rightV.texture[0] / rightV.vector[3] * rightB;
                            textureV = topV.texture[1] / topV.vector[3] * topB + leftV.texture[1] / leftV.vector[3] * leftB + rightV.texture[1] / rightV.vector[3] * rightB;
                            textureU = textureU / sum;
                            textureV = textureV / sum;
                        }
                        else {
                            textureU = topV.texture[0] * topB + leftV.texture[0] * leftB + rightV.texture[0] * rightB;
                            textureV = topV.texture[1] * topB + leftV.texture[1] * leftB + rightV.texture[1] * rightB;
                        }
                        Vector color = currentTexture->lookup(textureU, textureV);
                        r = color[0];
                        g = color[1];
                        b = color[2];
                    }
                    
                    if ((x > 0) && (x < virtualWidth) && (y > 0 ) && (y < virtualHeight)) {
                        double oldZ = zBuffer[int(x)][int(y)];
                        if (z <= oldZ) {
                            setPixel(x, y, r, g, b);
                            zBuffer[int(x)][int(y)] = z;
                        }
                    }
                }
            }
        }
    }
}


void myTranslate(double tx, double ty, double tz) {
    // @@@@ YOUR CODE HERE
    Matrix translateM(
        1,0,0,tx,
        0,1,0,ty,
        0,0,1,tz,
        0,0,0,1
    );
    modelViewMatrix *= translateM;
}


void myRotate(double angle, double axisX, double axisY, double axisZ) {
    // @@@@ YOUR CODE HERE
    // @@@@ NOTE: Make sure you're aware of degrees vs. radians!
    double angleR = angle * M_PI / 180;
    modelViewMatrix *= Matrix::rotation(angleR, Vector(axisX, axisY, axisZ));
}


void myScale(double sx, double sy, double sz) {
    // @@@@ YOUR CODE HERE
    Matrix scaleM(
        sx,0,0,0,
        0,sy,0,0,
        0,0,sz,0,
        0,0,0,1
    );
    modelViewMatrix *= scaleM;
}


void myFrustum(double left, double right, double bottom, double top, double near, double far) {
    // @@@@ YOUR CODE HERE
    Matrix frustumM(
        2 * near / (right - left), 0, (right + left) / (right - left), 0,
        0, 2 * near / (top - bottom), (top + bottom) / (top - bottom), 0,
        0, 0, -(far + near) / (far - near), -2 * far * near / (far - near),
        0, 0, -1, 0
    );
    
    projectionMatrix *= frustumM;
}

void myLookAt(double eyeX, double eyeY, double eyeZ,
              double cenX, double cenY, double cenZ,
              double  upX, double  upY, double  upZ) {
    // @@@@ YOUR CODE HERE
    Vector w(eyeX-cenX, eyeY-cenY, eyeZ-cenZ);
    w.normalize();
    Vector up(upX, upY, upZ);
    Vector u = up.cross(w, 1.0);
    u.normalize();
    Vector v = w.cross(u, 1.0);
    v.normalize();
    
    Matrix R(
        u[0], u[1], u[2], 0,
        v[0], v[1], v[2], 0,
        w[0], w[1], w[2], 0,
        0, 0, 0, 1
    );
    Matrix T(
        1, 0, 0, -eyeX,
        0, 1, 0, -eyeY,
        0, 0, 1, -eyeZ,
        0, 0, 0, 1
    );
    
    modelViewMatrix = R * T;
}


void myTexCoord(double s, double t) {
    // @@@@ YOUR CODE HERE
    currentTextureCoord[0] = s;
    currentTextureCoord[1] = t;
}


void myNormal(double x, double y, double z) {
    // @@@@ YOUR CODE HERE
}

