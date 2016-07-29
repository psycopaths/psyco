int __BLAST_NONDET;

// method ids
<methodIds:{m | 
int <m.name> = <m.val>;
}> 

int main() {
  
  int q = 0;
  int method_id;

  // variables
  <vars:{v | 
  <v.type> <v.name> = <v.val>;
  }>   

  while (1) {

    // parameters
    <pars:{p | 
    <p.type> <p.name>;
    }>
   
    // states
    <states:{s | 
    if (q == <s.id>){      
      <s.trans:{t | 
      if(__BLAST_NONDET){
        // assume guard
        if ( <t.pre> ){ 
          // record method id
          method_id = <t.id>;
          // non-conformance condition 
          if ( <t.assertion> ) {
            goto ERROR;
          \}
          // state update
          q = <t.succ>;
          // post condition
          <t.post>
        \}
        continue;
      \}
      }>
      continue;
    \}
    }>   

  \} // end while

  return 0;

  ERROR: goto ERROR;
\}
