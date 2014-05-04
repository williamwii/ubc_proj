//
//  p2_3myTree.c
//  used for part 2 and 3
//
//  contains keys in the internal node,
//  and real data in leafs.
//  fixed height of 4.
//  specialized for IP forwarding
//  similar with p1myTree but with more components
//
//

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "p2_3myTree.h"

// if the metric is higher than 1000, ceil to 1000
#define UNREACHABLE 1000

// count the non zero bits at the end of an unsigned integer
int nzc(unsigned int integer){
    int counter = 0;
    while (integer!=0) {
        integer = integer>>1;
        counter++;
    }
    return counter;
}

// initialize the NIC metric pair with the provided nic and metric
void initialize_NIC_metric(NIC_metric* nm, int nic, unsigned int metric){
    nm->NIC = nic;
    nm->metric = metric;
}

// initialize a leaf with the provided prefix and nic
void initialize_leaf(Leaf* leaf, int p, int nic, unsigned int metric, int num_nic){
    leaf->prefix = p;
    leaf->NIC = nic;
    leaf->metric = metric + 1;
	// initialize the table
    leaf->neighbour_table = (NIC_metric*) (malloc(num_nic*sizeof(NIC_metric)));
    int i = 0;
    while (i<num_nic){
        if (i==nic)
            initialize_NIC_metric(&leaf->neighbour_table[i], i, metric);
        else
            initialize_NIC_metric(&leaf->neighbour_table[i], i, 1000);
        i++;
    }
    leaf->prev = NULL;
    leaf->next = NULL;
}

// initialize a node with the provided index
void initialize_node(Node* node, unsigned int index){
    node->index = index;
    node->next = NULL;
    node->parent = NULL;
    node->children = NULL;
    node->leafs = NULL;
}

// find a node with the provided unsigned integer
// return NULL if node is not found
Node* find_node(Node* node, unsigned int a){
    Node* target = NULL;
    Node* temp = node;
    
    while (temp!=NULL) {
        if (temp->index==a){
            target = temp;
            break;
        }
        temp = temp->next;
    }
    
    return target;
}

// find the leaf with provided indices and prefix
Leaf* find_leaf(Node* root,unsigned int a, unsigned int b, unsigned int c, unsigned int d, int prefix){
    Node* target_node = find_node(root, a);
    Leaf* target_leaf = NULL;
    
    if (target_node!=NULL){
        target_node = find_node(target_node->children, b);
        if (target_node!=NULL){
            target_node = find_node(target_node->children, c);
            if (target_node!=NULL){
                target_node = find_node(target_node->children, d);
                if (target_node!=NULL){
                    Leaf* temp = target_node->leafs;
                    while (temp!=NULL) {
                        if (temp->prefix==prefix){
                            target_leaf = temp;
                            break;
                        }
                        temp = temp->next;
                    }
                }
            }
        }
    }
    
    return target_leaf;
}

// find the mininum match of the node's index
// in the linked list of nodes and i.
// start checking from the right end
int find_match(Node* node, unsigned int i){
    Node* temp = node;
    unsigned int checker = 10;
    int min = 9;
    while (temp!=NULL){
        if (min==0)
            break;
        checker = (temp->index)^i;
        checker = nzc(checker);
        if (checker < min)
            min = checker;
        temp = temp->next;
    }
    
    return min;
}

// to find the ip address using longest prefix match
Leaf* find_IP_route(Node* root, unsigned int a, unsigned int b, unsigned int c, unsigned int d){
    Node* target = find_node(root, a);
    Node* temp = NULL;
    Leaf* leaf;
    Leaf* target_leaf = NULL;
    int i, min, temp_match;
    int match = 0;
    if (target==NULL){
        min = find_match(root, a);
        for (i=min;i<=8;i++){
            temp_match = match;
            temp_match += (8 - i);
            unsigned int shift_a = (a>>i)<<i;
            
			// if node is not found in first level,
			// try to find the ip with longest prefix match in first level
			// with the the lower level index zeros
            temp = find_node(root, shift_a);
            if (temp!=NULL){
                temp = find_node(temp->children, 0);
                if (temp!=NULL){
                    temp = find_node(temp->children, 0);
                    if (temp!=NULL){
                        temp = find_node(temp->children, 0);
                        if (temp!=NULL){
                            leaf = temp->leafs;
                            while (leaf!=NULL) {
                                if (temp_match>=leaf->prefix)
                                    target_leaf = leaf;
                                leaf = leaf->next;
                            }
                            if (target_leaf!=NULL)
                                break;
                        }
                    }
                }
            }
        }
        return target_leaf;
    }
    
    else{
		// first level passes
        match += 8;
        
        temp = find_node(target->children, b);
        if (temp==NULL){
            min = find_match(target->children, b);
            
			// search for the longest prefix match,
			// starting from longest bit length
            for (i=min;i<=8;i++){
                temp_match = match;
                temp_match += (8 - i);
                unsigned int shift_b = (b>>i)<<i;
                
                temp = find_node(target->children, shift_b);
                if (temp!=NULL){
                    temp = find_node(temp->children, 0);
                    if (temp!=NULL){
                        temp = find_node(temp->children, 0);
                        if (temp!=NULL){
                            Leaf* leaf = temp->leafs;
                            while (leaf!=NULL) {
                                if (temp_match>=leaf->prefix)
                                    target_leaf = leaf;
                                leaf = leaf->next;
                            }
                            if (target_leaf!=NULL)
                                break;
                        }
                    }
                }
            }
			// if ip still not found,
            // try to find it in one level higher
            if (target_leaf==NULL){
                for (i=1;i<=8;i++){
                    temp_match = match - i;
                    unsigned int shift_a = (a>>i)<<i;
                    
                    temp = find_node(root, shift_a);
                    if (temp!=NULL){
                        temp = find_node(temp->children, 0);
                        if (temp!=NULL){
                            temp = find_node(temp->children, 0);
                            if (temp!=NULL){
                                temp = find_node(temp->children, 0);
                                if (temp!=NULL){
                                    leaf = temp->leafs;
                                    while (leaf!=NULL) {
                                        if (temp_match>=leaf->prefix)
                                            target_leaf = leaf;
                                        leaf = leaf->next;
                                    }
                                    if (target_leaf!=NULL)
                                        break;
                                }
                            }
                        }
                    }
                }
            }
            
            return target_leaf;
        }
        
        else{
			// second level passes
            match += 8;
            target = temp;
            
            temp = find_node(target->children, c);
            if (temp==NULL){
                min = find_match(target->children, c);
				// search for the longest prefix match,
				// starting from longest bit matching length
                for (i=min;i<=8;i++){
                    temp_match = match;
                    temp_match += (8 - i);
                    unsigned int shift_c = (c>>i)<<i;
                    
                    temp = find_node(target->children, shift_c);
                    if (temp!=NULL){
                        temp = find_node(temp->children, 0);
                        if (temp!=NULL){
                            leaf = temp->leafs;
                            while (leaf!=NULL) {
                                if (temp_match>=leaf->prefix)
                                    target_leaf = leaf;
                                leaf = leaf->next;
                            }
                            if (target_leaf!=NULL)
                                break;
                        }
                    }
                }
                // if ip not found, go back one level higher
                if (target_leaf==NULL){
                    for (i=1;i<=8;i++){
                        temp_match = match - i;
                        unsigned int shift_b = (b>>i)<<i;
                        
                        temp = find_node(target->parent->children, shift_b);
                        if (temp!=NULL){
                            temp = find_node(temp->children, 0);
                            if (temp!=NULL){
                                temp = find_node(temp->children, 0);
                                if (temp!=NULL){
                                    Leaf* leaf = temp->leafs;
                                    while (leaf!=NULL) {
                                        if (temp_match>=leaf->prefix)
                                            target_leaf = leaf;
                                        leaf = leaf->next;
                                    }
                                    if (target_leaf!=NULL)
                                        break;
                                }
                            }
                        }
                    }
                    if (target_leaf==NULL){
                        for (i=1;i<=8;i++){
                            temp_match = match - 8- i;
                            unsigned int shift_a = (a>>i)<<i;
                            
                            temp = find_node(root, shift_a);
                            if (temp!=NULL){
                                temp = find_node(temp->children, 0);
                                if (temp!=NULL){
                                    temp = find_node(temp->children, 0);
                                    if (temp!=NULL){
                                        temp = find_node(temp->children, 0);
                                        if (temp!=NULL){
                                            leaf = temp->leafs;
                                            while (leaf!=NULL) {
                                                if (temp_match>=leaf->prefix)
                                                    target_leaf = leaf;
                                                leaf = leaf->next;
                                            }
                                            if (target_leaf!=NULL)
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                }
                return target_leaf;
            }
            
            else{
				// third level passes
                match += 8;
                target = temp;
                
                temp = find_node(target->children, d);
                if (temp==NULL){
                    min = find_match(target->children, d);
                    for (i=min;i<=8;i++){
                        temp_match = match;
                        temp_match += (8 - i);
                        unsigned int shift_d = (d>>i)<<i;
                        
                        temp = find_node(target->children, shift_d);
                        if (temp!=NULL){
                            leaf = temp->leafs;
                            while (leaf!=NULL) {
                                if (temp_match>=leaf->prefix)
                                    target_leaf = leaf;
                                leaf = leaf->next;
                            }
                            if (target_leaf!=NULL)
                                break;
                        }
                    }
                    
                    if (target_leaf==NULL){
						// search for the longest prefix match,
						// starting from longest bit matching length
						for (i=min;i<=8;i++){
                            temp_match = match - i;
                            unsigned int shift_c = (c>>i)<<i;
                            
                            temp = find_node(target->parent->children, shift_c);
                            if (temp!=NULL){
                                temp = find_node(temp->children, 0);
                                if (temp!=NULL){
                                    leaf = temp->leafs;
                                    while (leaf!=NULL) {
                                        if (temp_match>=leaf->prefix)
                                            target_leaf = leaf;
                                        leaf = leaf->next;
                                    }
                                    if (target_leaf!=NULL)
                                        break;
                                }
                            }
                        }
                        // if ip not found, go back to higher levels
                        if (target_leaf==NULL){
                            for (i=1;i<=8;i++){
                                temp_match = match - 8 - i;              unsigned int shift_b = (b>>i)<<i;
                                
                                temp = find_node(target->parent->parent->children, shift_b);
                                if (temp!=NULL){
                                    temp = find_node(temp->children, 0);
                                    if (temp!=NULL){
                                        temp = find_node(temp->children, 0);
                                        if (temp!=NULL){
                                            Leaf* leaf = temp->leafs;
                                            while (leaf!=NULL) {
                                                if (temp_match>=leaf->prefix)
                                                    target_leaf = leaf;
                                                leaf = leaf->next;
                                            }
                                            if (target_leaf!=NULL)
                                                break;
                                        }
                                    }
                                }
                            }
                            if (target_leaf==NULL){
                                for (i=1;i<=8;i++){
                                    temp_match = match - 16 - i;                                    unsigned int shift_a = (a>>i)<<i;
                                    
                                    temp = find_node(root, shift_a);
                                    if (temp!=NULL){
                                        temp = find_node(temp->children, 0);
                                        if (temp!=NULL){
                                            temp = find_node(temp->children, 0);
                                            if (temp!=NULL){
                                                temp = find_node(temp->children, 0);
                                                if (temp!=NULL){
                                                    leaf = temp->leafs;
                                                    while (leaf!=NULL) {
                                                        if (temp_match>=leaf->prefix)
                                                            target_leaf = leaf;
                                                        leaf = leaf->next;
                                                    }
                                                    if (target_leaf!=NULL)
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            
                        }
                        
                    }
                    return target_leaf;
                }
                
                else{
					// fourth level passes
					// find the ip with all prefix matches
                    match += 8;
                    target = temp;
                    leaf = target->leafs;
                    
                    // try to find the IP with the provided prefix
                    // if it is not found, NULL is returned
                    while (leaf!=NULL) {
                        if (match>=leaf->prefix)
                            target_leaf = leaf;
                        leaf = leaf->next;
                    }
                    
                    return target_leaf;
                }
            }
        }
    }
}

// print the entire tree
void print_tree(Node* root, char* str){
	// base case : only lowest level nodes have leafs (the real data)
    if (root->leafs!=NULL){
        Leaf* leaf = root->leafs;
        while (leaf!=NULL) {
			// dont print if the IP is unreachable
            if (leaf->metric!=UNREACHABLE)
                fprintf(stdout, "%s%d/%d %d %d\n",str,root->index,leaf->prefix,leaf->NIC,leaf->metric);
            leaf = leaf -> next;
        }
    }
	// recursive case : append the index and dot
    else{
        Node* temp = root;
        while (temp!=NULL) {
            char temp_str[strlen(str)];
            strcpy(temp_str, str);
            char index_str[4];
            sprintf(index_str, "%d",temp->index);
            strcat(temp_str, index_str);
            strcat(temp_str, ".");
            
            print_tree(temp->children, temp_str);
            temp = temp->next;
        }
    }
}

// add the node to node_list
// used by add_leaf
// returning the root of the tree
Node* add_node(Node* node_list, Node* node){
    if (node_list==NULL){
        node_list = node;
        return node_list;
    }
    int index = node->index;
    Node* next = node_list;
    
    if (next->next==NULL){
        if (next->index < index)
            next->next = node;
        else{
            node_list = node;
            node->next = next;
            next->next = NULL;
        }
    }
    else{
        Node* prev = NULL;
        while (next!=NULL) {
            if (next->index > index)
                break;
            prev = next;
            next = next->next;
        }
        
        if (prev==NULL)
            node_list = node;
        else
            prev->next = node;
        node->next = next;
    }
    
    return node_list;
}

// add the child to parent
// used by add_leaf
void add_child_to_node(Node* parent, Node* child){
    child->parent = parent;
    if (parent->children==NULL){
        parent->children = child;
        return;
    }
    int index = child->index;
    Node* next = parent->children;
    
    if (next->next==NULL){
        if (next->index < index)
            next->next = child;
        else{
            parent->children = child;
            child->next = next;
            next->next = NULL;
        }
    }
    else{
        Node* prev = NULL;
        while (next!=NULL) {
            if (next->index > index)
                break;
            prev = next;
            next = next->next;
        }
        
        if (prev==NULL)
            parent->children = child;
        else
            prev->next = child;
        child->next = next;
    }
}

// add a leaf(ip) to the tree
// returning the root of the tree
Node* add_leaf(Node* root, unsigned int a, unsigned int b, unsigned int c, unsigned int d, int prefix, int NIC, unsigned int metric, int nic_num){
    
	// initialize the leaf with a,b,c,d,prefix,and NIC provided
	Leaf* leaf = (Leaf*) (malloc(sizeof(Leaf)));
    initialize_leaf(leaf, prefix, NIC,metric,nic_num);
    
    Node* level_one = find_node(root, a);
    if (level_one==NULL) {
        Node* temp = (Node*) (malloc(sizeof(Node)));
        initialize_node(temp, a);
        root = add_node(root, temp);
        level_one = temp;
    }
    
    Node* level_two = find_node(level_one->children, b);
    if (level_two==NULL) {
        Node* temp = (Node*) (malloc(sizeof(Node)));
        initialize_node(temp, b);
        add_child_to_node(level_one, temp);
        level_two = temp;
    }
    
    Node* level_three = find_node(level_two->children, c);
    if (level_three==NULL) {
        Node* temp = (Node*) (malloc(sizeof(Node)));
        initialize_node(temp, c);
        add_child_to_node(level_two, temp);
        level_three = temp;
    }
    
    Node* target = find_node(level_three->children, d);
    if (target==NULL) {
        Node* temp = (Node*) (malloc(sizeof(Node)));
        initialize_node(temp, d);
        add_child_to_node(level_three, temp);
        target = temp;
    }
    
    // add the leaf the list of leafs with different prefixes
	// list is sorted in increasing order
    if (target->leafs==NULL)
        target->leafs = leaf;
    else{
        Leaf* prev = target->leafs;
        
        if (prev->next==NULL){
            if (prev->prefix < prefix){
                prev->next = leaf;
                leaf->prev = prev;
            }
            else{
                target->leafs = leaf;
                leaf->next = prev;
                prev->prev = leaf;
                prev->next = NULL;
            }
        }
        else{
            Leaf* next = NULL;
            while (prev->next!=NULL){
                next = prev->next;
                if ((next->prefix >= prefix)&&(prev->prefix < prefix))
                    break;
                prev = next;
            }
            
            leaf->prev = prev;
            prev->next = leaf;
            next->prev = leaf;
            leaf->next = next;
        }
    }
    
    return root;
}

// update the current NIC, NIC table , and metric if needed
// return 1 if changes happen, 0 otherwise
int update_leaf_nic(Leaf* leaf, int nic, unsigned int new_metric, int nic_num){
    NIC_metric* table = leaf->neighbour_table;
    table[nic].metric = new_metric;
    
    unsigned int min_metric = -1;
    int min_metric_nic = -1;

    int j = 0;
    while (j<nic_num){
        if (table[j].metric < min_metric){
            min_metric = table[j].metric;
            min_metric_nic = table[j].NIC;
        }
        j++;
    }
    
    if (min_metric<UNREACHABLE){
        if ((leaf->metric == min_metric + 1)&&(leaf->NIC == min_metric_nic))
            return 0;
        
        leaf->metric = min_metric + 1;
        leaf->NIC = min_metric_nic;
        return 1;
    }
    
    else{
        if ((leaf->metric == UNREACHABLE)&&(leaf->NIC == min_metric_nic))
            return 0;
        
        leaf->metric = UNREACHABLE;
        leaf->NIC = min_metric_nic;
        return 1;
    }
}

