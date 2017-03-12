package marin.bralic.application;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import marin.bralic.chessgame.R;

public class PlayerStatusView extends View{
	private Handler handler;
	private int width, height, h, h_im, h_bo;
	private Paint paint;
	private Bitmap[] statusImage;
	
	private String playerName;
	private long time;
	private boolean your_turn, chess, mate, pat, draw;
	
	
	public PlayerStatusView(Context context) {
		super(context);		
		handler=new Handler(context.getMainLooper());
		initialize();
	}
	
	public PlayerStatusView(Context context, AttributeSet attrs) {
		super(context, attrs);		
		handler=new Handler(context.getMainLooper());
		initialize();
	}
	
	public PlayerStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);		
		handler=new Handler(context.getMainLooper());
		initialize();
	}
	
	
	private void initialize(){
		DisplayMetrics metrics=Resources.getSystem().getDisplayMetrics();
		width=metrics.widthPixels;
		height=metrics.heightPixels;
		
		playerName="";
		time=0;
		paint=new Paint();
		
		loadImages();
	}
	
	private void loadImages(){
		statusImage=new Bitmap[5];
		statusImage[0]=BitmapFactory.decodeResource(getResources(), R.drawable.your_move);
		statusImage[1]=BitmapFactory.decodeResource(getResources(), R.drawable.chess);
		statusImage[2]=BitmapFactory.decodeResource(getResources(), R.drawable.mate);
		statusImage[3]=BitmapFactory.decodeResource(getResources(), R.drawable.pat);
		statusImage[4]=BitmapFactory.decodeResource(getResources(), R.drawable.draw);
		
		h=(int)Math.round(height*0.055);
		h_im=(int)Math.round(h*0.85);
		h_bo=(h-h_im)/2;
		statusImage[0]=Bitmap.createScaledBitmap(statusImage[0], h_im, h_im, false);
		statusImage[1]=Bitmap.createScaledBitmap(statusImage[1], h_im, h_im, false);
		statusImage[2]=Bitmap.createScaledBitmap(statusImage[2], h_im, h_im, false);
		statusImage[3]=Bitmap.createScaledBitmap(statusImage[3], h_im, h_im, false);
		statusImage[4]=Bitmap.createScaledBitmap(statusImage[4], h_im, h_im, false);
	}	
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
						
		paint.setColor(Color.GRAY);
		canvas.drawRect(0, 0, width, h, paint);
		paint.setColor(Color.BLACK);
		canvas.drawRect(h_bo, h_bo, width-h_bo, h-h_bo, paint);
		
		paint.setColor(Color.CYAN);
		paint.setTextSize(40);
		canvas.drawText(playerName, 2*h_bo, h-(h/3), paint);
		
		paint.setTextSize(35);
		long min=time/60;
		long hours=time/3600;
		canvas.drawText(String.format("%02d:%02d:%02d", hours, min, time%60), width/2-25*h_bo, h-(h/3), paint);
		
		if(your_turn) canvas.drawBitmap(statusImage[0], width-h_bo*5-h_im*5, h_bo, paint);
		if(chess) canvas.drawBitmap(statusImage[1], width-h_bo*4-h_im*4, h_bo, paint);
		if(mate) canvas.drawBitmap(statusImage[2], width-h_bo*3-h_im*3, h_bo, paint);
		if(pat) canvas.drawBitmap(statusImage[3], width-h_bo*2-h_im*2, h_bo, paint);
		if(draw) canvas.drawBitmap(statusImage[4], width-h_bo-h_im, h_bo, paint);						
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(width, h);
	}
	
	private void repaint(){
		handler.post(new Runnable(){
			@Override
			public void run() {
				invalidate();
			}			
		});
	}
	
	public void clearAll(){
		playerName="";
		chess=mate=pat=draw=your_turn=false;
	}
	
    public void setName(String name){
    	playerName=name;
    	repaint();
    }
    
    public void setTurnState(boolean stat){
    	your_turn=stat;
    	repaint();
    }
    
    public void setChessState(boolean stat){
    	chess=stat;
    	repaint();
    }
 
    public void setMateState(boolean stat){
    	mate=stat;
    	repaint();
    }
 
    public void setPatState(boolean stat){
    	pat=stat;
    	repaint();
    }
 
    public void setDrawState(boolean stat){
    	draw=stat;
    	repaint();
    }
    
    public void setTime(long time_sec){
    	time=time_sec;
    	repaint();
    }

}
