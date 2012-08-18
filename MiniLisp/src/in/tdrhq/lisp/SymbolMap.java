package in.tdrhq.lisp;

import java.util.ArrayList;

public class SymbolMap {
	public static SymbolMap __singleton;
	public static SymbolMap singleton() {
		if (__singleton == null) {
			__singleton = new SymbolMap();
		}
		
		return __singleton;
	}
	
	public SymbolMap() {
		symbolToName.add(""); //dummy for 1-index
	}
	
	public ArrayList<String> symbolToName = new ArrayList<String>();
	
	public int intern(String name) {
		if (symbolToName.contains(name)) {
			return symbolToName.indexOf(name);
		}
		symbolToName.add(name);
		return symbolToName.size() - 1;
	}
	
	public String nameOf(int symbol) {
		return symbolToName.get(symbol);
	}
}
