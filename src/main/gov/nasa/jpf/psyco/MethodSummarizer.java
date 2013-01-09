//
// Copyright (C) 2008 United States Government as represented by the
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
package gov.nasa.jpf.psyco;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;

import gov.nasa.jpf.JPFShell;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.jdart.ConcolicConfig.MethodConfig;
import gov.nasa.jpf.psyco.explore.MethodExplorer;
import gov.nasa.jpf.psyco.summaries.MethodSummary;

import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.LogManager;
import java.io.*;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MethodSummarizer implements JPFShell {

  private Config config;
  
  private JPFLogger logger;  

  private Map<Method,MethodSummary> summaries = new HashMap<Method, MethodSummary>();  
  
  private Set<Method> target = new HashSet<Method>();

  private Valuation initial = new Valuation();
  
  public MethodSummarizer(Config conf) {
    this(conf, null);
  }

  public MethodSummarizer(Config conf, Collection<Method> targets) {
    this.config = conf;
    LogManager.init(conf);
    logger = JPF.getLogger("psyco");
    if (targets == null) {
      initilaize();
    }
    else {
      this.target.addAll(targets);   
    }
  }

  @Override
  public void start(String[] strings) {
    try {
      run();
    } catch (IOException ex) {
      Logger.getLogger(MethodSummarizer.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  public void run() throws IOException {
    for (Method m : this.target) {
      this.summaries.put(m, this.summarize(m));
    }
     
    logger.fine("Initial valuation:\n" + this.initial);
  }

  private void initilaize() {
    PsycoConfig pconf = new PsycoConfig(this.config);    
    for (MethodConfig mc : pconf.getAlphabetMethods()) {
      try {
        Class<?> c = Class.forName(mc.getClassName());
        Method m = c.getDeclaredMethod(mc.getMethodName(), mc.getParamTypes());     
        this.target.add(m);
      } catch (ClassNotFoundException ex) {
        logger.log(Level.SEVERE, null, ex);
      } catch (NoSuchMethodException ex) {
        logger.log(Level.SEVERE, null, ex);
      } catch (SecurityException ex) {
        logger.log(Level.SEVERE, null, ex);
      }
    }
  }
  
  private MethodSummary summarize(Method m) {
   
    MethodExplorer mex = new MethodExplorer(m, this.config);
    MethodSummary ms = mex.execute();
    logger.fine("Summary:\n" + ms);
    
    // memorize inital values
    for (Entry<Variable,Object> e : mex.getInitialValues().entrySet()) {
      this.initial.setValue(e.getKey(), e.getValue());
    }    
    return ms;
  }
  
  public Valuation getInitialValuation() {
    return this.initial;
  }

  public Map<Method,MethodSummary> getSummaries() {
    return this.summaries;
  }
    
}
