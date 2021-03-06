package in.tdrhq.lisp;

import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.spi.DirectoryManager;

public class NativeLibrary {
	World world;
	
	public NativeLibrary(World world) {
		this.world = world;
	}

	
	public void registerMethods() {
	    Method[] methods = getClass().getDeclaredMethods();
	    for (Method m : methods) {
	        NativeLambda l = new NativeLambda();
	        l.method = m;
	        l.library = this;
	        world.cl_intern(m.getName()).functionDefinition = l;
	    }
	    registerAlias("+", "add");
	}
	
	public void registerAlias(String alias, String real) {
	    world.cl_intern(alias).functionDefinition =
	            world.cl_intern(real).functionDefinition;
	}
	
	
	public String concat(String a, String b) {
		return ((String) a).concat((String) b);
	}
	
	public Integer string_length(String a) {
		return a.length();
	}
	
	public Object setfun(Symbol a, Lambda b) {
	    a.functionDefinition = b;
		return b;
	}
	
	public Object list(Object...objects) {
		return Cons.build(objects);
	}
	
	public Object setmacrofun(Symbol a, Lambda b) {
		return a.macroDefinition = b;
	}
	
	public Object load(String filename) {
	    return world.evalText(readFileAsString(filename), filename);
	}
	
	public String pwd() {
	    return System.getProperty("user.dir");
	}
	public static String readFileAsString(String filePath) {
	    try {
	        StringBuffer fileData = new StringBuffer(1000);
	        BufferedReader reader = new BufferedReader(
	                new FileReader(filePath));
	        char[] buf = new char[1024];
	        int numRead=0;
	        while((numRead=reader.read(buf)) != -1){
	            String readData = String.valueOf(buf, 0, numRead);
	            fileData.append(readData);
	            buf = new char[1024];
	        }
	        reader.close();
	        return fileData.toString();
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	public Cons cons(Object a, Object b) {
	    Cons ret = new Cons();
	    ret.car = a;
	    ret.cdr = b;
	    return ret;
	}
	
	public Object message(String s) {
	    System.out.println(s);
	    return null;
	}
	
	public Object car(Cons a) {
	    return a.car;
	}
	
	public Object cdr(Cons a) {
	    return a.cdr;
	}
	
	public Integer length(Object a) {
	    if (a == null) {
	        return 0;
	    }
	    if (a instanceof Cons) {
	        return ((Cons) a).toList().size();
	    } else if (a instanceof String) {
	        return ((String) a).length();
	    }
	    throw new RuntimeException("unexpected object to length");
	}
	
	public Lambda funcvalue(Symbol s) {
	    return s.functionDefinition;
	}
	
	public Object not(Object o) {
	    if (o == null) {
	        return world.trueObject;
	    } else {
	        return null;
	    }
	}
	
	public Object add(Object[] args) {
	    int res = 0;
	    for (Object o : args) {
	        res += ((Integer) o);
	    }
	    return Integer.valueOf(res);
	}
	
	public Object identity(Object o) {
	    return o;
	}
	public Object progn(Object[] res) {
	    if (res.length == 0) {
	        return null;
	    }
	    return res[res.length - 1];
	}
	
	// technically, I can write this in 
	// in lisp, but it's just more convenient to 
	// do it from here
	public Object replace_body_with_rest(Cons c) {
	    if (c == null) { 
	        return null;
	    }
	    Cons ret = new Cons();
	    if (c.car == world.intern("&body")) {
	        ret.car = world.intern("&rest");
	    } else {
	        ret.car = c.car;
	    }
	    ret.cdr = replace_body_with_rest((Cons) c.cdr);
	    return ret;
	}
	
	public void copy_symbol(String to, Symbol from) {
	    world.importSymbol(to, from);
	}
	
	public void create_package(String packageName) {
	    world.packageMap.put(packageName, new Package(packageName));
	}
	
	public void set_exports(String packageName, Cons exports) {
	    world.packageMap.get(packageName).setExports(exports.toList().toArray(new Symbol[exports.toList().size()]));
	}
	
	public Symbol intern(String name) {
	    return world.intern(name);
	}
	
	public Cons package_get_exports(String packageName) {
	    return null;
	}
	
	public boolean streql(String a, String b) {
	    return a.equals(b);
	}
	
	public boolean eq(Object a, Object b) {
	    return a == b;
	}

	public Object error(Symbol type, Object[] message) {
	    throw new LispError(type, "");
	}

	/* reflection api */
	public Class find_class(String klass) {
	    if (klass.equals("int")) {
	        return int.class;
	    }
	    
	    if (klass.equals("long")) {
	        return long.class;
	    }
	    
	    if (klass.equals("float")) {
	        return float.class;
	    }
	    
	    try {
            return Class.forName(klass);
        } catch (ClassNotFoundException e) {
            throw new LispError(world.intern("class-not-found"), "");
        }
	}
	
	public Method find_method(Class klass, String name, Object[] types) {
	    Class[] copy = new Class[types.length];
	    for (int i = 0; i < copy.length; i++) {
	        copy[i] = (Class) types[i];
	    }
	    try {
            return klass.getMethod(name, copy);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new LispError(world.intern("no-such-method"), "");
        }
	}
	
	public Method find_only_method(Class klass, String name, Integer count) {
	   Method ret = null;
	   for (Method m : klass.getMethods()) {
	       if (m.getName().equals(name) && m.getParameterTypes().length == count.intValue()) {
	           if (ret != null) {
	               throw new RuntimeException("multiple methods with same name");
	           }
	           ret = m;
	       }
	   }
	   if (ret == null) {
	       throw new RuntimeException("method with description not found");
	   }
	   return ret;
	}
	
    public Object send_method(Object o, String methodName, Object[] args) {
        try {
            return MethodUtils.invokeMethod(o, methodName, args); 
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            }
            
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Object send_static_method(Class klass, String methodName, Object[] args) {
        try {
            return MethodUtils.invokeStaticMethod(klass, methodName, args); 
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            }
            
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }      
    }
	public Object invoke_method(Method m, Object on, Cons args) {
	    try {
            Object ret = m.invoke(on, Cons.toList(args).toArray());
            if (Boolean.TRUE.equals(ret)) {
                return world.getSymbolValue("t");
            } else if (Boolean.FALSE.equals(ret)) {
                return null;
            }
            return ret;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            }
            throw new RuntimeException(e.getTargetException());
        }
	}

	public Class class_of(Object o) {
	    return o.getClass();
	}
	
	public Object instance_of(Class klass, Object o) {
	    if (klass.isInstance(o)) {
	        return "dfd"; //True
	    } else {
	        return null;
	    }
	}
	
	// list of all the interned symbols!
	public Cons all_symbols() {
	    Cons ret = null;
	    Set<Symbol> temp = new HashSet<Symbol>();
	    temp.addAll(world.internMap.values());
	    for (Symbol s : temp) {
	        Cons n = new Cons();
	        n.car = s;
	        n.cdr = ret;
	        ret = n;
	    }
	    return ret;
	}
	
	public void quit() {
	    System.exit(0);
	}
}
