#!/bin/sh

cd MiniLispRepl
ant build
cd ..

export CLASSPATH=$CLASSPATH:MiniLisp/bin:MiniLisp/lib/commons-lang3-3.1.jar:MiniLispRepl/bin
java in.tdrhq.lisp.Repl $@