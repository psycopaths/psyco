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

import static java.lang.Math.min;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author falkhowar
 */
public class TestSuite implements Iterable<TestSubSuite> {

  private int subSuiteSize = 100;

  private List<TestCase> testCases = new LinkedList<TestCase>();
  
  public TestSuite(Collection<TestCase> tests) {
    this.testCases.addAll(tests);
  }

  public TestSuite(Collection<TestCase> tests, int subSuiteSize) {
    this.testCases.addAll(tests);
    this.subSuiteSize = subSuiteSize;
  }
 
  
  public Iterator<TestSubSuite> iterator() {    
    List<TestSubSuite> subSuites = new LinkedList<TestSubSuite>();
    int offset = 0;
    for (int i=0;i<this.testCases.size(); i+= this.subSuiteSize) { 
      TestSubSuite sub = new TestSubSuite(this.testCases.subList(
              offset, min(offset+this.subSuiteSize, this.testCases.size()) ));
      subSuites.add(sub);
      offset += this.subSuiteSize;
    }    
    return subSuites.iterator();
  }
  
  
}
