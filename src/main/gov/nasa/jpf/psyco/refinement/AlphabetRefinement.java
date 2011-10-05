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

import java.util.ArrayList;
import jfuzz.ConstraintsTree;

public class AlphabetRefinement {
  private static Alphabet alphabet = new Alphabet();

  
  public static void refine(String methodName, ConstraintsTree constraintsTree) {
    System.out.println("Refining...");
    constraintsTree.printConstraintsTree();

    System.out.println("Error PCs...");
    ArrayList<ArrayList<Constraint>> errorPCs = constraintsTree.getErrorPathConstraints();
    Precondition precondition = new Precondition(errorPCs);
    Symbol symbol = new Symbol(methodName, precondition);
    alphabet.addSymbol(symbol);
    
    System.out.println("Covered PCs...");
    ArrayList<ArrayList<Constraint>> coveredPCs = constraintsTree.getCoveredPathConstraints();
    precondition = new Precondition(coveredPCs);
    symbol = new Symbol(methodName, precondition);
    alphabet.addSymbol(symbol);
    System.out.println(alphabet.toSource());
  }
    
   
}
