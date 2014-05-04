/* 221 STUDENTS: YOU NEED NOT UNDERSTAND ANYTHING IN THIS FILE! */


#ifndef _VISUALIZESQUAREMAZERUNNER_H
#define _VISUALIZESQUAREMAZERUNNER_H

using namespace std;

/*
  VisualizeSquareMazeRunner.h.

  This visualizes the progress of a Maze runner working on a SquareMaze (although the MazeRunner itself doesn't know that it happens to be running on a SquareMaze).
*/

class VisualizeSquareMazeRunner : public MazeChangeListener {
 protected:
  SquareMaze *maze;
  double pauseTimeInSec;

  void pause (void);
  void drawInitialWindow (void);
  int getColor (MazeNode::VisitationState visitationState);
  void drawNode (int x,int y,int color);

  // constants we use
  static const int PixelsPerNode; // # of pixels along X and Y for each Node in the maze
  static const int BackgroundColor;
  static const int WallColor;
  static const int VisitedColor;
  static const int VisitInProgressColor;
  static const int NotVisitedColor;
  static const int SolutionColor;
 public:
  VisualizeSquareMazeRunner (SquareMaze *_maze);
  ~VisualizeSquareMazeRunner ();

  void setPauseTime (double _pauseTimeInSec);

  // from MazeChangeListener
  void visitationStateChanged (MazeNode *changedNode);

  // semi-hack, to allow display of solutions
  void startSolutionPath (void);
  void addNextInSolutionPath (int x,int y);
  void doneSolutionPath(void);

  void waitForMouseClick (void);
};

#endif
