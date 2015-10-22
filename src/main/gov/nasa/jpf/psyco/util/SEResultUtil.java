/*
 * Copyright (C) 2015, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The PSYCO: A Predicate-based Symbolic Compositional Reasoning environment 
 * platform is licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may obtain a 
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 */
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
  
  private static class Mapping implements Function<String, String> {

    private final Map<String, String> map;

    public Mapping(Map<String, String> map) {
      this.map = map;
    }
    
    @Override
    public String apply(String f) {
      String ret = map.get(f);
      if (ret == null) {
        return f;
      }
      return ret;
    }
    
  }
  
  public static Function<String, String> func(Map<String, String> map) {
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
