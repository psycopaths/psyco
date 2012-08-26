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
package gov.nasa.jpf.psyco.tools;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFShell;
import gov.nasa.jpf.psyco.summaries.MethodSummary;
import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.psyco.compiler.Compiler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import solvers.Formula;
import solvers.LogicalExpression;
import solvers.LogicalOperator;
import solvers.TrueConstant;

/**
 *
 * @author falk
 */
public class ClassSummarizer implements JPFShell {

  private JPFLogger logger = JPF.getLogger("psyco");
  
  private Config conf;
  
  private String packageName;
  
  private String className;
  
  public ClassSummarizer(Config conf) {
    this.conf = conf;
    this.packageName = conf.getProperty("sut.package");
    this.className = conf.getProperty("sut.class");
  }
  
  public void start(String[] strings) {
    
    // find class and methods
    
    // 
    
    prepareTestCase(null);
    
  }
  
//  
//  
//  private MethodSummary summarize(Method m) {
//    Map<String,Object> attributes = new HashMap<String,Object>();
//    
//    prepareTestCase(attributes);
//    
//    
//  }
  

  
  private void prepareTestCase(Map<String,Object> attributes) {
    Compiler c = new Compiler(
            "MethodTestCase", 
            attributes, 
            conf, 
            packageName.replaceAll(".", "/") + "/MethodTest.java");
    
    c.compile();
  }
  
  
}
