

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Max of number of ports
#define TABLE_LENGTH 32

// Address that always broadcast
#define BROADCAST_ADDR strtoull("ffffffffffff",NULL,16)


typedef struct MAC{
    char* port;
    unsigned long long int addr;
    struct MAC* next;
}MAC;

typedef struct PORT{
	char* portname;
	MAC* MAC_list;
    int counter;
}PORT;

void init_PORT (PORT* p, char* p_name){
    p->portname = p_name;
    p->MAC_list = NULL;
    p->counter = 0;
}

void init_MAC (MAC* m, char* addr){
    m->addr = strtoull(addr, NULL, 16);
    m->port = NULL;
    m->next = NULL;
}


// Function used to add a mac address to a port
void add_MAC_to_list (PORT* port, MAC* target){
    target->port = port->portname;
    MAC* temp = port->MAC_list;
    if (temp==NULL){
        port->MAC_list = target;
        return;
    }
    while (temp->next!=NULL)
        temp = temp->next;
    temp->next = target;
}


// Function used to remove a mac address from port
MAC* remove_MAC_from_list (PORT* port, char* target){
    unsigned long long int target_num = strtoull(target, NULL, 16);
    MAC* to_return = NULL;
    MAC* temp = port->MAC_list;
    if (temp->addr==target_num){
        if (temp->next==NULL){
            port->MAC_list = NULL;
            return temp;
        }
        else{
            port->MAC_list = temp->next;
            temp->next = NULL;
            return temp;
        }
    }
    while (temp->next!=NULL){
        if (temp->next->addr==target_num){
            to_return = temp->next;
            if (temp->next->next==NULL)
                temp->next = NULL;
            else{
                MAC* temp_next_next = temp->next->next;
                temp->next->next = NULL;
                temp->next = temp_next_next;
            }
            break;
        }
        temp = temp->next;
    }
    return to_return;
}


// Function that return the index of the target port in pTable,
// if target is not found, return -1.
int find_port (char* target,PORT* PTable,int length){
    int i;
    for (i=0;i<length;i++){
        if (strcmp(PTable[i].portname,target)==0)
            return i;
    }
    return -1;
}

// Function to find port with an address passed in.
// Return the index of the port in the table.
// If address is not found, return -1.
int find_port_with_addr (char* addr,PORT* PTable,int length){
    //char* end;
    unsigned long long int addr_num = strtoull(addr, NULL, 16);
    int i;
    for (i=0;i<length;i++){
        MAC* temp = PTable[i].MAC_list;
        while (temp!=NULL){
            if (addr_num==temp->addr)
                return i;
            temp = temp->next;
        }
        
    }
    return -1;
}

// Function to check if the target MAC address is in the port.
// Return 1 if contains, 0 otherwise
int port_contains(char* target,PORT port){
    char* end;
    unsigned long long int target_num = strtoull(target, &end, 16);
    MAC* list = port.MAC_list;
    while (list!=NULL){
        if (target_num==list->addr)
            return 1;
        list = list->next;
    }
    return 0;
}


int main (int argc, const char * argv[])
{   
    FILE *file;
    
    // Open the file, if the file is not found, exit with code 1
    file = fopen(argv[1],"r");
    if (file==NULL){
        fprintf(stdout, "File not found \n");
        exit(1);
    }
	
    // Initialize the port table
    PORT* portTable = (PORT*) (malloc (TABLE_LENGTH * sizeof(PORT)));
    char* p_name_input = (char*) (malloc(128*sizeof(char)));
	fgets(p_name_input,128,file);
    
    // Set the names for all ports
    char* ep;
    char* sp = strtok_r(p_name_input," ",&ep);
    int port_num = 0;
	while (sp!=NULL){
		PORT temp;
        init_PORT(&temp, sp);
        portTable[port_num] = temp;
		port_num++;
		sp = strtok_r(ep," \n",&ep);
	}
    
    // Counter used to keep track of the number of MAC addresses
    int total_mac = 0;
    
	char* input = (char*) (malloc(128*sizeof(char)));
    while (fgets(input, 35, file)!=NULL){
        if (strcmp(input,"\n")!=0){
            char* rest;
            char* p_name = strtok_r(input, " \n", &rest);
        
            char* MAC_Source_Addr = strtok_r(rest, " \n", &rest);
            char* MAC_Dest_Addr = strtok_r(rest, " \n", &rest);
            char* id = strtok_r(rest, " \n", &rest);

            
            // Find the input port and the port containing the MAC address (source_port),
            // if the source port is not the input port, update the portTable
            // by adding the MAC address to input port and removing from the source port.
            int input_index = find_port(p_name, portTable, port_num);
            int source_index = find_port_with_addr(MAC_Source_Addr, portTable, port_num);

            PORT* input_port = &portTable[input_index];
            if (port_contains(MAC_Source_Addr,*input_port)==0){
                MAC* MAC_Source;
                if ((source_index!=-1)&&(source_index!=input_index))
                    MAC_Source = remove_MAC_from_list(&portTable[source_index],MAC_Source_Addr);
                else{
                    MAC_Source = (MAC*) (malloc(sizeof(MAC)));
                    init_MAC(MAC_Source, MAC_Source_Addr);
                    total_mac++;
                }
                add_MAC_to_list (input_port, MAC_Source);
            }
            
            
            // Find the destination port with the provided mac address.
            // If mac address is BROADCAST_ADDR or destination port is
            // not found in all the ports, broadcast the packet.
            // If dest port is same with source port, drop the packet.
            // Else, send to destination port.
            int dest_index = find_port_with_addr(MAC_Dest_Addr, portTable, port_num);
            unsigned long long int d_addr = strtoull(MAC_Dest_Addr, NULL, 16);
            char* dest;
            int i;
            if ((d_addr==BROADCAST_ADDR)||(dest_index==-1)){
                for (i=0;i<port_num;i++){
                    if (i!=input_index)
                        portTable[i].counter++;
                }
                dest = "BROADCAST";
            }
            else if (input_index==dest_index)
                dest = "DROP";
            else{
                portTable[dest_index].counter++;
                dest = portTable[dest_index].portname;
            }
        
            fprintf(stdout, "%s %s\n",id,dest);
        }
    }
    
    // Print out port statistics
    fprintf(stdout, "\n");
    int i;
    for (i=0;i<port_num;i++){
        fprintf(stdout, "%s %d\n",portTable[i].portname,portTable[i].counter);
    }
        
    // Putting all mac addresses into one array.
    MAC** all_mac = (MAC**) (malloc(total_mac * sizeof(MAC*)));
    int j;
    int k = 0;
    for (j=0;j<port_num;j++){
        MAC* temp = portTable[j].MAC_list;
        while (temp!=NULL) {
            all_mac[k] = temp;
            k++;
            temp = temp->next;
        }
    }

    // Do selection sort on the array.
    for (i=0;i<total_mac;i++){
        int smallest = i;
        for (j=i+1;j<total_mac;j++){
            if (all_mac[j]->addr<all_mac[smallest]->addr)
                smallest = j;
        }
        MAC* temp = all_mac[i];
        all_mac[i] = all_mac[smallest];
        all_mac[smallest] = temp;
    }
    
    // Print the sorted array of mac addresses and associated port.
    fprintf(stdout, "\n");
    for (k=0;k<total_mac;k++){
        fprintf(stdout, "%012llx %s\n",all_mac[k]->addr,all_mac[k]->port);
    }
    
    
	fclose (file);
    return 0;
}

