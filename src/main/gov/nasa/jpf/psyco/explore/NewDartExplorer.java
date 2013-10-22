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
import gov.nasa.jpf.jdart.JDart;
import gov.nasa.jpf.jdart.constraints.ConstraintsTree;
import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.psyco.PsycoConfig;

	/* The following class uses jdart to explore a program for psyco */

public class NewDartExplorer extends SymbolicExplorer {

  /*
   * the logger
   */
  private static JPFLogger logger = JPF.getLogger("psyco");

  /*
   * jdart/jpf config 
   */
  private Config jpfConf;
  
  /**
   * psyco config
   */
  private PsycoConfig psycoConf;
  
  /*
   * constraints tree
   */
  private ConstraintsTree tree;
  
  
  public NewDartExplorer (Config jpfConf, PsycoConfig psycoConf) {
    this.jpfConf = jpfConf;
    this.psycoConf = psycoConf;
  }
  
  @Override
  public void run() {
    JDart dart = new JDart(jpfConf, psycoConf);
    dart.run();
    // FIXME: there must be a better way ...
    this.tree = dart.getConcolicExplorer().getMethodExplorers().iterator().next().getConstraintsTree();
  }

  @Override
  public void reset() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public ConstraintsTree getConstraintsTree(String methodName) {
    return tree;
  }

  @Override
  public boolean inError(ConstraintsTree T) {
    return tree.inError();
  }
  
}
