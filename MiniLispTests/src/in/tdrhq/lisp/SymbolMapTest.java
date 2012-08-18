package in.tdrhq.lisp;

import static org.junit.Assert.*;

import org.junit.Test;

public class SymbolMapTest {
	SymbolMap map = new SymbolMap();
	
	@Test
	public void testBasicOperations() {
		assertEquals(1, map.intern("foo"));
		assertEquals(2, map.intern("bar"));
		assertEquals(1, map.intern("foo"));
		assertEquals(2, map.intern("bar"));
		
		assertEquals("foo", map.nameOf(1));
	}

}
