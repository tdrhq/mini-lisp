package in.tdrhq.lisp;

import java.util.List;

public class BuiltInFunctions {	
	public static Object runIf(Binding binding, List<Object> sexp) {
		if (sexp.size() != 4) {
			throw new RuntimeException("if should take exactly four arguments");	
		}
		if (binding.eval(sexp.get(1)) != null) {
			return binding.eval(sexp.get(1));
		} else {
			return binding.eval(sexp.get(2));
		}
	}
	
	public static Object runSet(Binding binding, List<Object> sexp) {
		if (sexp.size() != 3) {
			throw new RuntimeException("set takes three arguments");
		}
		
		int key = binding.world.symbolMap.intern((String) sexp.get(1));
		Object res = binding.eval(sexp.get(2));
		
		// TODO: if binding has a "var" binding on this
		// symbol, then we have to set that, but for now, only global
		// set's.
		
		
		return null;
	}
}
