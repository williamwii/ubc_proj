/* 221 STUDENTS: NO NEED TO UNDERSTAND ANYTHING IN THIS FILE. */

#include <iostream>
#include <cassert>
#include <cstring>
#include <cstdlib>

#include "Maze.h"
#include "SquareMaze.h"

SquareMaze::SquareMaze (istream& inData)
{
  // some initialization for sanity
  startNode=NULL;
  exitNode=NULL;

  // first figure out the width,height of the maze
  ParseSizeLine(inData);

  // allocate the nodes array, now that we know the dimensions
  nodes=new SquareMazeNode [width*height];

  // initialize every direction to "canGo" there.  When we get to walls, we'll
  // disable directions.
  int x,y;
  for (y=0; y<height; y++) {
    for (x=0; x<width; x++) {
      SquareMazeNode *node;
      node=getNode(x,y);
      node->x=x;
      node->y=y;
      node->maze=this;
    }
  }

  // now read in the file, disabling directions as appropriate.
  ReadActualMaze(inData);
  if (inData.eof()) {
    cerr << "SquareMaze: WARNING: maze seemed too small - it's probably in the wrong format\n";
  }
}
void SquareMaze::ParseSizeLine(istream& inData)
{
  char sizeLine[256]; // much larger than the largest legal line
  char *cursor;
  inData.getline(sizeLine,256);
  width=atoi(sizeLine);
  cursor=strchr(sizeLine,' ');
  if (cursor==NULL) {
    cerr << "SquareMaze: illegal width/height line.  Must have space separator.\n";
    exit(1);
  }
  height=atoi(cursor+1);
  if (width<=0 || height<=0) {
    cerr << "SquareMaze: illegal width/height line.  Width and Height must both be greater than 0.\n";
    exit(1);
  }
}
void SquareMaze::ReadActualMaze(istream& inData)
{
  int x,y;
  int col,row; // in file
  bool onWallX,onWallY;
  char ch; // current character from file
  bool isBoundary;

  for (row=0; row<2*height+1; row++) {
    for (col=0; col<2*width+1; col++) {

      inData.get(ch);

      // for convenience, the square we're on, 
      // or the first square down and to the right, if we're on a wall
      x=col/2;
      y=row/2;
                        
      onWallX=(col%2)==0;
      onWallY=(row%2)==0;

      // check for boundary of the maze
      isBoundary=false;
      if (!onWallX || !onWallY) {
	// check for boundaries, but don't do it on diagonal walls,
	// simply because in those cases we could end up deferencing an
	// illegal part of the array (e.g. where col==2*width, and row==0, then we do bad stuff for the row==0 case
	if (col==0) {
	  getNode(x,y)->canGoDirs[Left]=false;
	  isBoundary=true;
	}
	if (row==0) {
	  getNode(x,y)->canGoDirs[Up]=false;
	  isBoundary=true;
	}
	if (col==2*width) {
	  getNode(x-1,y)->canGoDirs[Right]=false;
	  isBoundary=true;
	}
	if (row==2*height) {
	  getNode(x,y-1)->canGoDirs[Down]=false;
	  isBoundary=true;
	}
      }

      if (isBoundary) {
	// we've already dealt with the boundaries of the maze
      }
      else {
	// there are 4 cases for if we're on a wall or not
	if (onWallX) {
	  if (onWallY) {
	    // we're on a diagonal wall, which is meaningless
	    // so, ignore this
	  }
	  else {
	    // we're on an X-wall
	    switch (ch) {
	    case ' ':
	      // no wall, so there's nothing to do
	      break;
	    default:
	      // just act like it's a wall
	    case '|':
	      // an X-wall is here
	      getNode(x-1,y)->canGoDirs[Right]=false;
	    getNode(x,y)->canGoDirs[Left]=false;
	    break;
	    }
	  }
	}
	else {
	  if (onWallY) {
	    // we're on a Y-wall
	    switch (ch) {
	    case ' ':
	      // no wall, so there's nothing to do
	      break;
	    default:
	      // just act like it's a wall
	    case '-':
	      // an Y-wall is here
	      getNode(x,y-1)->canGoDirs[Down]=false;
	    getNode(x,y)->canGoDirs[Up]=false;
	    break;
	    }
	  }
	  else {
	    // we're in a square (not on a wall)
	    // check if it's the start or exit square
	    switch (ch) {
	    case '*':
	      if (startNode!=NULL) {
		cerr << "SquareMaze WARNING: multiple start nodes.  Ignoring the earlier one\n";
	      }
	      startNode=getNode(x,y);
	      break;
	    case 'X':
	      if (exitNode!=NULL) {
		cerr << "SquareMaze WARNING: multiple exit nodes.  Ignoring the earlier one\n";
	      }
	      exitNode=getNode(x,y);
	      break;
	    case ' ':
	      // this is the expected case, so do nothing
	      break;
	    default:
	      cerr << "SquareMaze WARNING: character in cell of maze must be *,X or space.  Character is at x,y coord (" << x << "," << y << ")\n";
	      break;
	    }
	  }
	}
      }
    }

    // read extra return ('\n')
    inData.get(ch);
    if (ch=='\r') {
      // gracefully handle binary mode files on Windows
      inData.get(ch);
    }
  }

  if (startNode==NULL || exitNode==NULL) {
    cerr << "SquareMaze WARNING: maze lacks start node or exit node.\n";
  }
}
SquareMaze::~SquareMaze()
{
  delete [] nodes;
}
void SquareMaze::DebugDump (ostream& out)
{
  int x,y;
  for (y=0; y<height; y++) {
    for (x=0; x<width; x++) {

      SquareMazeNode *node=getNode(x,y);
      out << "(" << x << "," << y << "): ";

      int i;
      char dirName[4][8]={"Left","Right","Up","Down"};
      for (i=0; i<4; i++) {
	if (node->canGoDirs[i]) {
	  out << dirName[i] << ", ";
	}
      }

      if (node==startNode) {
	out << " (START)";
      }
      if (node==exitNode) {
	out << " (EXIT)";
      }

      out << "\n";
    }
  }
}
SquareMaze::SquareMazeNode *SquareMaze::getNode (int x,int y)
{
  assert(x>=0 && x<width && y>=0 && y<height);

  return &(nodes[y*width+x]);
}
MazeNode *SquareMaze::getStartMazeNode (void)
{
  return startNode;
}
MazeNodeIterator *SquareMaze::getAllMazeNodes (void)
{
  return new AllNodesIterator(this);
}
int SquareMaze::getNumNodes (void)
{
  return width*height;
}
int SquareMaze::getMaxNeighborsForNode (void)
{
  return 4;
}



// SquareMaze::AllNodesIterator 


SquareMaze::AllNodesIterator::AllNodesIterator (SquareMaze *_maze)
{
  maze=_maze;
  x=0;
  y=0;
}
SquareMaze::AllNodesIterator::~AllNodesIterator ()
{
}
bool SquareMaze::AllNodesIterator::hasNext (void)
{
  return y<maze->height;
}
MazeNode * SquareMaze::AllNodesIterator::next (void)
{
  MazeNode *result;
  result=maze->getNode(x,y);
  x++;
  if (x==maze->width) {
    x=0;
    y++;
  }
  return result;
}
int SquareMaze::getWidth (void)
{
  return width;
}
int SquareMaze::getHeight (void)
{
  return height;
}
SquareMaze::SquareMazeNode *SquareMaze::getNodeAt (int x,int y)
{
  return getNode(x,y);
}

// SquareMaze::SquareMazeNode

SquareMaze::SquareMazeNode::SquareMazeNode ()
{
  int i;
  for (i=0; i<4; i++) {
    canGoDirs[i]=true;
  }

  // SquareMaze will set it for us: maze, x, y
  maze=NULL; // make sure there's a problem if it doesn't
}
SquareMaze::SquareMazeNode::~SquareMazeNode ()
{
}
bool SquareMaze::SquareMazeNode::isExitNode (void)
{
  return this==maze->exitNode;
}
MazeNodeIterator *SquareMaze::SquareMazeNode::getNeighbors (void)
{
  return new NeighborIterator(this);
}
void SquareMaze::SquareMazeNode::setVisitationState (VisitationState newVisitationState)
{
  MazeNode::setVisitationState(newVisitationState);

  // notify listener, if any
  if (maze->changeListener!=NULL) {
    maze->changeListener->visitationStateChanged(this);
  }
}
void SquareMaze::SquareMazeNode::print (ostream& out)
{
  out << "(" << x << "," << y << ")";
}
bool SquareMaze::SquareMazeNode::readXY (istream& inData,int *x,int *y)
{
  char ch;
  // skip leading spaces, which are legit
  while (1) {
    inData.get(ch);
    if (ch!=' ') {
      break;
    }
  }
  if (ch!='(') {
    // not valid - probably a newline
    return false;
  }

  inData >> *x;
  if (inData.fail()) {
    inData.clear();
    cout << "WARNING: Solution seems to have a bad format\n";
    // a bit of random, defensive programming
    inData.ignore(100000,'\n'); // make sure we don't just keep reading the same thing
    return false;
  }
  inData.get(ch);
  inData >> *y;
  inData.get(ch);

  return true;
}
int SquareMaze::SquareMazeNode::getX (void)
{
  return x;
}
int SquareMaze::SquareMazeNode::getY (void)
{
  return y;
}
bool SquareMaze::SquareMazeNode::canGoLeft (void)
{
  return canGoDirs[Left];
}
bool SquareMaze::SquareMazeNode::canGoRight (void)
{
  return canGoDirs[Right];
}
bool SquareMaze::SquareMazeNode::canGoUp (void)
{
  return canGoDirs[Up];
}
bool SquareMaze::SquareMazeNode::canGoDown (void)
{
  return canGoDirs[Down];
}

// SquareMaze::SquareMazeNode::NeighborIterator

SquareMaze::SquareMazeNode::NeighborIterator::NeighborIterator (SquareMazeNode *_node)
{
  node=_node;
  currDir=0;

  ComputeNextNode();
}
SquareMaze::SquareMazeNode::NeighborIterator::~NeighborIterator ()
{
}
bool SquareMaze::SquareMazeNode::NeighborIterator::hasNext (void)
{
  return nextNode!=NULL;
}
MazeNode * SquareMaze::SquareMazeNode::NeighborIterator::next (void)
{
  SquareMazeNode *result;
  result=nextNode;
  ComputeNextNode();

  return result;
}
void SquareMaze::SquareMazeNode::NeighborIterator::ComputeNextNode(void)
{
  while (currDir<4) {
    if (node->canGoDirs[currDir]) {
      int x,y;
      x=node->x;
      y=node->y;
      switch (currDir) {
      case Left:
	x--;
	break;
      case Right:
	x++;
	break;
      case Up:
	y--;
	break;
      case Down:
	y++;
	break;
      }
      nextNode=node->maze->getNode(x,y);
      currDir++;
      return;
    }
    currDir++;
  }
  nextNode=NULL;
}
