#ifndef _RANDOMMAZERUNNER_H
#define _RANDOMMAZERUNNER_H

using namespace std;

/*
  RandomMazeRunner.h

  Goes through the maze randomly, trying to get to the exit.

  221 STUDENTS: This and its corresponding .cpp file are VERY useful to
  study.  Your maze runners will make decisions based on a search
  algorithm rather than randomly, but they will be similar in overall
  structure and even in many of the details of the solveMaze function
  (such as setting the "parent" node of a node and reconstructing the
  solution path).



  We randomly walk around the maze, updating the parent fields as we
  go.  Once we reach the exit of the maze, we can use the parent
  fields to work backward to the entrance and discover the solution.
  (We leave it to you to determine how to print the solution out in
  forward order, but a stack would help a lot!)

*/

class RandomMazeRunner : public MazeRunner {
 protected:
  // helper functions
  MazeNode *pickNextNode(MazeNode *currNode);

  // static initialization (currently seeds random number generator).
  // executes once per run of the program, regardless of how many
  // times it's called.
  static void initClass();  
 public:
  RandomMazeRunner ();
  ~RandomMazeRunner ();

  void solveMaze (Maze *maze,ostream& solutionOutput);
};

#endif
