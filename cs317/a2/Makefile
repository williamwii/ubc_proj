all : ip_forward ip_route ip_both

ip_forward : ip_forward.o p1myTree.o

ip_route : ip_route.o p2_3myTree.o

ip_both : ip_both.o p2_3myTree.o

ip_forward.o : ip_forward.c p1myTree.h
	gcc -c ip_forward.c
	
ip_route.o : ip_route.c p2_3myTree.h
	gcc -c ip_route.c
	
ip_both.o : ip_both.c p2_3myTree.h
	gcc -c ip_both.c
	
p1myTree.o : p1myTree.c p1myTree.h
	gcc -c p1myTree.c

p2_3myTree.o : p2_3myTree.c p2_3myTree.h
	gcc -c p2_3myTree.c

clean :
	rm *.o
	rm ip_forward
	rm ip_route
	rm ip_both
