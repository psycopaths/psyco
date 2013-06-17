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
package callingJDart;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPFShell;

import gov.nasa.jpf.psyco.explore.SequenceExplorer;
import gov.nasa.jpf.psyco.explore.SequenceExplorer.ExplorationMethod;
import gov.nasa.jpf.util.LogManager;

public class Call implements JPFShell {
	
	Config conf;
	
  public Call (Config conf) {
    LogManager.init(conf);
    this.conf = conf;
  }
  
  public void start(String[] args) {
  	System.out.println("-------- In start!");
  	
  	String jpfHome = conf.getProperty("jpf.home");
  	String jdartHome = conf.getProperty("jpf-jdart");  	
  	String yicesPath = jdartHome + "/lib/libYices.so";
  	
  	conf.setProperty("target", "callingJDart.Input");
  	conf.setProperty("yices.library.path", yicesPath);
  	conf.setProperty("jpf.basedir", jpfHome);
  	conf.setProperty("jfuzz.time", "3,3,0,0");
  	conf.setProperty("vm.insn_factory.class", "gov.nasa.jpf.jdart.ConcolicInstructionFactory");
  	conf.setProperty("listener", "gov.nasa.jpf.jdart.ConcolicListener");
  	conf.setProperty("perturb.params", "foo");
  	conf.setProperty("perturb.foo.class", "jfuzz.Producer");
  	conf.setProperty("perturb.foo.method", "callingJDart.Input.foo(int,boolean)");
  	conf.setProperty("symbolic.dp", "yices");
  	conf.setProperty("symbolic.method", "callingJDart.Input.foo(i#b)");

//  	SequenceExplorer explore = new SequenceExplorer(conf, ExplorationMethod.JDart, false, null, null);
//  	explore.run();
//  	explore = new SequenceExplorer(conf, ExplorationMethod.JDart, false, null, null);
//  	explore.run();
  }
}
