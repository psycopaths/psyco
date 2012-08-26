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
import gov.nasa.jpf.jdart.ConstraintsTree;
import gov.nasa.jpf.jdart.JFuzz;
import gov.nasa.jpf.jdart.bytecode.BytecodeUtils;
import gov.nasa.jpf.jdart.termination.*;
import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.LogManager;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.StringTokenizer;

import gov.nasa.jpf.jdart.termination.*;

	/* The following class uses jdart to explore a program for psyco */

public class JDartExplorer extends SymbolicExplorer {

  public Config config;
  private JFuzz jfuzz = null;
  private int sequenceLength = 0;
  private String[] sequenceMethodNames = null;
  
  public static JDartExplorer explorer = null;
  
  private static Method internalReset = null;

  public JDartExplorer (Config conf, boolean psyco) {
    this.config = conf;

    if (psyco) {
    	String optimizeStr = conf.getProperty("jdart.optimize");
    	boolean dontOptimize = false;
    	
    	if (optimizeStr != null && optimizeStr.equals("false"))
    		dontOptimize = true;
    	
    	String jpfHome = conf.getProperty("jpf.home");
    	String jdartHome = conf.getProperty("jpf-jdart");
    	String symbolicMethod = conf.getProperty("symbolic.method");
    	String yicesPath = jdartHome + "/lib/libYices.so";

    	config.setProperty("yices.library.path", yicesPath);
    	config.setProperty("jpf.basedir", jpfHome);

    	config.setProperty("jfuzz.time", "3,3,0,0");
    	config.setProperty("vm.insn_factory.class", "gov.nasa.jpf.jdart.ConcolicInstructionFactory");
    	
    	// by default we optimize    	
    	if (dontOptimize)
    		config.setProperty("listener", "gov.nasa.jpf.jdart.ConcolicListener");
    	else
      	config.setProperty("listener", "gov.nasa.jpf.psyco.explore.PsycoListener");
    	
    	config.setProperty("perturb.params", "foo");
    	
    	if (dontOptimize)
    		config.setProperty("perturb.class", "gov.nasa.jpf.jdart.Producer");
    	else
    		config.setProperty("perturb.class", "gov.nasa.jpf.psyco.explore.PsycoProducer");
    	
    	config.setProperty("perturb.foo.method", symbolicMethod);
    	config.setProperty("symbolic.dp", "yices");
    	
    	// now get a handle to the internalReset method of the class we want to 
    	// explore. This handle is used to restore the state of the fields of the
    	// class to their initialized values
    	
    	String module1_ = config.getString("sut.package") + "." + config.getString("sut.class"); // this is our target class
    	try {
    		Class<?> invokedClass = Class.forName(module1_);
    		try {
    			internalReset = invokedClass.getDeclaredMethod("internalReset");
    			internalReset.setAccessible(true);
    		} catch (NoSuchMethodException e2) {
    			System.out.println("Cannot find internalReset in class " + module1_ + ". Ignoring.");
    			;
    		}
    	} catch (ClassNotFoundException e1) {
    		// this is serious and should not happen
    		System.err.println("Class not found: " + module1_);
    	}
    }
    
    String sequenceMethods = config.getProperty("sequence.methods");
    if (sequenceMethods != null) {
    	String[] tokens = sequenceMethods.split(",");
    	sequenceLength = tokens.length;
    	sequenceMethodNames = new String[sequenceLength];
    	for (int i = 0; i < sequenceLength; i++) {
    		String token = tokens[i];
    		String methodName = token.substring(0, token.indexOf(':'));
    		sequenceMethodNames[i] = methodName.substring(0, methodName.length() - 1);
    	}
    }

    LogManager.init(config);
    
    jfuzz = new JFuzz(config);
    
    explorer = this;
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

  // for more debug info
  public static boolean debug = false;

  // constraints tree
  public static ConstraintsTree tree = new ConstraintsTree();
  
  // reset method for external use
  public void reset() {
    // now reset internal state
  	JFuzz.reset();
  }
  
  // method used to get the sequence length
  public int sequenceLength() {
  	return sequenceLength;
  }
  
  // method used to get the methods in the sequence
  public String[] sequenceMethodNames() {
  	return sequenceMethodNames;
  }

  static void internalReset() {
		try {
			internalReset.invoke(null);
		} catch (Throwable e) {
			System.err.println("Problem invoking internalReset. Ignoring.");
		}
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

    // initialize the producer
    PsycoProducer.initialize();

    long _start = System.currentTimeMillis();
    
    do {
      PsycoProducer.reset();
      try {
        
        long _start_jpf = System.currentTimeMillis();
        JPF jpf = new JPF(config);               
        long _stop_jpf = System.currentTimeMillis();
        logger.finest("Spent " + (_stop_jpf - _start_jpf)+  " ms in jpf init");

        _start_jpf = System.currentTimeMillis();
        jpf.run();
        _stop_jpf = System.currentTimeMillis();
        logger.finest("Spent " + (_stop_jpf - _start_jpf)+  " ms in jpf call");
                
      } catch (Throwable t) { 
        // Log the exception and continue 
        logger.info("Unexception exception " + t.getMessage());
        t.printStackTrace();
      } finally {
      }
    } while (!termination.isDone() && !ConstraintsTree.done());

    long _stop = System.currentTimeMillis();
    
    logger.finest("Spent " + (_stop - _start)+  " ms constructing constraint tree");
    
    // now that we are done exploration of the given sequence, call cleanup methods
    PsycoProducer.cleanup();

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
