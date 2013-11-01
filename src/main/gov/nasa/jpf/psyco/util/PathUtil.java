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
package gov.nasa.jpf.psyco.util;

import com.google.common.base.Function;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.PropositionalCompound;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.constraints.PathResult;
import gov.nasa.jpf.jdart.constraints.PathState;
import gov.nasa.jpf.jdart.constraints.PostCondition;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.automatalib.words.Word;

/**
 *
 * @author falkhowar
 */
public class PathUtil {

  
  public static Path executeSymbolically(
          Word<SymbolicMethodSymbol> sword, Word<Path> paths, Valuation initial) {

    ArrayList<Expression<Boolean>> pc = new ArrayList<>();
    Map<Variable<?>, Expression<?>> val = new HashMap<>();
    for (ValuationEntry e : initial) {
      val.put(e.getVariable(), new Constant(
              e.getVariable().getType(), e.getValue()));
    }
    int ppos = 1;
    int spos = 0;
    for (Path p : paths) {
      SymbolicMethodSymbol sms = sword.getSymbol(spos);      
      Function<String, String> shift = 
              SEResultUtil.shift(1, ppos, sms.getArity());
      Path pShifted = SEResultUtil.rename(p, shift);
      Expression<Boolean> fragment = executeSymbolically(pShifted, val);
      Collection<Expression<Boolean>> atoms = decomposePath(fragment);
      pc.addAll(atoms);
      spos++;
      ppos += sms.getArity();
    }
    
    Path last = paths.lastSymbol();
    PathResult res = null;
    switch (last.getState()) {
      case OK:
        res = PathResult.ok(null, asPostCondition(val));
        break;
      case ERROR:
        PathResult.ErrorResult err = last.getErrorResult();
        res = PathResult.error(null, err.getExceptionClass(), err.getStackTrace());
        break;
      case DONT_KNOW:
        res = PathResult.dontKnow();
        break;
    }
    
    return new Path(asPathCondition(pc), res);
  }
  
  private static PostCondition asPostCondition(Map<Variable<?>, Expression<?>> map) {
    PostCondition post = new PostCondition();
    post.getConditions().putAll(map);
    return post;
  }
  
  public static Expression<Boolean> executeSymbolically( 
          Path path, final Map<Variable<?>, Expression<?>> val) {
    
    Expression pc = transformVars(path.getPathCondition(), val); 
    
    if (path.getState() == PathState.OK) {
      Map<Variable<?>, Expression<?>> old = new HashMap<>(val);
      Map<Variable<?>, Expression<?>> post =
              path.getOkResult().getPostCondition().getConditions();
      val.clear();
      for (Variable<?> v : old.keySet()) {  
        if (post.containsKey(v)) {
          val.put(v, transformVars( post.get(v), old));
        } else {
          val.put(v, old.get(v));
        }
      }
    }    
    return pc;    
  } 
          
  private static Expression<?> transformVars(Expression<?> in, 
          final Map<Variable<?>, Expression<?>> val) {
    
    return ExpressionUtil.transformVars(
            in, new Function<Variable<?>, Expression<?>>() {
      @Override
      public Expression<?> apply(Variable<?> f) {
        return val.containsKey(f) ? val.get(f) : f;
      }
    });
  }

  private static Expression<Boolean> asPathCondition(ArrayList<Expression<Boolean>> pc) {
    if (pc.isEmpty()) {
      return ExpressionUtil.TRUE;
    }
    
    Expression<Boolean> expr = pc.remove(0);
    if (pc.isEmpty()) {
      return expr;
    }
    
    return ExpressionUtil.and(expr, asPathCondition(pc));
  }
  
  public static Collection<Expression<Boolean>> decomposePath(Expression<Boolean> path) {
    ArrayList<Expression<Boolean>> list = new ArrayList<>();
    decomposePath(path, list);
    list.removeAll(Collections.singleton(ExpressionUtil.TRUE));
    return list;
  }
  
  private static void decomposePath(Expression<Boolean> path, Collection<Expression<Boolean>> atoms) {
    if (!(path instanceof PropositionalCompound)) {
      atoms.add(path);
    } else {
      PropositionalCompound pc = (PropositionalCompound)path;
      decomposePath(pc.getLeft(), atoms);
      decomposePath(pc.getRight(), atoms);
    }
  }    
}
