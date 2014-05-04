//
//  p1myTree.h
//
//  contains keys in the internal node,
//  and real data in leafs.
//  fixed height of 4.
//  specialized for IP forwarding
//
//  Created by William You on 11-10-23.
//

#ifndef P1MYTREE_H
#define P1MYTREE_H

// the real data
// contains the prefix of an IP
// doubly linked list structure
typedef struct Leaf{
    int prefix;
    int NIC;
    struct Leaf* prev;
    struct Leaf* next;
}Leaf;

// the index is used to search for an IP
typedef struct Node {
    unsigned int index;
    struct Node* next;
    struct Node* parent;
    struct Node* children;
    // Only the deepest level contains leafs
    Leaf* leafs;
}Node;

// count the non zero bits at the end of an unsigned integer
int nzc(unsigned int);

// initialize a leaf with the provided prefix and nic
void initialize_leaf(Leaf*, int, int);

// initialize a node with the provided index
void initialize_node(Node*, unsigned int);

// find a node with the provided unsigned integer
// return NULL if node is not found
Node* find_node(Node*, unsigned int);

// find the mininum match of the node's index
// in the linked list of nodes and i.
// start checking from the right end
int find_match(Node*,unsigned int);

// to find the ip address using longest prefix match
Leaf* find_IP_route(Node*, unsigned int, unsigned int, unsigned int, unsigned int);

// print the entire tree
void print_tree(Node*, char*);

// add the node to node_list
// used by add_leaf
void add_node(Node*, Node*);

// add the child to parent
// used by add_leaf
void add_child_to_node(Node*, Node*);

// add a leaf(ip) to the tree
void add_leaf(Node*,unsigned int,unsigned int,unsigned int,unsigned int,int,int);

#endif
