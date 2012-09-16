package in.tdrhq.lisp;

public class SexpMetadata {
    String fileName;
    int lineNumber;
    
    public SexpMetadata(String fileName, int lineNumber) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }
}
