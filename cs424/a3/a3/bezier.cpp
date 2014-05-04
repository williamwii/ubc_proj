#include "bezier.h"

#ifdef __APPLE__
#  include <GLUT/glut.h>
#else
#  include <GL/glut.h>
#endif

#include <cmath>
#include <cassert>

#include <iostream>
using namespace std;

Vector2D BezierCurve::evaluate(float t) const
{
    assert(t >= 0.0f && t <= 1.0f);
    assert(controlPoints.size() > 1);

    // Evaluate the Bezier curve at the given t parameter.
    // You may find the following functions useful:
    //  - BezierCurve::binomialCoefficient(m,i) computes "m choose i", 
    //      aka: (m over i)
    //  - std::pow(t,i) computes t raised to the power i

    //@@@@@
    // YOUR CODE HERE
    //@@@@@
    double x = 0;
    double y = 0;
    int m = (int)controlPoints.size() - 1; // m control points, degree of m-1
    for (int i=0;i<=m;i++) {
        double bx = controlPoints[i][0];
        double by = controlPoints[i][1];
        double B = binomialCoefficient(m, i) * pow(t, i) * pow(1-t, m-i);
        x += bx * B;
        y += by * B;
    }
    
    return Vector2D(x, y);
}


void BezierCurve::subdivide(BezierCurve& curve1, BezierCurve& curve2) const
{
    // Subdivide this Bezier curve into two curves.
    // Return the two smaller curves in curve1 and curve2.

    //@@@@@
    // YOUR CODE HERE
    //@@@@@

    size_t m = controlPoints.size();
    std::vector<Vector2D> points = controlPoints;
    for (size_t i=0;i<m-1;i++) {
        std::vector<Vector2D> newPoints;
        for (size_t j=0;j<=i;j++) {
            newPoints.push_back(points[j]);
        }
        
        size_t n = points.size();
        for (size_t j=i;j<n-i-1;j++) {
            Vector2D v1 = points[j];
            Vector2D v2 = points[j+1];
            
            Vector2D mid(0.5*(v1[0]+v2[0]), 0.5*(v1[1]+v2[1]));
            newPoints.push_back(mid);
        }
        
        for (size_t j=n-i-1;j<n;j++){
            newPoints.push_back(points[j]);
        }
        points = newPoints;
    }
    
    size_t s = points.size();
    std::vector<Vector2D> c1, c2;
    for (size_t i=0;i<s;i++) {
        if (floor(s/2.0) == i) {
            c2.push_back(points[i]);
        }
        
        if (i < s/2.0) {
            c1.push_back(points[i]);
        }
        else {
            c2.push_back(points[i]);
        }
    }
    
    curve1 = BezierCurve(c1);
    curve2 = BezierCurve(c2);
}


void BezierCurve::draw() const
{
    // Draw this Bezier curve.
    // Do this by evaluating the curve at some finite number of t-values,
    // and drawing line segments between those points.
    // You may use the BezierCurve::drawLine() function to do the actual
    // drawing of line segments.

    //@@@@@
    // YOUR CODE HERE
    //@@@@@
    assert(controlPoints.size() > 1);
    
    for (int i=0;i<100;i++) {
        double t0 = i/100.0;
        double t1 = (i+1)/100.0;
        
        Vector2D p0 = evaluate(t0);
        Vector2D p1 = evaluate(t1);
        
        drawLine(p0, p1);
    }
}


void BezierCurve::drawControlPolygon() const
{
    for (size_t i = 1; i < controlPoints.size(); ++i)
    {
        drawLine(controlPoints[i-1], controlPoints[i]);
    }
}


unsigned long BezierCurve::binomialCoefficient(int n, int k)
{
    // Compute nCk ("n choose k")
    // WARNING: Vulnerable to overflow when n is very large!

    assert(k >= 0);
    assert(n >= k);

    unsigned long result = 1;
    for (int i = 1; i <= k; ++i)
    {
        result *= n-(k-i);
        result /= i;
    }
    return result;
}


void BezierCurve::drawLine(const Vector2D& p1, const Vector2D& p2)
{
    glBegin(GL_LINES);
    glVertex2f(p1[0], p1[1]);
    glVertex2f(p2[0], p2[1]);
    glEnd();
}


void CurveManager::drawCurves() const
{
    if (points == NULL || points->size() < 2)
    {
        return;
    }

    if (curveMode == BASIC_MODE)
    {
        // Basic Mode
        //
        // Create a Bezier curve from the entire set of points,
        // and then simply draw it to the screen.
        
        BezierCurve curve(*points);
        curve.draw();

    }
    else if (curveMode == SUBDIVISION_MODE)
    {
        // Subdivision mode
        //
        // Create a Bezier curve from the entire set of points,
        // then subdivide it the number of times indicated by the 
        // subdivisionLevel variable.
        // The control polygons of the subdivided curves will converge 
        // to the actual bezier curve, so we only need to draw their 
        // control polygons.

        //@@@@@
        // YOUR CODE HERE
        //@@@@@
        BezierCurve curve(*points);
        std::vector<BezierCurve> curves;
        curves.push_back(curve);
        
        for (int i=0;i<subdivisionLevel;i++) {
            std::vector<BezierCurve> dividedCurves;
            for (size_t j=0;j<curves.size();j++) {
                BezierCurve c1, c2;
                BezierCurve c = curves[j];
                c.subdivide(c1, c2);
                dividedCurves.push_back(c1);
                dividedCurves.push_back(c2);
            }
            curves = dividedCurves;
        }
        
        for (size_t i=0;i<curves.size();i++) {
            curves[i].drawControlPolygon();
        }
    }
    else if (curveMode == PIECEWISE_MODE)
    {
        // Piecewise mode
        //
        // Create multiple Bezier curves out of the set of poitns, 
        // each of degree equal to the piecewiseDegree variable.
        // (The last curve may have degree less than piecewiseDegree.)

        if (continuityMode == C0_MODE)
        {
            // C0 continuity
            //
            // Each piecewise curve should be C0 continuous with adjacent
            // curves, meaning they should share an endpoint.

            //@@@@@
            // YOUR CODE HERE
            //@@@@@
            size_t pCnt = points->size();
            for (int i=0;i<pCnt-1;i+=piecewiseDegree) {
                std::vector<Vector2D> *ps = new std::vector<Vector2D>();
                for (int j=i;j<=i+piecewiseDegree && j<pCnt;j++) {
                    ps->push_back((*points)[j]);
                }
                BezierCurve(*ps).draw();
            }
            
        }
        else if (continuityMode == C1_MODE)
        {
            // C1 continuity
            //
            // Each piecewise curve should be C1 continuous with adjacent 
            // curves.  This means that not only must they share an endpoint,
            // they must also have the same tangent at that endpoint.
            // You will likely need to add additional control points to your 
            // Bezier curves in order to enforce the C1 property.
            // These additional control points do not need to show up onscreen.

            //@@@@@
            // YOUR CODE HERE
            //@@@@@
            size_t pCnt = points->size();
            int cCnt = -1;
            for (size_t i=0;i<pCnt;i+=piecewiseDegree) { cCnt++; }
            
            for (int i=0;i<pCnt-1;i+=piecewiseDegree) {
                std::vector<Vector2D> *ps = new std::vector<Vector2D>();
                for (int j=i;j<=i+piecewiseDegree && j<pCnt;j++) {
                    ps->push_back((*points)[j]);
                    
                    if (i != 0 && j == i) {
                        Vector2D v1 = (*points)[j-1];
                        Vector2D v2 = (j < pCnt-1) ? (*points)[j+1] : (*points)[j];
                        Vector2D m = (v2 - v1).normalize()/ 10.0;
                        
                        ps->push_back((*points)[j]+m);
                    }
                    
                    if (i < cCnt*piecewiseDegree && j == i+piecewiseDegree-1) {
                        Vector2D v1 = (*points)[j];
                        Vector2D v2 = (j < pCnt-2) ? (*points)[j+2] : (*points)[j+1];
                        Vector2D m = (v2 - v1).normalize()/ 10.0;
                        
                        ps->push_back((*points)[j+1]-m);
                    }
                }
                BezierCurve(*ps).draw();
            }
        }
    }
}


