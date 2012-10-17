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
package gov.nasa.jpf.psyco.oracles;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.jdart.JDart;
import gov.nasa.jpf.jdart.constraints.ConstraintsTree;
import gov.nasa.jpf.psyco.PsycoConfig;
import gov.nasa.jpf.psyco.compiler.Compiler;
import gov.nasa.jpf.psyco.explore.NewDartExplorer;
import gov.nasa.jpf.psyco.refinement.ConstrainedMethodSequence;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class JDartOracle {
  
  private NewDartExplorer dart;
  
  private Config compilerConfig;

  private PsycoConfig psycoConfig;
  
  public JDartOracle(Config compilerConfig, PsycoConfig cfg) {
    this.psycoConfig = cfg;
    this.psycoConfig.addSymbolicMethod("temp.Query.sequence()");
    
    this.compilerConfig = compilerConfig;
    this.compilerConfig.setTarget("temp.Query");
    
    this.compilerConfig.remove("shell");
    
    this.dart = new NewDartExplorer(this.compilerConfig, psycoConfig);
  }
  
  public ConstraintsTree query(ConstrainedMethodSequence cms) {
  
    // prepare query
    Map<String,Object> attributes = new HashMap<String,Object>();    
    
    attributes.put("params", cms.getParameters());
    attributes.put("steps", cms.getSteps());
    attributes.put("package", "temp");
    
    // compile query
    Compiler cc = new Compiler("Query", attributes, compilerConfig, "src/examples/temp");    
    cc.compile(false);
    
    // execute query
    dart.run();
    
    // collect result ...
    return dart.getConstraintsTree(null);
  }
  
}
