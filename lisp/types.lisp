
(defun types::instance-of (object class-name)
  (let ((cls (find_class class-name)))
    (instance_of cls object)))

(defun types:symbol-p (object)
  (types::instance-of object "in.tdrhq.lisp.Symbol"))

(defun types:string-p (object)
  (types::instance-of object "java.lang.String"))