Student Name #1:Wei You
Student ugrad login #1:r9e7 

Student Name #2:
Student ugrad login #2:

Team name (for fun!):

Acknowledgment that you understand and have followed the course's
collaboration policy
(http://www.ugrad.cs.ubc.ca/~cs221/current/syllabus.shtml#conduct):

[[Put your names here again]]
Wei You
----------------------------------------------------------------------

Approximate hours on project:
20
----------------------------------------------------------------------

For teams, rough breakdown of work:

----------------------------------------------------------------------

Acknowledgment of assistance:
Koffman Wolfgang textbook
----------------------------------------------------------------------

Files in your submission:

[Add documentation to this list:]

* README.txt: this file

* DFSMazeRunner.[h,cpp] maze runner that search the exit by depth first search

* BFSMazeRunner.[h,cpp] maze runner that search the exit by depth first search

* runmaze.cpp contain the main function of the program to solve the maze

* Stack.[h,cpp] a stack of MazeNodes, used by DFSMazeRunner

* Queue.[h,cpp] a stack of MazeNodes, used by BFSMazeRunner

* Makefile to make and rebuild the runmaze program


----------------------------------------------------------------------

High-level description of MazeRunners, Stack, and Queue, including any
problems or surprises:
  
  -DFSMazeRunner : uses a stack to solve the maze. Push a single unvisited
				   neighbour node into the stack, if all the neighours are visited
				   or meet a dead end, pop a node from the stack and check the next
				   item on stack. When exit is found, the items on stack is the route
				   from starting point to exit (backward).
  
  -BFSMazeRunner : uses a queue to solve the maze. Enqueue all unvisited 
                   neighbour nodes into the queue. Check and do the same thing
				   with the next node in the queue until exit is found.
				  
  -Stack : the last item pushed onto stack is the first one out (LIFO)
  
  -Queue : the first item enqueued into queue is the first one out (FIFO)
  
  - a BFSMazeRunner will always find the shortest route to exit


----------------------------------------------------------------------

Example BFS << DFS maze(BFS explores less):
	a maze where the exit is right beside the starting node, but the exit node is the
	last node in iteration.

Example DFS << BFS maze(DFS explores less):
	a maze which the nodes in the route to exit is always the first node in iteration of
	neighour nodes.
----------------------------------------------------------------------

Answers to questions:
1) DFS will check alot more nodes or it might go into an infinte loop.
   BFS will check alot more nodes.
   This will have a greater impact on DFS.
   
2) DFS will run faster, beause my DFS is pushing only one neighbour into the stack each time 
   but my BFS is enqueuing all the neighbours into the queue and then check them. My DFS uses
   alot less space because of this. While the memory is full, DFS will run faster.


