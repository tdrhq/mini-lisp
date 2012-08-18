package in.tdrhq.lisp;

public class Symbol {
	public static int globalId = 1;
	public String stringValue;
	public Lambda macroDefinition;
	
	int id;
	Object globalValue = null;

	public Symbol(String name) {
		id = globalId ++;
		stringValue = name;
	}
	
	public String toString() {
		return String.valueOf(stringValue);
	}
}
