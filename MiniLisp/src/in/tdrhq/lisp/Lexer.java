package in.tdrhq.lisp;

import java.io.StringReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;


public class Lexer {
    String fileName = "unknown";
    int currentLineNumber = 1;
	public static abstract class Token {
		public boolean equals(Object other) {
			return this.getClass() == other.getClass();
		}
		String fileName;
		int lineNumber;
	}
	
	int offset = 0;
		
	String matchNext(String pattern) {
	    Pattern p = Pattern.compile(pattern);
	    Matcher matcher = p.matcher(code);
	    if (matcher.find(offset) && matcher.start() == offset) {
	        String ret = matcher.group();
         //   System.out.println("Matched '" + ret + "' for " + pattern + " and code " + code + " offset " + offset);
	        offset += ret.length();
	        return ret;
	    }
	    return null;
	}
	
	void fixOffset() {
	    code = code.substring(offset);
	    offset = 0;
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
	public Lexer(String code, String fileName) {
	    code = code.replaceAll("[;].*\n", "");
		this.code = code;
		this.fileName = fileName;
	}
	
	public Lexer(String code) {
	    this(code, "unknown");
	}
	
	public Token getNextToken() {
	    Token ret = getNextTokenWithoutMetadata();
	    if (ret != null) {
	        ret.fileName = fileName;
	        ret.lineNumber = currentLineNumber;
	    }
	    return ret;
	}
	
	public String trimCode(String code) {
	    for (int i = 0; i < code.length(); i++) {
	        char thisChar = code.charAt(i);
	        if (thisChar == '\n') {
	            currentLineNumber ++;
	        }
	        if (!Character.isWhitespace(thisChar)) {
	            return code.substring(i);
	        }
	    }
	    return "";
	}
	
	public Token getNextTokenWithoutMetadata() {
		// todo: fucking optimize this
		// remove whitespace:

	//    System.out.println("finding next for " + code + ":" + offset);
		matchNext("\\s*");
		if (code.length() == offset) {
		    return null;
		}
		
		String val;
		
		if (matchNext("\\(") != null) {
			return new LeftBracket();
		} 
		

		
		if (matchNext("\\)") != null) {
			return new RightBracket();
		}
		
        fixOffset();
        if (code.length() == offset) {
            return null;
        }
		// check for syntactic sugar:
		if (matchNext("[']") != null) {
		    return new QuoteToken();
		}
		
		if (matchNext("[`]") != null) {
		    return new BackquoteToken();
		}
		
		if (matchNext("#[']") != null) {
		    return new FunQuoteToken();
		}
		
		if (matchNext("[,][@]") != null) {
		    return new CommaAtToken();
		}
		
		if (matchNext(",") != null) {
		    return new CommaToken();
		}
		
		// match for a full string!
		String strRegex = "\"([^\"\\\\]|(\\\\.))*\"";
		String token = "";
		if ((token = matchNext(strRegex)) != null) {
		    token = token.substring(1, token.length() - 1);
		    return new StringToken(StringEscapeUtils.unescapeEcmaScript(token));
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
