package in.tdrhq.lisp;

import in.tdrhq.lisp.Lexer.IntToken;
import in.tdrhq.lisp.Lexer.NilToken;
import in.tdrhq.lisp.Lexer.StringToken;
import in.tdrhq.lisp.Lexer.SymbolToken;
import in.tdrhq.lisp.Lexer.Token;

import java.util.ArrayList;
import java.util.List;

public class Evaluator {
	World world;
	NativeLibrary nativeLibrary;
	
	public Evaluator(World world) {
		this.world = world;
		nativeLibrary = new NativeLibrary(world);
	}

	public Object eval(Environment env, Object code) {
		System.out.printf("Evaluating %s\n", code);
		if (code instanceof Cons) {
			code = ((Cons) code).toList();
		}
		if (code instanceof List) {
			return evalList(env, (List<Object>)code);
		} else if (code instanceof IntToken) {
			return (Integer) ((IntToken) code).value;
		} else if (code instanceof NilToken) {
			return null;
		} else if (code instanceof StringToken) {
			return ((StringToken) code).value;
		} else if (code instanceof SymbolToken) {
			return env.getSymbolValue(tokenToSymbol(code));
		}
		
		return null;
	}
	
	public Object evalList(Environment env, List<Object> code) {
		ArrayList<Object> args = new ArrayList<Object>();

		SymbolToken function = (SymbolToken) code.get(0);
		
		if (function.value.equals("if")) {
			// this is the only case that doesn't evaluate
			// bot sides
			Object res1 = eval(env, code.get(1));
			if (res1 != null) {
				return eval(env, code.get(2));
			}
			return null;
		}
		
		// next internal macro is quote!
		if (function.value.equals("quote")) {
			Object sym = code.get(1);
			return quote(sym);
		}
				
		// and then here comes lambda
		if (function.value.equals("lambda1") || function.value.equals("lambdam")) { 
			Lambda lambda;
			if (function.value.equals("lambda1")) {
				lambda = new Lambda();
			} else {
				lambda = new Macro();
			}
			lambda.ast = (List<Object>) code.get(2); // only single query allowed!

			List<Object> argList = (List<Object>) code.get(1);
			Symbol[] args1 = new Symbol[argList.size()];
			for (int i = 0; i < args1.length; i++) {
				args1[i] = tokenToSymbol(argList.get(i));
			}
			lambda.parameterNames = args1;
			return lambda;
		}
				
		Symbol functionSym = tokenToSymbol(function);
		
		if (functionSym.macroDefinition != null) {
			// ooh, user defined macro! quote each of the arguments
			// and send it over to the macro and evaluate the return
			// value
			
			List<Object> newargs = new ArrayList<Object>();
			for (Object arg : code) {
				if (newargs.size() == 0) {
					newargs.add(functionSym.macroDefinition);
				} else {
					newargs.add(quote(arg));
				}
			}

			Object result = funccall(env, newargs);
			
			// and now that we have the code ready... 
			eval(env, result);
		}
		
		// for each argument evaluate it
		boolean skipped = false;
		for (Object arg : code) {
			if (!skipped) {
				skipped = true;
				continue;
			}
			
			args.add(eval(env, arg));
		}
		
		// now let's do the actual computation
		if (function.value.equals("+")) {
			return add(args);
		}
		
		if (function.value.equals("identity")) {
			return args.get(0);
		}
		
		if (function.value.equals("progn")) {
			return args.get(args.size() - 1);
		}
		
		if (function.value.equals("funccall")) {
			return funccall(env, args);
		}
		
		if (function.value.equals("set")) {
			env.setSymbolValue((Symbol) args.get(0), args.get(1));
			return args.get(1);
		}
		
		// is this a user defined function in world?
		if (world.functionMap.containsKey(functionSym)) {
			List<Object> newArgs = new ArrayList<Object>();
			newArgs.add(world.functionMap.get(functionSym));
			newArgs.addAll(args);
			return funccall(env, newArgs);
		}
				
		if (nativeLibrary.isNativeMethod((String) function.value)) {
			return nativeLibrary.exec((String) function.value, args.toArray());
		}
		
		
		throw new RuntimeException(String.format("%s is not a function", function.value));
	}

	private Symbol tokenToSymbol(Object sym) {
		return world.intern((String) ((SymbolToken) sym).value);
	}
	
	public Object add(List<Object> args) {
		int result = 0;
		for (Object a : args) {
			result += (Integer) a;
		}
		return (Integer) result;
	}
	
	public Object funccall(Environment parent, List<Object> args) {
		Lambda l = (Lambda) args.get(0);
		LambdaEnvironment env = new LambdaEnvironment(parent);
		
		if (args.size() != l.parameterNames.length + 1) {
			throw new RuntimeException("wrong number of parameters");
		}
		
		for (int i = 0; i < l.parameterNames.length; i ++) {
			env.setSymbolValueInEnvironment(l.parameterNames[i], args.get(i + 1));
		}
		
		// how do you actually evaluate the lambda now? just evaluate
		// the ast in the environment
		Object res = eval(env, l.ast);
		
		if (l instanceof Macro) {
			// execute whatever was returned!
			return eval(parent, res);
		} else {
			return res;
		}
	}
	
	public Object quote(Object o) {
		System.out.printf("quoting %s\n", o);
		if (o instanceof List) {
			return quoteList((List<Object>) o, 0);
		} else if (o instanceof SymbolToken) {
			return tokenToSymbol(o);
		} else if (o instanceof IntToken) {
			return ((IntToken) o).value;
		} else {
			return null;
		}
	}
	
	public Cons quoteList(List<Object> l, int start) {
		if (start == l.size()) {
			return null;
		}
		
		Cons res = new Cons();
		res.car = quote(l.get(start));
		res.cdr = quoteList(l, start + 1);
		return res;
	}
}
