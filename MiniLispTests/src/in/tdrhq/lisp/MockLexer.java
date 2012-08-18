package in.tdrhq.lisp;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class MockLexer extends Lexer {
	protected MockLexer() {
		super("");
	}

	Queue<Token> tokens;
	
	
	public static Lexer build(Token... tokens) {
		MockLexer lexer = new MockLexer();
		lexer.tokens = new LinkedList<Token>(Arrays.asList(tokens));
		return lexer;
	}
	
	public void left() {
		tokens.add(new LeftBracket());
	}
	
	public void right() {
		tokens.add(new RightBracket());
	}
	
	public void sym(String name) {
		tokens.add(new SymbolToken(name));
	}
	
	public void str(String name) {
		tokens.add(new StringToken(name));
	}
	
	public void i(int value) {
		tokens.add(new IntToken(value));
	}

	public Token getNextToken() {
		return tokens.remove();
	}
}