//
// Copyright (C) 2007 United States Government as represented by the
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

//
// DISCLAIMER - this file is part of the 'ESAS' demonstration project. As
// such, it is only intended for demonstration purposes, does not contain
// or refer to actual NASA flight software, and is solely derived from
// publicly available information. For further details, please refer to the
// README-ESAS file that is included in this distribution.
//

package issta2013.cev;

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
