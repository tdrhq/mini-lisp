(defun java-tests:fn1 (x)
  ;; line number 4
  (error foo-bar))

(defun java-tests:fn2 (x)
  ;; line number 8
  (java-tests:fn1 x))

(defun java-tests:fn3 (x)
  (+ 1 
     ;; line number 13
     (java-tests:fn2 x)))
