/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.jdart.summaries.SummaryStore;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mmuesly
 */
public class SymbolicSearchUtilTest {
  
  public SymbolicSearchUtilTest() {
  }
  
  @Before
  public void setUp() {
    
  }

  @Test
  public void calculateTransitionRegion() throws IOException{
    Config config = new Config("src/examples/symbolicsearch/startingExample/searchTestConfig.jpf");
    config.setProperty("config_path", "/home/vagrant/gsoc-project/psyco_gsoc16");
    FileInputStream inStream = new FileInputStream("/home/vagrant/.jpf/site.properties");
    config.load(inStream);
    inStream = new FileInputStream("jpf.properties");
    config.load(inStream);
    SummaryStore store = SummaryStore.create(config);
    
    Valuation initialValue = store.getInitialValuation();
    assertEquals(1, store.getConcolicMethodIds().size());
    
  }
  // TODO add test methods here.
  // The methods must be annotated with annotation @Test. For example:
  //
  // @Test
  // public void hello() {}
}
