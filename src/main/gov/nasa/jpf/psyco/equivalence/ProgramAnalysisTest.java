/*
 * Copyright (C) 2015, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The PSYCO: A Predicate-based Symbolic Compositional Reasoning environment 
 * platform is licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may obtain a 
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 */
package gov.nasa.jpf.psyco.equivalence;

import com.google.common.io.Files;
import de.learnlib.oracles.DefaultQuery;
import gov.nasa.jpf.JPF;
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
import gov.nasa.jpf.psyco.learnlib.ThreeValuedOracle;
import gov.nasa.jpf.psyco.util.SEResultUtil;
import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.TemplateBasedCompiler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import net.automatalib.automata.transout.MealyMachine;
import net.automatalib.words.Word;

public class ProgramAnalysisTest implements SymbolicEquivalenceTest {

  
  private static final JPFLogger logger = JPF.getLogger("psyco");
  
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

  private final ThreeValuedOracle oracle;

  private Map<SymbolicMethodSymbol, Integer> idMap = new HashMap<>();
  private Map<Integer, SymbolicMethodSymbol> inverseMap = new HashMap<>();
  private Map<Path, Integer> pathMap = new HashMap<>();

  private MealyMachine<Object, SymbolicMethodSymbol, Object, SymbolicQueryOutput> hyp;

  private final String command;

  private final String[] params;

  public ProgramAnalysisTest(SummaryAlphabet inputs, ThreeValuedOracle oracle,
          String command, String[] params) {
    this.inputs = inputs;
    this.oracle = oracle;
    this.command = command;
    this.params = params;
  }

  @Override
  public DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> findCounterExample(
          MealyMachine<?, SymbolicMethodSymbol, ?, SymbolicQueryOutput> a,
          Collection<? extends SymbolicMethodSymbol> clctn) {

    try {
      this.hyp = (MealyMachine<Object, SymbolicMethodSymbol, Object, SymbolicQueryOutput>) a;
      //GraphDOT.write(this.hyp, inputs, System.out);

      // generate c file
      Map<String, Object> config = prepareTest();
      InputStream is = ProgramAnalysisTest.class.getResourceAsStream(
              "/gov/nasa/jpf/psyco/CEncoding.st");
      File tmpDir = Files.createTempDir();
      TemplateBasedCompiler compiler = new TemplateBasedCompiler(tmpDir);
      compiler.addDynamicSource("", "cex", config, is);

      // rename file 
      Files.move(new File(tmpDir, "cex.java"), new File(tmpDir, "cex.c"));
      logger.fine("Dir: " + tmpDir);

      // run blast
      boolean safe = runBlast(tmpDir);
      if (safe) {
        return null;
      }

      Word<SymbolicMethodSymbol> ce = Word.fromList(counterexample(tmpDir));

      DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> ceQuery
              = new DefaultQuery<>(ce);

      this.oracle.processQueries(Collections.singletonList(ceQuery));
      List<SymbolicQueryOutput> hypOut = new ArrayList<>();
      hyp.trace(ce, hypOut);

      logger.info("CE: " + ce);
      logger.info("SYS: " + ceQuery.getOutput());
      logger.info("HYP: " + hypOut.get(hypOut.size() - 1));
      return ceQuery;

    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }

  }

  @Override
  public void logStatistics() {
    // do nothing ...
  }

  private List<SymbolicMethodSymbol> counterexample(File tmpDir)
          throws FileNotFoundException, IOException {
    List<SymbolicMethodSymbol> ce = new ArrayList<>();

    BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(
                            new File(tmpDir, "output/Counterexample.1.c"))));

    String line = reader.readLine();
    while (line != null) {
      if (line.contains("method_id") && !line.contains("int")) {
        logger.fine(line);
        line = line.substring(line.indexOf("=") + 1, line.indexOf(";")).trim();
        Integer id = Integer.parseInt(line);
        ce.add(inverseMap.get(id));
      }
      line = reader.readLine();
    }
    reader.close();
    return ce;
  }

  private boolean runBlast(File tmpDir) throws IOException {

    String[] processParams = new String[this.params.length+2];
    processParams[0] = this.command;
    processParams[processParams.length-1] = "cex.c";
    System.arraycopy(this.params, 0, processParams, 1, this.params.length);
    logger.fine("BLAST COMMAND" + Arrays.toString(processParams));    
    
    ProcessBuilder pb = new ProcessBuilder(processParams);

    pb.directory(tmpDir);

    Process blast = pb.start();

    BufferedReader reader = new BufferedReader(
            new InputStreamReader(blast.getInputStream()));
    String line = reader.readLine();
    Boolean safe = null;
    while (line != null) {
      logger.fine("[BLAST] " + line);

      if (line.contains("Verification result: FALSE")) {
        safe = Boolean.FALSE;
      }
      if (line.contains("Verification result: TRUE")) {
        safe = Boolean.TRUE;
      }
      line = reader.readLine();
    }

    reader.close();
    if (safe == null) {
      throw new RuntimeException("Problem with blast");
    }

    return safe;
  }

  private DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> createCounterExample() {
    return null;
  }

  private Map<String, Object> prepareTest() {

    Map<String, String> renaming;
    idMap = new HashMap<>();
    inverseMap = new HashMap<>();
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
      inverseMap.put(ids, a);
      ids++;

      // parameters
      renaming = new HashMap<>();
      int pcount = 1;
      for (ParamConfig pc : a.getConcolicMethodConfig().getParams()) {
        renaming.put("P" + pcount, "P" + pid);
        // FIXME: what about other types ???
        Variable param = new Variable("int", "P" + pid, null);
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
        String name = v.getName().replaceAll("\\.", "_");
        renaming.put(v.getName(), name);
        Variable var = new Variable(translateType(v.getType()), name,
                e.getValue().toString().
                replaceAll("true", "1").
                replaceAll("false", "0"));
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

        String assertOk = createAssertion(summary.getOk());
        String assertErr = createAssertion(summary.getError());

        if (!error) {
          // ok case
          for (Path p : summary.getOk()) {

            String post = asAssignments(p.getPostCondition());
            if (summary.getOk().size() > 1) {
              pre += "&& (" + sanitize(p.getPathCondition()) + " || " + assertErr + ")";
            }
            Transition t = new Transition(pre, "" + idMap.get(a),
                    assertErr, post, "" + stateMap.get(succ), "" + pathMap.get(p));

            //System.out.println(t.id + " with " + t.pre + " to " + t.succ);
            //System.out.println("  Assertion: " + t.assertion);
            //System.out.println("  Post: " + t.post);
            trans.add(t);
          }
        } else {
          // error case
          if (summary.getError().size() < 1) {
            continue;
          }

          Transition t = new Transition(pre, "" + idMap.get(a),
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
    if (type.equals(BuiltinTypes.SINT32)
            || type.equals(BuiltinTypes.BOOL)) {
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
    for (Entry<gov.nasa.jpf.constraints.api.Variable<?>, Expression<?>> e
            : postCondition.getConditions().entrySet()) {

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
