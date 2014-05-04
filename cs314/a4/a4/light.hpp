#ifndef LIGHT_H
#define LIGHT_H

#include "linalg.hpp"
#include "lexer.hpp"


// A class to encapsulate all of the parameters of a light.
class PointLight {
public:
    
    // Constructors
    PointLight();
    PointLight(Vector const &position, ParamList &params) : 
        position(position)
        { init(params); }
   
    // Initialize the light's attributes from the given parameter list.
    void init(ParamList &params);
    
    Vector position; // Light location
    
    // Ambient/diffuse/specular light colors.
    Vector ambient;
    Vector diffuse;
    Vector specular;
   
    // Attenuation coefficients.
    // attenuation[0] = CONSTANT
    // attenuation[1] = LINEAR
    // attenuation[2] = QUADRATIC
    Vector attenuation;
};


#endif
