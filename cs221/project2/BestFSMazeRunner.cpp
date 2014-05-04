#include <iostream>
#include <cassert>
#include <cstdlib>
#include <ctime>

#include "Maze.h"
#include "MazeRunner.h"
#include "Stack.h"
#include "BestFSMazeRunner.h"

/*
  BestFSMazeRunner
  Use best first search to solve the maze
*/

//constructor BestFSMazeRunner with the provided PriorityQueue
BestFSMazeRunner::BestFSMazeRunner (PriorityQueue<MazeNode*,SquareMazeNodeCompare>* p)
{
  // Call the static class initializer. It will run only the first
  // time it's called.
  initClass();
  pq = p;
}

//destructor
BestFSMazeRunner::~BestFSMazeRunner ()
{
  delete pq;
}

//solve the maze provided
void BestFSMazeRunner::solveMaze (Maze *maze,ostream& solutionOutput)
{
  MazeNode *currNode;
  currNode=maze->getStartMazeNode();
  pq->insert( currNode );

  while (!currNode->isExitNode()) {
   
   if ( currNode->getVisitationState()==(MazeNode::NotVisited) ){
   	
	//set the currNode's state to be VisitInProgress
	currNode->setVisitationState(MazeNode::VisitInProgress);

	MazeNode* nextNode;
	nextNode = pickNextNode( currNode );

	// make a node that we have completed the visit
   	currNode->setVisitationState(MazeNode::Visited);
	
	// go to the next node
	currNode = nextNode;
    }

    //if currNode has been visited,
    //set currNode to be next in pq
    else
		pq->deleteMin(currNode);
  }
  // set the exit node to be visited
  currNode->setVisitationState(MazeNode::Visited);

  // yaay!!!  we solved it!
  
  // print out our solution
  solutionOutput << "(Best First)\n"; 

  //use a stack to get the path reversed
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
  
  delete s;
  
}

//helper method
//pick the next node
MazeNode* BestFSMazeRunner::pickNextNode( MazeNode* currNode )
{
  MazeNodeIterator *iter;
  
  iter=currNode->getNeighbors();
  while ( iter->hasNext() ){

	MazeNode* temp = iter->next();
	if ( temp->getVisitationState()==(MazeNode::NotVisited) ){
		pq->insert( temp );
		if ( temp->getPathParent() == NULL )
			temp->setPathParent( currNode );
	}

  }
  delete iter;

  return pq->findMin();
}


void BestFSMazeRunner::initClass()
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
