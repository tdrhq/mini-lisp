package in.tdrhq.lisp;

import static in.tdrhq.lisp.Lexer.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
	Lexer lexer;
	public Parser(Lexer lexer) {
		this.lexer = lexer;
	}
	
	// Parse any code
	public List<Object> parse() {
		List<Object> ret = new ArrayList<Object>();
		while (true) {
			Object o = parseOne();
			
			if (o == null) {
				break;
			}
			ret.add(o);
		}
		return ret;
	}
	
	// Parse one S-expression
	public List<Object> parseOne() {
		Token next = lexer.getNextToken();
		if (next == null) {
			return null;
		}
		if (!(next instanceof LeftBracket)) {
			throw new RuntimeException("Expected left bracket");
		}
		
		return parseNext();

	}
	
	// parse one S-expression, after the leading ( is ommitted
	public List<Object> parseNext() {	
		Token next;
		List<Object> ast = new ArrayList<Object>();
		while (true) {
			next = lexer.getNextToken();
			if (next == null) {
				throw new RuntimeException("unexpected end of input");
			}
			if (next instanceof RightBracket) {
				return ast;
			}
			if (next instanceof LeftBracket) {
				ast.add(parseNext());
				continue;
			}
			
			ast.add(next);
		}
	}
}
