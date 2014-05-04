#ifndef _DFSMAZERUNNER_H
#define _DFSMAZERUNNER_H

#include "Stack.h"

using namespace std;

/*
  DFSMazeRunner
  Use depth first search to solve the maze
*/
class DFSMazeRunner : public MazeRunner {

 protected:

  // helper functions
  MazeNode *pickNextNode();

  // static initialization (currently seeds random number generator).
  // executes once per run of the program, regardless of how many
  // times it's called.
  static void initClass();  
 
public:

  DFSMazeRunner ();
  ~DFSMazeRunner ();

  void solveMaze (Maze *maze,ostream& solutionOutput);
  
 private:
	Stack* s;
	
};

#endif
