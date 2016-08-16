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
import gov.nasa.jpf.psyco.alphabet.SummaryAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.automatalib.words.Word;

public class PathUtil {

  public static class PathQuery {

    private final Word<SymbolicMethodSymbol> methods;
    private final Word<Path> paths;

    public PathQuery(Word<SymbolicMethodSymbol> methods, Word<Path> paths) {
      this.methods = methods;
      this.paths = paths;
    }

    public Word<SymbolicMethodSymbol> getMethods() {
      return methods;
    }

    public Word<Path> getPaths() {
      return paths;
    }

  }

  public static Collection<PathQuery> explode(Word<SymbolicMethodSymbol> in, SummaryAlphabet inputs) {
    if (in.length() < 1) {
      Word<Path> eps = Word.epsilon();
      return Collections.singletonList(new PathQuery(in, eps));
    }

    ArrayList<PathQuery> queries = new ArrayList<>();
    Word<Path> eps = Word.epsilon();
    explode(in, 0, eps, queries, inputs);
    return queries;
  }

  private static void explode(Word<SymbolicMethodSymbol> in, int pos,
          Word<Path> prefix, Collection<PathQuery> queries, SummaryAlphabet inputs) {

    SymbolicMethodSymbol a = in.getSymbol(pos);
    SymbolicExecutionResult summary = inputs.getSummary(a);
    pos++;

    for (Path p : summary) {
      if (p.getState() == PathState.OK && pos < in.length()) {
        explode(in, pos, prefix.append(p), queries, inputs);
      } else {
        queries.add(new PathQuery(
                (pos < in.length() ? in.prefix(pos) : in), prefix.append(p)));
      }
    }
  }

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
      Function<String, String> shift
              = SEResultUtil.shift(1, ppos, sms.getArity());
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
      Map<Variable<?>, Expression<?>> post
              = path.getOkResult().getPostCondition().getConditions();
      val.clear();
      for (Variable<?> v : old.keySet()) {
        if (post.containsKey(v)) {
          val.put(v, transformVars(post.get(v), old));
        } else {
          val.put(v, old.get(v));
        }
      }
    }
    return pc;
  }

  public static Expression<?> transformVars(Expression<?> in,
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
      PropositionalCompound pc = (PropositionalCompound) path;
      decomposePath(pc.getLeft(), atoms);
      decomposePath(pc.getRight(), atoms);
    }
  }
}
