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
    Iterator<ArrayList<Constraint>> itr = PCs.iterator();
    while (itr.hasNext()) {
      ArrayList<Constraint> errorPC = itr.next();
      Iterator<Constraint> itr1 = errorPC.iterator();
      while (itr1.hasNext()) {
        Constraint pc = itr1.next();
        source += pc.sourcePC();
        if (itr1.hasNext()) {
          source += " && ";
        }
//        System.out.println(pc.stringPC());
      }      
      if (itr.hasNext()) {
        source += " || ";
      }
    }
    return source;
  }
}
