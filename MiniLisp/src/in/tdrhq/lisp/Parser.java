package in.tdrhq.lisp;

import static in.tdrhq.lisp.Lexer.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
	Lexer lexer;
	World world;
	
	public Parser(World world, Lexer lexer) {
		this.world = world;
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
			throw new RuntimeException(String.format("Expected left bracket, but found %s instead", next));
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
			
			// type of token:
			if (next instanceof SymbolToken) {
				ast.add(world.intern((String) ((SymbolToken) next).value));
			} else if (next instanceof StringToken) {
				ast.add(((StringToken) next).value);
			} else if (next instanceof IntToken) {
				ast.add(((IntToken) next).value);
			} else if (next instanceof NilToken) {
				ast.add(null);
			} else {
				throw new RuntimeException("unexpected token");
			}
		}
	}
}
