

(defun package::concat (s1 s2)
  (reflect:j s1 'concat s2))

(defun package::starts-with (s1 s2)
  (reflect:j s1 'startsWith s2))

(defun package::str-length (s1)
  (reflect:j s1 'length))

(defun package::substring (s1 from)
  (reflect:j s1 'substring from))

    
(defun package::import-symbol-if-req (good-prefix bad-prefix symbol)
  (let ((sym-str (reflect:j symbol 'toString)))
    (message (package::concat ">>>>>" sym-str))
    (if (not (package::starts-with sym-str bad-prefix))
        (if (package::starts-with sym-str good-prefix)
            (message (package::concat "Copying" sym-str))
            (let ((end-name (package::substring sym-str (package::str-length good-prefix))))
              (copy_symbol (package::concat (package::concat *package* ":") end-name) symbol)
              (copy_symbol (package::concat (package::concat *package* "::") end-name) symbol))
            ))))
        

(defun package::import-package (other-package)
  "Imports other-package into the package
   specified by *package*"
  (let ((prefix (package::concat other-package ":"))
        (not-prefix (package::concat other-package "::")))
    
    (mapcar (lambda (s)
              (package::import-symbol-if-req prefix not-prefix s))
            (all_symbols))))


(copy_symbol "package:import-package" 'package::import-package)

;; within packages we have a lot of interesting things we can do

(defun package:in-package (name)
  (setq cl:*package* name))
