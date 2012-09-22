package in.tdrhq.lisp;


import java.io.File;

import org.junit.Test;

import junit.framework.TestCase;

public class LoadingIntegrationTests extends TestCase {

    @Test
    public void test() {
        File dir = new File("../lisp/integration-tests");
        File[] fileList = dir.listFiles();
        
        for (File f : fileList) {
            if (!f.getName().endsWith(".lisp")) {
                 continue;
            }
                
            World world = new World();
            world.evalText("(load \"../lisp/prelude.lisp\")");
            world.evalText("(load \"../lisp/types.lisp\")");
            world.evalText("(load \"../lisp/reflect.lisp\")");
            world.evalText("(load \"../lisp/package.lisp\")");
            
            world.evalText(NativeLibrary.readFileAsString(f.getAbsolutePath()), f.getAbsolutePath());
        }
            
    }
}
