package in.tdrhq.lisp;

import static in.tdrhq.lisp.Lexer.*;

public class TokenHelpers {
	public static Token sym(String name) {
		return new SymbolToken(name);
	}
	
	public static Token i(int value) {
		return new IntToken(value);
	}
}
