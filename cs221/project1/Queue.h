#ifndef QUEUE_H_
#define QUEUE_H_

#include "Maze.h"

/*
  a Queue of MazeNode*
*/

class Queue{

	public:
	
		Queue();
		bool is_empty();
		bool is_full();
		void expand_queue();
		void enqueue(MazeNode* item);
		MazeNode* dequeue();

	private:
	
		MazeNode** ary;
		int ary_size;
		int front;
		int back;
		
};

#endif
