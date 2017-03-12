package marin.bralic.calc.calculators;

import java.util.Locale;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import marin.bralic.calc.Display;
import marin.bralic.calc.IDisplay;
import marin.bralic.calc.MMath;
import marin.bralic.calc.MainActivity;
import marin.bralic.calc.R;

public class ProgrammerCalculator implements ICalculator{
	private MainActivity mainActivity;
	private IDisplay display;
	private TextView[] base_displays;
	private RadioButton[] radio_buttons;
	private Button[] buttons;
	private Button[] base_buttons;
	
	private Base base;
	private boolean shift;
	private String result;
	
	
	public ProgrammerCalculator(MainActivity main){
		mainActivity=main;		
		initialize();
	}
	
	private void initialize(){
		display=(Display)mainActivity.findViewById(R.id.display);
		
		base_displays=new TextView[4];
		base_displays[0]=(TextView)mainActivity.findViewById(R.id.textView2);
		base_displays[1]=(TextView)mainActivity.findViewById(R.id.textView3);
		base_displays[2]=(TextView)mainActivity.findViewById(R.id.textView4);
		base_displays[3]=(TextView)mainActivity.findViewById(R.id.textView5);
		
		radio_buttons=new RadioButton[4];
		radio_buttons[0]=(RadioButton)mainActivity.findViewById(R.id.radioButton1);//HEX
		radio_buttons[1]=(RadioButton)mainActivity.findViewById(R.id.radioButton2);//OCT
		radio_buttons[2]=(RadioButton)mainActivity.findViewById(R.id.radioButton3);//BIN
		radio_buttons[3]=(RadioButton)mainActivity.findViewById(R.id.radioButton4);//DEC
		
		buttons=new Button[5];
		buttons[0]=(Button)mainActivity.findViewById(R.id.button_lsh_rol);
		buttons[1]=(Button)mainActivity.findViewById(R.id.button_rsh_ror);
		buttons[2]=(Button)mainActivity.findViewById(R.id.button_c_S);
		buttons[3]=(Button)mainActivity.findViewById(R.id.button_del_B);
		buttons[4]=(Button)mainActivity.findViewById(R.id.button_plus_settings);
		
		base_buttons=new Button[14];
		base_buttons[0]=(Button)mainActivity.findViewById(R.id.button2);
		base_buttons[1]=(Button)mainActivity.findViewById(R.id.button3);
		base_buttons[2]=(Button)mainActivity.findViewById(R.id.button4);
		base_buttons[3]=(Button)mainActivity.findViewById(R.id.button5);
		base_buttons[4]=(Button)mainActivity.findViewById(R.id.button6);
		base_buttons[5]=(Button)mainActivity.findViewById(R.id.button7);
		base_buttons[6]=(Button)mainActivity.findViewById(R.id.button8);
		base_buttons[7]=(Button)mainActivity.findViewById(R.id.button9);
		base_buttons[8]=(Button)mainActivity.findViewById(R.id.button_hexA);
		base_buttons[9]=(Button)mainActivity.findViewById(R.id.button_hexB);
		base_buttons[10]=(Button)mainActivity.findViewById(R.id.button_hexC);
		base_buttons[11]=(Button)mainActivity.findViewById(R.id.button_hexD);
		base_buttons[12]=(Button)mainActivity.findViewById(R.id.button_hexE);
		base_buttons[13]=(Button)mainActivity.findViewById(R.id.button_hexF);
		
		setBase(Base.DEC);
	}	
	
	private void setBase(Base b){
		display.clear();
		base=b;
		if(b==Base.BIN){
			for(int i=0;i<14;++i)
				base_buttons[i].setEnabled(false);
		}else if(b==Base.DEC){
			for(int i=0;i<8;++i)
				base_buttons[i].setEnabled(true);
			for(int i=8;i<14;++i)
				base_buttons[i].setEnabled(false);
		}else if(b==Base.OCT){
			for(int i=0;i<6;++i)
				base_buttons[i].setEnabled(true);
			for(int i=6;i<14;++i)
				base_buttons[i].setEnabled(false);
		}else if(b==Base.HEX){
			for(int i=0;i<14;++i)
				base_buttons[i].setEnabled(true);
		}
	}

	@Override
	public int press(View view) {
		int id=view.getId();
		display.setError(false);		
		
		if(shift){
			if(id==R.id.button_c_S) return 2;
			else if(id==R.id.button_del_B) return 4;
			else if(id==R.id.button_plus_settings) return 5;
			else if(id==R.id.button_lsh_rol) display.addText("<<");
			else if(id==R.id.button_rsh_ror) display.addText(">>");
			else if(id==R.id.button_s){
				display.setShift(shift=!shift);
				buttons[0].setText("Lsh");
				buttons[1].setText("Rsh");
				buttons[2].setText("C");
				buttons[3].setText("Del");
				buttons[4].setText("+");
				return 0;
			}	
		}else{
			if(id==R.id.button_c_S){
				display.clear();
				base_displays[0].setText("0");
				base_displays[1].setText("0");
				base_displays[2].setText("0");
				base_displays[3].setText("0");
			}
			else if(id==R.id.button_del_B) display.back();
			else if(id==R.id.button_plus_settings) display.addText("+");
			else if(id==R.id.button_lsh_rol) display.addText("<");
			else if(id==R.id.button_rsh_ror) display.addText(">");
			else if(id==R.id.button_s){
				display.setShift(shift=!shift);
				buttons[0].setText("Rol");
				buttons[1].setText("Ror");
				buttons[2].setText("Scf");
				buttons[3].setText("Bsc");
				buttons[4].setText("Sett");
				return 0;
			}
		}
		
		if(shift){
			display.setShift(shift=!shift);
			buttons[0].setText("Lsh");
			buttons[1].setText("Rsh");
			buttons[2].setText("C");
			buttons[3].setText("Del");
			buttons[4].setText("+");
		}
		
		if(id==R.id.radioButton1){			
			radio_buttons[1].setChecked(false);
			radio_buttons[2].setChecked(false);
			radio_buttons[3].setChecked(false);
			setBase(Base.HEX);
		}else if(id==R.id.radioButton2){
			radio_buttons[0].setChecked(false);
			radio_buttons[2].setChecked(false);
			radio_buttons[3].setChecked(false);
			setBase(Base.OCT);
		}else if(id==R.id.radioButton3){
			radio_buttons[0].setChecked(false);
			radio_buttons[1].setChecked(false);
			radio_buttons[3].setChecked(false);
			setBase(Base.BIN);
		}else if(id==R.id.radioButton4){
			radio_buttons[0].setChecked(false);
			radio_buttons[1].setChecked(false);
			radio_buttons[2].setChecked(false);
			setBase(Base.DEC);
		}
		
		if(id==R.id.button0) display.addText("0");
		else if(id==R.id.button1) display.addText("1");
		else if(id==R.id.button2) display.addText("2");
		else if(id==R.id.button3) display.addText("3");
		else if(id==R.id.button4) display.addText("4");
		else if(id==R.id.button5) display.addText("5");
		else if(id==R.id.button6) display.addText("6");
		else if(id==R.id.button7) display.addText("7");
		else if(id==R.id.button8) display.addText("8");
		else if(id==R.id.button9) display.addText("9");
		else if(id==R.id.button_hexA) display.addText("A");
		else if(id==R.id.button_hexB) display.addText("B");
		else if(id==R.id.button_hexC) display.addText("C");
		else if(id==R.id.button_hexD) display.addText("D");
		else if(id==R.id.button_hexE) display.addText("E");
		else if(id==R.id.button_hexF) display.addText("F");
		else if(id==R.id.button_minus) display.addText("-");
		else if(id==R.id.button_mull) display.addText("*");
		else if(id==R.id.button_div) display.addText("/");
		else if(id==R.id.button_left) display.left();
		else if(id==R.id.button_right) display.right();
		else if(id==R.id.button_mod) display.addText("%");
		else if(id==R.id.button_open) display.addText("(");
		else if(id==R.id.button_close && display.getBracket()>0) display.addText(")");
		else if(id==R.id.button_and) display.addText("and(");
		else if(id==R.id.button_or) display.addText("or(");
		else if(id==R.id.button_not) display.addText("not(");
		else if(id==R.id.button_xor) display.addText("xor(");
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
		
		try{
		  result=""+MMath.eval(text, base);
		}catch(ArithmeticException e){
			display.setText("\u221E");
			return;
		}
		long result_l=Long.parseLong(result);	
		
		base_displays[0].setText(""+result);
		base_displays[1].setText(""+Long.toBinaryString(result_l));
		base_displays[2].setText(""+Long.toOctalString(result_l));		
		base_displays[3].setText(""+Long.toHexString(result_l).toUpperCase(Locale.getDefault()));

		if(base==Base.BIN) display.setText(""+Long.toBinaryString(result_l));
		else if(base==Base.DEC) display.setText(result);
		else if(base==Base.OCT) display.setText(""+Long.toOctalString(result_l));
		else if(base==Base.HEX) display.setText(""+Long.toHexString(result_l).toUpperCase(Locale.getDefault()));
	}
	
	
	public static enum Base{
		BIN(), DEC(), OCT(), HEX();
	}

}
