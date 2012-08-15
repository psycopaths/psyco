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
package exampleXmppTimed;

import gov.nasa.jpf.jdart.Symbolic;

public class XMPPRegistry 
{

  private static boolean registered = false; // did register?
  private static boolean pending = false; // reg pending?
  private static boolean login = false; // active session?
  
	//@Symbolic("true")  
  private static int user = 0; // user id
  
	//@Symbolic("true")
  private static int pass = 0; // pwd
  
	//@Symbolic("true")
  private static int regtime = 0; // time of registration 
//  private static int timeout = 24; // timeout for pending reg.
  
  public static void internalReset() {
    registered = false;
    pending = false;
    login = false;
  }

  public static void register(int id, int pw, int time) {  
    if (registered)
      assert false;
    
    user = id;
    pass  = pw;
    regtime = time;
    pending = true;    
    registered = true;
    
//    confirm(id, time);
  }

  public static void confirm(int id, int time) {
//    if (!pending || time > regtime + 24 || time < regtime || id != user)
//      assert false;
//    
//    pending = false;
    
    if (!pending || time >  24 || id != user)
      return;
//      assert false;
    
    pending = false;
  }
  
  public static void login(int id, int pw) {
    
//    if (id == pw) ; else;
    
    if (!registered || pending || id != user || pw != pass)
      assert false;
    
    login = true;
  }
  
  public static void changePW(int pw) {
    if (!login)
      assert false;

//    pass = pw;
  }
  
  public static void delete() {
    if (!login)
      assert false;
    
    internalReset();
  }

  public static void logout() {
    if (!login)
      assert false;
    
    login = false;
  }
  

  
}
