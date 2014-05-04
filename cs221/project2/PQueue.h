// This file contains the declaration of the class PriorityQueue, a
// templated priority queue abstract base class. PriorityQueue may be
// instantiatied with any Object and Compare such that: Object has a
// copy constructor, Compare has a default constructor and an
// operator() which takes two Object arguments and returns true iff
// the first is less than the second.
//

#ifndef _PQUEUE_H
#define _PQUEUE_H

// The PriorityQueue class, an abstract base class which describes the
// ADT for priority queues. It is templated by Object, the type of
// object to be contained in the priority queue, and Compare, the
// comparator to be used in determining relative priorities for the
// priority queue.
template <class Object, class Compare>
class PriorityQueue {
public:
  // There are pure virtual functions, so there needs to be a virtual
  // destructor.
  virtual ~PriorityQueue() { };

  // Const methods

  virtual bool isEmpty() const = 0;           // true iff pqueue is empty
  virtual const Object & findMin() const = 0; // returns const
        // reference to the minimum element of the pqueue. Should not be 
        // used to change the contents of the pqueue.
  
  virtual void insert(const Object & x) = 0;    // insert a copy of x 
                                                // into the pqueue
  virtual void deleteMin() = 0;                 // delete the minimum 
                                                // element of the pqueue.
  virtual void deleteMin(Object & minItem) = 0; // delete the minimum 
                                                // element of the pqueue, 
                                                // putting a copy of it 
                                                // into minItem.
  virtual void makeEmpty() = 0;                 // delete all elements 
                                                // of the pqueue
};



#endif
