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

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.util.JPFLogger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

import solvers.Formula;
import solvers.LogicalExpression;
import solvers.LogicalOperator;
import solvers.NotExpression;

import jfuzz.ConstraintsTree;
import jfuzz.MixedParamsException;

public class AlphabetRefinement {
  public static final String REFINED_CLASS_NAME = "RefinedAlphabet";
  public static final int NUMBER_OF_METHOD_COPIES = 10;
  private JPFLogger logger = JPF.getLogger("refinement");
  private Alphabet alphabet;
  private String examplePath;
  private String originalClassName;
  private static int queryCounter = 0;
  
  public AlphabetRefinement(String examplePath, String packageName, String originalClassName) {
    alphabet = new Alphabet(packageName, REFINED_CLASS_NAME);
    this.examplePath = examplePath;
    this.originalClassName = originalClassName;
  }
  
  public void addInitialSymbol(String originalMethodName, int numParams) {
    Symbol initialSymbol = new Symbol(originalClassName, originalMethodName, numParams);
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

  public Symbol getSymbol(String symbolName) {
    return alphabet.getSymbol(symbolName);
  }

  public String refine(ConstraintsTree constraintsTree) {
    queryCounter++;
    logger.info("Refinement # " + queryCounter);
    assert constraintsTree != null;
    logger.info("Constraints tree:\n" + constraintsTree);

    if (constraintsTree.isEmpty()) {
      if (constraintsTree.inError()) {
        return "ERROR";
      } else {
        return "OK";
      }
    }

    HashSet<String> methodNames = new HashSet<String>();
    constraintsTree.getMentionedMethods(methodNames);
    assert !methodNames.isEmpty();
//    if (methodNames.isEmpty()) {
//      return "OK";
//    }

    HashSet<String> refinedSymbols = new HashSet<String>();
    boolean allErrors = true;
    boolean allCovered = true;
    boolean allDontKnow = true;
    for (String symbolName : methodNames) {
      logger.info("Processing symbol " + symbolName);

      String strippedSymbolName = symbolName.substring(symbolName.indexOf("_") + 1);
      if (refinedSymbols.contains(strippedSymbolName)) {
        // Don't refine again the same symbol
        continue;
      }
      Symbol oldSymbol = alphabet.getSymbol(strippedSymbolName);
      HashMap<String, String> replacementNames = new HashMap<String, String>();
      for (int i = 0; i < oldSymbol.getNumParams(); i++) {
        String oldParamName = symbolName + "_P" + i;
        String newParamName = strippedSymbolName + "_P" + i;
        replacementNames.put(oldParamName, newParamName);
      }

      Formula errorPCs;
      try {
        errorPCs = constraintsTree.getErrorPathConstraints(symbolName);
      } catch (MixedParamsException e) {
        return "UNKNOWN";
      }
      errorPCs.replaceNames(replacementNames);
      logger.info("Error PCs:" + errorPCs.sourcePC());

      Formula coveredDontKnowPCs;
      boolean errorPCsSatisfiable = errorPCs.isSatisfiable();
      if (errorPCsSatisfiable) {
        allCovered = false;
        allDontKnow = false;
        LogicalExpression andExpr = new LogicalExpression(LogicalOperator.AND);
        andExpr.addExpresion(oldSymbol.getPrecondition().getFormula());
        andExpr.addExpresion(new NotExpression(errorPCs));
        coveredDontKnowPCs = andExpr;
      } else {
        coveredDontKnowPCs = oldSymbol.getPrecondition().getFormula();
      }

      Formula dontKnowPCsTmp;
      try {
        dontKnowPCsTmp = constraintsTree.getDontKnowPathConstraints(symbolName);
      } catch (MixedParamsException e1) {
        return "UNKNOWN";
      }
      dontKnowPCsTmp.replaceNames(replacementNames);
      logger.info("DontKnow PCs Tmp:" + dontKnowPCsTmp.sourcePC());

      Formula coveredPCs;
      boolean dontKnowPCsSatisfiable = false;
      LogicalExpression dontKnowPCs = null;
      
      // if either dontKnowPCsTmp or coveredDontKnowPCs is false, then there is nothing to be done
      // insofar as computing dontKnowPCs. Therefore mark it as unsat and move on
      if (!dontKnowPCsTmp.sourcePC().equals("false") && !coveredDontKnowPCs.sourcePC().equals("false")) {      	
      	dontKnowPCs = new LogicalExpression(LogicalOperator.AND);
      	dontKnowPCs.addExpresion(coveredDontKnowPCs);
      	dontKnowPCs.addExpresion(dontKnowPCsTmp);
      	logger.info("DontKnow PCs:" + dontKnowPCs.sourcePC());

      	dontKnowPCsSatisfiable = dontKnowPCs.isSatisfiable();
      	if (dontKnowPCsSatisfiable) {
      		allErrors = false;
      		allCovered = false;
      		LogicalExpression andExpr = new LogicalExpression(LogicalOperator.AND);
      		andExpr.addExpresion(coveredDontKnowPCs);
      		andExpr.addExpresion(new NotExpression(dontKnowPCsTmp));
      		coveredPCs = andExpr;
      	} else {
      		coveredPCs = coveredDontKnowPCs;
      	}
      } else
      	coveredPCs = coveredDontKnowPCs;
      
      logger.info("Covered PCs:" + coveredPCs.sourcePC());

      boolean coveredPCsSatisfiable = coveredPCs.isSatisfiable();
      if (coveredPCsSatisfiable) {
        allErrors = false;
        allDontKnow = false;
      }        

      try {
        if (errorPCsSatisfiable && coveredPCsSatisfiable && dontKnowPCsSatisfiable) {
          Precondition preconditionCovered = new Precondition(coveredPCs.clone());
          Symbol newSymbol = new Symbol(strippedSymbolName, symbolName, originalClassName, oldSymbol.getOriginalMethodName(), oldSymbol.getNumParams(), preconditionCovered);
          alphabet.addSymbol(newSymbol);

          Precondition preconditionError = new Precondition(errorPCs.clone());
          newSymbol = new Symbol(strippedSymbolName, symbolName, originalClassName, oldSymbol.getOriginalMethodName(), oldSymbol.getNumParams(), preconditionError);
          alphabet.addSymbol(newSymbol);

          Precondition preconditionDontKnow = new Precondition(dontKnowPCs.clone());
          newSymbol = new Symbol(strippedSymbolName, symbolName, originalClassName, oldSymbol.getOriginalMethodName(), oldSymbol.getNumParams(), preconditionDontKnow);
          alphabet.addSymbol(newSymbol);

          refinedSymbols.add(strippedSymbolName);
        } else if (errorPCsSatisfiable && coveredPCsSatisfiable) {
          Precondition preconditionCovered = new Precondition(coveredPCs.clone());
          Symbol newSymbol = new Symbol(strippedSymbolName, symbolName, originalClassName, oldSymbol.getOriginalMethodName(), oldSymbol.getNumParams(), preconditionCovered);
          alphabet.addSymbol(newSymbol);

          Precondition preconditionError = new Precondition(errorPCs.clone());
          newSymbol = new Symbol(strippedSymbolName, symbolName, originalClassName, oldSymbol.getOriginalMethodName(), oldSymbol.getNumParams(), preconditionError);
          alphabet.addSymbol(newSymbol);

          refinedSymbols.add(strippedSymbolName);
        } else if (errorPCsSatisfiable && dontKnowPCsSatisfiable) {
          Precondition preconditionError = new Precondition(errorPCs.clone());
          Symbol newSymbol = new Symbol(strippedSymbolName, symbolName, originalClassName, oldSymbol.getOriginalMethodName(), oldSymbol.getNumParams(), preconditionError);
          alphabet.addSymbol(newSymbol);

          Precondition preconditionDontKnow = new Precondition(dontKnowPCs.clone());
          newSymbol = new Symbol(strippedSymbolName, symbolName, originalClassName, oldSymbol.getOriginalMethodName(), oldSymbol.getNumParams(), preconditionDontKnow);
          alphabet.addSymbol(newSymbol);

          refinedSymbols.add(strippedSymbolName);
        } else if (coveredPCsSatisfiable && dontKnowPCsSatisfiable) {
          Precondition preconditionCovered = new Precondition(coveredPCs.clone());
          Symbol newSymbol = new Symbol(strippedSymbolName, symbolName, originalClassName, oldSymbol.getOriginalMethodName(), oldSymbol.getNumParams(), preconditionCovered);
          alphabet.addSymbol(newSymbol);

          Precondition preconditionDontKnow = new Precondition(dontKnowPCs.clone());
          newSymbol = new Symbol(strippedSymbolName, symbolName, originalClassName, oldSymbol.getOriginalMethodName(), oldSymbol.getNumParams(), preconditionDontKnow);
          alphabet.addSymbol(newSymbol);

          refinedSymbols.add(strippedSymbolName);
        }
      } catch (CloneNotSupportedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
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
    } else {
      assert !refinedSymbols.isEmpty();
//      if (refinedSymbols.isEmpty()) {
//        return "ERROR";
//      }
      for (String refinedSymbolName : refinedSymbols) {
        alphabet.removeSymbol(refinedSymbolName);
      }
      writeAndCompileRefinement();
      String newAlphabet = alphabet.getSymbolsAsString();
      logger.info("New refined alphabet: " + newAlphabet);
      return newAlphabet;
    }
  }

  private void writeAndCompileRefinement() {
    logger.fine(alphabet.toSource());
    BufferedWriter writer = null;
    try {
      String fileName = examplePath + "/" + REFINED_CLASS_NAME + ".java";
      writer = new BufferedWriter(new FileWriter(fileName));
      writer.write(alphabet.toSource());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      try {
        if (writer != null) {
          writer.close( );
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    try {
      String s = null;
      Process p = Runtime.getRuntime().exec("ant compile-examples");

      BufferedReader stdInput = new BufferedReader(new InputStreamReader(
          p.getInputStream()));

      BufferedReader stdError = new BufferedReader(new InputStreamReader(
          p.getErrorStream()));

      // read the output from the command
      logger.fine("Here is the standard output of the compile command:\n");
      while ((s = stdInput.readLine()) != null) {
        logger.fine(s);
      }

      // read any errors from the attempted command
      logger.fine("Here is the standard error of the compile command (if any):\n");
      while ((s = stdError.readLine()) != null) {
        logger.fine(s);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }    
  }

}
