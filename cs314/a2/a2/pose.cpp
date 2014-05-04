#include <cstdlib>
#include <fstream>
#include <iostream>
#include <sstream>

#include "pose.hpp"


void readPoseFile(char const *path, std::vector<Pose> &poses) {
    
    // Open the file.
    std::ifstream fh;
    fh.open(path);    
    
    while (fh.good()) {
        
        double x, y, z;
        std::vector<double> angles;
        
        // Read a line from the file.
        std::string line;
        getline(fh, line);
        
        // Skip empty lines, or comments.
        if (!line.size() || line[0] == '#') {
            continue;
        }
        
        // Push the line into a stringstream so we can easily pull out doubles.
        std::stringstream ss;
        ss << line;        
        
        // Read translation coordinates.
        ss >> x;
        ss >> y;
        ss >> z;
        
        // Read angles while there is still stuff to read.
        while (ss.good()) {
            double a;
            ss >> a;
            angles.push_back(a);
        }
        
        poses.push_back(Pose(x, y, z, angles));
    }
}


void writePoseFile(char const *path, Pose const &pose) {
    
    // Try to open the file.
    std::ofstream outFile;
    outFile.open(path);
    if (!outFile.good()) {
        std::cerr << "could not open '" << path << "' for writing" << std::endl;
        return;
    }
    
    // Write position.
    outFile << pose.x_ << '\t';
    outFile << pose.y_ << '\t';
    outFile << pose.z_ << '\t';
    
    // Write angles.
    for (unsigned int i = 0; i < pose.angles_.size(); i++) {
        outFile << pose.angles_[i] << '\t';
    }
    
    // All done!
    outFile.close();
}

