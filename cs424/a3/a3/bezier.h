#ifndef BEZIER_H
#define BEZIER_H

#include "vector.h"
#include <vector>
#include <cstdlib>

// Enums indicating modes
enum CurveMode
{
    BASIC_MODE,
    SUBDIVISION_MODE,
    PIECEWISE_MODE
};

enum ContinuityMode
{
    C0_MODE,
    C1_MODE
};


//
// BezierCurve
//
// Class representing a Bezier curve.  The curve is constructed with a set
// of control points, and can then be evaluated at any t=[0,1].
//
class BezierCurve
{
public:
    // Default constructor
    BezierCurve() {}

    // Construct from a list of control points.
    BezierCurve(const std::vector<Vector2D>& points) : controlPoints(points) {}
   
    // Return the list of control points defining this curve.
    const std::vector<Vector2D>& getControlPoints() const 
    { return controlPoints; }

    // Change the control points that describe this curve.
    void setControlPoints(const std::vector<Vector2D>& points)
    { controlPoints = points; }
    
    // Add the given point to the end of the list of control points.
    void addControlPoint(const Vector2D& point) 
    { controlPoints.push_back(point); }

    // Remove all control points.
    void clearControlPoints() { controlPoints.clear(); }

    // Evaluate the curve at t = [0,1].
    Vector2D evaluate(float t) const;

    // Subdivide this curve into two Bezier curves.
    void subdivide(BezierCurve& curve1, BezierCurve& curve2) const;

    // Draw the curve as a sequence of line segments.
    void draw() const;

    // Draw the control polygon of this bezier curve.
    void drawControlPolygon() const;

private:
    std::vector<Vector2D> controlPoints;

    // Compute the binomial coefficient (n k)
    // also known as nCk or "n choose k"
    // WARNING: Vulnerable to overflow if n is very large!
    static unsigned long binomialCoefficient(int n, int k);

    // Draw a line between the two given points.
    static void drawLine(const Vector2D& p1, const Vector2D& p2);
};


//
// CurveManager
//
// A class for keeping track of Bezier curves and drawing them.
//
class CurveManager
{
public:
    // Constructor
    CurveManager() : points(NULL), curveMode(BASIC_MODE), 
                     continuityMode(C0_MODE), subdivisionLevel(0),
                     piecewiseDegree(1) {}
    
    // Mode modifiers
    void setCurveMode(CurveMode mode) { curveMode = mode; }
    void setContinuityMode(ContinuityMode mode) { continuityMode = mode; }
    
    // Modifiers for subdivision level and degree of piecewise curves.
    void increaseSubdivision() { subdivisionLevel++; }
    void decreaseSubdivision() { if (subdivisionLevel > 0) subdivisionLevel--; }
    void setDegree(int degree) { if (degree > 0) piecewiseDegree = degree; }
    
    // Update the set of points used to construct our curves.
    void updatePoints(const std::vector<Vector2D>* p) { points = p; }
    
    // Draw the curves formed by the current modes and set of points.
    void drawCurves() const;

private:
    const std::vector<Vector2D>* points; // Points used to construct curves
    CurveMode curveMode;                 // Current curve display mode
    ContinuityMode continuityMode;       // Continuity type in piecewise mode
    int subdivisionLevel;                // Number of subdivisions
    int piecewiseDegree;                 // Degree of piecewise curves
};

#endif
