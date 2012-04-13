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
import gov.nasa.jpf.learn.basic.Candidate;
import gov.nasa.jpf.learn.basic.SETException;
import gov.nasa.jpf.learn.basic.MinimallyAdequateTeacher;
import gov.nasa.jpf.learn.TDFA.MemoizeTable;
import gov.nasa.jpf.learn.TDFA.TDFALearner;
import gov.nasa.jpf.learn.basic.Learner;
import gov.nasa.jpf.learn.basic.ThreeValues;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.psyco.explore.SequenceExplorer;
import gov.nasa.jpf.psyco.explore.SequenceExplorer.ExplorationMethod;
import gov.nasa.jpf.psyco.explore.PsycoProducer;
import gov.nasa.jpf.psyco.refinement.AlphabetRefinement;
import gov.nasa.jpf.util.JPFLogger;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.Vector;
import jfuzz.ConstraintsTree;

import gov.nasa.jpf.psyco.refinement.Symbol;
import java.util.HashMap;
import gov.nasa.jpf.psyco.Target.ProgramExecutive;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/*
 * Teacher for interface generation in psyco using classic L*
 */
public class Teacher3Values implements MinimallyAdequateTeacher {

  public static int maxDepth = 1;
  public static final boolean CONCR = false;
  public static final boolean SYMB = true;
  public static final String TARGET = "gov.nasa.jpf.psyco.Target.ProgramExecutive";
  private JPFLogger logger = JPF.getLogger("teacher");
  private Learner set_;
  private MemoizeTable memoized_;
  private String module1_, module2_;
  private Config JPFargs_;
  private Vector alphabet_;
  private boolean refine = false;
  private String newAlphabet;
  // by default, mode is concrete
  private static boolean mode = CONCR;
  private static AlphabetRefinement alphaRefiner = null;
  private static boolean optimizeQueriesNoParams = true; // enabled by default
  private static int memoizeHits = 0;
  private static int totalQueries = 0;

  public Teacher3Values(Config conf, AlphabetRefinement ref) {
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
    String packageName = JPFargs_.getProperty("sut.package");
    String st = packageName + "." + AlphabetRefinement.REFINED_CLASS_NAME + "$" + "TotallyPsyco";
    JPFargs_.setProperty("symbolic.assertions", st);
    st = JPFargs_.getProperty("symbolic.classes");
    if (st != null) {
    	if (!st.contains(AlphabetRefinement.REFINED_CLASS_NAME))
    		st += "," + packageName + "." + AlphabetRefinement.REFINED_CLASS_NAME;
    } else {
      st = packageName + "." + AlphabetRefinement.REFINED_CLASS_NAME;
    }
    JPFargs_.setProperty("symbolic.classes", st);

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
  public Teacher3Values(Config conf, AlphabetRefinement ref, MemoizeTable mem) {
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
    String packageName = JPFargs_.getProperty("sut.package");
    String st = packageName + "." + AlphabetRefinement.REFINED_CLASS_NAME + "$" + "TotallyPsyco";
    JPFargs_.setProperty("symbolic.assertions", st);
    st = JPFargs_.getProperty("symbolic.classes");
    if (st != null) {
    	if (!st.contains(AlphabetRefinement.REFINED_CLASS_NAME))
    		st += "," + packageName + "." + AlphabetRefinement.REFINED_CLASS_NAME;
    } else {
      st = packageName + "." + AlphabetRefinement.REFINED_CLASS_NAME;
    }
    JPFargs_.setProperty("symbolic.classes", st);

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

  public ThreeValues query(AbstractList<String> sequence) throws SETException {
    return (query(sequence, true));
  }

  private static ThreeValues negateResult(ThreeValues original) {
    switch (original) {
      case TRUE:
        return ThreeValues.FALSE;
      case FALSE:
        return ThreeValues.TRUE;
      default:
        return ThreeValues.THIRD;
    }
  }

  public ThreeValues query(AbstractList<String> sequence, boolean memoize) throws SETException {
    if (refine) {
      return (ThreeValues.TRUE); // means we ignore all queries 
    }                     // when the alphabet will be refined 

    // the following assumes that initial state of target is OK
    if (sequence.isEmpty()) {
      return (ThreeValues.TRUE);
    }

    System.out.println("Total queires = " + totalQueries);
    System.out.println("Memoized hits = " + memoizeHits);
    logger.info("New query: ", sequence);
    totalQueries++;

    ThreeValues recalled;

    if (mode == SYMB) {
      recalled = memoized_.getSimulatedResult(sequence);
    } else {
      recalled = memoized_.getResult(sequence);
    }

    if (recalled != null) { // we get the result from memoized
      logger.info("Result from memoized for sequence: ", sequence);
      logger.info("Result to be negated is: ", recalled);
      memoizeHits++;

      return negateResult(recalled);
    }

    // we have not returned so we need to model check

    // first create sequence in format appropriate for Executor

    // default is to not use the optimization for queries with no parameters
    boolean parameters_involved = true;

    // is there any parameters involved?

    // only perform this if optimization is enabled
    if (optimizeQueriesNoParams) {
      parameters_involved = false;

      if (mode == SYMB) {
        for (String nextEl : sequence) {
          if ((alphaRefiner.getSymbol(nextEl)).getNumParams() > 0) {
            parameters_involved = true; // there are parameters so may need to refine
            break; // exit the loop
          }
        }
      }
    }

    String[] programArgs = null;
    int counter = 0;

    if (mode == CONCR || !parameters_involved) {
      logger.info("Mode is CONCR or no params");

      programArgs = new String[sequence.size() + 1];
      programArgs[0] = "sequence";
      counter = 1;
    } else if (mode == SYMB) {
      logger.info("Mode is symbolic");
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
      logger.info("Case no params");
      logger.info("---------- NO PARAMETERS - RUN JAVA------------");
      // just execute with simple java - avoid JPF altogether
      boolean result = true;
      try {
        resetTarget();
        ProgramExecutive.main(programArgs);
      } catch (Throwable e) {
        // make sure exception is due to assert false and not to some mistake...
        if (!((e.getCause()).toString()).startsWith("java.lang.AssertionError")) {
          logger.severe("Unexpected exception caught during query");
        }
        result = false;
      }

      logger.info("Result from running Java is ", result);

      if (result) {
        if (memoize) {
          memoized_.setResult(sequence, ThreeValues.FALSE); // memoized stores them the other way around  
          return (ThreeValues.TRUE);
        } else {
          if (memoize) {
            memoized_.setResult(sequence, ThreeValues.TRUE); // memoized stores them the other way around  
            return (ThreeValues.FALSE);
          }
        }
      }
    }

    if (mode == SYMB) {
      logger.info("---------- RUN CONCOLIC ------------");
      // TODO
      // first call jpf-jdart when it is ready and get the constraints tree
      // for the moment just get a new constraints tree

      SequenceExplorer explorer = createExplorer(programArgs, ExplorationMethod.JDart, sequence);
      explorer.run();
      ConstraintsTree ct = explorer.getConstraintsTree(TARGET + ".sequence()V");
      String result = alphaRefiner.refine(ct);
      logger.info("Refinement result: " + result);
      if (result.equals("OK")) {
        if (memoize) {
          memoized_.setResult(sequence, ThreeValues.FALSE); // memoized stores them the other way around
        }
        return (ThreeValues.TRUE);
      } else if (result.equals("ERROR")) {
        if (memoize) {
          memoized_.setResult(sequence, ThreeValues.TRUE); // memoized stores them the other way around
        }
        return (ThreeValues.FALSE);
      } else if (result.equals("UNKNOWN")) {
        if (memoize) {
          memoized_.setResult(sequence, ThreeValues.THIRD); // memoized stores them the other way around
        }
        return (ThreeValues.THIRD);
      } else { // must refine
        refine = true;
        newAlphabet = result;
        return (ThreeValues.TRUE); // will be ignored since refined was set to true
      }
    } else {  // mode is concrete
      logger.info("---------- RUN JPF CONCRETE MODE ------------");
      JPF jpf = createJPFInstance(programArgs); // driver for M1
      jpf.run();
      ThreeValues violating = null;
      if (jpf.foundErrors()) {
        if (memoize) {
          memoized_.setResult(sequence, ThreeValues.TRUE);
        }
        return ThreeValues.FALSE;
      } else {
        if (memoize) {
          memoized_.setResult(sequence, ThreeValues.FALSE);
        }
        return ThreeValues.TRUE;
      }
    }
  }

  public Vector conjecture(Candidate cndt) throws SETException {
    Vector cex = null;
    while (!refine && cex == null) {
      Candidate.printCandidateAssumption(cndt, alphabet_);

      logger.info("STARTING CONJECTURE");
      logger.info("k = " + maxDepth);
      System.out.println("k = " + maxDepth);

      cndt.DFSTraversal(cndt, 0, maxDepth, this.alphabet_, "");
      String result = cndt.allGoodSequences;
      String res = result.replaceFirst(";", "");

      String bd = cndt.allBadSequences;
      String bad = bd.replaceFirst(";", "");

      String dknow = cndt.allDKnowSequences;
      String other = dknow.replaceFirst(";", "");

      //System.out.println("Good is: " + res);
      //System.out.println("Bad is: " + bad);

      String[] goodSequences = res.split(";");
      String[] badSequences = bad.split(";");
      String[] otherSequences = other.split(";");

      logger.info("START CHECK SAFE");
      cex = checkSequences(goodSequences, ThreeValues.TRUE);
      if (cex == null) {
        logger.info("START CHECK PERMISSIVE");
        cex = checkSequences(badSequences, ThreeValues.FALSE);
        if (cex == null) {
          logger.info("START CHECK THIRD");
          cex = checkSequences(otherSequences, ThreeValues.THIRD);
        }
      }

      logger.info("ENDING CONJECTURE");
      logger.info("cex = " + cex);
      logger.info("refine = " + refine);
      System.out.println("cex = " + cex);
      System.out.println("refine = " + refine);
      // reinitialize these
      cndt.allGoodSequences = "";
      cndt.allBadSequences = "";
      cndt.allDKnowSequences = "";
      if (!refine && cex == null) {
        maxDepth++;
      }
    }

    return cex;
  }
  
  public Vector conjecture(Candidate cndt, int useDepth) throws SETException {
    if (refine) {
      return null; // we need to ignore conjectures because alphabet is refined
    }

    Candidate.printCandidateAssumption(cndt, alphabet_);

    logger.info("STARTING CONJECTURE");

    cndt.DFSTraversal(cndt, 0, useDepth, this.alphabet_, "");
    String result = cndt.allGoodSequences;
    String res = result.replaceFirst(";", "");

    String bd = cndt.allBadSequences;
    String bad = bd.replaceFirst(";", "");

    String dknow = cndt.allDKnowSequences;
    String other = dknow.replaceFirst(";", "");

    //System.out.println("Good is: " + res);
    //System.out.println("Bad is: " + bad);

    String[] goodSequences = res.split(";");
    String[] badSequences = bad.split(";");
    String[] otherSequences = other.split(";");

    Vector cex = null;
    logger.info("START CHECK SAFE");
    cex = checkSequences(goodSequences, ThreeValues.TRUE);
    if (cex == null) {
      logger.info("START CHECK PERMISSIVE");
      cex = checkSequences(badSequences, ThreeValues.FALSE);
      if (cex == null) {
        logger.info("START CHECK THIRD");
        cex = checkSequences(otherSequences, ThreeValues.THIRD);
      }
    }

    logger.info("ENDING CONJECTURE");
    // reinitialize these
    cndt.allGoodSequences = "";
    cndt.allBadSequences = "";
    cndt.allDKnowSequences = "";

    return cex;
  }
  
  private Vector checkSequences(String[] sequences, ThreeValues desired)  throws SETException {
    if (sequences.length == 0) {
      logger.info("NO TRACES IN SET");
    } else {
      for (String nextSeq : sequences) {
        // first convert sequence for query
        if (nextSeq.equals("")) {
          return null;
        }

        Vector seq = new Vector();

        // remove first : inserted
        String nS = nextSeq.replaceFirst(":", "");
        String[] methods = nS.split(":");
        for (String nextMeth : methods) {
          seq.add(nextMeth);
        }

        ThreeValues res = query(seq, true);

        if (res != desired) {
          return seq;
        }
      }
    }

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

  public void setLearner(Learner set) {
    set_ = set;
  }

  private void setUpJDartConfig() {
    JPFargs_.setTarget(TARGET);  // main program
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

  public SequenceExplorer createExplorer(String[] programArgs, 
  		SequenceExplorer.ExplorationMethod explorationMethod,
  		AbstractList<String> sequence) {
    JPFargs_.setTargetArgs(programArgs); // arguments to main
    return (new SequenceExplorer(JPFargs_, explorationMethod, true, sequence, alphaRefiner));
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

  public static void setOptimize(boolean o) {
    optimizeQueriesNoParams = o;
  }
}
