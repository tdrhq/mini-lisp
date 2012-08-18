package in.tdrhq.lisp;


import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import static in.tdrhq.lisp.TokenHelpers.*;

public class EvaluatorTest extends TestCase {
	World world;
	Evaluator e;
	List simpleSum = l(sym("+"), i(2), i(5));
	List nestedSum = l(sym("+"), simpleSum, i(1));
	List nestedSum2 = l(sym("+"), i(1), simpleSum);
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		
		world = new World();
		e = new Evaluator(world);
	}

	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	public static <T> List<T> l(T... t) {
		return Arrays.asList(t);
	}
	
	@Test
	public void testSimpleAddition() {
		assertEquals(Integer.valueOf(7),
				e.eval(world, simpleSum));
		assertEquals(Integer.valueOf(7),
				e.eval(world, l(sym("+"), i(1), i(1), i(5))));
	}
	
	@Test
	public void testNestedAddition() {
		assertEquals(Integer.valueOf(8), e.eval(world, nestedSum));
		assertEquals(Integer.valueOf(8), e.eval(world, nestedSum2));
	}
	

}
