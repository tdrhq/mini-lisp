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

			try {
			    System.out.printf("=> %s\n", world.evalText(s));
			} catch (Exception ee) {
			    ee.printStackTrace();
			}
		}
	}

}
