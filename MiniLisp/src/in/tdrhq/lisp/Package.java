package in.tdrhq.lisp;

import java.util.Arrays;
import java.util.HashMap;

public class Package {
    String name;
    Symbol[] exports;
    
    public Package(String name) {
        this.name = name;
    }
    
    public void setExports(Symbol[] exports) {
        this.exports = exports;
    }

    public Symbol[] getExports() {
        return exports;
    }
}   
