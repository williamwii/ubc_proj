#include <iostream>
#include <cassert>
#include <cstdlib>
#include <ctime>

#include "Maze.h"
#include "MazeRunner.h"
#include "DFSMazeRunner.h"

/*
  DFSMazeRunner
  Use depth first search to solve the maze
*/

//constructor
DFSMazeRunner::DFSMazeRunner ()
{
  // Call the static class initializer. It will run only the first
  // time it's called.
  initClass();
  s = new Stack();
}

//destructor
DFSMazeRunner::~DFSMazeRunner ()
{
}

//solve the maze provided
void DFSMazeRunner::solveMaze (Maze *maze,ostream& solutionOutput)
{
  MazeNode *currNode;
  currNode=maze->getStartMazeNode();
  s->push( currNode );

  while (!currNode->isExitNode()) {
   
    // pick a next node
    MazeNode *nextNode;
    nextNode=pickNextNode();
	
    // make a node that we have completed the visit
    currNode->setVisitationState(MazeNode::Visited);
	  
    // and go to the next node
    currNode=nextNode; 
	
	//set the visitation state to be VisitInProgress for the next node
	currNode->setVisitationState(MazeNode::VisitInProgress);
  }
  // yaay!!!  we solved it!

  // print out our solution
  solutionOutput << "(DFS)\n"; 

  //use another stack to reverse the original stack
  Stack* s2 = new Stack();
  while ( !s->is_empty() ){
	s2->push( s->pop() );
  }

  while ( !s2->is_empty() ) {
    s2->pop()->print(solutionOutput);
    solutionOutput << " "; // separate by spaces
  }
  solutionOutput << "\n";
}

//helper function
//to select the next node
MazeNode *DFSMazeRunner::pickNextNode()
{
  MazeNode *nextNode=NULL;
  MazeNodeIterator *iter;
  
  // loop through the currNode's neighbour until an unvisited one is found
  while ( nextNode==NULL ){

	MazeNode* currNode = s->top();
	iter=currNode->getNeighbors();

  //iterate through all the neighbours until have an unvisited one
	while ( iter->hasNext() ){

		MazeNode* temp = iter->next();
		if ( temp->getVisitationState()==(MazeNode::NotVisited) ){
			nextNode = temp;
			s->push( nextNode );
			if ( nextNode->getPathParent() == NULL )
				nextNode->setPathParent(currNode);
			break;
		}

	}
  //if all the neighbours have been visited, 
  //pop a MazeNode* from stack and check the next MazeNode*'s neighbours
	if ( nextNode==NULL )
		s->pop();
	delete iter;

  }

  return nextNode;

}


void DFSMazeRunner::initClass()
{
  static bool called = false;

  if (!called) {
    // Ensure that this function is called only once per program
    called = true;

#ifdef DEBUG
    // Initialize the randomizer to a constant for testing purposes.
    // Zero is a pretty, round constant.
    srand(0);
#else
    // Initialize the randomizer with time (which changes pretty frequently).
    srand(time(NULL));
#endif
  }
}
