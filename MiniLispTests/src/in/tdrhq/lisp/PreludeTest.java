package in.tdrhq.lisp;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class PreludeTest extends TestCase {
    World world;
    Evaluator e;

    @Before
    protected void setUp() throws Exception {
        super.setUp();
        
        world = new World();
        e = new Evaluator(world);
        world.evalText("(load \"lisp/prelude.lisp\")");
    }
    

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSimplePreludeLoaded() {
        
    }
    
    @Test
    public void testLambda() {
        Lambda l = (Lambda) world.evalText("(lambda (x) (+ 1 x))");
        assertEquals(5, world.evalText("(funccall (lambda (x) (+ 1 x) (+ 2 x)) 3)"));
    }

}
