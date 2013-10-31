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
package gov.nasa.jpf.psyco.filter;

import de.learnlib.api.Query;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.Pair;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.automatalib.words.Word;

/**
 *
 * @author falkhowar
 */
public class PORFilter extends MethodExecutionFilter {

  private static final JPFLogger logger = JPF.getLogger("psyco");
 
  private final Map<String, Pair<BitSet,BitSet>> po = new HashMap<>(); 

  public PORFilter(Collection<String> methods, SymbolicMethodAlphabet inputs) {
    this(methods, inputs, null);
  }

  public PORFilter(Collection<String> methods, SymbolicMethodAlphabet inputs, 
          MethodExecutionFilter next) {
    super(next);
    initialize(methods, inputs);
  }
  

  @Override
  public boolean evaluateQuery(Query<SymbolicMethodSymbol, Boolean> query) {
    Word<SymbolicMethodSymbol> input = query.getInput();
    
    // constructor + 1 will always be executed
    if (input.size() < 3) {
      return false;
    }
    
    //System.out.println(input);
    
    String lastName = input.getSymbol(1).getConcolicMethodConfig().getId();
    Pair<BitSet,BitSet> lastMethod = po.get(lastName);
    for (int i=2; i<input.size(); i++) {      
      String curName = input.getSymbol(i).getConcolicMethodConfig().getId();
      Pair<BitSet,BitSet> cur = po.get(curName); 
      
      if (cur != null && lastMethod != null) {
        if (curName.compareTo(lastName) < 0
                && !lastMethod._1.intersects(cur._2) // read-write
                && !lastMethod._2.intersects(cur._1) // write-read
                && !lastMethod._2.intersects(cur._2) // write-write
              ) {
          return false;
        }
      }
        
      lastName = curName;
      lastMethod = cur;
    }
    return true;
  }
  
  
  private void initialize(Collection<String> mconfig, 
          SymbolicMethodAlphabet inputs) {
    
//    System.out.println("POR CONFIG: " + Arrays.toString(mconfig.toArray()));
    
    Map<String,Pair<BitSet,BitSet>> config = new HashMap<>();
    
    for (String line : mconfig) {    
      String[] conf = line.trim().split(",");      
//      System.out.println("line: " + Arrays.toString(conf));
      config.put( conf[0].trim(), new Pair<>( 
              makeSet(conf[1].trim()), makeSet(conf[2].trim()) ));
    }
      
    for (SymbolicMethodSymbol m : inputs) {
      
      String mName = m.getConcolicMethodConfig().getId();
      
      Pair<BitSet,BitSet> sets = config.get(mName);
      if (sets == null) {
        logger.warning("POR: Symbol " + m + " has no POR configuration!");
        continue;
      }
      
      logger.fine("bit-sets for " + m + "  :  " + sets._1.toString() + " : " + sets._2.toString());
      this.po.put(mName, sets);
    }   
  }
  
  private BitSet makeSet(String bits) {    
    BitSet set = new BitSet(bits.length());
    for (int i=0;i<bits.length();i++) {
      set.set(i,  bits.charAt(i) == '1');
    }
    return set;
  }  
}
