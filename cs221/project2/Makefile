# 221 STUDENTS: The most useful commands with the makefile will be
# "make" alone (which should rebuild any portions of your project that
# need to be rebuilt) and "make clean" (which starts you off with a
# clean build).  If things seem to be going wrong with the build
# process, it's sometimes helpful to "make clean" and then "make".
#
# You will also need to make some changes to the makefile, as
# indicated by the comments below.

# default target "all" builds the executable file 'runmaze' and the
# executable 'sort'
all : runmaze sort

# runmaze is dependent on the following .o (object) files
# add any new .o files to the list
# build it using g++
runmaze : runmaze.o MazeRunner.o RandomMazeRunner.o BestFSMazeRunner.o Maze.o SquareMaze.o VisualizeSquareMazeRunner.o GPKernel.o Stack.o
	g++ -o runmaze -g runmaze.o MazeRunner.o RandomMazeRunner.o BestFSMazeRunner.o Maze.o SquareMaze.o VisualizeSquareMazeRunner.o GPKernel.o Stack.o -L/usr/X11R6/lib -lX11

# runmaze.o is dependant on one .cpp file, and several .h files
# IMPORTANT: when you #include a new .h file, be sure to add it here.  Otherwise, the 'make' command may not re-build your program even though you changed a file.
# -Wall: make the compiler display all warnings it knows about; hopefully it'll catch something for us
# -c: only compile, don't link (we do that above)
# -g: put in debug information, for those of you who are using debuggers (like gdb, xxgdb, ddd, or the like)
runmaze.o : runmaze.cpp Maze.h SquareMaze.h MazeRunner.h RandomMazeRunner.h BestFSMazeRunner.h VisualizeSquareMazeRunner.h 
	g++ -Wall -c -g runmaze.cpp

# and same idea for other .o files

MazeRunner.o : MazeRunner.cpp Maze.h MazeRunner.h
	g++ -Wall -c -g MazeRunner.cpp

RandomMazeRunner.o : RandomMazeRunner.cpp Maze.h MazeRunner.h RandomMazeRunner.h
	g++ -Wall -c -g RandomMazeRunner.cpp

BestFSMazeRunner.o : BestFSMazeRunner.cpp Maze.h MazeRunner.h Stack.h BestFSMazeRunner.h
	g++ -Wall -c -g BestFSMazeRunner.cpp
	
SquareMaze.o : SquareMaze.cpp Maze.h SquareMaze.h
	g++ -Wall -c -g SquareMaze.cpp

Maze.o : Maze.cpp Maze.h
	g++ -Wall -c -g Maze.cpp

VisualizeSquareMazeRunner.o : VisualizeSquareMazeRunner.cpp VisualizeSquareMazeRunner.h Maze.h SquareMaze.h GPKernel.h
	g++ -Wall -c -g VisualizeSquareMazeRunner.cpp

GPKernel.o : GPKernel.c GPKernel.h
	gcc -c -g GPKernel.c
	
Stack.o : Stack.cpp Maze.h Stack.h
	g++ -Wall -c -g Stack.cpp

# sort is dependent on the following .o (object) files. Note that no
# templated files have .o files here. That's because their code is
# included in every file that includes their .h files.  
# add any new .o files to the list 
# build it using g++
sort : sort.o
	g++ -o sort -g sort.o

sort.o : sort.cpp PQueue.h UnsLLPQueue.h UnsLLPQueue.cpp
	g++ -Wall -c -g sort.cpp

# 'clean' removes any files that were created via the Makefile, leaving you with only your original source code files
clean :
	rm *.o
	rm runmaze
	rm sort

