
(setmacrofun (quote defmacro) (lambda1 (name args &rest body)
                                (backquote
                                 (setmacrofun (quote (comma name)) (lambda1 (comma args) (comma (cons (quote progn)  body)))))))


(defmacro lambda (args &body body)
  `(lambda1 ,args ,(cons 'progn body)))

;; Now that we have the actual interesting lambda method, let's
;; create defun.


(defmacro defun (name args &body body) 
  `(setfun '(comma name)
           (lambda ,args ,@body)))

(defun +1 (x)
  (+ 1 x))

(defmacro setq (name value)
 (backquote
  (set (quote (comma name)) 
       (comma value))))

(setq t 't)

(defmacro if (test then &body else)
  `(if1 ,test ,then
        ;; else condition
        (progn ,@else)))

(defun mapcar (func list)
  (if list
      (cons
       (funccall func (car list))
       (mapcar func (cdr list)))))

(mapcar (funcvalue (quote +1))
   (list 1 2 3))

(defun second (list)
  (if (cdr list)
      (car (cdr list))))

(defmacro let (assocs &body body)
  `(funccall (lambda ,(mapcar #'car assocs)
     ,@body) ,@(mapcar #'second assocs)))

(defun not (val)
  (if val nil t))

;; A package and path aware loader





          
          




