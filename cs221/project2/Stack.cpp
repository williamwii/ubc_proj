#include "Stack.h"
#include "Maze.h"

/*
  A stack of MazeNode*
*/

//constructor
Stack::Stack(){
	top_of_stack=NULL;
}

//destructor
Stack::~Stack(){
	if ( is_empty() ) return;
	while ( top_of_stack->next!=NULL ){
		Node* temp = top_of_stack;
		top_of_stack = top_of_stack->next;
		delete temp;
	}
	delete top_of_stack;
}

//check to see if the stack is empty
bool Stack::is_empty(){
	return top_of_stack==NULL;
}

//push an item onto the top of the stack
void Stack::push(MazeNode* item){
	Node* temp = top_of_stack;
	top_of_stack = new Node(item);
	top_of_stack->next = temp;
}

//return the top item of the stack without removing it
MazeNode* Stack::top(){
	if ( is_empty() )
		return NULL;
	return top_of_stack->data;
}

//pop and return the top item of the stack
MazeNode* Stack::pop(){
	if ( is_empty() )
		return NULL;
	MazeNode* return_data = top_of_stack->data;
	Node* temp = top_of_stack;
	top_of_stack = top_of_stack->next;
	delete temp;
	return return_data;
}

//constructor of Node
Stack::Node::Node(MazeNode* i){
	data = i;
	next=NULL;
}
