package in.tdrhq.lisp;

import java.util.HashMap;

public class LambdaEnvironment implements Environment {
	Environment parent;
	HashMap<Symbol, Object> bindings = new HashMap<Symbol, Object> ();

	public LambdaEnvironment(Environment parent) {
		this.parent = parent;
	}
	
	@Override
	public Object getSymbolValue(Symbol s) {
		if (bindings.containsKey(s)) {
			return bindings.get(s);
		}

		return parent.getSymbolValue(s);
	}

	@Override
	public void setSymbolValue(Symbol s, Object value) {
		if (bindings.containsKey(s)) {
			bindings.put(s, value);
		}
		parent.setSymbolValue(s, value);
	}
	
	public void setSymbolValueInEnvironment(Symbol s, Object value) {
		bindings.put(s, value);
	}

	@Override
	public Environment getParent() {
		return parent;
	}
}
