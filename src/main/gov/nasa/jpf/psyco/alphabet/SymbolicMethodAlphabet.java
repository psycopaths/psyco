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
package gov.nasa.jpf.psyco.alphabet;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.expressions.Negation;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.config.ConcolicMethodConfig;
import gov.nasa.jpf.jdart.config.ParamConfig;
import gov.nasa.jpf.psyco.util.SEResultUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import net.automatalib.words.Alphabet;

/**
 *
 * @author falkhowar
 */
public class SymbolicMethodAlphabet extends ArrayList<SymbolicMethodSymbol> 
    implements Alphabet<SymbolicMethodSymbol> {

  protected SymbolicMethodAlphabet() {    
  }
  
  public SymbolicMethodAlphabet(Collection<ConcolicMethodConfig> cmc) {
    for (ConcolicMethodConfig c : cmc) {
      this.addSymbol(c.getId(), c, ExpressionUtil.TRUE);
    }
  } 
  
  public boolean refine(SymbolicMethodSymbol sms, Expression<Boolean> refiner) {
    this.remove(sms);
    this.addSymbol(sms.getId() + "_1", sms.getConcolicMethodConfig(), 
            SEResultUtil.stripLeadingTrue(ExpressionUtil.and(
                    sms.getPrecondition(), refiner)));    
    this.addSymbol(sms.getId() + "_2", sms.getConcolicMethodConfig(), 
            SEResultUtil.stripLeadingTrue(ExpressionUtil.and(
                    sms.getPrecondition(), new Negation(refiner))));
    
    return true;
  }

  @Override
  public SymbolicMethodSymbol getSymbol(int i) 
          throws IllegalArgumentException {
    return this.get(i);
  }

  @Override
  public int getSymbolIndex(SymbolicMethodSymbol i) 
          throws IllegalArgumentException {
    return this.indexOf(i);
  }

  @Override
  public void writeToArray(int i, Object[] os, int i1, int i2) {
    throw new UnsupportedOperationException("Not supported yet."); 
  }

  @Override
  public int compare(SymbolicMethodSymbol o1, SymbolicMethodSymbol o2) {
    throw new UnsupportedOperationException("Not supported yet."); 
  }  

  public final SymbolicMethodSymbol addSymbol(String id, ConcolicMethodConfig cmc, 
          Expression<Boolean> precondition) {
    boolean isConstructor = false;
    boolean isStatic = false;
    
    try {
      Method m = loadMethodObject(cmc);
      isStatic = Modifier.isStatic(m.getModifiers());      
    } catch (ClassNotFoundException | NoSuchMethodException e){
      try {
        Constructor c = loadConstructorObject(cmc);
        isConstructor = true;
      } catch (ClassNotFoundException | NoSuchMethodException e2) {
        throw new RuntimeException(e2);
      }
    }
    
    SymbolicMethodSymbol sms = new SymbolicMethodSymbol(
            id, cmc, precondition, isConstructor, isStatic);
    
    this.add(sms);
    return sms;
  }
  
  private Constructor loadConstructorObject(ConcolicMethodConfig cmc)
          throws ClassNotFoundException, NoSuchMethodException {
    
    Class clazz = Class.forName(cmc.getClassName());
    Constructor m = clazz.getConstructor(getParameterTypes(cmc));
    return m;
  }
  
  private Method loadMethodObject(ConcolicMethodConfig cmc) 
          throws ClassNotFoundException, NoSuchMethodException {
   
    Class clazz = Class.forName(cmc.getClassName());
    Method m = clazz.getMethod(cmc.getMethodName(), getParameterTypes(cmc));
    return m;
  }
  
  private Class<?>[] getParameterTypes(ConcolicMethodConfig cmc) 
          throws ClassNotFoundException {
    
    Class[] paramTypes = new Class[cmc.getParams().size()];
    int i = 0;
    for (ParamConfig pc : cmc.getParams()) {
      paramTypes[i++] = getType(pc.getType());
    }
    return paramTypes;
  } 

  private Class getType(String name) throws ClassNotFoundException
  {
    if (name.equals("byte")) return byte.class;
    if (name.equals("short")) return short.class;
    if (name.equals("int")) return int.class;
    if (name.equals("long")) return long.class;
    if (name.equals("char")) return char.class;
    if (name.equals("float")) return float.class;
    if (name.equals("double")) return double.class;
    if (name.equals("boolean")) return boolean.class;
    return Class.forName(name);
  }  
}
