#
# This code is adapted from a programming course at 
# Boston University for use in CPSC 317 at UBC
#

CC     = gcc
CFLAGS = -g -Wall
CLIBS  = 

all: SendApp ReceiveApp

SendApp: sender.o stp.o wraparound.o
	$(CC) -o $@ $(CLIBS) $(CFLAGS) $^

ReceiveApp: receiver.o wraparound.o receiver_list.o stp.o 
	$(CC) -o $@ $(CLIBS) $(CFLAGS) $^

sender.o: sender.c stp.h wraparound.h
receiver.o: receiver.c stp.h receiver_list.h wraparound.h
wraparound.o: wraparound.c stp.h wraparound.h
receiver_list.o: receiver_list.c stp.h receiver_list.h wraparound.h
stp.o: stp.h stp.c

realclean: emacsClean clean
clean:
	-rm -f *.o SendApp ReceiveApp
emacsClean:
	-rm -f *~
