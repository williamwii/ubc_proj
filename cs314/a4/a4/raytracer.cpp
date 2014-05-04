#include <cstdio>
#include <cstdlib>
#include <cfloat>
#include <cmath>
#include <iostream>
#include <vector>

#include "raytracer.hpp"
#include "utils.hpp"
#include "linalg.hpp"
#include "scene.hpp"
#include "image.hpp"


using namespace std;


void    
Raytracer::render(const char *filename, const char *depth_filename, 
                  Scene const &scene) {
    
    // Allocate the two images that will ultimately be saved.
    Image colorImage(scene.resolution[0], scene.resolution[1]);
    Image depthImage(scene.resolution[0], scene.resolution[1]);
    
    // Create the zBuffer.
    double *zBuffer = new double[scene.resolution[0] * scene.resolution[1]];
    for(int i = 0; i < scene.resolution[0] * scene.resolution[1]; i++) {
        zBuffer[i] = DBL_MAX;
    }

    Vector w = scene.camera.center - scene.camera.position;
    w.normalize();
    Vector u = w.cross(scene.camera.up);
    u.normalize();
    Vector v = u.cross(w);
    v.normalize();
    
    double fovy = scene.camera.fovy;
    double t = tan(fovy/2.0 * M_PI / 180.0) * scene.camera.zNear;
    double b = -t;
    double l = b * scene.camera.aspect;
    double r = t * scene.camera.aspect;
    Vector O = scene.camera.position + scene.camera.zNear * w + l * u + b * v;
    
    // Iterate over all the pixels in the image.
    for(int y = 0; y < scene.resolution[1]; y++) {
        for(int x = 0; x < scene.resolution[0]; x++) {

            // @@@@@ YOUR CODE HERE
            // Generate the appropriate ray for this pixel
            // @@@@@
            Vector Pij = O + x * ((r - l) / (scene.resolution[0] - 1)) * u + y * ((t - b) / (scene.resolution[1] - 1)) * v;
            Vector P = (Pij - scene.camera.position).normalized();
            
            Ray ray(scene.camera.position, P);

            // Initialize recursive ray depth.
            int rayDepth = 0;
           
            // Our recursive raytrace will compute the color and the z-depth
            Vector color;

            // This should be the maximum depth, corresponding to the far plane.
            // NOTE: This assumes the ray direction is unit-length and the
            // ray origin is at the camera position.
            double depth = scene.camera.zFar;

            // Calculate the pixel value by shooting the ray into the scene
            trace(ray, rayDepth, scene, color, depth);
            
            // Depth test
            if(depth >= scene.camera.zNear && depth <= scene.camera.zFar && 
                depth < zBuffer[x + y*scene.resolution[0]]) {
                zBuffer[x + y*scene.resolution[0]] = depth;

                // Set the image color (and depth)
                colorImage.setPixel(x, y, color);
                depthImage.setPixel(x, y, (depth-scene.camera.zNear) / 
                                        (scene.camera.zFar-scene.camera.zNear));
            }
        }
    }

    colorImage.writePPM(filename);
    depthImage.writePPM(depth_filename);

    delete[] zBuffer;
}


bool
Raytracer::trace(Ray const &ray, 
                 int &rayDepth,
                 Scene const &scene,
                 Vector &outColor, double &depth)
{
    // Increment the ray depth.
    rayDepth++;

    // @@@@@ YOUR CODE HERE
    // - iterate over all objects calling Object::intersect.
    // - don't accept intersections closer than given depth.
    // - call Raytracer::shade with the closest intersection.
    // - return true iff the ray hits an object.
    // @@@@@
    bool intersected = false;
    Intersection intersection;
    intersection.depth = depth;
    Material material;
    
    for (int i=0;i<scene.objects.size();i++) {
        Intersection intersect;
        if (scene.objects[i]->intersect(ray, intersect)) {
            // Find the intersection closest to camera
            if (intersect.depth < intersection.depth) {
                intersected = true;
                intersection = intersect;
                material = scene.objects[i]->material;
            }
        }
    }

    if (intersected) {
        depth = intersection.depth;
        outColor = shade(ray, rayDepth, intersection, material, scene);
        for (int i=0;i<3;i++) {
            if (outColor[i] < 0) {
                outColor[i] = 0;
            }
            else if (outColor[i] > 1) {
                outColor[i] = 1;
            }
        }
    }
    
    // Decrement the ray depth.
    rayDepth--;
    return intersected;
}


Vector
Raytracer::shade(Ray const &ray,
                 int &rayDepth,
                 Intersection const &intersection,
                 Material const &material,
                 Scene const &scene)
{
    // @@@@@ YOUR CODE HERE
    // - iterate over all lights, calculating ambient/diffuse/specular
    //   contribution
    // - use shadow rays to determine shadows
    // - integrate the contributions of each light
    // - include emission of the surface material
    // - call Raytracer::trace for reflection/refraction colors
    // Don't reflect/refract if maximum ray recursion depth has been reached!
    
    Vector colors(material.emission);
    Vector position = intersection.position;
    Vector normal = intersection.normal.normalized();
    Vector camera = scene.camera.position;
    Vector viewer = (ray.origin - position).normalized();
    
    for (int i=0;i<scene.lights.size();i++) {
        Vector color;
        PointLight light = scene.lights[i];
        Vector lightPos = light.position;
        Vector lightV = (lightPos - position).normalized();
        Vector negLightV(-lightV[0], -lightV[1], -lightV[2], 0);
        Vector lightSpec = (negLightV + 2 * normal.dot(lightV) * normal).normalized();
        
        double d = (lightPos - position).length();
        double fallOff = 1.0 / (light.attenuation[2] * pow(d, 2) + light.attenuation[1] * d + light.attenuation[0]);
        
        Vector ambient = material.ambient * light.ambient;
        Vector diffuse = normal.dot(lightV) >= 0.0 ? material.diffuse * (light.diffuse * fallOff) * normal.dot(lightV) : Vector(0, 0, 0);
//        Vector specular = viewer.dot(lightSpec) >= 0.0 ? material.specular * (light.specular * fallOff) * pow(viewer.dot(lightSpec), material.shininess) : Vector(0, 0, 0);
        // Half vector method
        Vector specular = normal.dot(((lightV+viewer)*0.5).normalized()) >= 0.0 ? material.specular * (light.specular * fallOff) * pow(normal.dot(((lightV+viewer)*0.5).normalized()), material.shininess) : Vector(0, 0, 0);
        
        color = ambient + diffuse + specular;

        if (material.shadow > 0 && rayDepth < MAX_RAY_RECURSION) {
            if (normal.dot(lightV) >= 0) {
                Vector shadowColor;
                Ray shadowRay(position + 0.0001 * normal, lightV);
                int shadowDepth = MAX_RAY_RECURSION;
                double depth = d;
                bool isShadow = trace(shadowRay, shadowDepth, scene, shadowColor, depth);
                
                if (isShadow) {
                    color = color * (1 - material.shadow);
                }
            }
        }
        
        colors = colors + color;
    }
    
    if (rayDepth < MAX_RAY_RECURSION) {
        Vector negViewer(-viewer[0], -viewer[1], -viewer[2], 0);
        if (material.reflect > 0) {
            Vector reflectDir = (negViewer + 2 * normal.dot(viewer) * normal).normalized();
            Ray reflectRay(position + 0.0001 * normal, reflectDir);
            
            Vector reflection;
            double depth = scene.camera.zFar;
            trace(reflectRay, rayDepth, scene, reflection, depth);
            colors = (1 - material.reflect) * colors + material.reflect * reflection;
        }
        
        // Refraction not working yet
        if (material.transparency < 1) {
            double factor = 1;
            if (viewer.dot(normal) < 0) {
                factor = 1.0 / material.refract;
            }
            else {
                factor = material.refract / 1.0;
            }
            Vector refractDir = (factor * normal.dot(negViewer) - sqrt(1 - pow(factor, 2) * (1 - pow(normal.dot(negViewer), 2)))) * normal - factor * negViewer;
            Ray refractRay(position - 0.0001 * normal, refractDir);
            
            Vector refraction;
            double refractDepth = (position - camera).length();
            trace(refractRay, rayDepth, scene, refraction, refractDepth);
            colors = material.transparency * colors + (1 - material.transparency) * refraction;
        }
    }
    
    return colors;
}


