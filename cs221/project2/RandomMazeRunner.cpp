/* 221 STUDENTS: this is a useful file; see comments in RandomMazeRunner.h */

#include <iostream>
#include <cassert>
#include <cstdlib>
#include <ctime>

#include "Maze.h"
#include "MazeRunner.h"
#include "RandomMazeRunner.h"

RandomMazeRunner::RandomMazeRunner ()
{
  // Call the static class initializer. It will run only the first
  // time it's called.
  initClass();
}

RandomMazeRunner::~RandomMazeRunner ()
{
}

void RandomMazeRunner::solveMaze (Maze *maze,ostream& solutionOutput)
{
  MazeNode *currNode;

  currNode=maze->getStartMazeNode();
  while (!currNode->isExitNode()) {

    // for debugging
    // currNode->print(cerr);
    // cerr << "\n";

    // make a note that we have started the visit
    currNode->setVisitationState(MazeNode::VisitInProgress);

    // pick a next node
    MazeNode *nextNode;
    nextNode=pickNextNode(currNode);

    // make a note that this node is the parent of the next node (so
    // that we can later reconstruct the path we followed to the
    // solution).
    //
    // ONLY change the parent if it's not already set, however.
    // (Otherwise, we can end up with a parent loop!)
    if (nextNode->getPathParent() == NULL)
      nextNode->setPathParent(currNode);

    // make a node that we have completed the visit
    currNode->setVisitationState(MazeNode::Visited);

    // and go to the next node
    currNode=nextNode;
  }

  // yaay!!!  we solved it!

  // print out our solution
  solutionOutput << "RANDOM\n"; // print out the type of solution
  solutionOutput << "(Solution is backward.  Oops!\n"
		 << "221 students: see RandomMazeRunner.cpp for " 
		 << "some thoughts about that.\n";

  // We'll print out the solution path backward.  
  //
  // If only we had a STACK, we could push the nodes in the path onto
  // the stack and then when we popped them off, they'd be in the
  // right order to print forward!
  //
  // (And yes, that's a SECOND use for a stack besides using it to
  // manage nodes during DFS.)
  //
  // Alternatively, a simple recursive function can reverse the path
  // order for printing.  (How?  By relying on the function call stack
  // to reverse the order of the prints: given a node on the path,
  // first recursively print the node's parent (and the rest of the
  // path beyond the parent), then print the node itself.)

  // Note that currNode is already the exit node (at the point we left
  // the loop above).
  while (currNode != maze->getStartMazeNode()) {

    // print this node in the solution
    currNode->print(solutionOutput);
    solutionOutput << " "; // separate by spaces

    // get to the next node
    currNode = currNode->getPathParent();
  }
  // and the entry node
  currNode->print(solutionOutput);
  solutionOutput << "\n";
}

MazeNode *RandomMazeRunner::pickNextNode(MazeNode *currNode)
{
  MazeNode *nextNode;
  int numNeighbors,pickNum,r;
  numNeighbors=currNode->getNumNeighbors();
  r=rand();
  pickNum=(int)((double)(numNeighbors)*(double)(rand())/((double)(RAND_MAX)+1.0));
  assert(pickNum>=0 && pickNum<numNeighbors);

  // now get the node corresponding to that number
  MazeNodeIterator *iter;
  int i;
  iter=currNode->getNeighbors();
  for (i=0; i<=pickNum; i++) {
    assert(iter->hasNext());
    nextNode=iter->next();
  }
  delete iter;

  return nextNode;
}

void RandomMazeRunner::initClass()
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
