/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.explore;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.jdart.ConcolicExplorer;
import gov.nasa.jpf.jdart.ConcolicMethodExplorer;
import gov.nasa.jpf.jdart.constraints.ConstraintsTree;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.constraints.PostCondition;
import gov.nasa.jpf.psyco.compiler.Compiler;
import gov.nasa.jpf.psyco.compiler.Parameter;
import gov.nasa.jpf.psyco.summaries.MethodSummary;
import gov.nasa.jpf.psyco.summaries.MethodSummary.MethodPath;
import gov.nasa.jpf.psyco.util.*;
import gov.nasa.jpf.util.JPFLogger;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;


/**
 *
 * @author falk
 */
public class MethodExplorer {

  private JPFLogger logger = JPF.getLogger("psyco");
  private Config conf;
  private Method explore;
  private String methodName;
  private String className;
  private String packageName;

  private Valuation initialValues;
  
  public MethodExplorer(Method m, Config conf) {
    this.conf = conf;
    this.explore = m;
    this.methodName  = this.explore.getName();
    this.className   = this.explore.getDeclaringClass().getSimpleName();
    this.packageName = this.explore.getDeclaringClass().getCanonicalName();
    this.packageName = this.packageName.replace("." + className, "");  
  }
  
  
  public MethodSummary execute() {        

    String sig = MethodUtil.getMethodSignature(explore);
    logger.finest("exploring method: " + sig);
    prepareTestCase();

    logger.finest("creating JDartExplorer");
    JDartExplorer explorer = prepareJDart();
      
    logger.finest("starting JDartExplorer");
    explorer.run();    
      
    // FIXME: there should be a better way of getting the right explorer
    ConcolicMethodExplorer mex = ConcolicExplorer.getInstance().getMethodExplorers().iterator().next();
    ConstraintsTree ct = mex.getConstraintsTree();
    logger.finest(ct);
    this.initialValues = mex.getOrininalInitialValuation();
      
    Collection<MethodPath> paths = new ArrayList<MethodPath>();
    for (Path p : ct.getCoveredPaths()) {
      paths.add(new MethodPath(p.getPathCondition().getConstraint(), p.getPostCondition(), MethodSummary.PathState.OK));
    }
    for (Path p : ct.getErrorPaths()) {
      paths.add(new MethodPath(p.getPathCondition().getConstraint(), p.getPostCondition(), MethodSummary.PathState.ERROR));
    }
    for (Path p : ct.getDontKnowPaths()) {
      paths.add(new MethodPath(p.getPathCondition().getConstraint(), p.getPostCondition(), MethodSummary.PathState.DONT_KNOW));
    }
      
    MethodSummary summary = new MethodSummary(explore,paths);
    return summary;
  }    

  
  private void prepareTestCase() {
    
    // prepare template parameters
    Map<String,Object> attributes = new HashMap<String, Object>();
    attributes.put("testpackage", "temp");
    attributes.put("sutpackage", this.packageName);
    attributes.put("sutclass", this.className);
    attributes.put("methodName", this.methodName);
    
    List<Parameter> params = new ArrayList<Parameter>();
    int i= 1;
    for (Class<?> cl : this.explore.getParameterTypes()) {
      params.add(new Parameter(cl.getCanonicalName(), "P" + i++ ));           
    }
    
    attributes.put("params",params);    
    
    // compile example ...
    Compiler c = new Compiler(
            "MethodTestCase", 
            attributes, 
            conf, 
            "src/examples/temp");
    
    c.compile(false);    
  }
  
  
  private JDartExplorer prepareJDart() {            
    // FIXME: create a copy of conf
    Config jDartConf = conf;
  
    conf.setTarget("temp.MethodTestCase");  // main program
    conf.setProperty("symbolic.method", MethodUtil.getSymbolicPattern(explore));
    conf.setProperty("perturb.foo.method", MethodUtil.getPerturbPattern(explore));
    conf.setProperty("perturb.params", "foo");
    conf.setProperty("symbolic.classes", this.packageName + "." + this.className );
    conf.setProperty("sut.package", this.packageName);
    conf.setProperty("sut.class", this.className);    
    //conf.setProperty("vm.storage.class", null);
    conf.setProperty("jfuzz.timing","false");
    
    conf.setProperty("log.finest", "jdart");

    //return new JDartExplorer(jDartConf, true);
    return null;
  }
  
  public Valuation getInitialValues() {
    return initialValues;
  }
  
  
//  private PostCondition repairNamesPostCondition post) {
//
//    ConstraintsTree.PostCondition pcNew = new ConstraintsTree.PostCondition();
//    ExpressionNameCollector ec = new ExpressionNameCollector();
//    for (Expression e : post.getConditions().values()) {
//      // FIXME: this cast may fail in general
//      ec.walkOver( (IntegerExpression)e );
//    }
//
//    Set<String> rename = ec.getNames();
//    rename.addAll(post.getConditions().keySet());
//    Map<String,String> names = remapNames(rename);
//    
//    ExpressionRenamer er = new ExpressionRenamer(names);
//    for (String s : post.getConditions().keySet()) {
//      // FIXME: this cast may fail in general
//      Expression e = er.walkOver( (IntegerExpression) post.getConditions().get(s));
//      pcNew.getConditions().put(names.get(s), e);
//    }     
//      
//    return pcNew;
//  } 
//      
//  
//  private Formula repairNames(Formula f) {
//    FormulaNameCollector nc = new FormulaNameCollector();    
//    nc.walkOver(f);
//    FormulaRenamer nr = new FormulaRenamer(remapNames(nc.getNames()));
//    return nr.walkOver(f);
//  }  
//  
//  private Map<String,String> remapNames(Set<String> rename) {
////    System.out.println("Renaming: ");
//    Map<String,String> names = new HashMap<String,String>();
//    for (String name : rename) {
//      String stripped = name.substring(name.lastIndexOf(".") +1);
//      if (stripped.matches("P\\d+")) {
//        names.put(name,stripped);
////        System.out.println(name + " -> " + stripped);
//      } else {
//        // for globals keep classname ...
//        stripped = name.replace("__G.", "");
//        names.put(name, stripped);
////        System.out.println(name + " -> " + stripped);
//      }
//    }
//    return names;
//  }
  
}
