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

import jfuzz.ConstraintsTree;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.util.LogManager;

// The main class to explore a method symbolically

public class SequenceExplorer {
  public enum ExplorationMethod {
    Java, JPF, JDart
  }

  Config config;
  SymbolicExplorer explorer = null;

  static boolean startReuse = false;

  public SequenceExplorer (Config conf, ExplorationMethod explorationMethod, boolean psyco) {
    this.config = conf;
    LogManager.init(conf);
    
    switch (explorationMethod) {
    case JDart:
    	explorer = new JDartExplorer(conf, psyco);
    	break;
    default:
    	throw new IllegalArgumentException("JDart is the only symbolic exploration technique currently supported");
    }

    startReuse = false;
    
    // Now check if we have a sequence of length one or more. 
    // For all sequences of length one, we automatically set the exploration to run in the 
    // vector capture mode. For sequences longer than one, we set the exploration to run in
    // the vector re-use mode.
    String sequenceMethods = config.getProperty("sequence.methods");
    if (sequenceMethods != null) {
    	String[] methodSpecs = sequenceMethods.split(",");
    	if (methodSpecs.length > 1) {
    		startReuse = true;
    		explorer.startReuse();
    	} else {
    		explorer.capture();
    	}
    } else {
    	explorer.capture();
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

