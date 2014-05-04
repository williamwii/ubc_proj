// Implementation file for the unsorted linked list priority queue
// class (UnsLLPQueue).

// Since this is a templated type, we're likely to include the .cpp
// file in the header file, so protect the .cpp file from multiple
// includes.
#ifndef _UNSLLPQUEUE_CPP
#define _UNSLLPQUEUE_CPP

#include "UnsLLPQueue.h"
#include <cassert>      // for assert

// Constructor; makes the list empty.
template <class Object, class Compare>
UnsLLPQueue<Object, Compare>::UnsLLPQueue() : header(NULL)
{

}

// Checks if the list is empty (no nodes -> header points to NULL).
template <class Object, class Compare>
bool UnsLLPQueue<Object, Compare>::isEmpty() const
{
  return header == NULL;
}

// Find the minimum element in the queue.
template <class Object, class Compare>
const Object & UnsLLPQueue<Object, Compare>::findMin() const
{
  assert(!isEmpty());

  // findMinNode const version returns the node containing the minimum
  // element, just dereference to the contents for minimum object.
  return findMinNode()->contents;
}

// Find the node containing the minimum element in the priority queue.
// precondition: !isEmpty()
template <class Object, class Compare>
const class UnsLLPQueue<Object, Compare>::ListNode* UnsLLPQueue<Object, Compare>::findMinNode() const
{
  // Start by assuming header is best and search for better nodes.
  ListNode * best = header;
  ListNode * node = header->next;
  Compare comp;

  // Loop through each node until returning to the start of the list
  // (circular list means terminating condition is node == header).
  while (node != header) {

    // If the contents of this node are better than the best so far,
    // update the best.
    if (comp(node->contents, best->contents)) {
	 best = node;
    }
    node = node->next;
  }

  // Return the best node.
  return best;
}

// Find the node containing the minimum element in the priority queue.
// precondition: !isEmpty()
template <class Object, class Compare>
class UnsLLPQueue<Object,Compare>::ListNode * 
UnsLLPQueue<Object, Compare>::findMinNode()
{
  Compare comp;

  // Start by assuming header is best and search for better nodes.
  ListNode * best = header;
  ListNode * node = header->next;

  // Loop through each node until returning to the start of the list
  // (circular list means terminating condition is node == header).
  while (node != header) {
    // If the contents of this node are better than the best so far,
    // update the best.
    if (comp(node->contents, best->contents)) {
	 best = node;
    }
    node = node->next;
  }

  // Return the best node.
  return best;
}

// Insert a copy of x into the priority queue.
template <class Object, class Compare>
void UnsLLPQueue<Object, Compare>::insert(const Object & x)
{
  // Have to construct a new list if the header is NULL
  if (header == NULL) {
    // Construct a size one list with x in it.
    header = new ListNode(x);
    assert(header != NULL);

    // Circularly linked list, so link this node to itself.
    header->next = header->prev = header;
  }
  // Otherwise, just insert at the front of the list, making sure
  // that both next and previous links are copacetic (OK).
  else {
    // Put a node with x in it into the list (at the head).
    header = new ListNode(x, header, header->prev);
    assert(header != NULL);

    // Fix up surrounding links in the (circularly linked) list.
    header->next->prev = header;
    header->prev->next = header;
  }
}

// Remove the node containing the minimum element from the list and
// return it.
// precondtion: !isEmpty()
template <class Object, class Compare>
class UnsLLPQueue<Object,Compare>::ListNode * 
UnsLLPQueue<Object, Compare>::removeMinNode()
{
  // Start by finding the minimum node (the node to remove).
  ListNode * node = findMinNode();
  
  // Circular linkage makes deleting a node relatively easy.

  // First, move the header along if necessary:
  if (header == node) {
    header = header->next;
    // If we're still at this node, the list is length one, so it's
    // about to be erased, point header to NULL.
    if (header == node) {
	 header = NULL;
    }
  }
  
  // Next, fix the neighboring nodes' links. Works even if list length
  // is 1.
  node->next->prev = node->prev;
  node->prev->next = node->next;

  // Finally, return the node itself
  return node;
}

// Delete the minimum element from the list.
template <class Object, class Compare>
void UnsLLPQueue<Object, Compare>::deleteMin()
{
  assert(!isEmpty());

  // Find and remove the minimum node; then, free up the storage used
  // by it.
  delete removeMinNode();
}

// Delete the minimum element from the list, putting a copy in
// minItem.
template <class Object, class Compare>
void UnsLLPQueue<Object, Compare>::deleteMin(Object & minItem)
{
  assert(!isEmpty());

  // Find and remove the minimum node.
  ListNode * node = removeMinNode();

  // Put a copy of its contents in minItem.
  minItem = node->contents;

  // Finally, free up the storage used by the node.
  delete node;
}

// Remove all elements from the queue.
// This is a ridiculously inefficient implementation of makeEmpty!
template <class Object, class Compare>
void UnsLLPQueue<Object, Compare>::makeEmpty()
{
  // As long as any element remains. Delete the minimum element.
  while (!isEmpty()) {
    deleteMin();
  }
}

#endif
