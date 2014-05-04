#include <cmath>
#include <vector>

#ifdef __APPLE__
#  include <GLUT/glut.h>
#else
#  include <GL/glut.h>
#endif

#include "utils.hpp"
#include "robot.hpp"

#define JOINTS 12

// Constants for indexes into the angles std::vector.
enum JointIndex
{
	BODY_ANGLE = 0, HEAD_ANGLE, LEFT_SHOULDER, LEFT_ELBOW,
	RIGHT_SHOULDER, RIGHT_ELBOW, LEFT_HIP, LEFT_KNEE, LEFT_ANKLE,
	RIGHT_HIP, RIGHT_KNEE, RIGHT_ANKLE, BODY_Y, HEAD_Y, LEFT_HAND, RIGHT_HAND,
    LEFT_SHOULDER_Y, RIGHT_SHOULDER_Y, LEFT_HIP_Y, RIGHT_HIP_Y,
    // @@@@@ If you wanted more angles in a pose, you would add them here.
	NUM_ANGLES // This one is not a joint, but a count of how many joints there are.
};


void Robot::setPoses(std::vector<Pose> const &poses) {
    poses_ = poses;
}


Pose Robot::getPose() const {
    
    double x=0, y=0, z=0;
    std::vector<double> angles(NUM_ANGLES);
    
    // If we don't have any poses then return an empty pose.
    if (!poses_.size()) {
        return Pose(x, y, z, angles);
    }
    
    // If we only have one pose, then return it.
    if (poses_.size() == 1) {
        return poses_[0];
    }

    // Return the current pose.
    // NOTE: poseIndex is double, NOT an int!!! Fractional values represent
    // interpolation between neighbouring poses.  Eg: 2.5 would be halfway 
    // between the 3rd and 4th pose.

//    return poses_[int(floor(poseIndex_))];

    // @@@@@ Replace the above return statement with your own code to implement
    // @@@@@ linear interpolation between poses.

    Pose pose = poses_[int(floor(poseIndex_))];
    Pose nextPose = poses_[ceil(poseIndex_) >= poses_.size() ? 0 : int(ceil(poseIndex_))];
    double percentage = poseIndex_ - floor(poseIndex_);
    
    Pose newPose(x, y, z, angles);
    newPose.x_ = (nextPose.x_ - pose.x_) * percentage + pose.x_;
    newPose.y_ = (nextPose.y_ - pose.y_) * percentage + pose.y_;
    newPose.z_ = (nextPose.z_ - pose.z_) * percentage + pose.z_;
    std::vector<double> *poseAngles = new std::vector<double>();
    for (int i=0;i<pose.angles_.size();i++) {
        double angle = (nextPose.angles_[i] - pose.angles_[i]) * percentage + pose.angles_[i];
        poseAngles->push_back(angle);
    }
    if (pose.angles_.size()>12)
    {
        printf("here");
    }
    newPose.angles_ = *poseAngles;
    
    return newPose;
}


void Robot::setPoseIndex(double poseIndex) {
    poseIndex_ = std::max(0.0, std::min(double(poses_.size()-1), poseIndex));
}
    
    
void Robot::incrementPoseIndex(double timeChange) {
    poseIndex_ = fmod(poseIndex_ + timeChange, poses_.size());
    
    // Need to handle negative deltas.
    if (poseIndex_ < 0) {
        poseIndex_ += poses_.size();
    }
}


void Robot::moveToPrevPose() {
    if (poses_.size()) {
        poseIndex_ = int(poseIndex_ + poses_.size() - 1) % poses_.size();
    }
}


void Robot::moveToNextPose() {
    if (poses_.size()) {
        poseIndex_ = int(poseIndex_ + 1) % poses_.size();
    }
}


void Robot::drawHands(Pose pose) const {
    
    glPushMatrix();
    glTranslated(0, -0.125, 0);
    
    // Left hand
    glPushMatrix();
    glTranslated(0, 0, 1.05);
    // Shoulder
    glPushMatrix();
    glTranslated(0, 1, 0);
    glRotated(pose.angles_[LEFT_SHOULDER], 0, 0, 1);
    if (pose.angles_.size() > JOINTS) {
        glRotated(pose.angles_[LEFT_SHOULDER_Y], 0, 1, 0);
    }
    glTranslated(0, -0.5, 0);
    drawCuboid(0.6, 1, 0.6);
    // Elbow
    glPushMatrix();
    glTranslated(0, -0.5, 0);
    glRotated(pose.angles_[LEFT_ELBOW], 0, 0, 1);
    glTranslated(0, -0.5, 0);
    drawCuboid(0.5, 1, 0.5);
    // Hand
    glPushMatrix();
    glTranslated(0, -0.5, 0);
    if (pose.angles_.size() > JOINTS) {
        glRotated(pose.angles_[LEFT_HAND], 1, 0, 0);
    }
    glTranslated(0, -0.25, 0);
    drawCuboid(0.4, 0.5, 0.3);
    glPopMatrix();
    glPopMatrix();
    glPopMatrix();
    glPopMatrix();
    
    // Right Hand
    glPushMatrix();
    glTranslated(0, 0, -1.05);
    // Shoulder
    glPushMatrix();
    glTranslated(0, 1, 0);
    glRotated(pose.angles_[RIGHT_SHOULDER], 0, 0, 1);
    if (pose.angles_.size() > JOINTS) {
        glRotated(pose.angles_[RIGHT_SHOULDER_Y], 0, 1, 0);
    }
    glTranslated(0, -0.5, 0);
    drawCuboid(0.6, 1, 0.6);
    // Elbow
    glPushMatrix();
    glTranslated(0, -0.5, 0);
    glRotated(pose.angles_[RIGHT_ELBOW], 0, 0, 1);
    glTranslated(0, -0.5, 0);
    drawCuboid(0.5, 1, 0.5);
    // Hand
    glPushMatrix();
    glTranslated(0, -0.5, 0);
    if (pose.angles_.size() > JOINTS) {
        glRotated(pose.angles_[RIGHT_HAND], 1, 0, 0);
    }
    glTranslated(0, -0.25, 0);
    drawCuboid(0.4, 0.5, 0.3);
    glPopMatrix();
    glPopMatrix();
    glPopMatrix();
    glPopMatrix();
    // Pop hands
    glPopMatrix();
}

void Robot::drawLegs(Pose pose) const {
    // Legs
    glPushMatrix();
    glTranslated(0, -1, 0);
    
    // Left leg
    glPushMatrix();
    glTranslated(0, 0, 0.25+0.125);
    // Hip
    glPushMatrix();
    if (pose.angles_.size() > JOINTS) {
        glRotated(pose.angles_[LEFT_HIP_Y], 0, 1, 0);
    }
    glRotated(pose.angles_[LEFT_HIP], 0, 0, 1);
    glTranslated(0, -0.5, 0);
    drawCuboid(0.5, 1, 0.5);
    // Knee
    glPushMatrix();
    glTranslated(0, -0.5, 0);
    glRotated(pose.angles_[LEFT_KNEE], 0, 0, 1);
    glTranslated(0, -0.375, 0);
    drawCuboid(0.5, 0.75, 0.5);
    // Ankle
    glPushMatrix();
    glTranslated(-0.125, -0.5, 0);
    glRotated(pose.angles_[LEFT_ANKLE], 0, 0, 1);
    glTranslated(0.25, 0, 0);
    drawCuboid(0.75, 0.25, 0.5);
    glPopMatrix();
    glPopMatrix();
    glPopMatrix();
    glPopMatrix();
    
    // Right leg
    glPushMatrix();
    glTranslated(0, 0, -0.25-0.125);
    // Hip
    glPushMatrix();
    if (pose.angles_.size() > JOINTS) {
        glRotated(pose.angles_[RIGHT_HIP_Y], 0, 1, 0);
    }
    glRotated(pose.angles_[RIGHT_HIP], 0, 0, 1);
    glTranslated(0, -0.5, 0);
    drawCuboid(0.5, 1, 0.5);
    // Knee
    glPushMatrix();
    glTranslated(0, -0.5, 0);
    glRotated(pose.angles_[RIGHT_KNEE], 0, 0, 1);
    glTranslated(0, -0.375, 0);
    drawCuboid(0.5, 0.75, 0.5);
    // Ankle
    glPushMatrix();
    glTranslated(-0.125, -0.5, 0);
    glRotated(pose.angles_[RIGHT_ANKLE], 0, 0, 1);
    glTranslated(0.25, 0, 0);
    drawCuboid(0.75, 0.25, 0.5);
    glPopMatrix();
    glPopMatrix();
    glPopMatrix();
    glPopMatrix();
    // Pop legs
    glPopMatrix();
}

void Robot::draw() const {
    
    // Retrieve the current (interpolated) pose of the robot.
    Pose pose = getPose();
    
    // You can set the robot to be whatever color you like.
    // By default it is white.
    glColor4d(1, 1, 1, 1);

    // @@@@@ Add your own code to draw the robot.
    // @@@@@ You might want to split this into multiple functions that draw
    // @@@@@ different parts of the robot.
    
    glPushMatrix();
    glTranslated(pose.x_, pose.y_, pose.z_);
    glRotated(pose.angles_[BODY_ANGLE], 0, 0, 1);

    // Body
    if (pose.angles_.size() > JOINTS) {
        glRotated(pose.angles_[BODY_Y], 0, 1, 0);
    }
    drawCuboid(1, 2, 1.5);

    // Head
    glPushMatrix();
    glTranslated(0, 1, 0);
    if (pose.angles_.size() > JOINTS) {
        glRotated(pose.angles_[HEAD_Y], 0, 1, 0);
    }
    glRotated(pose.angles_[HEAD_ANGLE], 0, 0, 1);
    glTranslated(0, 0.5, 0);
    drawCuboid(0.75, 1, 1);
    
    
    // Antennae
    glPushMatrix();
    glTranslated(0.425, 0.5, 0);
    // Left
    glPushMatrix();
    glTranslated(0, 0, 0.25);
    glRotated(45, 1, 0, 0);
    drawCuboid(0.1, 0.5, 0.1);
    glPopMatrix();
    // Right
    glPushMatrix();
    glTranslated(0, 0, -0.25);
    glRotated(-45, 1, 0, 0);
    drawCuboid(0.1, 0.5, 0.1);
    glPopMatrix();
    glPopMatrix();
    glPopMatrix();
    
    drawHands(pose);
    drawLegs(pose);
    
    glPopMatrix();
}

