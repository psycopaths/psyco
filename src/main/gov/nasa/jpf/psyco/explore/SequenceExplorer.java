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

import jfuzz.ConstraintsTree;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.psyco.refinement.AlphabetRefinement;
import gov.nasa.jpf.util.LogManager;

// The main class to explore a method symbolically

public class SequenceExplorer {
  public enum ExplorationMethod {
    Java, JPF, JDart
  }

  Config config;
  SymbolicExplorer explorer = null;

  public SequenceExplorer (Config conf, ExplorationMethod explorationMethod, boolean psyco,
  		AbstractList<String> sequence, AlphabetRefinement refiner) {
    this.config = conf;
    LogManager.init(conf);
    
    // now create a string array of methods in the sequence and use that to get
    // the sequence.methods string from the refiner
    String sequenceMethods = null;
    
    if (sequence != null) {
    	String[] sequenceStrings = new String[sequence.size()];
    	int index = 0;
    	for (String s : sequence) {
    		sequenceStrings[index++] = s;
    	}
    	sequenceMethods = refiner.getSequenceMethodsForJDart(sequenceStrings);
    	config.setProperty("sequence.methods", sequenceMethods);
    } else {
    	sequenceMethods = config.getProperty("sequence.methods");
    }
    
    switch (explorationMethod) {
    case JDart:
    	explorer = new JDartExplorer(config, psyco);
    	break;
    default:
    	throw new IllegalArgumentException("JDart is the only symbolic exploration technique currently supported");
    }
    
//    System.out.println("Sequence methods string = " + sequenceMethods);    
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

