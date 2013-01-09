
package <testpackage>;

import gov.nasa.jpf.jdart.Symbolic;
import <sutpackage>.<sutclass>;

public class MethodTestCase {

  <params:{p | 
  @Symbolic("true")
  public static <p.type> <p.name>;  
  }> 

  private static <sutclass> testObject = null;          

  public static void sequence() {
    testObject.<methodName>(
      <trunc(params):{p | <p.name>, 
      }> 
      <if(params)> <last(params).name> <endif>
    );
  }

  public static void main(String[] args) {
    // has to have default constructor ...
    testObject = new <sutclass>();
    sequence();
  }
} 
  
