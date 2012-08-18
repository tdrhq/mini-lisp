package in.tdrhq.lisp;

import java.util.List;

public class Binding {
	World world;
	
	Object getValue(int symbol) {
		return world.globalValueMap.get(symbol);
	}
	
	public Object eval(Object o) {
		if (o instanceof Integer) {
			// TODO: lexical scoping
			return getValue((Integer) o);
		} else {
			return evalList((List<Object>) o);
		}
	}
	
	public Object evalList(List<Object> symbolizedSexp) {
		Object _cmd = symbolizedSexp.get(0);
		
		if (_cmd instanceof List) {
			throw new RuntimeException("Cannot run a list as a function");
		}
		
		int cmd = (Integer) _cmd;
		
		// firstly, we need to handle built-in commands
		if (cmd == world.keywords.If) {
			return BuiltInFunctions.runIf(this, symbolizedSexp);
		} else if (cmd == world.keywords.Set){
			return BuiltInFunctions.runSet(this, symbolizedSexp);
		}
		
		return null;
	}
}
