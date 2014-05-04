#include <iostream>
#include <cassert>
#include <cstdlib>
#include <ctime>

#include "Maze.h"
#include "MazeRunner.h"
#include "Stack.h"
#include "BFSMazeRunner.h"

/*
  BFSMazeRunner
  Use breadth first search to solve the maze
*/

//constructor
BFSMazeRunner::BFSMazeRunner ()
{
  // Call the static class initializer. It will run only the first
  // time it's called.
  initClass();
  q = new Queue();
}

//destructor
BFSMazeRunner::~BFSMazeRunner ()
{
}

//solve the maze provided
void BFSMazeRunner::solveMaze (Maze *maze,ostream& solutionOutput)
{
  MazeNode *currNode;
  currNode=maze->getStartMazeNode();
  q->enqueue( currNode );

  while (!currNode->isExitNode()) {
   
   if ( currNode->getVisitationState()==(MazeNode::NotVisited) ){
   	
	//set the currNode's state to be VisitInProgress
	currNode->setVisitationState(MazeNode::VisitInProgress);

    	// pick a next node
    	MazeNode *nextNode;
   	nextNode=pickNextNode( currNode );
		
	// make a node that we have completed the visit
   	currNode->setVisitationState(MazeNode::Visited);
	
	//go to the next node
	currNode = nextNode;
    }
    
    //if currNode has been visited,
    //set currNode to be next in queue
    else
	currNode=q->dequeue(); 
  }
  // yaay!!!  we solved it!

  // print out our solution
  solutionOutput << "(BFS)\n"; 

  //use a stack to get the path
  Stack* s = new Stack();
  while ( currNode != maze->getStartMazeNode() ){

	s->push( currNode );
	currNode = currNode->getPathParent();

  }
  //push the starting node onto the stack
  s->push( currNode );

  while ( !s->is_empty() ) {

    s->pop()->print(solutionOutput);
    solutionOutput << " "; // separate by spaces

  }
  solutionOutput << "\n";
  
}

//helper method
//pick the next node
MazeNode *BFSMazeRunner::pickNextNode( MazeNode* currNode )
{
  MazeNodeIterator *iter;
  
  iter=currNode->getNeighbors();
  while ( iter->hasNext() ){

	//enqueue all the neighbours which are not visited into the queue
	MazeNode* temp = iter->next();
	if ( temp->getVisitationState()==(MazeNode::NotVisited) ){
		q->enqueue( temp );
		if ( temp->getPathParent() == NULL )
			temp->setPathParent( currNode );
	}

  }
  delete iter;

  return q->dequeue();
}


void BFSMazeRunner::initClass()
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
