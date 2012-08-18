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
	List simpleSum;
	List nestedSum;
	List nestedSum2;
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		
		world = new World();
		e = new Evaluator(world);
		simpleSum = l(world.intern("+"), 2, 5);;
		nestedSum = l(world.intern("+"), simpleSum, 1);
		nestedSum2 = l(world.intern("+"), 1, simpleSum);
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
				e.eval(world, l(world.intern("+"), 1, 1, 5)));
	}
	
	@Test
	public void testNestedAddition() {
		assertEquals(Integer.valueOf(8), e.eval(world, nestedSum));
		assertEquals(Integer.valueOf(8), e.eval(world, nestedSum2));
	}
	

}
