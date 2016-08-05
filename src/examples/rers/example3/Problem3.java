package rers.example3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import gov.nasa.jpf.jdart.Symbolic;

public class Problem3 {
	static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

  @Symbolic("true")
	public int[] inputs = {66,69,67,65,68};

  @Symbolic("true")
	public int a171 = 103;
  @Symbolic("true")
	public int a5 = 104;
  @Symbolic("true")
	public int a37 = 4;
  @Symbolic("true")
	public int a131 = 7;
  @Symbolic("true")
	public int a157 = 10;
  @Symbolic("true")
	public int a6 = 102;
  @Symbolic("true")
	public int a165 = 101;
  @Symbolic("true")
	public int a112 = 8;
  @Symbolic("true")
	public int a103 = 103;
  @Symbolic("true")
	public int a182 = 10;
  @Symbolic("true")
	public int a155 = 8;
  @Symbolic("true")
	public int a135 = 103;
  @Symbolic("true")
	public int a184 = 5;
  @Symbolic("true")
	public int a161 = 6;
  @Symbolic("true")
	public int a183 = 103;
  @Symbolic("true")
	public int a52 = 101;
  @Symbolic("true")
	public int a77 = 103;
  @Symbolic("true")
	public int a118 = 105;
  @Symbolic("true")
	public int a105 = 14;
  @Symbolic("true")
	public int a17 = 12;
  @Symbolic("true")
	public int a75 = 103;
  @Symbolic("true")
	public int a122 = 103;
  @Symbolic("true")
	public int a82 = 105;
  @Symbolic("true")
	public int a7 = 9;
  @Symbolic("true")
	public int a163 = 103;
  @Symbolic("true")
	public int a142 = 10;
  @Symbolic("true")
	public int a128 = 5;
  @Symbolic("true")
	public int a104 = 15;
  @Symbolic("true")
	public int a199 = 14;
  @Symbolic("true")
	public int a3 = 17;
  @Symbolic("true")
	public int a176 = 103;
  @Symbolic("true")
	public int a47 = 9;
  @Symbolic("true")
	public int a53 = 103;
  @Symbolic("true")
	public int a150 = 103;
  @Symbolic("true")
	public int a138 = 105;
  @Symbolic("true")
	public int a12 = 13;
  @Symbolic("true")
	public int a36 = 11;
  @Symbolic("true")
	public int a85 = 12;
  @Symbolic("true")
	public int a119 = 104;
  @Symbolic("true")
	public int a180 = 104;
  @Symbolic("true")
	public int a84 = 103;
  @Symbolic("true")
	public int a110 = 8;
  @Symbolic("true")
	public int a57 = 3;
  @Symbolic("true")
	public int a87 = 102;
  @Symbolic("true")
	public int a44 = 13;
  @Symbolic("true")
	public int a15 = 103;
	public boolean cf = true;
  @Symbolic("true")
	public int a175 = 4;
  @Symbolic("true")
	public int a145 = 1;
  @Symbolic("true")
	public int a16 = 103;
  @Symbolic("true")
	public int a190 = 103;

	public void errorCheck() {
	    if((((a150 == 104) && (a6 == 103)) && (a138 == 101))){
	    	cf = false;
	    	Errors.__VERIFIER_error(0);
	    }
	    if((((a17 == 8) && (a6 == 104)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(1);
	    }
	    if((((a3 == 12) && (a145 == 2)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(2);
	    }
	    if((((a44 == 9) && (a105 == 11)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(3);
	    }
	    if((((a36 == 10) && (a145 == 3)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(4);
	    }
	    if((((a52 == 104) && (a105 == 16)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(5);
	    }
	    if((((a36 == 16) && (a145 == 3)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(6);
	    }
	    if((((a7 == 8) && (a145 == 6)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(7);
	    }
	    if((((a110 == 4) && (a82 == 101)) && (a138 == 103))){
	    	cf = false;
	    	Errors.__VERIFIER_error(8);
	    }
	    if((((a119 == 102) && (a82 == 105)) && (a138 == 103))){
	    	cf = false;
	    	Errors.__VERIFIER_error(9);
	    }
	    if((((a165 == 102) && (a105 == 12)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(10);
	    }
	    if((((a3 == 16) && (a145 == 2)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(11);
	    }
	    if((((a112 == 11) && (a105 == 10)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(12);
	    }
	    if((((a77 == 104) && (a145 == 8)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(13);
	    }
	    if((((a183 == 103) && (a6 == 104)) && (a138 == 101))){
	    	cf = false;
	    	Errors.__VERIFIER_error(14);
	    }
	    if((((a190 == 105) && (a6 == 103)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(15);
	    }
	    if((((a199 == 12) && (a6 == 101)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(16);
	    }
	    if((((a36 == 13) && (a145 == 3)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(17);
	    }
	    if((((a17 == 6) && (a6 == 104)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(18);
	    }
	    if((((a104 == 16) && (a145 == 7)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(19);
	    }
	    if((((a180 == 101) && (a105 == 14)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(20);
	    }
	    if((((a150 == 102) && (a6 == 103)) && (a138 == 101))){
	    	cf = false;
	    	Errors.__VERIFIER_error(21);
	    }
	    if((((a190 == 102) && (a6 == 103)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(22);
	    }
	    if((((a118 == 105) && (a6 == 105)) && (a138 == 101))){
	    	cf = false;
	    	Errors.__VERIFIER_error(23);
	    }
	    if((((a87 == 103) && (a82 == 103)) && (a138 == 103))){
	    	cf = false;
	    	Errors.__VERIFIER_error(24);
	    }
	    if((((a36 == 17) && (a145 == 3)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(25);
	    }
	    if((((a112 == 10) && (a105 == 10)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(26);
	    }
	    if((((a7 == 14) && (a145 == 6)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(27);
	    }
	    if((((a5 == 103) && (a6 == 101)) && (a138 == 101))){
	    	cf = false;
	    	Errors.__VERIFIER_error(28);
	    }
	    if((((a87 == 105) && (a82 == 103)) && (a138 == 103))){
	    	cf = false;
	    	Errors.__VERIFIER_error(29);
	    }
	    if((((a119 == 104) && (a82 == 105)) && (a138 == 103))){
	    	cf = false;
	    	Errors.__VERIFIER_error(30);
	    }
	    if((((a150 == 103) && (a145 == 4)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(31);
	    }
	    if((((a87 == 104) && (a82 == 103)) && (a138 == 103))){
	    	cf = false;
	    	Errors.__VERIFIER_error(32);
	    }
	    if((((a183 == 104) && (a6 == 104)) && (a138 == 101))){
	    	cf = false;
	    	Errors.__VERIFIER_error(33);
	    }
	    if((((a182 == 12) && (a6 == 105)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(34);
	    }
	    if((((a17 == 13) && (a6 == 104)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(35);
	    }
	    if((((a112 == 5) && (a105 == 10)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(36);
	    }
	    if((((a110 == 2) && (a82 == 101)) && (a138 == 103))){
	    	cf = false;
	    	Errors.__VERIFIER_error(37);
	    }
	    if((((a182 == 8) && (a6 == 105)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(38);
	    }
	    if((((a183 == 101) && (a6 == 104)) && (a138 == 101))){
	    	cf = false;
	    	Errors.__VERIFIER_error(39);
	    }
	    if((((a7 == 14) && (a105 == 15)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(40);
	    }
	    if((((a16 == 102) && (a145 == 1)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(41);
	    }
	    if((((a104 == 12) && (a145 == 7)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(42);
	    }
	    if((((a110 == 3) && (a82 == 101)) && (a138 == 103))){
	    	cf = false;
	    	Errors.__VERIFIER_error(43);
	    }
	    if((((a3 == 11) && (a145 == 2)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(44);
	    }
	    if((((a7 == 11) && (a82 == 102)) && (a138 == 103))){
	    	cf = false;
	    	Errors.__VERIFIER_error(45);
	    }
	    if((((a128 == 8) && (a105 == 13)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(46);
	    }
	    if((((a157 == 10) && (a6 == 102)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(47);
	    }
	    if((((a12 == 13) && (a105 == 9)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(48);
	    }
	    if((((a17 == 10) && (a6 == 104)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(49);
	    }
	    if((((a12 == 8) && (a105 == 9)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(50);
	    }
	    if((((a17 == 7) && (a6 == 104)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(51);
	    }
	    if((((a16 == 101) && (a145 == 1)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(52);
	    }
	    if((((a128 == 6) && (a105 == 13)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(53);
	    }
	    if((((a77 == 105) && (a145 == 8)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(54);
	    }
	    if((((a128 == 4) && (a105 == 13)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(55);
	    }
	    if((((a182 == 9) && (a6 == 105)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(56);
	    }
	    if((((a7 == 9) && (a145 == 6)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(57);
	    }
	    if((((a3 == 13) && (a145 == 2)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(58);
	    }
	    if((((a180 == 105) && (a105 == 14)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(59);
	    }
	    if((((a7 == 13) && (a82 == 102)) && (a138 == 103))){
	    	cf = false;
	    	Errors.__VERIFIER_error(60);
	    }
	    if((((a119 == 105) && (a145 == 5)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(61);
	    }
	    if((((a17 == 12) && (a6 == 104)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(62);
	    }
	    if((((a118 == 101) && (a6 == 105)) && (a138 == 101))){
	    	cf = false;
	    	Errors.__VERIFIER_error(63);
	    }
	    if((((a7 == 10) && (a82 == 102)) && (a138 == 103))){
	    	cf = false;
	    	Errors.__VERIFIER_error(64);
	    }
	    if((((a180 == 102) && (a105 == 14)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(65);
	    }
	    if((((a165 == 104) && (a105 == 12)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(66);
	    }
	    if((((a7 == 8) && (a105 == 15)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(67);
	    }
	    if((((a157 == 12) && (a6 == 102)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(68);
	    }
	    if((((a44 == 12) && (a6 == 102)) && (a138 == 101))){
	    	cf = false;
	    	Errors.__VERIFIER_error(69);
	    }
	    if((((a17 == 9) && (a6 == 104)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(70);
	    }
	    if((((a7 == 11) && (a145 == 6)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(71);
	    }
	    if((((a150 == 101) && (a145 == 4)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(72);
	    }
	    if((((a5 == 103) && (a82 == 104)) && (a138 == 103))){
	    	cf = false;
	    	Errors.__VERIFIER_error(73);
	    }
	    if((((a128 == 9) && (a105 == 13)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(74);
	    }
	    if((((a5 == 102) && (a82 == 104)) && (a138 == 103))){
	    	cf = false;
	    	Errors.__VERIFIER_error(75);
	    }
	    if((((a12 == 11) && (a105 == 9)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(76);
	    }
	    if((((a52 == 101) && (a105 == 16)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(77);
	    }
	    if((((a199 == 11) && (a6 == 101)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(78);
	    }
	    if((((a183 == 105) && (a6 == 104)) && (a138 == 101))){
	    	cf = false;
	    	Errors.__VERIFIER_error(79);
	    }
	    if((((a118 == 103) && (a6 == 105)) && (a138 == 101))){
	    	cf = false;
	    	Errors.__VERIFIER_error(80);
	    }
	    if((((a44 == 12) && (a105 == 11)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(81);
	    }
	    if((((a128 == 7) && (a105 == 13)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(82);
	    }
	    if((((a12 == 15) && (a105 == 9)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(83);
	    }
	    if((((a150 == 103) && (a6 == 103)) && (a138 == 101))){
	    	cf = false;
	    	Errors.__VERIFIER_error(84);
	    }
	    if((((a199 == 15) && (a6 == 101)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(85);
	    }
	    if((((a17 == 11) && (a6 == 104)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(86);
	    }
	    if((((a3 == 15) && (a145 == 2)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(87);
	    }
	    if((((a12 == 9) && (a105 == 9)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(88);
	    }
	    if((((a7 == 12) && (a145 == 6)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(89);
	    }
	    if((((a12 == 10) && (a105 == 9)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(90);
	    }
	    if((((a44 == 8) && (a105 == 11)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(91);
	    }
	    if((((a182 == 7) && (a6 == 105)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(92);
	    }
	    if((((a52 == 102) && (a105 == 16)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(93);
	    }
	    if((((a128 == 10) && (a105 == 13)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(94);
	    }
	    if((((a190 == 101) && (a6 == 103)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(95);
	    }
	    if((((a44 == 13) && (a105 == 11)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(96);
	    }
	    if((((a199 == 13) && (a6 == 101)) && (a138 == 104))){
	    	cf = false;
	    	Errors.__VERIFIER_error(97);
	    }
	    if((((a104 == 14) && (a145 == 7)) && (a138 == 105))){
	    	cf = false;
	    	Errors.__VERIFIER_error(98);
	    }
	    if((((a52 == 103) && (a105 == 16)) && (a138 == 102))){
	    	cf = false;
	    	Errors.__VERIFIER_error(99);
	    }
	}
public  void calculateOutputm32(int input) {
    if((((a184 == 3) && (a84 == 101)) && (((a175 == 2) && ((((input == 68) && cf) && (a176 == 101)) && (a175 == 2))) && (a103 == 101)))) {
    	cf = false;
    	a103 = 103;
    	a138 = 103;
    	a53 = 103;
    	a75 = 103;
    	a142 = 10;
    	a84 = 103;
    	a131 = 7;
    	a184 = 5;
    	a161 = 6;
    	a82 = 101;
    	a163 = 103;
    	a176 = 103;
    	a122 = 103;
    	a135 = 103;
    	a175 = 4;
    	a110 = 3; 
    	System.out.println("W");
    } 
    if((((((cf && (input == 67)) && (a175 == 2)) && (a171 == 101)) && (a84 == 101)) && ((a171 == 101) && ((a15 == 101) && (a161 == 4))))) {
    	cf = false;
    	a103 = 103;
    	a82 = 103;
    	a15 = 103;
    	a161 = 6;
    	a138 = 103;
    	a176 = 103;
    	a163 = 103;
    	a47 = 9;
    	a135 = 103;
    	a53 = 103;
    	a87 = 104;
    	a131 = 7; 
    	System.out.println("T");
    } 
    if((((a131 == 5) && (a47 == 7)) && ((a103 == 101) && (((a184 == 3) && ((a75 == 101) && (cf && (input == 66)))) && (a37 == 2))))) {
    	cf = false;
    	a184 = 4;
    	a175 = 3;
    	a122 = 102;
    	a103 = 102;
    	a84 = 102;
    	a131 = 6;
    	a15 = 102;
    	a138 = 104;
    	a161 = 5;
    	a6 = 102;
    	a135 = 102;
    	a37 = 3;
    	a176 = 102;
    	a157 = 10; 
    	System.out.println("Z");
    } 
    if((((a57 == 1) && (a53 == 101)) && (((a15 == 101) && ((a131 == 5) && ((cf && (input == 66)) && (a135 == 101)))) && (a176 == 101)))) {
    	cf = false;
    	a52 = 105;
    	a131 = 6;
    	a176 = 102;
    	a15 = 102;
    	a84 = 103;
    	a53 = 102;
    	a161 = 5;
    	a57 = 3;
    	a47 = 8;
    	a103 = 102;
    	a37 = 3;
    	a155 = 7;
    	a142 = 9;
    	a171 = 102;
    	a138 = 102;
    	a122 = 102;
    	a163 = 102;
    	a184 = 4;
    	a75 = 102;
    	a175 = 3;
    	a135 = 102;
    	a105 = 16; 
    	System.out.println("V");
    } 
    if((((a122 == 101) && (((input == 101) && cf) && (a75 == 101))) && ((a131 == 5) && (((a131 == 5) && (a184 == 3)) && (a175 == 2))))) {
    	cf = false;
    	a183 = 102;
    	a6 = 104;
    	a85 = 10; 
    	System.out.println("U");
    } 
}
public  void calculateOutputm33(int input) {
    if((((((input == 67) && cf) && (a135 == 101)) && (a47 == 7)) && ((((a84 == 101) && (a171 == 101)) && (a176 == 101)) && (a142 == 8)))) {
    	cf = false;
    	a135 = 102;
    	a57 = 2;
    	a176 = 102;
    	a84 = 102;
    	a47 = 8;
    	a180 = 105;
    	a75 = 102;
    	a131 = 6;
    	a103 = 102;
    	a122 = 102;
    	a53 = 102;
    	a184 = 4;
    	a138 = 102;
    	a105 = 14; 
    	System.out.println("Y");
    } 
    if(((((a75 == 101) && ((a84 == 101) && ((a47 == 7) && (a103 == 101)))) && (a37 == 2)) && ((a161 == 4) && (cf && (input == 68))))) {
    	cf = false;
    	a145 = 3;
    	a37 = 4;
    	a57 = 3;
    	a84 = 103;
    	a75 = 103;
    	a184 = 5;
    	a131 = 7;
    	a175 = 4;
    	a15 = 103;
    	a155 = 8;
    	a138 = 105;
    	a85 = 12;
    	a36 = 10; 
    	System.out.println("V");
    } 
    if(((((((a84 == 101) && (a75 == 101)) && (a57 == 1)) && (a131 == 5)) && (a176 == 101)) && ((a103 == 101) && ((input == 101) && cf)))) {
    	cf = false;
    	a176 = 102;
    	a85 = 11;
    	a53 = 102;
    	a142 = 9;
    	a138 = 102;
    	a15 = 102;
    	a122 = 102;
    	a103 = 102;
    	a57 = 2;
    	a47 = 8;
    	a105 = 9;
    	a131 = 6;
    	a37 = 3;
    	a12 = 13; 
    	System.out.println("W");
    } 
    if((((a15 == 101) && (((a103 == 101) && (cf && (input == 66))) && (a171 == 101))) && ((a131 == 5) && ((a103 == 101) && (a84 == 101))))) {
    	cf = false;
    	a122 = 102;
    	a57 = 2;
    	a138 = 104;
    	a85 = 11;
    	a155 = 7;
    	a103 = 102;
    	a161 = 5;
    	a171 = 102;
    	a131 = 6;
    	a6 = 104;
    	a175 = 3;
    	a37 = 3;
    	a17 = 13; 
    	System.out.println("W");
    } 
    if((((a135 == 101) && (a184 == 3)) && (((a85 == 10) && ((a175 == 2) && (((input == 66) && cf) && (a135 == 101)))) && (a37 == 2)))) {
    	cf = false;
    	a84 = 103;
    	a6 = 105;
    	a163 = 101;
    	a118 = 102; 
    	System.out.println("U");
    } 
}
public  void calculateOutputm1(int input) {
    if((((a176 == 101) && (a84 == 101)) && (((a163 == 101) && ((a15 == 101) && ((a37 == 2) && ((a5 == 101) && cf)))) && (a122 == 101)))) {
    	calculateOutputm32(input);
    } 
    if((((((((a103 == 101) && (a15 == 101)) && (a75 == 101)) && (a75 == 101)) && (a47 == 7)) && (a15 == 101)) && (cf && (a5 == 102)))) {
    	calculateOutputm33(input);
    } 
}
public  void calculateOutputm40(int input) {
    if((((a85 == 10) && ((a175 == 2) && ((input == 66) && cf))) && ((((a85 == 10) && (a135 == 101)) && (a142 == 8)) && (a163 == 101)))) {
    	cf = false;
    	a119 = 102;
    	a161 = 6;
    	a176 = 103;
    	a53 = 103;
    	a131 = 7;
    	a184 = 5;
    	a138 = 103;
    	a75 = 103;
    	a82 = 105;
    	a85 = 12;
    	a155 = 8;
    	a135 = 103;
    	a103 = 103;
    	a47 = 9;
    	a175 = 4;
    	a37 = 4; 
    	System.out.println("Z");
    } 
    if(((((a75 == 101) && ((a53 == 101) && (a103 == 101))) && (a84 == 101)) && ((a53 == 101) && ((cf && (input == 66)) && (a175 == 2))))) {
    	cf = false;
    	a103 = 102;
    	a142 = 9;
    	a105 = 9;
    	a176 = 102;
    	a131 = 6;
    	a138 = 102;
    	a85 = 11;
    	a122 = 102;
    	a171 = 102;
    	a47 = 8;
    	a53 = 102;
    	a37 = 3;
    	a163 = 102;
    	a12 = 11; 
    	System.out.println("U");
    } 
    if((((a171 == 101) && ((a135 == 101) && (a131 == 5))) && ((a75 == 101) && ((a75 == 101) && ((a142 == 8) && ((input == 68) && cf)))))) {
    	cf = false;
    	a171 = 102;
    	a75 = 102;
    	a163 = 102;
    	a15 = 102;
    	a84 = 102;
    	a161 = 6;
    	a138 = 102;
    	a57 = 2;
    	a85 = 11;
    	a105 = 9;
    	a47 = 8;
    	a142 = 9;
    	a37 = 3;
    	a122 = 102;
    	a155 = 7;
    	a53 = 102;
    	a184 = 4;
    	a135 = 102;
    	a176 = 102;
    	a131 = 6;
    	a175 = 3;
    	a103 = 102;
    	a12 = 12; 
    	System.out.println("U");
    } 
    if(((((a161 == 4) && (((input == 101) && cf) && (a37 == 2))) && (a176 == 101)) && (((a15 == 101) && (a57 == 1)) && (a75 == 101)))) {
    	cf = false;
    	a155 = 8;
    	a75 = 103;
    	a161 = 6;
    	a150 = 101;
    	a37 = 4;
    	a135 = 103;
    	a184 = 5;
    	a84 = 103;
    	a57 = 3;
    	a163 = 103;
    	a47 = 9;
    	a131 = 7;
    	a138 = 105;
    	a53 = 103;
    	a145 = 4; 
    	System.out.println("S");
    } 
    if((((a184 == 3) && ((a85 == 10) && ((((a103 == 101) && (a175 == 2)) && (a84 == 101)) && (a184 == 3)))) && ((input == 67) && cf))) {
    	cf = false;
    	a53 = 102;
    	a103 = 102;
    	a135 = 102;
    	a180 = 102;
    	a131 = 6;
    	a84 = 102;
    	a163 = 102;
    	a176 = 102;
    	a122 = 102;
    	a138 = 102;
    	a57 = 2;
    	a142 = 9;
    	a47 = 8;
    	a105 = 14; 
    	System.out.println("W");
    } 
}
public  void calculateOutputm4(int input) {
    if((((((a15 == 101) && (cf && (a183 == 102))) && (a53 == 101)) && (a175 == 2)) && ((a53 == 101) && ((a163 == 101) && (a37 == 2))))) {
    	calculateOutputm40(input);
    } 
}
public  void calculateOutputm45(int input) {
    if((((((a122 == 101) && (a53 == 101)) && (a142 == 8)) && (a75 == 101)) && (((a37 == 2) && (cf && (input == 67))) && (a131 == 5)))) {
    	cf = false;
    	a122 = 103;
    	a184 = 5;
    	a53 = 103;
    	a37 = 4;
    	a175 = 4;
    	a131 = 7;
    	a75 = 103;
    	a176 = 103;
    	a15 = 103;
    	a138 = 105;
    	a85 = 12;
    	a145 = 3;
    	a57 = 3;
    	a36 = 13; 
    	System.out.println("Z");
    } 
    if(((((input == 66) && cf) && (a142 == 8)) && (((a175 == 2) && (((a122 == 101) && (a171 == 101)) && (a171 == 101))) && (a53 == 101)))) {
    	cf = false;
    	a53 = 102;
    	a163 = 102;
    	a175 = 3;
    	a103 = 102;
    	a57 = 2;
    	a161 = 5;
    	a122 = 102;
    	a37 = 3;
    	a138 = 104;
    	a131 = 6;
    	a142 = 9;
    	a135 = 102;
    	a6 = 104;
    	a155 = 7;
    	a47 = 8;
    	a17 = 9; 
    	System.out.println("U");
    } 
    if((((((a75 == 101) && (cf && (input == 66))) && (a155 == 6)) && (a85 == 10)) && (((a184 == 3) && (a15 == 101)) && (a57 == 1)))) {
    	cf = false;
    	 
    	System.out.println("Z");
    } 
    if(((((a103 == 101) && ((a75 == 101) && (a57 == 1))) && (a103 == 101)) && ((a75 == 101) && ((cf && (input == 101)) && (a103 == 101))))) {
    	cf = false;
    	a163 = 102;
    	a122 = 102;
    	a138 = 104;
    	a175 = 3;
    	a75 = 102;
    	a15 = 102;
    	a37 = 3;
    	a131 = 6;
    	a84 = 102;
    	a161 = 5;
    	a6 = 101;
    	a199 = 11; 
    	System.out.println("U");
    } 
    if(((((cf && (input == 68)) && (a122 == 101)) && (a15 == 101)) && ((((a163 == 101) && (a131 == 5)) && (a171 == 101)) && (a47 == 7)))) {
    	cf = false;
    	 
    	System.out.println("Z");
    } 
}
public  void calculateOutputm5(int input) {
    if(((((a155 == 6) && ((a171 == 101) && (a85 == 10))) && (a15 == 101)) && (((cf && (a118 == 102)) && (a103 == 101)) && (a161 == 4)))) {
    	calculateOutputm45(input);
    } 
}
public  void calculateOutputm52(int input) {
    if((((a176 == 102) && ((cf && (input == 68)) && (a84 == 102))) && ((a47 == 8) && ((a15 == 102) && ((a15 == 102) && (a75 == 102)))))) {
    	cf = false;
    	a142 = 10;
    	a138 = 105;
    	a175 = 4;
    	a47 = 9;
    	a57 = 3;
    	a131 = 7;
    	a75 = 103;
    	a53 = 103;
    	a171 = 103;
    	a84 = 103;
    	a163 = 103;
    	a135 = 103;
    	a15 = 103;
    	a37 = 4;
    	a145 = 2;
    	a3 = 12; 
    	System.out.println("V");
    } 
    if(((((a184 == 4) && ((a15 == 102) && ((a57 == 2) && (a84 == 102)))) && (a122 == 102)) && ((a103 == 102) && ((input == 67) && cf)))) {
    	cf = false;
    	a135 = 103;
    	a47 = 9;
    	a37 = 4;
    	a122 = 103;
    	a57 = 3;
    	a163 = 103;
    	a176 = 103;
    	a175 = 4;
    	a16 = 102;
    	a138 = 105;
    	a84 = 103;
    	a131 = 7;
    	a142 = 10;
    	a145 = 1; 
    	System.out.println("V");
    } 
    if(((((a135 == 102) && (a131 == 6)) && (a57 == 2)) && ((a163 == 102) && ((a155 == 7) && ((a85 == 11) && ((input == 66) && cf)))))) {
    	cf = false;
    	a6 = 104;
    	a138 = 104;
    	a161 = 5;
    	a17 = 10; 
    	System.out.println("Z");
    } 
    if(((((a184 == 4) && ((a135 == 102) && (cf && (input == 66)))) && (a142 == 9)) && ((a122 == 102) && ((a85 == 11) && (a184 == 4))))) {
    	cf = false;
    	a57 = 3;
    	a171 = 103;
    	a175 = 4;
    	a142 = 10;
    	a47 = 9;
    	a176 = 103;
    	a131 = 7;
    	a77 = 103;
    	a122 = 103;
    	a85 = 12;
    	a103 = 103;
    	a135 = 103;
    	a138 = 105;
    	a155 = 8;
    	a184 = 5;
    	a37 = 4;
    	a53 = 103;
    	a15 = 103;
    	a84 = 103;
    	a163 = 103;
    	a75 = 103;
    	a145 = 8; 
    	System.out.println("S");
    } 
    if(((((a171 == 102) && (a131 == 6)) && (a175 == 3)) && ((a122 == 102) && (((a37 == 3) && (cf && (input == 101))) && (a84 == 102))))) {
    	cf = false;
    	a135 = 103;
    	a190 = 104;
    	a138 = 104;
    	a6 = 103;
    	a161 = 5;
    	a85 = 12; 
    	System.out.println("U");
    } 
}
public  void calculateOutputm6(int input) {
    if((((a171 == 102) && ((a184 == 4) && ((((a84 == 102) && ((a12 == 12) && cf)) && (a15 == 102)) && (a184 == 4)))) && (a122 == 102))) {
    	calculateOutputm52(input);
    } 
}
public  void calculateOutputm56(int input) {
    if((((a15 == 102) && (a57 == 2)) && ((a37 == 3) && ((a142 == 9) && (((a85 == 11) && (cf && (input == 101))) && (a84 == 102)))))) {
    	cf = false;
    	a184 = 5;
    	a122 = 103;
    	a84 = 103;
    	a57 = 3;
    	a176 = 103;
    	a145 = 3;
    	a75 = 103;
    	a175 = 4;
    	a53 = 103;
    	a138 = 105;
    	a131 = 7;
    	a37 = 4;
    	a85 = 12;
    	a36 = 17; 
    	System.out.println("Z");
    } 
    if((((((a176 == 102) && ((input == 68) && cf)) && (a135 == 102)) && (a84 == 102)) && ((a47 == 8) && ((a155 == 7) && (a15 == 102))))) {
    	cf = false;
    	a175 = 2;
    	a53 = 101;
    	a84 = 103;
    	a163 = 101;
    	a155 = 6;
    	a176 = 101;
    	a47 = 7;
    	a103 = 101;
    	a142 = 8;
    	a15 = 101;
    	a131 = 5;
    	a122 = 101;
    	a118 = 102;
    	a171 = 101;
    	a57 = 1;
    	a75 = 101;
    	a6 = 105;
    	a37 = 2;
    	a138 = 101;
    	a135 = 101;
    	a85 = 10;
    	a184 = 3; 
    	System.out.println("Z");
    } 
    if((((a103 == 102) && (a53 == 102)) && ((((((input == 66) && cf) && (a176 == 102)) && (a15 == 102)) && (a122 == 102)) && (a175 == 3)))) {
    	cf = false;
    	a155 = 6;
    	a176 = 101;
    	a103 = 101;
    	a15 = 101;
    	a163 = 101;
    	a138 = 101;
    	a175 = 2;
    	a135 = 101;
    	a150 = 102;
    	a85 = 10;
    	a142 = 8;
    	a53 = 101;
    	a122 = 101;
    	a6 = 103;
    	a47 = 7; 
    	System.out.println("U");
    } 
    if((((a184 == 4) && ((((a103 == 102) && (a131 == 6)) && (a176 == 102)) && (a175 == 3))) && ((a175 == 3) && ((input == 66) && cf)))) {
    	cf = false;
    	a161 = 5;
    	a105 = 13;
    	a128 = 4; 
    	System.out.println("Z");
    } 
    if((((a37 == 3) && ((a122 == 102) && (((input == 67) && cf) && (a135 == 102)))) && ((a142 == 9) && ((a75 == 102) && (a57 == 2))))) {
    	cf = false;
    	a105 = 15;
    	a161 = 5;
    	a7 = 8; 
    	System.out.println("U");
    } 
}
public  void calculateOutputm7(int input) {
    if((((((a47 == 8) && (a37 == 3)) && (a184 == 4)) && (a53 == 102)) && ((a53 == 102) && ((cf && (a112 == 9)) && (a163 == 102))))) {
    	calculateOutputm56(input);
    } 
}
public  void calculateOutputm61(int input) {
    if((((a135 == 102) && ((a176 == 102) && (((a184 == 4) && (cf && (input == 67))) && (a57 == 2)))) && ((a84 == 102) && (a135 == 102)))) {
    	cf = false;
    	a180 = 101;
    	a105 = 14; 
    	System.out.println("S");
    } 
    if(((((a85 == 11) && (a184 == 4)) && (a163 == 102)) && (((a155 == 7) && ((a142 == 9) && (cf && (input == 66)))) && (a142 == 9)))) {
    	cf = false;
    	a131 = 7;
    	a85 = 12;
    	a57 = 3;
    	a122 = 103;
    	a175 = 4;
    	a142 = 10;
    	a15 = 103;
    	a75 = 103;
    	a176 = 103;
    	a103 = 103;
    	a145 = 7;
    	a161 = 6;
    	a138 = 105;
    	a135 = 103;
    	a37 = 4;
    	a184 = 5;
    	a53 = 103;
    	a163 = 103;
    	a171 = 103;
    	a47 = 9;
    	a155 = 8;
    	a84 = 103;
    	a104 = 15; 
    	System.out.println("Z");
    } 
    if(((((a103 == 102) && (cf && (input == 101))) && (a57 == 2)) && ((a122 == 102) && ((a53 == 102) && ((a37 == 3) && (a131 == 6)))))) {
    	cf = false;
    	a6 = 104;
    	a138 = 104;
    	a17 = 8; 
    	System.out.println("V");
    } 
    if(((((a131 == 6) && (a53 == 102)) && (a161 == 5)) && ((a53 == 102) && ((a176 == 102) && ((cf && (input == 68)) && (a171 == 102)))))) {
    	cf = false;
    	a105 = 13;
    	a128 = 8; 
    	System.out.println("W");
    } 
    if(((((a131 == 6) && ((a171 == 102) && (a131 == 6))) && (a103 == 102)) && ((a176 == 102) && ((a53 == 102) && ((input == 66) && cf))))) {
    	cf = false;
    	a53 = 103;
    	a171 = 103;
    	a85 = 12;
    	a131 = 7;
    	a142 = 10;
    	a15 = 103;
    	a122 = 103;
    	a145 = 7;
    	a57 = 3;
    	a37 = 4;
    	a138 = 105;
    	a84 = 103;
    	a176 = 103;
    	a104 = 16; 
    	System.out.println("U");
    } 
}
public  void calculateOutputm8(int input) {
    if((((a57 == 2) && ((a171 == 102) && (a75 == 102))) && (((((a44 == 11) && cf) && (a176 == 102)) && (a122 == 102)) && (a184 == 4)))) {
    	calculateOutputm61(input);
    } 
}
public  void calculateOutputm81(int input) {
    if((((a184 == 4) && ((a176 == 102) && ((a15 == 102) && ((a171 == 102) && (a75 == 102))))) && (((input == 66) && cf) && (a85 == 11)))) {
    	cf = false;
    	a15 = 103;
    	a176 = 103;
    	a171 = 103;
    	a82 = 102;
    	a103 = 103;
    	a85 = 12;
    	a161 = 6;
    	a184 = 5;
    	a138 = 103;
    	a131 = 7;
    	a163 = 103;
    	a175 = 4;
    	a53 = 103;
    	a135 = 103;
    	a7 = 11; 
    	System.out.println("V");
    } 
    if((((a184 == 4) && ((a37 == 3) && (((a131 == 6) && (a131 == 6)) && (a103 == 102)))) && ((cf && (input == 68)) && (a171 == 102)))) {
    	cf = false;
    	a184 = 5;
    	a145 = 2;
    	a15 = 103;
    	a175 = 4;
    	a53 = 103;
    	a131 = 7;
    	a47 = 9;
    	a155 = 8;
    	a171 = 103;
    	a135 = 103;
    	a75 = 103;
    	a37 = 4;
    	a103 = 103;
    	a138 = 105;
    	a85 = 12;
    	a163 = 103;
    	a161 = 6;
    	a176 = 103;
    	a122 = 103;
    	a142 = 10;
    	a3 = 10; 
    	System.out.println("V");
    } 
}
public  void calculateOutputm13(int input) {
    if((((a142 == 9) && ((a47 == 8) && (((a52 == 105) && cf) && (a175 == 3)))) && (((a75 == 102) && (a142 == 9)) && (a53 == 102)))) {
    	calculateOutputm81(input);
    } 
}
public  void calculateOutputm85(int input) {
    if((((a122 == 103) && ((a175 == 4) && ((a37 == 4) && ((a142 == 10) && (a163 == 103))))) && ((a184 == 5) && ((input == 66) && cf)))) {
    	cf = false;
    	a138 = 105;
    	a145 = 7;
    	a104 = 15; 
    	System.out.println("Z");
    } 
}
public  void calculateOutputm14(int input) {
    if((((((a103 == 103) && (a85 == 12)) && (a84 == 103)) && (a37 == 4)) && (((a15 == 103) && (cf && (a110 == 5))) && (a75 == 103)))) {
    	calculateOutputm85(input);
    } 
}
public  void calculateOutputm100(int input) {
    if((((a131 == 6) && ((input == 101) && cf)) && (((((a84 == 102) && (a75 == 102)) && (a84 == 102)) && (a75 == 102)) && (a142 == 9)))) {
    	cf = false;
    	a138 = 102;
    	a57 = 3;
    	a184 = 4;
    	a84 = 103;
    	a53 = 102;
    	a52 = 105;
    	a103 = 102;
    	a85 = 11;
    	a135 = 102;
    	a105 = 16; 
    	System.out.println("V");
    } 
    if((((a57 == 2) && ((a142 == 9) && ((a47 == 8) && (cf && (input == 68))))) && (((a75 == 102) && (a142 == 9)) && (a75 == 102)))) {
    	cf = false;
    	a84 = 103;
    	a145 = 2;
    	a155 = 8;
    	a176 = 103;
    	a75 = 103;
    	a47 = 9;
    	a163 = 103;
    	a57 = 3;
    	a131 = 7;
    	a37 = 4;
    	a138 = 105;
    	a175 = 2;
    	a122 = 103;
    	a15 = 103;
    	a171 = 101;
    	a142 = 10;
    	a161 = 4;
    	a3 = 17; 
    	System.out.println("T");
    } 
    if((((a171 == 102) && ((input == 66) && cf)) && (((((a75 == 102) && (a75 == 102)) && (a161 == 5)) && (a176 == 102)) && (a161 == 5)))) {
    	cf = false;
    	a135 = 102;
    	a103 = 102;
    	a171 = 103;
    	a85 = 11;
    	a53 = 102;
    	a105 = 10;
    	a184 = 4;
    	a138 = 102;
    	a161 = 4;
    	a112 = 9; 
    	System.out.println("U");
    } 
}
public  void calculateOutputm19(int input) {
    if((((((a199 == 16) && cf) && (a155 == 7)) && (a155 == 7)) && (((a57 == 2) && ((a37 == 3) && (a175 == 3))) && (a131 == 6)))) {
    	calculateOutputm100(input);
    } 
}
public  void calculateOutputm105(int input) {
    if(((((a175 == 3) && (a176 == 102)) && (a47 == 8)) && (((a103 == 102) && ((cf && (input == 67)) && (a122 == 102))) && (a15 == 102)))) {
    	cf = false;
    	a176 = 103;
    	a138 = 105;
    	a142 = 10;
    	a37 = 4;
    	a122 = 103;
    	a84 = 103;
    	a131 = 7;
    	a57 = 3;
    	a171 = 103;
    	a145 = 7;
    	a184 = 5;
    	a15 = 103;
    	a104 = 12; 
    	System.out.println("T");
    } 
    if((((a184 == 4) && ((a131 == 6) && (cf && (input == 101)))) && (((a161 == 5) && ((a163 == 102) && (a15 == 102))) && (a142 == 9)))) {
    	cf = false;
    	a85 = 11;
    	a57 = 3;
    	a84 = 103;
    	a52 = 105;
    	a135 = 102;
    	a138 = 102;
    	a105 = 16; 
    	System.out.println("V");
    } 
    if((((a175 == 3) && ((a57 == 2) && ((((input == 66) && cf) && (a131 == 6)) && (a75 == 102)))) && ((a163 == 102) && (a37 == 3)))) {
    	cf = false;
    	a52 = 105;
    	a57 = 3;
    	a138 = 102;
    	a135 = 102;
    	a84 = 103;
    	a85 = 11;
    	a105 = 16; 
    	System.out.println("V");
    } 
    if(((a15 == 102) && ((a155 == 7) && ((a53 == 102) && ((((a161 == 5) && (cf && (input == 68))) && (a75 == 102)) && (a171 == 102)))))) {
    	cf = false;
    	a52 = 105;
    	a84 = 103;
    	a57 = 3;
    	a135 = 102;
    	a138 = 102;
    	a85 = 11;
    	a105 = 16; 
    	System.out.println("V");
    } 
    if((((a75 == 102) && ((cf && (input == 66)) && (a57 == 2))) && ((a103 == 102) && ((a175 == 3) && ((a84 == 102) && (a161 == 5)))))) {
    	cf = false;
    	a15 = 103;
    	a176 = 103;
    	a155 = 8;
    	a175 = 4;
    	a163 = 103;
    	a103 = 103;
    	a138 = 103;
    	a57 = 3;
    	a82 = 103;
    	a171 = 103;
    	a131 = 7;
    	a161 = 6;
    	a53 = 103;
    	a87 = 105;
    	a47 = 9; 
    	System.out.println("T");
    } 
}
public  void calculateOutputm21(int input) {
    if((((a176 == 102) && ((a163 == 102) && (a57 == 2))) && ((a15 == 102) && ((a47 == 8) && ((a175 == 3) && ((a190 == 104) && cf)))))) {
    	calculateOutputm105(input);
    } 
}
public  void calculateOutputm121(int input) {
    if((((((a155 == 8) && (a85 == 12)) && (a155 == 8)) && (a47 == 9)) && ((a175 == 4) && (((input == 66) && cf) && (a135 == 103))))) {
    	cf = false;
    	a131 = 6;
    	a142 = 9;
    	a175 = 3;
    	a138 = 104;
    	a57 = 2;
    	a75 = 102;
    	a6 = 101;
    	a184 = 3;
    	a37 = 3;
    	a47 = 8;
    	a84 = 102;
    	a85 = 10;
    	a163 = 102;
    	a171 = 102;
    	a15 = 102;
    	a155 = 7;
    	a122 = 102;
    	a161 = 5;
    	a176 = 102;
    	a199 = 16; 
    	System.out.println("W");
    } 
    if(((((a85 == 12) && ((a184 == 5) && ((cf && (input == 68)) && (a122 == 103)))) && (a184 == 5)) && ((a175 == 4) && (a122 == 103)))) {
    	cf = false;
    	a82 = 101;
    	a138 = 103;
    	a110 = 5; 
    	System.out.println("T");
    } 
}
public  void calculateOutputm24(int input) {
    if((((a155 == 8) && ((a176 == 103) && (a15 == 103))) && ((((cf && (a16 == 103)) && (a57 == 3)) && (a131 == 7)) && (a131 == 7)))) {
    	calculateOutputm121(input);
    } 
}
public  void calculateOutputm122(int input) {
    if((((input == 66) && cf) && (((a103 == 103) && ((a142 == 10) && (((a15 == 103) && (a161 == 6)) && (a184 == 5)))) && (a84 == 103)))) {
    	cf = false;
    	a85 = 11;
    	a5 = 101;
    	a37 = 2;
    	a57 = 1;
    	a171 = 101;
    	a176 = 101;
    	a135 = 101;
    	a138 = 101;
    	a161 = 4;
    	a47 = 7;
    	a75 = 101;
    	a163 = 101;
    	a84 = 101;
    	a103 = 101;
    	a6 = 101;
    	a155 = 6;
    	a175 = 2;
    	a184 = 3;
    	a131 = 5;
    	a122 = 101;
    	a15 = 101;
    	a53 = 101;
    	a142 = 8; 
    	System.out.println("U");
    } 
    if((((a37 == 4) && (((a37 == 4) && (a103 == 103)) && (a103 == 103))) && ((a84 == 103) && (((input == 101) && cf) && (a84 == 103))))) {
    	cf = false;
    	a6 = 104;
    	a184 = 4;
    	a175 = 3;
    	a163 = 102;
    	a176 = 102;
    	a135 = 102;
    	a161 = 5;
    	a138 = 104;
    	a155 = 7;
    	a122 = 102;
    	a131 = 6;
    	a103 = 102;
    	a57 = 2;
    	a142 = 9;
    	a53 = 102;
    	a37 = 3;
    	a17 = 12; 
    	System.out.println("Z");
    } 
    if((((cf && (input == 67)) && (a175 == 4)) && (((a122 == 103) && (((a84 == 103) && (a171 == 103)) && (a57 == 3))) && (a163 == 103)))) {
    	cf = false;
    	a6 = 102;
    	a75 = 101;
    	a53 = 101;
    	a122 = 101;
    	a135 = 101;
    	a138 = 101;
    	a131 = 5;
    	a85 = 10;
    	a142 = 8;
    	a84 = 101;
    	a176 = 101;
    	a47 = 7;
    	a171 = 101;
    	a184 = 3;
    	a44 = 12; 
    	System.out.println("V");
    } 
    if(((((a135 == 103) && ((a75 == 103) && (((input == 66) && cf) && (a84 == 103)))) && (a85 == 12)) && ((a85 == 12) && (a163 == 103)))) {
    	cf = false;
    	a145 = 6;
    	a7 = 14; 
    	System.out.println("V");
    } 
    if((((a175 == 4) && ((a155 == 8) && ((((input == 68) && cf) && (a184 == 5)) && (a131 == 7)))) && ((a122 == 103) && (a15 == 103)))) {
    	cf = false;
    	a138 = 103;
    	a82 = 105;
    	a119 = 104; 
    	System.out.println("U");
    } 
}
public  void calculateOutputm128(int input) {
    if(((((a53 == 103) && (a57 == 3)) && (a142 == 10)) && ((((a122 == 103) && (cf && (input == 66))) && (a122 == 103)) && (a37 == 4)))) {
    	cf = false;
    	a57 = 1;
    	a47 = 7;
    	a37 = 2;
    	a131 = 5;
    	a135 = 101;
    	a163 = 102;
    	a6 = 101;
    	a138 = 101;
    	a15 = 101;
    	a84 = 101;
    	a122 = 101;
    	a53 = 101;
    	a75 = 101;
    	a142 = 8;
    	a103 = 101;
    	a5 = 102;
    	a176 = 101;
    	a155 = 6; 
    	System.out.println("S");
    } 
    if((((a103 == 103) && (((a155 == 8) && ((input == 67) && cf)) && (a135 == 103))) && (((a75 == 103) && (a163 == 103)) && (a176 == 103)))) {
    	cf = false;
    	a6 = 104;
    	a53 = 101;
    	a135 = 101;
    	a163 = 101;
    	a138 = 101;
    	a47 = 7;
    	a57 = 1;
    	a183 = 105;
    	a122 = 101;
    	a176 = 101;
    	a103 = 101;
    	a155 = 6; 
    	System.out.println("U");
    } 
    if((((a75 == 103) && ((a163 == 103) && (((input == 68) && cf) && (a84 == 103)))) && ((a135 == 103) && ((a37 == 4) && (a131 == 7))))) {
    	cf = false;
    	a37 = 3;
    	a53 = 102;
    	a75 = 102;
    	a84 = 102;
    	a138 = 102;
    	a122 = 102;
    	a57 = 2;
    	a47 = 8;
    	a15 = 102;
    	a163 = 102;
    	a105 = 13;
    	a161 = 5;
    	a103 = 102;
    	a176 = 102;
    	a128 = 10; 
    	System.out.println("T");
    } 
    if((((a57 == 3) && ((a176 == 103) && (a47 == 9))) && ((a155 == 8) && (((cf && (input == 101)) && (a176 == 103)) && (a84 == 103))))) {
    	cf = false;
    	a122 = 101;
    	a53 = 101;
    	a5 = 103;
    	a176 = 101;
    	a103 = 101;
    	a47 = 7;
    	a142 = 8;
    	a135 = 101;
    	a138 = 101;
    	a131 = 5;
    	a6 = 101;
    	a155 = 6; 
    	System.out.println("U");
    } 
    if((((a103 == 103) && ((((cf && (input == 66)) && (a142 == 10)) && (a57 == 3)) && (a15 == 103))) && ((a135 == 103) && (a84 == 103)))) {
    	cf = false;
    	a85 = 12;
    	a184 = 5;
    	a171 = 103;
    	a145 = 3;
    	a161 = 6;
    	a53 = 102;
    	a175 = 4;
    	a36 = 11; 
    	System.out.println("Y");
    } 
}
public  void calculateOutputm25(int input) {
    if((((a85 == 12) && ((a85 == 12) && ((a75 == 103) && (a37 == 4)))) && (((a103 == 103) && (cf && (a3 == 10))) && (a142 == 10)))) {
    	calculateOutputm122(input);
    } 
    if(((((a37 == 4) && (a37 == 4)) && (a53 == 103)) && (((a176 == 103) && ((a75 == 103) && ((a3 == 17) && cf))) && (a155 == 8)))) {
    	calculateOutputm128(input);
    } 
}
public  void calculateOutputm130(int input) {
    if((((((a155 == 8) && ((a171 == 103) && (a122 == 103))) && (a84 == 103)) && (a142 == 10)) && (((input == 66) && cf) && (a15 == 103)))) {
    	cf = false;
    	a176 = 102;
    	a47 = 8;
    	a155 = 7;
    	a131 = 6;
    	a138 = 102;
    	a105 = 10;
    	a122 = 102;
    	a142 = 9;
    	a15 = 102;
    	a103 = 102;
    	a85 = 11;
    	a84 = 102;
    	a163 = 102;
    	a37 = 3;
    	a184 = 4;
    	a112 = 10; 
    	System.out.println("U");
    } 
    if(((((((a142 == 10) && ((input == 101) && cf)) && (a103 == 103)) && (a155 == 8)) && (a84 == 103)) && ((a142 == 10) && (a37 == 4)))) {
    	cf = false;
    	a155 = 7;
    	a176 = 102;
    	a75 = 102;
    	a105 = 11;
    	a184 = 4;
    	a84 = 102;
    	a85 = 11;
    	a122 = 102;
    	a57 = 2;
    	a103 = 102;
    	a138 = 102;
    	a47 = 8;
    	a15 = 102;
    	a142 = 9;
    	a161 = 5;
    	a44 = 12; 
    	System.out.println("U");
    } 
    if((((a75 == 103) && (((cf && (input == 68)) && (a122 == 103)) && (a163 == 103))) && ((a171 == 103) && ((a122 == 103) && (a15 == 103))))) {
    	cf = false;
    	 
    	System.out.println("Y");
    } 
    if((((a161 == 6) && ((a57 == 3) && ((cf && (input == 67)) && (a176 == 103)))) && ((a122 == 103) && ((a75 == 103) && (a135 == 103))))) {
    	cf = false;
    	a47 = 7;
    	a135 = 101;
    	a131 = 5;
    	a15 = 101;
    	a175 = 2;
    	a53 = 101;
    	a155 = 6;
    	a138 = 101;
    	a118 = 103;
    	a122 = 101;
    	a37 = 2;
    	a176 = 101;
    	a6 = 105;
    	a85 = 10; 
    	System.out.println("S");
    } 
    if(((((a57 == 3) && ((a85 == 12) && (a135 == 103))) && (a37 == 4)) && ((a47 == 9) && ((a122 == 103) && ((input == 66) && cf))))) {
    	cf = false;
    	a145 = 6;
    	a7 = 11; 
    	System.out.println("U");
    } 
}
public  void calculateOutputm26(int input) {
    if((((a75 == 103) && ((a85 == 12) && (cf && (a36 == 11)))) && ((((a75 == 103) && (a103 == 103)) && (a171 == 103)) && (a142 == 10)))) {
    	calculateOutputm130(input);
    } 
}
public  void calculateOutputm144(int input) {
    if((((((a142 == 10) && (a184 == 5)) && (a142 == 10)) && (a85 == 12)) && ((a176 == 103) && ((a103 == 103) && ((input == 67) && cf))))) {
    	cf = false;
    	a82 = 104;
    	a138 = 103;
    	a5 = 102; 
    	System.out.println("U");
    } 
    if(((((cf && (input == 66)) && (a135 == 103)) && (a175 == 4)) && ((a84 == 103) && (((a171 == 103) && (a163 == 103)) && (a53 == 103))))) {
    	cf = false;
    	a75 = 102;
    	a85 = 11;
    	a15 = 102;
    	a37 = 3;
    	a53 = 102;
    	a155 = 7;
    	a122 = 102;
    	a176 = 102;
    	a142 = 9;
    	a103 = 102;
    	a135 = 102;
    	a47 = 8;
    	a161 = 5;
    	a131 = 6;
    	a105 = 11;
    	a175 = 3;
    	a84 = 102;
    	a184 = 4;
    	a163 = 102;
    	a138 = 102;
    	a57 = 2;
    	a171 = 102;
    	a44 = 11; 
    	System.out.println("S");
    } 
    if(((a57 == 3) && ((a155 == 8) && ((a171 == 103) && ((a15 == 103) && ((a37 == 4) && ((cf && (input == 66)) && (a131 == 7)))))))) {
    	cf = false;
    	a155 = 7;
    	a47 = 8;
    	a53 = 102;
    	a135 = 102;
    	a85 = 11;
    	a37 = 3;
    	a161 = 5;
    	a103 = 102;
    	a75 = 102;
    	a122 = 102;
    	a52 = 102;
    	a163 = 102;
    	a138 = 102;
    	a105 = 16; 
    	System.out.println("Z");
    } 
    if(((((a142 == 10) && (a37 == 4)) && (a131 == 7)) && ((((cf && (input == 68)) && (a15 == 103)) && (a85 == 12)) && (a47 == 9)))) {
    	cf = false;
    	a57 = 2;
    	a53 = 102;
    	a131 = 6;
    	a122 = 102;
    	a75 = 102;
    	a105 = 13;
    	a175 = 3;
    	a171 = 102;
    	a47 = 8;
    	a103 = 102;
    	a155 = 7;
    	a15 = 102;
    	a161 = 5;
    	a138 = 102;
    	a128 = 7; 
    	System.out.println("Y");
    } 
    if(((((a131 == 7) && ((((input == 101) && cf) && (a47 == 9)) && (a75 == 103))) && (a142 == 10)) && ((a184 == 5) && (a103 == 103)))) {
    	cf = false;
    	a77 = 105;
    	a145 = 8; 
    	System.out.println("S");
    } 
}
public  void calculateOutputm30(int input) {
    if((((((a176 == 103) && (a84 == 103)) && (a135 == 103)) && (a176 == 103)) && (((cf && (a104 == 15)) && (a53 == 103)) && (a163 == 103)))) {
    	calculateOutputm144(input);
    } 
}
public  void calculateOutputm146(int input) {
    if((((((a155 == 8) && (cf && (input == 67))) && (a142 == 10)) && (a53 == 103)) && ((a131 == 7) && ((a47 == 9) && (a131 == 7))))) {
    	cf = false;
    	a82 = 101;
    	a138 = 103;
    	a110 = 4; 
    	System.out.println("V");
    } 
    if(((((a131 == 7) && (((a37 == 4) && (cf && (input == 66))) && (a175 == 4))) && (a47 == 9)) && ((a85 == 12) && (a163 == 103)))) {
    	cf = false;
    	a161 = 5;
    	a171 = 102;
    	a175 = 3;
    	a138 = 102;
    	a155 = 7;
    	a131 = 6;
    	a176 = 102;
    	a103 = 102;
    	a47 = 8;
    	a135 = 102;
    	a163 = 102;
    	a184 = 4;
    	a75 = 102;
    	a52 = 105;
    	a122 = 102;
    	a15 = 102;
    	a53 = 102;
    	a37 = 3;
    	a85 = 11;
    	a142 = 9;
    	a105 = 16; 
    	System.out.println("V");
    } 
    if((((a47 == 9) && ((a122 == 103) && (a175 == 4))) && ((a103 == 103) && ((a155 == 8) && ((a142 == 10) && ((input == 101) && cf)))))) {
    	cf = false;
    	a176 = 102;
    	a163 = 102;
    	a85 = 11;
    	a15 = 102;
    	a52 = 105;
    	a47 = 8;
    	a142 = 9;
    	a131 = 6;
    	a161 = 5;
    	a138 = 102;
    	a135 = 102;
    	a103 = 102;
    	a175 = 3;
    	a171 = 102;
    	a75 = 102;
    	a53 = 102;
    	a184 = 4;
    	a155 = 7;
    	a122 = 102;
    	a37 = 3;
    	a105 = 16; 
    	System.out.println("V");
    } 
    if((((a53 == 103) && ((a184 == 5) && (a142 == 10))) && ((((cf && (input == 66)) && (a122 == 103)) && (a163 == 103)) && (a57 == 3)))) {
    	cf = false;
    	a145 = 6;
    	a7 = 8; 
    	System.out.println("U");
    } 
    if((((a184 == 5) && ((a131 == 7) && ((a161 == 6) && (a85 == 12)))) && ((a155 == 8) && (((input == 68) && cf) && (a53 == 103))))) {
    	cf = false;
    	a138 = 102;
    	a171 = 102;
    	a52 = 105;
    	a176 = 102;
    	a155 = 7;
    	a175 = 3;
    	a131 = 6;
    	a122 = 102;
    	a161 = 5;
    	a47 = 8;
    	a75 = 102;
    	a53 = 102;
    	a37 = 3;
    	a163 = 102;
    	a184 = 4;
    	a103 = 102;
    	a135 = 102;
    	a142 = 9;
    	a85 = 11;
    	a15 = 102;
    	a105 = 16; 
    	System.out.println("V");
    } 
}
public  void calculateOutputm31(int input) {
    if((((a163 == 103) && ((a155 == 8) && (a85 == 12))) && ((a84 == 103) && ((((a77 == 103) && cf) && (a163 == 103)) && (a163 == 103))))) {
    	calculateOutputm146(input);
    } 
}



public  void calculateOutput(int input) {
 	cf = true;
    if((((a53 == 101) && ((a135 == 101) && (a176 == 101))) && ((a122 == 101) && (((cf && (a138 == 101)) && (a53 == 101)) && (a47 == 7))))) {
    	if((((a142 == 8) && (((a131 == 5) && (a53 == 101)) && (a155 == 6))) && (((a176 == 101) && (cf && (a6 == 101))) && (a131 == 5)))) {
    		calculateOutputm1(input);
    	} 
    	if((((a57 == 1) && ((a161 == 4) && ((cf && (a6 == 104)) && (a155 == 6)))) && ((a163 == 101) && ((a171 == 101) && (a171 == 101))))) {
    		calculateOutputm4(input);
    	} 
    	if(((((((a155 == 6) && (a122 == 101)) && (a131 == 5)) && (a175 == 2)) && (a15 == 101)) && (((a6 == 105) && cf) && (a122 == 101)))) {
    		calculateOutputm5(input);
    	} 
    } 
    if(((((a47 == 8) && (((cf && (a138 == 102)) && (a103 == 102)) && (a53 == 102))) && (a47 == 8)) && ((a122 == 102) && (a53 == 102)))) {
    	if((((a37 == 3) && ((cf && (a105 == 9)) && (a142 == 9))) && (((a163 == 102) && ((a85 == 11) && (a176 == 102))) && (a85 == 11)))) {
    		calculateOutputm6(input);
    	} 
    	if((((((a105 == 10) && cf) && (a84 == 102)) && (a176 == 102)) && ((a142 == 9) && ((a184 == 4) && ((a163 == 102) && (a47 == 8)))))) {
    		calculateOutputm7(input);
    	} 
    	if(((((a75 == 102) && ((a57 == 2) && (a142 == 9))) && (a47 == 8)) && ((((a105 == 11) && cf) && (a184 == 4)) && (a15 == 102)))) {
    		calculateOutputm8(input);
    	} 
    	if((((a161 == 5) && (((a163 == 102) && (a135 == 102)) && (a37 == 3))) && (((a53 == 102) && ((a105 == 16) && cf)) && (a155 == 7)))) {
    		calculateOutputm13(input);
    	} 
    } 
    if((((a176 == 103) && (a161 == 6)) && ((((a53 == 103) && ((a131 == 7) && (cf && (a138 == 103)))) && (a135 == 103)) && (a103 == 103)))) {
    	if(((cf && (a82 == 101)) && ((a84 == 103) && ((a142 == 10) && ((a161 == 6) && ((a161 == 6) && ((a184 == 5) && (a122 == 103)))))))) {
    		calculateOutputm14(input);
    	} 
    } 
    if((((((a161 == 5) && ((cf && (a138 == 104)) && (a37 == 3))) && (a131 == 6)) && (a37 == 3)) && ((a122 == 102) && (a175 == 3)))) {
    	if((((a15 == 102) && (((cf && (a6 == 101)) && (a75 == 102)) && (a131 == 6))) && (((a84 == 102) && (a163 == 102)) && (a75 == 102)))) {
    		calculateOutputm19(input);
    	} 
    	if((((a142 == 9) && ((a6 == 103) && cf)) && (((a176 == 102) && ((a53 == 102) && ((a75 == 102) && (a161 == 5)))) && (a175 == 3)))) {
    		calculateOutputm21(input);
    	} 
    } 
    if((((((a131 == 7) && ((a37 == 4) && (a57 == 3))) && (a84 == 103)) && (a37 == 4)) && ((a37 == 4) && (cf && (a138 == 105))))) {
    	if((((a122 == 103) && (((a135 == 103) && ((a145 == 1) && cf)) && (a142 == 10))) && ((a175 == 4) && ((a84 == 103) && (a163 == 103))))) {
    		calculateOutputm24(input);
    	} 
    	if(((((a15 == 103) && ((a131 == 7) && (cf && (a145 == 2)))) && (a135 == 103)) && (((a131 == 7) && (a75 == 103)) && (a53 == 103)))) {
    		calculateOutputm25(input);
    	} 
    	if((((a175 == 4) && ((a184 == 5) && (cf && (a145 == 3)))) && (((a131 == 7) && ((a57 == 3) && (a85 == 12))) && (a75 == 103)))) {
    		calculateOutputm26(input);
    	} 
    	if((((((a142 == 10) && (a142 == 10)) && (a142 == 10)) && (a171 == 103)) && ((a142 == 10) && ((a122 == 103) && (cf && (a145 == 7)))))) {
    		calculateOutputm30(input);
    	} 
    	if(((((a53 == 103) && ((a145 == 8) && cf)) && (a176 == 103)) && ((a57 == 3) && (((a47 == 9) && (a155 == 8)) && (a161 == 6))))) {
    		calculateOutputm31(input);
    	} 
    } 

    errorCheck();
    if(cf)
    	throw new IllegalArgumentException("Current state has no transition for this input!");
}


public static void main(int[] args) throws Exception {
	// init system and input reader
	Problem3 eca = new Problem3();

	// main i/o-loop
	while(true) {
		//read input
		int input = Integer.valueOf(stdin.readLine());

		 if((input == 66) && (input == 101) && (input == 67) && (input == 66) && (input == 68))
			throw new IllegalArgumentException("Current state has no transition for this input!");
		try {
			//operate eca engine output = 
			eca.calculateOutput(input);
		} catch(IllegalArgumentException e) {
			System.err.println("Invalid input: " + e.getMessage());
		}
	}
}
}