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
	
	public Evaluator(World world) {
		this.world = world;
	}

	public Object eval(Environment env, Object code) {
		System.out.printf("Evaluating %s\n", code);
		if (code instanceof Cons) {
			code = ((Cons) code).toList();
		}
		
		if (code instanceof List) {
			return evalList(env, (List<Object>)code);
		} else if (code instanceof Integer) {
			return (Integer) code;
		} else if (code instanceof NilToken) {
			return null;
		} else if (code instanceof String) {
			return code;
		} else if (code instanceof Symbol) {
			return env.getSymbolValue((Symbol) code);
		}
		
		return null;
	}
	
	public Object evalList(Environment env, List<Object> code) {
		ArrayList<Object> args = new ArrayList<Object>();

		Symbol function = (Symbol) code.get(0);
		
		if (function == world.intern("if1")) {
			// this is the only case that doesn't evaluate
			// bot sides
			Object res1 = eval(env, code.get(1));
			if (res1 != null) {
				return eval(env, code.get(2));
			}
			
			if (code.size() > 3) {
			    return eval(env, code.get(3));
			} else {
			    return null;
			}
		}
		
		// next internal macro is quote!
		if (function == world.intern("quote")) {
			Object sym = code.get(1);
			return quote(sym);
		}
		
		if (function == world.intern("backquote")) {
		    return uncommafy(env, quote(code.get(1)));
		}
				
		// and then here comes lambda
		if (function == world.intern("lambda1")) { 
			Lambda lambda;
			lambda = new Lambda();
			Object ast = code.get(2);
			if (ast instanceof Cons) {
			    ast = ((Cons) ast).toList();
			}
			lambda.ast = (List<Object>) ast; // only single query allowed!
			Object argList_ = code.get(1);
			if (argList_ instanceof Cons) {
			    argList_ = ((Cons) argList_).toList();
			}
			List<Object> argList = (List<Object>) argList_;
			Symbol[] args1 = new Symbol[argList.size()];
			for (int i = 0; i < args1.length; i++) {
				args1[i] = (Symbol) argList.get(i);
			}
			lambda.parameterNames = args1;
			return lambda;
		}
				
		
		if (function.macroDefinition != null) {
			// ooh, user defined macro! quote each of the arguments
			// and send it over to the macro and evaluate the return
			// value
			
			List<Object> newargs = new ArrayList<Object>();
			for (Object arg : code) {
				if (newargs.size() == 0) {
					newargs.add(function.macroDefinition);
				} else {
					newargs.add(quote(arg));
				}
			}

			Object result = funccall(env, newargs);
			
			// and now that we have the code ready... 
			System.out.printf("Macroexpansion: %s\n", result);
			return eval(env, result);
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
		if (function == world.intern("+")) {
			return add(args);
		}
		
		if (function == world.intern("identity")) {
			return args.get(0);
		}
		
		if (function == world.intern("progn")) {
			return args.get(args.size() - 1);
		}
		
		if (function == world.intern("eval")) {
		    
		}
		

		if (function == world.intern("funccall")) {
			return funccall(env, args);
		}
		
		if (function == world.intern("apply")) {
		    return apply(env, (Lambda) args.get(0), (Cons) args.get(1));
		}
		
		if (function == world.intern("set")) {
			env.setSymbolValue((Symbol) args.get(0), args.get(1));
			return args.get(1);
		}
		
		// is this a user defined function in world?
		if (function.functionDefinition != null) {
			List<Object> newArgs = new ArrayList<Object>();
			newArgs.add(function.functionDefinition);
			newArgs.addAll(args);
			return funccall(env, newArgs);
		}
						
		throw new RuntimeException(String.format("%s is not a function", function.stringValue));
	}


	public Object add(List<Object> args) {
		int result = 0;
		for (Object a : args) {
			result += (Integer) a;
		}
		return (Integer) result;
	}
	
	public Object apply(Environment parent, Lambda lambda, Cons args) {
	    List<Object> tmp = new ArrayList<Object>();
	    tmp.add(lambda);
	    tmp.addAll(args.toList());
	    return funccall(parent, tmp);
	}
	
	public Object funccall(Environment parent, List<Object> args) {
		Lambda l = (Lambda) args.get(0);
		
		if (l instanceof NativeLambda) {
		    Object[] fargs = new Object[args.size() - 1];
		    for (int i = 1; i < args.size(); i++) {
		        fargs[i - 1] = args.get(i);
		    }
		    return ((NativeLambda) l).eval(fargs);
		}
		LambdaEnvironment env = new LambdaEnvironment(parent);
		
		System.out.printf("Funcalling with %s\n", args);
		
		for (int i = 0; i < l.parameterNames.length; i ++) {
		    Symbol pn = l.parameterNames[i];
		    Object value;
		    if (pn == world.intern("&rest")) {
		        // var args!
		        i ++;
		        pn = l.parameterNames[i];
		        value = Cons.fromList(args, i);
		        System.out.printf("final vararg is %s (%s : %d)\n", value, args, i);
		    } else {
		        value = args.get(i + 1);
		    }
			env.setSymbolValueInEnvironment(pn, value);
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
		} else {
			return o;
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
	
	class CommaAt {
	    Cons cons;
	}
	public Object uncommafy(Environment env, Object o) {
	    if (o instanceof Cons) {
	        Cons a = (Cons) o;
	        if (a.car == world.intern("comma")) {
	           return eval(env, ((Cons) a.cdr).car);
	        } else if (a.car == world.intern("comma-at")) {
	            CommaAt ret = new CommaAt();
	            ret.cons = (Cons) eval(env, ((Cons) a.cdr).car);
	            return ret;
	        } else {
	            return uncommafy_inner(env, a);
	        }
	    } else {
	        return o;
	    }
	}
	
	public Object uncommafy_inner(Environment env, Object o) {
        if (o instanceof Cons) {
            Cons a = (Cons) o;
            
            Cons ret = new Cons();
            
            if (a.car instanceof Cons) {
                Object inner = uncommafy(env, a.car);
                
                if (inner instanceof CommaAt) {
                    ret = ((CommaAt) inner).cons;
                    Cons last = ret;
                    while (last.cdr != null) {
                        last = (Cons) last.cdr;
                    }
                    
                    last.cdr = uncommafy_inner(env, a.cdr);
                    return ret;
                } else {
                    ret.car = uncommafy(env, a.car);
                    ret.cdr = uncommafy_inner(env, a.cdr);
                }
                return ret;
            } else {
                ret.car = uncommafy_inner(env, a.car);
                ret.cdr = uncommafy_inner(env, a.cdr);
            }
            return ret;
        } else {
            return o;
        }
	}
}
