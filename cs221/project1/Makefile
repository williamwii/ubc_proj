# 221 STUDENTS: The most useful commands with the makefile will be
# "make" alone (which should rebuild any portions of your project that
# need to be rebuilt) and "make clean" (which starts you off with a
# clean build).  If things seem to be going wrong with the build
# process, it's sometimes helpful to "make clean" and then "make".
#
# You will also need to make some changes to the makefile, as
# indicated by the comments below.

# our project ("all") is just to build the executable file 'runmaze'
all : runmaze

# 221 STUDENTS: You will eventually need to edit the "runmaze" line
# below to include DFSMazeRunner.o, BFSMazeRunner.o, Stack.o, and
# Queue.o.

# runmaze is dependant on all of the .o (object) files
# add any new .o files to the list
# build it using g++
runmaze : runmaze.o MazeRunner.o RandomMazeRunner.o Maze.o SquareMaze.o VisualizeSquareMazeRunner.o GPKernel.o DFSMazeRunner.o Stack.o BFSMazeRunner.o Queue.o
	g++ -o runmaze -g runmaze.o MazeRunner.o RandomMazeRunner.o DFSMazeRunner.o BFSMazeRunner.o Stack.o Queue.o Maze.o SquareMaze.o VisualizeSquareMazeRunner.o GPKernel.o -L/usr/X11R6/lib -lX11


# 221 STUDENTS: when you #include a new .h file, be sure to add it
# here.  Otherwise, the 'make' command may not re-build your program
# even though you changed a file.

# runmaze.o is dependant on one .cpp file, and several .h files
# -Wall: make the compiler display all warnings it knows about; hopefully it'll catch something for us
# -c: only compile, don't link (we do that above)
# -g: put in debug information, for those of you who are using debuggers (like gdb, xxgdb, ddd, or the like)
runmaze.o : runmaze.cpp Maze.h SquareMaze.h MazeRunner.h RandomMazeRunner.h DFSMazeRunner.h BFSMazeRunner.h Stack.h Queue.h VisualizeSquareMazeRunner.h 
	g++ -Wall -c -g runmaze.cpp

# 221 STUDENTS: And.. you'll want to similarly update the other
# dependencies (including adding BFSMazeRunner.o and DFSMazeRunner.o
# targets).

MazeRunner.o : MazeRunner.cpp Maze.h MazeRunner.h
	g++ -Wall -c -g MazeRunner.cpp

RandomMazeRunner.o : RandomMazeRunner.cpp Maze.h MazeRunner.h RandomMazeRunner.h
	g++ -Wall -c -g RandomMazeRunner.cpp

DFSMazeRunner.o : DFSMazeRunner.cpp Maze.h MazeRunner.h Stack.h DFSMazeRunner.h
	g++ -Wall -c -g DFSMazeRunner.cpp

BFSMazeRunner.o : BFSMazeRunner.cpp Maze.h MazeRunner.h Queue.h BFSMazeRunner.h
	g++ -Wall -c -g BFSMazeRunner.cpp
	
Stack.o : Stack.cpp Maze.h Stack.h
	g++ -Wall -c -g Stack.cpp
	
Queue.o : Queue.cpp Maze.h Queue.h
	g++ -Wall -c -g Queue.cpp
	
SquareMaze.o : SquareMaze.cpp Maze.h SquareMaze.h
	g++ -Wall -c -g SquareMaze.cpp

Maze.o : Maze.cpp Maze.h
	g++ -Wall -c -g Maze.cpp

VisualizeSquareMazeRunner.o : VisualizeSquareMazeRunner.cpp VisualizeSquareMazeRunner.h Maze.h SquareMaze.h
	g++ -Wall -c -g VisualizeSquareMazeRunner.cpp

GPKernel.o : GPKernel.c GPKernel.h
	gcc -c -g GPKernel.c

# 'clean' removes any files that were created via the Makefile, leaving you with only your original source code files
clean :
	rm *.o
	rm runmaze

