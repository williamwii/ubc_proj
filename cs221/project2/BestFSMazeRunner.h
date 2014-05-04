#ifndef _BESTFSMAZERUNNER_H
#define _BESTFSMAZERUNNER_H

using namespace std;

#include "HeapPQueue.h"
#include "Compare.h"

class BestFSMazeRunner : public MazeRunner {

 protected:

  // helper functions
  MazeNode* pickNextNode(MazeNode *currNode);

  // static initialization (currently seeds random number generator).
  // executes once per run of the program, regardless of how many
  // times it's called.
  static void initClass();
  
 public:
 
  //constructor
  BestFSMazeRunner (PriorityQueue<MazeNode*,SquareMazeNodeCompare>* p);
 
  //destructor
  ~BestFSMazeRunner ();

  void solveMaze (Maze *maze,ostream& solutionOutput);
  
  private:
  
   //PriorityQueue used to store MazeNodes
   PriorityQueue<MazeNode*,SquareMazeNodeCompare>* pq;
   
};

#endif
