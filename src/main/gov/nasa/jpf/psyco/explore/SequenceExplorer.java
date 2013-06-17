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
package gov.nasa.jpf.psyco.explore;

import java.util.AbstractList;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.jdart.constraints.ConstraintsTree;
import gov.nasa.jpf.psyco.PsycoConfig;
import gov.nasa.jpf.psyco.refinement.AlphabetRefinement;

// The main class to explore a method symbolically

public class SequenceExplorer {

  public enum ExplorationMethod {
    Java, JPF, JDart
  }

  Config config;

  SymbolicExplorer explorer = null;  
  
  PsycoConfig psy;
  
  public SequenceExplorer (Config conf, PsycoConfig psy, ExplorationMethod explorationMethod, boolean psyco,
  		AbstractList<String> sequence, AlphabetRefinement refiner) {

    this.config = conf;
    this.psy = psy;
    
    // now create a string array of methods in the sequence and use that to get
    // the sequence.methods string from the refiner
    String[] target_args = new String[sequence.size()+1];
    target_args[0] = "sequence";
    if (sequence != null) {
    	int index = 0;
    	for (String s : sequence) {
    		target_args[index+1] = "temp.Alphabet:" + s + "_" + (index);
        index++;
    	}
      
      this.config.setTarget("gov.nasa.jpf.psyco.target.ProgramExecutive");      
      this.config.setTargetArgs(target_args);
    }
    
    this.psy.addSymbolicMethod("gov.nasa.jpf.psyco.target.ProgramExecutive.sequence()");
    this.psy.addAssertion("temp.Alphabet$TotallyPsyco");
    this.psy.addConcolicClass("temp.Alphabet");

    // create concrete explorer
    switch (explorationMethod) {
    case JDart:
      explorer = new NewDartExplorer(conf, psy);
    	break;
    default:
    	throw new IllegalArgumentException("JDart is the only symbolic exploration technique currently supported");
    }    
  }
  
  // reset method for external use
  public void reset() {
    explorer.reset();
  }
      
  // method to start exploring
   
  public void run() {
  	explorer.run();
  }
  
  public ConstraintsTree getConstraintsTree(String methodName) {
  	return explorer.getConstraintsTree(methodName);
  }
  
  public boolean inError(ConstraintsTree T) {
  	return explorer.inError(T);
  }
}

