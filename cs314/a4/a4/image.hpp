#ifndef IMAGE_H
#define IMAGE_H


#include <map>
#include <string>

#include "linalg.hpp"


// Class to represent images, capable of reading/writing in PPM format.
// All external coordinates will be expressed with the origin at the bottom
// left, even though most image encodings start at the top left.
class Image {
protected:

    int width, height;
    unsigned char *data;


public:
    
    // Constructor which does nothing; for calling readPPM afterwards.
    Image() : width(0), height(0), data(NULL) {}

    // Constructor which initializes the data buffer.
    Image(int width, int height) : width(width), height(height) {
        data = new unsigned char[3 * width * height];
    }

    // Destructor.
    ~Image() {
        delete[] data;
    }

    // Read PPM data from a given file.
    bool readPPM(std::string const &filename);

    // Write PPM data to the given file.
    bool writePPM(std::string const &filename) const;

    // Tell us if the Image is ready to be used.
    bool good() const { return data != NULL; }

    // Look up the color (as an RGB vector) at the given UV coordinates 
    // (from 0 to 1).
    Vector lookup(double u, double v) const;

    // Set a given pixel. Values should be from 0 to 1.
    void setPixel(int x, int y, Vector const& color);
    void setPixel(int x, int y, double gray);

};


#endif
