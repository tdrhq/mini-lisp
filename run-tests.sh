#!/bin/sh

for i in lisp/tests/*.lisp; do
    if ! ( ./lisp.sh $i ); then
	echo "Test $i failed"
	exit 1
    fi
done

echo "All Tests passed"