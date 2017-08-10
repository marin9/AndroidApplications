package marin.notes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class Note {
    static final int GRAY=0;
    static final int RED=1;
    static final int BLUE=2;
    static final int GREEN=3;
    static final int YELLOW=4;

    private long id;
    private int color;
    private String text;
    private String date;
    private boolean hidden;

    Note(){
        id=System.currentTimeMillis();
        color=GRAY;
        text="";
        SimpleDateFormat sdf=new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH);
        date=sdf.format(new Date());
        hidden=false;
    }


    void setId(long nid){
        id=nid;
    }

    void setColor(int ncolor){
        color=ncolor;
    }

    void setDate(String ndate){
        date=ndate;
    }

    void setText(String ntext){
        text=ntext;
    }

    void setHidden(boolean hide_on){
        hidden=hide_on;
    }


    long getId(){
        return id;
    }

    int getColor(){
        return color;
    }

    String getText(){
        return text;
    }

    String getDate(){
        return date;
    }

    boolean isHidden(){
        return hidden;
    }
}
