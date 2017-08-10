package marin.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

class DataBase extends SQLiteOpenHelper{
    private static final String DATABASE_NAME="Notes.db";
    private static final String NOTES_TABLE_NAME="Notes";
    private static final String COLUMN_ID="id";
    private static final String COLUMN_COLOR="color";
    private static final String COLUMN_DATE="date";
    private static final String COLUMN_TEXT="text";
    private static final String COLUMN_HIDDEN="hidden";


    DataBase(Context context){
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+NOTES_TABLE_NAME+"("+
                    COLUMN_ID+" INTEGER primary key, "+
                    COLUMN_COLOR+" INTEGER, "+
                    COLUMN_DATE+" TEXT, "+
                    COLUMN_HIDDEN+" INTEGER, "+
                    COLUMN_TEXT+" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+NOTES_TABLE_NAME);
        onCreate(db);
    }


    void insertNote(Note note){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_ID, note.getId());
        contentValues.put(COLUMN_COLOR, note.getColor());
        contentValues.put(COLUMN_DATE, note.getDate());
        contentValues.put(COLUMN_TEXT, note.getText());
        contentValues.put(COLUMN_HIDDEN, note.isHidden());
        db.insert(NOTES_TABLE_NAME, null, contentValues);
    }

    Note getNote(long id) {
        SQLiteDatabase db=getReadableDatabase();
        Cursor res=db.rawQuery("select * from "+NOTES_TABLE_NAME+" where "+COLUMN_ID+"="+id+"", null);
        res.moveToFirst();
        if(res.getCount()<=0){
            res.close();
            return null;
        }
        Note nt=new Note();
        nt.setId(res.getLong(res.getColumnIndex(COLUMN_ID)));
        nt.setColor(res.getInt(res.getColumnIndex(COLUMN_COLOR)));
        nt.setDate(res.getString(res.getColumnIndex(COLUMN_DATE)));
        nt.setHidden(res.getInt(res.getColumnIndex(COLUMN_HIDDEN))>0);
        nt.setText(res.getString(res.getColumnIndex(COLUMN_TEXT)));
        res.close();
        return nt;
    }

    ArrayList<Note> getAllNotes(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor res=db.rawQuery( "select * from "+NOTES_TABLE_NAME+"", null );
        res.moveToFirst();

        ArrayList<Note> list=new ArrayList<>();
        Note nt;
        while(!res.isAfterLast()){
            nt=new Note();
            nt.setId(res.getLong(res.getColumnIndex(COLUMN_ID)));
            nt.setColor(res.getInt(res.getColumnIndex(COLUMN_COLOR)));
            nt.setDate(res.getString(res.getColumnIndex(COLUMN_DATE)));
            nt.setHidden(res.getInt(res.getColumnIndex(COLUMN_HIDDEN))>0);
            nt.setText(res.getString(res.getColumnIndex(COLUMN_TEXT)));
            list.add(nt);
            res.moveToNext();
        }
        res.close();
        return list;
    }

    void updateNote(Note note) {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_ID, note.getId());
        contentValues.put(COLUMN_COLOR, note.getColor());
        contentValues.put(COLUMN_DATE, note.getDate());
        contentValues.put(COLUMN_TEXT, note.getText());
        contentValues.put(COLUMN_HIDDEN, note.isHidden());
        db.update(NOTES_TABLE_NAME, contentValues, "id = ? ", new String[] {Long.toString(note.getId())} );
    }

    void deleteNote(Note note) {
        SQLiteDatabase db=getWritableDatabase();
        db.delete(NOTES_TABLE_NAME, "id = ? ", new String[] {Long.toString(note.getId())} );
    }

    void toFile(MainActivity activity){
        SQLiteDatabase db=getReadableDatabase();
        Cursor res=db.rawQuery( "select * from "+NOTES_TABLE_NAME+"", null );
        res.moveToFirst();

        String dir=Environment.getExternalStorageDirectory().getAbsolutePath();
        File file=new File(dir+"/NotesBackup"+System.currentTimeMillis()+".txt");
        try{
            if(!file.createNewFile()){
                Toast.makeText(activity, "File already exist", Toast.LENGTH_SHORT).show();
                res.close();
                return;
            }
            FileWriter fwriter=new FileWriter(file);
            BufferedWriter out=new BufferedWriter(fwriter);

            out.write("Notes backup\n\n");
            while(!res.isAfterLast()){
                out.write("#Date:"+res.getString(res.getColumnIndex(COLUMN_DATE))+"\n");
                out.write(res.getString(res.getColumnIndex(COLUMN_TEXT))+"\n\n");
                res.moveToNext();
            }
            res.close();
            out.close();
            fwriter.close();
        }catch(IOException e){
            res.close();
            Toast.makeText(activity, "Write to file error", Toast.LENGTH_SHORT).show();
        }
    }
}
