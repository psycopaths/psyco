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

public class Alphabet {
  private HashMap<String, Symbol> symbols = new HashMap<String, Symbol>();
  
  public void addSymbol(Symbol symbol) {
    symbols.put(symbol.getSymbolName(), symbol);
  }
  
  public Symbol getSymbol(String symbolName) {
    return symbols.get(symbolName);
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
    source += "package simple2;\n\n";
    source += "import gov.nasa.jpf.symbc.Symbolic;\n\n";
    source += "public class ExampleAlphabet {\n\n";
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
}
