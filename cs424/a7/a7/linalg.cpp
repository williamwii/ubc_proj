#include "linalg.h"
#include <iostream>
#include <cmath>

Matrix::Matrix() {
    data[0] = Vector(1,0,0,0);
    data[1] = Vector(0,1,0,0);
    data[2] = Vector(0,0,1,0);
    data[3] = Vector(0,0,0,1);
}


Matrix::Matrix(Matrix const &other) {
    for (int i = 0; i < 4; i++) {
        data[i] = other.data[i];
    }
}


Matrix::Matrix(Vector const &a, Vector const &b, Vector const &c) {
    data[0] = a;
    data[1] = b;
    data[2] = c;
    data[3] = Vector(0,0,0,1);
}


Matrix::Matrix(Vector const &a, Vector const &b, Vector const &c, Vector const &d) {
    data[0] = a;
    data[1] = b;
    data[2] = c;
    data[3] = d;
}


Matrix::Matrix(float a, float b, float c,
               float d, float e, float f,
               float g, float h, float i) {
    data[0] = Vector(a, b, c, 0);
    data[1] = Vector(d, e, f, 0);
    data[2] = Vector(g, h, i, 0);
    data[3] = Vector(0, 0, 0, 1);
}

Matrix::Matrix(float a, float b, float c, float d,
               float e, float f, float g, float h,
               float i, float j, float k, float l,
               float m, float n, float o, float p) {
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


Matrix Matrix::rotation(float angle, Vector const &axis) {
    
    float c_angle = cos(angle);
    float v_angle = 1.0f - c_angle;
    float s_angle = sin(angle);

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


Matrix Matrix::translation(float x, float y, float z) {
    return Matrix(1, 0, 0, x,
                  0, 1, 0, y,
                  0, 0, 1, z,
                  0, 0, 0, 1);
}


Matrix Matrix::scale(float x, float y, float z) {
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
        other.dotH(data[0]),
        other.dotH(data[1]),
        other.dotH(data[2]),
        other.dotH(data[3])
    );
}


Matrix Matrix::operator*(Matrix const &other) const {
    Matrix transposed = other.transpose();
    Matrix result;
    for (int r = 0; r < 4; r++) {
        for (int c = 0; c < 4; c++) {
            result[r][c] = data[r].dotH(transposed.data[c]);
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
               
    float det = data[0][0] * inv[0][0] +
                 data[0][1] * inv[1][0] +
                 data[0][2] * inv[2][0] +
                 data[0][3] * inv[3][0];

    if (det == 0) {
        return false;
    }

    det = 1.0f / det;

    for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 4; j++) {
            inv[i][j] *= det;
        }
    }

    return true;
}
        





