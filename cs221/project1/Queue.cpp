#include "Queue.h"
#include "Maze.h"

/*
  a Queue of MazeNode*
*/

//constructor
Queue::Queue(){
	ary = new MazeNode*[10];
	front = back = 0;
	ary_size = 10;
}

//check to see if the queue is empty
bool Queue::is_empty(){
	return front==back;
}

//check to see if the queue is full
bool Queue::is_full(){
	return front == (back+1)%ary_size;
}

//expand the queue to doubled size
void Queue::expand_queue(){
	MazeNode** temp = ary;
	ary = new MazeNode*[ary_size*2];
	for ( int i=0;i<ary_size;i++ )
		ary[i]=temp[front+i];
	front = 0;
	back = ary_size;
	delete temp;
	ary_size *= 2;
}

//enqueue an item to the back of the queue
void Queue::enqueue(MazeNode* item){

	//expand the queue if it is full
	if ( is_full() )
		expand_queue();

	ary[back] = item;
	back = (back+1)%ary_size;
}

//dequeue and return the first item in the queue
MazeNode* Queue::dequeue(){
	if ( is_empty() )
		return NULL;
	MazeNode* data = ary[front];
	front = (front+1)%ary_size;
	return data;
}

