package in.tdrhq.lisp;

import java.io.StringReader;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;


public class Lexer {
	public static abstract class Token {
		public boolean equals(Object other) {
			return this.getClass() == other.getClass();
		}
	}

	public static abstract class ValueToken extends Token {
		public Object value;
		
		ValueToken(Object value) {
			this.value = value;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other.getClass() != this.getClass()) {
				return false;
			}
			ValueToken tOther = (ValueToken) other;
			if (value == null) {
				return tOther.value == null;
			}
			
			return value.equals(tOther.value);
		}
		
		@Override
		public String toString() {
			return this.getClass().toString().substring("in.tdrhq.lisp.Lexer.Lexer$".length()) + "<" + String.valueOf(value) + ">";
		}
	}
	
	public static abstract class StringValuedToken extends ValueToken {
		public StringValuedToken(String value) {
			super(value);
		}
		
		public String getValue() {
			return (String) value;
		}
	}
	public static class LeftBracket extends Token {}
	public static class RightBracket extends Token {}
	public static class NilToken extends Token {}
	public static class Quote extends Token {}
	public static class FunQuote extends Token {}
	public static class SymbolToken extends ValueToken {
		int internedValue; // will be cached later!
		public SymbolToken(String value) {
			super(value);
		}
	}
	
	// syntactic sugar:
	public static class SugarToken extends Token {}
	
	public static class QuoteToken extends SugarToken {}
	public static class FunQuoteToken extends SugarToken {}
	public static class BackquoteToken extends SugarToken {}
	public static class CommaToken extends SugarToken {}
	public static class CommaAtToken extends SugarToken {}
	
	public static class StringToken extends StringValuedToken {

		public StringToken(String value) {
			super(value);
		}
	}
	
	public static class IntToken extends ValueToken {
		public IntToken(int value) {
			super(value);
		}
	}

	
	
    String code;	
	public Lexer(String code) {
	    code = code.replaceAll("[;].*\n", "");
		this.code = code;
	}
	
	public Token getNextToken() {
		// todo: fucking optimize this
		// remove whitespace:
		code = code.trim();
		if (code.length() == 0) {
			return null;
		}
		
		if (code.charAt(0) == '(') {
			code = code.substring(1);
			return new LeftBracket();
		} 
		
		if (code.charAt(0) == ')') {
			code = code.substring(1);
			return new RightBracket();
		}

		// check for syntactic sugar:
		if (code.startsWith("'")) {
		    code = code.substring(1);
		    return new QuoteToken();
		}
		
		if (code.startsWith("`")) {
		    code = code.substring(1);
		    return new BackquoteToken();
		}
		
		if (code.startsWith("#'")) {
		    code = code.substring(2);
		    return new FunQuoteToken();
		}
		
		if (code.startsWith(",@")) {
		    code = code.substring(2);
		    return new CommaAtToken();
		}
		
		if (code.startsWith(",")) {
		    code = code.substring(1);
		    return new CommaToken();
		}
		
		// else read the next entire word till a space is reached
		String name = "";
		char lastChar = ' ';
		while (code.length() != 0 && ((code.charAt(0) != '(' && code.charAt(0) != ')') || lastChar == '\\')) {
			char thisChar = code.charAt(0);
			
			if (Character.isWhitespace(thisChar)) {
				break;
			}
			code = code.substring(1);
			name += thisChar;
		}
		
		// is this is a symbol or a string?
		if (name.equals("nil")) {
			return new NilToken();
		} else if (Character.isDigit(name.charAt(0))) {
			return new IntToken(Integer.parseInt(name));
		}
		else if (name.charAt(0) == '"' && name.charAt(name.length() - 1) == '"') {
			name = name.substring(1, name.length() -1);
			return new StringToken(StringEscapeUtils.unescapeEcmaScript(name));
		} else {
			return new SymbolToken(name);
		}
	}
}
