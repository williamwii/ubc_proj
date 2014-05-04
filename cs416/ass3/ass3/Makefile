all: electleader

electleader: electleader.o
	gcc electleader.o -o electleader -L/cs/local/lib/pkg/mpich-3.0.4/lib -lmpich -lopa -lmpl -lrt -lpthread

electleader.o: electleader.c
	gcc -c -I /cs/local/lib/pkg/mpich-3.0.4/include electleader.c

clean:
	rm *.o
	rm electleader
