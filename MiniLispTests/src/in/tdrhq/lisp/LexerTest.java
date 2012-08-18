package in.tdrhq.lisp;


import org.junit.Test;

import junit.framework.TestCase;
import static in.tdrhq.lisp.Lexer.*;

public class LexerTest extends TestCase {

    @Test
    public void testEquality() {
    	assertEquals(new LeftBracket(), new LeftBracket());
    	assertEquals(new SymbolToken("foo"), new SymbolToken("foo"));
    }
	@Test
	public void testBasic() {
		Lexer  l = new Lexer("( foo bar )");
		assertEquals(new LeftBracket(), l.getNextToken());
		assertEquals(new SymbolToken("foo"), l.getNextToken());
		assertEquals(new SymbolToken("bar"), l.getNextToken());
		assertEquals(new RightBracket(), l.getNextToken());
		assertNull(l.getNextToken());
	}
	
	@Test
	public void testStrings() {
		Lexer l = new Lexer("(foo \"bar\")");
		assertEquals(new LeftBracket(), l.getNextToken());
		assertEquals(new SymbolToken("foo"), l.getNextToken());
		assertEquals(new StringToken("bar"), l.getNextToken());
		assertEquals(new RightBracket(), l.getNextToken());
		assertNull(l.getNextToken());
	}

	@Test
	public void testStringEscaping() {
		Lexer l = new Lexer("\"bar\\\"r\"");
		assertEquals(new StringToken("bar\"r"), l.getNextToken());
	}
	
	@Test
	public void testCommentsAreExcluded() {
	    Lexer l = new Lexer("; foo bar\n 20");
	    assertEquals(new IntToken(20), l.getNextToken());
	    assertEquals(null, l.getNextToken());
	}
}
