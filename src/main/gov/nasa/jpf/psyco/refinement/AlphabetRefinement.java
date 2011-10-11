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

import gov.nasa.jpf.symbc.numeric.Constraint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

import jfuzz.ConstraintsTree;

public class AlphabetRefinement {
  public static final String REFINED_CLASS_NAME = "RefinedAlphabet";
  public static final int NUMBER_OF_METHOD_COPIES = 10;
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

  public String refine(ConstraintsTree constraintsTree) {
    queryCounter++;
    System.out.println("Query counter: " + queryCounter);
    if (constraintsTree == null) {
      return "OK";
    }
    System.out.println("Refining...");
    constraintsTree.printConstraintsTree();
    
    HashSet<String> methodNames = new HashSet<String>();
    constraintsTree.getMentionedMethods(1, methodNames);
    
    if (methodNames.isEmpty()) {
      return "OK";
    }

    HashSet<String> refinedSymbols = new HashSet<String>();
    boolean allErrors = true;
    boolean allCovered = true;
    for (String symbolName : methodNames) {
//      System.out.println("Symbol: " + symbolName);
//      System.out.println("Covered PCs...");
      ArrayList<ArrayList<Constraint>> coveredPCs = constraintsTree.getCoveredPathConstraints(symbolName);
      if (!coveredPCs.isEmpty()) {
        allErrors = false;
      }

//      System.out.println("Error PCs...");
      ArrayList<ArrayList<Constraint>> errorPCs = constraintsTree.getErrorPathConstraints(symbolName);
      if (!errorPCs.isEmpty()) {
        allCovered = false;
      }

      if (!coveredPCs.isEmpty() && !errorPCs.isEmpty()) {
        Precondition precondition = new Precondition(coveredPCs);
        String strippedSymbolName = symbolName.substring(symbolName.indexOf("_") + 1);
        Symbol oldSymbol = alphabet.getSymbol(strippedSymbolName);
        Symbol newSymbol = new Symbol(strippedSymbolName, symbolName, originalClassName, oldSymbol.getOriginalMethodName(), oldSymbol.getNumParams(), precondition);
        alphabet.addSymbol(newSymbol);

        precondition = new Precondition(errorPCs);
        newSymbol = new Symbol(strippedSymbolName, symbolName, originalClassName, oldSymbol.getOriginalMethodName(), oldSymbol.getNumParams(), precondition);
        alphabet.addSymbol(newSymbol);

        refinedSymbols.add(strippedSymbolName);
      }
    }

    if (allCovered) {
      assert !allErrors;
      return "OK";
    } else if (allErrors) {
      assert !allCovered;
      return "ERROR";
    } else {
      for (String refinedSymbolName : refinedSymbols) {
        System.out.println("Removed symbol: " + refinedSymbolName);
        alphabet.removeSymbol(refinedSymbolName);
      }
      writeAndCompileRefinement();
      String newAlphabet = alphabet.getSymbolsAsString();
      System.out.println("New refined alphabet: " + newAlphabet);
      return newAlphabet;
    }
  }
  
  private void writeAndCompileRefinement() {
    System.out.println(alphabet.toSource());
    BufferedWriter writer = null;
    try {
      String fileName = examplePath + "/" + REFINED_CLASS_NAME + ".java";
      writer = new BufferedWriter(new FileWriter(fileName));
      writer.write(alphabet.toSource());
      System.out.println("Written refined alphabet into " + fileName);
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
      System.out.println("Here is the standard output of the command:\n");
      while ((s = stdInput.readLine()) != null) {
        System.out.println(s);
      }

      // read any errors from the attempted command
      System.out.println("Here is the standard error of the command (if any):\n");
      while ((s = stdError.readLine()) != null) {
        System.out.println(s);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }    
  }

}
