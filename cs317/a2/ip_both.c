//
//  main
//  ip_both.c
//

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "p2_3myTree.h"

#define UNREACHABLE 1000

int main (int argc, const char * argv[])
{
    FILE* file;
    char *current, *rest, *type, *ip_pointer;
    unsigned int a, b, c, d, metric;
    int prefix, NIC, NIC_num, id;
    
    Node* tree = (Node*) (malloc(sizeof(Node)));
    
    current = (char*) (malloc(128*sizeof(char)));
    
    /* Check arguments */
    if (argc != 2) {
        fprintf(stderr, "Invalid arguments. You should provide a single argument.\n");
        return 1;
    }
    
    // Open the file
    //file = fopen ("/Users/William/Documents/CPSC 317/a2/ip_both/ip_both/p3input1.txt","r");
    
    file = fopen(argv[1], "r");
    // Check for errors when opening the file
    if (!file){
        perror("Invalid file");
        return 2;
    }
    
	// get the total amount of NIC
    fgets(current, 35, file);
    NIC_num = atoi(current);
    
    fgets(current, 35, file);
    type = strtok_r(current, " ", &rest);
    
	// assuming the first line after NIC number is of type "U"
	// used to initial the tree (myTree need to initial the root)
    if (strcmp(type, "U")==0){
        ip_pointer = strtok_r(rest, ".", &rest);
        a = atoi(ip_pointer);
        ip_pointer = strtok_r(rest, ".",&rest);
        b = atoi(ip_pointer);
        ip_pointer = strtok_r(rest, ".", &rest);
        c = atoi(ip_pointer);
        ip_pointer = strtok_r(rest, "/",&rest);
        d = atoi(ip_pointer);
        ip_pointer = strtok_r(rest, " ",&rest);
        prefix = atoi(ip_pointer);
        ip_pointer = strtok_r(rest, " ",&rest);
        NIC = atoi(ip_pointer);
        ip_pointer = strtok_r(rest, " ",&rest);
        metric = atoi(ip_pointer);
        ip_pointer = strtok_r(rest, " ",&rest);
        id = atoi(ip_pointer);
        
        
        initialize_node(tree, a);
        tree = add_leaf(tree, a, b, c, d, prefix, NIC, metric, NIC_num);
        fprintf(stdout, "%s %d.%d.%d.%d/%d %d %d\n", "A", a, b, c, d, prefix, metric+1, id);
    }
    
    
    
    while (fgets(current, 35, file)!=NULL) {
        
        type = strtok_r(current, " ", &rest);
        
		// do the table update if is type "U"
        if (strcmp(type, "U")==0){
            ip_pointer = strtok_r(rest, ".", &rest);
            a = atoi(ip_pointer);
            ip_pointer = strtok_r(rest, ".",&rest);
            b = atoi(ip_pointer);
            ip_pointer = strtok_r(rest, ".", &rest);
            c = atoi(ip_pointer);
            ip_pointer = strtok_r(rest, "/",&rest);
            d = atoi(ip_pointer);
            ip_pointer = strtok_r(rest, " ",&rest);
            prefix = atoi(ip_pointer);
            ip_pointer = strtok_r(rest, " \n",&rest);
            NIC = atoi(ip_pointer);
            ip_pointer = strtok_r(rest, " ",&rest);
            metric = atoi(ip_pointer);
            ip_pointer = strtok_r(rest, " ",&rest);
            id = atoi(ip_pointer);
            
            Leaf* leaf = find_leaf(tree, a, b, c, d,prefix);
			
			// if the address is not found, add that ip to the table
            if (leaf==NULL){
                tree = add_leaf(tree, a, b, c, d, prefix, NIC, metric, NIC_num);
                fprintf(stdout, "%s %d.%d.%d.%d/%d %d %d\n", "A", a, b, c, d, prefix, metric+1, id);
            }
			// else update the table
            else{
                if (update_leaf_nic(leaf, NIC, metric, NIC_num)==1)
                    fprintf(stdout, "%s %d.%d.%d.%d/%d %d %d\n", "A", a, b, c, d, prefix, leaf->metric, id);
            }
        }
        
		// forward the packet if type is "P"
        else if (strcmp(type, "P")==0){
            ip_pointer = strtok_r(rest, ".", &rest);
            a = atoi(ip_pointer);
            ip_pointer = strtok_r(rest, ".",&rest);
            b = atoi(ip_pointer);
            ip_pointer = strtok_r(rest, ".", &rest);
            c = atoi(ip_pointer);
            ip_pointer = strtok_r(rest, " ", &rest);
            d = atoi(ip_pointer);
            ip_pointer = strtok_r(rest, " \n", &rest);
            int packet_id = atoi(ip_pointer);
            
            Leaf* target = find_IP_route(tree, a, b, c, d);
            
			// if target is not found to it is unreachable, NIC is -1
            if ((target==NULL)||(target->metric==UNREACHABLE))
                NIC = -1;
            else
                NIC = target->NIC;
            
            fprintf(stdout, "O %d %d\n", packet_id, NIC);
        }

        
        
        
    }
    
	// check errors after opening the file
    if (ferror(file)) {
        perror("Error reading file");
        fclose(file);
        return 3;
    }
    
    fprintf(stdout, "\n");
    
	// printing the tree
    char* my_printer = (char*) (malloc(20*sizeof(char)));
    print_tree(tree, my_printer);
    
    fclose(file);
    return 0;
    
}

