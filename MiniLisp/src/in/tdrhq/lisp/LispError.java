package in.tdrhq.lisp;

import java.util.List;



public class LispError extends RuntimeException {
    Symbol type;
    
    /**
     * The stack at the time the exception was thrown
     */
    List<Object> stack;
    
    public LispError(Symbol type, String message, List<Object> stack) {
        super(message);
        this.type = type;
        this.stack = stack;
    }
    
    public LispError(Symbol type, String message) {
        this(type, message, null);
    }
    
    public LispError(Symbol type) {
        this(type, type.toString());
    }
    
    public void printLisptrace() {
        
    }
}
