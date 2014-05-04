#lang web-server/insta
(require plai/datatype)

;; A binding of quesion and answer
(define-type String-Tuple
  (string-tuple (question string?) (answer string?)))

;; Create a new web page with the provided title
(define (web-page title (html-for-body '()))
  (response/xexpr 
   `(html ((title ,title))
          (body (h1 ,title) 
                ;; The . below means the list at the end (what html-for-body evaluates to)
                ;; is the TAIL of the s-expression rather than another element.
                ;; So, html-for-body gets merged into this expression.
                .
                ,html-for-body))))

;; Create a new web page with an input prompt
(define (web-read page-title (input-prompt page-title) (html-for-body '())) 
  ;; This extracts the value bound (by the webpage) to "result".
  (extract-binding/single 
   'result
   ;; This accesses the bindings that come back from a web response.
   (request-bindings 
    ;; Here's where we do our "stack rip".  
    ;; Quite a bit easier than pulling things completely apart, huh?
    ;; What's happening?  send/suspend posts the webpage and pauses for the user
    ;; to reply.  When it resumes, it calls the lambda we put inside.
    ;;
    ;; Here's the cool part that we get from continuations built into the language:
    ;; when it resumes, that lambda is called right here, in the same dynamic context
    ;; in which send/suspend was called!  We don't need to rip our code apart to handle
    ;; resuming elsewhere!
    (send/suspend
     (lambda (k)
       (response/xexpr
        `(html ((title ,page-title))
               (body (h1 ,page-title)
                     (form ((action ,k))
                           (input ((name "result")))
                           (input ((type "submit") (value ,input-prompt))))
                     .
                     ,html-for-body))))))))

;; Create a web page with an Okay button
(define (web-okay-button page-title (input-prompt page-title) (html-for-body '())) 
    (send/suspend
     (lambda (k)
       (response/xexpr
        `(html ((title ,page-title))
               (body (h1 ,page-title)
                     (form ((action ,k))
                           (input ((type "submit") (value ,"Okay"))))
                     .
                     ,html-for-body))))))


;; display the first question of the survey, wait until the user enter an input and click Submit
;; only accept yes or no answer, if answer is yes, jump to question two,
;; else if the answer is no, jump to question seven (last question)
;; if the answer is not yes or no, show the error page and force the user to click the back button
(define (question-one/k k)
  (local ([define answer (web-read "Are you planning to travel soon?" "Submit")]
          [define new-tuple (string-tuple "Are you planning to travel soon?" answer)]
          [define new-k (cons new-tuple k)])
    (cond 
      [(string=? (string-locale-downcase answer) "yes") (question-two/k new-k)]
      [(string=? (string-locale-downcase answer) "no") (question-seven/k new-k)] 
      [else (error-page "Error" answer "Yes/No")])))

;; display the second question of the survey, wait until the user enter an input and click Submit
;; if answer is Italy, jump to question four,
;; otherwise, jump to question three
(define (question-two/k k)
  (local ([define answer (web-read "What country are you planning to travel?" "Submit")]
          [define new-tuple (string-tuple "What country are you planning to travel?" answer)]
          [define new-k (cons new-tuple k)])
    (if (string=? (string-locale-downcase answer) "italy")
        (question-four/k new-k)
        (question-three/k new-k))))

;; display the third question of the survey, wait until the user enter an input and click Submit
;; only accept yes or no answer, if answer is valid, jump to question four
;; if the answer is not yes or no, show the error page and force the user to click the back button
(define (question-three/k k)
  (local ([define answer (web-read "Are you also interested in travelling to Italy?" "Submit")]
          [define new-tuple (string-tuple "Are you also interested in travelling to Italy?" answer)]
          [define new-k (cons new-tuple k)])
    (cond 
      [(or (string=? (string-locale-downcase answer) "yes") (string=? (string-locale-downcase answer) "no")) (question-four/k new-k)]
      [else (error-page "Error" answer "Yes/No")])))

;;displays the page for getting the user input for question four, and then sends the user to either question
;;five, six, or seven depending on input. If the input is invalid, the error page is displayed.
(define (question-four/k k)
  (local ([define answer (web-read "How many people are accompanying you?" "Submit")]
          [define new-tuple (string-tuple "How many people are accompanying you?" answer)]
          [define new-k (cons new-tuple k)]
          [define int-answer (if (string->number answer)
                                 (string->number answer)
                                 -1)]
          [define head-k (string-locale-downcase (string-tuple-answer (first k)))]
          [define yes-italy (or (string=? head-k "italy") (string=? head-k "yes"))])
    (cond 
      [(and (< 3 int-answer) yes-italy) (question-five/k new-k)]
      [(<= 1 int-answer) (question-six/k new-k)]
      [(= 0 int-answer) (question-seven/k new-k)]
      [else (error-page "Error" answer "a valid number (e.g. 5)")])))

;;displays the page for getting the user input for question five and then 
;;calls the seventh question if the input was valid. Otherwise, sends an error page.
(define (question-five/k k)
  (local ([define answer (web-read "We offer 30% discount for those travelling with 4 or more people to Italy. Are you interested?" "Submit")]
          [define new-tuple (string-tuple "We offer 30% discount for those travelling with 4 or more people to Italy. Are you interested?" answer)]
          [define new-k (cons new-tuple k)])
    (if (or (string=? (string-locale-downcase answer) "yes")
            (string=? (string-locale-downcase answer) "no"))
        (question-seven/k new-k)
        (error-page "Error" answer "Yes/No"))))

;;displays the page for the 'question' six page, that displays the user's promotional code. 
;;It calls the seventh question once the user presses the 'Okay' button.
(define (question-six/k k)
  (local ([define promotion-code (get-p-code)]
          [define step-over (web-okay-button (string-append "We offer Basic Saving Plan of 5% discount for people travelling in groups. This is your promotion code " promotion-code))]
          [define new-tuple (string-tuple "We offer Basic Saving Plan of 5% discount for people travelling in groups. This is your promotion code " promotion-code)]
          [define new-k (cons new-tuple k)])
    (question-seven/k new-k)))

;;displays the page for the seventh question of the survey and gets the user's final input, then 
;;displays the page for the survey results. If input is not valid, displays an error page instead.
(define (question-seven/k k)
  (local ([define answer (web-read "Would you like to subscribe our newsletter?" "Submit")]
          [define new-tuple (string-tuple "Would you like to subscribe our newsletter?" answer)]
          [define new-k (cons new-tuple k)])
    (if (not (or (string=? (string-locale-downcase answer) "yes") (string=? (string-locale-downcase answer) "no"))) (error-page "Error" answer "Yes/No") 
        (web-page "Your Answers" (map (lambda (x) `(p,x)) (display-list (reverse new-k)))))))

;;a function that consumes a list of string-tuples and converts them to a list of strings.
(define (display-list list)
  (cond 
    [(empty? list) empty]
    [else (local ([define answer (string-tuple-answer (first list))]
                  [define question (string-tuple-question (first list))]
                  [define temp-str (string-append (string-append question " ") answer)])
            (cons temp-str (display-list (rest list))))]))

;;starts the survey
(define (start req)
  (question-one/k '()))

;;a function that consumes a title, an invalid answer and a valid answer and displays a web page with
;;a message about an error that occured and what the valid responces to the previous question are.
(define (error-page title invalid-answer valid-answer (html-for-body '()))
  (local ([define error-msg 
            (string-append (string-append (string-append 
                                           (string-append "Error! You entered the invalid answer \"" invalid-answer)
                                           "\". Please press back and enter ") valid-answer) ".")])
    (response/xexpr 
     `(html ((title ,title))
            (body (h1 ,title) 
                  (b1 , error-msg)
                  ;; The . below means the list at the end (what html-for-body evaluates to)
                  ;; is the TAIL of the s-expression rather than another element.
                  ;; So, html-for-body gets merged into this expression.
                  .
                  ,html-for-body)))))

;;a function that returns a pseudo-random integer between 10000 and 99999.
(define (get-p-code)
  (local ([define number (random 100000 (make-pseudo-random-generator))])
    (if (> 10000 number)
        (get-p-code)
        (number->string number))))