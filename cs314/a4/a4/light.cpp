#include "light.hpp"

void PointLight::init(ParamList &params) {

    #define SET_VECTOR(_name) _name = params[#_name];
    SET_VECTOR(ambient)
    SET_VECTOR(diffuse)
    SET_VECTOR(specular)
    SET_VECTOR(attenuation)

}
