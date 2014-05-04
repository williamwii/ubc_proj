#ifndef _HEAPPQUEUE_H
#define _HEAPPQUEUE_H

#include <iostream>
#include "PQueue.h"

using namespace std;

/*
	A template HeapPQueue class that store Object and compared by Compare.
*/

template <class Object, class Compare>
class HeapPQueue : public PriorityQueue<Object, Compare> {
public:

  HeapPQueue();                              // Default Constructor
  HeapPQueue(const int numChildren);         // Constructor
  ~HeapPQueue() { delete [] heapAry; };      // Destructor

  // Const functions

  bool isEmpty() const;               // True iff the pqueue is empty
  
  const Object & findMin() const;     // Returns unchangeable reference to
                                      // minimum elment. Should _not_ be 
                                      // used to alter the minimum
                                      // element in any way.
  
  // Non-const functions
  
  void insert(const Object & x);      // Inserts a copy of x into the pqueue
  
  void deleteMin();                   // Deletes the minimum element from 
                                      // the pqueue 
  void deleteMin(Object & minItem);   // Deletes the minimum element
                                      // from the pqueue and puts a copy 
                                      // of it into minItem.
  void makeEmpty();                   // Deletes all elements from the
                                      // pqueue.

private:

  int arity;						  // Number of children each item have.
									  // Default as 2.
									  
  int arySize;						  // Size of array.
  
  int numElt;						  // Number of elements in heap.
  
  Object* heapAry;					  // The array used for heap.

  // Some helper functions for internal use

  void expand();					  // Expand the array when it is full.
  
  int percolateDown(int hole, Object val);// Percolate val down into the right hole.
  
  int percolateUp(int hole, Object val);  // Oercolate val up into the right hole.
  

};

#include "HeapPQueue.cpp"


#endif
