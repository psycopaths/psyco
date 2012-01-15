package gov.nasa.jpf.psyco.explore;

//
// Copyright (C) 2010 United States Government as represented by the
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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.sun.tools.internal.xjc.ModelLoader;

import jfuzz.ConstraintsTree;
import jfuzz.JFuzz;
import jfuzz.Producer;
import jfuzz.Producer.Associator;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.Types;
import gov.nasa.jpf.perturb.GenericDataAbstractor.Valuation;
import gov.nasa.jpf.util.JPFLogger;

public class PsycoProducer extends Producer {
  private static JPFLogger logger = JPF.getLogger("jdart");
  static PsycoProducer producer = null;
    
  // Hash that maps each method name to a vector of Objects that represent
  // all the values that were applied to the parameters of that method
  static HashMap<String, Vector<Vector<Object>>> methodToValuations =
  		new HashMap<String, Vector<Vector<Object>>>();
    
  // sequence valuation vectors. Created at the time of setMethodInfo and read
  // destructively
  static Vector<Vector<Object>> sequenceValuations = null;
  static Vector<String> sequenceFields = null;
  
  // the following static map keeps track of all the methods that we have fully
  // explored. We update this map during cleanup
  static HashSet<String> exploredMethods = new HashSet<String>();
  
  // current choice of valuation for optimized assignment
  static int currentChoice = 0;
  
  // start re-use when set
  static boolean startReuse = false;
  
  public PsycoProducer (Config conf, String keyPrefix){
    super(conf, keyPrefix);
    
    if (sequenceValuations == null) {
    	currentChoice = 0;
      sequenceValuations = new Vector<Vector<Object>>();
      sequenceFields = new Vector<String>();
    }
    
    producer = this;
  }
  
  public static void initialize() {
  	startReuse = startReuse();
  	
  	currentChoice = 0;
  	if (sequenceValuations != null)
  		sequenceValuations.clear();
  	if (sequenceFields != null)
  		sequenceFields.clear();
  }
  
  public static void cleanup() {
  	currentChoice = 0;
  	startReuse = false;
  	
  	if (sequenceValuations != null)
  		sequenceValuations.clear();
  	if (sequenceFields != null)
  		sequenceFields.clear();
    
    // iterate over methodToValuations and put the methods found into
    // exploredMethods. We do this step only for sequences of length 1
    if (JDartExplorer.explorer.sequenceLength() == 1) {
      @SuppressWarnings("rawtypes")
      Iterator itr = methodToValuations.entrySet().iterator();
      while (itr.hasNext()) {
        @SuppressWarnings("unchecked")
        Map.Entry<String, Vector<Vector<Object>>> next = (Map.Entry<String, Vector<Vector<Object>>>)itr.next();
        Map.Entry<String, Vector<Vector<Object>>> e = next;
        String methodName = (String)e.getKey();
        if (!exploredMethods.contains(methodName))
        	exploredMethods.add(methodName);
      }    	
    }
  }
  
  // Method to add a valuation vector corresponding to the values with which
  // the method with MethodInfo mi was called
  
  public static void addValuationVector(MethodInfo mi, Vector<Object> valuation) {
  	// if we have already fully processed this method then simply return
  	String toCompare = mi.getFullName();
  	toCompare = toCompare.substring(0, toCompare.length() - 1);
  	if (exploredMethods.contains(toCompare))
  		return;

  	// otherwise, clone and add the valuation vector to the methodToValuations map
  	String key = mi.getFullName();
  	key = key.substring(0, key.length() - 1);
  	Vector<Vector<Object>> vectors = methodToValuations.get(key);
  	if (vectors == null) {
  		vectors = new Vector<Vector<Object>>();
  		methodToValuations.put(key, vectors);
  	}
  	if (!vectors.contains(valuation)) {
//  	System.out.println(mi.getFullName() + " " + valuation.toString());
  		Vector<Object> clone = new Vector<Object>();
  		for (int i = 0; i < valuation.size(); i++)
  			clone.add(valuation.elementAt(i));
  		vectors.add(clone);
  	}
  }
  
  // The following method is used to check if we can re-use prior vectors.
  // Given a sequence of methods that we want to explore, we check if each 
  // of the methods in the sequence are in exploredMethods. If they are, then
  // we have prior vectors that we can use to optimize exploration
  
  public static boolean startReuse() {
  	if (JDartExplorer.explorer.sequenceLength() == 0)
  		return false;
  	
  	String[] sequenceMethodNames = JDartExplorer.explorer.sequenceMethodNames();
  	for (int i = 0; i < sequenceMethodNames.length; i++) {
  		if (exploredMethods.contains(sequenceMethodNames[i])) {
  			return true;
  		}
  	}
  	return false;
  }

  // The following method defers to the base class implementation for
  // initialization. If there exists a constraints tree for the method
  // and their are unknowns in the tree, then the method sets inRandomMode
  // to false, which will prevent the random initialization of the random
  // vector as per the implementation in the GenericDataAbstractor, but will
  // instead solve the first unknown and stop as soon as a solution is found.
  // Since solveUnknowns automatically updates the valuations vector, a 
  // subsequent call to create a choice generator will indeed create one
  // with as many choices (1) as the number of valuations in the valuations
  // vector

  public void setMethodInfo(MethodInfo m, StackFrame frame) {  	
    ConstraintsTree T = ConstraintsTree.getTree(frame, m);
    if (T == null) {
    	super.setMethodInfo(m, frame);

      // optimization for setting global state
      // Build a set of valuations based on previous exploration of 
      // methods in isolation. The exploration of methods in isolation will
      // yield all valuations to cover the methods. In this mode we simply
      // apply all those previously stored values first before looking for
      // unknowns in the constraints tree
      if (startReuse) {
      	choices = populateValuations();
//      	choices = 1;
      	currentChoice = 0;
      }
    } else {
      inRandomMode.put(m.getFullName(), false);
    	if (!hasChoices())
    		super.setMethodInfo(m, frame);
    	else {
    		choices = 1;
        mi = m;
        stackFrame = frame;
    	}
    }
  }
  
  // Method that walks over all the methods declared in sequence.methods
  // and builds valuation vectors over class global variables to exhaustively 
  // apply all values that have been applied to these methods in isolation before. 
  // Using this mechanism, we "perturb" the method to explore with values from 
  // these previously obtained values. The mechanism works only for global
  // symbolic variables
  
  public int populateValuations() {
  	choices = 0;
  	
    sequenceValuations = new Vector<Vector<Object>>();
    sequenceFields = new Vector<String>();

    Vector<String> sequenceMethods = new Vector<String>();
  	
		Config conf = JVM.getVM().getConfig();
		String[] methodNames = conf.getStringArray("sequence.methods");
		if (methodNames != null) {
			for (String methodName : methodNames) {
				String[] tokens = methodName.split(":");
				methodName = tokens[0];
				methodName = methodName.substring(0, methodName.length() - 1);
				sequenceMethods.add(methodName);
				
				// add fields for this method
				for (int i = 1; i < tokens.length; i++) {
					sequenceFields.add(tokens[i]);
				}
			}
			
			// we now have the sequence method names in a vector. We recursively
			// generate valuations using this sequence and the methodToValuations
			// map
			populateValuations(sequenceMethods, null, 0 /* index */);
			choices = sequenceValuations.size();
			
			logger.info("We have " + choices + " choices of pre-valuated vectors");
			logger.info("..and the valuations are");
  		for (int i = 0; i < sequenceValuations.size(); i++) {
  			Vector<Object> v = sequenceValuations.elementAt(i);
  			logger.info("  " + v.toString());
  		}			
		}
		
		return choices;
  }
  
  // The worker bee that does the recursive addition of valuations
  
  public void populateValuations(Vector<String> sequenceMethods, Vector<Object> valuation, int index) {
  	if (index >= sequenceMethods.size())
  		return;
  	
  	String methodName = sequenceMethods.elementAt(index);
  	Vector<Vector<Object>> priorVectors = methodToValuations.get(methodName);

  	if (priorVectors != null && priorVectors.size() > 0) {
  		for (int i = 0; i < priorVectors.size(); i++) {
  			Vector<Object> v = new Vector<Object>();
  			if (valuation != null)
  				v.addAll(valuation);

  			v.addAll(priorVectors.elementAt(i));
  			populateValuations(sequenceMethods, v, index + 1);

  			if (index == sequenceMethods.size() - 1 && v.size() > 0)
  				sequenceValuations.add(v);
  		}
  	} else {
			Vector<Object> v = new Vector<Object>();
			if (valuation != null)
				v.addAll(valuation);
			populateValuations(sequenceMethods, v, index + 1);
			
			if (index == sequenceMethods.size() - 1 && v.size() > 0)
				sequenceValuations.add(v);
  	}
  }

  // If we are in random perturbation mode, then revert to the base class
  // implementation. Otherwise, apply values from the set of valuations at
  // index 0, which would have been updated via a call to registerValues.
  
  public boolean perturb(ChoiceGenerator<?>cg, StackFrame frame) {
  	if (hasChoices())
  		return false;
  	
    if (mi == null || inRandomMode.get(mi.getFullName())) {
    	return super.perturb(cg, frame);
    }

    // now that we are out of random mode, the reason we are here
    // is because we have a model to replay. Hence set the state 
    // of the first unknown constraint to satisfiable and proceed
    // - handles the case where an assignment is obtained from the
    // solver but the assignment is bogus (talk to me for an explanation)

    if (!hasChoices()) {
    	return super.perturb(cg, frame);
    }
    
    return false;
  }
  
  // method to check if we are in random mode to do deferred assignments
  
  static public boolean hasChoices() {
  	if (sequenceValuations == null || sequenceValuations.size() == 0)
  		return false;
  	
  	if (currentChoice < sequenceValuations.size())
  		return true;
  	return false;
  }

  // The following method is used to do deferred setting of values of fields
  // of classes that are outside of the class that defines the method we want
  // to execute concolically. These classes are subject to late initialization
  // when a method of these classes gets called. The late initialization will
  // clobber values we set at the very beginning of the execution of the 
  // symbolic method. We perform deferred setting of values of these class
  // fields in this method
  //
  // Note: The mechanism only supports static fields at this point

  static public void doDeferredAssignments(ClassInfo ci) {
		boolean processedChoice = false;
  	if (currentChoice < sequenceValuations.size()) {
  		Vector<Object> valuation = sequenceValuations.elementAt(currentChoice);
  	
  		if (valuation != null) {
  			for (int i = 0; i < valuation.size(); i++) {
  				String fieldName = sequenceFields.elementAt(i);
  				Object value = valuation.elementAt(i);

  				String className = fieldName.substring(0, fieldName.lastIndexOf('.'));
  				if (className.equals(ci.getName())) {
  					PsycoProducer.producer.registerClassFieldValue(null, 0, fieldName, value, null);
  					processedChoice = true;
  				}
  			}
  		}
  		if (processedChoice) {
  		  logger.info("currentChoice = " + currentChoice);
  			sequenceValuations.setElementAt(null, currentChoice);
  			currentChoice++;
  		}
  	}
  	
  	// if there is an internalReset method in the class we want to do assignments
  	// on, then call that method before setting a subset of the fields
  	if (processedChoice) {
  		JDartExplorer.internalReset();
  	}

    Producer.doDeferredAssignments(ci);
  }
}
