#ifndef ROBOT_H
#define ROBOT_H

#include <vector>
#include "pose.hpp"


class Robot {
    
    // Series of poses that we can take.
    std::vector<Pose> poses_;
    
    // Current index into the poses vector. Fractional values represent
    // interpolation between neighboring poses.
    double poseIndex_; 

private:
    // Draw hands of robot
    void drawHands(Pose pose) const;

    // Draw legs of robot
    void drawLegs(Pose pose) const;
    
public:
    
    Robot() : poseIndex_(0) {}

    // Sets the series of poses the robot will take when animated.
    void setPoses(std::vector<Pose> const &poses);
    
    // Returns current (interpolated) pose.
    Pose getPose() const;

    // Draw the robot!
    void draw() const;
    
    // Set the poseIndex to the given value.
    void setPoseIndex(double poseIndex);
    
    // Increment the poseIndex by the given value, wrapping around if needed.
    void incrementPoseIndex(double timeChange);
    
    // Set the poseIndex to the previous/next whole keyframe.
    void moveToPrevPose();
    void moveToNextPose();
     
};


#endif
