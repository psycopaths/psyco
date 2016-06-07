/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.region.util;

import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.psyco.search.region.Region;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mmuesly
 * @param <T> The specific region type. For example ValuationRegion.
 */
public interface RegionUtil<T extends Region> {

  public T disjunction(T regionA, T regionB);

  public T conjunction(T regionA, T regionB);

  public T difference(T outterRegion, T excludedRegion);

  public T exists(T aRegion, Set<Variable<?>> subsetOfVariables);

  public Set<Variable<?>> convertToVariableSet(T region);

  public T rename(T region, List<Variable<?>> oldNames, List<Variable<?>> newNames);
}
