#ifndef MATERIAL_H
#define MATERIAL_H

#include <string>

#include "linalg.hpp"
#include "lexer.hpp"


// A class to encapsulate all parameters for a material.
class Material {

public:

    // Constructors
    Material() {};
    Material(ParamList &params) { init(params); }
    
    void init(ParamList &params);

    // Ambient/diffuse/specular/emissive colors.
    Vector ambient;
    Vector diffuse;
    Vector specular;
    Vector emission;

    // "Shininess" factor (specular exponent).
    double shininess;

    // Shadow coefficient, [0 -> no shadow, 1 -> black shadow]
    // everything in between is blended by that factor with the surface color
    double shadow;

    // Reflection coefficient [0 -> no reflection, 1 -> total reflection]
    // everything in between is blended by that factor with the surface color
    double reflect;
    
    double transparency;
    double refract;
};


#endif

