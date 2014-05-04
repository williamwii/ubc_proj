#ifndef _BFSMAZERUNNER_H
#define _BFSMAZERUNNER_H

#include "Queue.h"

using namespace std;

/*
  BFSMazeRunner
  Use breadth first search to solve the maze
*/

class BFSMazeRunner : public MazeRunner {
 protected:
  // helper functions
  MazeNode *pickNextNode( MazeNode* currNode );

  // static initialization (currently seeds random number generator).
  // executes once per run of the program, regardless of how many
  // times it's called.
  static void initClass();  
 public:
  BFSMazeRunner ();
  ~BFSMazeRunner ();

  void solveMaze (Maze *maze,ostream& solutionOutput);
  
 private:
	Queue* q;
	
};

#endif
