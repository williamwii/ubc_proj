#ifndef POSE_H
#define POSE_H

#include <vector>


// A class to encapsulate a single pose.
struct Pose
{
    Pose(double x, double y, double z, std::vector<double> const &angles)
    : x_(x), y_(y), z_(z), angles_(angles)
    {}

    double x_, y_, z_;
    std::vector<double> angles_;
};


// Read a series of poses from a file and store them in the given vector.
void readPoseFile(char const *path, std::vector<Pose> &poses);


// Write the given pose to a file.
void writePoseFile(char const *path, Pose const &pose);


#endif
