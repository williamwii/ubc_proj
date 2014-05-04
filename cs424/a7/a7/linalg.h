#ifndef _LINALG_H_
#define _LINALG_H_

#define _USE_MATH_DEFINES
#include <cmath>

// A generalized vector class.
// Can be used as a 3-vector, 4-vector, or 3-vector with homogeneous component
// By default, the fourth component is zero.
class Vector
{
public:
    // Default constructor
    inline Vector() { vec[0] = vec[1] = vec[2] = vec[3] = 0.0; }
    // Construct from fwo floats
    inline Vector(float x, float y, float z, float w = 0.0) 
    { vec[0] = x; vec[1] = y; vec[2] = z; vec[3] = w; }
    // Copy constructor
    inline Vector(const Vector& v) 
    { vec[0]=v.vec[0]; vec[1]=v.vec[1]; vec[2]=v.vec[2]; vec[3]=v.vec[3]; }

    // Assignment operator
    inline Vector& operator=(const Vector& v) { 
        vec[0]=v.vec[0]; vec[1]=v.vec[1]; vec[2]=v.vec[2]; vec[3]=v.vec[3];
        return *this; 
    }

    // Element access
    inline float& operator[](int i) { return vec[i]; }
    inline const float& operator[](int i) const { return vec[i]; }

    // Cast operator to float array
    inline operator float*() { return vec; }
    inline operator const float*() const { return vec; }

    // Comparison operators
    inline bool operator==(const Vector& v) const {
        return vec[0] == v.vec[0] && vec[1] == v.vec[1] 
            && vec[2] == v.vec[2] && vec[3] == v.vec[3];
    }
    inline bool operator!=(const Vector& v) const { return !(*this == v); }

    //
    // Arithmetic operators
    //

    // Vector-vector addition/subtraction
    // NOTE: These ignore the homogeneous component.
    inline Vector operator+(const Vector& v) const
    { return Vector(vec[0]+v.vec[0], vec[1]+v.vec[1], vec[2]+v.vec[2]); }
    inline Vector operator-(const Vector& v) const
    { return Vector(vec[0]-v.vec[0], vec[1]-v.vec[1], vec[2]-v.vec[2]); }
    inline Vector& operator+=(const Vector& v)
    { vec[0]+=v.vec[0]; vec[1]+=v.vec[1]; vec[2]+=v.vec[2]; return *this; }
    inline Vector& operator-=(const Vector& v)
    { vec[0]-=v.vec[0]; vec[1]-=v.vec[1]; vec[2]-=v.vec[2]; return *this; }

    // Vector-scalar multiplication/division
    inline Vector operator*(float a) const
    { return Vector(vec[0]*a, vec[1]*a, vec[2]*a, vec[3]*a); }
    inline Vector operator/(float a) const {
        float inv_a = 1.0f/a; 
        return Vector(vec[0]*inv_a, vec[1]*inv_a, vec[2]*inv_a, vec[3]*inv_a);
    }
    inline Vector& operator*=(float a)
    { vec[0] *= a; vec[1] *= a; vec[2] *= a; vec[3] *= a; return *this; }
    inline Vector& operator/=(float a) {
        float inv_a = 1.0f/a; 
        vec[0]*=inv_a; vec[1]*=inv_a; vec[2]*=inv_a; vec[3]*=inv_a;
        return *this;
    }

    // Normalization operations
    // NOTE: These ignore the homogeneous component.
    inline float norm2() const 
    { return vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]; }
    inline float norm() const { return sqrt(norm2()); }
    inline float length() const { return norm(); }
    inline Vector& normalize() {
        float h = norm();
        if (h != 0.0)
        {
            h = 1.0f/h;
            vec[0] *= h; vec[1] *= h; vec[2] *= h;
        }
        return *this;
    }

    // Dot product
    inline float dot(const Vector& v) const 
    { return vec[0]*v.vec[0] + vec[1]*v.vec[1] + vec[2]*v.vec[2]; }

    // Dot product, including homogeneous component
    inline float dotH(const Vector& v) const
    { return vec[0]*v.vec[0]+vec[1]*v.vec[1]+vec[2]*v.vec[2]+vec[3]*v.vec[3]; }

    // Cross product
    inline Vector cross(const Vector& v) const {
        return Vector(vec[1]*v.vec[2] - v[2]*v.vec[1],
                        vec[2]*v.vec[0] - v[0]*v.vec[2],
                        vec[0]*v.vec[1] - v[1]*v.vec[0]);
    }


private:
    float vec[4];
};

//
// Free functions
//

// Scalar-vector multiplication operator
static inline Vector operator*(float a, const Vector& v)
{ return Vector(v[0]*a, v[1]*a, v[2]*a, v[3]*a); }

// Dot product
inline float dot(const Vector& v1, const Vector& v2)
{ return v1[0]*v2[0] + v1[1]*v2[1] + v1[2]*v2[2]; }

// Cross product
inline Vector cross(const Vector& v1, const Vector& v2) {
    return Vector(v1[1]*v2[2] - v1[2]*v2[1],
                    v1[2]*v2[0] - v1[0]*v2[2],
                    v1[0]*v2[1] - v1[1]*v2[0]);
}



// A class to represent transformation matrices. They are stored in
// row-major order, so they are indexed by row and then column.
class Matrix {
private:
    
    Vector data[4];

public:

    // Default constructor.
    Matrix();
    
    // Copy constructor.
    Matrix(Matrix const &other);

    // Construct from rows.
    Matrix(Vector const &a, Vector const &b, Vector const &c);
    Matrix(Vector const &a, Vector const &b, Vector const &c, Vector const &d);

    // Construct from 9 floats.
    Matrix(float a, float b, float c,
           float d, float e, float f,
           float g, float h, float i);

    // Construct from 16 floats.
    Matrix(float a, float b, float c, float d,
           float e, float f, float g, float h,
           float i, float j, float k, float l,
           float m=0, float n=0, float o=0, float p=1);
    
    // Build an identity matrix.
    static Matrix identity();

    // Build a matrix for rotating about an arbitrary axis.
    static Matrix rotation(float angle, Vector const &axis);
    
    // Build a matrix for translating.
    static Matrix translation(float x, float y, float z);

    // Build a matrix for scaling.
    static Matrix scale(float x, float y, float z);

    // Print out a matrix (split onto 4 lines if "pretty").
    void print(bool pretty=false) const;
    
    // Indexing operators. Allows for getting/setting rows of a matrix,
    // which can also be indexed themselves.
    // E.g.: `m[0]` is the first row.
    //       `m[1][2]` is the 3rd element of the 2nd row.
    inline Vector  operator[](int index) const { return data[index]; }
    inline Vector& operator[](int index)       { return data[index]; }
    
    // Return a transposed copy of the matrix.
    Matrix transpose() const;

    // Invert a matrix.
    bool invert(Matrix &inv) const;

    // Multiplication of a column Vector by the matrix.
    // E.g.: `Vector b = m * a` where m is a Matrix and a is a Vector.
    Vector operator*(Vector const &other) const;

    // Matrix multiplication.
    // E.g.: `Matrix c = a * b`.
    Matrix operator*(Matrix const &other) const;

    // In-place matrix multiplication.
    // E.g.: `a *= b` is the same as `a = a * b`.
    void operator*=(Matrix const &other);



};


#endif
