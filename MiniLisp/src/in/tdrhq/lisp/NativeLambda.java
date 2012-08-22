package in.tdrhq.lisp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NativeLambda extends Lambda {
    Object library;
    Method method;
    
    public Object eval(Object[] args) {
        try {             
            // is name a varargs function?
            Class<?> [] types = method.getParameterTypes();


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
                return method.invoke(library, newargs);
            }

            return method.invoke(library, args);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            }
            throw new RuntimeException(e);
        }
    }
}
