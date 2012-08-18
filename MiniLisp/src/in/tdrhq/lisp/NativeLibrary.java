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
	
	public Method getNativeMethod(String name) {
	
		Method[] methods = getClass().getMethods();
		for (Method m : methods) {
			if (m.getName().equals(name)) {
				return m;
			}
		}
		return null;

	}
	
	public boolean isNativeMethod(String name) {
		return getNativeMethod(name) != null;
	}
	
	public Object exec(String name, Object[] args) {
		try {
			Method m = getNativeMethod(name);
			
			// is name a varargs function?
			Class<?> [] types = m.getParameterTypes();


			if (types.length > 0 && types[types.length - 1] == Object[].class) {
				// this is var args!
				Object[] newargs = new Object[types.length];
				
				for (int i = 0; i < types.length - 1; i++) {
					newargs[i] = args[i];
				}
				
				Object[] finalargs = new Object[args.length - types.length + 1];
				for (int i = newargs.length - 1; i < args.length; i ++) {
					finalargs[i - newargs.length + 1] = args[i];
				}
				newargs[newargs.length - 1] = finalargs;
				return m.invoke(this, newargs);
			}
			
			return m.invoke(this, args);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	
	public String concat(String a, String b) {
		return ((String) a).concat((String) b);
	}
	
	public Integer string_length(String a) {
		return a.length();
	}
	
	public Object setfun(Symbol a, Lambda b) {
		world.functionMap.put(a, b);
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
}
