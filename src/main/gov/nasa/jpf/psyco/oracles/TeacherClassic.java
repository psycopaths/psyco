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
import gov.nasa.jpf.psyco.refinement.AlphabetRefinement;
import gov.nasa.jpf.util.JPFLogger;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.Vector;
import jfuzz.ConstraintsTree;
import jfuzz.JFuzz;
import gov.nasa.jpf.learn.classic.Candidate;
import gov.nasa.jpf.psyco.refinement.Symbol;
import java.util.HashMap;
import gov.nasa.jpf.psyco.Target.ProgramExecutive;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/*
 * Teacher for interface generation in psyco using classic L*
 */
public class TeacherClassic implements MinimallyAdequateTeacher {

  public static final boolean CONCR = false;
  public static final boolean SYMB = true;
  public static final String TARGET = "gov.nasa.jpf.psyco.Target.ProgramExecutive";
  private JPFLogger logger = JPF.getLogger("teacher");
  private SETLearner set_;
  private MemoizeTable memoized_;
  private String module1_, module2_;
  private Config JPFargs_;
  private Vector alphabet_;
  private boolean refine = false;
  private String newAlphabet;
  // by default, mode is concrete
  private static boolean mode = CONCR;
  private static AlphabetRefinement alphaRefiner = null;
  private int memoizeHits = 0;

  public TeacherClassic(Config conf, AlphabetRefinement ref) {

    memoized_ = new MemoizeTable();

    /* targetArgs are no longer relevant
    
    String[] targetArgs = conf.getTargetArgs();
    if (targetArgs.length < 1)
    throw new RuntimeException("No target arguments configured");
    
     */

    refine = false;
    alphaRefiner = ref;
    module1_ = conf.getString("sut.package") + "." + conf.getString("sut.class"); // this is our target class
    module2_ = null; // TODO change for compositional verification
    JPFargs_ = conf;
    String[] alpha = conf.getStringArray("interface.alphabet");
    alphabet_ = new Vector();

    logger.info("New teacher alphabet is: ");
    for (String a : alpha) {
      alphabet_.add(a);
      logger.info(a);
    }

    if (mode == SYMB) { 
      setUpJDartConfig();
    }

  }

  
  // allow reuse of memoized table after refinements
  public TeacherClassic(Config conf, AlphabetRefinement ref, MemoizeTable mem) {

    if (mem == null) {
      memoized_ = new MemoizeTable();
    } else {
      memoized_ = mem;
    }
    
    /* targetArgs are no longer relevant
    
    String[] targetArgs = conf.getTargetArgs();
    if (targetArgs.length < 1)
    throw new RuntimeException("No target arguments configured");
    
     */

    refine = false;
    alphaRefiner = ref;
    module1_ = conf.getString("sut.package") + "." + conf.getString("sut.class"); // this is our target class
    module2_ = null; // TODO change for compositional verification
    JPFargs_ = conf;
    String[] alpha = conf.getStringArray("interface.alphabet");
    alphabet_ = new Vector();

    logger.info("New teacher alphabet is: ");
    for (String a : alpha) {
      alphabet_.add(a);
      logger.info(a);
    }

    if (mode == SYMB) {
      setUpJDartConfig();
    }

  }
  
  private String getPrefix(String action, HashMap hm) {
      return ("PSYCO" + hm.get(action) + "_");
  }
  
  private void resetTarget() {

    try {
      Class<?> invokedClass = Class.forName(module1_);
      try {
        Method clinit = invokedClass.getDeclaredMethod("internalReset");
        clinit.setAccessible(true);
        try {
          clinit.invoke(null);
        } catch (Throwable e) {
          System.err.println("Problem accessing internalReset?");
        }

      } catch (NoSuchMethodException e2) {
        System.err.println("Method not found for name internalReset");
      };
    } catch (ClassNotFoundException e1) {
      System.err.println("Class not found: " + module1_);
    }

  }

  public boolean query(AbstractList<String> sequence) throws SETException {
    return (query(sequence, true));
  }

  public boolean query(AbstractList<String> sequence, boolean memoize) throws SETException {

    if (refine) {
      return (true); // means we ignore all queries 
    }                     // when the alphabet will be refined 

    // the following assumes that initial state of target is not error
    if (sequence.isEmpty()) {
      return true;
    }
    
    
    logger.info("New query: ", sequence);
    
    Boolean recalled;
    
    if (mode == SYMB) {
      recalled = memoized_.getSimulatedResult(sequence);
    } else {
      recalled = memoized_.getResult(sequence);
    }


    if (recalled != null) { // we get the result from memoized
      logger.info("Result from memoized for sequence: ", sequence);
      logger.info("Result is: ", !recalled.booleanValue());
      memoizeHits++;
      return (!recalled.booleanValue()); // note the fact that it is the other way around      
    }

    // we have not returned so we need to model check



    // first create sequence in format appropriate for Executor

    

    // is there any parameters involved?
    boolean parameters_involved = false;

    if (mode == SYMB) {
      for (String nextEl : sequence) {
        if ((alphaRefiner.getSymbol(nextEl)).getNumParams() > 0) {
          parameters_involved = true; // there are parameters so may need to refine
          break; // exit the loop
        }
      }
    }
    
   
    //parameters_involved = true;
    
    String[] programArgs = null;
    int counter = 0;

    if (mode == CONCR || !parameters_involved) {
      programArgs = new String[sequence.size() + 1];
      programArgs[0] = "sequence";
      counter = 1;
    } else if (mode == SYMB) {
      programArgs = new String[sequence.size() + 2]; // also need to call init() first for jdart to work
      programArgs[0] = "sequence";
      programArgs[1] = JPFargs_.getProperty("sut.package") + "." + AlphabetRefinement.REFINED_CLASS_NAME + ":" + "init";
      counter = 2;
    }

    // create the PSYCO names for the refiner

    HashMap hm = new HashMap();
    for (String nextEl : sequence) {

      if (!hm.containsKey(nextEl)) {
        hm.put(nextEl, new Integer(0));
      }

      if (mode == CONCR) {
        programArgs[counter] = module1_ + ":" + nextEl;
      } else if (!parameters_involved) {
        programArgs[counter] = module1_ + ":" + (alphaRefiner.getSymbol(nextEl)).getOriginalMethodName();
      } else { // symbolic mode and parameters involved
        programArgs[counter] = JPFargs_.getProperty("sut.package") + "." + AlphabetRefinement.REFINED_CLASS_NAME + ":" + getPrefix(nextEl, hm) + nextEl;
      }

      counter++;
      Integer value = (Integer) hm.get(nextEl);
      value++;
      hm.put(nextEl, value);
    }

    if ((mode == SYMB) && (!parameters_involved)) {
      logger.info("---------- NO PARAMETERS - RUN JAVA------------");
      // just execute with simple java - avoid JPF altogether
      boolean result = true;
      try {
        resetTarget();
        ProgramExecutive.main(programArgs);
      } catch (Throwable e) {
        // make sure exception is due to assert false and not to some mistake...
        if (!((e.getCause()).toString()).equals("java.lang.AssertionError")) {
          System.err.println("Unexpected exception caught during query");
        }

        result = false;
      }
      if (memoize) {
        memoized_.setResult(sequence, !result); // memoized stores them the other way around
      }
      logger.info("Result from running Java is ", result);
      return result;
    }


    if (mode == SYMB) {
      logger.info("---------- RUN CONCOLIC ------------");
      // TODO
      // first call jpf-jdart when it is ready and get the constraints tree
      // for the moment just get a new constraints tree

      JFuzz jfuzz = createJDartInstance(programArgs);
      jfuzz.runJDart();
      ConstraintsTree ct = jfuzz.getConstraintsTree(TARGET + ".sequence()V");
      String result = alphaRefiner.refine(ct);
      logger.info("Refinement result: " + result);
      if (result.equals("OK")) {
        if (memoize) {
          memoized_.setResult(sequence, false); // memoized stores them the other way around
        }
        return true;
      } else if (result.equals("ERROR")) {
        if (memoize) {
          memoized_.setResult(sequence, true); // memoized stores them the other way around
        }
        return false;
      } else { // must refine
        refine = true;
        newAlphabet = result;
        return (true); // will be ignored since refined was set to true
      }
    } else {  // mode is concrete
      logger.info("---------- RUN JPF CONCRETE MODE ------------");
      JPF jpf = createJPFInstance(programArgs); // driver for M1
      jpf.run();
      boolean violating = jpf.foundErrors();
      if (memoize) {
        memoized_.setResult(sequence, violating);
      }
      return (!violating);
    }
    
  }

  public Vector conjecture(Candidate cndt) throws SETException {

    if (refine) {
      return null; // we need to ignore conjectures because alphabet is refined 
    }

    Candidate.printCandidateAssumption(cndt, alphabet_);

    boolean conjRes;
    int maxDepth = 3;
    logger.info("STARTING CONJECTURE");

    Candidate.DFSTraversal(cndt, 0, maxDepth, this.alphabet_, "");
    String result = Candidate.allSequences;
    String res = result.replaceFirst(";", "");

    String bd = Candidate.allBadSequences;
    String bad = bd.replaceFirst(";", "");

    System.out.println("Good is: " + res);
    //System.out.println("Bad is: " + bad);

    String[] sequences = res.split(";");
    String[] badSequences = bad.split(";");

    logger.info("START CHECK SAFE");
    // check safety
    if (sequences.length == 0) {
      logger.info("NO ACCEPTED TRACES");
      // check permissive next
    } else {
      for (String nextSeq : sequences) {
        // first convert sequence for query
        Vector seq = new Vector();

        
        if (!nextSeq.isEmpty()) { // if empty shows no accepted traces
          // remove first : inserted
          String nS = nextSeq.replaceFirst(":", "");
          String[] methods = nS.split(":");
          for (String nextMeth : methods) {
            seq.add(nextMeth);
          }

          conjRes = query(seq, true); // let it memoize and see what happens

          if (!conjRes) // result is false so assumption does not block enough
          {
            logger.info("ENDING CONJECTURE");
            // reinitialize these
            Candidate.allSequences = "";
            Candidate.allBadSequences = "";
            return (seq); // refine the assumption
          }
        }

      }
    }

    logger.info("START CHECK PERMISSIVE");
    // check permissiveness

    if (badSequences.length == 0) {
      logger.info("NO REJECTED TRACES");
      logger.info("ENDING CONJECTURE");
      // reinitialize these
      Candidate.allSequences = "";
      Candidate.allBadSequences = "";
      return null;
    }
    for (String nextSeq : badSequences) {
      // first convert sequence for query
      if (nextSeq.equals("")) {
        Candidate.allSequences = "";
        Candidate.allBadSequences = "";
        return null;
      }

      Vector seq = new Vector();

      // remove first : inserted
      String nS = nextSeq.replaceFirst(":", "");
      String[] methods = nS.split(":");
      for (String nextMeth : methods) {
        seq.add(nextMeth);
      }

      conjRes = query(seq, true); // let it memoize and see what happens

      if (conjRes) // result is true so incompatible with assumption generated
      {
        logger.info("ENDING CONJECTURE");
        // reinitialize these
        Candidate.allSequences = "";
        Candidate.allBadSequences = "";
        return (seq); // refine the assumption
      }

    }
    logger.info("ENDING CONJECTURE");
    // reinitialize these
    Candidate.allSequences = "";
    Candidate.allBadSequences = "";
    return null; // will need to check if there was refinement involved

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

  private void setUpJDartConfig() {

    String jpfHome = JPFargs_.getProperty("jpf.home");
    String jdartHome = JPFargs_.getProperty("jpf-jdart");
    String yicesPath = jdartHome + "/lib/libYices.so";

    JPFargs_.setProperty("yices.library.path", yicesPath);
    JPFargs_.setProperty("jpf.basedir", jpfHome);

    JPFargs_.setTarget(TARGET);  // main program
    JPFargs_.setProperty("jfuzz.time", "3,3,0,0");
    JPFargs_.setProperty("vm.insn_factory.class", "gov.nasa.jpf.jdart.ConcolicInstructionFactory");
    JPFargs_.setProperty("listener", "jfuzz.ConcolicListener");
    JPFargs_.setProperty("perturb.params", "foo");
    JPFargs_.setProperty("perturb.foo.class", "jfuzz.Producer");
    JPFargs_.setProperty("perturb.foo.method", "gov.nasa.jpf.psyco.Target.ProgramExecutive.sequence()");
    JPFargs_.setProperty("symbolic.dp", "yices");
    JPFargs_.setProperty("symbolic.method", "gov.nasa.jpf.psyco.Target.ProgramExecutive.sequence()");
    
    // the following ensures state matching is off - should it be in dart jpf.properties?
    // caused memory leak so adding it here
    JPFargs_.setProperty("vm.storage.class", null);

  }

  public JPF createJPFInstance(String[] programArgs) {
    JPFargs_.setTarget(TARGET);  // main program
    JPFargs_.setTargetArgs(programArgs); // arguments to main
    JPF jpf = new JPF(JPFargs_);
    return jpf;
  }

  public JFuzz createJDartInstance(String[] programArgs) {
    JPFargs_.setTargetArgs(programArgs); // arguments to main
    String packageName = JPFargs_.getProperty("sut.package");
    String st = packageName + "." + AlphabetRefinement.REFINED_CLASS_NAME + "$" + "TotallyPsyco";
    JPFargs_.setProperty("symbolic.assertions", st);
    st = JPFargs_.getProperty("symbolic.classes");
    if (st != null) {
      st += "," + packageName + "." + AlphabetRefinement.REFINED_CLASS_NAME;
    } else {
      st = packageName + "." + AlphabetRefinement.REFINED_CLASS_NAME;
    }
    JPFargs_.setProperty("symbolic.classes", st);
    return (new JFuzz(JPFargs_));
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
  
  public MemoizeTable getMemoizeTable() {
    return memoized_;
  }

  public static void setMode(boolean m) {
    mode = m;
  }
  
  public int getMemoizeHits() {
    return memoizeHits;
  }
}
