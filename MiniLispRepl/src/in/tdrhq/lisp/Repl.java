package in.tdrhq.lisp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Repl {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		World world = new World();
		Evaluator e = new Evaluator(world);
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		  
		// always load the prelude
        world.evalText("(load \"lisp/prelude.lisp\")");
        world.evalText("(load \"lisp/types.lisp\")");
        world.evalText("(load \"lisp/reflect.lisp\")");
        world.evalText("(load \"lisp/package.lisp\")");

        if (args.length == 1) {
            world.evalText(NativeLibrary.readFileAsString(args[0]));
	    return;
        }
        
		while (true) {
			String s;
			try {
				s = in.readLine();
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}

			try {
			    System.out.printf("=> %s\n", world.evalText(s));
			} catch (LispError e1) {
			    System.out.println("LispError thrown:\n");
                System.out.println(world.formatLispError(e1));
            } catch (Exception ee) {
			    ee.printStackTrace();
			}
		}
	}

}
