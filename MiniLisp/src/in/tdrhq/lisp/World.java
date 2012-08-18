package in.tdrhq.lisp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class World implements Environment {
	SymbolMap symbolMap = SymbolMap.singleton();
	
	HashMap<Symbol, Object> globalValueMap = new HashMap<Symbol, Object>();
	HashMap<Symbol, Lambda> functionMap = new HashMap<Symbol, Lambda>();
	HashMap<String, Symbol> internMap = new HashMap<String, Symbol> ();
	
	// special symbols
	public static class Keywords { 
		int If;
		int Set;
	}
	
	public Keywords keywords = new Keywords();
	
	public World() {
		// setup keywords and intern them
		keywords.If = symbolMap.intern("if");
		keywords.Set = symbolMap.intern("set");
	}
	
	public List<Object> compileWithSymbols(List<Object> ast) {
		List<Object> ret = new ArrayList<Object>();

		for (Object o : ast) {
			if (o instanceof List) {
				ret.add(compileWithSymbols((List<Object>) o));
			} else {
				ret.add(symbolMap.intern((String) o));
			}
		}
		return ret;
	}
	

	public Object getSymbolValue(String name) {
		return getSymbolValue(intern(name));
	}
	
	public Object getSymbolValue(Symbol s) {
		return globalValueMap.get(s);
	}
	
	public void setSymbolValue(Symbol s, Object value) {
		globalValueMap.put(s, value);
	}
	
	public Symbol intern(String name) {
		if (internMap.containsKey(name)) {
			return internMap.get(name);
		} else {
			internMap.put(name, new Symbol(name));
			return internMap.get(name);
		}
	}

	@Override
	public Environment getParent() {
		return null;
	}
	
	public Object evalText(String s) {
        Lexer lexer = new Lexer(s);
        Parser parser = new Parser(this, lexer);
        Object res = null;
        for (Object o : parser.parse()) {
            System.out.printf("parsed as %s\n", o);
            res = new Evaluator(this).eval(this, o);
        }    
        return res;
	}
}
