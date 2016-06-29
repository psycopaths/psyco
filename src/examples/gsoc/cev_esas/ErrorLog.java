/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gsoc.cev_esas;

import gov.nasa.jpf.jdart.Symbolic;

/**
 *
 * @author mmuesly
 */
//import java.util.ArrayList;

/**
 * class keeping a log of model errors (which are not to be confused with
 * modeled HW failures)
 * 
 * advanced topic: use this to show the danger of not closing the state space:
 * if the log list is enabled, it effectively turns off state matching by JPF,
 * unless the 'log' data structure is filtered out by the JPF state management
 */
public class ErrorLog {
  
  //ArrayList<String> log = new ArrayList<String>();
  @Symbolic("true")
  String error;
  
  public String log (String msg) {
    //log.add(msg);
    error = msg;
    return msg;
  }
  
  public String last () {
    if (error == null) {
      return "no error";
    } else {
      return error;
    }
    
    /**
    if (log.size() > 0){
      return log.get(log.size()-1);
    } else {
      return "no error";
    }
    **/
  }
}