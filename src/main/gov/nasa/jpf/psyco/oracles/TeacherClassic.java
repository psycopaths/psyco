//
// Copyright (C) 2007 United States Government as represented by the
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

package gov.nasa.jpf.psyco.oracles;

// needs learning project
import gov.nasa.jpf.Config;
import gov.nasa.jpf.learn.classic.Candidate;
import gov.nasa.jpf.learn.classic.SETException;
import gov.nasa.jpf.learn.classic.SETLearner;
import gov.nasa.jpf.learn.classic.MinimallyAdequateTeacher;
import gov.nasa.jpf.learn.classic.MemoizeTable;


import gov.nasa.jpf.JPF;
import gov.nasa.jpf.util.JPFLogger;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.Vector;

/*
 * Teacher for interface generation in psyco using classic L*
 */


public class TeacherClassic  implements MinimallyAdequateTeacher {
  
  private JPFLogger logger = JPF.getLogger("teacher");
	private SETLearner set_;
  
  private MemoizeTable memoized_;
  private String module1_, module2_;
  private Config JPFargs_;
  private Vector alphabet_;
  
  private boolean refine = false;
  private String newAlphabet;
  
  
  	public TeacherClassic(Config conf) {
		
		memoized_ = new MemoizeTable();
    
    /* targetArgs are no longer relevant
     
		String[] targetArgs = conf.getTargetArgs();
		if (targetArgs.length < 1)
			throw new RuntimeException("No target arguments configured");
		
    */
    
    refine = false;
    module1_ = conf.getString("sut.class"); // this is our target class
    module2_ = null; // TODO change for compositional verification
		JPFargs_ = conf;	
		String[] alpha = conf.getStringArray("interface.alphabet");
		alphabet_ = new Vector();
    
    for (String a : alpha) {
				alphabet_.add(a);
			}
    }

  public boolean query(AbstractList<String> sequence) throws SETException {
    
    if (refine)
      return (true); // means we ignore all queries 
                     // when the alphabet will be refined 
    if (sequence.isEmpty()) 
      return true;
    Boolean recalled = memoized_.getResult(sequence);
		if (recalled != null) { // we get the result from memoized
			return (!recalled.booleanValue()); // note the fact that it is the other way around
		} else { // need to model check
			
      
      // first create sequence in format appropriate for Executor
      
      logger.info("New query: ", sequence);
      
      String[] programArgs = new String[sequence.size() + 1];
      programArgs[0] = "sequence";
      int counter = 1;
      for (String nextEl : sequence) {
        programArgs[counter] = module1_ + ":" + nextEl;
        counter++;
      }
            
			JPF jpf = createJPFInstance(programArgs); // driver for M1
			jpf.run();
      // call Zvon's stuff
      if (refine)
        return (true); // will be ignored anyway
			boolean violating = jpf.foundErrors();
			memoized_.setResult(sequence, violating);
			return (!violating);
		}
  }

  public Vector conjecture(Candidate cndt) throws SETException {    
    // TODO provide support
    return null;
  }

 public Iterator getAlphabetIterator() {
		return (alphabet_.iterator());
	}
 
 public Vector getAlphabet() {
   return alphabet_;
 }

	public Object getAssumption(Candidate c) {
		return c;
	}

	public void setSETLearner(SETLearner set) {
		set_ = set;
	}
  
	
  public JPF createJPFInstance(String[] programArgs) {
    JPFargs_.setTarget("gov.nasa.jpf.psyco.Target.ProgramExecutive");  // main program
		JPFargs_.setTargetArgs(programArgs); // arguments to main
		
		JPF jpf = new JPF(JPFargs_);
		return jpf;
	}
  
  public boolean refine() {
    return refine;
  }
  
  public void setRefine(boolean v) {
    refine = v;
  }

  public String getNewAlphabet() {
    return newAlphabet;
  }
	
}
  
