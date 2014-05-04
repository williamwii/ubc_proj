#ifndef STACK_H_
#define STACK_H_

#include "Maze.h"

/*
  A stack of MazeNode*
*/

class Stack{

  public:

	Stack();
	~Stack();
	bool is_empty();
	void push(MazeNode* item);
	MazeNode* top();
	MazeNode* pop();

  private:
  
	struct Node{
		Node(MazeNode* i);
		MazeNode* data;
		Node* next;
	};

	Node* top_of_stack;

};

#endif

