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
10
----------------------------------------------------------------------

For teams, rough breakdown of work:

----------------------------------------------------------------------

Acknowledgment of assistance:
Koffman Wolfgang textbook
----------------------------------------------------------------------

Files in your submission:

[Add documentation to this list:]

* README.txt: this file

* BestFSMazeRunner.[h,cpp] maze runner that search the exit by best first search

* Compare.h a comparator for MazeNode*s

* HeapPQueue.[h,cpp] a PriorityQueue that is stored by a heap

* runmaze.cpp contain the main function of the program to solve the maze

* Stack.[h,cpp] a stack of MazeNodes, used by BestFSMazeRunner to reverse the output

* Makefile to make and rebuild the runmaze program


----------------------------------------------------------------------

High-level description of MazeRunners, Stack, and Queue, including any
problems or surprises:
  
  -BestFSMazeRunner : uses a priority queue to solve the maze. insert all unvisited 
					  neighbour nodes into the queue. Find the node that have the
					  shortest distance to exit and insert the neighbour of that node.
					  If a dead end is found, remove the minimum and check the pqueue 
					  again.Keep doing this until exit is found.
				  
  -Heap : Each item in heap has d childrens and one parent(except the root has no parent)
		  The minimum item is always going to get deleted first.
		  
  -comparator : Compare two MazeNode*s. Help the heap to find minimum value and manage the heap.
			    The MzeNode* that is closer to the exit node is wanted.

----------------------------------------------------------------------

Answers to questions:
1) Yes, Best First Search guaranteed to find the shortest solution to the maze.
   
2)
The makeEmpty function in UnsLLPQueue.cpp jumps around a lot of other functions and does
a lot of checkings and it need to loop through the whole queue.

better way:
while ( header->next!=NULL ){
	temp = header;
	header = header->next;
	delete temp;
}
delete header;


