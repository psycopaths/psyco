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

import java.util.HashMap;
import java.util.Vector;

import jfuzz.ConstraintsTree;
import jfuzz.JFuzz;
import jfuzz.Producer;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.Types;
import gov.nasa.jpf.perturb.GenericDataAbstractor.Valuation;

public class PsycoProducer extends Producer {
  static PsycoProducer producer = null;
    
  // Hash that maps each method name to a vector of Objects that represent
  // all the values that were applied to the parameters of that method
  static HashMap<String, Vector<Vector<Object>>> methodToValuations = null;
    
  // sequence valuation vectors. Created at the time of setMethodInfo and read
  // destructively
  static Vector<Vector<Object>> sequenceValuations = null;
  static Vector<String> sequenceFields = null;
  
  // current choice of valuation for optimized assignment
  static int currentChoice = 0;

  public PsycoProducer (Config conf, String keyPrefix){
    super(conf, keyPrefix);
    
    producer = this;
  }
  
  // method used to start storing valuation vectors for later re-use
  static public void captureVectors() {
  	methodToValuations = new HashMap<String, Vector<Vector<Object>>>();
    sequenceValuations = new Vector<Vector<Object>>();
    sequenceFields = new Vector<String>();
  }
  
  // Method to add a valuation vector corresponding to the values with which
  // the method with MethodInfo mi was called
  
  public static void addValuationVector(MethodInfo mi, Vector<Object> valuation) {
  	Vector<Vector<Object>> vectors = methodToValuations.get(mi.getFullName());
  	if (vectors == null) {
  		vectors = new Vector<Vector<Object>>();
  		methodToValuations.put(mi.getFullName(), vectors);
  	}
  	System.out.println(mi.getFullName() + " " + valuation.toString());
  	Vector<Object> clone = new Vector<Object>();
  	for (int i = 0; i < valuation.size(); i++)
  		clone.add(valuation.elementAt(i));
  	vectors.add(clone);
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
      if (JFuzz.startReuse) {
      	populateValuations();
      	choices = 1;
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
  	
  	Vector<String> sequenceMethods = new Vector<String>();
  	
		Config conf = JVM.getVM().getConfig();
		String[] methodNames = conf.getStringArray("sequence.methods");
		if (methodNames != null) {
			for (String methodName : methodNames) {
				String[] tokens = methodName.split(":");
				methodName = tokens[0];
				Vector<Vector<Object>> valuations = methodToValuations.get(methodName);
				if (valuations != null) {
					sequenceMethods.add(methodName);
				}
				// add fields for this method
				for (int i = 1; i < tokens.length; i++) {
					sequenceFields.add(tokens[i]);
				}
			}
			
			// we now have the sequence method names in a vector. We recursively
			// generate valuations using this sequence and the methodToValuations
			// map
			choices = populateValuations(sequenceMethods, null, 0 /* index */);
			
			System.out.println("We have " + choices + " choices of pre-valuated vectors");
			System.out.println("..and the valuations are");
  		for (int i = 0; i < sequenceValuations.size(); i++) {
  			Vector<Object> v = sequenceValuations.elementAt(i);
  			System.out.println("  " + v.toString());
  		}			
		}
		
		return choices;
  }
  
  // The worker bee that does the recursive addition of valuations
  
  public int populateValuations(Vector<String> sequenceMethods, Vector<Object> valuation, int index) {
  	int choices = 0;
  	if (index >= sequenceMethods.size())
  		return choices;
  	
  	String methodName = sequenceMethods.elementAt(index);
  	Vector<Vector<Object>> priorVectors = methodToValuations.get(methodName);
  	assert(priorVectors != null);
  	
  	for (int i = 0; i < priorVectors.size(); i++) {
  		Vector<Object> v = new Vector<Object>();
  		if (valuation != null)
  			v.addAll(valuation);
  		
  		v.addAll(priorVectors.elementAt(i));
  		int k = populateValuations(sequenceMethods, v, index + 1);
  		if (k != 0)
  			choices += k;
  		else
  			choices++;
  		
  		if (index == sequenceMethods.size() - 1)
  			sequenceValuations.add(v);
  	}
  	return choices;
  }

  // If we are in random perturbation mode, then revert to the base class
  // implementation. Otherwise, apply values from the set of valuations at
  // index 0, which would have been updated via a call to registerValues.
  
  public boolean perturb(ChoiceGenerator<?>cg, StackFrame frame) {    
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
  	if (currentChoice < sequenceValuations.size()) {
  		Vector<Object> valuation = sequenceValuations.elementAt(currentChoice);
  		
  		for (int i = 0; i < valuation.size(); i++) {
  			String fieldName = sequenceFields.elementAt(i);
  			Object value = valuation.elementAt(i);

  			String className = fieldName.substring(0, fieldName.lastIndexOf('.'));
  			if (className.equals(ci.getName()))
  				PsycoProducer.producer.registerClassFieldValue(null, 0, fieldName, value, null);
  		}
  		System.out.println("currentChoice = " + currentChoice);
			currentChoice++;
  	}
  	Producer.doDeferredAssignments(ci);
  }
}
