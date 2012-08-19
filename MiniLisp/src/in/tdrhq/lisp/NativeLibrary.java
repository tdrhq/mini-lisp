package in.tdrhq.lisp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.naming.spi.DirectoryManager;

public class NativeLibrary {
	World world;
	
	public NativeLibrary(World world) {
		this.world = world;
	}

	
	public void registerMethods() {
	    Method[] methods = getClass().getMethods();
	    for (Method m : methods) {
	        NativeLambda l = new NativeLambda();
	        l.method = m;
	        l.library = this;
	        world.intern(m.getName()).functionDefinition = l;
	    }
	    registerAlias("+", "add");
	}
	
	public void registerAlias(String alias, String real) {
	    world.intern(alias).functionDefinition =
	            world.intern(real).functionDefinition;
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
	    return world.evalText(readFileAsString(filename));
	}
	
	public String pwd() {
	    return System.getProperty("user.dir");
	}
	private static String readFileAsString(String filePath) {
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
}
