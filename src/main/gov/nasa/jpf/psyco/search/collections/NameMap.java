/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.collections;

import com.google.common.base.Function;
import java.util.HashMap;

/**
 *
 * @author mmuesly
 */
public class NameMap implements Function<String, String>{

  HashMap<String, String> map;
  
  public NameMap(){
    map = new HashMap<String,String>();
  }
  public void mapNames(String oldName, String newName){
    map.put(oldName, newName);
  }
  @Override
  public String apply(String f) {
    return map.getOrDefault(f, f);
  }
  
}
