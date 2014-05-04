// This is the skeleton code for the comparator class.
// You will need to change the return value of the 
// operator() function to reflect the heuristic specified
// in the handout.  Feel free to add private methods to
// this class if it will help you perform the calculation.
// You will need to use the _new_ functions listed below:
//   SquareMaze::SquareMazeNode::getMazeWidth()
//   SquareMaze::SquareMazeNode::getMazeHeight()
//   SquareMaze::SquareMazeNode::getExitX()
//   SquareMaze::SquareMazeNode::getExitY()

#ifndef _COMPARE_H
#define _COMPARE_H

#include "Maze.h"
#include "SquareMaze.h"

class SquareMazeNodeCompare {

 public:

  // Return true iff larg is closer to exit than rarg.
  bool operator() (const MazeNode * larg, 
                   const MazeNode * rarg) const
    {
      SquareMaze::SquareMazeNode * lsmn = (SquareMaze::SquareMazeNode *)larg;
      SquareMaze::SquareMazeNode * rsmn = (SquareMaze::SquareMazeNode *)rarg;
	  
	  //exit
	  int exitX = lsmn->getExitX();
	  int exitY = lsmn->getExitY();
	  
	  //maze
	  int mazeHeight = lsmn->getMazeHeight();
	  int mazeWidth = lsmn->getMazeWidth();
	  
	  //left
	  int lNodeX = lsmn->getX();
	  int lNodeY = lsmn->getY();
	  
	  int temp1 = lNodeX - exitX;
	  if ( temp1<0 ) temp1 = -temp1;
	  int temp2 = lNodeY - exitY;
	  if ( temp2<0 ) temp2 = -temp2;
	  
	  int lVal = (temp1+temp2)*mazeHeight*mazeWidth + lNodeX*mazeHeight + lNodeY;
	  
	  //right
	  int rNodeX = rsmn->getX();
	  int rNodeY = rsmn->getY();
	  
	  int temp3 = rNodeX - exitX;
	  if ( temp3<0 ) temp3 = -temp3;
	  int temp4 = rNodeY - exitY;
	  if ( temp4<0 ) temp4 = -temp4;
	  
	  int rVal = (temp3+temp4)*mazeHeight*mazeWidth + rNodeX*mazeHeight + rNodeY;

      return lVal<rVal;
    }

};

#endif
