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
import exampleProtocolSteffen2011.IntProtocol;
import CEV.CEV;
import java.lang.reflect.Method;

import solvers.TrueConstant;

import gov.nasa.jpf.psyco.summaries.MethodSummary;
import gov.nasa.jpf.psyco.summaries.InitialValuation;
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
    
    try {
      
//      Class c = IntProtocol.class;
//      Method m1 = c.getMethod("msg", int.class, int.class);
//      Method m2 = c.getMethod("recv_ack", int.class);
       
      Class c = CEV.class;
      Method m1 = c.getMethod("reset", int.class);
              
      MethodExplorer mex1 = new MethodExplorer(m1,conf);
      MethodSummary summary1 = mex1.execute();

//      MethodExplorer mex2 = new MethodExplorer(m2,conf);
//      MethodSummary summary2 = mex2.execute();
      
      System.out.println(summary1);
//      System.out.println(summary2);
  
      InitialValuation init = new InitialValuation();
//      init.setValue("buffer_empty", 1);
//      init.setValue("expect", 0);
      
      SummaryOracle o = new SummaryOracle(init);
      List<MethodSummary> seq = new ArrayList<MethodSummary>();
      seq.add(summary1);
      seq.add(summary1);
      seq.add(summary1);
      seq.add(summary1);
      
      o.query(seq, new TrueConstant());
      
    } catch (NoSuchMethodException ex) {
    } catch (SecurityException ex) {
    }
         
  }  
  
}
