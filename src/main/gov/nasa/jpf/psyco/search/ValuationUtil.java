/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search;

import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import java.util.Collection;

/**
 *
 * @author mmuesly
 */
public class ValuationUtil {

  public static boolean isEmpty(Valuation valuation){
    return valuation.entries().isEmpty();
  }
}
