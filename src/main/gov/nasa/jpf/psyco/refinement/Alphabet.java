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

public class Alphabet implements Iterable<MethodSymbol> {
  
  
  private HashMap<String, MethodSymbol> symbols = new HashMap<String, MethodSymbol>();
  
  public Alphabet() {    
  }
   
  public void addSymbol(MethodSymbol symbol) {
    symbols.put(symbol.getSymbolName(), symbol);
  }
  
  public MethodSymbol getSymbol(String symbolName) {
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
      symbolsAsString += symbolName; // + "[" + symbols.get(symbolName).getPrecondition() + "]";
      if (itr.hasNext()) {
        symbolsAsString += ",";
      }
    }
    return symbolsAsString;
  }

  public String getSequenceMethodsForJDart(String[] sequence) {
    HashMap<String, Integer> sequenceSymbolCount = new HashMap<String, Integer>();
    String sequenceMethods = "";
//    for (int i = 0; i < sequence.length; i++) {
//      String symbolStr = sequence[i];
//      int symbolCount = 0;
//      if (sequenceSymbolCount.containsKey(symbolStr)) {
//        symbolCount = sequenceSymbolCount.get(symbolStr) + 1;
//      }
//      sequenceSymbolCount.put(symbolStr, symbolCount);
//      MethodSymbol symbol = symbols.get(symbolStr);
//
//      sequenceMethods += packageName + "." + symbol.getOriginalClassName() + ".";
//      sequenceMethods += symbol.getOriginalMethodName();
//
//      sequenceMethods += "(";
//      for (int j = 0; j < symbol.getNumParams(); j++) {
//        sequenceMethods += "I";
//      }
//      sequenceMethods += ")V:";
//
//      for (int j = 0; j < symbol.getNumParams(); j++) {
//        sequenceMethods += packageName + "." + className + ".";
//        sequenceMethods += "PSYCO" + symbolCount + "_" + symbol.getSymbolName() + "_P" + j;
//        if (j < symbol.getNumParams() - 1) {
//          sequenceMethods += ":";
//        }
//      }
//
//      if (i < sequence.length - 1) {
//        sequenceMethods += ",";
//      }
//    }
    return sequenceMethods;
  }

  public Iterator<MethodSymbol> iterator() {
    return this.symbols.values().iterator();
  }

  /* ***************************************************************************
   * 
   * 
   *  methods for pretty printing ... 
   * 
   */
  
  public HashMap<String, String> getSymbolsToPreconditions() {
    HashMap<String, String> symbolsToPreconditions = new HashMap<String, String>();
    Iterator<Map.Entry<String, MethodSymbol>> itr = symbols.entrySet().iterator();
    while (itr.hasNext()) {
      Map.Entry<String, MethodSymbol> entry = itr.next();
      String preconditionStr = entry.getValue().getPrecondition().toString();
      symbolsToPreconditions.put(entry.getKey(), preconditionStr);
    }
    return symbolsToPreconditions;
  }
  
  public HashMap<String, String> getSymbolsToMethodNames() {
    HashMap<String, String> symbolsToMethodNames = new HashMap<String, String>();
    Iterator<Map.Entry<String, MethodSymbol>> itr = symbols.entrySet().iterator();
    while (itr.hasNext()) {
      Map.Entry<String, MethodSymbol> entry = itr.next();
      String methodName = entry.getValue().getOriginalMethodName();
      symbolsToMethodNames.put(entry.getKey(), methodName);
    }
    return symbolsToMethodNames;
  }
  

  public String toString() {
    String symbolsAsString = "Alphabet:\n";
    Iterator<String> itr = symbols.keySet().iterator();
    while (itr.hasNext()) {
      String symbolName = itr.next();
      symbolsAsString += symbolName + "[" + symbols.get(symbolName).getPrecondition() + "]\n";
      if (itr.hasNext()) {
        symbolsAsString += ",";
      }
    }
    return symbolsAsString;
  }  

}
