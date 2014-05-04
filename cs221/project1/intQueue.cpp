#include "Queue.h"
#include <iostream>

using namespace std;

Queue::Queue(){
	ary = new int[10];
	front = back = 0;
	ary_size = 10;
}

bool Queue::is_empty(){
	return front==back;
}

bool Queue::is_full(){
	return front == (back+1)%ary_size;
}

void Queue::expand_queue(){
	int* temp = ary;
	ary = new int[ary_size*2];
	for ( int i=0;i<ary_size;i++ )
		ary[i]=temp[front+i];
	front = 0;
	back = ary_size;
	delete temp;
	ary_size *= 2;
}

int Queue::first(){
	return ary[front];
}

int Queue::last(){
	return ary[back-1];
}

void Queue::enqueue(int item){
	if ( is_full() )
		expand_queue();
	ary[back] = item;
	back = (back+1)%ary_size;
}

int Queue::dequeue(){
	if ( is_empty() )
		return 0;
	int data = ary[front];
	front = (front+1)%ary_size;
	return data;
}

int main(){
	Queue q;
	q.dequeue();
	for( int i=1;i<=50;i++ ){
		q.enqueue(i);
		cout<<q.first()<<endl;
		cout<<q.last()<<endl;
	}
	
	for( int i=0;i<=50;i++ ){
		cout<<q.dequeue()<<endl;
	}
	

	return 0;
}
