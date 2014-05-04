Assignment 2

Wei (William) You
77610095

I have read and complied with the collaboration policy.

Questions:

1) Because if we allow thunks in concrete syntax, we will need some way
	to handle that in the interpretor.
	(fun {} (+ x y)) is now interpreted into an error is (first args) is
	not more valid. Although we can change the interpretor so it gets
	interp to a thunkV, but then when we do the final strict after 
	interpretation, the thunkV will get "unwrapped", which is not what we
	want.
	(fun {} (+ x y)) will produce an error of free indentifier when doing
	the final unwrap.

2) The expression {with* {{x <exp1>} {y <exp2>}} <body>} should be pre-process into
	{app {app {fun {x} {fun {y} <body>}} <exp1>} <exp2>} in case to get he right answer.

	{{fun {x y} <body>} <exp1> <exp2>} when interping this, you will have an environment where {x y} is bind with <exp1> and an extra argument while the function it self takes no more arguments. This will produce an error.

3) Frustrating part is to make sure every error is handled.
   Interesting part is to figure out how to bind the function argument to the app argument
   to make the environment.