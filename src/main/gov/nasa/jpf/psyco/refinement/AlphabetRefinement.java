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
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.util.JPFLogger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import jfuzz.ConstraintsTree;

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
    constraintsTree.getMentionedMethods(1, methodNames);
    
    if (methodNames.isEmpty()) {
      return "OK";
    }

    HashSet<String> refinedSymbols = new HashSet<String>();
    boolean allErrors = true;
    boolean allCovered = true;
    for (String symbolName : methodNames) {
      logger.info("Processing symbol " + symbolName);
      ArrayList<ArrayList<Constraint>> coveredPCs = constraintsTree.getCoveredPathConstraints(symbolName);
      if (!coveredPCs.isEmpty()) {
        allErrors = false;
      }

      ArrayList<ArrayList<Constraint>> errorPCs = constraintsTree.getErrorPathConstraints(symbolName);
      if (!errorPCs.isEmpty()) {
        allCovered = false;
      }

      if (!coveredPCs.isEmpty() && !errorPCs.isEmpty()) {
        String strippedSymbolName = symbolName.substring(symbolName.indexOf("_") + 1);
        Symbol oldSymbol = alphabet.getSymbol(strippedSymbolName);

        Precondition preconditionCovered = new Precondition(coveredPCs);
        Precondition preconditionError = new Precondition(errorPCs);

        if (!preconditionCovered.equals(preconditionError)) {
          Symbol newSymbol = new Symbol(strippedSymbolName, symbolName, originalClassName, oldSymbol.getOriginalMethodName(), oldSymbol.getNumParams(), preconditionCovered);
          alphabet.addSymbol(newSymbol);

          newSymbol = new Symbol(strippedSymbolName, symbolName, originalClassName, oldSymbol.getOriginalMethodName(), oldSymbol.getNumParams(), preconditionError);
          alphabet.addSymbol(newSymbol);

          refinedSymbols.add(strippedSymbolName);
        }
      }
    }

    if (allCovered) {
      assert !allErrors;
      return "OK";
    } else if (allErrors) {
      assert !allCovered;
      return "ERROR";
    } else {
      if (refinedSymbols.isEmpty()) {
        return "ERROR";
      }
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
