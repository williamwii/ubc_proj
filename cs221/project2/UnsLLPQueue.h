// This file contains the declaration of the concrete class
// UnsLLPQueue which extends the base class
// PriorityQueue. UnsLLPQueue adds no new
// requirements for Object and Compare.

#ifndef _UNSLLPQUEUE_H
#define _UNSLLPQUEUE_H

#include <iostream>
#include "PQueue.h"

using namespace std;

// The UnsLLPQueue class is an unsorted linked list implementation of
// priority queues. It extends the abstract class PriorityQueue
// keeping both template arguments.
template <class Object, class Compare>
class UnsLLPQueue : public PriorityQueue<Object, Compare> {
public:


  UnsLLPQueue();                      // Constructor
  ~UnsLLPQueue() { makeEmpty(); };    // Destructor

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

  // A helper class private to UnsLLPQueue. This is the node structure
  // of UnsLLPQueue's linked list. The linked list is a doubly linked,
  // circular list with no dummy header node. The actual header node
  // is pointed to by the member variable header.
  struct ListNode {
    ListNode(const Object & x, ListNode * n = NULL, ListNode * p = NULL)
	 : contents(x), next(n), prev(p) {}

    Object contents;
    ListNode * next;
    ListNode * prev;
  };

  // Head of the doubly linked circular list.
  ListNode * header;


  // Some helper functions for internal use

  ListNode * removeMinNode();           // Remove the minimum node in
                                        // the pqueue from the list and 
                                        // return a pointer to it.
  ListNode * findMinNode();             // Find the minimum node in the 
                                        // pqueue and return a pointer 
                                        // to it.
  const ListNode * findMinNode() const; // Same as above but for const 
                                        // PQueues.
};


// Suck in the implementation code to make templates work.
#include "UnsLLPQueue.cpp"


#endif
