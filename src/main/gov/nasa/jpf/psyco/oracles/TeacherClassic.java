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
import java.util.HashMap;


/*
 * Teacher for interface generation in psyco using classic L*
 */
public class TeacherClassic implements MinimallyAdequateTeacher {

  public static final String CONCR = "concrete";
  public static final String SYMB = "symbolic";
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
  private static String mode = CONCR;
  private static AlphabetRefinement alphaRefiner = null;

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


    for (String a : alpha) {
      alphabet_.add(a);
    }

    if (mode.equals(SYMB)) {
      setUpJDartConfig();
    }

  }

  private String getPrefix(String action, HashMap hm) {
    if (mode.equals(CONCR)) {
      System.out.println(("PSYCO" + hm.get(action) + "_")); // only for testing symbolic mode
      return "";
    } else {
      return ("PSYCO" + hm.get(action) + "_");
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

    Boolean recalled = memoized_.getResult(sequence);

    if (recalled != null) { // we get the result from memoized
      return (!recalled.booleanValue()); // note the fact that it is the other way around
    } else { // need to model check


      // first create sequence in format appropriate for Executor

      logger.info("New query: ", sequence);


      String[] programArgs = null;
      int counter = 0;

      if (mode.equals(CONCR)) {
        programArgs = new String[sequence.size() + 1];
        programArgs[0] = "sequence";
        counter = 1;
      } else if (mode.equals(SYMB)) {
        programArgs = new String[sequence.size() + 2]; // also need to call init() first for jdart to work
        programArgs[0] = "sequence";
        programArgs[1] = module1_ + ":" + "init";
        counter = 2;
      }

      HashMap hm = new HashMap();
      for (String nextEl : sequence) {
        if (!hm.containsKey(nextEl)) {
          hm.put(nextEl, new Integer(0));
        }

        programArgs[counter] = module1_ + ":" + getPrefix(nextEl, hm) + nextEl;
        counter++;
        Integer value = (Integer) hm.get(nextEl);
        value++;
        hm.put(nextEl, value);
      }


      if (mode.equals(SYMB)) {
        // TODO
        // first call jpf-jdart when it is ready and get the constraints tree
        // for the moment just get a new constraints tree

        JFuzz jfuzz = createJDartInstance(programArgs);
        jfuzz.runJDart();
        ConstraintsTree ct = jfuzz.getConstraintsTree(TARGET + ".sequence()V");
        String result = alphaRefiner.refine(ct);
        System.out.println("Refinement result: " + result);
        if (result.equals("OK")) {
          if (memoize) {
            memoized_.setResult(sequence, true);
          }
          return true;
        } else if (result.equals("ERROR")) {
          if (memoize) {
            memoized_.setResult(sequence, false);
          }
          return false;
        } else { // must refine
          refine = true;
          newAlphabet = result;
          return (true); // will be ignored since refined was set to true
        }
      } else {  // mode is concrete
        JPF jpf = createJPFInstance(programArgs); // driver for M1
        jpf.run();
        boolean violating = jpf.foundErrors();
        if (memoize) {
          memoized_.setResult(sequence, violating);
        }
        return (!violating);
      }
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
    System.out.println("Bad is: " + bad);

    String[] sequences = res.split(";");
    String[] badSequences = bad.split(";");

    logger.info("START CHECK SAFE");
    // check safety
    for (String nextSeq : sequences) {
      // first convert sequence for query
      Vector seq = new Vector();

      // remove first : inserted
      String nS = nextSeq.replaceFirst(":", "");
      String[] methods = nS.split(":");
      for (String nextMeth : methods) {
        seq.add(nextMeth);
      }

      conjRes = query(seq, false); // do not memoize

      if (!conjRes) // result is false so assumption does not block enough
      {
        logger.info("ENDING CONJECTURE");
        return (seq); // refine the assumption
      }

    }

    logger.info("START CHECK PERMISSIVE");
    // check permissiveness
    for (String nextSeq : badSequences) {
      // first convert sequence for query
      Vector seq = new Vector();

      // remove first : inserted
      String nS = nextSeq.replaceFirst(":", "");
      String[] methods = nS.split(":");
      for (String nextMeth : methods) {
        seq.add(nextMeth);
      }

      conjRes = query(seq, false); // do not memoize

      if (conjRes) // result is true so incompatible with assumption generated
      {
        logger.info("ENDING CONJECTURE");
        return (seq); // refine the assumption
      }

    }
    logger.info("ENDING CONJECTURE");
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

  public static void setMode(String m) {
    mode = m;
  }
}
