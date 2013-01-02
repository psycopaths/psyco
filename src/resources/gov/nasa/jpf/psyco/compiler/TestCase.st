package <package>;

import junit.framework.*;

public class PsycoTest<suite><part> extends TestCase {

  public static boolean debug = <debug>;

<tests:{t | 

  public void test<i0>() throws Throwable {
    if (debug) System.out.println("Test<suite><part>.test<i0>");

  <t.params:{p | 
  <p.type> <p.name> = <p.init>; 
  }>
    
  <t.methods,t.checks:{m,c | 
    // step <i0>    
    if (!(<m.precondition>)) {
      fail("Precondition not met: <m.precondition>");
    \}
    <if(c.exception)>
    try {
      <m.call>;
      fail("Expected exception of type <c.exceptionClass>");
    \} catch (<c.exceptionClass> e) {
    \}
    <else>
      <m.call>;
    <endif>
    // perform checks 
    <if(c.checks)>
      <c.checkCalls:{a | 
      <a>;
      }>
    <endif>
  }>
  \} // end of test case <i0>

}>

\}