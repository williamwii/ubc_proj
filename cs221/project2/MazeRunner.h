#ifndef _MAZERUNNER_H
#define _MAZERUNNER_H

#include <iostream>
#include "Maze.h"

using namespace std;

/*
  MazeRunner.h

  Defines the abstract type MazeRunner - a class that knows how to solve
  a maze.  An abstract class is like a combination of an interface and a
  class in Java.  It contains some methods that are already implemented
  and some that (as in interfaces) are unimplemented, which subclasses
  are required to implement.

  221 STUDENTS: You will extend this class to create a concrete maze
  runner.

*/

class MazeRunner {
 public:
  // virtual destructor, since we have virtual methods
  virtual ~MazeRunner();

  virtual void solveMaze (Maze *maze,ostream& solutionOutput) = 0;
};
#endif
