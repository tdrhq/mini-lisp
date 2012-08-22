package in.tdrhq.lisp;

public class LispError extends RuntimeException {
    Symbol type;
    
    public LispError(Symbol type, String message) {
        super(message);
        this.type = type;
    }
}
