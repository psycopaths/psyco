/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.testing;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author falkhowar
 */
public class MethodChecks {
  
  private boolean exception = false;
  
  private String exceptionClass = null;
  
  private boolean checks = false;
  
  private List<String> checkCalls = new ArrayList<String>(); 

  public MethodChecks() {
  }
  
  public MethodChecks(String[] checks) {
    for (String c : checks) {
      addCheck(c);
    }
  }  
  
  public MethodChecks(String exception) {
    setExpectedException(exception);    
  }  

  public MethodChecks(String excetion, String[] checks) {
    this(checks);
    setExpectedException(excetion);
  }    
  public final void addCheck(String check) {
    this.checkCalls.add(check);
    this.checks = true;
  } 
  
  public final void setExpectedException(String className) {
    this.exception = true;
    this.exceptionClass = className;
  }

  /**
   * @return the exception
   */
  public boolean isException() {
    return exception;
  }

  /**
   * @return the exceptionClass
   */
  public String getExceptionClass() {
    return exceptionClass;
  }

  /**
   * @return the checks
   */
  public boolean isChecks() {
    return checks;
  }

  /**
   * @return the checkCalls
   */
  public List<String> getCheckCalls() {
    return checkCalls;
  }
    
  
}
