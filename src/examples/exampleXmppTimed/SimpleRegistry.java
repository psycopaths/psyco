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

public class SimpleRegistry 
{

  private static boolean registered = false; // did register?
  private static boolean login = false; // active session?
  
  private static int user = 0; // user id
  private static int pass = 0; // pwd
  
  
  public static void internalReset() {
    registered = false;
    login = false;
  }

  public static void register(int id, int pw) {  
    if (registered)
      assert false;
    
    //if (id == pw) ; else;
    
    user = id;
    pass  = pw;
    registered = true;
           
  }
  
  public static void login(int id, int pw) {
    
    if (!registered || login || id != user || pw != pass)
      assert false;
    
    login = true;
  }

  public static void logout() {
    if (!login)
      assert false;
    
    login = false;
  }
  
//  public static void changePW(int pw) {
//    if (!login)
//      assert false;
//
////    pass = pw;
//  }
//  
//  public static void delete() {
//    if (!login)
//      assert false;
//
//  }

  
}
