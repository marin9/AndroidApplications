package tictactoe.gui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Switch;

public class MainActivity extends Activity{
	private Switch sw1;
	private Switch sw2;
	private Switch sw3;	
	private Board board;
	 

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 		
		menu(null);	
	}	
	 
	
	public void start(View view){
		setContentView(R.layout.game_layout);
		board=(Board)findViewById(R.id.board1);		
		board.start(sw1.isChecked()? 5:3, sw2.isChecked()? false:true, sw3.isChecked()? false:true);
	}
	
	public void reset(View view){
		board.reset();
	}
	
	public void menu(View view){	
		if(view!=null) board.finish();
		setContentView(R.layout.activity_main);
		sw1=(Switch)findViewById(R.id.switch1);
		sw2=(Switch)findViewById(R.id.switch2);
		sw3=(Switch)findViewById(R.id.switch3);		
		board=null;
	}
	
}
