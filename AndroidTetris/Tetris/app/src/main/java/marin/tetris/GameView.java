package marin.tetris;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable{
    private final Object monitor=new Object();

    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private Context c;
    private Bitmap[] icon= new Bitmap[7];
    private Bitmap[] iconScaled=new Bitmap[7];
    private Bitmap[] statusIcon=new Bitmap[3];
    private Bitmap imSound, imPause, imGameOver;
    private SoundManager soundManager;
    private TetrisBoard board;
    private SSNumber ssHighScore, ssScore, ssLevel;

    private boolean play;
    private boolean run;
    private int level;
    private int highScore;
    private boolean showNext;
    private boolean highScoreS;
    private boolean oneTime;

    private int orientation;
    private int height, width, padding, paddingSide;
    private int blockSize;


    public GameView(Context context){
        super(context);
        initialize(context);
    }

    public GameView(Context context, AttributeSet attrs){
        super(context, attrs);
        initialize(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        initialize(context);
    }

    private void initialize(Context context){
        c=context;
        paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        surfaceHolder=getHolder();
        soundManager=new SoundManager(context);
        board=new TetrisBoard(soundManager);

        icon[0]=BitmapFactory.decodeResource(getResources(),R.drawable.block_blue);
        icon[1]=BitmapFactory.decodeResource(getResources(),R.drawable.block_cyan);
        icon[2]=BitmapFactory.decodeResource(getResources(),R.drawable.block_red);
        icon[3]=BitmapFactory.decodeResource(getResources(),R.drawable.block_green);
        icon[4]=BitmapFactory.decodeResource(getResources(),R.drawable.block_yellow);
        icon[5]=BitmapFactory.decodeResource(getResources(),R.drawable.block_orange);
        icon[6]=BitmapFactory.decodeResource(getResources(),R.drawable.block_purple);

        statusIcon[0]=BitmapFactory.decodeResource(getResources(),R.drawable.sound);
        statusIcon[1]=BitmapFactory.decodeResource(getResources(),R.drawable.pause);
        statusIcon[2]=BitmapFactory.decodeResource(getResources(),R.drawable.game_over);

        play=false;
        showNext=true;
        level=1;
        run=true;
        highScoreS=false;
        oneTime=true;

        ssHighScore=new SSNumber(c, 7);
        ssScore=new SSNumber(c, 7);
        ssLevel=new SSNumber(c, 7);

        loadHighScore();
    }


    public void menu(){
        highScoreS=false;
        soundManager.playButton();
        synchronized (monitor){
            play=false;
            loadHighScore();
            soundManager.stopMusic();
            soundManager.stopHighScore();
        }
    }

    public void sound(){
        soundManager.playButton();
        synchronized (monitor){
            soundManager.setSoundStatus(!soundManager.getSoundStatus());
            if(play) soundManager.playMusic();
        }
    }

    public void pause(){
        soundManager.playButton();
        synchronized (monitor){
            if(play && !board.isGameOver()){
                board.setPause(!board.isPaused());
                if(board.isPaused()) soundManager.stopMusic();
                else soundManager.playMusic();
            }
        }
    }

    public void rotate(){
        soundManager.playButton();
        synchronized (monitor){
            if(play) board.rotate();
            else{
                board.reset(level-1, showNext);
                play=true;
                soundManager.playMusic();
                oneTime=true;
            }
        }
    }

    public void down(){
        soundManager.playButton();
        synchronized (monitor){
            if(play) board.down();
            else showNext=!showNext;
        }
    }

    public void left(){
        soundManager.playButton();
        synchronized (monitor){
            if(play) board.left();
        }

        if(!play){
            if(c.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
                ((MainActivity)c).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                ((MainActivity)c).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            }else{
                ((MainActivity)c).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                ((MainActivity)c).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            }
        }
    }

    public void right(){
        soundManager.playButton();
        synchronized (monitor){
            if(play) board.right();
            else{
                level=(++level)%11;
                if(level==0) level=1;
            }
        }
    }

    public void start(){
        Thread thread = new Thread(this);
        run=true;
        thread.start();
    }


    @Override
    public void run(){
        long time1, time2;
        while(run){
            synchronized(monitor){
                time1=System.currentTimeMillis();
                if(play) board.step();
                if(board.isGameOver() && oneTime){
                    oneTime=false;
                    if(board.getScore()>highScore){
                        highScoreS=true;
                        soundManager.playHighScore();
                    }else{
                        soundManager.playGameOver();
                    }
                    soundManager.stopMusic();
                    highScore=Math.max(board.getScore(), highScore);
                    storeHighScore();
                }
                render();
            }
            try{
                time2=50-(System.currentTimeMillis()-time1);
                if(time2>0) Thread.sleep(time2);
                //Log.d("XXXXXXXXXXXXXX", "Time: "+time2+" ms");
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void render(){
        Canvas canvas;
        if(surfaceHolder.getSurface().isValid()){
            canvas=surfaceHolder.lockCanvas();
            if(canvas==null) return;

            if(getResources().getConfiguration().orientation!=orientation){
                calculate(canvas);
                orientation=getResources().getConfiguration().orientation;
            }

            paint.setColor(Color.rgb(0x90, 0x90, 0xa0));
            canvas.drawRect(0, 0, width, height, paint);
            paint.setColor(Color.rgb(0x50, 0x50, 0x70));
            canvas.drawRect(paddingSide-7, padding-7, width-paddingSide+7, height-padding+7, paint);
            paint.setColor(Color.BLACK);
            canvas.drawRect(paddingSide, padding, width-paddingSide, height-padding, paint);
            paint.setColor(Color.rgb(0x20, 0x20, 0x30));
            paint.setStrokeWidth(5);
            canvas.drawLine(paddingSide+10*blockSize+5, padding, paddingSide+10*blockSize+5, height-padding, paint);

            paint.setColor(Color.rgb(0, 100, 0));
            paint.setTextSize(50);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("Tetris", width/2-60, 50, paint);

            paint.setColor(Color.rgb(255, 200, 10));
            paint.setTextSize(blockSize*0.7f);
            if(highScoreS) paint.setColor(Color.rgb(0, 255, 0));
            canvas.drawText("Hi-Score", paddingSide+10*blockSize+5+blockSize/2, padding+blockSize, paint);
            paint.setColor(Color.rgb(255, 200, 10));
            paint.setTextSize(blockSize*0.7f);
            canvas.drawText("Score", paddingSide+10*blockSize+5+blockSize/2, padding+4*blockSize, paint);
            paint.setTextSize(blockSize*0.7f);
            canvas.drawText("Next", paddingSide+10*blockSize+5+blockSize/2, padding+7*blockSize, paint);
            paint.setTextSize(blockSize*0.7f);
            canvas.drawText("Level", paddingSide+10*blockSize+5+blockSize/2, padding+13*blockSize, paint);

            if(orientation==Configuration.ORIENTATION_PORTRAIT) drawOutside(canvas, paint);

            if(soundManager.getSoundStatus()) canvas.drawBitmap(imSound, paddingSide+10*blockSize+5+blockSize/2, padding+15*blockSize, paint);

            if(!play){
                ssHighScore.drawNumber(highScore, paddingSide+10*blockSize+5+blockSize/2, padding+(3*blockSize)/2, canvas, paint);
                ssLevel.drawNumber(level, paddingSide+10*blockSize+5+blockSize/2, padding+13*blockSize+blockSize/2, canvas, paint);

                int[][] next=new int[][]{{0,0,0,0}, {0,1,1,0}, {0,1,1,0}, {0,0,0,0}};
                if(showNext) {
                    for (int y = 0; y < 4; ++y) {
                        for (int x = 0; x < 4; ++x) {
                            if (next[y][x] != 0)
                                canvas.drawBitmap(iconScaled[next[y][x]-1], paddingSide + 10 * blockSize + 5 + blockSize / 2 + x * blockSize, padding + 8 * blockSize + y * blockSize, paint);
                        }
                    }
                }

                int[][] sm=new int[][]{{0,0,0,1,0,0,1,0,0,0},
                                        {0,0,0,0,0,0,0,0,0,0},
                                        {0,0,1,0,0,0,0,1,0,0},
                                        {0,0,0,1,1,1,1,0,0,0}};

                for(int y=0;y<4;++y){
                    for(int x=0;x<10;++x){
                        if(sm[y][x]!=0)
                            canvas.drawBitmap(iconScaled[3], paddingSide+x*blockSize, padding+(y+7)*blockSize, paint);
                    }
                }

            }else{
                ssHighScore.drawNumber(highScore, paddingSide+10*blockSize+5+blockSize/2, padding+(3*blockSize)/2, canvas, paint);
                ssScore.drawNumber(board.getScore(), paddingSide+10*blockSize+5+blockSize/2, padding+4*blockSize+blockSize/2, canvas, paint);
                ssLevel.drawNumber(board.getLevel(), paddingSide+10*blockSize+5+blockSize/2, padding+13*blockSize+blockSize/2, canvas, paint);

                if(board.isPaused()) canvas.drawBitmap(imPause, paddingSide+10*blockSize+5+blockSize/2+2*blockSize, padding+15*blockSize, paint);
                if(board.isGameOver()) canvas.drawBitmap(imGameOver, paddingSide+10*blockSize+5+blockSize/2, padding+17*blockSize, paint);


                int[][] next=board.getNext();
                if(next!=null)
                for(int y=0;y<4;++y){
                    for(int x=0;x<4;++x){
                        if(next[y][x]!=0)
                            canvas.drawBitmap(iconScaled[next[y][x]-1], paddingSide+10*blockSize+5+blockSize/2+x*blockSize, padding+8*blockSize+y*blockSize, paint);
                    }
                }

                for(int y=0;y<20;++y){
                    for(int x=0;x<10;++x){
                        if(board.getCell(x, y)!=0)
                            canvas.drawBitmap(iconScaled[board.getCell(x, y)-1], paddingSide+x*blockSize, padding+y*blockSize, paint);
                    }
                }

                int[][] current=board.getCurrentType();
                int cx=board.getCurrentX();
                int cy=board.getCurrentY();
                for(int y=0;y<4;++y){
                    for(int x=0;x<4;++x){
                        if(current[y][x]!=0)
                            canvas.drawBitmap(iconScaled[current[y][x]-1], paddingSide+cx*blockSize+x*blockSize, padding+cy*blockSize+y*blockSize, paint);
                    }
                }
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawOutside(Canvas canvas, Paint paint){
        int psl=paddingSide-10;
        int pt=padding-7;
        int psr=width-paddingSide+10;

        //1
        canvas.drawBitmap(iconScaled[4], psl-blockSize*2, pt, paint);
        canvas.drawBitmap(iconScaled[4], psl-blockSize, pt, paint);
        canvas.drawBitmap(iconScaled[4], psl-blockSize, pt+blockSize, paint);
        canvas.drawBitmap(iconScaled[4], psl-blockSize, pt+blockSize*2, paint);

        canvas.drawBitmap(iconScaled[4], psr+blockSize, pt, paint);
        canvas.drawBitmap(iconScaled[4], psr, pt, paint);
        canvas.drawBitmap(iconScaled[4], psr, pt+blockSize, paint);
        canvas.drawBitmap(iconScaled[4], psr, pt+blockSize*2, paint);

        pt=pt+blockSize*4;
        //2
        canvas.drawBitmap(iconScaled[6], psl-blockSize*2, pt+blockSize, paint);
        canvas.drawBitmap(iconScaled[6], psl-blockSize, pt, paint);
        canvas.drawBitmap(iconScaled[6], psl-blockSize, pt+blockSize, paint);
        canvas.drawBitmap(iconScaled[6], psl-blockSize, pt+blockSize*2, paint);

        canvas.drawBitmap(iconScaled[6], psr+blockSize, pt+blockSize, paint);
        canvas.drawBitmap(iconScaled[6], psr, pt, paint);
        canvas.drawBitmap(iconScaled[6], psr, pt+blockSize, paint);
        canvas.drawBitmap(iconScaled[6], psr, pt+blockSize*2, paint);

        pt=pt+blockSize*4;
        //3
        canvas.drawBitmap(iconScaled[5], psl-blockSize*2, pt+blockSize*2, paint);
        canvas.drawBitmap(iconScaled[5], psl-blockSize, pt, paint);
        canvas.drawBitmap(iconScaled[5], psl-blockSize, pt+blockSize, paint);
        canvas.drawBitmap(iconScaled[5], psl-blockSize, pt+blockSize*2, paint);

        canvas.drawBitmap(iconScaled[5], psr+blockSize, pt+blockSize*2, paint);
        canvas.drawBitmap(iconScaled[5], psr, pt, paint);
        canvas.drawBitmap(iconScaled[5], psr, pt+blockSize, paint);
        canvas.drawBitmap(iconScaled[5], psr, pt+blockSize*2, paint);

        pt=pt+blockSize*4;
        //4
        canvas.drawBitmap(iconScaled[2], psl-blockSize*2, pt, paint);
        canvas.drawBitmap(iconScaled[2], psl-blockSize*2, pt+blockSize, paint);
        canvas.drawBitmap(iconScaled[2], psl-blockSize, pt+blockSize, paint);
        canvas.drawBitmap(iconScaled[2], psl-blockSize, pt+blockSize*2, paint);

        canvas.drawBitmap(iconScaled[2], psr+blockSize, pt, paint);
        canvas.drawBitmap(iconScaled[2], psr+blockSize, pt+blockSize, paint);
        canvas.drawBitmap(iconScaled[2], psr, pt+blockSize, paint);
        canvas.drawBitmap(iconScaled[2], psr, pt+blockSize*2, paint);

        pt=pt+blockSize*4;
        //5
        canvas.drawBitmap(iconScaled[1], psl-blockSize, pt, paint);
        canvas.drawBitmap(iconScaled[1], psl-blockSize, pt+blockSize, paint);
        canvas.drawBitmap(iconScaled[1], psl-blockSize, pt+blockSize*2, paint);
        canvas.drawBitmap(iconScaled[1], psl-blockSize, pt+blockSize*3, paint);

        canvas.drawBitmap(iconScaled[1], psr, pt, paint);
        canvas.drawBitmap(iconScaled[1], psr, pt+blockSize, paint);
        canvas.drawBitmap(iconScaled[1], psr, pt+blockSize*2, paint);
        canvas.drawBitmap(iconScaled[1], psr, pt+blockSize*3, paint);
    }

    private void calculate(Canvas canvas){
        int windowHeight, windowWidth;

        height=canvas.getHeight();
        width=canvas.getWidth();
        padding=(int)Math.round(height*0.1);
        windowHeight=height-2*padding;
        windowWidth=(int)Math.round(windowHeight*0.75);
        paddingSide=width/2-windowWidth/2;
        blockSize=windowHeight/20;
        padding=(height-20*blockSize)/2;

        imSound=Bitmap.createScaledBitmap(statusIcon[0], 2*blockSize, 2*blockSize, false);
        imPause=Bitmap.createScaledBitmap(statusIcon[1], 2*blockSize, 2*blockSize, false);
        imGameOver=Bitmap.createScaledBitmap(statusIcon[2], 4*blockSize, 3*blockSize, false);

        for(int i=0;i<7;++i){
            iconScaled[i]=Bitmap.createScaledBitmap(icon[i], blockSize, blockSize, false);
        }

        ssHighScore.setDigitSize(blockSize/2+2, blockSize);
        ssScore.setDigitSize(blockSize/2+2, blockSize);
        ssLevel.setDigitSize(blockSize/2+2, blockSize);
    }

    private void loadHighScore(){
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(c);
        highScore=sp.getInt("HIGH_SCORE", 0);
    }

    private void storeHighScore(){
        SharedPreferences.Editor ed=PreferenceManager.getDefaultSharedPreferences(c).edit();
        ed.putInt("HIGH_SCORE", highScore);
        ed.apply();
    }
}
