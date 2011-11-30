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

import java.util.Vector;

import solvers.PathCondition;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.jdart.bytecode.BytecodeUtils;
import gov.nasa.jpf.jvm.bytecode.ATHROW;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.FieldInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.symbc.numeric.MinMax;
import jfuzz.*;

/* This class extends the Perturbator and is at the heart of the jdart
 * execution framework. The method defers to the perturbator to invoke a
 * method with a set of values as picked up by the generic data abstractor.
 * Upon termination of default perturbation, the constraints tree is examined
 * for unknowns. These unknowns are solved and solutions found are replayed
 * by re-executing the method in a manner identical to the perturbator.
 */

public class PsycoListener extends ConcolicListener {
	PsycoListener listener;

	public PsycoListener(Config conf) {
	  super(conf);
	  // TODO Auto-generated constructor stub
	  listener = this;
  }
	
  // The following method is used to keep track of valuation vectors that have been
  // applied to each method of interest. We use this to optimize sequences of
  // method calls
  
	@Override
	public void registerValuationVector(Instruction insn, ThreadInfo ti, MethodInfo mi, boolean isSequenceMethod) {
		super.registerValuationVector(insn, ti, mi, isSequenceMethod);

		// now register the valuation vector with Producer
  	if (isSequenceMethod)
  		PsycoProducer.addValuationVector(mi, latestValuation);
  }
	
	@Override
  public void executeInstruction (JVM vm) {
    ThreadInfo ti = vm.getLastThreadInfo();
    Instruction insn = vm.getLastInstruction();

    if (insn instanceof InvokeInstruction) {
      MethodInfo mi = ((InvokeInstruction) insn).getInvokedMethod();
      if (isMethodWatched(insn, mi)) {
      	super.executeInstruction(vm);
      } else {
        mi = ((InvokeInstruction) insn).getInvokedMethod();
        String methodName = mi.getName();
        if (!methodName.equals("<clinit>") &&
        		!methodName.equals("init")) {
        	// now pick up the sequence methods and stuff them into sequenceMethods
      		Config conf = JVM.getVM().getConfig();
      		sequenceMethods = conf.getStringArray("sequence.methods");
      		if (sequenceMethods != null) {
      			for (int i = 0; i < sequenceMethods.length; i++) {
      				sequenceMethods[i] = sequenceMethods[i].substring(0, sequenceMethods[i].indexOf(':'));
      			}
      			for (String sequenceMethodName : sequenceMethods) {
      				if (sequenceMethodName.equals(mi.getFullName())) {
      					registerValuationVector(insn, ti, mi, true);
      					break;
      				}
      			}
        	}
        	ClassInfo ci = mi.getClassInfo();
        	if (BytecodeUtils.doDeferredSymbolicFieldProcessing(ci)) 
        		PsycoProducer.doDeferredAssignments(ci);
//  		  		if (JDartExplorer.explorer.sequenceLength() > 1) {
//  		  			ClassInfo x = ClassInfo.getInitializedClassInfo("sequencetest.State", JVM.getVM().getCurrentThread());
//  		  			if (x != null) {
//  		  				FieldInfo y = x.getStaticField("moreResets");
//  		  				if (y != null) {
//  		  					ElementInfo z = x.getStaticElementInfo();
//  		  					if (z != null)
//  		  						z.setIntField(y, (Integer)1);
//  		  				}
//  		  			}
//  		  		}
        }
      }
    } else if (insn instanceof ATHROW) {
    	super.executeInstruction(vm);
    }
  }

	@Override
  public void instructionExecuted(JVM vm) {
    super.instructionExecuted(vm);
  }
  
	@Override
  public void methodExited(JVM vm) {
    Instruction insn = vm.getLastInstruction();

    MethodInfo mi = insn.getMethodInfo();
    if (isMethodWatched(insn, mi)) {
      PathCondition pc = BytecodeUtils.getPC();
      if (pc == null)
        return;
//      System.out.println("Adding " + pc.stringPC());
      ThreadInfo ti = vm.getLastThreadInfo();
      ConstraintsTree T = ConstraintsTree.getTree(ti.getTopFrame(), mi);
      assert T != null;
      T.insertPC(pc, latestValuation);
      // the following calls ensure that a subsequent re-exec of the
      // method will maintain the same symbolic names for all
      // symbolic inputs
      MinMax.reset();
      
      // the following call resets all field attributes for a potential
      // re-exec of this symbolic method
      BytecodeUtils.resetPC();

      //      T.printConstraintsTree();
    }
  }

	@Override
  public void exceptionThrown(JVM vm) {
  	super.exceptionThrown(vm);
  }
}
