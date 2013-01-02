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
package gov.nasa.jpf.psyco.testing;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.psyco.compiler.Compiler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author falkhowar
 */
public class TestCaseGenerator {
  
  private TestSuite suite;
  
  private String suiteName;
  
  private String packageName;
  
  private boolean debug = false;
  
  private Config compilerConfig;
    
  private String outDir;

  public TestCaseGenerator(TestSuite suite, String suiteName, String packageName, Config compilerConfig, String outDir) {
    this.suite = suite;
    this.suiteName = suiteName;
    this.packageName = packageName;
    this.compilerConfig = compilerConfig;
    this.outDir = outDir;
  }  
  
  public void generate() {
      
    int parts = 0;
    for (TestSubSuite sub : suite) {
      Map<String,Object> subInfo = new HashMap<String,Object>();
      subInfo.put("package", packageName);
      subInfo.put("suite", suiteName);
      subInfo.put("part", parts++);
      subInfo.put("debug", "" + debug);
      subInfo.put("tests", sub.getTests());
      
      // generate sub suite
      Compiler compiler = new Compiler("TestCase", subInfo, this.compilerConfig, 
              this.outDir, "PsycoTest" + this.suiteName + "" + subInfo.get("part"));
      compiler.compile(false);
      
    }    
    
    // generate suite file
    Map<String,Object> suiteInfo = new HashMap<String,Object>();
    suiteInfo.put("package", packageName);
    suiteInfo.put("suite", suiteName);
    List<String> partNames = new ArrayList<String>();
    for (int i=0;i<parts;i++) {
      partNames.add("" + i);
    }
    suiteInfo.put("parts", partNames);

    Compiler compiler = new Compiler("TestSuite", suiteInfo, this.compilerConfig, 
            this.outDir, "PsycoTest" + this.suiteName);
    compiler.compile(false);
        
  }
  
  public void run() {
    throw new IllegalStateException("not implemented yet.");
  }
  
}
