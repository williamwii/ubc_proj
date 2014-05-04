all: assign2

assign2: assign2.o
	gcc assign2.o -o assign2 -lpthread

assign2.o: assign2.c
	gcc -c assign2.c

clean:
	rm *.o
	rm assign2
