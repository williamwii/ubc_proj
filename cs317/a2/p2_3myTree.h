//
//  p2_3myTree.h
//  used for part 2 and 3
//
//  contains keys in the internal node,
//  and real data in leafs.
//  fixed height of 4.
//  specialized for IP forwarding
//  similar with p1myTree but with more components
//
//

#ifndef P2_3MYTREE_H
#define P2_3MYTREE_H

// structure used to store the NIC metric pairs for each subnet
typedef struct NIC_metric{
    int NIC;
    unsigned int metric;
}NIC_metric;

// the real data
// contains the prefix of an IP
// doubly linked list structure
typedef struct Leaf{
    int prefix;
	// the currently using NIC
    int NIC;
	// the shortest metric
    unsigned int metric;
	// a list of NIC_metric pair
    NIC_metric* neighbour_table;
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

// initialize a NIC_metric with the provided NIC and metric
void initialize_NIC_metric(NIC_metric*,int, unsigned int);

// initialize a leaf with the provided prefix and nic
void initialize_leaf(Leaf*, int, int, unsigned int, int);

// initialize a node with the provided index
void initialize_node(Node*, unsigned int);

// find a node with the provided unsigned integer
// return NULL if node is not found
Node* find_node(Node*, unsigned int);

// find the leaf with provided indices and prefix
Leaf* find_leaf(Node*,unsigned int, unsigned int, unsigned int, unsigned int, int);

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
// returning the root of the tree
Node* add_node(Node*, Node*);

// add the child to parent
// used by add_leaf
void add_child_to_node(Node*, Node*);

// add a leaf(ip) to the tree
// returning the root of the tree
Node* add_leaf(Node*,unsigned int,unsigned int,unsigned int,unsigned int,int,int, unsigned int, int);

// update the current NIC, NIC table , and metric if needed
// return 1 if changes happen, 0 otherwise
int update_leaf_nic(Leaf*,int, unsigned int, int);

#endif
