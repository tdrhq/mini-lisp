
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


(defun reflect::find-method (class-name name &rest type-names) 
  (let ((types (mapcar #'find_class type-names))
        (klass (find_class class-name)))
      (apply #'find_method klass name types)))


(defmacro reflect::defalias (alias-name class-name name &rest type-names)
  (let ((method (apply #'reflect::find-method class-name name type-names)))
    `(defun ,alias-name (object &rest args)
       (invoke_method ,method object args))))

(reflect::defalias reflect::length "java.lang.String" "length")
(reflect::defalias reflect::substring "java.lang.String" "substring" "int" "int")