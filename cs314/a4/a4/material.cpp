#include "material.hpp"


void Material::init(ParamList &params) {

    #define SET_VECTOR(_name) _name = params[#_name];
    SET_VECTOR(ambient)
    SET_VECTOR(diffuse)
    SET_VECTOR(specular)
    SET_VECTOR(emission)

    #define SET_FLOAT(_name) _name = params[#_name].size() ? params[#_name][0] : 0;
    SET_FLOAT(shininess)
    SET_FLOAT(shadow)
    SET_FLOAT(reflect)
    
    #define SET_ONES(_name) _name = params[#_name].size() ? params[#_name][0] : 1;
    SET_ONES(transparency)
    SET_ONES(refract)

}
