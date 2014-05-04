Philip Storey
94300076
l9n7

Wei You
77610095
r9e7

I have read and complied with the collaboration policy.

1. The first execute takes longer because Haskell "remembers" the results after evaluating them the first time. It is perhaps caching the generated list.

2. It takes the same amount of time for running both times. Haskell only caches lists and the first isPrime function does not check if the 104729 is a prime by checking it against a list of primes.

3. (with (x y) (with (y 1) x))
	static = unbound identifier y
	dynamic = 1

4. 	a. We only get the value for read-eval from the user when it needs to be evaluated and we can’t predict user input.
	b. The body of function complex computation is not know, it might be some really complex computation that we can not compute by hand. This will make it impossible to predict the exact value bounded to x.
	c. We can only know the output of this statement if we know the order of computation of the multiplication function (left to right or right to left).

5. 	a. Static = Unbound identifier countdown
	   Dynamic = 0
	b. Static = Unbound identifier countdown
	   Dynamic = Unbound identifier countdown

6. Haskell will have to check all 1000000000 values to get result because we are demanding it give us a full list.

7. with* will produce different output after pre-processing with different method. For example :
(with* ( (x 1) (y 2) ) (+ x y))

under the first method, this will become
(with (x y) (with (y 2) (+ x y))) then ((fun (x) ((fun (y) (+ x y)) 2) y)

with the second method, it will become
( (fun ‘(x y) (+ x y)) y 2) then ((fun (x) (fun (y) (+ x y))) y) 2)

The first method will result an error of unbounded identifier of y, and second one will evaluate to 4, because for the first one, the named-expr of with are evaluated with different environments where the second method is evaluating named-expr in same environment (all bindings).

The second method is pre-processing the with* expression into the expression provided in P5.1(d)

8. Haskell is really good for implementing simple games like Mancala. I like it a lot.