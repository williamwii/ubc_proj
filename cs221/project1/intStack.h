#ifndef STACK_H_
#define STACK_H_

class Stack{

  public:

	Stack();
	bool is_empty();
    	void push(int item);
	int top();
    	int pop();
    	int get_size();

  private:
  
	struct Node{
		Node(int i);
		int data;
		Node* next;
	};

	Node* top_of_stack;

};

#endif

