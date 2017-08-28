package marin.tetris;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

class SSNumber {
    private Context context;
    private Bitmap[] images;
    private Bitmap[] scaledImages;
    private int digitNum;
    private int dwidth;


    SSNumber(Context c, int digit_num){
        context=c;
        images=new Bitmap[10];
        scaledImages=new Bitmap[10];
        digitNum=digit_num;
        loadImages();
    }

    private void loadImages(){
        Bitmap img= BitmapFactory.decodeResource(context.getResources(),R.drawable.numbers);
        int w=img.getWidth()/10;
        int h=img.getHeight();

        for(int i=0;i<10;++i){
            images[i]=Bitmap.createBitmap(img, i*39, 0, w, h);

        }
        setDigitSize(50, 100);
    }

    void setDigitSize(int width, int height){
        dwidth=width;
        for(int i=0;i<10;++i){
            scaledImages[i]=Bitmap.createScaledBitmap(images[i], width, height, false);
        }
    }

    void drawNumber(int n, int x, int y, Canvas canvas, Paint paint){
        char[] num=(""+n).toCharArray();
        int len=num.length;
        int offset=(digitNum-len)*dwidth;

        for(int i=0;i<len;++i){
            if(i<len) canvas.drawBitmap(scaledImages[num[i]-48], x+offset+i*dwidth, y, paint);
        }
    }
}
