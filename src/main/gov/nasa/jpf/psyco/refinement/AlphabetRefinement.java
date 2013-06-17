//
// Copyright (C) 2008 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package gov.nasa.jpf.psyco.refinement;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.LogicalOperator;
import gov.nasa.jpf.constraints.expressions.Negation;
import gov.nasa.jpf.constraints.expressions.PropositionalCompound;
import gov.nasa.jpf.jdart.ConcolicConfig.MethodConfig;
import gov.nasa.jpf.jdart.constraints.ConstraintsTree;
import gov.nasa.jpf.psyco.PsycoConfig;
import gov.nasa.jpf.testing.compiler.MethodWrapper;
import gov.nasa.jpf.testing.compiler.Parameter;
import gov.nasa.jpf.psyco.util.ExpressionRestrictor;
import gov.nasa.jpf.psyco.util.ExpressionSimplifier;
import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.SimpleProfiler;

import java.util.*;
import java.util.Map.Entry;


public class AlphabetRefinement {

  public static class MixedParamException extends RuntimeException {  
  }
  
  public static final String REFINED_CLASS_NAME = "RefinedAlphabet";
  public static final int NUMBER_OF_METHOD_COPIES = 10;
  private JPFLogger logger = JPF.getLogger("psyco");
  private Alphabet alphabet;

  private static int queryCounter = 0;
  
  private PsycoConfig pconf;

  private Config compilerConfig;
  
  public AlphabetRefinement(PsycoConfig pconf, Config cconf) {
    this.pconf = pconf;
    this.compilerConfig = cconf;
    this.alphabet = new Alphabet();
  }
  
//  public AlphabetRefinement(String examplePath, String packageName, String originalClassName) {
//    alphabet = new Alphabet(packageName, REFINED_CLASS_NAME);
//    this.examplePath = examplePath;
//    this.originalClassName = originalClassName;
//  }
  
  public void addInitialSymbol(MethodConfig mc) {
    MethodSymbol initialSymbol = new MethodSymbol(mc,mc.getMethodName());
    alphabet.addSymbol(initialSymbol);
  }
  
  public String createInitialRefinement() {
    writeAndCompileRefinement();
    return alphabet.getSymbolsAsString();
  }

  public HashMap<String, String> getSymbolsToPreconditions() {
    return alphabet.getSymbolsToPreconditions();
  }

  public HashMap<String, String> getSymbolsToMethodNames() {
    return alphabet.getSymbolsToMethodNames();
  }

  public String getSequenceMethodsForJDart(String[] sequence) {
    return alphabet.getSequenceMethodsForJDart(sequence);
  }

  public MethodSymbol getSymbol(String symbolName) {
    return alphabet.getSymbol(symbolName);
  }

  public String refine(ConstraintsTree constraintsTree) {       
    queryCounter++;
    logger.fine("Refinement # " + queryCounter);
    assert constraintsTree != null;
    logger.finest("Constraints tree:\n" + constraintsTree);

    if (constraintsTree.isEmpty()) {
      if (constraintsTree.inError()) {
        return "ERROR";
      } else {
        return "OK";
      }
    }

    Set<String> methodNames = getMentionedMethodNames(constraintsTree);
    ExpressionSimplifier simplifier = new ExpressionSimplifier();
//    constraintsTree.getMentionedMethods(methodNames);
//    assert !methodNames.isEmpty();
//    if (methodNames.isEmpty()) {
//      return "OK";
//    }
    
    HashSet<String> refinedSymbols = new HashSet<String>();
    boolean allErrors = true;
    boolean allCovered = true;
    boolean allDontKnow = true;
    
    for (String symbolName : methodNames) {
      boolean dontKnowPCsSatisfiable = false;
      boolean errorPCsSatisfiable = false;
      boolean coveredPCsSatisfiable = false;

      String strippedSymbolName = symbolName.substring(
              symbolName.lastIndexOf(".")+1, symbolName.lastIndexOf("_"));
      Integer copyID = Integer.parseInt(symbolName.substring(symbolName.lastIndexOf("_")+1));
      logger.fine("Processing symbol " + strippedSymbolName + ", instance " + copyID);
              
      if (refinedSymbols.contains(strippedSymbolName)) {
        // Don't refine again the same symbol
        continue;
      }
      MethodSymbol oldSymbol = alphabet.getSymbol(strippedSymbolName);
      Map<Expression, Expression> replacements = getReplacementsForCopy(oldSymbol,copyID,true);
      for (Entry e : replacements.entrySet()) {
        logger.finest(e.getKey() + " -> " + e.getValue());        
      }
      Set<Variable> restriction = new HashSet(replacements.keySet());
      ExpressionRestrictor restrictor = new ExpressionRestrictor(restriction);            

      logger.finest("Precondition:" + oldSymbol.getPrecondition());

      Expression<Boolean> errorPCs = constraintsTree.getErrorConstraint();      
      logger.finest("Error PCs (orig):" + errorPCs);
      errorPCs = restrictor.restrict(errorPCs);
      logger.finest("Error PCs (rest):" + errorPCs);
      if (restrictor.hasMixedParameters()) {
        return "UNKNOWN";        
      }

      errorPCs = constraintsTree.getErrorConstraintRestricted(restriction);
      logger.finest("Error PCs (tree):" + errorPCs);
      errorPCs = errorPCs.replaceTerms(replacements);      
      logger.finest("Error PCs (repl):" + errorPCs);
      errorPCs = simplifier.simplify(errorPCs);
      logger.finest("Error PCs:" + errorPCs);
      
      Expression<Boolean> coveredDontKnowPCs;      
      ConstraintSolver solver = pconf.getSolver();
      
      SimpleProfiler.start("PSYCO-solve-refinement-constraint");
      Result errorResult = solver.isSatisfiable(errorPCs, pconf.getMinMax());
      SimpleProfiler.stop("PSYCO-solve-refinement-constraint");
      if (errorResult.equals(Result.SAT)) {
        errorPCsSatisfiable = true;
        allCovered = false;
        allDontKnow = false;
        
        PropositionalCompound andExpr = new PropositionalCompound(
                oldSymbol.getPrecondition(), LogicalOperator.AND, new Negation(errorPCs));
        coveredDontKnowPCs = andExpr;
      } 
      else {
        coveredDontKnowPCs = oldSymbol.getPrecondition();
      }

      Expression<Boolean> dontKnowPCsTmp = constraintsTree.getDontKnowConstraint();
      dontKnowPCsTmp = restrictor.restrict(dontKnowPCsTmp);
      logger.finest("DontKnow PCs Tmp:" + dontKnowPCsTmp);
      if (restrictor.hasMixedParameters()) {
        return "UNKNOWN";        
      }

      dontKnowPCsTmp = constraintsTree.getDontKnowConstraintRestricted(restriction);
      dontKnowPCsTmp = dontKnowPCsTmp.replaceTerms(replacements);
      dontKnowPCsTmp=  simplifier.simplify(dontKnowPCsTmp);

      Expression<Boolean> coveredPCs;
      Expression dontKnowPCs = null;
      
      // if either dontKnowPCsTmp or coveredDontKnowPCs is false, then there is nothing to be done
      // insofar as computing dontKnowPCs. Therefore mark it as unsat and move on
      if (!dontKnowPCsTmp.toString().equals("false") && !coveredDontKnowPCs.toString().equals("false")) {      	
      	dontKnowPCs = new PropositionalCompound(
          coveredDontKnowPCs, LogicalOperator.AND, dontKnowPCsTmp);

        dontKnowPCs = simplifier.simplify(dontKnowPCs);
        logger.finest("DontKnow PCs:" + dontKnowPCs);

        SimpleProfiler.start("PSYCO-solve-refinement-constraint");
        Result dontKnowResult = solver.isSatisfiable(dontKnowPCs, pconf.getMinMax());
        SimpleProfiler.stop("PSYCO-solve-refinement-constraint");
      	if (dontKnowResult.equals(Result.SAT)) {
          dontKnowPCsSatisfiable = true;
      		allErrors = false;
      		allCovered = false;
          PropositionalCompound andExpr = new PropositionalCompound(
                coveredDontKnowPCs, LogicalOperator.AND, new Negation(dontKnowPCsTmp));          
      		coveredPCs = andExpr;
      	} 
        else {
      		coveredPCs = coveredDontKnowPCs;
      	}
      } 
      else {
      	coveredPCs = coveredDontKnowPCs;
      }

      coveredPCs = simplifier.simplify(coveredPCs);      
      logger.finest("Covered PCs:" + coveredPCs);

      SimpleProfiler.start("PSYCO-solve-refinement-constraint");
      Result coveredPCsResult = solver.isSatisfiable(coveredPCs, pconf.getMinMax());
      SimpleProfiler.stop("PSYCO-solve-refinement-constraint");
      if (coveredPCsResult.equals(Result.SAT)) {
        coveredPCsSatisfiable = true;
        allErrors = false;
        allDontKnow = false;
      }        

      int satisfiable = (errorPCsSatisfiable ? 1 : 0) + (coveredPCsSatisfiable ? 1 : 0) + (dontKnowPCsSatisfiable ? 1 : 0);
      if (satisfiable < 2) {
        continue;
      }
      
      refinedSymbols.add(strippedSymbolName);      
      if (errorPCsSatisfiable) {
        alphabet.addSymbol(new MethodSymbol(oldSymbol.getMethod(), simplifier.simplify(errorPCs), strippedSymbolName + "_E"));        
      }        
      if (coveredPCsSatisfiable) {
        alphabet.addSymbol(new MethodSymbol(oldSymbol.getMethod(), simplifier.simplify(coveredPCs), strippedSymbolName + "_C"));
      }
      if (dontKnowPCsSatisfiable) {
        alphabet.addSymbol(new MethodSymbol(oldSymbol.getMethod(), simplifier.simplify(dontKnowPCs), strippedSymbolName + "_D"));
      } 
    }

    if (allCovered) {
      assert !allErrors && !allDontKnow;
      return "OK";
    } else if (allErrors) {
      assert !allCovered && !allDontKnow;
      return "ERROR";
    } else if (allDontKnow) {
      assert !allCovered && !allErrors;
      return "UNKNOWN";
    } 
    else {
      assert !refinedSymbols.isEmpty();
//      if (refinedSymbols.isEmpty()) {
//        return "ERROR";
//      }
      
      logger.info(constraintsTree);
      
      for (String refinedSymbolName : refinedSymbols) {
        alphabet.removeSymbol(refinedSymbolName);
      }
      writeAndCompileRefinement();
      String newAlphabet = alphabet.getSymbolsAsString();
      logger.info("New " + alphabet);
      return newAlphabet;
    }
  }

  private void writeAndCompileRefinement() {
    
    List<Parameter> params = new LinkedList<Parameter>();
    List<MethodWrapper> symbols = new LinkedList<MethodWrapper>();
    
    for (MethodSymbol ms : this.alphabet) { 
      for (int copy=0; copy < NUMBER_OF_METHOD_COPIES; copy++) {
        String methodName = ms.getSymbolName() + "_" + copy;
        Map<Expression, Expression> replacements = getReplacementsForPrecondition(ms, methodName);      
        
        // create params
        String[] pnames = new String[ms.getNumParams()];
        for (int p=0;p<pnames.length;p++) {
          String pname = methodName + "_" + ms.getMethod().getParamNames()[p];          
          Parameter param = new Parameter(ms.getMethod().getParamTypes()[p].getName(), pname);
          pnames[p] = pname;
          params.add(param);
        }
        
        // create invokation
        String plist = Arrays.toString(pnames).replace("[", "").replace("]", "");        
        String call = ms.getMethod().getClassName() + "." + ms.getMethod().getMethodName() + "(" + plist + ")";
    
        // create condition
        Expression<Boolean> precondition = ms.getPrecondition();
        Set<Variable> vars = new HashSet<Variable>();
        precondition = precondition.replaceTerms(replacements);

        // add symbol
        symbols.add(new MethodWrapper(call, precondition.toString(), methodName));      
      }                  
    }
    
    // compile alphabet
    Map<String,Object> attributes = new HashMap<String,Object>();        
    attributes.put("params", params);
    attributes.put("symbols", symbols);
    attributes.put("package", "temp");
    
    gov.nasa.jpf.testing.compiler.Compiler cc = new gov.nasa.jpf.testing.compiler.Compiler(
            "Alphabet", attributes, compilerConfig, "src/examples/temp");    
    cc.compile(false);        
  }

  
  private Set<String> getMentionedMethodNames(ConstraintsTree constraintsTree) {
    Set<String> methodNames = new HashSet<String>();
    for (Variable v  : constraintsTree.getVariables()) {
      // FIXME: this works only if no-one uses "_" in variable names ...
      methodNames.add(v.getName().substring(0,v.getName().lastIndexOf("_")));
    }
    return methodNames;
  }
    
  
  public static Map<Expression,Expression> getReplacementsForCopy(MethodSymbol ms, int copyId, boolean reverse) {
    Map<Expression,Expression> ret = new HashMap<Expression,Expression>();
    String copyMethodName = ms.getSymbolName() + "_" + copyId;
    String className = ms.getMethod().getClassName();
    for (int p=0;p<ms.getNumParams();p++) {
      String origName = className + "." + ms.getOriginalMethodName() + "()."  + ms.getMethod().getParamNames()[p];;         
      String copyName = "temp.Alphabet." + copyMethodName + "_" + ms.getMethod().getParamNames()[p];          
      Class type = ms.getMethod().getParamTypes()[p];
      if (type.equals(int.class)) {
        type = Integer.class;
      }
      Variable orig = new Variable(type,origName);
      Variable copy = new Variable(type,copyName);
      if (reverse) {
        ret.put(copy, orig);
      } else {
        ret.put(orig, copy);
      }
    }
    return ret;
  }

  public static Map<Expression,Expression> getReplacementsForPrecondition(MethodSymbol ms, String symbolName) {
    Map<Expression,Expression> ret = new HashMap<Expression,Expression>();
    String methodName = ms.getMethod().getMethodName();
    String className = ms.getMethod().getClassName();
    for (int p=0;p<ms.getNumParams();p++) {
      String origName = className + "." + methodName + "()."  + ms.getMethod().getParamNames()[p];;         
      String copyName = symbolName + "_" + ms.getMethod().getParamNames()[p];          
      Class type = ms.getMethod().getParamTypes()[p];
      if (type.equals(int.class)) {
        type = Integer.class;
      }
      Variable orig = new Variable(type,origName);
      Variable copy = new Variable(type,copyName);
      ret.put(orig, copy);
    }
    return ret;
  }  
}
