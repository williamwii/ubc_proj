Philip Storey
94300076
l9n7

Wei You
77610095
r9e7

We have read and complied with the collaboration policy.

**Did bonus, but nor sure if it is right**

Problem 1

1. In continuation passing style, all computations are tail calls, which means that nothing is added to the stack at each point in the main computation taking place.

2. Not all code should be converted to continuation passing style because it is not always required. If you have written a program that is not in continuation passing style and still does not use up too much stack space, then there is little reason to convert it. It would be extra work with little benefit. Furthermore, not all programming languages are optimized for tail calls. If this is the case, then it becomes much more difficult to convert to continuation passing style. Also, CPS requires access to the entire program. If a procedure call is built in the language, CPS translator will either fail to run or will produce potential erroneous output.

3.	A. Once a function returns, the environment that it was calculated in is no longer active, thus the dynamic scope no longer matches it.
	B. Threaded scope matches for the rest of the program because anything done in a static scope does not effect lines of code executed later. After calling bless, not all lines of check will automatically work. Only those that are in the bless will evaluate to 0. Anything later that is outside the bless will fail unless another bless is called.
	C. Any recursive calls to interp inside of a bless will also be blessed. Dynamic scope remains until the function returns.
	D. Threaded scope matches until a continuation is called and the code jumps back to the environment that it had when the continuation was created. This essentially erases all threaded scope that has been created in the meantime.
	E. When we evaluate a function, the static scope persists until the matching closing brace is hit. Things can only be added to the static scope.
	F. Once the continuation that was passed in has been applied, the environment of the program returns to where the continuation was originally called from. Any work done since that point is no longer applicable and therefor the dynamic scope no longer matches.
	G. The abstract syntax tree has a very clearly defined hierarchy. There is not way to jump around in it and therefore static scope matches at each subtree of a node.

4. R. interpreting: {{fun {f} {+ {f 0} 0}} {fun {ignore} deepness}} 
   in [deepness -> 0]

R.a interpreting: {fun {f} {+ {f 0} 0}}
    in 	[deepness -> 0]
[deepness -> 1]

R.b interpreting: {fun {ignore} deepness}
    in 	[deepness -> 0]
[deepness -> 1]
	
R.c interpreting: {+ {f 0} 0}
    in 	[deepness -> 0]
[deepness -> 1]
	[deepness -> 2]
[f -> {closureV ignore deepness (anEnv deepness 2 (anEnv deepness 1 (anEnv deepness 0 (mtEnv))))) }]

R.c.a interpreting {f 0}
    in 	[deepness -> 0]
[deepness -> 1]
	[deepness -> 2]
[f -> {closureV ignore deepness (anEnv deepness 2 (anEnv deepness 1 (anEnv deepness 0 (mtEnv))))) }]
[deepness -> 3]

R.c.a.a interpreting f
    in 	[deepness -> 0]
[deepness -> 1]
	[deepness -> 2]
[f -> {closureV ignore deepness (anEnv deepness 2 (anEnv deepness 1 (anEnv deepness 0 (mtEnv))))) }]
[deepness -> 3]
[deepness -> 4]

R.c.a.b interpreting 0
    in	[deepness -> 0]
[deepness -> 1]
	[deepness -> 2]
[f -> {closureV ignore deepness (anEnv deepness 2 (anEnv deepness 1 (anEnv deepness 0 (mtEnv))))) }]
[deepness -> 3]
[deepness -> 4]

R.c.a.c interpreting deepness
    in 	[deepness -> 0]
[deepness -> 1]
	[deepness -> 2]
[deepness -> 3]

R.c.b interpreting 0
   in 	[deepness -> 0]
[deepness -> 1]
	[deepness -> 2]
[f -> {closureV ignore deepness (anEnv deepness 2 (anEnv deepness 1 (anEnv deepness 0 (mtEnv))))) }]
[deepness -> 3]

5. The definition is not truly in continuation passing style. It will evaluate the continuation at every call instead of just the final one, which will call the printf statement at each level. This will evaluate to void regardless of the symbol its trying to evaluate, which is not and therefore true. When the and is called on void, it will come back true. Thereby the function will always return true.

6. Error. numV is not an CFWAE expression, it is a CFWAE-Value, which cannot be closed over.

7. It is inconsistent with static scope because foo would be unbound when it is evaluated in the line \cbdSemA{\foo}. foo is only bound within the function cbdSemC under static scope. However, neither should this be bound under dynamic scope, since cbdSemC doesn¡¯t call any other method to allow this binding to exist outside of the cbdSemC function closing bracket.