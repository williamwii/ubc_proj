/* 221 STUDENTS: This implementation code is irrelevant to your work,
   although Maze.h will be very useful! */

#include <iostream>

#include "Maze.h"

/*
  MazeNodeIterator implementation
*/

MazeNodeIterator::~MazeNodeIterator()
{
}


/*
  MazeNode implementation
*/

MazeNode::MazeNode()
{
  parent=NULL;
  visitationState=NotVisited;
}

MazeNode::~MazeNode()
{
}

MazeNode *MazeNode::getPathParent (void)
{
  return parent;
}

void MazeNode::setPathParent (MazeNode *newParent)
{
  parent=newParent;
}

MazeNode::VisitationState MazeNode::getVisitationState (void)
{
  return visitationState;
}

void MazeNode::setVisitationState (VisitationState newVisitationState)
{
  visitationState=newVisitationState;
}

int MazeNode::getNumNeighbors (void)
{
  MazeNodeIterator *iter;
  int result;
  iter=getNeighbors();
  result=0;
  while (iter->hasNext()) {
    iter->next();
    result++;
  }

  return result;
}


/*
  Maze implementation
*/

Maze::Maze ()
{
  // initialize to NULL, to indicate no listener
  changeListener=NULL;
}

Maze::~Maze()
{
}

void Maze::setChangeListener (MazeChangeListener *newChangeListener)
{
  changeListener=newChangeListener;
}
void Maze::reinitializeMaze (void)
{
  MazeNodeIterator *iter;
  iter=getAllMazeNodes();
  while (iter->hasNext()) {
    MazeNode *node;
    node=iter->next();
    node->setVisitationState(MazeNode::NotVisited);
    node->setPathParent(NULL);
  }
  delete iter;
}

// MazeChangeListener implementation

MazeChangeListener::~MazeChangeListener()
{
}
void MazeChangeListener::visitationStateChanged (MazeNode *changedNode)
{
}

