#ifndef SCENE_H
#define SCENE_H


#include <vector>
#include <map>
#include <string>

#include "object.hpp"
#include "light.hpp"
#include "camera.hpp"


// A class that stores all of the parameters, materials, and objects in a 
// scene that we want to render.
class Scene {
public:
    // Width/height resolution of output image, in pixels.
    int resolution[2]; 

    // The camera to use when rendering the scene.
    Camera camera;     
    
    // Mapping of material names to the materials themselves.
    std::map<std::string, Material> materials;

    // List of point lights.
    std::vector<PointLight> lights; 

    // List of pointers to Objects in the scene.
    // Note that Object is an abstract class, so these will actually be
    // Spheres, Planes, Meshes, etc.
    std::vector<Object*> objects;
};


#endif
