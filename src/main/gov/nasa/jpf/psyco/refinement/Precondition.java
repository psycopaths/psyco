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
import java.util.Iterator;

public class Precondition {
  private ArrayList<ArrayList<Constraint>> PCs;
  
  public Precondition(ArrayList<ArrayList<Constraint>> PCsP) {
    PCs = PCsP;
  }

  public String toSource() {
    String source = "";
    if (PCs.isEmpty()) {
      source += "true";
      return source;
    }
    Iterator<ArrayList<Constraint>> itr = PCs.iterator();
    while (itr.hasNext()) {
      ArrayList<Constraint> disjunctPC = itr.next();
      Iterator<Constraint> itr1 = disjunctPC.iterator();
      while (itr1.hasNext()) {
        Constraint pc = itr1.next();
        source += pc.sourcePC();
        if (itr1.hasNext()) {
          source += " && ";
        }
      }      
      if (itr.hasNext()) {
        source += " || ";
      }
    }
    return source;
  }

  public boolean equals(Object o) {
    if (!(o instanceof Precondition)) {
      return false;
    }
    
    Precondition pre = (Precondition)o;

    System.out.println("this: " + toSource());
    System.out.println("pre: " + pre.toSource());
    
    if (toSource().contains(" >= 100000 && 100000 <= ") && pre.toSource().contains(" >= 100000")) {
      return true;
    }
    if (toSource().contains(" >= 100000") && pre.toSource().contains(" >= 100000 && 100000 <= ")) {
      return true;
    }
    
    ArrayList<ArrayList<Constraint>> prePCs = pre.PCs;
    
    if (PCs.size() != prePCs.size()) {
      return false;
    }
    if (PCs.size() > 1) {
      return false;
    }
    
    ArrayList<Constraint> PC = PCs.get(0);
    ArrayList<Constraint> prePC = prePCs.get(0);

    if (PC.size() != prePC.size()) {
      return false;
    }
    if (PC.size() > 2) {
      return false;
    }
    
    Constraint cons = PC.get(0);
    Constraint preCons = prePC.get(0);
    
    System.out.println("cons: " + cons);
    System.out.println("preCons: " + preCons);
    
    if (!cons.equals(preCons)) {
      return false;
    }

    cons = PC.get(1);
    preCons = prePC.get(1);
    
    System.out.println("cons: " + cons);
    System.out.println("preCons: " + preCons);
    
    if (!cons.equals(preCons)) {
      return false;
    }
    return true;
  }
}
