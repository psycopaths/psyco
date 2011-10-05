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
package gov.nasa.jpf.psyco.refinement;

import gov.nasa.jpf.symbc.Symbolic;

public class Symbol {
  private static int symbolCounter = 0;
  private String symbolName;
  private String oldSymbolName;
  private String methodName;
  private int numParams = 0;
  private Precondition precondition;
  
//  public Symbol(String symbolName, String methodName, Precondition precondition) {
//    this.symbolName = symbolName + "_" + symbolCounter;
//    this.methodName = methodName;
//    this.precondition = precondition;
//    symbolCounter++;
//  }
  
  public Symbol(String symbolName, String methodName, int numParams, Precondition precondition) {
    this.oldSymbolName = symbolName;
    this.symbolName = symbolName + "_" + symbolCounter;
    symbolCounter++;
    this.methodName = methodName;
    this.numParams = numParams;
    this.precondition = precondition;
  }

  public String getSymbolName() {
    return symbolName;
  }
  
  public String getMethodName() {
    return methodName;
  }

  public int getNumParams() {
    return numParams;
  }

  public String[] getParams() {
    String[] params = new String[numParams];
    for (int i = 0; i < numParams; i++) {
      String paramName = symbolName + "_" + i;
      params[i] = paramName;
    }
    return params;
  }

  public String toSource() {
    String source = "";
    for (int i = 0; i < numParams; i++) {
      String paramName = symbolName + "_" + i;
      source += "  @Symbolic(\"true\")\n";
      source += "  public static int " + paramName + " = 0;\n";
    }
    source += "  public static Boolean ";
    source += symbolName;
    source += "() {\n";
    source += "    if (";
    String preconditionStr = precondition.toSource();
    for (int i = 0; i < numParams; i++) {
      String oldParamName = oldSymbolName + "_" + i;
      String newParamName = symbolName + "_" + i;
      preconditionStr = preconditionStr.replaceAll(oldParamName, newParamName);
    }
    source += preconditionStr;
    source += ") {\n";
    source += "      Example." + methodName + "(";
    for (int i = 0; i < numParams; i++) {
      source += symbolName + "_" + i;
      if (i < numParams - 1) {
        source += ", ";
      }
    }
    source += ");\n";
    source += "      return true;\n";
    source += "    } else {\n";
    source += "      return false;\n";
    source += "    }\n";
    source += "  }\n";
    return source;
  }
}
