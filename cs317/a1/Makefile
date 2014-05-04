all : eth_switch vlan_switch

eth_switch : eth_switch.o

vlan_switch : vlan_switch.o


eth_switch.o : eth_switch.c
	gcc -c eth_switch.c

vlan_switch.o : vlan_switch.c
	gcc -c vlan_switch.c


clean :
	rm *.o
	rm eth_switch
	rm vlan_switch