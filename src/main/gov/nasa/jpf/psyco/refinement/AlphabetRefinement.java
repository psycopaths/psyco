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
import jfuzz.ConstraintsTree;

public class AlphabetRefinement {
  private static Alphabet alphabet = new Alphabet();

  // return enum type (true, false, refined)
  public static void refine(String[] symbols, ConstraintsTree constraintsTree) {
    System.out.println("Refining...");
    constraintsTree.printConstraintsTree();
    
    Symbol initialSymbol = new Symbol("init", "init", 2, new Precondition(new ArrayList<ArrayList<Constraint>>()));
    alphabet.addSymbol(initialSymbol);
    initialSymbol = new Symbol("a", "a", 0, new Precondition(new ArrayList<ArrayList<Constraint>>()));
    alphabet.addSymbol(initialSymbol);
    initialSymbol = new Symbol("b", "b", 0, new Precondition(new ArrayList<ArrayList<Constraint>>()));
    alphabet.addSymbol(initialSymbol);

    for (String symbolName : symbols) {
      System.out.println("Symbol: " + symbolName);
      System.out.println("Error PCs...");
      ArrayList<ArrayList<Constraint>> errorPCs = constraintsTree.getErrorPathConstraints(symbolName);
      Precondition precondition = new Precondition(errorPCs);
      Symbol newSymbol = new Symbol(symbolName, alphabet.getSymbol(symbolName).getMethodName(), alphabet.getSymbol(symbolName).getNumParams(), precondition);
      alphabet.addSymbol(newSymbol);
    
      System.out.println("Covered PCs...");
      ArrayList<ArrayList<Constraint>> coveredPCs = constraintsTree.getCoveredPathConstraints(symbolName);
      precondition = new Precondition(coveredPCs);
      newSymbol = new Symbol(symbolName, alphabet.getSymbol(symbolName).getMethodName(), alphabet.getSymbol(symbolName).getNumParams(), precondition);
      alphabet.addSymbol(newSymbol);
    }

    System.out.println(alphabet.toSource());
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter("/Users/zrakamar/projects/jpf/jpf-psyco/src/examples/simple2/ExampleAlphabet.java"));
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
      Process p = Runtime.getRuntime().exec("/Users/zrakamar/projects/jpf/jpf-psyco/bin/compile_example");

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
