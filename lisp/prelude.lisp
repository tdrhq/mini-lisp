(set (quote foo) 2)

;; defmacro, you sons of bitches. Oh, also comments work now.

(setmacrofun (quote defmacro) (lambda1 (name args &rest body)
                                (backquote
                                 (setmacrofun (quote (comma name)) (lambda1 (comma args) (comma (cons (quote progn)  body)))))))

(message "defmacrodefined")

(defmacro lambda (args &rest body)
  (backquote
   (lambda1 (comma args) (comma (cons (quote progn) body)))))

;; Now that we have the actual interesting lambda
