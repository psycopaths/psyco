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
import com.google.common.base.Predicate;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.PropositionalCompound;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.constraints.PathResult;
import gov.nasa.jpf.jdart.constraints.PostCondition;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author falkhowar
 */
public class SEResultUtil {

  private static class Restriction implements Predicate<Variable<?>> {

    private final Set<String> set;

    public Restriction(Set<String> set) {
      this.set = set;
    }

    @Override
    public boolean apply(Variable<?> t) {
      return set.contains(t.getName());
    }

  }
  
  private static class Mapping<T1, T2> implements Function<T1, T2> {

    private final Map<T1, T2> map;

    public Mapping(Map<T1, T2> map) {
      this.map = map;
    }
    
    @Override
    public T2 apply(T1 f) {
      T2 ret = map.get(f);
      if (ret == null) {
        // FIXME: this may not always work!
        return (T2) f;
      }
      return ret;
    }
    
  }
  
  public static <T1, T2> Function<T1, T2> func(Map<T1, T2> map) {
    return new Mapping(map);
  }
  
  public static Function<String, String> shift(int from, int to, int count) {
    Map<String, String> map = new HashMap<>(); 
    for (int i=0; i<count; i++) {
      map.put("P" + (from + i), "P" + (to + i));
    }
    return func(map);
  }
  
  public static Predicate<Variable<?>> interval(int offset, int count) {
    Set<String> set = new HashSet<>();
    for (int i=0; i<count; i++) {
      set.add("P" + (offset + i));
    }
    return new Restriction(set);
  }
  
  //public static 
  
  public static SymbolicExecutionResult rename(SymbolicExecutionResult in, 
          Function<String, String> repl) {
    
    ArrayList<Path> ok = new ArrayList();
    for (Path p : in.getOk()) {
      ok.add(rename(p, repl));
    }
    ArrayList<Path> err = new ArrayList();
    for (Path p : in.getError()) {
      err.add(rename(p, repl));
    }
    ArrayList<Path> dk = new ArrayList();
    for (Path p : in.getDontKnow()) {
      dk.add(rename(p, repl));
    }
    
    return new SymbolicExecutionResult(ok, err, dk);
  }
  
  public static Path rename(Path in, Function<String, String> repl) {
        
    Expression<Boolean> pc = ExpressionUtil.renameVars(in.getPathCondition(), repl);
    PathResult res = null;
    
    switch (in.getState()) {
      case OK: 
        PathResult.OkResult ok = in.getOkResult();
        res = PathResult.ok(rename(ok.getValuation(), repl), 
                rename(ok.getPostCondition(), repl));
        break;
      case ERROR:
        PathResult.ErrorResult err = in.getErrorResult();
        res = PathResult.error(rename(err.getValuation(), repl), 
                err.getExceptionClass(), err.getStackTrace());
        break;
      case DONT_KNOW:    
        res = PathResult.dontKnow();
        break;
    }
    
    return new Path(pc, res);    
  }
  
  
  private static Valuation rename(Valuation in, Function<String, String> repl) {
    Valuation ret = new Valuation();
    for (Variable v : in.getVariables()) {
      Variable vNew = new Variable(v.getType(), repl.apply(v.getName()));
      ret.setValue(vNew, in.getValue(v));
    }
    return ret;
  }
  
  private static PostCondition rename(PostCondition in, Function<String, String> repl) {
    PostCondition ret = new PostCondition();
    for (Entry<Variable<?>, Expression<?>> e : in.getConditions().entrySet()) {
      ret.addCondition(
              (Variable) ExpressionUtil.renameVars(e.getKey(), repl), 
              (Expression) ExpressionUtil.renameVars(e.getValue(), repl));
    }
    return ret;
  }

  
 public static Expression<Boolean> stripLeadingTrue(Expression<Boolean> in) {
    if (in == null) {
      return in;
    }
    
    if (in instanceof PropositionalCompound) {
      PropositionalCompound pc = (PropositionalCompound)in;
      if (pc.getLeft().equals(ExpressionUtil.TRUE)) {
        return stripLeadingTrue(pc.getRight());
      }
    }
    
    if (in.equals(ExpressionUtil.TRUE)) {
      return null;
    }
    return in;
  }  
 
}
