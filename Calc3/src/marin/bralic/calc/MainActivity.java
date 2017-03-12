package marin.bralic.calc;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import marin.bralic.calc.calculators.BasicCalculator;
import marin.bralic.calc.calculators.ICalculator;
import marin.bralic.calc.calculators.ProgrammerCalculator;
import marin.bralic.calc.calculators.ScientificCalculator;

public class MainActivity extends Activity{
	private Vibrator vibrator;
	private ICalculator calculator;
	
	public static boolean vib, light_color, rad, settings;
	public static int round, stat;
	public static String mod;
	
		 
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);        
		vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);	
		
		loadData(); 
		loadLayout();
	}
	
	@Override
	protected void onDestroy() {
		saveData();
		super.onDestroy();
	}
	
	private void loadLayout(){	        				 
		if(light_color){
			if(mod.equals("B")) setContentView(R.layout.light_basic_layout);
			else if(mod.equals("S")) setContentView(R.layout.light_scientific_layout);
			else if(mod.equals("P")) setContentView(R.layout.light_progremmer_layout);
		}else{
			if(mod.equals("B")) setContentView(R.layout.dark_basic_layout);
			else if(mod.equals("S")) setContentView(R.layout.dark_scientific_layout);
			else if(mod.equals("P")) setContentView(R.layout.dark_programmer_layout);
		}	
		
		if(mod.equals("B")) calculator=new BasicCalculator(this);
		else if(mod.equals("S")) calculator=new ScientificCalculator(this);
		else if(mod.equals("P")) calculator=new ProgrammerCalculator(this);
	}
	
	private void loadSettings(){  
		settings=true;		
		setContentView(R.layout.settings_layout);
		
		if(vib) ((CheckBox)findViewById(R.id.checkBox_vib)).setChecked(true);		
		if(light_color) ((CheckBox)findViewById(R.id.checkBox_color)).setChecked(true);			
		if(rad) ((CheckBox)findViewById(R.id.checkBox_rad)).setChecked(true);	
		  
		((TextView)findViewById(R.id.roundNumber)).setText(""+round);

		if(mod.equals("B")) ((RadioButton)findViewById(R.id.radio0)).setChecked(true);
		else if(mod.equals("S")) ((RadioButton)findViewById(R.id.radio1)).setChecked(true);
		else if(mod.equals("P")) ((RadioButton)findViewById(R.id.radio2)).setChecked(true);		
	}
	
	  
	public void pressButton(View view){
		if(vib) vibrator.vibrate(60);
		
		if(settings){
			int id=view.getId();
						
			if(id==R.id.rbt1){
				TextView roundText=(TextView)findViewById(R.id.roundNumber);
				int r=Integer.parseInt(""+roundText.getText());
				if(r==-1) return;
				roundText.setText(""+(r-1));
			}else if(id==R.id.rbt2){
				TextView roundText=(TextView)findViewById(R.id.roundNumber);
				int r=Integer.parseInt(""+roundText.getText());
				if(r==15) return;
				roundText.setText(""+(r+1));
			}else if(id==R.id.cancleButton){
				settings=false;
				vib=((CheckBox)findViewById(R.id.checkBox_vib)).isChecked();
				light_color=((CheckBox)findViewById(R.id.checkBox_color)).isChecked();
				rad=((CheckBox)findViewById(R.id.checkBox_rad)).isChecked();
				
				round=(int)Integer.parseInt(""+((TextView)findViewById(R.id.roundNumber)).getText());
				
				if(((RadioButton)findViewById(R.id.radio0)).isChecked()) mod="B";
				else if(((RadioButton)findViewById(R.id.radio1)).isChecked()) mod="S";
				else if(((RadioButton)findViewById(R.id.radio2)).isChecked()) mod="P";
				
				saveData();
				loadLayout();
			}
		}else{
			stat=calculator.press(view);
					
			if(stat==2) mod="S";				
			else if(stat==3) mod="P";							
			else if(stat==4) mod="B";								
			
			if(stat!=0 && stat!=5) loadLayout();
			else if(stat==5) loadSettings();
		}		
	}
	
		
	private void loadData(){
		SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		mod=settings.getString("MOD", "B");
		rad=settings.getBoolean("RAD", false);
		light_color=settings.getBoolean("LAYOUT", true);
		vib=settings.getBoolean("VIBRATION", true);
		round=settings.getInt("ROUND", -1);		
	}
	
	private void saveData(){
		SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());	
		SharedPreferences.Editor editor=settings.edit();
		
		editor.putString("MOD", mod);
		editor.putBoolean("RAD", rad);
		editor.putBoolean("LAYOUT", light_color);
		editor.putBoolean("VIBRATION", vib);
		editor.putInt("ROUND", round);		
		editor.commit();
	}
}
