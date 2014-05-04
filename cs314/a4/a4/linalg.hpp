#ifndef LINALG_H
#define LINALG_H

#include <vector>


// A class to represent and perform operations on homogeneous
// coordinates, RGB colors, etc..
class Vector {
private:
    
    double data[4];

public:
    
    // Direct and copy constructors.
    Vector(double x=0.0, double y=0.0, double z=0.0, double w=0.0);
    Vector(std::vector<double> const &vec);
    Vector(Vector const &other);
    
    // Prints a vector like "(x,y,z,w)", without a newline.
    void print() const;
    
    // Indexing operators. Allows for getting and setting components via
    // array syntax.
    inline double  operator[](int index) const { return data[index]; }
    inline double& operator[](int index)       { return data[index]; }
    
    // Scalar multiplication of all components.
    // E.g.: `Vector b = a * k` where a is a Vector, and k is a double.
    Vector operator*(double value) const;
    
    // Scalar division of all components.
    // E.g.: `Vector b = a / k` where a is a Vector, and k is a double.
    Vector operator/(double value) const;

    // Component-wise multiplation of two vectors.
    // E.g.: `Vector c = a * b` performs `c[i] = a[i] * b[i]`.
    Vector operator*(Vector const &other) const; 
    
    // ======================================================================
    // Every method below this line ignores the 4th coordinate. Ergo, if your
    // W is not 1, then these will return incorrect homogeneous results.
    // ======================================================================

    // Length and squared length of the represented coordinate.
    double length() const;
    double length2() const;

    // Normalize represented coordinate (scale to length 1) in place; modifies the vector.
    void normalize();

    // Get a normalized copy of the represented coordinate.
    Vector normalized() const;
    
    // Addition and subtraction of represented coordinates.
    Vector operator+(Vector const &other) const;
    Vector operator-(Vector const &other) const;
    
    // Dot product of two vectors. Optional flag indicates if we should
    // be including the 4th coordinate.
    // E.g.: `Vector c = a.dot(b)` for `c = a . b`.
    double dot(Vector const &other, bool homogeneous=false) const;

    // Cross product of two vectors.
    // E.g.: `Vector c = a.cross(b)` for `c = a x b`. 
    Vector cross(Vector const &other, double w=1.0) const;

};


Vector operator*(double value, Vector const& vec);


// A class to represent 4x4 transformation matrices. They are stored in
// row-major order, so they are indexed by row and then column.
class Matrix {
private:
    
    Vector data[4];

public:
    
    // Copy constructor.
    Matrix(Matrix const &other);

    // Constructo from rows.
    Matrix(Vector const &a, Vector const &b, Vector const &c, Vector const &d);

    // Construct from 16 doubles.
    Matrix(double a=0, double b=0, double c=0, double d=0,
        double e=0, double f=0, double g=0, double h=0,
        double i=0, double j=0, double k=0, double l=0,
        double m=0, double n=0, double o=0, double p=1);
    
    // Build an identity matrix.
    static Matrix identity();

    // Build a matrix for rotating about an arbitrary axis.
    static Matrix rotation(double angle, Vector const &axis);
    
    // Build a matrix for translating.
    static Matrix translation(double x, double y, double z);

    // Build a matrix for scaling.
    static Matrix scale(double x, double y, double z);

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
