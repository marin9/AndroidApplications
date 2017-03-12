package marin.bralic.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import marin.bralic.chessgame.R;

public class MainActivity extends Activity {	
	private Thread gameThread;
	private BoardView gameView;	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.offline_layout);
		
		gameView=(BoardView)findViewById(R.id.boardView1);
		PlayerStatusView stat=(PlayerStatusView)findViewById(R.id.playerStatusView2);
		
		gameView.prepare(stat);
		gameThread=new Thread((Runnable)gameView);
		gameThread.start();	
	}
	
	
	public void exit(View view){
		new AlertDialog.Builder(this)
	    .setTitle("Exit")
	    .setMessage("Are you sure you want to exit ?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	gameView.close();
	        	try{   gameThread.join();   }catch(InterruptedException e){}
	    		finish();
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            return;
	        }
	     })
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .show();
		
	}
	
	public void newGame(View view){
		new AlertDialog.Builder(this)
	    .setTitle("New Game")
	    .setMessage("Are you sure you want to start new game ?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            gameView.reset();
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            return;
	        }
	     })
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .show();
	}

	public void showLastMove(View view){
		gameView.showLastMove();		
	}

	
	@Override
	protected void onPause() {
		gameView.pauseTimers();
		super.onPause();
	}


	@Override
	protected void onResume() {
		gameView.resumeTimers();
		super.onResume();
	}
}
