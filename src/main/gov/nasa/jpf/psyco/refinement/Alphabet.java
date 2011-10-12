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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Alphabet {
  private HashMap<String, Symbol> symbols = new HashMap<String, Symbol>();
  private String packageName;
  private String className;
  
  public Alphabet(String packageName, String className) {
    this.packageName = packageName;
    this.className = className;
  }
  
  public void addSymbol(Symbol symbol) {
    symbols.put(symbol.getSymbolName(), symbol);
  }
  
  public Symbol getSymbol(String symbolName) {
    return symbols.get(symbolName);
  }

  public void removeSymbol(String symbolName) {
    symbols.remove(symbolName);
  }

  public String getSymbolsAsString() {
    String symbolsAsString = "";
    Iterator<String> itr = symbols.keySet().iterator();
    while (itr.hasNext()) {
      String symbolName = itr.next();
      symbolsAsString += symbolName;
      if (itr.hasNext()) {
        symbolsAsString += ",";
      }
    }
    return symbolsAsString;
  }
  
  public String toSource() {
    String source = "";
    source += "package " + packageName + ";\n\n";
    source += "import gov.nasa.jpf.symbc.Symbolic;\n\n";
    source += "public class " + className + " {\n\n";

    source += "  public static class TotallyPsyco extends java.lang.AssertionError {\n";
    source += "    private static final long serialVersionUID = 1L;\n\n";
    source += "    TotallyPsyco(String msg) {\n";
    source += "      super(msg);\n";
    source += "    }\n";
    source += "  }\n\n";

    source += "  public static void init() {}\n\n";

    Iterator<Symbol> itr = symbols.values().iterator();
    while (itr.hasNext()) {
      Symbol symbol = itr.next();
      source += symbol.toSource();
      source += "\n";
    }
    source += "}\n";
    return source;
  }
  
  public HashMap<String, String> getSymbolsToPreconditions() {
    HashMap<String, String> symbolsToPreconditions = new HashMap<String, String>();
    Iterator<Map.Entry<String, Symbol>> itr = symbols.entrySet().iterator();
    while (itr.hasNext()) {
      Map.Entry<String, Symbol> entry = itr.next();
      String preconditionStr = entry.getValue().getSymbolToPrecondition();
      symbolsToPreconditions.put(entry.getKey(), preconditionStr);
    }
    return symbolsToPreconditions;
  }
}
