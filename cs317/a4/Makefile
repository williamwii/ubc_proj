all : http_service

http_service : http_service.o

http_service.o : http_service.c
	gcc -c http_service.c
	
clean :
	rm *.o
	rm http_service