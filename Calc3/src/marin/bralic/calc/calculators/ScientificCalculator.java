package marin.bralic.calc.calculators;

import java.util.LinkedList;
import java.util.List;
import android.view.View;
import android.widget.Button;
import marin.bralic.calc.Display;
import marin.bralic.calc.Display.Mod;
import marin.bralic.calc.IDisplay;
import marin.bralic.calc.MMath;
import marin.bralic.calc.MainActivity;
import marin.bralic.calc.R;

public class ScientificCalculator implements ICalculator{
	private MainActivity mainActivity;
	private IDisplay display;
	private Button[] buttons;

	private List<String> history;
	private int history_pointer, abc_select;
	private String result;
	private double ans, a, b, c;
	private Mod mod;
	private boolean shift, const_;
	
	
	public ScientificCalculator(MainActivity main){
		mainActivity=main;		
		initialize();	
	}
	
	private void initialize(){
		display=(Display)mainActivity.findViewById(R.id.display);
		display.setScfMod(true);
		display.setRad(MainActivity.rad);
		if(MainActivity.round>=0 && MainActivity.round<=10) display.setRound(MainActivity.round);
		
		buttons=new Button[29];
		buttons[0]=(Button)mainActivity.findViewById(R.id.button_c_P);
		buttons[1]=(Button)mainActivity.findViewById(R.id.button_ans_sin);
		buttons[2]=(Button)mainActivity.findViewById(R.id.button_pi_cos_arg);
		buttons[3]=(Button)mainActivity.findViewById(R.id.button_e_tan_cnj);
		buttons[4]=(Button)mainActivity.findViewById(R.id.button_del_B);
		buttons[5]=(Button)mainActivity.findViewById(R.id.button_pn_asin);
		buttons[6]=(Button)mainActivity.findViewById(R.id.button_nsqrt_acos_abi);
		buttons[7]=(Button)mainActivity.findViewById(R.id.button_p1_atan_toAfi);
		buttons[8]=(Button)mainActivity.findViewById(R.id.button_ln_log_i);
		buttons[9]=(Button)mainActivity.findViewById(R.id.button_sqrt_n);
		buttons[10]=(Button)mainActivity.findViewById(R.id.button_open);
		buttons[11]=(Button)mainActivity.findViewById(R.id.button_close);
		buttons[12]=(Button)mainActivity.findViewById(R.id.button_p2_abs);
		
		buttons[13]=(Button)mainActivity.findViewById(R.id.button7);
		buttons[14]=(Button)mainActivity.findViewById(R.id.button8);
		buttons[15]=(Button)mainActivity.findViewById(R.id.button9_deg_rad);
		buttons[16]=(Button)mainActivity.findViewById(R.id.button_plus_settings);
		buttons[17]=(Button)mainActivity.findViewById(R.id.button4);		
		buttons[18]=(Button)mainActivity.findViewById(R.id.button5);
		buttons[19]=(Button)mainActivity.findViewById(R.id.button6);
		buttons[20]=(Button)mainActivity.findViewById(R.id.button_minus_qveq);		
		buttons[21]=(Button)mainActivity.findViewById(R.id.button1_up);
		buttons[22]=(Button)mainActivity.findViewById(R.id.button2);
		buttons[23]=(Button)mainActivity.findViewById(R.id.button3);		
		buttons[24]=(Button)mainActivity.findViewById(R.id.button_mull_sum);
		buttons[25]=(Button)mainActivity.findViewById(R.id.button0_down);
		buttons[26]=(Button)mainActivity.findViewById(R.id.button_point_const);		
		buttons[27]=(Button)mainActivity.findViewById(R.id.button_eq_mem);
		buttons[28]=(Button)mainActivity.findViewById(R.id.button_div_cmplx);	
		
		history=new LinkedList<String>();
		history_pointer=-1;
		ans=0;
		mod=Mod.N;
		shift=false;		
	}
		
	
	private void setButtonsToN(){
		if(shift){
			buttons[0].setText("Prog");
			buttons[1].setText("sin");
			buttons[2].setText("cos");
			buttons[3].setText("tan");
			buttons[4].setText("Bsc");
			buttons[5].setText("asin");
			buttons[6].setText("acos");
			buttons[7].setText("atan");
			buttons[8].setText("log");
			buttons[9].setText("n!");
			buttons[10].setText("(");
			buttons[11].setText(")");
			buttons[12].setText("|x|");
			buttons[13].setText("7");
			buttons[14].setText("8");
			buttons[15].setText("D/R");
			buttons[16].setText("Sett");
			buttons[17].setText("4");
			buttons[18].setText("5");
			buttons[19].setText("6");
			buttons[20].setText("(x+a)\u2072");
			buttons[21].setText("\u25B3");
			buttons[22].setText("2");
			buttons[23].setText("3");
			buttons[24].setText("x");
			buttons[25].setText("\u25BD");
			buttons[26].setText("const");
			buttons[27].setText("MC");
			buttons[28].setText("/");
		}else{
			buttons[0].setText("C");
			buttons[1].setText("ans");
			buttons[2].setText("\u03C0");
			buttons[3].setText("e\u207F");
			buttons[4].setText("Del");
			buttons[5].setText("x\u207F");
			buttons[6].setText("\u207F\u221Ax");
			buttons[7].setText("x^-1");
			buttons[8].setText("ln");
			buttons[9].setText("\u221Ax");
			buttons[10].setText("(");
			buttons[11].setText(")");
			buttons[12].setText("x\u2072");
			buttons[13].setText("7");
			buttons[14].setText("8");
			buttons[15].setText("9");
			buttons[16].setText("+");
			buttons[17].setText("4");
			buttons[18].setText("5");
			buttons[19].setText("6");
			buttons[20].setText("-");
			buttons[21].setText("1");
			buttons[22].setText("2");
			buttons[23].setText("3");
			buttons[24].setText("*");
			buttons[25].setText("0");
			buttons[26].setText(".");
			buttons[27].setText("=");
			buttons[28].setText("/");
		}
	}
	
	private void setButtonsToEqu(){
		if(shift){
			buttons[0].setText("Prog");
			buttons[1].setText("sin");
			buttons[2].setText("cos");
			buttons[3].setText("tan");
			buttons[4].setText("Bsc");
			buttons[5].setText("asin");
			buttons[6].setText("acos");
			buttons[7].setText("atan");
			buttons[8].setText("log");
			buttons[9].setText("n!");
			buttons[10].setText("\u21D0");
			buttons[11].setText("\u21D2");
			buttons[12].setText("|x|");
			buttons[13].setText("7");
			buttons[14].setText("8");
			buttons[15].setText("D/R");
			buttons[16].setText("Sett");
			buttons[17].setText("4");
			buttons[18].setText("5");
			buttons[19].setText("6");
			buttons[20].setText("Norm");
			buttons[21].setText("\u25B3");
			buttons[22].setText("2");
			buttons[23].setText("3");
			buttons[24].setText("x");
			buttons[25].setText("\u25BD");
			buttons[26].setText("const");
			buttons[27].setText("MC");
			buttons[28].setText("/");
		}else{
			buttons[0].setText("C");
			buttons[1].setText("ans");
			buttons[2].setText("\u03C0");
			buttons[3].setText("e\u207F");
			buttons[4].setText("Del");
			buttons[5].setText("x\u207F");
			buttons[6].setText("\u207F\u221Ax");
			buttons[7].setText("x^-1");
			buttons[8].setText("ln");
			buttons[9].setText("\u221Ax");
			buttons[10].setText("(");
			buttons[11].setText(")");
			buttons[12].setText("x\u2072");
			buttons[13].setText("7");
			buttons[14].setText("8");
			buttons[15].setText("9");
			buttons[16].setText("+");
			buttons[17].setText("4");
			buttons[18].setText("5");
			buttons[19].setText("6");
			buttons[20].setText("-");
			buttons[21].setText("1");
			buttons[22].setText("2");
			buttons[23].setText("3");
			buttons[24].setText("*");
			buttons[25].setText("0");
			buttons[26].setText(".");
			buttons[27].setText("=");
			buttons[28].setText("/");
		}
	}
		
	private void setButtonsToConst(){
		buttons[0].setText("c\u2080");
		buttons[1].setText("g");
		buttons[2].setText("mp");
		buttons[3].setText("mn");
		buttons[4].setText("me");
		buttons[5].setText("m\u03BC");
		buttons[6].setText("a\u2080");
		buttons[7].setText("h");
		buttons[8].setText("\u0127");
		buttons[9].setText("re");
		buttons[10].setText("R");
		buttons[11].setText("R\u221E");
		buttons[12].setText("G");
		buttons[13].setText("\u03BC\u2080");
		buttons[14].setText("\u03B5\u2080");
		buttons[15].setText("Vm");
		buttons[16].setText("F");
		buttons[17].setText("k");
		buttons[18].setText("u");
		buttons[19].setText("e");
		buttons[20].setText("\u03BCN");
		buttons[21].setText("\u03BCB");
		buttons[22].setText("\u03BBc");
		buttons[23].setText("\u03B1");
		buttons[24].setText("Na");
		buttons[25].setText("t\u2080");
		buttons[26].setText("Z\u2080");
		buttons[27].setText("G\u2080");
		buttons[28].setText("atm");
	}
	
	@Override
	public int press(View view) {
		int id=view.getId();			
		if(id!=R.id.button_s && id!=R.id.button_left && id!=R.id.button_right) display.setError(false);				
				 
		if(const_){
			if(id==R.id.button_c_P) display.addText(MMath.c);
			else if(id==R.id.button_ans_sin) display.addText(MMath.g);
			else if(id==R.id.button_pi_cos_arg) display.addText(MMath.mp);
			else if(id==R.id.button_e_tan_cnj) display.addText(MMath.mn);
			else if(id==R.id.button_del_B) display.addText(MMath.me);
			else if(id==R.id.button_pn_asin) display.addText(MMath.mmi);
			else if(id==R.id.button_nsqrt_acos_abi) display.addText(MMath.a0);
			else if(id==R.id.button_p1_atan_toAfi) display.addText(MMath.h);
			else if(id==R.id.button_ln_log_i) display.addText(MMath.h_);
			else if(id==R.id.button_sqrt_n) display.addText(MMath.re);
			else if(id==R.id.button_open) display.addText(MMath.R);
			else if(id==R.id.button_close) display.addText(MMath.R_inf);
			else if(id==R.id.button_p2_abs) display.addText(MMath.G);
			else if(id==R.id.button7) display.addText(MMath.mi0);
			else if(id==R.id.button8) display.addText(MMath.epsilon0);
			else if(id==R.id.button9_deg_rad) display.addText(MMath.Vm);
			else if(id==R.id.button_plus_settings) display.addText(MMath.F);
			else if(id==R.id.button4) display.addText(MMath.k);
			else if(id==R.id.button5) display.addText(MMath.u);
			else if(id==R.id.button6) display.addText(MMath.e);
			else if(id==R.id.button_minus_qveq) display.addText(MMath.mN);
			else if(id==R.id.button1_up) display.addText(MMath.mB);
			else if(id==R.id.button2) display.addText(MMath.lambda_c);
			else if(id==R.id.button3) display.addText(MMath.alfa);
			else if(id==R.id.button_mull_sum) display.addText(MMath.NA);
			else if(id==R.id.button0_down) display.addText(MMath.t0);
			else if(id==R.id.button_point_const) display.addText(MMath.Z0);
			else if(id==R.id.button_eq_mem) display.addText(MMath.G0);
			else if(id==R.id.button_div_cmplx) display.addText(MMath.atm);
			
			setButtonsToN();
			const_=false;
			return 0;
		}
		
		if(mod==Mod.N){
			if(id==R.id.button_s){
				display.setShift(shift=!shift);
				setButtonsToN();
				return 0;
			}			
			if(shift){			
				if(id==R.id.button_left) display.left();
				else if(id==R.id.button_right) display.right();
				else if(id==R.id.button_c_P) return 3;
				else if(id==R.id.button_ans_sin) display.addText("sin(");
				else if(id==R.id.button_pi_cos_arg) display.addText("cos(");
				else if(id==R.id.button_e_tan_cnj) display.addText("tan(");
				else if(id==R.id.button_del_B) return 4;
				else if(id==R.id.button_pn_asin) display.addText("asin(");
				else if(id==R.id.button_nsqrt_acos_abi) display.addText("acos(");
				else if(id==R.id.button_p1_atan_toAfi) display.addText("atan(");
				else if(id==R.id.button_ln_log_i) display.addText("log(");
				else if(id==R.id.button_sqrt_n) display.addText("fact(");
				else if(id==R.id.button_open) display.addText("(");
				else if(id==R.id.button_close && display.getBracket()>0) display.addText(")");
				else if(id==R.id.button_p2_abs) display.addText("abs(");
				else if(id==R.id.button7) display.addText("7");
				else if(id==R.id.button8) display.addText("8");
				else if(id==R.id.button9_deg_rad) display.setRad(MainActivity.rad=!MainActivity.rad);
				else if(id==R.id.button_plus_settings) return 5;
				else if(id==R.id.button4) display.addText("4");
				else if(id==R.id.button5) display.addText("5");
				else if(id==R.id.button6) display.addText("6");
				else if(id==R.id.button_minus_qveq){
					setButtonsToEqu();
					a=b=c=0;
					abc_select=1;
					display.setMod(mod=Mod.EQU);
				}
				else if(id==R.id.button1_up){					
					if(history_pointer<1) return 0;
					--history_pointer;
					 
					String text=history.get(history_pointer);					
					
					display.setText(text);
					if(history_pointer==0) display.setUpDown(0);
					else if(history_pointer<history.size()-1) display.setUpDown(2);
					
					return 0;
				}
				else if(id==R.id.button2) display.addText("2");
				else if(id==R.id.button3) display.addText("3");
				else if(id==R.id.button_mull_sum) display.addText("*");
				else if(id==R.id.button0_down){
                    if(history_pointer>=history.size()-1) return 0;
                    ++history_pointer;
					
					String text=history.get(history_pointer);					
					
					display.setText(text);
					if(history_pointer==history.size()-1) display.setUpDown(1);
					else display.setUpDown(2);
					
					return 0;
				}
				else if(id==R.id.button_point_const){
					const_=true;
					setButtonsToConst();
					display.setShift(shift=false);
					return 0;
				}
				else if(id==R.id.button_eq_mem){
					history_pointer=-1;
					history.clear();
					display.setUpDown(-1);
					display.clear();
				}
				else if(id==R.id.button_div_cmplx)display.addText("/");
			}else{
				if(id==R.id.button_left) display.left();
				else if(id==R.id.button_right) display.right();
				else if(id==R.id.button_c_P) display.clear();
				else if(id==R.id.button_ans_sin) display.addText("ans");
				else if(id==R.id.button_pi_cos_arg) display.addText("\u03C0");
				else if(id==R.id.button_e_tan_cnj) display.addText("e^(");
				else if(id==R.id.button_del_B) display.back();
				else if(id==R.id.button_pn_asin) display.addText("^(");
				else if(id==R.id.button_nsqrt_acos_abi) display.addText("^(1/");
				else if(id==R.id.button_p1_atan_toAfi) display.addText("^(-1)");
				else if(id==R.id.button_ln_log_i) display.addText("ln(");
				else if(id==R.id.button_sqrt_n) display.addText("\u221A(");
				else if(id==R.id.button_open) display.addText("(");
				else if(id==R.id.button_close && display.getBracket()>0) display.addText(")");
				else if(id==R.id.button_p2_abs) display.addText("^(2)");
				else if(id==R.id.button7) display.addText("7");
				else if(id==R.id.button8) display.addText("8");
				else if(id==R.id.button9_deg_rad) display.addText("9");
				else if(id==R.id.button_plus_settings) display.addText("+");
				else if(id==R.id.button4) display.addText("4");
				else if(id==R.id.button5) display.addText("5");
				else if(id==R.id.button6) display.addText("6");
				else if(id==R.id.button_minus_qveq) display.addText("-");
				else if(id==R.id.button1_up) display.addText("1");
				else if(id==R.id.button2) display.addText("2");
				else if(id==R.id.button3) display.addText("3");
				else if(id==R.id.button_mull_sum) display.addText("*");
				else if(id==R.id.button0_down) display.addText("0");
				else if(id==R.id.button_point_const) display.addText(".");
				else if(id==R.id.button_eq_mem) equals(display.getText());
				else if(id==R.id.button_div_cmplx) display.addText("/");
			}
			
			if(shift){
				display.setShift(shift=!shift);
				setButtonsToN();
			}	
		}else if(mod==Mod.EQU){
			if(id==R.id.button_s){
				display.setShift(shift=!shift);
				setButtonsToEqu();
				return 0;
			}			
			if(shift){		
				if(id==R.id.button_left) display.left();
				else if(id==R.id.button_right) display.right();
				else if(id==R.id.button_c_P) return 3;
				else if(id==R.id.button_ans_sin) display.addText("sin(");
				else if(id==R.id.button_pi_cos_arg) display.addText("cos(");
				else if(id==R.id.button_e_tan_cnj) display.addText("tan(");
				else if(id==R.id.button_del_B) return 4;
				else if(id==R.id.button_pn_asin) display.addText("asin(");
				else if(id==R.id.button_nsqrt_acos_abi) display.addText("acos(");
				else if(id==R.id.button_p1_atan_toAfi) display.addText("atan(");
				else if(id==R.id.button_ln_log_i) display.addText("log(");
				else if(id==R.id.button_sqrt_n) display.addText("fact(");
				else if(id==R.id.button_open) display.addText("(");
				else if(id==R.id.button_close && display.getBracket()>0) display.addText(")");
				else if(id==R.id.button_p2_abs) display.addText("abs(");
				else if(id==R.id.button7) display.addText("7");
				else if(id==R.id.button8) display.addText("8");
				else if(id==R.id.button9_deg_rad) display.setRad(MainActivity.rad=!MainActivity.rad);
				else if(id==R.id.button_plus_settings) return 5;
				else if(id==R.id.button4) display.addText("4");
				else if(id==R.id.button5) display.addText("5");
				else if(id==R.id.button6) display.addText("6");
				else if(id==R.id.button_minus_qveq){
					setButtonsToN();
					display.setMod(mod=Mod.N);
					if(history.size()!=0){
						history_pointer=history.size();
						display.setUpDown(1);						
					}else{
						display.setUpDown(-1);
						history_pointer=-1;
					}
				}
				else if(id==R.id.button1_up){	
					--abc_select;
					if(abc_select<1) abc_select=3;
					
					if(abc_select==1) display.setText(""+a);
					else if(abc_select==2) display.setText(""+b);
					else if(abc_select==3) display.setText(""+c);
					
					display.setVarSelected(abc_select);
					return 0;
				}
				else if(id==R.id.button2) display.addText("2");
				else if(id==R.id.button3) display.addText("3");
				else if(id==R.id.button_mull_sum) display.addText("*");
				else if(id==R.id.button0_down){
					++abc_select;
					if(abc_select>3) abc_select=1;
					
					if(abc_select==1) display.setText(""+a);
					else if(abc_select==2) display.setText(""+b);
					else if(abc_select==3) display.setText(""+c);
					
					display.setVarSelected(abc_select);
					return 0;
				}
				else if(id==R.id.button_point_const){
					const_=true;
					setButtonsToConst();
					display.setShift(shift=false);
					return 0;
				}
				else if(id==R.id.button_eq_mem){
					history_pointer=-1;
					history.clear();
					display.setUpDown(-1);
					display.clear();
				}
				else if(id==R.id.button_div_cmplx)display.addText("/");
			}else{
				if(id==R.id.button_left) display.left();
				else if(id==R.id.button_right) display.right();
				else if(id==R.id.button_c_P){
					display.setVarSelected(abc_select=1);
					display.clear();
				}
				else if(id==R.id.button_ans_sin) display.addText("ans");
				else if(id==R.id.button_pi_cos_arg) display.addText("\u03C0");
				else if(id==R.id.button_e_tan_cnj) display.addText("e^(");
				else if(id==R.id.button_del_B){
					display.setVarSelected(abc_select=1);
					display.back();
				}
				else if(id==R.id.button_pn_asin) display.addText("^(");
				else if(id==R.id.button_nsqrt_acos_abi) display.addText("^(1/");
				else if(id==R.id.button_p1_atan_toAfi) display.addText("^(-1)");
				else if(id==R.id.button_ln_log_i) display.addText("ln(");
				else if(id==R.id.button_sqrt_n) display.addText("\u221A(");
				else if(id==R.id.button_open) display.addText("(");
				else if(id==R.id.button_close && display.getBracket()>0) display.addText(")");
				else if(id==R.id.button_p2_abs) display.addText("^(2)");
				else if(id==R.id.button7) display.addText("7");
				else if(id==R.id.button8) display.addText("8");
				else if(id==R.id.button9_deg_rad) display.addText("9");
				else if(id==R.id.button_plus_settings) display.addText("+");
				else if(id==R.id.button4) display.addText("4");
				else if(id==R.id.button5) display.addText("5");
				else if(id==R.id.button6) display.addText("6");
				else if(id==R.id.button_minus_qveq) display.addText("-");
				else if(id==R.id.button1_up) display.addText("1");
				else if(id==R.id.button2) display.addText("2");
				else if(id==R.id.button3) display.addText("3");
				else if(id==R.id.button_mull_sum) display.addText("*");
				else if(id==R.id.button0_down) display.addText("0");
				else if(id==R.id.button_point_const) display.addText(".");
				else if(id==R.id.button_eq_mem) equals(display.getText());
				else if(id==R.id.button_div_cmplx) display.addText("/");
			}
			
			if(shift){
				display.setShift(shift=!shift);
				setButtonsToEqu();
			}	
		}		
				
		return 0;
	}

	private void equals(String text){
		if(display.getBracket()!=0){
			display.setError(true);
			return;
		}		
			
		if(!MMath.parsiraj(text)){
			display.setError(true);
			return;
		}
		
		if(mod==Mod.N){
			if(MainActivity.round>=0 && MainActivity.round<=10){
				ans=MMath.eval(MMath.convertConstantc(text, ans), !MainActivity.rad);
				result=""+MMath.round(""+ans, MainActivity.round);	
			}else{
				ans=MMath.eval(MMath.convertConstantc(text, ans), !MainActivity.rad);
				result=""+MMath.round(""+ans, 11);	
			}
			
			if(result.contains("E")){
				result=result.replace("E", "*10^(");
				result+=")";
			}
			
			if(MainActivity.round==0) result=result.substring(0, result.length()-2);
			if(result.contains("Infinity")) result=result.replace("Infinity", "\u221E");
			
			history.add(display.getText());
			history_pointer=history.size();
			display.setUpDown(1);
			
			display.setText(result);
		}else if(mod==Mod.EQU){
			if(abc_select==1){
				a=MMath.eval(MMath.convertConstantc(text, 0), !MainActivity.rad);
				a=MMath.round(""+a, 11);
				display.clear();
				display.setVarSelected(++abc_select);
			}else if(abc_select==2){
				b=MMath.eval(MMath.convertConstantc(text, 0), !MainActivity.rad);
				b=MMath.round(""+b, 11);
				display.clear();
				display.setVarSelected(++abc_select);
			}else if(abc_select==3){
				c=MMath.eval(MMath.convertConstantc(text, 0), !MainActivity.rad);
				c=MMath.round(""+c, 11);
				display.clear();
				display.setVarSelected(++abc_select);

				display.setDoubleAns(MMath.equ_x1(a, b, c, MainActivity.round), MMath.equ_x2(a, b, c, MainActivity.round));
			}
		}
		
		
	}
	
}
