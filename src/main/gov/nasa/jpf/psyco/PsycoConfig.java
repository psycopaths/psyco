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
package gov.nasa.jpf.psyco;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.jdart.ConcolicConfig;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class PsycoConfig extends ConcolicConfig {
  
  private int maxDepth = -1;
  
  private Set<MethodConfig> alphabetMethods;
  
  public PsycoConfig(Config conf) {
    super(conf);
    this.alphabetMethods = new HashSet<MethodConfig>();
    initialize(conf);
  }
  
  public int getMaxDepth() {
    return this.maxDepth;
  }  

  public Set<MethodConfig> getAlphabetMethods() {
    return alphabetMethods;
  }

  public void addSymbolicMethod(String s) {
    MethodConfig mc = parseMethodConfig(s);
    this.symbolicMethods.put(mc,mc);
  }
            
  private void initialize(Config conf) {
    // parse max depth
    if (conf.hasValue("psyco.depth")) {
      maxDepth = conf.getInt("psyco.depth");
    }
    
    // parse symbolic method info
    // TODO: need a better pattern once parameter min and max can be specified, too
    for (String key : conf.getKeysStartingWith("psyco.interface.m")) {
      MethodConfig mc = parseMethodConfig(conf.getProperty(key));
      this.alphabetMethods.add(mc);
    }    
  }  
   
}
