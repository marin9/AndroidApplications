package marin.bralic.calc.calculators;

import android.view.View;
import android.widget.Button;
import marin.bralic.calc.Display;
import marin.bralic.calc.IDisplay;
import marin.bralic.calc.MMath;
import marin.bralic.calc.MainActivity;
import marin.bralic.calc.R;

public class BasicCalculator implements ICalculator{
	private MainActivity mainActivity;
	private IDisplay display;
	private Button[] buttons;
	
	private String result;
	private boolean shift;
	
	
	public BasicCalculator(MainActivity main){
		mainActivity=main;		
		initialize();
	}
	
	private void initialize(){
		display=(Display)mainActivity.findViewById(R.id.display);
		
		buttons=new Button[3];
		buttons[0]=(Button)mainActivity.findViewById(R.id.button_c_S);
		buttons[1]=(Button)mainActivity.findViewById(R.id.button_del_P);
		buttons[2]=(Button)mainActivity.findViewById(R.id.button_plus_settings);
		
		if(MainActivity.round>=0 && MainActivity.round<=10) display.setRound(MainActivity.round);
	}

	@Override
	public int press(View view) {
		int id=view.getId();
		display.setError(false);
		
		if(shift){
			if(id==R.id.button_c_S) return 2;
			else if(id==R.id.button_del_P) return 3;
			else if(id==R.id.button_plus_settings) return 5;
			else if(id==R.id.button_s){
				display.setShift(shift=!shift);
				buttons[0].setText("C");
				buttons[1].setText("Del");
				buttons[2].setText("+");
				return 0;
			}	
		}else{
			if(id==R.id.button_c_S) display.clear();
			else if(id==R.id.button_del_P) display.back();
			else if(id==R.id.button_plus_settings) display.addText("+");
			else if(id==R.id.button_s){
				display.setShift(shift=!shift);
				buttons[0].setText("Scf");
				buttons[1].setText("Prog");
				buttons[2].setText("Sett");
				return 0;
			}
		}
		
		if(shift){
			display.setShift(shift=!shift);
			buttons[0].setText("C");
			buttons[1].setText("Del");
			buttons[2].setText("+");
		}
		
		if(id==R.id.button_sqrt) display.addText("\u221A(");
		else if(id==R.id.button_pn) display.addText("^(");
		else if(id==R.id.button_pi) display.addText("\u03C0");
		else if(id==R.id.button_open) display.addText("(");
		else if(id==R.id.button_close && display.getBracket()>0) display.addText(")");
		else if(id==R.id.button0) display.addText("0");
		else if(id==R.id.button1) display.addText("1");
		else if(id==R.id.button2) display.addText("2");
		else if(id==R.id.button3) display.addText("3");
		else if(id==R.id.button4) display.addText("4");
		else if(id==R.id.button5) display.addText("5");
		else if(id==R.id.button6) display.addText("6");
		else if(id==R.id.button7) display.addText("7");
		else if(id==R.id.button8) display.addText("8");
		else if(id==R.id.button9) display.addText("9");
		else if(id==R.id.button_minus) display.addText("-");
		else if(id==R.id.button_mull) display.addText("*");
		else if(id==R.id.button_div) display.addText("/");
		else if(id==R.id.button_point) display.addText(".");
		else if(id==R.id.button_eq) equals(display.getText());		
						
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
		
		if(MainActivity.round>=0 && MainActivity.round<=10) result=""+MMath.round(""+MMath.eval(MMath.convertConstantc(text, 0), true), MainActivity.round);	
		else result=""+MMath.round(""+MMath.eval(MMath.convertConstantc(text, 0), true), 11);	
		
		if(result.contains("E")){
			result=result.replace("E", "*10^(");
			result+=")";
		}
		
		if(MainActivity.round==0) result=result.substring(0, result.length()-2);
		if(result.contains("Infinity")) result=result.replace("Infinity", "\u221E");
		
		display.setText(result);
	}
}
