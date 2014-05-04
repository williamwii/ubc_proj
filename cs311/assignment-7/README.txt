Philip Storey
94300076
l9n7

Wei You
77610095
r9e7

We have read and complied with the collaboration policy.

1.PPExpr does not include type information because PPExprs are only used after type checking has already been complete. 
Types have already been verified by this point and no longer matter.


2.
a. The default expression should be evaluated only when the function is applied, 
since there is no guarantee that it will be used. To evaluate it earlier would be inefficient. 
It should be evaluated in the environment that existed at the time the function was created.

b.  Г|- e2 : τ1, Г|- e3 : τ2
	-----------------------------
	Г|- (fun (e1:τ1  e2) e3) : τ2
	
c. closureV must contain one more field which is the default value.


3.Type checking seems very powerful in proving the correctness of your program. It reminds me of doing mathematical induction on algorithms.


Type Judgements

nnode:
Г|- e1 : number, Г|- e2 : ntree, Г|- e3 : ntree
---------------------------------------------
     Г|-(nnode e1 e2 e3) : ntree
  
  
nempty:
Г|-(nempty) : ntree


nempty?:
     Г|- e1 : ntree
--------------------------
Г|- (nempty? e1) : boolean


<:
Г|- e1 : number, Г|- e2 : number
------------------------------
   Г|- (< e1 e2) : boolean


rec:
Г(e1<-τ1)|-e3 : τ2, Г(e1<-τ1)|-e3 : τ2
--------------------------------------
    Г|- (rec (e1:τ1 e2) e3) : τ2