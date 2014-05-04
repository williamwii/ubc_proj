#lang plai

;; Name 1: Philip Storey
;; Student ID 1: 94300076

;; Name 2: Wei You
;; Student ID 2: 77610095

(define-type KCFAE
  [num (n number?)]
  [add (lhs KCFAE?) (rhs KCFAE?)]
  [id (name symbol?)]
  [if0 (test KCFAE?) (truth KCFAE?) (falsity KCFAE?)]
  [fun (param symbol?) (body KCFAE?)]
  [app (fun-expr KCFAE?) (arg-expr KCFAE?)]
  [bless (body KCFAE?)]
  [check])

(define-type KCFAE-Value
  [numV (n number?)]
  [closureV (p procedure?)])

(define-type DefrdSub
  [mtSub]
  [aSub (name symbol?) (value KCFAE-Value?) (ds DefrdSub?)])

;; lookup : symbol DefrdSub -> KCFAE-Value
(define (lookup name ds)
  (type-case DefrdSub ds
    [mtSub () (error 'lookup "no binding for identifier")]
    [aSub (bound-name bound-value rest-ds)
          (if (symbol=? bound-name name)
              bound-value
              (lookup name rest-ds))]))

;; num+ : KCFAE-Value KCFAE-Value -> KCFAE-Value
(define (num+ x y)
  (numV (+ (type-case KCFAE-Value x
             [numV (n) n]
             [else (error 'num+ "addition of a non-number")])
           (type-case KCFAE-Value y
             [numV (n) n]
             [else (error 'num+ "addition of a non-number")]))))

;; num-zero? : KCFAE-Value -> boolean
(define (num-zero? n)
  (type-case KCFAE-Value n
    [numV (n) (= n 0)]
    [else (error 'num-zero? "num-zero? argumnet must be a number")]))


;; interp : KCFAE Env receiver -> (doesn't return)
(define (interp expr env true/false k)
  (type-case KCFAE expr
    [num (n) (k (numV n))]
    [add (l r) (interp l env true/false
                       (lambda (lv)
                         (interp r env true/false
                                 (lambda (rv)
                                   (k (num+ lv rv))))))]
    [if0 (test truth falsity)
         (interp test env true/false
                 (lambda (tv)
                   (if (num-zero? tv)
                       (interp truth env k)
                       (interp falsity env k))))]
    [id (v) (k (lookup v env))]
    
    [fun (param body)
         (k (closureV (lambda (arg-val tf-val dyn-k)
                        (interp body (aSub param arg-val env) tf-val dyn-k))))]
    
    [app (fun-expr arg-expr)
         (interp fun-expr env true/false
                 (lambda (fun-val)
                   (interp arg-expr env true/false
                           (lambda (arg-val)
                             (type-case KCFAE-Value fun-val
                               [closureV (c) (c arg-val true/false k)]
                               [else (error "not an applicable value")])))))]
    [bless (expr) (interp expr env true k)]
    [check () (if true/false
                  (k (numV 0))
                  (error 'interp ""))]))

;; test-interp : KCFAE -> KCFAE-Value
(define (test-interp expr)
  (let/cc k (interp expr (mtSub) false k)))

(test (test-interp (bless (add (check) (num 2)))) (numV 2))
(test/exn (test-interp (add (check) (num 2))) "")
(test (test-interp (bless (check))) (numV 0))
(test/exn (test-interp (add (bless (add (check) (num 2))) (check))) "")
(test/exn (test-interp (add (check) (bless (check)))) "")
(test (test-interp (app
 (fun 'f (add  (num 4) (bless (app (id 'f) (num 4)))))
 (fun 'x (add (check) (id 'x))))) (numV 8))
(test (test-interp (app
 (fun 'f (add  (num 4) (bless (app (id 'f) (num 4)))))
 (fun 'x (add (check) (num 0))))) (numV 4))
(test (test-interp (bless (add (bless (check)) (check)))) (numV 0))

(test (test-interp ((let/cc k (k bless)) (check))) (numV 0))
(test/exn (test-interp (add (let/cc k (bless (check)))) (add (check) (num 4))) "")
(test (test-interp (add (add (bless (check)) (num 4)) (let/cc k (if false (check) (bless (check)))))) (numV 4))
(test/exn (test-interp (add (add (bless (check)) (num 4)) (let/cc k (if true (check) (bless (check)))))) "")