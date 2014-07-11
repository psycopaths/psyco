/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.nasa.jpf.psyco.equivalence;

import de.learnlib.oracles.DefaultQuery;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.types.BuiltinTypes;
import gov.nasa.jpf.constraints.types.Type;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.config.ParamConfig;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.constraints.PostCondition;
import gov.nasa.jpf.psyco.alphabet.SummaryAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.learnlib.SymbolicEquivalenceTest;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionResult;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.util.SEResultUtil;
import gov.nasa.jpf.util.TemplateBasedCompiler;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.automatalib.automata.transout.MealyMachine;
import net.automatalib.util.graphs.dot.GraphDOT;

/**
 *
 * @author falk
 */
public class ProgramAnalysisTest implements SymbolicEquivalenceTest { 
  
  private static class Variable {
    
    private final String type, name, val;
    
    public Variable(String type, String name, String val) {
      this.type = type;
      this.name = name;
      this.val = val;
    }
    
    public String getType() {
      return type;
    }
    
    public String getName() {
      return name;
    }

    public String getVal() {
      return val;
    }    
  }
  
  private static class Transition {
    
    private final String pre, id, assertion, post, succ, path;

    public Transition(String pre, String id, String assertion, 
            String post, String succ, String path) {
      this.pre = pre;
      this.id = id;
      this.assertion = assertion;
      this.post = post;
      this.succ = succ;
      this.path = path;
    }


    public String getPre() {
      return pre;
    }

    public String getId() {
      return id;
    }

    public String getAssertion() {
      return assertion;
    }

    public String getPost() {
      return post;
    }

    public String getSucc() {
      return succ;
    }    

    public String getPath() {
      return path;
    } 
  }
  
  private static class State {
    
    private final String id;
    private final List<Transition> trans;

    public State(String id, List<Transition> trans) {
      this.id = id;
      this.trans = trans;
    }

    public String getId() {
      return id;
    }

    public List<Transition> getTrans() {
      return trans;
    }    
  }
  
  private final SummaryAlphabet inputs; 

  private Map<SymbolicMethodSymbol, Integer> idMap = new HashMap<>();
  
  private Map<Path, Integer> pathMap = new HashMap<>();

  private MealyMachine<Object, SymbolicMethodSymbol, Object, SymbolicQueryOutput> hyp;
  
  public ProgramAnalysisTest(SummaryAlphabet inputs) {
    this.inputs = inputs;
  }
  
  
  @Override
  public DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> findCounterExample(
          MealyMachine<?, SymbolicMethodSymbol, ?, SymbolicQueryOutput> a, 
          Collection<? extends SymbolicMethodSymbol> clctn) {
    
    this.hyp = (MealyMachine<Object, SymbolicMethodSymbol, Object, SymbolicQueryOutput>) a;
    
    try {
      GraphDOT.write(this.hyp, inputs, System.out);
    } catch (IOException ex) {
    }
    
    Map<String, Object> config = prepareTest();

    InputStream is = ProgramAnalysisTest.class.getResourceAsStream(
            "/gov/nasa/jpf/psyco/CEncoding.st");
      
    TemplateBasedCompiler compiler = new TemplateBasedCompiler(new File("/tmp"));
    try {
      compiler.addDynamicSource("", "cex", config, is);
    } catch (IOException ex) {
    }
    
    throw new IllegalStateException("not implemented yet.");
  }
    
  @Override
  public void logStatistics() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  private boolean runBlast() {
    return false;
  }
  
  private DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> createCounterExample() {
    return null;
  }
  
  private Map<String, Object> prepareTest() {
    
    Map<String , String> renaming;
    idMap = new HashMap<>();
    pathMap = new HashMap<>();
    Map<String, Object> config = new HashMap<>();
    Map<Object, Integer> stateMap = new HashMap<>();
    Map<SymbolicMethodSymbol, SymbolicExecutionResult> summaries = new HashMap<>();
    List<Variable> methodIds = new ArrayList<>();
    List<Variable> vars = new ArrayList<>();
    List<Variable> pars = new ArrayList<>();
    List<State> states = new ArrayList<>();
    
    config.put("methodIds", methodIds);
    config.put("pars", pars);
    config.put("vars", vars);
    config.put("states", states);
      
    // method and path ids
    int ids = 1;
    int paths = 1;
    int pid = 1;
    for (SymbolicMethodSymbol a : this.inputs) {
      Variable mid = new Variable(null, "m_" + a.getId(), "" + ids);
      //System.out.println(mid.getName() + " = " + mid.getVal());
      methodIds.add(mid);
      idMap.put(a, ids);
      ids++;

      // parameters
      renaming = new HashMap<>();
      int pcount = 1;
      for (ParamConfig pc : a.getConcolicMethodConfig().getParams()) {
        renaming.put("P" + pcount, "P" + pid);
        Variable param = new Variable( pc.getType(), "P" + pid, null);
        //System.out.println(param.getType() + " " + param.getName() +  ";");
        pars.add(param);
        pcount++;
        pid++;
      }
      
      // prepare summary
      SymbolicExecutionResult summary = this.inputs.getSummary(a);
      summary = SEResultUtil.rename(summary, SEResultUtil.func(renaming));
      summaries.put(a, summary);
      for (Path p : summary) {
        pathMap.put(p, paths++);
      }      
    }
  
    // variables
    renaming = new HashMap<>();
    for (ValuationEntry e : this.inputs.getInitialValuation().entries()) {
      gov.nasa.jpf.constraints.api.Variable v = e.getVariable();
      if (v.getName().startsWith("this.")) {
        String name = v.getName().replace("this.", "this_");
        renaming.put(v.getName(), name);
        Variable var = new Variable(translateType(v.getType()), name, 
                e.getValue().toString());
        //System.out.println(var.getType() + " " + var.getName() + " = " + var.getVal() + ";");
        vars.add(var);
      }
    }

    // states
    Object initial = hyp.getInitialState();
    int sid = 0;
    stateMap.put(initial, sid++);
    for (Object o : hyp.getStates()) {
      if (o != initial) {
        stateMap.put(o, sid++);
      }
    }
    
    // transitions
    for (Object o : hyp.getStates()) {
      List<Transition> trans = new ArrayList<>();
      State s = new State("" + stateMap.get(o), trans);
      //System.out.println("--- State " + s.getId());
      states.add(s);
      for (SymbolicMethodSymbol a : this.inputs) {
      
        if ((o == initial) ^ a.isConstructor()) {
          continue;
        }
        Object _t = hyp.getTransition(o, a);
        SymbolicQueryOutput out = hyp.getTransitionOutput(_t);
        Object succ = hyp.getSuccessor(_t);
        
        // TODO: add dont know case 
        boolean error = out.equals(SymbolicQueryOutput.ERROR);        
        String pre = sanitize(a.getPrecondition());
        
        SymbolicExecutionResult summary = summaries.get(a);
        summary = SEResultUtil.rename(summary, SEResultUtil.func(renaming));
        
        String assertOk  = createAssertion(summary.getOk());
        String assertErr = createAssertion(summary.getError());
        
        if (!error) {
          // ok case
          for (Path p : summary.getOk()) {

            String post = asAssignments(p.getPostCondition());

            Transition t = new Transition( pre, "" + idMap.get(a), 
                    assertErr, post, "" + stateMap.get(succ), "" + pathMap.get(p));

            //System.out.println(t.id + " with " + t.pre + " to " + t.succ);
            //System.out.println("  Assertion: " + t.assertion);
            //System.out.println("  Post: " + t.post);

            trans.add(t);  
          }
        } 
        else {
          // error case
          if (summary.getError().size() < 1) {
            continue;
          }

          Transition t = new Transition( pre, "" + idMap.get(a), 
              assertOk, "break;", "" + stateMap.get(succ), "-1");

          //System.out.println(t.id + " with " + t.pre + " to " + t.succ);
          //System.out.println("  Assertion: " + t.assertion);
          //System.out.println("  Post: " + t.post);

          trans.add(t);  
        }
      }
    }
    
    return config;
  }
   
  private String translateType(Type type) {
    if (type.equals (BuiltinTypes.SINT32)) {
      return "int";
    }
    throw new IllegalArgumentException("Type not supported: " + type);
  }
   
  private String sanitize(Expression e) {
    String s = e.toString();
    s = s.replaceAll("'", "");
    s = s.replaceAll("true", "1");
    s = s.replaceAll("false", "0");
    
    return s;
  }

  private String asAssignments(PostCondition postCondition) {
    
   String ret = "";
   for (Entry<gov.nasa.jpf.constraints.api.Variable<?>, Expression<?>> e : 
           postCondition.getConditions().entrySet()) {
     
     String left = sanitize(e.getKey());
     String right = sanitize(e.getValue());
     
     if (left.startsWith("this_")) {
       ret += (left + "=" + right + "; ");
     }
   }
   return ret;
  }
  
  private String createAssertion(Collection<Path> paths) {
    List<Expression<Boolean>> errExpr = new ArrayList<>();
    for (Path p : paths) {
      errExpr.add(p.getPathCondition());
    }
    return sanitize(ExpressionUtil.or(errExpr));    
  }
  
}
