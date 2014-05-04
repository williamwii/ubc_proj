#lang plai


;; Name: Wei You
;; Student ID: 77610095

(define-type Binding
  [binding (name symbol?) (named-expr WAE?)])

(define-type WAE
  [num (n number?)]
  [binop (op procedure?) (lhs WAE?) (rhs WAE?)]
  [with (b Binding?) (body WAE?)]
  [with* (lob (listof Binding?)) (body WAE?)]
  [id (name symbol?)])

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

(test (convert-symbol-op '+) +)

;; parse : s-exp -> WAE
;; Consumes an s-expression and generates the corresponding WAE
(define (parse sexp)
  (cond
    [(number? sexp) (num sexp)]
    [(symbol? sexp) (id sexp)]
    [(list? sexp)
     (match (first sexp)
       [(or '+ '- '* '/) (binop (convert-symbol-op (first sexp)) (parse (second sexp)) (parse (third sexp)))]
       ['with (with (binding (first (second sexp)) (parse (second (second sexp)))) (parse (third sexp)))]
       ['with* (with*
                (local (
                        [define (bind lob)
                          (cond
                            [(empty? lob) empty]
                            [else  (cons (binding (first (first lob)) (parse (second (first lob)))) (bind (rest lob)))
                                   ])
                          ])
                  (bind (second sexp))
                  )
                (parse (third sexp)))]
       )
     ]
    ))

;;; parse tests

;; Test each type of expression.
;; Note: no need for complicated recursive expressions to verify that
;; you're descending into the appropriate sub-exprs, since even numbers
;; and symbols must be parsed.  (Unless you believe for some reason you're
;; only descending "one level down".)
(test (parse '1) (num 1))
(test (parse 'x) (id 'x))
(test (parse '{+ 1 1}) (binop + (num 1) (num 1)))
(test (parse '{* 3 4}) (binop * (num 3) (num 4)))
(test (parse '{with {x 1} x}) (with (binding 'x (num 1)) (id 'x)))

; One extra with test, because it might be handy in interp.
(test (parse '{with {x 1} {with {x 2} x}}) 
      (with (binding 'x (num 1)) (with (binding 'x (num 2)) (id 'x))))

; For with*, the same idea but also test with 0, 1, and 2 bindings and
; throw in a test the same identifier bound twice, since it might be
; handy to have around for interp (though should be nothing special for parse).
(test (parse '{with* {} 1})
      (with* '() (num 1)))
(test (parse '{with* {{x 1}} x})
      (with* (list (binding 'x (num 1))) (id 'x)))
(test (parse '{with* {{x 1} {y 1}} {+ x y}}) 
      (with* (list (binding 'x (num 1)) 
                   (binding 'y (num 1))) 
             (binop + (id 'x) (id 'y))))
(test (parse '{with* {{x 1}
                      {x {+ x 1}}}
                     x})
             (with* (list (binding 'x (num 1))
                          (binding 'x (binop + (id 'x) (num 1))))
                    (id 'x)))

;; symbol WAE WAE -> WAE
;; Produces an abstract syntax tree in which all
;; free instances of the id in the expr have been
;; replaced by its replacement repl.
(define (sub-all-free-instances-of sub-id expr repl)
  (type-case WAE expr
    [num (n) expr]
    [binop (op lhs rhs) (binop op (sub-all-free-instances-of sub-id lhs repl) 
                            (sub-all-free-instances-of sub-id rhs repl))]
    [with (b body)
          (local (
                  [define this-id (binding-name b)]
                  [define named-e (binding-named-expr b)])
            (if (symbol=? sub-id this-id)
                (with (binding this-id
                      (sub-all-free-instances-of sub-id named-e repl))
                      body)
                (with (binding this-id
                      (sub-all-free-instances-of sub-id named-e repl))
                      (sub-all-free-instances-of sub-id body repl)))
            )
          ]
    [with* (lob body) '...]
    [id (name) 
        (if (symbol=? name sub-id)
            repl
            expr)]
    ))

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

;; interp : WAE -> number
;; Consumes a WAE representation of an expression and computes
;;   the corresponding numerical result
(define (interp expr)
  (type-case WAE expr
    [num (n) n]
    [binop (op lhs rhs) (op (interp lhs) (interp rhs))]
    [with (b body) (local (
                           [define name (binding-name b)]
                           [define named-expr (binding-named-expr b)])
                     (interp (sub-all-free-instances-of name body named-expr))
                     )]
    [with* (lob body) 
           (cond
             [(= (length lob) 0) (interp body)]
             [(= (length lob) 1) (interp (with (first lob) body))]
             [else 
              (if
               (symbol=? (binding-name (first lob)) (binding-name (second lob))) 
               (interp (with* (rest lob) (with (binding (binding-name (second lob)) (num (interp (with (first lob) (binding-named-expr (second lob)))))) body)))
               (interp (with* (rest lob) (with (first lob) body)))
                )
                ]
             )
           ]
    [id (name)  (error 'interp "Encountered unbound identifier ~a." name)] 
    )
  )



;;; A few--too few!--interp tests.

(test (interp (num 100)) 100)
(test (interp (parse '{+ 1 2})) 3)
(test (interp (parse '{- 3 2})) 1)

(test (interp (parse '{with {x 1} x})) 1)
(test/exn (interp (parse 'x)) "")
(test/exn (interp (parse '{with {x 1} y})) "")
(test (interp (parse '{with {x 1} x})) 1)
(test (interp (parse '{with {x 1} 2})) 2)
(test (interp (parse '{with {x 1} 
                            {with {x 2} x}})) 2)
(test (interp (parse '{with {x 1}
                            {with {y 2} x}})) 1)
(test (interp (parse '{with {x 1}
                            {with {x {+ x 1}} x}})) 2)
(test (interp (parse '{with {x 1}
                            {with {y {+ x 1}} {+ x y}}})) 3)



(test (interp (parse '{with* {} 1})) 1)
(test (interp (parse '{with* {{x 1}} x})) 1)
(test (interp (parse '{with* {{x 1} {y 1}} {+ x y}})) 2) 
(test (interp (parse '{with* {{x 1}
                      {x {+ x 1}}}
                     x})) 2)
;; P.S. Steve's favorite programming language keyword is "catch"
;; because his first programming assignment at the college level was
;; about fishing, and so he and half the class named a variable
;; "catch" and spent hours debugging novice-incomprehensible error
;; messages as a result.