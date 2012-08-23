/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.summaries;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author falk
 */
public class ClassSummary {
  
  private Class clazz;
  
  private Map<Method,MethodSummary> methodSummaries;
  
  private Set<String> globals;
  
}
