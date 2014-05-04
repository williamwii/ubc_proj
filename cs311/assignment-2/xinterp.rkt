#lang plai

;; Name: Wei You
;; Student ID: 77610095

(define-type Binding
  [binding (name symbol?) (named-expr CFWAE?)])

(define-type CFWAE
  [num (n number?)]
  [binop (op procedure?) (lhs CFWAE?) (rhs CFWAE?)]
  [with (b Binding?) (body CFWAE?)]
  [id (name symbol?)]
  [if0 (c CFWAE?) (t CFWAE?) (e CFWAE?)]
  [fun (args (listof symbol?)) (body CFWAE?)]
  [app (f CFWAE?) (args (listof CFWAE?))])

(define-type Env
  [mtEnv]
  [anEnv (name symbol?) (value CFWAE-Value?) (env Env?)])

(define-type CFWAE-Value
  [numV (n number?)]
  [thunkV (body CFWAE?) (env Env?)]
  [closureV (param symbol?)
            (body CFWAE?)
            (env Env?)])


;; convert: symbol->procedure
;;Comsume a symbol of binop and convert it to its corresponding procedure
(define (convert-symbol-op s)
  (match s
    ['+ +]
    ['- -]
    ['* *]
    ['/ /]
    )
  )
;; Test for convert-symbol-op
(test (convert-symbol-op '+) +)

;; parse : expression -> CFWAE
;; This procedure parses an expression into a CFWAE
(define (parse sexp)
  (cond
    [(number? sexp) (num sexp)]
    [(symbol? sexp) (id sexp)]
    [(list? sexp)
     (match (first sexp)
       [(or '+ '- '*) (binop 
                          (convert-symbol-op (first sexp)) 
                          (parse (second sexp)) 
                          (parse (third sexp)))]
       ['/ (if (= (third sexp) 0) (error '/ "Dividing by 0")
               (binop 
                (convert-symbol-op (first sexp)) 
                (parse (second sexp)) 
                (parse (third sexp))))]
       ['with (if (and (list? (second sexp)) (= (length (second sexp)) 2))
                  (with (binding (first (second sexp))
                                 (parse (second (second sexp))))
                    (parse (third sexp))) (error 'with "No binding is found."))]
       ['if0 (if0 (parse (second sexp))
                  (parse (third sexp)) 
                  (parse (fourth sexp)))]
       ['fun (fun (second sexp) (parse (third sexp)))]
       [else (local ([define second-sexp (second sexp)]
                          [define (parse-all l)
                            (cond
                              [(empty? l) empty]
                              [else (cons (parse (first l)) 
                                          (parse-all (rest l)))])
                            ])
               (if (list? second-sexp)
                   (app (parse (first sexp)) (parse-all second-sexp))
                   (app (parse (first sexp)) (list (parse second-sexp)))))]
       )
     ]
    )
  )

;; Test for parse:
(test (parse '1) (num 1))
(test (parse 'x) (id 'x))
(test/exn (parse '(/ 1 0)) "")
(test (parse '{+ 1 1}) (binop + (num 1) (num 1)))
(test (parse '{* 3 4}) (binop * (num 3) (num 4)))
(test (parse '{with {x 1} x}) (with (binding 'x (num 1)) (id 'x)))
(test (parse '{with {x 1} {with {x 2} x}})
      (with (binding 'x (num 1)) (with (binding 'x (num 2)) (id 'x))))
(test/exn (parse '(with x 1 x)) "")
(test (parse '{if0 (+ 1 0) (+ 1 1) (- 1 1)})
      (if0 (binop + (num 1) (num 0)) (binop + (num 1) (num 1)) (binop - (num 1) (num 1))))
(test (parse '(fun {x} y)) (fun '(x) (id 'y)))
(test (parse '(fun {x y} (+ x y)))
      (fun '(x y) (binop + (id 'x) (id 'y))))
(test (parse '((+ x 1) {})) 
      (app (binop + (id 'x) (num 1)) '()))
(test (parse '((+ x 1) {1 2 3})) 
      (app (binop + (id 'x) (num 1)) (list (num 1) (num 2) (num 3))))
(test (parse '((fun {x y} (+ x y)) {2 3 4})) 
      (app (fun '(x y) (binop + (id 'x) (id 'y))) (list (num 2) (num 3) (num 4))))


;; pre-process : CFWAE -> CFWAE
;; Consumes a CFWAE and constructs a corresponding CFWAE without
;; with expressions (which are replaced by function application) 
;; and with no functions or applications of more than one argument.
;; (Assumes the input was successfully produced by parse.)
(define (pre-process expr)
  (type-case CFWAE expr
    [num (n) expr]
    [binop (op lhs rhs)
           (binop op (pre-process lhs) (pre-process rhs))]
    [with (b body) (app (fun (list (binding-name b)) (pre-process body))
                        (list (pre-process (binding-named-expr b))))]
    [id (name) expr]
    [if0 (c t e) (if0 (pre-process c)
                      (pre-process t)
                      (pre-process e))]
    [fun (args body) (cond
                       [(= 0 (length args)) (fun args (pre-process body))]
                       [(= 1 (length args)) (fun (list (first args)) (pre-process body))]
                       [else (fun (list (first args))
                                  (pre-process (fun (rest args) body)))])]
    [app (f args) (cond
                    [(= 0 (length args)) (app (pre-process f) args)]
                    [(= 1 (length args)) (app (pre-process f) (list (pre-process (first args))))]
                    [else (pre-process (app (app f (list (pre-process (first args)))) (rest args)))])]
    )
  )

;; Test cases for pre-process
(test (pre-process (num 2)) (num 2))
(test (pre-process (id 'y)) (id 'y))
(test (pre-process (binop * (num 3) (num 4))) (binop * (num 3) (num 4)))
(test (pre-process (with (binding 'x (num 3)) (binop / (id 'x) (num 5)))) 
      (app (fun '(x) (binop / (id 'x) (num 5))) (list (num 3))))
(test (pre-process (if0 (with (binding 'x (num 3)) (binop / (id 'x) (num 5)))
                        (binop + (id 'x) (num 5))
                        (binop - (id 'x) (num 5))))
                   (if0 (app (fun '(x) (binop / (id 'x) (num 5))) (list (num 3)))
                        (binop + (id 'x) (num 5))
                        (binop - (id 'x) (num 5))))
(test (pre-process (fun '(x) (binop + (id 'x) (num 1)))) 
      (fun '(x) (binop + (id 'x) (num 1))))
(test (pre-process (fun '(x y z) (binop + (id 'x) (binop - (id 'y) (id 'z))))) 
      (fun '(x) (fun '(y) (fun '(z) (binop + (id 'x) (binop - (id 'y) (id 'z)))))))
(test (pre-process (app (fun '(x) (binop + (id 'x) (num 1))) (list (num 1)))) 
      (app (fun '(x) (binop + (id 'x) (num 1))) (list (num 1))))
(test (pre-process (app (fun '(x y z) (binop + (id 'x) (binop - (id 'y) (id 'z)))) 
                        (list (num 1) (num 2) (num 3)))) 
      (app (app (app (fun '(x) (fun '(y) (fun '(z) 
                                          (binop + (id 'x) (binop - (id 'y) (id 'z)))))) 
                 (list (num 1))) (list (num 2))) (list (num 3))))

;; lookup-env : Env symbol -> CFWAE-Value
;; looks up the symbol in the given environment, returning its value
;; ERROR if the symbol is undefined.
;; NOTE: where the symbol is bound multiple times, returns the value
;; of the binding that is outermost in the Env object.
(define (lookup-env env target-name)
  (type-case Env env
    [mtEnv () (error 'lookup-env "Unbound identifier ~a" target-name)]
    [anEnv (name value restEnv)
           (if (symbol=? target-name name)
               value
               (lookup-env restEnv target-name))]))

(test/exn (lookup-env (mtEnv) 'x) "")
(test (lookup-env (anEnv 'x (numV 1) (mtEnv)) 'x) (numV 1))
(test/exn (lookup-env (anEnv 'y (numV 1) (mtEnv)) 'x) "")
(test (lookup-env (anEnv 'x (numV 1) (anEnv 'y (numV 2) (mtEnv))) 'x) (numV 1))
(test (lookup-env (anEnv 'x (numV 1) (anEnv 'y (numV 2) (mtEnv))) 'y) (numV 2))
(test (lookup-env (anEnv 'x (numV 1) (anEnv 'x (numV 2) (mtEnv))) 'x) (numV 1))


;; symbol WAE WAE or WAE-Value -> WAE or WAE-Value
;; Produces an abstract syntax tree in which all
;; free instances of the id in the expr have been
;; replaced by its replacement repl.
;; did not use in this assignment
(define (sub-all-free-instances-of sub-id expr repl)
  (if (CFWAE-Value? expr)
      expr
      (type-case CFWAE expr
        [num (n) expr]
        [binop (op lhs rhs) (op (sub-all-free-instances-of sub-id lhs repl) 
                                (sub-all-free-instances-of sub-id rhs repl))]
        [with (b body)
              (local ([define this-id (binding-name b)]
                      [define named-e (binding-named-expr b)])
              (if (symbol=? sub-id this-id)
                  (with (binding this-id
                        (sub-all-free-instances-of sub-id named-e repl))
                        body)
                  (with (binding this-id
                        (sub-all-free-instances-of sub-id named-e repl))
                        (sub-all-free-instances-of sub-id body repl)))
                )]
        [id (name)
            (if (symbol=? name sub-id)
                repl
                expr)]
        [if0 (c t e) (if0 (sub-all-free-instances-of sub-id c repl)
                          (sub-all-free-instances-of sub-id t repl)
                          (sub-all-free-instances-of sub-id e repl))
             ]
        
        ;; called after pre-process
        [fun (args body)
             (if (symbol=? (first args) sub-id)
                 (fun args body)
                 (fun args (sub-all-free-instances-of sub-id body repl)))]
        ;; called after pre-process
        [app (f args)
             (app (sub-all-free-instances-of sub-id f repl)
                  (list (sub-all-free-instances-of sub-id (first args) repl)))]
        )))
;; Test for sub-all
(test (sub-all-free-instances-of 'x (id 'x) (num 5))
      (num 5))
(test (sub-all-free-instances-of 'y (id 'x) (num 5))
      (id 'x))
(test (sub-all-free-instances-of 'x 
                                 (with (binding 'x (num 1)) (id 'x)) (num 5))
      (with (binding 'x (num 1)) (id 'x)))
(test (sub-all-free-instances-of 'x 
                                 (with (binding 'y (num 1)) (id 'x)) (num 5))
      (with (binding 'y (num 1)) (num 5)))
(test (sub-all-free-instances-of 'x 
                                 (with (binding 'y (id 'x)) (id 'y)) (num 5))
      (with (binding 'y (num 5)) (id 'y)))
(test (sub-all-free-instances-of 'x 
                                 (with (binding 'x (id 'x)) (id 'x)) (num 5))
      (with (binding 'x (num 5)) (id 'x)))
(test (sub-all-free-instances-of 'x (if0 (id 'x) 
                                         (fun (list 'x) (fun (list 'y) (binop + (id 'x) (id 'y))))
                                         (fun (list 'y) (id 'x)))
                                 (num 5))
      (if0 (num 5)
           (fun (list 'x) (fun (list 'y) (binop + (id 'x) (id 'y))))
           (fun (list 'y) (num 5))))
(test (sub-all-free-instances-of 'x (fun (list 'y) (id 'x)) (num 5))
      (fun (list 'y) (num 5)))
(test (sub-all-free-instances-of 'y (fun (list 'x) (fun (list 'y) (binop + (id 'x) (id 'y)))) (num 5))
      (fun (list 'x) (fun (list 'y) (binop + (id 'x) (id 'y)))))
(test (sub-all-free-instances-of 
       'x (app (app (app
                     (fun '(x) (fun '(y) (fun '(z) (binop + (id 'x) (binop - (id 'y) (id 'z)))))) 
                     (list (num 1))) (list (num 2))) (list (id 'x))) (num 5))
      (app (app (app (fun '(x) (fun '(y) (fun '(z) (binop + (id 'x) (binop - (id 'y) (id 'z))))))
                     (list (num 1))) (list (num 2))) (list (num 5))))


;; interp with Env
(define (interp-Env expr env)
  (if (CFWAE-Value? expr)
      expr
      (type-case CFWAE expr
        [num (n)
             (numV n)]
        [binop (op lhs rhs)
               (numV (op (numV-n (interp-Env lhs env)) 
                         (numV-n (interp-Env rhs env))))]
        [with (b body)
              (local ([define name (binding-name b)]
                      [define named-expr (binding-named-expr b)]
                      [define val (interp-Env named-expr env)])
                (interp-Env body (anEnv name val env)))]
        [id (name)
            (lookup-env env name)]
        [if0 (c t e)
             (local ([define condition (interp-Env c env)])
               (cond
                 [(numV? condition)
                  (if (= 0 (numV-n condition))
                      (interp-Env t env)
                      (interp-Env e env))]
                 [else (error 'if0 "Non-numeric test value")]))]
        [fun (args body)
             (cond
               [(empty? args) (thunkV body env)]
               [else (closureV (first args) body env)])]
        [app (f args)
             (local ([define f-value (interp-Env f env)])
                   (type-case CFWAE-Value f-value
                     [closureV (param body env)
                               (local([define f-param (closureV-param f-value)]
                                      [define f-body  (closureV-body f-value)]
                                      [define f-env   (closureV-env f-value)]
                                      [define value   (interp-Env (first args) env)])
                                 (interp-Env f-body
                                             (anEnv f-param value f-env)))]
                     [else
                      (error 'app "App error")]))]
        )
      )
  )
        
;; interp : CFWAE -> CFWAE-Value
;; This procedure interprets the given CFWAE and produces a result 
;; in the form of a CFWAE-Value (either a closureV, thunkV, or numV).
;; (Assumes the input was successfully produced by pre-process.)
(define (interp expr)
  (interp-Env expr (mtEnv))
  )

;; run : sexp -> CFWAE-Value
;; Consumes an sexp and passes it through parsing, pre-processing,
;; and then interpretation to produce a result.
(define (run sexp)
  (interp (pre-process (parse sexp))))


;; test for run/interp
(test (run '4) (numV 4))
(test (run '(+ 4 5)) (numV 9))
(test (run '(* 7 3)) (numV 21))
(test/exn (run '(/ 1 0)) "")
(test (run '(+ (- 3 4) 7)) (numV 6))
(test (run '(if0 0 1 2)) (numV 1))
(test (run '(if0 2 1 2)) (numV 2))
(test/exn (run '(if0 x 1 2)) "")
(test/exn (run 'x) "")
(test/exn (interp (parse '(with {x 1} y))) "")
(test (run '(fun {} x)) (thunkV (id 'x) (mtEnv)))
(test (run '(fun (x) (+ x 1))) (closureV 'x (binop + (id 'x) (num 1)) (mtEnv)))
(test (run '(fun {x y} (+ x y))) (closureV 'x (fun '(y) (binop + (id 'x) (id 'y))) (mtEnv)))
(test (run '((fun {x y} (+ x y)) {3 5})) (numV 8))
(test/exn (run '((fun {x y} (* x y)) {1 2 3 4})) "")
(test (run '((fun {x y z} (+ (* x y) z)) {1 2}))
      (closureV 'z (binop + (binop * (id 'x) (id 'y)) (id 'z))
                (anEnv 'y (numV 2) (anEnv 'x (numV 1) (mtEnv)))))
(test (run '(((fun {x y z} (+ (* x y) z)) {1 2}) (3))) (numV 5))
(test/exn (run '{1 2}) "")




;; Possibly useful additional functions:

;; failed-tests : -> (listof plai-test-result)
;; Generates a list of only the failed (non-good) tests from plai-all-test-results.
(define (failed-tests)
  (reverse (filter (compose not (curry symbol=? 'good) first) plai-all-test-results)))

;; CFWAE-pre-fold : (CFWAE -> CFWAE) CFWAE -> CFWAE
;; Takes a function and applies it to each expression node in the 
;; given CFWAE.  Note that the function is applied pre-order; so
;; it is applied to a node before its sub-trees.  WARNING: if
;; your function generates a new node that itself needs to be
;; re-processed through the function, CFWAE-pre-fold will not do
;; so.  (It calls f on a node and then recurses into any sub-nodes
;; of whatever node f returns.  It does not reprocess the node 
;; itself.)
(define (CFWAE-pre-fold f expr)
  (local ([define (ffold expr)
            (type-case CFWAE (f expr)
                       [num (n) (num n)]
                       [binop (op lhs rhs) (binop op (ffold lhs) (ffold rhs))]
                       [with (b body) (with (binding (binding-name b)
                                                     (ffold (binding-named-expr b)))
                                            (ffold body))]
                       [id (name) (id name)]
                       [if0 (c t e) (if0 (ffold c) (ffold t) (ffold e))]
                       [fun (args body) (fun args (ffold body))]
                       [app (f args) (app (ffold f) (map ffold args))])])
    (ffold expr)))

;; Example: 
;; swap-op-args : CFWAE -> CFWAE
;; Consumes a program and generates the corresponding program in which
;; each instance of a binop has had its lhs and rhs swapped.
;(define (swap-op-args program)
;  (CFWAE-pre-fold (lambda (exp)
;                    (type-case CFWAE exp
;                               [binop (op lhs rhs) (binop op rhs lhs)]
;                               [else exp]))
;                  program))
;
;(test (swap-op-args (parse '{+ 1 2})) (parse '{+ 2 1}))
;(test (swap-op-args (parse '{+ 3 {- {* 1 2} {/ 3 4}}}))
;      (parse '{+ {- {/ 4 3} {* 2 1}} 3}))
;(test (swap-op-args (parse '{fun {x} {+ x {if0 0 {+ 1 2} 3}}}))
;      (parse '{fun {x} {+ {if0 0 {+ 2 1} 3} x}}))


