(setmacrofun (quote defmacro) (lambda1 (name args &rest body)
                                (backquote
                                 (setmacrofun (quote (comma name)) (lambda1 (comma args) (comma (cons (quote progn)  body)))))))

(message "defmacrodefined")

(defmacro lambda (args &rest body)
  (backquote
   (lambda1 (comma args) (comma (cons (quote progn) body)))))

;; Now that we have the actual interesting lambda method, let's
;; create defun.

(defmacro defun (name args &rest body) 
  (backquote
   (setfun (quote (comma name))
           (lambda (comma args)
             (comma-at body)))))

(defun +1 (x)
  (+ 1 x))

(defmacro setq (name value)
 (backquote
  (set (quote (comma name)) 
       (comma value))))


(defmacro if (test then &rest else)
 (backquote
  (if1 (comma test)
       (comma then)
       ;; else condition
       (progn (comma-at else)))))


(defun mapcar (func list)
  (if (not list)
      nil
      (cons
       (funccall func (car list))
       (mapcar func (cdr list)))))

(mapcar (funcvalue (quote +1))
   (list 1 2 3))

  





