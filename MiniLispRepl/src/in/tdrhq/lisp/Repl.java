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
		  

		while (true) {
			String s;
			try {
				s = in.readLine();
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
			
			Lexer lexer = new Lexer(s);
			Parser parser = new Parser(world, lexer);
			Object res = null;
			for (Object o : parser.parse()) {
				System.out.printf("parsed as %s\n", o);
				res = e.eval(world, o);
			}
			System.out.printf("=> %s\n", res);
		}
	}

}
