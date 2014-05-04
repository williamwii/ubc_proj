#ifndef _SQUAREMAZE_H
#define _SQUAREMAZE_H

using namespace std;

/*
  SquareMaze.h
  The classes in Maze.h decribe abstract mazes.  The classes in this
  file declares concrete implementations of those abstract classes
  in order to represent square mazes.

  221 STUDENTS: You need only use this class as it is already used in
  runmaze.cpp to load a maze file (and work with the visualization, if
  you choose to).

  Many of these classes are "friends" of each other.  This is because
  they are all part of the exact same implementation. But, they merely present
  different views of the same information, for the convenience of other classes.
*/

class SquareMaze : public Maze {
 public:
  class SquareMazeNode; // forward declaration of our nested class
 protected:

  int width,height;

  SquareMazeNode *nodes;

  // define the Start
  SquareMazeNode *startNode;
  // define the Exit
  SquareMazeNode *exitNode;

  // convenience function to get a node*, given (x,y)
  SquareMazeNode *getNode (int x,int y);

  // sub-routines used to read in from file
  void ParseSizeLine(istream& inData);
  void ReadActualMaze(istream& inData);
 public:
  // construct a SquareMaze from a file, using the format described in the Project 1 handout
  SquareMaze (istream& inData);

  // dump maze in computer-oriented format, for debugging
  void DebugDump (ostream& out);

  ~SquareMaze();

  // methods from abstract base class
  MazeNode *getStartMazeNode (void);
  MazeNodeIterator *getAllMazeNodes (void);
  int getNumNodes (void);
  int getMaxNeighborsForNode (void);

  // things for classes that want to know that this is a square maze (like the visualizer)
  enum Directions { Left=0,Right=1,Up=2,Down=3 };
  int getWidth (void);
  int getHeight (void);
  SquareMazeNode *getNodeAt (int x,int y);

 protected: // our nested iterator classes are not public - you can only access them via the abstract classes defined in Maze.h

  // AllNodesIterator nested class iterates through all nodes
  class AllNodesIterator : public MazeNodeIterator {
    friend class SquareMaze; // SquareMaze knows about this class, mainly so it can call the protected constructor
  protected:
    SquareMaze *maze;
    int x,y; // current x,y position

    AllNodesIterator (SquareMaze *_maze);
  public:
    ~AllNodesIterator ();

    // methods from abstract base class
    bool hasNext (void);
    MazeNode * next (void);
  };
  friend class AllNodesIterator; // AllNodesIterator knows about the internals of how the SquareMaze is implemented

 public: // but some classes (e.g. visualizer) may want to see the SquareMazeNode
  // here's our node class, which is also nested
  class SquareMazeNode : public MazeNode {
    friend class SquareMaze; // SquareMaze knows about this class, mainly so it can call the protected constructor
  protected:
    SquareMaze *maze;
    int x,y; // x,y coordinates of this node, so we know what it's neighbors are
    bool canGoDirs[4]; // array of whether or not we can go in each of the 4 directions

    SquareMazeNode (); // must set members 'maze', 'x', 'y'
  public:
    ~SquareMazeNode ();

    // methods from abstract base class
    bool isExitNode (void);
    MazeNodeIterator *getNeighbors (void);
    void setVisitationState (VisitationState newVisitationState);
    void print (ostream& out);

    // things for classes that want to know that this is a square maze (like the visualizer)
    int getX (void);
    int getY (void);
    bool canGoLeft (void);
    bool canGoRight (void);
    bool canGoUp (void);
    bool canGoDown (void);

    // read the x,y coordinates we outputted with our print method
    // returns true iff the input had a legitimate print out (in particular, it'll return false if there was an end-of-line character
    static bool /* got node */ readXY (istream& inData,int *x,int *y);

    // the NeighborIterator class, nested
  protected: 
    class NeighborIterator : public MazeNodeIterator {
      friend class SquareMazeNode; // SquareMazeNode knows about this class, mainly so it can call the protected constructor
    protected:
      SquareMazeNode *node,*nextNode;
      int currDir;

      NeighborIterator (SquareMazeNode *_node);

      // convenience function, set nextNode based on currDir and node
      void ComputeNextNode(void);
    public:
      ~NeighborIterator ();

      // methods from abstract base class
      bool hasNext (void);
      MazeNode * next (void);
    };
    friend class NeighborIterator; // NeighborIterator knows about the internals of SquareMazeNode
  };
  friend class SquareMazeNode; // SquareMazeNode knows about the internals of how the SquareMaze is implemented
  friend class SquareMazeNode::NeighborIterator; // NeighborIterator knows about the internals of SquareMaze
};

#endif
