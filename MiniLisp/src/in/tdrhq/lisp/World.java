package in.tdrhq.lisp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

public class World implements Environment {
	SymbolMap symbolMap = SymbolMap.singleton();
	public Object trueObject = new Object();
	Map<String, Symbol> internMap = new HashMap<String, Symbol> ();
	Map<String, Package> packageMap = new HashMap<String, Package> ();
	Map<Object, SexpMetadata> sexpMetadatas = new WeakHashMap<Object, SexpMetadata>();	
	
	// special symbols
	public static class Keywords { 
		Symbol IF1;
		Symbol SET;
		Symbol QUOTE;
		Symbol COMMA;
		Symbol COMMA_AT;
		Symbol LAMBDA1;
		Symbol TRY1;
		Symbol LAST_ERROR;
		Symbol PACKAGE;
		Symbol FUNCCALL;
		Symbol APPLY;
		Symbol BACKQUOTE;
		Symbol AMP_REST;
		Symbol AMP_BODY;
	}
	
	public Keywords keywords = new Keywords();
	
	public World() {
		// setup keywords and intern them
		new NativeLibrary(this).registerMethods();
		setupKeywords();
	}
	
	public void setupKeywords() {
	    keywords.IF1 = cl_intern("if1");
	    keywords.SET = cl_intern("set");
	    keywords.QUOTE = cl_intern("quote");
	    keywords.COMMA = cl_intern("comma");
	    keywords.COMMA_AT = cl_intern("comma-at");
	    keywords.LAMBDA1 = cl_intern("lambda1");
	    keywords.TRY1 = cl_intern("try1");
	    keywords.PACKAGE = cl_intern("*package*");
	    keywords.PACKAGE.globalValue = "cl";
	    keywords.LAST_ERROR = cl_intern("*last-error*");
	    keywords.FUNCCALL = cl_intern("funccall");
	    keywords.APPLY = cl_intern("apply");
	    keywords.BACKQUOTE = cl_intern("backquote");
	    keywords.AMP_BODY = cl_intern("&body");
	    keywords.AMP_REST = cl_intern("&rest");
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
		return s.globalValue;
	}
	
	public void setSymbolValue(Symbol s, Object value) {
		s.globalValue = value;
	}
	
	public Symbol intern(String name) {
		if (internMap.containsKey(name)) {
			return internMap.get(name);
		} else {
			internMap.put(name, new Symbol(name));
			return internMap.get(name);
		}
	}
	
	// make two identifiers map to the same symbol.
	// the original identifier takes precedence when
	// converting the symbol to string
	public void importSymbol(String to, Symbol from) {
	    if (internMap.containsKey(to)) {
	        throw new RuntimeException(to + " is already present");
	    }
	    internMap.put(to, from);
	}

	@Override
	public Environment getParent() {
		return null;
	}
	
	public Object evalText(String s, String fileName) {
        Lexer lexer = new Lexer(s, fileName);
        Parser parser = new Parser(this, lexer);
        Object res = null;
        Object next = null;
        while ((next = parser.parseOne()) != null) {
            System.out.printf("parsed as %s\n", next);
            res = new Evaluator(this).eval(this, next);
        }    
        return res;
	}

	public Object evalText(String s) {
	    return evalText(s, "unknown");
	}
	
	public Symbol cl_intern(String name) {
	    String exportedName = "cl:" + name;
	    if (internMap.containsKey(exportedName)) {
	        return intern(exportedName);
	    }
	    Symbol ret = intern(exportedName);
	    importSymbol("cl::" + name, ret);
	    importSymbol(name, ret);
	    return ret;
	}
	
	public String formatLambda(Lambda lambda) {
	    // first off, is there a symbol that maps to this lambda?
	    // let's pull out the first one that does!
	    
	    for (Symbol symbol : internMap.values()) {
	        if (symbol.functionDefinition == lambda) {
	            return symbol.toString();
	        }
	    }
	    return lambda.toString();
	}
	
	public String formatLispError(LispError e) {
	    String result = e.getMessage() + "\n";
	    
	    for (Object lambda : e.stack) {
	        result += "   in " + formatLambda((Lambda) lambda) + "\n"; 
	    }
	    return result;
	}
}
	
