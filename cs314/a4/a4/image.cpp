#include <cstdio>
#include <iostream>
#include <fstream>

#include "image.hpp"




bool Image::readPPM(std::string const &filename) {

    // Open and assert the file is good.
    std::ifstream file(filename.c_str());
    if (!file.good()) {
        std::cerr << "unable to open PPM file \"" << filename << "\"" << std::endl;
        return false;
    }

    // Make sure this is a PPM file.
    unsigned char c1, c2;
    file >> c1;
    file >> c2;
    if (c1 != 'P' || c2 != '6') {
        std::cerr << "File \"" << filename << "\" is not a PPM file: " <<
            int(c1) << "," << int(c2) << std::endl;
        return false;
    }
    
    // Read image metadata.
    file >> width;
    file >> height;

    int maxValue;
    file >> maxValue;

    // Discard a whitespace character.
    file.ignore(1);
    
    // Discard old data, if any.
    delete [] data;
    
    // Read the data.
    int dataSize = width * height * 3;
    data = new unsigned char[dataSize];
    file.read((char*)data, dataSize);

    // Were we successful? Clear out the data array if we weren't.
    bool success = !file.fail();
    if (!success) {
        delete [] data;
    }

    // Clean up after ourselves.
    file.close();

    return success;
}


bool Image::writePPM(std::string const &filename) const {

    // Open and assert the file is good.
    std::ofstream file(filename.c_str(), std::ios::out | std::ios::binary);
    if (!file.good()) {
        std::cerr << "unable to open PPM file \"" << filename << "\"" << std::endl;
        return false;
    }

    // Write magic value and headers.
    file << "P6 " << width << " " << height << " 255\r";

    // Write the data.
    file.write((char*)data, width * height * 3);
    
    // Clean up.
    file.close();

    return file.good();
}


Vector Image::lookup(double u, double v) const {
    
    // Clamp coords from 0 to 1.
    u = std::min(1.0, std::max(0.0, u));
    v = std::min(1.0, std::max(0.0, v));

    // Calculate pixel coords.
    int x = static_cast<int>(u * (width - 1));
    int y = static_cast<int>(height - v * (height - 1) - 1);

    // Calculate offset into data buffer.
    unsigned char *p = data + 3 * (y * width + x);
    
    // Convert to doubles from 0 to 1.
    return Vector(
        double(p[0]) / 255.0,
        double(p[1]) / 255.0,
        double(p[2]) / 255.0
    );

}


void Image::setPixel(int x, int y, Vector const &color) {
    y = height - y - 1;
    int offset = 3 * (y * width + x);
    for (int i = 0; i < 3; i++) {
		data[offset + i] = static_cast<unsigned char>(
			std::min(255.0, std::max(0.0, 255.0*color[i])));
    }
}


void Image::setPixel(int x, int y, double gray) {
    setPixel(x, y, Vector(gray, gray, gray));
}


