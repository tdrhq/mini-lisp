package in.tdrhq.lisp;


import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;
import static in.tdrhq.lisp.Lexer.*;

public class ParserTest extends TestCase {
	World world = new World();
	
	public static List<Object> list(Object... objects) {
		ArrayList<Object> ret = new ArrayList<Object> ();
		for (Object o : objects) {
			ret.add(o);
		}
		return ret;
	}
	
	public List<Object> compile(String code) {
		return new Parser(world, new Lexer(code)).parse(); 
	}
	
	public Object sym(String s) {
		return world.intern("cl::" + s);
	}
	@Test
	public void testSimpleParsing() {
		assertEquals(list(list(sym("foo"), sym("bar"))), compile("(foo bar)"));
		assertEquals(
				list(
						list(sym("foo"), sym("bar")),
						list(sym("foo"), list(sym("bar")))),
						compile("(foo bar)(foo (bar))"));
						
	}
	
	@Test
	public void testParsingSingleSymbol() {
	    assertEquals(list(sym("foo")), compile("foo"));
	    assertEquals(list("foo"), compile("\"foo\""));
	    assertEquals(list("foo bar"), compile("\"foo bar\""));
	}
	
	@Test
	public void testSugar() {
	    assertEquals(compile("(i (quote foo))"), compile("(i 'foo)"));
	    assertEquals(compile("(i (backquote foo))"), compile("(i `foo)"));
	    assertEquals(compile("(i (funcvalue (quote foo)))"), compile("(i #'foo)"));
	}

}
