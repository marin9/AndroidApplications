package tictactoe.gui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import tictactoe.game.Game3;
import tictactoe.game.Game5;
import tictactoe.game.IGame;
import tictactoe.game.Sign;
import tictactoe.game.Status;

public class Board extends View{
	private int width;
	private Paint paint;
	private Bitmap xImage, oImage, statusImage[];
	
	private IGame game;
	private Thread pcThread;
	private volatile boolean wait, pc, player_x;
	private int board_n, d3, d5, statusImH;

	
	public Board(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	private void initialize(){
		width=Resources.getSystem().getDisplayMetrics().widthPixels;
		
		paint=new Paint();
		d3=width/3;
		d5=width/5;
		statusImH=(int)Math.round(width*0.05);
		board_n=3;
		game=new Game3();
			
		statusImage=new Bitmap[6];
		statusImage[0]=BitmapFactory.decodeResource(getResources(), R.drawable.x_turn);
		statusImage[1]=BitmapFactory.decodeResource(getResources(), R.drawable.o_turn);
		statusImage[2]=BitmapFactory.decodeResource(getResources(), R.drawable.wait);
		statusImage[3]=BitmapFactory.decodeResource(getResources(), R.drawable.x_win);
		statusImage[4]=BitmapFactory.decodeResource(getResources(), R.drawable.o_win);
		statusImage[5]=BitmapFactory.decodeResource(getResources(), R.drawable.draw);
		
		statusImage[0]=Bitmap.createScaledBitmap(statusImage[0], statusImH*5, statusImH, false);
		statusImage[1]=Bitmap.createScaledBitmap(statusImage[1], statusImH*5, statusImH, false);
		statusImage[2]=Bitmap.createScaledBitmap(statusImage[2], statusImH*5, statusImH, false);
		statusImage[3]=Bitmap.createScaledBitmap(statusImage[3], statusImH*5, statusImH, false);
		statusImage[4]=Bitmap.createScaledBitmap(statusImage[4], statusImH*5, statusImH, false);
		statusImage[5]=Bitmap.createScaledBitmap(statusImage[5], statusImH*5, statusImH, false);
		
		xImage=BitmapFactory.decodeResource(getResources(), R.drawable.x);			
		oImage=BitmapFactory.decodeResource(getResources(), R.drawable.o);				
	}
	

	@SuppressLint("ClickableViewAccessibility")
	public void start(int board_length, boolean pc_on, boolean x){
		board_n=board_length;
		pc=pc_on;
		player_x=x;
		
		if(board_n==3){
			game=new Game3();
			xImage=Bitmap.createScaledBitmap(xImage, d3, d3, false);
			oImage=Bitmap.createScaledBitmap(oImage, d3, d3, false);
		}else{
			game=new Game5();
			xImage=Bitmap.createScaledBitmap(xImage, d5, d5, false);
			oImage=Bitmap.createScaledBitmap(oImage, d5, d5, false);
		}		
		
		reset();       
	}
	
	public void reset(){
		finish();
		
		game.reset();
		invalidate();
		
		if(pc && !player_x) doPcMove();	
	}
	
	public void finish(){
		if(pcThread!=null && pcThread.isAlive()){
			pcThread.interrupt();
			try{  pcThread.join();  }catch(InterruptedException e){}
			wait=false;
		}
	}
		
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(width, width+statusImH+20);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {	
		if(board_n==3){
			paint.setColor(Color.rgb(30, 30, 50));
			canvas.drawRect(0, 0, width, width, paint);
			
			paint.setColor(Color.rgb(255, 170, 30));
			paint.setStrokeWidth(10);
			canvas.drawLine(d3, 0, width/3, width, paint);
			canvas.drawLine(2*d3, 0, 2*d3, width, paint);		
			
			canvas.drawLine(0, d3, width, d3, paint);
			canvas.drawLine(0, 2*d3, width, 2*d3, paint);
			
			for(int i=0;i<3;++i){
				for(int j=0;j<3;++j){
					if(game.getSign(i, j)==Sign.X) canvas.drawBitmap(xImage, i*d3, j*d3, paint);
					else if (game.getSign(i, j)==Sign.O) canvas.drawBitmap(oImage, i*d3, j*d3, paint);
				}
			}
			
		}else{
			paint.setColor(Color.rgb(30, 30, 50));
			canvas.drawRect(0, 0, width, width, paint);
			
			paint.setColor(Color.rgb(255, 170, 30));
			paint.setStrokeWidth(10);
			canvas.drawLine(width/5, 0, width/5, width, paint);
			canvas.drawLine(2*width/5, 0, 2*width/5, width, paint);
			canvas.drawLine(3*width/5, 0, 3*width/5, width, paint);		
			canvas.drawLine(4*width/5, 0, 4*width/5, width, paint);		
			
			canvas.drawLine(0, width/5, width, width/5, paint);
			canvas.drawLine(0, 2*width/5, width, 2*width/5, paint);
			canvas.drawLine(0, 3*width/5, width, 3*width/5, paint);
			canvas.drawLine(0, 4*width/5, width, 4*width/5, paint);
			
			for(int i=0;i<5;++i){
				for(int j=0;j<5;++j){
					if(game.getSign(i, j)==Sign.X) canvas.drawBitmap(xImage, i*d5, j*d5, paint);
					else if (game.getSign(i, j)==Sign.O) canvas.drawBitmap(oImage, i*d5, j*d5, paint);
				}
			}
		}	

		Status gstatus=game.getStatus();
		if(gstatus==Status.X_TURN) canvas.drawBitmap(statusImage[0], 0, width+10, paint);
		else if(gstatus==Status.O_TURN) canvas.drawBitmap(statusImage[1], 0, width+10, paint);
		else if(gstatus==Status.X_WIN) canvas.drawBitmap(statusImage[3], 0, width+10, paint);
		else if(gstatus==Status.O_WIN) canvas.drawBitmap(statusImage[4], 0, width+10, paint);
		else if(gstatus==Status.DRAW) canvas.drawBitmap(statusImage[5], 0, width+10, paint);
		
		if(wait) canvas.drawBitmap(statusImage[2], 0, width+10, paint);
	}
	
	
	private void doPcMove(){
		pcThread=new Thread(new Runnable(){
			@Override
			public void run() {
				wait=true;
				Board.this.postInvalidate();
				try{  Thread.sleep(500);  }catch(InterruptedException e){}
				game.playBestMove();
				wait=false;
				Board.this.postInvalidate();
			} 			
		});
		
		pcThread.start();
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(wait) return true;
		
		if(board_n==3){
    		int x=(int)(event.getX()/d3);
        	int y=(int)(event.getY()/d3);
        	if(x>2 || y>2) return true;

        	 game.move(x, y);
    	}else{
    		int x=(int)(event.getX()/d5);
        	int y=(int)(event.getY()/d5);
        	if(x>4 || y>4) return true;
       	
        	game.move(x, y); 
    	}
    	
        if(pc && (game.getStatus()==Status.X_TURN && !player_x || game.getStatus()==Status.O_TURN && player_x)) doPcMove();
        else invalidate();
        
		return super.onTouchEvent(event);
	}
	
}
