package in.tdrhq.lisp;

import in.tdrhq.lisp.Lexer.IntToken;
import in.tdrhq.lisp.Lexer.NilToken;
import in.tdrhq.lisp.Lexer.StringToken;
import in.tdrhq.lisp.Lexer.SymbolToken;
import in.tdrhq.lisp.Lexer.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Evaluator {
	World world;
	Stack<Object> stack = new Stack<Object>();
	
	public Evaluator(World world) {
		this.world = world;
	}

	public Object eval(Environment env, Object code) {
	    try {
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
	        
	        return code;
	    } catch (LispError e) {
	        if (e.stack == null) {
	            // let's fill up the stack
	            e.stack = new ArrayList<Object>(stack);
	        }
	        throw e;
	    }
	}
	    
	public Object evalList(Environment env, List<Object> code) {
		ArrayList<Object> args = new ArrayList<Object>();

		Symbol function = (Symbol) code.get(0);
		
		if (function == world.keywords.IF1) {
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
		if (function == world.keywords.QUOTE) {
			Object sym = code.get(1);
			return quote(sym);
		}
		
		if (function == world.keywords.BACKQUOTE) {
		    return uncommafy(env, quote(code.get(1)));
		}
				
		// and then here comes lambda
		if (function == world.keywords.LAMBDA1) { 
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

		if (function == world.keywords.TRY1) {
		    try {
		        return eval(env, code.get(1));
		    } catch (Exception e) {
		        world.setSymbolValue(world.keywords.LAST_ERROR, e);
		        return null;
		    }
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
			//System.out.printf("Macroexpansion: %s\n", result);
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
		

		if (function == world.keywords.FUNCCALL) {
			return funccall(env, args);
		}
		
		if (function == world.keywords.APPLY) {
		    Cons last = (Cons) args.get(args.size() - 1);
		    for (int i = args.size() - 2; i >= 1; i--) {
		        last = cons(args.get(i), last);		        
		    }
		    return apply(env, (Lambda) args.get(0), last);
		}
		
		if (function == world.keywords.SET) {
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
	    stack.push(args.get(0));
	    try {
	        return funccallWithoutStack(parent, args);
	    } finally {
	        stack.pop();
	    }
	}
	public Object funccallWithoutStack(Environment parent, List<Object> args) {
		Lambda l = (Lambda) args.get(0);
		
		if (l instanceof NativeLambda) {
		    Object[] fargs = new Object[args.size() - 1];
		    for (int i = 1; i < args.size(); i++) {
		        fargs[i - 1] = args.get(i);
		    }
		    return ((NativeLambda) l).eval(fargs);
		}
		LambdaEnvironment env = new LambdaEnvironment(parent);
		
		//System.out.printf("Funcalling with %s\n", args);
		
		for (int i = 0; i < l.parameterNames.length; i ++) {
		    Symbol pn = l.parameterNames[i];
		    Object value;
		    if (pn == world.keywords.AMP_REST || pn == world.keywords.AMP_BODY) {
		        // var args!
		        i ++;
		        pn = l.parameterNames[i];
		        value = Cons.fromList(args, i);
		        //System.out.printf("final vararg is %s (%s : %d)\n", value, args, i);
		        env.setSymbolValueInEnvironment(pn, value);		        
		        break;
		    } else {
		        value = args.get(i + 1);
                env.setSymbolValueInEnvironment(pn, value);
		    }
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
	//	System.out.printf("quoting %s\n", o);
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
	        if (a.car == world.keywords.COMMA) {
	           return eval(env, ((Cons) a.cdr).car);
	        } else if (a.car == world.keywords.COMMA_AT) {
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
                    if (last == null) {
                        return uncommafy_inner(env, a.cdr);
                    }
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
	
	public Cons cons(Object a, Object b) {
	    Cons c = new Cons();
	    c.car = a;
	    c.cdr = b;
	    return c;
	}
}
