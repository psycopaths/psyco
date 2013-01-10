/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;


import gov.nasa.jpf.JPFShell;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.psyco.explore.MethodExplorer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import summaries.IntProtocol;
import CEV.CEV;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.jdart.ConcolicConfig;
import gov.nasa.jpf.psyco.MethodSummarizer;
import java.lang.reflect.Method;


import gov.nasa.jpf.psyco.summaries.MethodSummary;
import gov.nasa.jpf.psyco.util.MethodUtil;

import gov.nasa.jpf.psyco.oracles.SummaryOracle;

import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.LogManager;
/**
 *
 * @author falk
 */
public class CompilerExample implements JPFShell {
  
  private Config conf;  
  
  public CompilerExample(Config conf) {
    this.conf = conf;
    LogManager.init(conf);
    logger = JPF.getLogger("psyco");    
  }
  
  private JPFLogger logger;

  public void start(String[] strings) {
    
    logger.info("START...");
  
    ConcolicConfig cc = new ConcolicConfig(conf);
    
    try {

//      Class c = CEV.class;
//      Method m1 = c.getMethod("reset", int.class);
//      Method m1 = c.getMethod("lsamRendezvous"); 
      
      Class c = IntProtocol.class;
      Method m1 = c.getMethod("msg", int.class, int.class);
      Method m2 = c.getMethod("recv_ack", int.class);

      List<Method> methods = new ArrayList<Method>();
      methods.add(m1);
      methods.add(m2);      
      MethodSummarizer sum = new MethodSummarizer(conf, methods);

      sum.start(null);  
      Valuation init = sum.getInitialValuation();
      MethodSummary summary1 = sum.getSummaries().get(m1);
      MethodSummary summary2 = sum.getSummaries().get(m2);
      
      SummaryOracle o = new SummaryOracle(init,cc.getSolver(),cc.getMinMax());
      List<MethodSummary> seq = new ArrayList<MethodSummary>();
      seq.add(summary1);
      seq.add(summary2);
      
      o.query(seq, new Constant<Boolean>(Boolean.class, true));

//      seq.clear();
//      seq.add(summary1);
//      seq.add(summary1);
//      seq.add(summary1);
//      seq.add(summary1);
//      o.query(seq, new Constant<Boolean>(Boolean.class, true));
      
    } catch (NoSuchMethodException ex) {
    } catch (SecurityException ex) {
    }
         
  }  
  
}
