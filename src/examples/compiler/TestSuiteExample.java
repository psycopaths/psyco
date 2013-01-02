/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFShell;
import gov.nasa.jpf.psyco.compiler.MethodWrapper;
import gov.nasa.jpf.psyco.compiler.Parameter;
import gov.nasa.jpf.psyco.testing.MethodChecks;
import gov.nasa.jpf.psyco.testing.TestCase;
import gov.nasa.jpf.psyco.testing.TestCaseGenerator;
import gov.nasa.jpf.psyco.testing.TestSuite;
import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.LogManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



/**
 *
 * @author falk
 */
public class TestSuiteExample implements JPFShell {
  
  private Config conf;  
  
  public TestSuiteExample(Config conf) {
    this.conf = conf;
    LogManager.init(conf);
    logger = JPF.getLogger("psyco");    
  }
  
  private JPFLogger logger;

  public void start(String[] strings) {
    
    logger.info("START...");

    List<Parameter> params = new ArrayList<Parameter>();
    params.add(new Parameter("java.util.List", "list0", "null"));
    
    // first test case    
    List<MethodWrapper> m0 = new ArrayList<MethodWrapper>();
    List<MethodChecks>  c0 = new ArrayList<MethodChecks>();
    
    m0.add(new MethodWrapper("list0 = new java.util.ArrayList();", "true"));
    c0.add(new MethodChecks(new String[] {"assertNotNull(list0)", "assertTrue(list0.isEmpty())"} ));
    
    m0.add(new MethodWrapper("list0.add(\"TEST\")", "list0.size() > 0"));
    c0.add(new MethodChecks());
    
    TestCase test0 = new TestCase(m0,c0,params);   

    // second test case
    List<MethodWrapper> m1 = new ArrayList<MethodWrapper>();
    List<MethodChecks>  c1 = new ArrayList<MethodChecks>();
    
    m1.add(new MethodWrapper("list0.clear();", "true"));
    c1.add(new MethodChecks("java.lang.NullPointerException"));
    
    TestCase test1 = new TestCase(m1,c1,params);  
    
    // create suite   
    Collection<TestCase> tests = new ArrayList<TestCase>();   
    tests.add(test0);
    tests.add(test1);   
    TestSuite suite = new TestSuite(tests,1);
    
    // generate
    TestCaseGenerator gen = new TestCaseGenerator(suite, "Demo", "temp", conf, "src/examples/temp");
    gen.generate();
    
         
  }  
  
}
