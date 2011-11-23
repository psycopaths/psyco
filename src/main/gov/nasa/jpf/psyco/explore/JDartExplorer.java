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

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jdart.bytecode.BytecodeUtils;
import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.LogManager;

import java.util.Date;
import java.util.StringTokenizer;

import jfuzz.ConstraintsTree;
import jfuzz.JFuzz;
import jfuzz.termination.*;

	/* The following class uses jdart to explore a program for psyco */

public class JDartExplorer extends SymbolicExplorer {

  Config config;
  JFuzz jfuzz = null;

  public JDartExplorer (Config conf, boolean psyco) {
    this.config = conf;

    if (psyco) {
    	String jpfHome = conf.getProperty("jpf.home");
    	String jdartHome = conf.getProperty("jpf-jdart");
    	String symbolicMethod = conf.getProperty("symbolic.method");
    	String yicesPath = jdartHome + "/lib/libYices.so";

    	config.setProperty("yices.library.path", yicesPath);
    	config.setProperty("jpf.basedir", jpfHome);

    	config.setProperty("jfuzz.time", "3,3,0,0");
    	config.setProperty("vm.insn_factory.class", "gov.nasa.jpf.jdart.ConcolicInstructionFactory");
//    	config.setProperty("listener", "jfuzz.ConcolicListener");
    	config.setProperty("listener", "gov.nasa.jpf.psyco.explore.PsycoListener");
    	config.setProperty("perturb.params", "foo");
//    	config.setProperty("perturb.foo.class", "jfuzz.Producer");
    	config.setProperty("perturb.foo.class", "gov.nasa.jpf.psyco.explore.PsycoProducer");
    	config.setProperty("perturb.foo.method", symbolicMethod);
    	config.setProperty("symbolic.dp", "yices");
    }

    LogManager.init(config);
    
    jfuzz = new JFuzz(config);
  }

  // the logger
  public static JPFLogger logger = JPF.getLogger("jdart");

  // the output directory
  public static final String GEN_DIR = "generated";

  // classpath for the user program
  public static final String JFUZZ_INPUT_CP = "jfuzz.input.cp";

  // number of executions of the jdart
  public static final String JFUZZ_NUM_EXEC = "jfuzz.numexec";

  // Specifies the time that jFuzz has to execute<br><br>
  // The values should be comma separated with no spaces: hours,minutes,seconds,milliseconds
  // ex. +jfuzz.time=0,10,30,100
  // This will cause jFuzz to run for 10 minutes 30 seconds and 100 milliseconds.
  //
  // Note: This argument takes priority over jfuzz.numexec.
  
  public static final String JFUZZ_TIME_EXEC = "jfuzz.time";

  // java library path
  public static final String JFUZZ_JAVA_LIB_PATH = "java.library.path";

  // this flag when set tells us that we should start re-using vectors from prior
  // exploration runs
  public static boolean startReuse = false;

  // for more debug info
  public static boolean debug = false;

  // constraints tree
  public static ConstraintsTree tree = new ConstraintsTree();
  
  // reset method for external use
  public void reset() {
    // now reset internal state
  	JFuzz.reset();
  }
  
  // The following method is used to let us know that we need to accumulate vectors 
  // for a subsequent optimal answering of queries
  
  public void capture() {
  	PsycoProducer.captureVectors();
  }
  
  // The following method is used to inform us that we should start re-using 
  // captured vectors for method exploration
  
  public void startReuse() {
  	startReuse = true;
  }
     
  /* 
   * Method used to run JDart without a shell. The method is expected to be called after
   * registering a config object at the time of constructing JDartExplorer. The method terminates
   * after exploring all symbolic methods to completion. 
   */
  
  public void run() {
    long start = System.currentTimeMillis();
    print("Running whitebox fuzzer " + new Date(start));
    
    // gets the basedir of JPF if required.
    // otherwise sets to the current dir
    String baseDir = config.getString("jpf.basedir");
    if (baseDir == null)
      baseDir = ".";

    jfuzz.setRestart(true);
    
    TerminationStrategy termination = makeTermination(config.getString(JFUZZ_TIME_EXEC), 
        config.getInt(JFUZZ_NUM_EXEC, -1));

    PsycoProducer.registerTermination(termination);
    
    String solver = config.getString("symbolic.dp");
    if (solver.equals("yices")) {
    	System.setProperty("yices.library.path", config.getString("yices.library.path"));
    }
    
    // now reset internal state
    BytecodeUtils.reset();
    ConstraintsTree.reset();
    
    do {
      PsycoProducer.reset();
      try {
        BytecodeUtils.resetSymVarCounter();
        JPF jpf = new JPF(config);
        jpf.run();
      } catch (Throwable t) { 
        // Log the exception and continue 
        logger.info("Unexception exception " + t.getMessage());
      } finally {
      }
    } while (!termination.isDone() && !ConstraintsTree.done());

    ConstraintsTree.printAllTrees();
    PsycoProducer.printAllValuations();
    logger.info("End of execution, " + termination.getReason());
  }
  
  public ConstraintsTree getConstraintsTree(String methodName) {
  	return ConstraintsTree.getTree(methodName);
  }
  
  public boolean inError(ConstraintsTree T) {
  	return T.inError();
  }

  private static TerminationStrategy makeTermination(String time, int numexec) {
    if(time != null) {
      StringTokenizer timeString = new StringTokenizer(time);
      try {
        int hours = Integer.parseInt(timeString.nextToken(","));
        int minutes = Integer.parseInt(timeString.nextToken());
        int seconds = Integer.parseInt(timeString.nextToken());
        int millis = Integer.parseInt(timeString.nextToken());

        return new TimedTermination(hours, minutes, seconds, millis);
      } catch (Exception e) {
        throw new IllegalArgumentException("Time argument shoud be of the form: +jfuzz.time=hours,minutes,seconds,millis");
      }
    }

    if (numexec != -1)
      return new UpToFixedNumber(numexec);

    return new NeverTerminate();
  }

  private static void print(Object s) {
    logger.info("[" + new Date() + "] " + s);
  }

  public static void printDebug(Object s) {
    if (JDartExplorer.debug)
      logger.info(s);
  }
}
