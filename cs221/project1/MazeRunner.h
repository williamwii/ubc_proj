#ifndef _MAZERUNNER_H
#define _MAZERUNNER_H

#include "Maze.h"
#include <iostream>

using namespace std;

/*
  MazeRunner.h

  Defines the abstract type MazeRunner - a class that knows how to solve
  a maze.  An abstract class is like a combination of an interface and a
  class in Java.  It contains some methods that are already implemented
  and some that (as in interfaces) are unimplemented, which subclasses
  are required to implement.

  221 STUDENTS: You will extend this class to create a concrete maze
  runner (actually, you'll do it twice, once for DFSMazeRunner and once
  for BFSMazeRunner).  You can follow the lead of RandomMazeRunner to
  see how.

*/

class MazeRunner {
 public:
  // virtual destructor, since we have virtual methods
  virtual ~MazeRunner();

  virtual void solveMaze (Maze *maze,ostream& solutionOutput) = 0;
};
#endif
