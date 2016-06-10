/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.util;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.psyco.search.collections.IterationImage;
import gov.nasa.jpf.psyco.search.region.Region;
import java.util.List;

/**
 *
 * @author mmuesly
 */
public interface SearchUtil<T extends Region<?>> {
   public IterationImage<T> post(T newRegion, List<Path> transitionSystem,
          ConstraintSolver solver);

}
