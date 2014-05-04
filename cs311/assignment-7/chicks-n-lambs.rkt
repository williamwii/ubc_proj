#lang plai
(print-only-errors)

;;Philip Storey
;;94300076
;;l9n7

;;Wei You
;;77610095
;;r9e7

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Problem definition and TODO items.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;; You're type-checking and interpreting a new language 
;; with binary trees, booleans, numbers, and recursion.
;; However, a simple interpreter is already available.
;; It doesn't know how to handle booleans, recursion, or
;; trees; so, you'll write a preprocessor to eliminate all
;; of those.

;; In particular, you must:
;; - Write type judgments for nnode, nempty?, nempty, and <.
;; - Complete the pre-processor (the pre-process function).
;; - Complete the pre-processor's test cases.
;; - Complete the type-of-tenv function.
;;
;; Each of these is marked with TODO below.

;; Here's the language's EBNF:
;; <expr> ::= <num>
;;
;;          | <id>
;;          | {with {<id> : <type> <expr>} <expr>}
;;          | {rec {<id> : <type> <expr>} <expr>}
;;          | {fun {<id> : <type>} <expr>}
;;          | {<expr> <expr>}               ;; function application
;;
;;          | {+ <expr> <expr>}
;;          | {- <expr> <expr>}
;;          | {* <expr> <expr>}
;;          | {/ <expr> <expr>}
;;
;;          | {< <expr> <expr>}
;;          | true
;;          | false
;;          | bif
;;
;;          | {nnode <expr> <expr> <expr>}  ;; that's value, then lhs, then rhs
;;          | nempty
;;          | {nleft <expr>}
;;          | {nright <expr>}
;;          | {nempty? <expr>}
;;          | {nvalue <expr>}

;; The language for types:
;; <type> ::= number
;;          | boolean
;;          | ntree
;;          | (<type> -> <type>)   
;;
;; Note: In the concrete syntax for types, base types are represented
;; by symbols, and the arrow type by a Racket list of three elements:
;; the type of the argument, the symbol ->, and the type of the
;; result.

;; TODO: Be sure (as demanded in the assignment) to write out type
;; judgments for nnode, nempty, nempty?, <, and rec in your README!
;; Use plain ascii characters to represent special symbols: ->, <-, and |-.
;;
;; It will help to jot down type judgments for the other constructs,
;; at least on paper!

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Key define-types.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; A "raw" AST (expression) type:
(define-type Expr
  [num (n number?)]
  [id (name symbol?)]
  [bool (b boolean?)]
  [bin-num-op (op symbol?) (lhs Expr?) (rhs Expr?)]
  [less (lhs Expr?) (rhs Expr?)]
  [bif (test Expr?) (then Expr?) (else Expr?)]
  [with (bound-id symbol?) (bound-type Type?) (bound-body Expr?) (body Expr?)]
  [rec (bound-id symbol?) (bound-type Type?) (bound-body Expr?) (body Expr?)]
  [fun (arg-id symbol?) (arg-type Type?) (body Expr?)]
  [app (fun-expr Expr?) (arg-expr Expr?)]
  [nempty]
  [nnode (value Expr?) (left Expr?) (right Expr?)]
  [nvalue (e Expr?)]
  [nleft (e Expr?)]
  [nright (e Expr?)]
  [isnempty (e Expr?)])

;; The available types.
(define-type Type
  [t-num]
  [t-bool]
  [t-ntree]
  [t-fun (arg Type?) (result Type?)])


;; A pre-processed AST (expression) type.
;;
;; Note the availability of ppbottom to represent expressions whose
;; values should never be used (e.g., the "left" and "right" of an
;; empty tree).
;;
;; Also note that it lacks many constructs AND all type information.
(define-type PPExpr
  [ppnum (n number?)]
  [ppid (name symbol?)]
  [ppbin-num-op (op symbol?) (lhs PPExpr?) (rhs PPExpr?)]
  [ppless (lhs PPExpr?) (rhs PPExpr?)]
  [ppfun (arg-id symbol?) (body PPExpr?)]
  [ppapp (fun-expr PPExpr?) (arg-expr PPExpr?)]
  [ppbottom (text string?)]) 


;; Values during execution.
;; Note again that there are no trees or booleans here.
;; We have added a bottomV (illegal) value that carries
;; an error string along with it.
(define-type Value
  [numV (n number?)]
  [closureV (param symbol?)
            (body PPExpr?)
            (env Env?)]
  [bottomV (text string?)])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Environment (for values)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(define-type Env
  [mtEnv]
  [anEnv (name symbol?) (value Value?) (env Env?)])

;; lookup : symbol Env -> (or/c boolean CFWAE-Value)
;; Consumes a symbol and an environment in which to find that
;; symbol.  Produces either the symbol's bound value or
;; false if the symbol is not found.
(define (lookup id env)
  (type-case Env env
    [mtEnv () #f]
    [anEnv (name value env)
           (if (symbol=? id name)
               (if (bottomV? value)
                   (error 'lookup (format "Attempted to fetch bottom value associated with ~a; error text was: ~a" id
                                          (bottomV-text value)))
                   value)
               (lookup id env))]))

(test (lookup 'x (mtEnv)) false)
(test (lookup 'x (anEnv 'x (numV 1) (mtEnv))) (numV 1))
(test (lookup 'x (anEnv 'y (numV 1) (anEnv 'x (numV 2) (mtEnv)))) (numV 2))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Helper functions for binary operators
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Global table of operators.
(define *op-table*
  `((+ ,+)
    (- ,-)
    (* ,*)
    (/ ,(lambda (x y) (if (= y 0) (error '/ "Division by zero") (/ x y))))))

;; operator-symbol? any -> boolean
;; Consumes anything and reports whether it is the symbol
;; for an operator in the global operator table.
(define (operator-symbol? x)
  (not (eq? false (assoc x *op-table*))))

;; make-op : operator-symbol -> (Value Value -> Value)
;; Consumes a symbol for a binary operator and produces
;; a function ready to operate on the language's values.
(define (make-op op-symbol)
  (lambda (arg1 arg2)
    (numV ((second (assoc op-symbol *op-table*)) (numV-n arg1) (numV-n arg2)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Parsing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; reserved-word? : any -> boolean
;; Determines whether the given value is a reserved word in
;; the language.  Note that "undefined" is available to you
;; if you need to name variables with something users cannot
;; touch.
(define (reserved-word? x)
  (or (member x '(true false : -> < bif with fun nempty nnode
                       nleft nright nempty? rec number boolean ntree 
                       undefined))
      (operator-symbol? x)))

;; valid-id? : any -> boolean
;; Determines whether the given object is a valid identifier,
;; i.e., a symbol and not a reserved word.
(define (valid-id? x)
  (and (symbol? x)
       (not (reserved-word? x))))

;; parse-type : sexp -> Type
;; Consumes an sexp representing a type and produces the
;; corresponding type.  Reports errors if there are any.
(define (parse-type expr)
  (match expr
    ['number (t-num)]
    ['boolean (t-bool)]
    ['ntree (t-ntree)]
    [(list tl '-> tr)
     (t-fun (parse-type tl) (parse-type tr))]
    [_ (error 'parse-type (format "Unable to parse type: ~a" expr))]))

(test (parse-type 'number) (t-num))
(test (parse-type 'boolean) (t-bool))
(test (parse-type 'ntree) (t-ntree))
(test (parse-type '(number -> boolean)) (t-fun (t-num) (t-bool)))
(test/exn (parse-type '(number ->)) "")
(test/exn (parse-type 'random) "")

;; parse : sexp -> Expr
;; Consumes an sexp and generates the corresponding Expr.
;; Does thorough error checking on PARSING errors.  (For 
;; example, produces an error if any reserved word is used
;; as if it were a normal identifier anywhere BUT produces
;; no error if a literal number is applied as if it were
;; a function.)
(define (parse expr)
  {match expr
    [(? number?) (num expr)]
    [(? valid-id?) (id expr)]
    ['true (bool true)]
    ['false (bool false)]
    [(list (and op (? operator-symbol?)) lhs rhs) 
     (bin-num-op op (parse lhs) (parse rhs))]
    [(list '< lhs rhs) (less (parse lhs) (parse rhs))]
    [(list 'bif c t e) (bif (parse c) (parse t) (parse e))]
    [(list 'with (list (and id (? valid-id?)) ': type bound-b) body)
     (with id (parse-type type) (parse bound-b) (parse body))]
    [(list 'rec (list (and id (? valid-id?)) ': type bound-b) body)
     (rec id (parse-type type) (parse bound-b) (parse body))]
    [(list 'fun (list (and id (? valid-id?)) ': type) body)
     (fun id (parse-type type) (parse body))]
    ['nempty (nempty)]
    [(list 'nnode val lhs rhs) (nnode (parse val) (parse lhs) (parse rhs))]
    [(list 'nvalue exp) (nvalue (parse exp))]
    [(list 'nleft exp) (nleft (parse exp))]
    [(list 'nright exp) (nright (parse exp))]
    [(list 'nempty? exp) (isnempty (parse exp))]
    
    [(list (and word (? symbol?) (not (? valid-id?))) _)
     (error 'parse (format "Misused reserved word ~a in: ~a" word expr))]
    
    [(list f a) (app (parse f) (parse a))]
    
    [_ (error 'parse "Unable to match expr: ~a" expr)]})


(test (parse '1) (num 1))

(test (parse 'x) (id 'x))

(test/exn (parse 'fun) "")
(test/exn (parse '+) "")
(test/exn (parse '->) "")

(test (parse 'true) (bool true))
(test (parse 'false) (bool false))
(test (parse '{+ 1 2}) (bin-num-op '+ (num 1) (num 2)))

(test (parse '{< 1 2}) (less (num 1) (num 2)))
(test (parse '{bif true 1 2}) (bif (bool true) (num 1) (num 2)))
(test (parse '{with {x : number 1} 2}) (with 'x (t-num) (num 1) (num 2)))
(test (parse '{rec {x : number 1} 2}) (rec 'x (t-num) (num 1) (num 2)))
(test (parse '{fun {x : number} 1}) (fun 'x (t-num) (num 1)))
(test (parse '{x y}) (app (id 'x) (id 'y)))
(test (parse '{{fun {x : number} 1} 2}) (app (fun 'x (t-num) (num 1)) (num 2)))

(test (parse 'nempty) (nempty))
(test (parse '{nnode 1 nempty nempty}) (nnode (num 1) (nempty) (nempty)))
(test (parse '{nleft nempty}) (nleft (nempty)))
(test (parse '{nvalue nempty}) (nvalue (nempty)))
(test (parse '{nright nempty}) (nright (nempty)))
(test (parse '{nempty? nempty}) (isnempty (nempty)))

;; Stand-in for all wrong lengths/formats.
(test/exn (parse '{bif true 1 2 3}) "")
(test/exn (parse '{+ 1 2 3}) "")
(test/exn (parse '{with {fun : number 1} 1}) "")
(test/exn (parse '{with fun : number 1 1}) "")
(test/exn (parse '{rec {fun : number 1} 1}) "")
(test/exn (parse '{rec fun : number 1 1}) "")
(test/exn (parse '{fun {fun} 1}) "")
(test/exn (parse '{fun {x y} 1}) "")
(test/exn (parse '{fun {x} 1 2}) "")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Pre-processing
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; pre-process : Expr -> PPExpr
;; Consumes a (raw) Expr and produces a pre-processed PPExpr.
;; Eliminates all with, rec, bif, true, false, nnode, nempty,
;; nempty?, nleft, and nright expressions.  Also discards 
;; type information.  
(define (pre-process expr)
  (type-case Expr expr
    [num (n) (ppnum n)]
    [id (name) (ppid name)]
    [bin-num-op (op lhs rhs) (ppbin-num-op op (pre-process lhs) (pre-process rhs))]
    [less (lhs rhs) (ppless (pre-process lhs) (pre-process rhs))]
    [app (fe ae) (ppapp (pre-process fe) (pre-process ae))]
    [fun (arg type body) (ppfun arg (pre-process body))]
    
    ;; Provided as an example.  Note that you will often need to
    ;; recursively call pre-process on the structures you create.
    [with (arg type bound-body body) 
          (pre-process (app (fun arg type body) bound-body))]
    
    ;; The rest are TODO.
    [rec (arg type bound-body body) (local ([define bounded-body (app fix (fun arg type bound-body))])
                                      (pre-process (with arg type bounded-body body)))]
    
    [bool (b) (if (eq? b true) (ppfun 't (ppfun 'f (ppapp (ppid 't) (ppnum 1)))) (ppfun 't (ppfun 'f (ppapp (ppid 'f) (ppnum 1)))))]
    [bif (c t e) (ppapp (ppapp (pre-process c) (ppfun 'ignore (pre-process t))) (ppfun 'ignore (pre-process e)))]
    [nempty () (ppfun 'the-fun (ppapp (ppapp (ppapp (ppapp (ppid 'the-fun) (ppnum 0)) (ppnum 0)) (ppnum 0)) (pre-process (bool true))))]
    [nnode (val lhs rhs) (ppfun 'the-fun (ppapp (ppapp (ppapp (ppapp (ppid 'the-fun)  (pre-process val)) (pre-process lhs)) (pre-process rhs)) (pre-process (bool false))))]
    [isnempty (e) (local ([define pre-processed-node (pre-process e)])
                    (ppapp pre-processed-node 
                           (ppfun 'x (ppfun 'y (ppfun 'z (ppfun 'a (ppid 'a)))))))]
    
    [nvalue (e) (ppapp (ppapp (pre-process (isnempty e)) 
                              (ppfun 'ignore 
                                     (ppapp (ppfun 'undefined (ppid 'undefined)) (ppbottom "Bottom"))))
                              (ppfun 'ignore 
                                     (ppapp (pre-process e) (ppfun 'x (ppfun 'y (ppfun 'z (ppfun 'a (ppid 'x))))))))]
    [nleft (e) (ppapp (ppapp (pre-process (isnempty e)) 
                              (ppfun 'ignore 
                                     (ppapp (ppfun 'undefined (ppid 'undefined)) (ppbottom "Bottom"))))
                              (ppfun 'ignore 
                                     (ppapp (pre-process e) (ppfun 'x (ppfun 'y (ppfun 'z (ppfun 'a (ppid 'y))))))))]
    [nright (e) (ppapp (ppapp (pre-process (isnempty e)) 
                              (ppfun 'ignore 
                                     (ppapp (ppfun 'undefined (ppid 'undefined)) (ppbottom "Bottom"))))
                              (ppfun 'ignore 
                                     (ppapp (pre-process e) (ppfun 'x (ppfun 'y (ppfun 'z (ppfun 'a (ppid 'z))))))))]))

;; The Y combinator (fixpoint function).   An expression like:

#;
(ppapp (pre-process fix) (ppfun 'fact (ppfun 'n (...))))

;; produces the actual factorial function, a function that consumes a number and
;; produces the factorial of that number.  (Assuming you fill in the (...) with the
;; body of the function.)
;;
;; Note: our version of fix works with eager evaluation but assumes it's applied to 
;; a function that takes a function (itself) as an argument and then takes its "true" 
;; argument.  
(define fix
  (with 'fix-maker (t-num)
        (fun 'fix-maker (t-num)
             (fun 'f (t-num)
                  (app (id 'f) (fun 'x (t-num)
                                    (app (app (app (id 'fix-maker) (id 'fix-maker)) (id 'f)) (id 'x))))))
        (app (id 'fix-maker) (id 'fix-maker))))

(test ((compose pre-process parse) '1) (ppnum 1))
(test ((compose pre-process parse) 'x) (ppid 'x))
(test ((compose pre-process parse) '{+ 1 2}) (ppbin-num-op '+ (ppnum 1) (ppnum 2)))
(test ((compose pre-process parse) '{< 1 2}) (ppless (ppnum 1) (ppnum 2)))
(test ((compose pre-process parse) '{x y}) (ppapp (ppid 'x) (ppid 'y)))

(test ((compose pre-process parse) '{fun {x : number} 1}) (ppfun 'x (ppnum 1)))

(test ((compose pre-process parse) '{with {x : number 1} 2}) (ppapp (ppfun 'x (ppnum 2)) (ppnum 1)))

;; The remaining tests are TODO (because we don't know how you'll implement these!).
(test ((compose pre-process parse) '{rec {x : {number -> number}
                                            {{fun {y : {number -> number}} y}
                                             {fun {z : number} {x z}}}}
                                      1})
      (ppapp
       (ppfun 'x (ppnum 1))
       (ppapp
        (ppapp
         (ppfun 'fix-maker (ppapp (ppid 'fix-maker) (ppid 'fix-maker)))
         (ppfun 'fix-maker (ppfun 'f (ppapp (ppid 'f) (ppfun 'x (ppapp (ppapp (ppapp (ppid 'fix-maker) (ppid 'fix-maker)) (ppid 'f)) (ppid 'x)))))))
        (ppfun 'x (ppapp (ppfun 'y (ppid 'y)) (ppfun 'z (ppapp (ppid 'x) (ppid 'z))))))))

(test ((compose pre-process parse) 'true) (ppfun 't (ppfun 'f (ppapp (ppid 't) (ppnum 1)))))
(test ((compose pre-process parse) 'false) (ppfun 't (ppfun 'f (ppapp (ppid 'f) (ppnum 1)))))
(test ((compose pre-process parse) '{bif 'x 1 2}) (ppapp (ppapp (ppapp (ppid 'quote) (ppid 'x)) (ppfun 'ignore (ppnum 1))) (ppfun 'ignore (ppnum 2))))

(test ((compose pre-process parse) 'nempty) (ppfun 'the-fun (ppapp (ppapp (ppapp (ppapp (ppid 'the-fun) (ppnum 0)) (ppnum 0)) (ppnum 0)) (ppfun 't (ppfun 'f (ppapp (ppid 't) (ppnum 1)))))))

(test ((compose pre-process parse) '{nnode 1 x y}) (ppfun 'the-fun (ppapp (ppapp (ppapp (ppapp (ppid 'the-fun) (ppnum 1)) (ppid 'x)) (ppid 'y)) (ppfun 't (ppfun 'f (ppapp (ppid 'f) (ppnum 1)))))))

(test ((compose pre-process parse) '{nvalue x}) (ppapp (ppapp (ppapp (ppid 'x) (ppfun 'x (ppfun 'y (ppfun 'z (ppfun 'a (ppid 'a)))))) (ppfun 'ignore (ppapp (ppfun 'undefined (ppid 'undefined)) (ppbottom "Bottom")))) (ppfun 'ignore (ppapp (ppid 'x) (ppfun 'x (ppfun 'y (ppfun 'z (ppfun 'a (ppid 'x)))))))))

(test ((compose pre-process parse) '{nleft x}) (ppapp (ppapp (ppapp (ppid 'x) (ppfun 'x (ppfun 'y (ppfun 'z (ppfun 'a (ppid 'a)))))) (ppfun 'ignore (ppapp (ppfun 'undefined (ppid 'undefined)) (ppbottom "Bottom")))) (ppfun 'ignore (ppapp (ppid 'x) (ppfun 'x (ppfun 'y (ppfun 'z (ppfun 'a (ppid 'y)))))))))

(test ((compose pre-process parse) '{nright x}) (ppapp (ppapp (ppapp (ppid 'x) (ppfun 'x (ppfun 'y (ppfun 'z (ppfun 'a (ppid 'a)))))) (ppfun 'ignore (ppapp (ppfun 'undefined (ppid 'undefined)) (ppbottom "Bottom")))) (ppfun 'ignore (ppapp (ppid 'x) (ppfun 'x (ppfun 'y (ppfun 'z (ppfun 'a (ppid 'z)))))))))

(test ((compose pre-process parse) '{nempty? x}) (ppapp (ppid 'x) (ppfun 'x (ppfun 'y (ppfun 'z (ppfun 'a (ppid 'a)))))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Interpretation 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; interp-env : PPExpr -> Value
;; Interprets the given expression and produces a result
;; in the form of a Value (which should be a closureV or
;; a numV).
(define (interp-env expr env)
  (type-case PPExpr expr
    [ppnum (n) (numV n)]
    [ppbin-num-op (op lhs rhs)
                  ((make-op op) (interp-env lhs env) (interp-env rhs env))]
    [ppid (name) (lookup name env)]
    [ppless (lhs rhs)
            (local ([define result (< (numV-n (interp-env lhs env)) (numV-n (interp-env rhs env)))])
              (interp-env (pre-process (parse (if result 'true 'false))) (mtEnv)))]
    [ppapp (fe ae) 
           (local ([define the-fun (interp-env fe env)]
                   [define the-arg (interp-env ae env)])
             (interp-env (closureV-body the-fun)
                         (anEnv (closureV-param the-fun)
                                the-arg
                                (closureV-env the-fun))))]
    [ppfun (arg body) (closureV arg body env)]
    [ppbottom (text) (bottomV text)]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Type Environment and type helper functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; The type environment, binding identifiers to their static types.
(define-type TEnv
  [mtTEnv]
  [aTEnv (name symbol?) (type Type?) (env TEnv?)])

;; tlookup : symbol TEnv -> (or/c boolean Type)
;; Consumes a symbol and an environment in which to find that
;; symbol.  Produces either the symbol's bound type or
;; false if the symbol is not found.
(define (tlookup id tenv)
  (type-case TEnv tenv
    [mtTEnv () #f]
    [aTEnv (name value tenv)
           (if (symbol=? id name)
               value
               (tlookup id tenv))]))

;; tassert : Type Type (optional string) -> void
;; Consumes a pair of types.  If they're the same, does nothing.
;; If they're different, reports an error using the given format string
;; (but it has a somewhat sensible default).  If you want a BETTER
;; format string (which can help immensely with debugging!), be sure
;; to include two instances of ~a in it.  The first will be replaced
;; by type1, the second by type2.
(define (tassert type1 type2 (text "Type error, expected ~a but received ~a"))
  (if (equal? type1 type2)
      (void)
      (error 'tassert (format text type1 type2))))

;; type-of-tenv : Expr TEnv -> Type
;; Consumes an expression and the current type environment
;; and deduces the type of the expression.  Generates an
;; error if the expression includes a type error (including
;; unbound identifiers).
;; The available types.
;;(define-type Type
;;  [t-num]
;;  [t-bool]
;;  [t-ntree]
;;  [t-fun (arg Type?) (result Type?)])
(define (type-of-tenv exp tenv)
  (type-case Expr exp
    [num (n) (t-num)]  ;; Left as an example.
    [id (name) (local ([define type (tlookup name tenv)])
                 (if type
                     type
                     (error "invalid type")))]
    [bin-num-op (op lhs rhs) 
                (begin 
                  (when (not (t-num? (type-of-tenv lhs tenv)))
                    (error 'type-of-tenv "operating on a non-number"))
                  (when (not (t-num? (type-of-tenv rhs tenv)))
                    (error 'type-of-tenv "operating on a non-number"))
                  (t-num))]
    [less (lhs rhs) 
          (begin 
            (when (not (tassert (t-num) (type-of-tenv lhs tenv)))
              (error 'type-of-tenv "Checking a non-number"))
            (when (not (tassert (t-num) (type-of-tenv rhs tenv)))
              (error 'type-of-tenv "Checking a non-number"))
            (t-bool))]
    [app (fe ae)
         (begin
           (local ([define f-type (type-of-tenv fe tenv)])
             (when (not (t-fun? f-type))
               (error 'type-of-tenv "Not a function type in application."))
             (when (not (equal? (t-fun-arg f-type) (type-of-tenv ae tenv)))
               (error 'type-of-tenv "Arguement type does not match"))
             (t-fun-result f-type)))]
    [fun (arg type body)
         (t-fun type (type-of-tenv body (aTEnv arg type tenv)))]
    [with (arg type bound-body body) 
          (begin
            (when (not (tassert type (type-of-tenv bound-body tenv)))
              (error 'static-check "Bad with!"))
            (type-of-tenv body (aTEnv arg type tenv)))]
    [rec (arg type bound-body body)
      (begin
        (when (not (t-fun? type))
          (error 'type-of-tenv "Bad rec!"))
        (local ([define extended-tenv (aTEnv arg type tenv)]
                [define b-body-type (type-of-tenv bound-body extended-tenv)])
          (when (not (tassert type b-body-type))
            (error 'type-of-tenv "Bad rec!!"))
          (type-of-tenv body extended-tenv)))]
    [bool (b) (t-bool)]
    [bif (c t e) (local ([define type (type-of-tenv t tenv)])
                   (begin
                     (when (not (t-bool? (type-of-tenv c tenv)))
                       (error 'type-of-tenv "condition is not a boolean"))
                     (when (not (tassert (type-of-tenv t tenv) (type-of-tenv e tenv)))
                       (error 'type-of-tenv "then and else branch do not have the same type"))
                     type))]
    [nempty () (t-ntree)]
    [nnode (val lhs rhs) (begin
                           (when (not (t-num? (type-of-tenv val tenv)))
                             (error "val is not a number"))
                           (when (not (t-ntree? (type-of-tenv lhs tenv)))
                             (error "lhs is not a tree"))
                           (when (not (t-ntree? (type-of-tenv rhs tenv)))
                             (error "rhs is not a tree"))
                           (t-ntree)
                           )]
    [isnempty (e) (begin
                    (when (not (tassert (t-ntree) (type-of-tenv e tenv)))
                      (error "expression is not a tree"))
                    (t-bool))]
    [nvalue (e) (begin
                  (when (not (t-ntree? (type-of-tenv e tenv)))
                    (error "expression is not a tree"))
                  (t-num))]
    [nleft (e) (begin 
                 (when (not (t-ntree? (type-of-tenv e tenv)))
                   (error "expression is not a tree"))
                 (t-ntree))]
    [nright (e) (begin 
                  (when (not (t-ntree? (type-of-tenv e tenv)))
                    (error "expression is not a tree"))
                  (t-ntree))]))

;; type-of : Expr -> Type
;; Consumes an expression and deduces its type.  
;; Generates an error if the expression includes
;; a type error.
(define (type-of exp)
  (type-of-tenv exp (mtTEnv)))

;; run : sexp -> Value
;; Consumes an sexpression, parses it, type-checks the result
;; (producing an error if the expression includes any type 
;; error), pre-processes the AST to remove many nodes, and
;; interprets the simplified result to its value.
(define (run sexp)
  (local ([define exp (parse sexp)])
    (type-of exp)
    (interp-env (pre-process exp) (mtEnv))))






;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Type-checking tests.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; <num>
(test (type-of (parse '1)) (t-num))

;; true | false
(test (type-of (parse 'false)) (t-bool))
(test (type-of (parse 'true)) (t-bool))

;; <id>
(test/exn (type-of (parse 'x)) "")
;; Leaving additional tests to function/with section.

;; {+ <e> <e>} (and /, -, *)
(test (type-of (parse '{+ 1 2})) (t-num))
(test/exn (type-of (parse '{+ true 2})) "")
(test/exn (type-of (parse '{+ 2 true})) "")

;; {< <e> <e>}
(test (type-of (parse '{< 0 1})) (t-bool))
(test/exn (type-of (parse '{< true 1})) "")
(test/exn (type-of (parse '{< 1 true})) "")

;; {bif <e> <e> <e>}
(test (type-of (parse '{bif true true true})) (t-bool))
(test (type-of (parse '{bif true 1 1})) (t-num))
(test/exn (type-of (parse '{bif true 1 true})) "")
(test/exn (type-of (parse '{bif true true 1})) "")
(test/exn (type-of (parse '{bif 1 true true})) "")

;; {with {<id> : <type> <e>} <e>}
(test (type-of (parse '{with {x : number 1} x})) (t-num))
(test (type-of (parse '{with {x : number 1} true})) (t-bool))
(test/exn (type-of (parse '{with {x : number true} x})) "")
(test/exn (type-of (parse '{with {x : number x} 1})) "")

;; {fun {<id> : <type>} <e>}
(test (type-of (parse '{fun {x : number} true})) (t-fun (t-num) (t-bool)))
(test (type-of (parse '{fun {x : number} x})) (t-fun (t-num) (t-num)))
(test/exn (type-of (parse '{fun {x : number} {bif x 0 1}})) "") ;; redundant w/previous and bif tests, really

;; {<e> <e>}
(test (type-of (parse '{{fun {x : number} true} 1})) (t-bool))
(test/exn (type-of (parse '{{fun {x : number} true} true})) "")
(test/exn (type-of (parse '{1 1})) "")

;; {rec {<id> : <type> <e>} <e>}  ;; but the type must be a function type
(test/exn (type-of (parse '{rec {x : number 1} x})) "")
(test (type-of (parse '{rec {f : (number -> boolean) {fun {x : number} true}} f})) (t-fun (t-num) (t-bool)))
(test (type-of (parse '{rec {f : (number -> number) {fun {x : number} x}} {f 1}})) (t-num))
;; doesn't run, but does type-check..
(test (type-of (parse '{rec {f : (number -> number) {fun {x : number} {f x}}} {f 1}})) (t-num)) 
(test/exn (type-of (parse '{rec {f : (number -> number) {fun {x : number} {f true}}} {f 1}})) "") 


;; nempty
(test (type-of (parse 'nempty)) (t-ntree))

;; {nnode <e> <e> <e>}
(test (type-of (parse '{nnode 0 nempty nempty})) (t-ntree))
(test/exn (type-of (parse '{nnode true nempty nempty})) "")
(test/exn (type-of (parse '{nnode 0 1 nempty})) "")
(test/exn (type-of (parse '{nnode 0 nempty 1})) "")

;; {nleft <e>}
(test (type-of (parse '{nleft nempty})) (t-ntree))
(test/exn (type-of (parse '{nleft 0})) "")

;; {nright <e>}
(test (type-of (parse '{nright nempty})) (t-ntree))
(test/exn (type-of (parse '{nright 0})) "")

;; {nempty? <e>}
(test (type-of (parse '{nempty? nempty})) (t-bool))
(test/exn (type-of (parse '{nempty? 0})) "")

;; {nvalue <e>}
(test (type-of (parse '{nvalue nempty})) (t-num))
(test/exn (type-of (parse '{nvalue 0})) "")


;; One larger example from the book:
(test (type-of (parse '{bif {< 1 {+ 1 2}}
                            {{fun {x : number} {+ 1 x}} 7}
                            {{fun {x : number} {+ 1 {+ 2 x}}} 1}}))
      (t-num))

;; Factorial
(test (type-of (parse '{rec {fact : (number -> number)
                                  {fun {n : number}
                                       {bif {< n 1}
                                            1
                                            {* n {fact {- n 1}}}}}}
                         {fact 4}})) (t-num))

;; Map (tree version)
(test (type-of (parse '{rec {map : ((number -> number) -> (ntree -> ntree))
                                 {fun {f : (number -> number)}
                                      {fun {tree : ntree}
                                           {bif {nempty? tree}
                                                nempty
                                                {nnode {f {nvalue tree}}
                                                       {{map f} {nleft tree}}
                                                       {{map f} {nright tree}}}}}}}
                         map}))
      (t-fun (t-fun (t-num) (t-num)) (t-fun (t-ntree) (t-ntree))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; End-to-end tests
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; numbers
(test (run '1) (numV 1))

;; binops
(test (run '{+ 1 2}) (numV 3))
(test (run '{- 1 2}) (numV -1))
(test (run '{* 1 2}) (numV 2))
(test (run '{/ 1 2}) (numV 1/2))
(test/exn (run '{+ {fun {x : number} 1} 2}) "")
(test/exn (run '{+ 1 {fun {x : number} 2}}) "")
(test/exn (run '{/ 1 0}) "")

;; identifiers
(test (run '{with {x : number 1} x}) (numV 1))
(test/exn (run 'x) "")

;; bif, true, and false
(test (run '{bif true 2 3}) (numV 2))
(test (run '{bif false 2 3}) (numV 3))
(test/exn (run '{bif 0 1 3}) "")
(test (run '{bif false {/ 1 0} 3}) (numV 3))
(test (run '{bif true 3 {/ 1 0}}) (numV 3))
(test/exn (run '{bif true {/ 1 0} 3}) "")

;; functions
(test (run '{fun {x : number} 1}) (closureV 'x (ppnum 1) (mtEnv)))
(test (run '{with {x : number 1} {fun {x : number} 1}}) (closureV 'x (ppnum 1) (anEnv 'x (numV 1) (mtEnv))))
(test (run '{with {x : number {+ 1 2}} {fun {x : number} 1}}) (closureV 'x (ppnum 1) (anEnv 'x (numV 3) (mtEnv))))

(test (run '{{fun {x : number} x} 1}) (numV 1))

(test (run '{with {f : (number -> number) {fun {x : number} 1}} {f 2}}) (numV 1))

(test (run '{with {f : (number -> number) {with {x : number 1} {fun {y : number} x}}} {f 2}}) (numV 1))
(test (run '{with {f : (number -> number) {with {x : number 1} {fun {y : number} x}}} {with {x : number 2} {f 2}}}) (numV 1))

(test (run '{{{fun {x : number} {fun {y : number} {- x y}}} 1} 2}) (numV -1))

(test (run '{rec {fact : (number -> number)
                       {fun {n : number} {bif {< n 1}
                                              1
                                              {* n {fact {- n 1}}}}}}
              {fact 4}})
      (numV 24))

;; <, nvalue, nempty?, nnode, nleft, nright.
(test (run '{bif {< 1 2} 0 1}) (numV 0))
(test (run '{bif {< 2 2} 0 1}) (numV 1))
(test (run '{nvalue {nnode 1 nempty nempty}}) (numV 1))
(test (run '{bif {nempty? {nnode 1 nempty nempty}} 0 1}) (numV 1))
(test (run '{bif {nempty? nempty} 0 1}) (numV 0))
(test/exn (run '{nvalue nempty}) "")
(test/exn (run '{nleft nempty}) "")
(test/exn (run '{nright nempty}) "")
(test (run '{nvalue {nleft {nnode 1 
                                  {nnode 2 nempty nempty}
                                  {nnode 3 nempty nempty}}}}) (numV 2))
(test (run '{nvalue {nright {nnode 1 
                                   {nnode 2 nempty nempty}
                                   {nnode 3 nempty nempty}}}}) (numV 3))

(test (run '(with (x : ntree (nnode 1 nempty nempty)) (nleft x))) (closureV 'the-fun (ppapp (ppapp (ppapp (ppapp (ppid 'the-fun) (ppnum 0)) (ppnum 0)) (ppnum 0)) (ppfun 't (ppfun 'f (ppapp (ppid 't) (ppnum 1))))) (anEnv 'the-fun (closureV 'x (ppfun 'y (ppfun 'z (ppfun 'a (ppid 'y)))) (anEnv 'ignore (numV 1) (anEnv 'x (closureV 'the-fun (ppapp (ppapp (ppapp (ppapp (ppid 'the-fun) (ppnum 1)) (ppfun 'the-fun (ppapp (ppapp (ppapp (ppapp (ppid 'the-fun) (ppnum 0)) (ppnum 0)) (ppnum 0)) (ppfun 't (ppfun 'f (ppapp (ppid 't) (ppnum 1))))))) (ppfun 'the-fun (ppapp (ppapp (ppapp (ppapp (ppid 'the-fun) (ppnum 0)) (ppnum 0)) (ppnum 0)) (ppfun 't (ppfun 'f (ppapp (ppid 't) (ppnum 1))))))) (ppfun 't (ppfun 'f (ppapp (ppid 'f) (ppnum 1))))) (mtEnv)) (mtEnv)))) (mtEnv))))

(test (run '(bif (with (x : ntree (nnode 1 nempty nempty)) (nempty? x)) 0 1)) (numV 1))