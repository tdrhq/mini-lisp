package in.tdrhq.lisp;

import static in.tdrhq.lisp.TokenHelpers.i;
import static in.tdrhq.lisp.TokenHelpers.sym;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class EvaluatorIntegrationTest extends TestCase {
	World world;
	Evaluator e;

	@Before
	protected void setUp() throws Exception {
		super.setUp();
		
		world = new World();
		e = new Evaluator(world);
	}

	public Object eval(String str) {
		Lexer l = new Lexer(str);
		Parser p = new Parser(world, l);
		
		List<Object> ast = p.parse();
		System.out.printf("AST: %s\n", ast);
		
		Object ret = null;
		// the top level AST node is a list of S-expressions
		// that cannot be executed on its own.
		for (Object o : ast) {
			ret = e.eval(world, o);
		}
		return ret;
	}
	
	public void assertEval(Object res, String str) {
		assertEquals(res, eval(str));
	}
	
	public void assertEval(int res, String str) {
		assertEval((Object) (new Integer(res)), str);
	}
	
	@Test
	public void testSimpleAddition() {
		assertEval(5, "(+ 2 3)");
		assertEval(5, "(+ (+ 1 2) 2)");
		assertEval(6, "(+ (+ 1 2) (+ 1 2))");
	}
	
	@Test
	public void testTerminals() {
		assertEval(null, "(identity nil)");
		assertEval(5, "(identity 5)");
		assertEval("foo", "(identity \"foo\")");
	}
	
	@Test
	public void testNativeLibrary() {
		assertEval("arnold", "(concat \"arn\" \"old\")");
		assertEval(5, "(+ 1 (string_length \"arno\"))");
	}
	
	@Test
	public void testIf() {
		assertEval(5, "(if 3 5)");
		assertEval(null, "(if nil 5)");
	}
	
	@Test
	public void testSettingGlobalValues() {
		assertEval(5, "(set (quote foo) 5)");
		assertEquals(5, world.getSymbolValue("foo"));
		assertEval(5, "(identity 5)");
		assertEval(5, "(identity foo)");
		assertEval(6, "(set (quote bar) 6)");
		assertEval(11,"(+ foo bar)");
	}
	
	@Test
	public void testLambdaStuff() {
		assertTrue(eval("(lambda1 (x) (+ 1 x))") instanceof Lambda);
	}
	
	@Test
	public void testEvaluatingALambda() {
		assertEval(5, "(funccall (lambda1 (x) (+ 1 x)) 4)");
		assertEval(5, "(funccall (lambda1 (x y) (+ x y))  2 3)");
	}
	
	@Test
	public void testSettingAVariableInALambda() {
		assertEval(5, "(funccall (lambda1 (x) (progn (set (quote x) (+ 1 x)) x)) 4)");
	}
	
	@Test
	public void testGlobalFunction() {
		eval("(setfun (quote foo) (lambda1 (x) (+ 1 x)))");
		assertEval(5, "(foo 4)");
	}
	
	@Test
	public void testQuotingForLists() {
		Cons a = (Cons) eval("(quote (foo bar))");
		assertEquals(world.intern("foo"), a.getIndex(0));
		assertEquals(world.intern("bar"), a.getIndex(1));
		
		Cons c =(Cons) eval("(quote (foo bar (bar)))");
		assertEquals(world.intern("foo"), c.getIndex(0));
		assertEquals(world.intern("bar"), c.getIndex(1));
		assertEquals(world.intern("bar"), ((Cons)c.getIndex(2)).getIndex(0));
	}

	@Test
	public void testQuotingWithConstants() {
		assertEval(Cons.build(world.intern("foo"), 5),
				"(quote (foo 5))");
	}
	
	@Test
	public void testListAndThereforeVarargNatives() {
		assertEval(Cons.build(5, 2),
				"(list (+ 2 3) 2)");
	}
	
	@Test
	public void testMacroFunccal() {
		eval("(set (quote lb) (lambda1 (x y) (list (quote set) (list (quote quote) x) y)))");
		
		// let me just make sure macroexpansion is correct
		Object one = eval("(quote (set (quote t) 10))");
		Object two = eval("(funccall lb (quote t) (quote 10))");
		assertEquals(one, two);
		
		eval("(setmacrofun (quote mysetq) lb)");
		eval("(mysetq yy 10)");
		assertEval(10, "(identity yy)");
	}
	   
    @Test
    public void testVarArgs() {
        eval("(set (quote foo) (lambda1 (x &rest args) (identity args)))");
        assertEval("(quote (2 1 0)", "(funccall foo 3 2 1 0)"); 
    }
	
}
