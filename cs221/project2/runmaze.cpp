/*
  runmaze.C: contains 'main' function.

  221 STUDENTS: FEEL FREE TO USE THIS AS THE BASIS FOR YOUR MAIN PROGRAM.

*/


#include <iostream>

#include <cstring>
#include <cstdlib>

// 221 STUDENTS: You'll need to include any .h files of classes that
// you use here (probably your maze runners!)
#include "Maze.h"
#include "SquareMaze.h"
#include "MazeRunner.h"
#include "RandomMazeRunner.h"
#include "BestFSMazeRunner.h"
#include "HeapPQueue.h"
#include "VisualizeSquareMazeRunner.h"
#include "Compare.h"

// 221 STUDENTS: You may safely ignore this function.
// Process the command line arguments (altering the given default
// values for the parameters as needed).
void processCommandLineArguments(int argc, char * argv[], 
				 bool & showViz, 
				 bool & showSoln, 
				 double & vizPauseSecs)
{
  int a = 1; // the index of the next argument.
  while (a<argc) {

    if (strcmp(argv[a],"-h")==0 || strcmp(argv[a],"--help")==0 || strcmp(argv[a],"-?")==0) {
      cout << "runmaze usage:\nrunmaze [-p <secs>] [-v] [-s]\n";
      cout << "-v : show visualization";
      cout << "-p <sec> : pause for <sec> seconds each time a new node is visited.  Only valid with -v (visualization).  Default pause time is " << vizPauseSecs << "\n";
      cout << "-s : \"Show solution Mode\".  The input to the program will be a maze, followed by one or more solutions.  Show each solution.\n";
    }
    if (strcmp(argv[a],"-p")==0) {
      a++;
      if (a>=argc) {
	cout << "-p flag given, but <sec> did not follow.  Run with -h flag for help\n";
	exit(1);
      }
      vizPauseSecs=atof(argv[a]);
    }
    if (strcmp(argv[a],"-v")==0) {
      showViz=true;
    }
    if (strcmp(argv[a],"-s")==0) {
      showSoln=true;
      showViz = true;  // automatically sets up the viz, too.
    }
    a++;
  }
}

int main (int argc, char *argv[])
{
  SquareMaze *maze;

  bool showVisualization=false;
  bool showSolutionMode=false;
  double visualizationPauseInSec=0.1;

  // 221 STUDENTS: you may safely ignore (or even delete) the function
  // call to processCommandLineArguments.  We will test your program
  // with no command-line arguments.  (You are free to leave the call
  // here and, e.g., use the visualization for your debugging,
  // however!)
  processCommandLineArguments(argc, argv, 
			      showVisualization, 
			      showSolutionMode, 
			      visualizationPauseInSec);
	   

  // 221 STUDENTS: This reads a square maze from standard input.  You
  // can use this as is.  
  maze = new SquareMaze(cin);


  // 221 STUDENTS: You may safely ignore (or even delete) the
  // visualization code below.  Even if you would like to use it, it
  // should work with no changes on your part.

  // Set up the visualizer, if it has been requested.  If solution
  // visualization has been requested, then the input should include
  // solutions to the maze, and showing the solutions is all this
  // program will do.  (It will not also try to solve the maze.)
  //
  // WARNING: this code does not work on Windows
  VisualizeSquareMazeRunner *visualizer=NULL;
#ifndef WIN32
  if (showVisualization) {
    visualizer=new VisualizeSquareMazeRunner(maze);
    visualizer->setPauseTime(visualizationPauseInSec);
	  
    if (showSolutionMode) {
      while (!cin.eof()) {
	char solutionAlg[256];
	cin.getline(solutionAlg,256);
	if (cin.eof()) {
	  break;
	}
	cout << " Printing solution done by " << solutionAlg << " algorithm\n";
	int x,y;
	visualizer->startSolutionPath();
	while (SquareMaze::SquareMazeNode::readXY(cin,&x,&y)) {
	  visualizer->addNextInSolutionPath(x,y);
	  //cout << "(" << x << "," << y << ")\n";
	}
	visualizer->doneSolutionPath();

	cout << "Click mouse in solution display window to continue...\n";
	cout << flush;
	visualizer->waitForMouseClick();

      }
      return 0;
    }
  }
#endif


  MazeRunner *mazeRunner;

  // 221 STUDENTS: You'll want to use your own maze runner rather than
  // a RandomMazeRunner.  (Just change the constructor called on the
  // right side of the assignment statement.)  You'll also want to run
  // two mazerunners (which you can do by copying similar code below).

  HeapPQueue<MazeNode*,SquareMazeNodeCompare>* hpq = new HeapPQueue<MazeNode*,SquareMazeNodeCompare>();
  mazeRunner = new BestFSMazeRunner(hpq);
  mazeRunner->solveMaze(maze,cout);
  delete mazeRunner;

  // Clean up the maze, in case we need to run another maze runner.
  maze->reinitializeMaze();






  // Clean up and return.
  if (visualizer != NULL)
    delete visualizer;

  delete maze;

  return 0;
}
