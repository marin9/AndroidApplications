package marin.bralic.calc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class Display extends View implements IDisplay{
	private static int WINDOW_LENGTH=20;
	private Paint paint;
	private Typeface bold;
	
	private String text;
	private int[] window;
	private boolean scfMod, rad, shift, error;
	private Mod mod;
	private int open_bracket, round, cursor, up_down, abc_select;
	private String x1, x2;
	private boolean x1x2;
	
	
	public Display(Context context){
		super(context);
		initialize();		
	}
	
	public Display(Context context, AttributeSet attrs){
		super(context, attrs);
		initialize();
	}
	
	public Display(Context context, AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
        initialize();
	}
	
	private void initialize(){
		setPadding(20, 20, 20, 10);
		paint=new Paint();
		round=-1;
		cursor=0;
		text="";
		scfMod=false;
		up_down=-1;
		window=new int[]{0, WINDOW_LENGTH};
	}


	@Override
	protected void onDraw(Canvas canvas) {
		int h=getHeight(), w=getWidth(), n=5, m=2;
		
		canvas.drawColor(Color.BLACK);	
		paint.setColor(Color.GRAY);
		canvas.drawRoundRect(m, m, w-m, h-m, 10, 10, paint);
		paint.setColor(Color.BLACK);
		canvas.drawRoundRect(m+n, m+n, w-n-m, h-n-m, 10, 10, paint);	
			
		bold=Typeface.create(paint.getTypeface(), Typeface.BOLD);
		paint.setTypeface(bold);
		paint.setTextSize(20);

		if(shift){			
			paint.setColor(Color.rgb(255, 170, 0));
			canvas.drawText("S", 30, 27, paint);
		}	
		
		if(open_bracket!=0){
			paint.setColor(Color.RED);
			canvas.drawText("("+open_bracket+")", 50, 27, paint);
		}
		
		if(scfMod){
			if(rad){
				paint.setColor(Color.GREEN);
				canvas.drawText("RAD", 100, 27, paint);
			}else{
				paint.setColor(Color.rgb(50, 50, 255));
				canvas.drawText("DEG", 145, 27, paint);
			}
		}
		
		if(round!=-1){
			paint.setColor(Color.rgb(255, 170, 0));
			canvas.drawText(". "+round, 200, 27, paint);
		}	
		
		if(error){
			paint.setColor(Color.rgb(230, 170, 0));
			canvas.drawText("Error", 245, 27, paint);
		}
		
		paint.setTextSize(23);
		paint.setColor(Color.GREEN);
		if(mod==Mod.EQU) canvas.drawText("ax\u00B2+bx+c", 300, 29, paint);
		
		if(mod==Mod.EQU){
			paint.setColor(Color.rgb(255, 170, 0));
			if(abc_select==1) canvas.drawText("a=", 410, 29, paint); 
			else if(abc_select==2) canvas.drawText("b=", 410, 29, paint); 
			else if(abc_select==3) canvas.drawText("c=", 410, 29, paint); 
		}
		
		if(mod!=Mod.EQU){ 
			paint.setColor(Color.rgb(50, 50, 255));
			if(up_down==2){//up and down
				canvas.drawText("\u25B2", 440, 27, paint);
				canvas.drawText("\u25BC", 460, 27, paint);
			}
			else if(up_down==1) canvas.drawText("\u25B2", 440, 27, paint);//up
			else if(up_down==0) canvas.drawText("\u25BC", 460, 27, paint);//down
		}				
		
		//draw text		
		paint.setColor(Color.CYAN);
		
		if(x1x2){
			paint.setTextSize(40);
			canvas.drawText("x\u2081 = "+x1, 25, 65, paint);
			canvas.drawText("x\u2082= "+x2, 25, 100, paint);
			x1x2=false;
		}else{
			if(paint.measureText(text)<250){		
				if(paint.measureText(text)<170) paint.setTextSize(70);
				else paint.setTextSize(50);
				
				canvas.drawText(text, 25, 90, paint);		
				paint.setColor(Color.GRAY);
				canvas.drawText("|", 25+paint.measureText(text.substring(0, cursor))-5, 90, paint);		
				
				window[0]=0;
				window[1]=WINDOW_LENGTH;
			}else{
				paint.setTextSize(50);			
				String show_text=text.substring(window[0], window[1]);
							
				if(window[0]==0) canvas.drawText(show_text+"\u25BA", 25, 90, paint);		
				else if(window[1]==text.length()) canvas.drawText("\u25C4"+show_text, 25, 90, paint);	
				else canvas.drawText("\u25C4"+show_text+"\u25BA", 25, 90, paint);	
				
				paint.setColor(Color.GRAY);
				if(window[0]==0) canvas.drawText("|", 25+paint.measureText(text.substring(window[0], cursor))-5, 90, paint);
				else canvas.drawText("|", 25+paint.measureText(text.substring(window[0], cursor)+1), 90, paint);
			}
		}	

		super.onDraw(canvas);
	}
	
	@Override
	public void setScfMod(boolean scf){
		scfMod=scf;
		invalidate();
	}
  
	@Override
	public void setShift(boolean shf) {
		shift=shf;
		invalidate();
	}

	@Override
	public void setRad(boolean rad) {
		this.rad=rad;
		invalidate();
	}

	@Override
	public void setRound(int n) {
		round=n;
		invalidate();
	}
	
	@Override
	public void setError(boolean tf) {
		error=tf;
		invalidate();
	}

	@Override
	public void setMod(Mod m) {
		mod=m;
		if(mod==Mod.EQU) abc_select=1;
		invalidate();
	}
	
	@Override
	public void addText(String t){		
	    if(t.contains("(") && !t.contains(")")) ++open_bracket;
		else if(t.contains(")") && !t.contains("(")) --open_bracket;
		
		String s1=text.substring(0, cursor);
		String s2=text.substring(cursor, text.length());
		
		text=s1+t+s2;
		cursor=cursor+t.length();
		
		if(cursor>WINDOW_LENGTH){
			window[0]=cursor-WINDOW_LENGTH;		
			window[1]=cursor;			
		}
		
		invalidate();
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void left() {
		if(cursor>0){
			--cursor;
			if(cursor<=window[0] && window[0]!=0){
				--window[0];
				--window[1];
			}
			
			
			String last1=null, last2=null;
			try{ 
				last1=text.substring(cursor-1, cursor); 
				last2=text.substring(cursor-2, cursor-1); 
			}catch(Exception e){}
			char c='\0', c2='\0';
			
			if(last1!=null){
				c=last1.charAt(0);
				try{ c2=last2.charAt(0); }catch(Exception e){c2='\0';}
				if(last1!=null && (c>='a' && c<='z' && c2!='n' || c=='\u221A' || c=='^')){
					left();
				}
			}			
			
			invalidate();
		}
	}

	@Override
	public void right() {
		if(cursor<getText().length()){
			++cursor;
			if(cursor>window[1]){
				++window[0];
				++window[1];
			}
			
			String last=null;
			try{ last=text.substring(cursor-1, cursor); }catch(Exception e){}
			char c='\0';
			
			if(last!=null){
				c=last.charAt(0);
				if(last!=null && (c>='a' && c<='z' || c=='\u221A' || c=='^')){
					right();
				}
			}
			
			invalidate();
		}
	}

	@Override
	public void back() {
		if(cursor>0){
			String s1=text.substring(0, cursor-1);
			String last=null;
			try{ last=text.substring(cursor-2, cursor-1); }catch(Exception e){}
			String deleted_char=text.substring(cursor-1, cursor);
			String s2=text.substring(cursor, text.length());
			text=s1+s2;
			--cursor;
		 	
			if(window[0]!=0){
				--window[0];
				--window[1];	
			}					
			
			char c='\0';
			if(last!=null) c=last.charAt(0);
			
			if(deleted_char.equals("(")){
				--open_bracket;				
				if(last!=null && ((c=='\u221A' || c=='^'))) back();
			}else if(deleted_char.equals(")")) ++open_bracket;
			
			if(last!=null && (c>='a' && c<='z')) back();
			
			invalidate();
		}	
	}

	@Override
	public void clear() {
		cursor=open_bracket=0;
		text="";
		window[0]=0;
		window[1]=WINDOW_LENGTH;
		invalidate();
	}

	@Override
	public void setText(String t) {
		text=t;
		open_bracket=0;
		cursor=t.length();
		window[1]=t.length();
		window[0]=t.length()-WINDOW_LENGTH;
		invalidate();
	}

	@Override
	public void setToBegin() {
		cursor=0;
		window[0]=0;
		window[1]=WINDOW_LENGTH;
		invalidate();
	}

	@Override
	public void setToEnd() {
		cursor=text.length();
		window[1]=cursor;
		window[0]=cursor-WINDOW_LENGTH;
		invalidate();
	}

	@Override
	public int getBracket() {
		return open_bracket;
	}	
	
	@Override
	public void setUpDown(int s) {
		up_down=s;
		invalidate();
	}
	
	public static enum Mod{
		N(), EQU();
	}

	@Override
	public void setVarSelected(int n) {
		abc_select=n;
		invalidate();
	}
	
	@Override
	public void setDoubleAns(String x1, String x2){
		x1x2=true;
		this.x1=x1;
		this.x2=x2;
		invalidate();
	}
	
}
