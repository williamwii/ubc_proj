
#include <iostream>
#include <cmath>

#include "linalg.hpp"


Vector::Vector(double x, double y, double z, double w) {
    data[0] = x;
    data[1] = y;
    data[2] = z;
    data[3] = w;
}


Vector::Vector(std::vector<double> const &vec) {
    for (size_t i = 0; i < 4; i++) {
        data[i] = vec.size() > i ? vec[i] : 0;
    }
}


Vector::Vector(Vector const &other) {
    for (int i = 0; i < 4; i++) {
        data[i] = other.data[i];
    }
}


void Vector::print() const {
    std::cout << '(';
    for (int i = 0; i < 4; i++) {
        std::cout << (i ? "," : "") << data[i];
    }
    std::cout << ')';
}


double Vector::length() const {
    double sum = 0;
    // Ignoring W.
    for (int i = 0; i < 3; i++) {
        sum += data[i] * data[i];
    }
    return sqrt(sum);
}


double Vector::length2() const {
    double sum = 0;
    // Ignoring W.
    for (int i = 0; i < 3; i++) {
        sum += data[i] * data[i];
    }
    return sum;
}


void Vector::normalize() {
    double len = length();
    // Ignoring W.
    for (int i = 0; i < 3; i++) {
        data[i] /= len;
    }
}


Vector Vector::normalized() const {
    Vector v(*this);
    v.normalize();
    return v;
}


Vector Vector::operator+(Vector const &other) const {
    Vector result;
    // Ignoring W.
    for (int i = 0; i < 3; i++) {
        result[i] = data[i] + other.data[i];
    }
    return result;
}


Vector Vector::operator-(Vector const &other) const {
    Vector result;
    // Ignoring W.
    for (int i = 0; i < 3; i++) {
        result[i] = data[i] - other.data[i];
    }
    return result;
}


Vector Vector::operator*(double value) const {
    Vector result;
    for (int i = 0; i < 4; i++) {
        result[i] = data[i] * value;
    }
    return result;
}


Vector operator*(double value, Vector const &vec) {
    return vec * value;
}


Vector Vector::operator/(double value) const {
    Vector result;
    for (int i = 0; i < 4; i++) {
        result[i] = data[i] / value;
    }
    return result;
}


Vector Vector::operator*(Vector const &other) const {
    Vector result;
    for (int i = 0; i < 4; i++) {
        result[i] = data[i] * other.data[i];
    }
    return result;
}


double Vector::dot(Vector const &other, bool homogeneous) const {
    double result = 0;
    // Ignoring W.
    for (int i = 0; i < (homogeneous ? 4 : 3); i++) {
        result += data[i] * other.data[i];
    }
    return result;
}


Vector Vector::cross(Vector const &other, double w) const {
    return Vector(
         data[1] * other.data[2] - other.data[1] * data[2],
        -data[0] * other.data[2] + other.data[0] * data[2],
         data[0] * other.data[1] - other.data[0] * data[1],
         w
    );
}


Matrix::Matrix(Matrix const &other) {
    for (int i = 0; i < 4; i++) {
        data[i] = other.data[i];
    }
}


Matrix::Matrix(Vector const &a, Vector const &b, Vector const &c, Vector const &d) {
    data[0] = a;
    data[1] = b;
    data[2] = c;
    data[3] = d;
}


Matrix::Matrix(double a, double b, double c, double d,
         double e, double f, double g, double h,
         double i, double j, double k, double l,
         double m, double n, double o, double p) {
     data[0] = Vector(a, b, c, d);
     data[1] = Vector(e, f, g, h);
     data[2] = Vector(i, j, k, l);
     data[3] = Vector(m, n, o, p);
}


Matrix Matrix::identity() {
    return Matrix(
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1
    );
}


Matrix Matrix::rotation(double angle, Vector const &axis) {
    
    double c_angle = cos(angle);
    double v_angle = 1.0 - c_angle;
    double s_angle = sin(angle);

    return Matrix(
        axis[0] * axis[0] * v_angle + c_angle,
        axis[1] * axis[0] * v_angle - axis[2] * s_angle,
        axis[2] * axis[0] * v_angle + axis[1] * s_angle,
        0,
        axis[0] * axis[1] * v_angle + axis[2] * s_angle,
        axis[1] * axis[1] * v_angle + c_angle,
        axis[2] * axis[1] * v_angle - axis[0] * s_angle,
        0,
        axis[0] * axis[2] * v_angle - axis[1] * s_angle,
        axis[1] * axis[2] * v_angle + axis[0] * s_angle,
        axis[2] * axis[2] * v_angle + c_angle,
        0,
        0, 0, 0, 1
    );
}


Matrix Matrix::translation(double x, double y, double z) {
    return Matrix(1, 0, 0, x,
                  0, 1, 0, y,
                  0, 0, 1, z,
                  0, 0, 0, 1);
}


Matrix Matrix::scale(double x, double y, double z) {
    return Matrix(x, 0, 0, 0,
                  0, y, 0, 0,
                  0, 0, z, 0,
                  0, 0, 0, 1);
}


void Matrix::print(bool pretty) const {
    std::cout << '(';
    for (int r = 0; r < 4; r++) {
        std::cout << (r ? ";" : "") << (r && pretty ? "\n " : "");
        for (int c = 0; c < 4; c++) {
            std::cout << (c ? "," : "") << data[r][c];
        }
    }
    std::cout << ')';
}


Matrix Matrix::transpose() const {
    Matrix result;
    for (int r = 0; r < 4; r++) {
        for (int c = 0; c < 4; c++) {
            result[r][c] = data[c][r];
        }
    }
    return result;
}


Vector Matrix::operator*(Vector const &other) const {
    return Vector(
        other.dot(data[0], true),
        other.dot(data[1], true),
        other.dot(data[2], true),
        other.dot(data[3], true)
    );
}


Matrix Matrix::operator*(Matrix const &other) const {
    Matrix transposed = other.transpose();
    Matrix result;
    for (int r = 0; r < 4; r++) {
        for (int c = 0; c < 4; c++) {
            result[r][c] = data[r].dot(transposed.data[c], true);
        }
    }
    return result;
}


void Matrix::operator*=(Matrix const &other) {
    Matrix copy = (*this) * other;
    for (int i = 0; i < 4; i++) {
        data[i] = copy.data[i];
    }
}


bool Matrix::invert(Matrix &inv) const {

    inv[0][0] = data[1][1] * data[2][2] * data[3][3] -
                data[1][1] * data[2][3] * data[3][2] -
                data[2][1] * data[1][2] * data[3][3] +
                data[2][1] * data[1][3] * data[3][2] +
                data[3][1] * data[1][2] * data[2][3] -
                data[3][1] * data[1][3] * data[2][2];
    inv[1][0] = -data[1][0] * data[2][2] * data[3][3] +
                data[1][0] * data[2][3] * data[3][2] +
                data[2][0] * data[1][2] * data[3][3] -
                data[2][0] * data[1][3] * data[3][2] -
                data[3][0] * data[1][2] * data[2][3] +
                data[3][0] * data[1][3] * data[2][2];
    inv[2][0] = data[1][0] * data[2][1] * data[3][3] -
                data[1][0] * data[2][3] * data[3][1] -
                data[2][0] * data[1][1] * data[3][3] +
                data[2][0] * data[1][3] * data[3][1] +
                data[3][0] * data[1][1] * data[2][3] -
                data[3][0] * data[1][3] * data[2][1];
    inv[3][0] = -data[1][0] * data[2][1] * data[3][2] +
                data[1][0] * data[2][2] * data[3][1] +
                data[2][0] * data[1][1] * data[3][2] -
                data[2][0] * data[1][2] * data[3][1] -
                data[3][0] * data[1][1] * data[2][2] +
                data[3][0] * data[1][2] * data[2][1];
    inv[0][1] = -data[0][1] * data[2][2] * data[3][3] +
                data[0][1] * data[2][3] * data[3][2] +
                data[2][1] * data[0][2] * data[3][3] -
                data[2][1] * data[0][3] * data[3][2] -
                data[3][1] * data[0][2] * data[2][3] +
                data[3][1] * data[0][3] * data[2][2];
    inv[1][1] = data[0][0] * data[2][2] * data[3][3] -
                data[0][0] * data[2][3] * data[3][2] -
                data[2][0] * data[0][2] * data[3][3] +
                data[2][0] * data[0][3] * data[3][2] +
                data[3][0] * data[0][2] * data[2][3] -
                data[3][0] * data[0][3] * data[2][2];
    inv[2][1] = -data[0][0] * data[2][1] * data[3][3] +
                data[0][0] * data[2][3] * data[3][1] +
                data[2][0] * data[0][1] * data[3][3] -
                data[2][0] * data[0][3] * data[3][1] -
                data[3][0] * data[0][1] * data[2][3] +
                data[3][0] * data[0][3] * data[2][1];
    inv[3][1] = data[0][0] * data[2][1] * data[3][2] -
                data[0][0] * data[2][2] * data[3][1] -
                data[2][0] * data[0][1] * data[3][2] +
                data[2][0] * data[0][2] * data[3][1] +
                data[3][0] * data[0][1] * data[2][2] -
                data[3][0] * data[0][2] * data[2][1];
    inv[0][2] = data[0][1] * data[1][2] * data[3][3] -
                data[0][1] * data[1][3] * data[3][2] -
                data[1][1] * data[0][2] * data[3][3] +
                data[1][1] * data[0][3] * data[3][2] +
                data[3][1] * data[0][2] * data[1][3] -
                data[3][1] * data[0][3] * data[1][2];
    inv[1][2] = -data[0][0] * data[1][2] * data[3][3] +
                data[0][0] * data[1][3] * data[3][2] +
                data[1][0] * data[0][2] * data[3][3] -
                data[1][0] * data[0][3] * data[3][2] -
                data[3][0] * data[0][2] * data[1][3] +
                data[3][0] * data[0][3] * data[1][2];
    inv[2][2] = data[0][0] * data[1][1] * data[3][3] -
                data[0][0] * data[1][3] * data[3][1] -
                data[1][0] * data[0][1] * data[3][3] +
                data[1][0] * data[0][3] * data[3][1] +
                data[3][0] * data[0][1] * data[1][3] -
                data[3][0] * data[0][3] * data[1][1];
    inv[3][2] = -data[0][0] * data[1][1] * data[3][2] +
                data[0][0] * data[1][2] * data[3][1] +
                data[1][0] * data[0][1] * data[3][2] -
                data[1][0] * data[0][2] * data[3][1] -
                data[3][0] * data[0][1] * data[1][2] +
                data[3][0] * data[0][2] * data[1][1];
    inv[0][3] = -data[0][1] * data[1][2] * data[2][3] +
                data[0][1] * data[1][3] * data[2][2] +
                data[1][1] * data[0][2] * data[2][3] -
                data[1][1] * data[0][3] * data[2][2] -
                data[2][1] * data[0][2] * data[1][3] +
                data[2][1] * data[0][3] * data[1][2];
    inv[1][3] = data[0][0] * data[1][2] * data[2][3] -
                data[0][0] * data[1][3] * data[2][2] -
                data[1][0] * data[0][2] * data[2][3] +
                data[1][0] * data[0][3] * data[2][2] +
                data[2][0] * data[0][2] * data[1][3] -
                data[2][0] * data[0][3] * data[1][2];
    inv[2][3] = -data[0][0] * data[1][1] * data[2][3] +
                data[0][0] * data[1][3] * data[2][1] +
                data[1][0] * data[0][1] * data[2][3] -
                data[1][0] * data[0][3] * data[2][1] -
                data[2][0] * data[0][1] * data[1][3] +
                data[2][0] * data[0][3] * data[1][1];
    inv[3][3] = data[0][0] * data[1][1] * data[2][2] -
                data[0][0] * data[1][2] * data[2][1] -
                data[1][0] * data[0][1] * data[2][2] +
                data[1][0] * data[0][2] * data[2][1] +
                data[2][0] * data[0][1] * data[1][2] -
                data[2][0] * data[0][2] * data[1][1];
               
    double det = data[0][0] * inv[0][0] +
                 data[0][1] * inv[1][0] +
                 data[0][2] * inv[2][0] +
                 data[0][3] * inv[3][0];

    if (det == 0) {
        return false;
    }

    det = 1.0 / det;

    for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 4; j++) {
            inv[i][j] *= det;
        }
    }

    return true;
}
        





