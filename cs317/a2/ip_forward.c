//
//  main
//  ip_forward.c
//

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "p1myTree.h"

int main (int argc, const char * argv[])
{
    FILE* file;
    char *current, *rest, *type, *ip_pointer;
    unsigned int a, b, c, d;
    int prefix, NIC, NIC_num;
    
    
    Node* tree = (Node*) (malloc(sizeof(Node)));
    
    current = (char*) (malloc(128*sizeof(char)));
    
    /* Check arguments */
    if (argc != 2) {
     fprintf(stderr, "Invalid arguments. You should provide a single argument.\n");
     return 1;
     }
    
    // Open the file
    //file = fopen ("/Users/William/Documents/CPSC 317/a2/ip_forward/ip_forward/p1input1.txt","r");
    
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
	
	// assuming the first line after NIC number is of type "T"
	// used to initial the tree (myTree need to initial the root)
    if (strcmp(type, "T")==0){
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
        
        initialize_node(tree, a);
        add_leaf(tree, a, b, c, d, prefix, NIC);
    }
    
    
    
    while (fgets(current, 35, file)!=NULL) {
        
        type = strtok_r(current, " ", &rest);
        
		// setting up the table
        if (strcmp(type, "T")==0){
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
            
            add_leaf(tree, a, b, c, d, prefix, NIC);
        }
        
		// forward the packet
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
            
			// if the address is not found NIC is -1
            if (target==NULL)
                NIC = -1;
            else
                NIC = target->NIC;
            
            fprintf(stdout, "O %d %d\n", packet_id, NIC);
        }
        
    }
    
	// check for errors after opening the file
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

