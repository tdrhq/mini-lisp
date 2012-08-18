package in.tdrhq.lisp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
			System.out.printf("types are %s", types);
			if (types[types.length - 1] == Object[].class) {
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

}
