  /*******************************************************************************
 * Copyright (C) 2008 United States Government as represented by the
 * Administrator of the National Aeronautics and Space Administration
 * (NASA).  All Rights Reserved.
 * 
 * This software is distributed under the NASA Open Source Agreement
 * (NOSA), version 1.3.  The NOSA has been approved by the Open Source
 * Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
 * directory tree for the complete NOSA document.
 * 
 * THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
 * KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
 * LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
 * SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
 * THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
 * DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
 ******************************************************************************/
package gov.nasa.jpf.psyco.oracles;

import com.google.common.base.Function;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionOracle;
import de.learnlib.api.Query;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.ConcolicExplorer;
import gov.nasa.jpf.jdart.JDart;
import gov.nasa.jpf.jdart.config.ConcolicMethodConfig;
import gov.nasa.jpf.jdart.config.ParamConfig;
import gov.nasa.jpf.jdart.constraints.ConstraintsTree;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionResult;
import gov.nasa.jpf.psyco.util.SEResultUtil;
import gov.nasa.jpf.util.TemplateBasedCompiler;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.automatalib.words.Word;

/**
 *
 * @author falkhowar
 */
public class JDartOracle implements SymbolicExecutionOracle {

  public static class PreconditionError extends java.lang.AssertionError {
    private static final long serialVersionUID = 1L;
    public PreconditionError(String msg) {
      super(msg);
    }
  }
  
  private static class MethodWrapper {    
    private final String name;
    private final String precondition;
    private final String call;
    public MethodWrapper(String name, String precondition, String call) {
      this.name = name;
      this.precondition = precondition;
      this.call = call;
    }
    public String getName() {
      return name;
    }
    public String getPrecondition() {
      return precondition;
    }
    public String getCall() {
      return call;
    }   
  }
  
  public static final String ALPHABET_PKG = "gov.nasa.jpf.psyco.oracles";
  
  public static final String ALPHABET_CLS = "JDartAlphabet";

  public static final String ALPHABET_CLASS = ALPHABET_PKG + "." + ALPHABET_CLS;
  
  private static final String CONCOLIC_METHOD = JDartOracleTarget.class.getName() + ".query()";
    
  private final Config config;
  
  private final SymbolicMethodAlphabet inputs;
  
  private int copies = 1;
  
  private int sigma = -1;
  
  private final String targetClasspath;
  
  public JDartOracle(Config config, SymbolicMethodAlphabet inputs) {
    this.config = config;
    this.inputs = inputs;
    this.targetClasspath = this.config.getProperty("classpath");
    
    this.config.setTarget(JDartOracleTarget.class.getName());
    this.config.setProperty("concolic.method", "q__uery");
    this.config.setProperty("concolic.method.q__uery", CONCOLIC_METHOD);
    //this.config.setProperty("concolic.method.q__uery.config","all_fields_symbolic");
    this.config.setProperty("concolic.method.q__uery.config","q__uery");
    this.config.setProperty("jdart.configs.q__uery.symbolic.include", 
            ALPHABET_CLASS + ";" + ALPHABET_CLASS + ".*");
    this.config.setProperty("jdart.configs.q__uery.symbolic.statics", ALPHABET_CLASS);
  }

  /* ***************************************************************************
   * 
   * ANSWERING QUERIES
   * 
   */  
  
  @Override
  public void processQueries(Collection<? extends Query<SymbolicMethodSymbol, SymbolicExecutionResult>> clctn) {
    for(Query<SymbolicMethodSymbol, SymbolicExecutionResult> query : clctn) {
      SymbolicExecutionResult result = processQuery(query.getInput());
      query.answer(result);
    }
  }
  
  public SymbolicExecutionResult processQuery(Word<SymbolicMethodSymbol> query) {
    compileAlphabetIfNecessary(query);
    this.config.setTargetArgs(queryToString(query));
    JDart jdart = new JDart(this.config);    
    ConcolicExplorer cex = jdart.run();
    ConstraintsTree ct = cex.getFirstCompletedAnalysis("q__uery").getConstraintsTree();    
    
    ArrayList<Path> errors = new ArrayList<>();
    for (Path p : ct.getErrorPaths()) {
      if (!p.getErrorResult().getExceptionClass().endsWith("PreconditionError")) {
        errors.add(p);
      }
    }
    
    SymbolicExecutionResult result = new SymbolicExecutionResult(
            ct.getCoveredPaths(), errors, ct.getDontKnowPaths());
    
    return SEResultUtil.rename(result, buildRenaming(query)); 
  }
  
  private String[] queryToString(Word<SymbolicMethodSymbol> query) {
    String[] ret = new String[query.length()];
    int i = 0;
    for (SymbolicMethodSymbol s : query) {
      ret[i++] = s.getId() + "_" + i;
    }
    return ret;
  }

  /* ***************************************************************************
   * 
   * ALPHABET COMPILATION
   * 
   */
  
  private void compileAlphabetIfNecessary(Word<SymbolicMethodSymbol> in) {    
    if (this.copies < in.length() || this.sigma < this.inputs.size()) {
      compileAlphabet(this.copies * 2);
    }    
  }
  
  private void compileAlphabet(int count) {
    this.copies = count;
    this.sigma = this.inputs.size();

    Map<String,Object> data = new HashMap<>();
    ArrayList<ParamConfig> params = new ArrayList<>();
    ArrayList<MethodWrapper> wrappers = new ArrayList<>();
    data.put("params", params);
    data.put("symbols", wrappers);
       
    for (SymbolicMethodSymbol sms : this.inputs) {
      String basename = sms.getId();
      Map<String, String> pmap = new HashMap<>();
      
      for (int copy=1; copy<=this.copies; copy++) {
        String name = basename + "_" + copy;
        for (int p=1; p<=sms.getArity(); p++) {
          pmap.put("P" + p, name + "_" + p);
        }
        String pcall = buildParams(sms, params, name);
        String call = buildMethodCall(sms, pcall);        
        if (sms.isConstructor()) {
          data.put("obj", "" + sms.getConcolicMethodConfig().getClassName() + " obj");
        }        
        
        //System.out.println(sms.getPrecondition());
        Expression<Boolean> precondition = SEResultUtil.stripLeadingTrue(
                ExpressionUtil.renameVars(sms.getPrecondition(), SEResultUtil.func(pmap)));        
                
        String preString = (precondition == null ? "true" : precondition.toString());        
        wrappers.add(new MethodWrapper(name, preString.replaceAll("'", ""), call));
      }      
    }
    
    try {
      TemplateBasedCompiler compiler = new TemplateBasedCompiler();
      compiler.addDynamicSource(ALPHABET_PKG, ALPHABET_CLS, data,
              JDartOracle.class.getResourceAsStream("/gov/nasa/jpf/psyco/Alphabet.st"));      
      
      File dir = compiler.compile(config);
      config.setProperty("classpath", this.targetClasspath + ";" 
              + dir.getAbsolutePath());
            
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }    
  }
  
  private String buildParams(SymbolicMethodSymbol sms, 
          Collection<ParamConfig> params, String basename) {    
    
    int ppos=1;
    String[] pcall = new String[sms.getConcolicMethodConfig().getParams().size()];
    for (ParamConfig pconf : sms.getConcolicMethodConfig().getParams()) {
      String pname = basename + "_" + ppos;
      pcall[ppos-1] = pname;
      params.add(new ParamConfig(pconf.getType(), pname));
      ppos++;
    }  
    return implode(pcall);
  }
  
  private Function<String, String> buildRenaming(Word<SymbolicMethodSymbol> in) {
    Map<String, String> map = new HashMap<>();
    
    int ppos = 1;
    int spos = 1;
    for (SymbolicMethodSymbol s : in) {
      int pcount = 1;
      for (ParamConfig p : s.getConcolicMethodConfig().getParams()) {
        map.put( ALPHABET_CLASS + "." + s.getId() + "_" + spos + "_" + pcount , "P" + ppos);
        pcount++;
        ppos++;
      }
      spos++;
    }
    
    return SEResultUtil.func(map);  
  }
          
  
  private String buildMethodCall(SymbolicMethodSymbol sms, String params) {
    ConcolicMethodConfig c = sms.getConcolicMethodConfig();
    String base = c.getMethodName() + "(" + params + ")";
    
    if (sms.isConstructor()) {
      return " obj = new " + base.replaceFirst(
              sms.getConcolicMethodConfig().getMethodName(), 
              sms.getConcolicMethodConfig().getClassName());
    }

    if (sms.isStatic()) {
      return sms.getConcolicMethodConfig().getClassName() + "." + base;
    } 
    
    return "obj." + base;
  }
  
  
  private String implode(String[] args) {
    return Arrays.toString(args).replace("[", "").replace("]", "");
  }
  
}
