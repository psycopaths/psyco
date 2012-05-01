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

import gov.nasa.jpf.jdart.numeric.Constraint;

import java.util.ArrayList;
import java.util.HashMap;

public class Symbol {
  private static int symbolCounter = 0;
  private String symbolName;
  private String oldSymbolName;
  private String originalMethodName;
  private String originalClassName;
  private int numParams = 0;
  private Precondition precondition;
  
//  public Symbol(String symbolName, String methodName, Precondition precondition) {
//    this.symbolName = symbolName + "_" + symbolCounter;
//    this.methodName = methodName;
//    this.precondition = precondition;
//    symbolCounter++;
//  }
  
  public Symbol(String originalClassName, String originalMethodName, int numParams) {
    oldSymbolName = originalMethodName;
    symbolName = originalMethodName + "_" + symbolCounter;
    symbolCounter++;
    this.originalMethodName = originalMethodName;
    this.originalClassName = originalClassName;
    this.numParams = numParams;
    precondition = new Precondition();
  }

  public Symbol(String symbolName, String oldSymbolName, String originalClassName, String originalMethodName, int numParams, Precondition precondition) {
    this.oldSymbolName = oldSymbolName;
    this.symbolName = symbolName + "_" + symbolCounter;
    symbolCounter++;
    this.originalMethodName = originalMethodName;
    this.originalClassName = originalClassName;
    this.numParams = numParams;
    
    HashMap<String, String> replacementNames = new HashMap<String, String>();
    for (int i = 0; i < numParams; i++) {
      String oldParamName = symbolName + "_P" + i;
      String newParamName = this.symbolName + "_P" + i;
      replacementNames.put(oldParamName, newParamName);
    }
    precondition.replaceNames(replacementNames);
    this.precondition = precondition;
  }

  public String getSymbolName() {
    return symbolName;
  }
  
  public String getOriginalMethodName() {
    return originalMethodName;
  }

  public String getOriginalClassName() {
    return originalClassName;
  }

  public int getNumParams() {
    return numParams;
  }
  
  public Precondition getPrecondition() {
    return precondition;
  }
  
  public String getSymbolToPrecondition() {
    String[] paramNames = new String[]{"p", "q", "r"};
    String preconditionStr = precondition.toSource();
    for (int i = 0; i < numParams; i++) {
      String oldParamName = symbolName + "_P" + i;
      preconditionStr = preconditionStr.replaceAll(oldParamName, paramNames[i]);
    }
    return preconditionStr;
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
    for (int methodCopy = 0; methodCopy < AlphabetRefinement.NUMBER_OF_METHOD_COPIES; methodCopy++) {
      for (int i = 0; i < numParams; i++) {
        String paramName = "PSYCO" + methodCopy + "_" + symbolName + "_P" + i;
        source += "  @Symbolic(\"true\")\n";
        source += "  public static int " + paramName + " = 0;\n";
      }
      source += "  public static void ";
      source += "PSYCO" + methodCopy + "_" + symbolName;
      source += "() {\n";
      source += "    if (";
      String preconditionStr = precondition.toSource();
      for (int i = 0; i < numParams; i++) {
        String oldParamName = symbolName + "_P" + i;
        String newParamName = "PSYCO" + methodCopy + "_" + symbolName + "_P" + i;
        preconditionStr = preconditionStr.replaceAll(oldParamName, newParamName);
      }
      source += preconditionStr;
      source += ") {\n";
      source += "      " + originalClassName + "." + originalMethodName + "(";
      for (int i = 0; i < numParams; i++) {
        source += "PSYCO" + methodCopy + "_" + symbolName + "_P" + i;
        if (i < numParams - 1) {
          source += ", ";
        }
      }
      source += ");\n";
      source += "    } else {\n";
      source += "      throw new TotallyPsyco(\"Odd Psyco\");\n";
      source += "    }\n";
      source += "  }\n\n";
    }
    return source;
  }
}
