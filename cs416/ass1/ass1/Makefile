all: mtserver

mtserver: mtserver.o
	gcc mtserver.o -o mtserver -lpthread

mtserver.o: mtserver.c
	gcc -c mtserver.c

clean:
	rm *.o
	rm mtserver
