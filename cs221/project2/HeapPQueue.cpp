#ifndef _HEAPPQUEUE_CPP
#define _HEAPPQUEUE_CPP

#include "HeapPQueue.h"

/*
	A template HeapPQueue class that store Object and compared by Compare.
*/

// Default Constructor; binary heap
template <class Object, class Compare>
HeapPQueue<Object, Compare>::HeapPQueue(){
	arity = 2;
	arySize = 10;
	numElt = 0;
	heapAry = new Object[10];
}

// D-heap Constructor
template <class Object, class Compare>
HeapPQueue<Object, Compare>::HeapPQueue(int numChildren){
	arity = numChildren;
	arySize = 10;
	numElt = 0;
	heapAry = new Object[10];
}

//test to see if the PQueue is empty
template <class Object, class Compare>
bool HeapPQueue<Object, Compare>::isEmpty() const{
	return numElt==0;
}

//return the minimun item in the PQueue without deleting it
template <class Object, class Compare>
const Object & HeapPQueue<Object, Compare>::findMin() const{
	return heapAry[1];
}

//insert an item into the PQueue
template <class Object, class Compare>
void HeapPQueue<Object,Compare>::insert(const Object & x){
	if ( numElt==(arySize-1) ) expand();
	int newPos = percolateUp(++numElt,x);
	heapAry[newPos] = x;
}

//delete the minimun item from the PQueue
template <class Object, class Compare>
void HeapPQueue<Object,Compare>::deleteMin(){
	if ( !isEmpty() ){
		int newPos = percolateDown(1,heapAry[numElt]);
		heapAry[newPos] = heapAry[numElt];
		numElt--;
	}
}

//store the minimun item into minItem and delete it from the PQueue
template <class Object, class Compare>
void HeapPQueue<Object,Compare>::deleteMin(Object & minItem){
	if ( !isEmpty() ){
		minItem = heapAry[1];
		int newPos = percolateDown(1,heapAry[numElt]);
		heapAry[newPos] = heapAry[numElt];
		numElt--;
	}
}

//make an empty PQueue
template <class Object, class Compare>
void HeapPQueue<Object,Compare>::makeEmpty(){
	if ( !isEmpty() ){
		Object* newAry = new Object[arySize];
		Object* temp = heapAry;
		heapAry = newAry;
		delete [] temp;
		numElt = 0;
	}
}

//helper function
//used to expand the PQueue when it is full
template <class Object, class Compare>
void HeapPQueue<Object,Compare>::expand(){
	Object* newAry = new Object[arySize+10];
	for ( int i=1;i<arySize;i++ )
		newAry[i] = heapAry[i];
	arySize+=10;
	Object* temp = heapAry;
	heapAry = newAry;
	delete [] temp;
}

//helper function
//find the right hole for val and return the position in the arrray
template <class Object, class Compare>
int HeapPQueue<Object,Compare>::percolateDown(int hole, Object val){
	Compare comp;
	while ( (hole*arity-(arity-2))<=numElt ){
		int minPos = hole*arity-(arity-2);
		for ( int i=minPos+1;i<hole*arity+2;i++ ){
			if ( i<=numElt && comp(heapAry[i],heapAry[minPos]) )
				minPos = i;
		}
		if ( comp(heapAry[minPos],val) ){
			heapAry[hole] = heapAry[minPos];
			hole = minPos;
		}
		else
			break;
	}
	return hole;
}

//helper function
//find the right hole for val and return the position in the arrray
template <class Object, class Compare>
int HeapPQueue<Object,Compare>::percolateUp(int hole, Object val){
	Compare comp;
	while ( hole>1 && comp(val,heapAry[(hole+arity-2)/arity]) ){
		heapAry[hole] = heapAry[(hole+arity-2)/arity];
		hole = (hole+arity-2)/arity;
	}
	return hole;
}

#endif
