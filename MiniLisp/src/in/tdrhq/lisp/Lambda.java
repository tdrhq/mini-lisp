package in.tdrhq.lisp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public class Lambda {
	List<Object> ast;
	Symbol[] parameterNames;
	

	public List<Object> getAst() {
		return ast;
	}
}
