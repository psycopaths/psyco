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
package sequencetest;

import gov.nasa.jpf.jdart.*;

public class Input {
	
	@Symbolic("true")
	public static int i = 0;
	@Symbolic("true")
	public static boolean b = false;
	@Symbolic("true")
	public static int[] k = {1, 2, 3};
	@Symbolic("true")
	public static double[] d = {1.0, 2.0, 3.0, 4.0};
	@Symbolic("true")
	public static int state = 0;

	public static void init() {
	}
	
  static int STAGE1 = 0;
  static int STAGE2 = 1;
  static int LAS = 2;
  static int CM = 3;
  static int SM = 4;
  static int LSAM_ASCENT = 5;
  static int LSAM_DESCENT = 6;
  static int EDS = 7;
  
  static boolean doneStage1 = false;
  static boolean doneStage2 = false;
  static boolean lasDocked = true;
  static boolean cmDocked = true;
  static boolean smDocked = true;
  static boolean lsamAscentDocked = false;
  static boolean lsamDescentDocked = false;
  static boolean edsDocked = false;
  
  @Symbolic("true")
  public static int c1 = 0;  
  @Symbolic("true")
  public static int c2 = 0;
  @Symbolic("true")
  public static int c3 = 0;
  @Symbolic("true")
  public static int c4 = 0;  
  @Symbolic("true")
  public static int c5 = 0;
  @Symbolic("true")
  public static int c6 = 0;
  
  public static void reset_1() {
  	reset(c1);
  }
  public static void reset_2() {
  	reset(c2);
  }
  public static void reset_3() {
  	reset(c3);
  }
  public static void reset_4() {
  	reset(c4);
  }
  public static void reset_5() {
  	reset(c5);
  }
  public static void reset_6() {
  	reset(c6);
  }
  
  //--- actions
  
  public static void reset(int component) {
    if (component == LAS)
      lasDocked = true;
    else if (component == STAGE1)
  		doneStage1 = false;
  	else if (component == STAGE2)
  		doneStage2 = false;
  	else if (component == CM)
  		cmDocked = true;
  	else if (component == SM)
  		smDocked = true;
  	else if (component == LSAM_ASCENT)
  		lsamAscentDocked = false;
  	else if (component == LSAM_DESCENT)
  		lsamDescentDocked = false;
  	else if (component == EDS)
  		edsDocked = false;
  }

  public static void foo() {
    if (i > 200000) {
      if (b == true) {
        if (k[0] == k[1])
        	if (d[1] != d[3])
        		d[0] = d[1] + d[3];
        	else
        		d[1] = d[0] + d[3];
      } else {
        ;
      }
      state = k[0];
    } else
    	state = k[1];
  }
}
