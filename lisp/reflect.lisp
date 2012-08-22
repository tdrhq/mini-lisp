
(defun reflect::j-internal (object name args)
  (let ((klass (class_of object)))
    (let ((method (find_only_method klass name (length args))))
      (invoke_method method object args))))

(defun reflect::j-fun (object name args)
  ;; here name can be a symbol or a string
  (let ((name2 (if (types:symbol-p name)
                   (reflect::j-internal name "toStringWithoutNs" nil)
                   name)))
    (reflect::j-internal object name2 args)))

(defun reflect:j (object method &rest fargs)
  (reflect::j-fun object method fargs))

