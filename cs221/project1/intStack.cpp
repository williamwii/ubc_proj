#include "Stack.h"
#include <iostream>

using namespace std;

Stack::Stack(){
	top_of_stack=NULL;
}

bool Stack::is_empty(){
	return top_of_stack==NULL;
}
	
void Stack::push(int item){
	Node* temp = top_of_stack;
	top_of_stack = new Node(item);
	top_of_stack->next = temp;
}

int Stack::top(){
	if ( is_empty() )
		return NULL;
	return top_of_stack->data;
}

int Stack::pop(){
	if ( is_empty() )
		return NULL;
	int return_data = top_of_stack->data;
	Node* temp = top_of_stack;
	top_of_stack = top_of_stack->next;
	delete temp;
	return return_data;
}

Stack::Node::Node(int i){
	data = i;
	next=NULL;
}

/*int main(){
	Stack a;
	a.push(1);
	cout<<a.top()<<endl;
	a.pop();
	cout<<a.top()<<endl;
	a.push(2);
	a.push(3);
	a.push(4);
	cout<<a.top()<<endl;
	a.push(5);
	a.push(6);
	a.push(7);
	cout<<a.top()<<endl;
	a.pop();
	cout<<a.top()<<endl;
	a.pop();
	cout<<a.top()<<endl;
	a.pop();
	cout<<a.top()<<endl;
	a.pop();
	cout<<a.top()<<endl;
	a.pop();
	cout<<a.top()<<endl;
	a.pop();
	cout<<a.top()<<endl;
	a.pop();
	cout<<a.top()<<endl;
	a.pop();
	cout<<a.top()<<endl;
	return 0;

}*/
