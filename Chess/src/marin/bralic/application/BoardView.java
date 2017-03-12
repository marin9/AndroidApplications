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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import marin.bralic.chessgame.R;
import marin.bralic.game.Game;
import marin.bralic.game.IllegalMoveException;
import marin.bralic.game.Pieces;

public class BoardView extends View implements Runnable{
	//view resources
	private boolean run;
	private Context context;
	private Handler handler;
	private int width, m_im, m_bo;
	private Bitmap[] fieldImages;
	private Bitmap[][] piecesImage;
	private Paint paint;
	
	private Game game;
	private char myColor;
	private PlayerStatusView statusView;	
	private Time tw, tb;
	
	//animation variable
	private int moveingX2, moveingY2, dx, dy, i1x, i1y, i2x, i2y;
	private Pieces animPieces, eatedPieces;
	private boolean animation;
	
	//last move
	private volatile boolean lastMove;
	private long lastMoveT;
	
	
	public BoardView(Context context) {
		super(context);
		initialize(context);
	}
	
	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}
	
	public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize(context);
	}
	
	public void close(){
		run=false;
		tw.finish();
		tb.finish();
		
		try{   tw.join();  }catch(InterruptedException e){}
		try{   tb.join();  }catch(InterruptedException e){}
	}
	
	private void initialize(Context context){
		handler=new Handler(context.getMainLooper());
		this.context=context;
		
		DisplayMetrics metrics=Resources.getSystem().getDisplayMetrics();
		width=metrics.widthPixels;
		
		paint=new Paint();
		game=new Game();
		myColor='w';

		loadImages();
		
		setOnTouchListener(new TouchListener());			

	}
		
	private void loadImages(){
		m_bo=(int)Math.round(width*0.026);
		m_im=(width-2*m_bo)/8;
		
		fieldImages=new Bitmap[5];
		fieldImages[0]=BitmapFactory.decodeResource(getResources(), R.drawable.b_field);
		fieldImages[1]=BitmapFactory.decodeResource(getResources(), R.drawable.w_field);
		
		fieldImages[0]=Bitmap.createScaledBitmap(fieldImages[0], m_im, m_im, false);
		fieldImages[1]=Bitmap.createScaledBitmap(fieldImages[1], m_im, m_im, false);
		
		piecesImage=new Bitmap[6][6];		
		piecesImage[0][0]=BitmapFactory.decodeResource(getResources(), R.drawable.b_pawn);
		piecesImage[0][1]=BitmapFactory.decodeResource(getResources(), R.drawable.b_horse);
		piecesImage[0][2]=BitmapFactory.decodeResource(getResources(), R.drawable.b_hunter);
		piecesImage[0][3]=BitmapFactory.decodeResource(getResources(), R.drawable.b_rock);
		piecesImage[0][4]=BitmapFactory.decodeResource(getResources(), R.drawable.b_queen);
		piecesImage[0][5]=BitmapFactory.decodeResource(getResources(), R.drawable.b_king);
		piecesImage[1][0]=BitmapFactory.decodeResource(getResources(), R.drawable.w_pawn);
		piecesImage[1][1]=BitmapFactory.decodeResource(getResources(), R.drawable.w_horse);
		piecesImage[1][2]=BitmapFactory.decodeResource(getResources(), R.drawable.w_hunter);
		piecesImage[1][3]=BitmapFactory.decodeResource(getResources(), R.drawable.w_rock);
		piecesImage[1][4]=BitmapFactory.decodeResource(getResources(), R.drawable.w_queen);
		piecesImage[1][5]=BitmapFactory.decodeResource(getResources(), R.drawable.w_king);
		
		piecesImage[0][0]=Bitmap.createScaledBitmap(piecesImage[0][0], m_im, m_im, false);
		piecesImage[0][1]=Bitmap.createScaledBitmap(piecesImage[0][1], m_im, m_im, false);
		piecesImage[0][2]=Bitmap.createScaledBitmap(piecesImage[0][2], m_im, m_im, false);
		piecesImage[0][3]=Bitmap.createScaledBitmap(piecesImage[0][3], m_im, m_im, false);
		piecesImage[0][4]=Bitmap.createScaledBitmap(piecesImage[0][4], m_im, m_im, false);
		piecesImage[0][5]=Bitmap.createScaledBitmap(piecesImage[0][5], m_im, m_im, false);
		piecesImage[1][0]=Bitmap.createScaledBitmap(piecesImage[1][0], m_im, m_im, false);
		piecesImage[1][1]=Bitmap.createScaledBitmap(piecesImage[1][1], m_im, m_im, false);
		piecesImage[1][2]=Bitmap.createScaledBitmap(piecesImage[1][2], m_im, m_im, false);
		piecesImage[1][3]=Bitmap.createScaledBitmap(piecesImage[1][3], m_im, m_im, false);
		piecesImage[1][4]=Bitmap.createScaledBitmap(piecesImage[1][4], m_im, m_im, false);
		piecesImage[1][5]=Bitmap.createScaledBitmap(piecesImage[1][5], m_im, m_im, false);
	}

	public void reset(){
		if(tw!=null && tw.isAlive()){
			tw.finish();
			try {
				tw.join();
			} catch (InterruptedException e) {}
		}
		
		if(tb!=null && tb.isAlive()){
			tb.finish();
			try {
				tb.join();
			} catch (InterruptedException e) {}
		}
		
		tw=new Time();
		tb=new Time();
		tw.start();
		tb.start();
		tw.resumeT();
		
		
		myColor='w';	
		lastMove=false;
		game.reset();
		
		statusView.clearAll();
		setStatus();		
	}
	
	public void prepare(PlayerStatusView p2){
		statusView=p2;
		reset();
	}
	
	public void showLastMove(){
		lastMoveT=0;
		lastMove=!lastMove;
		
		if(game.getTurn()=='w'){
			long min=tb.getTsec()/60;
			long hours=tb.getTsec()/3600;
			
			Toast.makeText(context, String.format("%02d:%02d:%02d", hours, min, tb.getTsec()%60), Toast.LENGTH_SHORT).show();
		}else{
			long min=tw.getTsec()/60;
			long hours=tw.getTsec()/3600;
			
			Toast.makeText(context, String.format("%02d:%02d:%02d", hours, min, tw.getTsec()%60), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void pauseTimers(){
		tw.pauseT();
		tb.pauseT();
	}
	
	public void resumeTimers(){
		if(game.getTurn()=='w') tw.resumeT();
		else tb.resumeT();
	}
	
	
	
	@Override
	public void run() {	
		run=true;
		
		while(run){
			if(animation)
				for(int i=0;i<4;++i)
					step();
			
			if(lastMove){
				++lastMoveT;
				if(lastMoveT==150) lastMove=false;
			}
			
			render();	
			try{  Thread.sleep(10);  }catch(InterruptedException e){}
		}
	}
	
	private void step(){
		if(dx<moveingX2) ++dx;
		else if(dx>moveingX2) --dx;
		
		if(dy<moveingY2) ++dy;
		else if(dy>moveingY2) --dy;
		
		if(dx==moveingX2 && dy==moveingY2 && animation){
			animation=false;
			try{   Thread.sleep(500);   } catch (InterruptedException e){}
			myColor=(char)(217-myColor);
			setStatus();
		}
	}
	
	private void setStatus(){
		int status=game.getStatus();
		
		statusView.setTurnState(true);

	    if(game.getTurn()=='w') statusView.setName("White");
	    else statusView.setName("Black");	             
	    	    	    
        
        if(status==Game.CHESS) statusView.setChessState(true);  
        else statusView.setChessState(false);  
        
        if(status==Game.MATE){
        	statusView.setChessState(true);  
        	statusView.setMateState(true);
        	statusView.setTurnState(false);
        	tb.pauseT();
        	tw.pauseT();
        }
        
        if(status==Game.PAT){
        	statusView.setPatState(true); 
        	statusView.setTurnState(false);
        	tb.pauseT();
        	tw.pauseT();
        }
	    
	    if(status==Game.DRAW){
        	statusView.setDrawState(true);
        	statusView.setTurnState(false);
        	tb.pauseT();
        	tw.pauseT();
        }	    	
	}
	 
	private void render(){
		handler.post(new Runnable(){
			@Override
			public void run() {
				invalidate();
			}			
		});
	}
	
    @Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if(myColor=='w'){
			//For white
			
			//draw border
			paint.setColor(Color.rgb(40, 30, 10));
			canvas.drawRect(0, 0, width, width, paint);		
			paint.setColor(Color.rgb(150, 150, 150));
			paint.setTextSize(17);
			for(int i=0;i<8;++i){
				canvas.drawText(""+(char)('A'+i), i*m_im+(m_im/2)+m_bo, m_bo-m_bo*0.2f, paint);
				canvas.drawText(""+(char)('A'+i), i*m_im+(m_im/2)+m_bo, width-m_bo*0.3f, paint);
				canvas.drawText(""+(8-i), 2, i*m_im+(m_im/2)+m_bo, paint);
				canvas.drawText(""+(8-i), width-m_bo+2, i*m_im+(m_im/2)+m_bo, paint);
			}
			
			//draw board and selected and available
			for(int i=0;i<8;++i){
				for(int j=0;j<8;++j){
					if(i%2==j%2) canvas.drawBitmap(fieldImages[1], m_bo+m_im*i, m_bo+m_im*j, paint);
					else canvas.drawBitmap(fieldImages[0], m_bo+m_im*i, m_bo+m_im*j, paint);
					
					Pieces p=game.getPieces(i, j);
					if(!(animation && (i==i1x && j==i1y || i==i2x && j==i2y))){
						if(p!=Pieces.FREE){
							if(p==Pieces.PAWN_B) canvas.drawBitmap(piecesImage[0][0], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.HORSE_B) canvas.drawBitmap(piecesImage[0][1], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.HUNTER_B) canvas.drawBitmap(piecesImage[0][2], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.ROCK_B) canvas.drawBitmap(piecesImage[0][3], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.QUEEN_B) canvas.drawBitmap(piecesImage[0][4], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.KING_B) canvas.drawBitmap(piecesImage[0][5], m_bo+m_im*i, m_bo+m_im*j, paint);
							
							else if(p==Pieces.PAWN_W) canvas.drawBitmap(piecesImage[1][0], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.HORSE_W) canvas.drawBitmap(piecesImage[1][1], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.HUNTER_W) canvas.drawBitmap(piecesImage[1][2], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.ROCK_W) canvas.drawBitmap(piecesImage[1][3], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.QUEEN_W) canvas.drawBitmap(piecesImage[1][4], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.KING_W) canvas.drawBitmap(piecesImage[1][5], m_bo+m_im*i, m_bo+m_im*j, paint);
						}
					}	
					if(animation && i==i2x && j==i2y){
						if(eatedPieces==Pieces.PAWN_B) canvas.drawBitmap(piecesImage[0][0], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.HORSE_B) canvas.drawBitmap(piecesImage[0][1], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.HUNTER_B) canvas.drawBitmap(piecesImage[0][2], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.ROCK_B) canvas.drawBitmap(piecesImage[0][3], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.QUEEN_B) canvas.drawBitmap(piecesImage[0][4], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.KING_B) canvas.drawBitmap(piecesImage[0][5], m_bo+m_im*i, m_bo+m_im*j, paint);
						
						else if(eatedPieces==Pieces.PAWN_W) canvas.drawBitmap(piecesImage[1][0], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.HORSE_W) canvas.drawBitmap(piecesImage[1][1], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.HUNTER_W) canvas.drawBitmap(piecesImage[1][2], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.ROCK_W) canvas.drawBitmap(piecesImage[1][3], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.QUEEN_W) canvas.drawBitmap(piecesImage[1][4], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.KING_W) canvas.drawBitmap(piecesImage[1][5], m_bo+m_im*i, m_bo+m_im*j, paint);
					}
					
					if(game.isSelected() && game.getSelectedX()==i && game.getSelectedY()==j){
						paint.setColor(Color.GREEN);
						int d=5;
						paint.setStrokeWidth(d*2);
						canvas.drawLine(m_bo+i*m_im,   m_bo+j*m_im+d,   m_bo+(i+1)*m_im,   m_bo+j*m_im+d, paint);	//top
						canvas.drawLine(m_bo+i*m_im,   m_bo+j*m_im-d+m_im,   m_bo+(i+1)*m_im,   m_bo+j*m_im-d+m_im, paint);		//left			
						canvas.drawLine(m_bo+i*m_im+d,   m_bo+j*m_im+d,   m_bo+i*m_im+d,   m_bo+j*m_im-d+m_im, paint);	
						canvas.drawLine(m_bo+(i+1)*m_im-d,   m_bo+j*m_im+d,   m_bo+(i+1)*m_im-d,   m_bo+j*m_im-d+m_im, paint);		//bottom					
						
					}else if(game.isSelected() && game.isAllowed(i, j)){
						paint.setColor(Color.rgb(0, 170, 170));
						canvas.drawCircle(m_bo+i*m_im+(m_im/2), m_bo+j*m_im+(m_im/2), m_im/6, paint);					
					}
				}
			}
			
			if(animation){
				if(animPieces==Pieces.PAWN_B) canvas.drawBitmap(piecesImage[0][0], dx, dy, paint);
				else if(animPieces==Pieces.HORSE_B) canvas.drawBitmap(piecesImage[0][1], dx, dy, paint);
				else if(animPieces==Pieces.HUNTER_B) canvas.drawBitmap(piecesImage[0][2], dx, dy, paint);
				else if(animPieces==Pieces.ROCK_B) canvas.drawBitmap(piecesImage[0][3], dx, dy, paint);
				else if(animPieces==Pieces.QUEEN_B) canvas.drawBitmap(piecesImage[0][4], dx, dy, paint);
				else if(animPieces==Pieces.KING_B) canvas.drawBitmap(piecesImage[0][5], dx, dy, paint);
				
				else if(animPieces==Pieces.PAWN_W) canvas.drawBitmap(piecesImage[1][0], dx, dy, paint);
				else if(animPieces==Pieces.HORSE_W) canvas.drawBitmap(piecesImage[1][1], dx, dy, paint);
				else if(animPieces==Pieces.HUNTER_W) canvas.drawBitmap(piecesImage[1][2], dx, dy, paint);
				else if(animPieces==Pieces.ROCK_W) canvas.drawBitmap(piecesImage[1][3], dx, dy, paint);
				else if(animPieces==Pieces.QUEEN_W) canvas.drawBitmap(piecesImage[1][4], dx, dy, paint);
				else if(animPieces==Pieces.KING_W) canvas.drawBitmap(piecesImage[1][5], dx, dy, paint);
			}
		}else{
			//For black
			
			//draw border
			paint.setColor(Color.rgb(40, 30, 10));
			canvas.drawRect(0, 0, width, width, paint);		
			paint.setColor(Color.rgb(150, 150, 150));
			paint.setTextSize(17);
			for(int i=0;i<8;++i){
				canvas.drawText(""+(char)('H'-i), i*m_im+(m_im/2)+m_bo, m_bo-2, paint);
				canvas.drawText(""+(char)('H'-i), i*m_im+(m_im/2)+m_bo, width-2, paint);
				canvas.drawText(""+(i+1), 2, i*m_im+(m_im/2)+m_bo, paint);
				canvas.drawText(""+(i+1), width-m_bo+2, i*m_im+(m_im/2)+m_bo, paint);
			}
			
			//draw board and selected and available
			for(int i=0;i<8;++i){
				for(int j=0;j<8;++j){
					if(i%2==j%2) canvas.drawBitmap(fieldImages[1], m_bo+m_im*i, m_bo+m_im*j, paint);
					else canvas.drawBitmap(fieldImages[0], m_bo+m_im*i, m_bo+m_im*j, paint);
					
					Pieces p=game.getPieces(7-i, 7-j);
					if(!(animation && (i==i1x && j==i1y || i==i2x && j==i2y))){
						if(p!=Pieces.FREE){
							if(p==Pieces.PAWN_B) canvas.drawBitmap(piecesImage[0][0], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.HORSE_B) canvas.drawBitmap(piecesImage[0][1], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.HUNTER_B) canvas.drawBitmap(piecesImage[0][2], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.ROCK_B) canvas.drawBitmap(piecesImage[0][3], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.QUEEN_B) canvas.drawBitmap(piecesImage[0][4], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.KING_B) canvas.drawBitmap(piecesImage[0][5], m_bo+m_im*i, m_bo+m_im*j, paint);
							
							else if(p==Pieces.PAWN_W) canvas.drawBitmap(piecesImage[1][0], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.HORSE_W) canvas.drawBitmap(piecesImage[1][1], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.HUNTER_W) canvas.drawBitmap(piecesImage[1][2], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.ROCK_W) canvas.drawBitmap(piecesImage[1][3], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.QUEEN_W) canvas.drawBitmap(piecesImage[1][4], m_bo+m_im*i, m_bo+m_im*j, paint);
							else if(p==Pieces.KING_W) canvas.drawBitmap(piecesImage[1][5], m_bo+m_im*i, m_bo+m_im*j, paint);
						}
					}	
					if(animation && i==i2x && j==i2y){
						if(eatedPieces==Pieces.PAWN_B) canvas.drawBitmap(piecesImage[0][0], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.HORSE_B) canvas.drawBitmap(piecesImage[0][1], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.HUNTER_B) canvas.drawBitmap(piecesImage[0][2], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.ROCK_B) canvas.drawBitmap(piecesImage[0][3], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.QUEEN_B) canvas.drawBitmap(piecesImage[0][4], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.KING_B) canvas.drawBitmap(piecesImage[0][5], m_bo+m_im*i, m_bo+m_im*j, paint);
						
						else if(eatedPieces==Pieces.PAWN_W) canvas.drawBitmap(piecesImage[1][0], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.HORSE_W) canvas.drawBitmap(piecesImage[1][1], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.HUNTER_W) canvas.drawBitmap(piecesImage[1][2], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.ROCK_W) canvas.drawBitmap(piecesImage[1][3], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.QUEEN_W) canvas.drawBitmap(piecesImage[1][4], m_bo+m_im*i, m_bo+m_im*j, paint);
						else if(eatedPieces==Pieces.KING_W) canvas.drawBitmap(piecesImage[1][5], m_bo+m_im*i, m_bo+m_im*j, paint);
					}
					
					if(game.isSelected() && (7-game.getSelectedX())==i && (7-game.getSelectedY())==j){
						paint.setColor(Color.GREEN);
						int d=5;
						paint.setStrokeWidth(d*2);
						canvas.drawLine(m_bo+i*m_im,   m_bo+j*m_im+d,   m_bo+(i+1)*m_im,   m_bo+j*m_im+d, paint);	//top
						canvas.drawLine(m_bo+i*m_im,   m_bo+j*m_im-d+m_im,   m_bo+(i+1)*m_im,   m_bo+j*m_im-d+m_im, paint);		//left			
						canvas.drawLine(m_bo+i*m_im+d,   m_bo+j*m_im+d,   m_bo+i*m_im+d,   m_bo+j*m_im-d+m_im, paint);	
						canvas.drawLine(m_bo+(i+1)*m_im-d,   m_bo+j*m_im+d,   m_bo+(i+1)*m_im-d,   m_bo+j*m_im-d+m_im, paint);		//bottom					
						
					}else if(game.isSelected() && game.isAllowed(7-i, 7-j)){
						paint.setColor(Color.rgb(0, 170, 170));
						canvas.drawCircle(m_bo+i*m_im+(m_im/2), m_bo+j*m_im+(m_im/2), m_im/6, paint);					
					}
					
				}
			}
			
			if(animation){
				if(animPieces==Pieces.PAWN_B) canvas.drawBitmap(piecesImage[0][0], dx, dy, paint);
				else if(animPieces==Pieces.HORSE_B) canvas.drawBitmap(piecesImage[0][1], dx, dy, paint);
				else if(animPieces==Pieces.HUNTER_B) canvas.drawBitmap(piecesImage[0][2], dx, dy, paint);
				else if(animPieces==Pieces.ROCK_B) canvas.drawBitmap(piecesImage[0][3], dx, dy, paint);
				else if(animPieces==Pieces.QUEEN_B) canvas.drawBitmap(piecesImage[0][4], dx, dy, paint);
				else if(animPieces==Pieces.KING_B) canvas.drawBitmap(piecesImage[0][5], dx, dy, paint);
				
				else if(animPieces==Pieces.PAWN_W) canvas.drawBitmap(piecesImage[1][0], dx, dy, paint);
				else if(animPieces==Pieces.HORSE_W) canvas.drawBitmap(piecesImage[1][1], dx, dy, paint);
				else if(animPieces==Pieces.HUNTER_W) canvas.drawBitmap(piecesImage[1][2], dx, dy, paint);
				else if(animPieces==Pieces.ROCK_W) canvas.drawBitmap(piecesImage[1][3], dx, dy, paint);
				else if(animPieces==Pieces.QUEEN_W) canvas.drawBitmap(piecesImage[1][4], dx, dy, paint);
				else if(animPieces==Pieces.KING_W) canvas.drawBitmap(piecesImage[1][5], dx, dy, paint);
			}
		}
		
		//draw last move
		if(lastMove && game.getUkMoveNUmber()!=0){
			paint.setColor(Color.rgb(100, 70, 200));
			int d=4;
			paint.setStrokeWidth(d*2);
			
			//1
			canvas.drawLine(m_bo+(7-i1x)*m_im,   m_bo+(7-i1y)*m_im+d,   m_bo+((7-i1x)+1)*m_im,   m_bo+(7-i1y)*m_im+d, paint);	//top
			canvas.drawLine(m_bo+(7-i1x)*m_im,   m_bo+(7-i1y)*m_im-d+m_im,   m_bo+((7-i1x)+1)*m_im,   m_bo+(7-i1y)*m_im-d+m_im, paint);		//left			
			canvas.drawLine(m_bo+(7-i1x)*m_im+d,   m_bo+(7-i1y)*m_im+d,   m_bo+(7-i1x)*m_im+d,   m_bo+(7-i1y)*m_im-d+m_im, paint);	
			canvas.drawLine(m_bo+((7-i1x)+1)*m_im-d,   m_bo+(7-i1y)*m_im+d,   m_bo+((7-i1x)+1)*m_im-d,   m_bo+(7-i1y)*m_im-d+m_im, paint);		//bottom			
			//2
			canvas.drawLine(m_bo+(7-i2x)*m_im,   m_bo+(7-i2y)*m_im+d,   m_bo+((7-i2x)+1)*m_im,   m_bo+(7-i2y)*m_im+d, paint);	//top
			canvas.drawLine(m_bo+(7-i2x)*m_im,   m_bo+(7-i2y)*m_im-d+m_im,   m_bo+((7-i2x)+1)*m_im,   m_bo+(7-i2y)*m_im-d+m_im, paint);		//left			
			canvas.drawLine(m_bo+(7-i2x)*m_im+d,   m_bo+(7-i2y)*m_im+d,   m_bo+(7-i2x)*m_im+d,   m_bo+(7-i2y)*m_im-d+m_im, paint);	
			canvas.drawLine(m_bo+((7-i2x)+1)*m_im-d,   m_bo+(7-i2y)*m_im+d,   m_bo+((7-i2x)+1)*m_im-d,   m_bo+(7-i2y)*m_im-d+m_im, paint);		//bottom			
		}
		
		if(game.getTurn()=='w') statusView.setTime(tw.getTsec());	
		else statusView.setTime(tb.getTsec());
		
	}
	
    @Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(width, m_bo*2+8*m_im);
	}
    
	@Override
	public boolean performClick() {
		return super.performClick();
	}
    
    class TouchListener implements OnTouchListener{

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			if(animation) return false;
			
			int tx=(int)(arg1.getX()-m_bo)/m_im;
			int ty=(int)(arg1.getY()-m_bo)/m_im;
			if(tx>7 || ty>7) return false;
			
			lastMove=false;
			
			if(!game.isSelected()){
				if(myColor=='w') game.select(tx, ty, myColor);
				else game.select(7-tx, 7-ty, myColor);
				
			}else{
						
				try {
					animation=true;	
					if(myColor=='w'){
						i1x=game.getSelectedX();
						i1y=game.getSelectedY();
						dx=i1x*m_im+m_bo;
						dy=i1y*m_im+m_bo;
						animPieces=game.getPieces(i1x, i1y);
						eatedPieces=game.getPieces(tx, ty);
						
						game.move(tx, ty, Pieces.QUEEN_B);	
						
						i2x=tx;
						i2y=ty;
						moveingX2=tx*m_im+m_bo;
						moveingY2=ty*m_im+m_bo;
					}else{
						i1x=7-game.getSelectedX();
						i1y=7-game.getSelectedY();
						dx=i1x*m_im+m_bo;
						dy=i1y*m_im+m_bo;
						animPieces=game.getPieces(7-i1x, 7-i1y);
						eatedPieces=game.getPieces(7-tx, 7-ty);
						
						game.move(7-tx, 7-ty, Pieces.QUEEN_B);	
						
						i2x=tx;
						i2y=ty;
						moveingX2=tx*m_im+m_bo;
						moveingY2=ty*m_im+m_bo;
					}
					
					if(game.getTurn()=='w'){
						tb.pauseT();
						tw.resumeT();
					}else{
						tw.pauseT();
						tb.resumeT();
					}
					
					
				} catch (IllegalMoveException e) {
					animation=false;
				}
			}
			
			arg0.performClick();
			return false;
		}	    	
    }
	

}
