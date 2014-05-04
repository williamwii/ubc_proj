{- 
Philip Storey
94300076
l9n7

Wei You
77610095
r9e7
-}

module Laziness (divides, isPrime, isPrime2, primes, mkTree, mancala, prune, minimax, level) where

import Game
import Test.HUnit

-- PROBLEM 1

-- divides x n is True iff x evenly divides n.
divides :: Integer -> Integer -> Bool
divides x y
	| y `mod` x == 0	= True
	| otherwise    		= False

divTests = test [ "div1" ~: "(divides 2 4)" ~: (divides 2 4) ~=? True,
	   	  "div2" ~: "(divides 3 4)" ~: (divides 3 4) ~=? False,
		  "div3" ~: "(divides 4 100)" ~: (divides 4 100) ~=? True]

-- isPrime n is True if n is a prime number
isPrime :: Integer -> Bool
isPrime x
	| listDivisors 1 x == [1, x] = True
	| otherwise    	      	     = False

-- listDivisors y x checks to see if y evenly divides x. If so, it adds it to the list of divisors.
listDivisors :: Integer -> Integer -> [Integer]
listDivisors y x
	     | y > x		= []
	     | divides y x	= y:listDivisors (y+1) x
	     | otherwise 	= listDivisors (y+1) x

ipTests = test [ "isPrime1" ~: "(isPrime 2)" ~: (isPrime 2) ~=? True,
	       	 "isPrime2" ~: "(isPrime 3)" ~: (isPrime 3) ~=? True,
		 "isPrime3" ~: "(isPrime 4)" ~: (isPrime 4) ~=? False,
		 "isPrime4" ~: "(isPrime 661)" ~: (isPrime 661) ~=? True]

-- primes is a list of all the prime numbers
primes :: [Integer]
primes = [x | x <- [1..], (isPrime x)]

primesTest = test ["primes1" ~: "(take 3 primes)" ~: (take 3 primes) ~=? [2, 3, 5]]

-- isPrime2 n is True iff n is a prime number, but it uses primes
isPrime2 :: Integer -> Bool
isPrime2 x
	| x `elem` takeWhile (<=x) primes	= True 
	| otherwise		   		= False

-- PROBLEM 2

-- NOTE: it may be harder to build good test cases here.  Try to do so
-- at least for prune and minimax!

-- This is the GameTree datatype you should define.
-- Remember: "Each node should have its current configuration 
-- and a list of trees, where each tree corresponds to the game 
-- states obtainable after making any one legal move."

data GameTree = GameTree GameState [GameTree] deriving (Show)

-- mkTree s yields the complete GameTree whose root has state s
mkTree :: GameState -> GameTree
mkTree myState = GameTree myState (map mkTree (nextStates myState))

-- mancala is the entire game of mancala as a GameTree.  Note that PlayerA
-- goes first.
mancala :: GameTree
mancala = mkTree (initialState PlayerA)

-- prune n gt with n > 0 yields a GameTree equivalent to gt up to depth n,
-- but with no subtrees below depth n.
-- Note: prune 0 gt is nonsense; do not provide an equation for prune 0 gt.
prune :: Int -> GameTree -> GameTree
prune x myGameTree
      | x == 1			= GameTree (getStateFromTree myGameTree) []
      | otherwise		= GameTree (getStateFromTree myGameTree) (map (prune (x-1)) (getTreesFromTree myGameTree))

getStateFromTree :: GameTree -> GameState
getStateFromTree (GameTree myState _) = myState

getTreesFromTree :: GameTree -> [GameTree]
getTreesFromTree (GameTree _ myTree) = myTree

-- NOTE: you may need to replace "GameTree" in this test with something else
-- to make it work, depending on how you defined your data type!
pruneTests = test [ "total-prune" ~: "(prune 1 mancala)" ~: 
	   (null (case (prune 1 mancala) of GameTree s ts -> ts)) ~=? True ]

-- minimax gt yields the minimaxed gameValue of the given GameTree.  The value
-- of a node with no children is the gameValue of its GameState.  
-- If the node has children and it's PlayerA's turn, then A can choose the
-- child state with maximum value.  If the node has no children and it's 
-- PlayerB's turn, then B can choose the child state with minimum value.
-- Together, these rules define the value of any (finite) game tree.
minimax :: GameTree -> GameValue
minimax gt
	| null (getTreesFromTree gt)				= gameValue (getStateFromTree gt)
	| (getPlayer (getStateFromTree gt)) == PlayerA		= maximum (map minimax (getTreesFromTree gt))
	| otherwise    		       				= minimum (map minimax (getTreesFromTree gt))

-- level n simulates a game of mancala such that PlayerA (who goes first) is
-- controlled by your minimax AI, with n levels of lookahead.  We have written
-- this one for you.
level :: Int -> IO ()
level n = simulateAIGame (minimax . (prune n) . mkTree)


tests = TestList [divTests, ipTests, pruneTests, primesTest]
-- TODO: add your tests here as you create them!
-- use:
--   runTestTT tests 
-- to run them.