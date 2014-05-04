/* 221 STUDENTS: NO NEED TO UNDERSTAND ANYTHING IN THIS FILE. */

#include <iostream>
#include <cassert>
//#include <unistd>

#include "Maze.h"
#include "SquareMaze.h"
#include "VisualizeSquareMazeRunner.h"

extern "C" {
#include "GPKernel.h" // some graphics stuff borrowed from a previous course
}

const int VisualizeSquareMazeRunner::PixelsPerNode=40;
const int VisualizeSquareMazeRunner::BackgroundColor=MED_GRAY; // this shows up very little, as you can see
const int VisualizeSquareMazeRunner::WallColor=WHITE;
const int VisualizeSquareMazeRunner::VisitedColor=BLUE;
const int VisualizeSquareMazeRunner::VisitInProgressColor=YELLOW;
const int VisualizeSquareMazeRunner::NotVisitedColor=MED_GRAY;
const int VisualizeSquareMazeRunner::SolutionColor=PINK;

VisualizeSquareMazeRunner::VisualizeSquareMazeRunner (SquareMaze *_maze)
{
  pauseTimeInSec=0.05; // default

  maze=_maze;
  maze->setChangeListener(this);

  // set up our window
  int windowWidth,windowHeight;
  windowWidth=maze->getWidth()*PixelsPerNode;
  windowHeight=maze->getHeight()*PixelsPerNode;
  openPackage(windowWidth+1,windowHeight+1);

  drawInitialWindow();
  flush(); // flush graphics operations to screen

  pause();
}
VisualizeSquareMazeRunner::~VisualizeSquareMazeRunner ()
{
  closePackage();
}
void VisualizeSquareMazeRunner::drawInitialWindow (void)
{
  int windowWidth,windowHeight;
  windowWidth=maze->getWidth()*PixelsPerNode;
  windowHeight=maze->getHeight()*PixelsPerNode;

  setPenColor(BackgroundColor);
  drawRect(0,0,windowWidth,windowHeight,0);
  
  // draw boundary lines
  setPenColor(WallColor);
  drawLine(0,0,windowWidth,0,1);
  drawLine(windowWidth,0,windowWidth,windowHeight,1);
  drawLine(windowWidth,windowHeight,0,windowHeight,1);
  drawLine(0,windowHeight,0,0,1);

  // draw walls
  int x,y;
  for (y=0; y<maze->getHeight(); y++) {
    for (x=0; x<maze->getWidth(); x++) {
      SquareMaze::SquareMazeNode *node=maze->getNodeAt(x,y);
      if (!node->canGoLeft()) {
	drawLine(x*PixelsPerNode,y*PixelsPerNode,x*PixelsPerNode,(y+1)*PixelsPerNode,1);
      }
      if (!node->canGoUp()) {
	drawLine(x*PixelsPerNode,y*PixelsPerNode,(x+1)*PixelsPerNode,y*PixelsPerNode,1);
      }
    }
  }

  // draw squares
  for (y=0; y<maze->getHeight(); y++) {
    for (x=0; x<maze->getWidth(); x++) {
      SquareMaze::SquareMazeNode *node=maze->getNodeAt(x,y);
      drawNode(x,y,getColor(node->getVisitationState()));
    }
  }
}
int VisualizeSquareMazeRunner::getColor (MazeNode::VisitationState visitationState)
{
  switch (visitationState) {
  case MazeNode::Visited:
    return VisitedColor;
    break;
  case MazeNode::NotVisited:
    return NotVisitedColor;
    break;
  case MazeNode::VisitInProgress:
    return VisitInProgressColor;
    break;
  }
  assert(0); // illegal or unknown value
  return BackgroundColor; // keep compiler happy
}
void VisualizeSquareMazeRunner::drawNode (int x,int y,int color)
{
  setPenColor(color);
  drawRect(x*PixelsPerNode+1,y*PixelsPerNode+1,PixelsPerNode-1,PixelsPerNode-1,0);
}
void VisualizeSquareMazeRunner::visitationStateChanged (MazeNode *_changedNode)
{
  // well, we know this is really a SquareMazeNode, since that's all we work on
  SquareMaze::SquareMazeNode *node=(SquareMaze::SquareMazeNode *)_changedNode;

  drawNode(node->getX(),node->getY(),getColor(node->getVisitationState()));
  flush();
  pause();
}
void VisualizeSquareMazeRunner::setPauseTime (double _pauseTimeInSec)
{
  pauseTimeInSec=_pauseTimeInSec;
}
void VisualizeSquareMazeRunner::pause (void)
{
  usleep((unsigned long)(pauseTimeInSec*1000000.0));
}
void VisualizeSquareMazeRunner::startSolutionPath (void)
{
  drawInitialWindow(); // just in case we've already drawn a previous solution
}
void VisualizeSquareMazeRunner::addNextInSolutionPath (int x,int y)
{
  drawNode(x,y,SolutionColor);
}
void VisualizeSquareMazeRunner::doneSolutionPath(void)
{
  flush();
}
void VisualizeSquareMazeRunner::waitForMouseClick (void)
{
  eventRecord e;
  int type;
  do {
    type = getNextEvent(&e);
    if (type == EXPOSE) {
      flush();
    }
  } while (type != MOUSE);

}
