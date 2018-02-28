package bralic.marin.notes;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static String PASSWORD="Password";
    private static String LOCK="Lock";
    private SharedPreferences prefs;
    private String password;

    private DataBase dataBase;
    private ListView listView;
    private ArrayList<Note> items;
    private ArrayAdapter<Note> adapter;
    private boolean select;
    private boolean showPrivate;
    private Note note;
    private EditText text;

    private Button buttonColor;
    private PopupWindow menu;
    private Dialog dialog;

    private static int VIEW_MAIN=0;
    private static int VIEW_EDIT=1;
    private int view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs=getSharedPreferences("Settings", MODE_PRIVATE);
        password=prefs.getString(PASSWORD, null);

        dataBase=new DataBase(this);
        items=new ArrayList<>();
        adapter=new ListAdapter();

        boolean lock=prefs.getBoolean(LOCK, false);
        if(lock && password!=null && !password.equals("")){
            setContentView(R.layout.login);
        }else{
            setMainContentView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String permissions[], @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    dataBase.toFile(this);
                    Toast.makeText(this, "Backup finish", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        while(menu.isShowing()) menu.dismiss();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed(){
        if(menu!=null && menu.isShowing()){
            while(menu.isShowing()) menu.dismiss();
        }else if(view==VIEW_MAIN){
            while(menu!=null && menu.isShowing()) menu.dismiss();
            super.onBackPressed();
        }else if(view==VIEW_EDIT){
            setMainContentView();
        }
    }



    public void showMenu(View view){
        String tag=(String)view.getTag();
        LayoutInflater layoutInflater=(LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView;

        switch(tag){
            case "main_menu":
                hideKeyboard(view);
                if(select) popupView=layoutInflater.inflate(R.layout.menu_select, (ViewGroup)view.getParent(), false);
                else{
                    popupView=layoutInflater.inflate(R.layout.menu, (ViewGroup)view.getParent(), false);
                    Button btn=popupView.findViewById(R.id.button_menu_show);
                    if(showPrivate) btn.setText("Hide\nprivate");
                    else btn.setText("Show\nprivate");
                }
                break;

            case "color_menu":
                hideKeyboard(view);
                text.clearFocus();
                popupView=layoutInflater.inflate(R.layout.menu_color, (ViewGroup)view.getParent(), false);
                break;

            case "item_menu":
                hideKeyboard(view);
                text.clearFocus();
                popupView=layoutInflater.inflate(R.layout.menu_item, (ViewGroup)view.getParent(), false);

                Button buttonLock=popupView.findViewById(R.id.button_item_lock);
                if(note.isHidden()){
                    buttonLock.setText("Unlock");
                    int imgResource=R.drawable.unlock_item;
                    buttonLock.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                }else{
                    buttonLock.setText("Lock");
                    int imgResource=R.drawable.lock_item;
                    buttonLock.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                }
                break;

            default:
                return;
        }
        menu=new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        menu.setOutsideTouchable(true);
        menu.showAsDropDown(view, 0, 0);
    }

    public void addNewNotes(View view){
        select=false;
        note=new Note();
        openNote();
        showKeyboard(text);
    }

    public void openNote(){
        setContentView(R.layout.edit);
        view=VIEW_EDIT;
        buttonColor=(Button)findViewById(R.id.button_color);
        text=(EditText)findViewById(R.id.editText);

        int c=note.getColor();
        if(c==Note.RED){
            text.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_text_red, null));
            buttonColor.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_red, null));
        }else if(c==Note.BLUE){
            text.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_text_blue, null));
            buttonColor.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_blue, null));
        }else if(c==Note.GREEN){
            text.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_text_green, null));
            buttonColor.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_green, null));
        }else if(c==Note.YELLOW){
            text.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_text_yellow, null));
            buttonColor.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_yellow, null));
        }else if(c==Note.GRAY){
            text.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_text_gray, null));
            buttonColor.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_gray, null));
        }
        if(note.isHidden()) buttonColor.setText("\u26bf");
        else buttonColor.setText("");
        text.setText(note.getText());
    }

    public void dialogClose(View view){
        hideKeyboard(view);
        dialog.dismiss();
    }

    public void dialogOk(View view){
        String tag=(String)view.getTag();

        switch(tag){
            case "login":
                hideKeyboard(view);
                EditText input=(EditText)findViewById(R.id.editText_login);
                if(input.getText().toString().equals(password)){
                    SharedPreferences.Editor editor=prefs.edit();
                    editor.putBoolean(LOCK, false);
                    editor.apply();
                    setMainContentView();
                }else{
                    Toast.makeText(MainActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                }
                break;

            case "set_new_password":
                EditText inputOld=dialog.findViewById(R.id.editText_old_pass);
                EditText inputNew1=dialog.findViewById(R.id.editText_new_pass);
                EditText inputNew2=dialog.findViewById(R.id.editText_new_pass2);

                if(!inputOld.getText().toString().equals(password) && password!=null){
                    Toast.makeText(MainActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                }else if(!inputNew1.getText().toString().equals(inputNew2.getText().toString())){
                    Toast.makeText(MainActivity.this, "New passwords are different", Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferences.Editor editor=prefs.edit();
                    password=inputNew2.getText().toString();
                    editor.putString(PASSWORD, password);
                    editor.apply();
                    hideKeyboard(view);
                    Toast.makeText(MainActivity.this, "Password changed", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                break;

            case "backup":
                EditText et_pass=dialog.findViewById(R.id.editText_confirm_pass);
                String pass=et_pass.getText().toString();
                if(!pass.equals(password)){
                    Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
                    return;
                }
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                hideKeyboard(view);
                dialog.dismiss();
                break;

            case "delete_current":
                dataBase.deleteNote(note);
                dialog.dismiss();
                Toast.makeText(this, "Note removed", Toast.LENGTH_SHORT).show();
                setMainContentView();
                break;

            case "delete_selected":
                int len=listView.getCount();
                SparseBooleanArray checked=listView.getCheckedItemPositions();

                for(int i=0;i<len;++i){
                    if(checked.get(i)){
                        Note item=items.get(i);
                        dataBase.deleteNote(item);
                    }
                }
                select=false;
                dialog.dismiss();
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                listView.requestLayout();
                listView.clearChoices();
                loadData();
                break;

            case "private_mod_on":
                showPrivate =true;
                loadData();
                hideKeyboard(view);
                dialog.dismiss();
        }
    }

    public void showDialog(View view){
        String tag=(String)view.getTag();

        switch(tag){
            case "delete_current":
                dialog=new Dialog(this);
                dialog.setContentView(R.layout.confirm);
                dialog.setCancelable(false);
                menu.dismiss();
                dialog.show();
                dialog.findViewById(R.id.button_dialog_ok).setTag("delete_current");
                break;

            case "delete_selected":
                dialog=new Dialog(this);
                dialog.setContentView(R.layout.confirm);
                dialog.setCancelable(false);
                menu.dismiss();
                dialog.show();
                dialog.findViewById(R.id.button_dialog_ok).setTag("delete_selected");
                break;

            case "change_password":
                dialog=new Dialog(this);
                dialog.setContentView(R.layout.confirm_changepass);
                dialog.setCancelable(false);
                menu.dismiss();
                dialog.show();
                dialog.findViewById(R.id.button_dialog_ok).setTag("set_new_password");
                showKeyboard(dialog.findViewById(R.id.editText_old_pass));
                break;

            case "private_mod":
                if(!showPrivate){
                    dialog=new Dialog(this);
                    dialog.setContentView(R.layout.confirm_pass);
                    dialog.setCancelable(false);
                    menu.dismiss();
                    dialog.show();
                    dialog.findViewById(R.id.button_dialog_ok).setTag("private_mod_on");
                    showKeyboard(dialog.findViewById(R.id.editText_confirm_pass));
                }else{
                    showPrivate =false;
                    menu.dismiss();
                    loadData();
                }
                break;

            case "backup":
                dialog=new Dialog(this);
                dialog.setContentView(R.layout.confirm_pass);
                dialog.setCancelable(false);
                menu.dismiss();
                dialog.show();
                dialog.findViewById(R.id.button_dialog_ok).setTag("backup");
                showKeyboard(dialog.findViewById(R.id.editText_confirm_pass));
                break;
        }
    }


    public void back(View view){
        hideKeyboard(view);
        setMainContentView();
    }

    public void save(View view){
        note.setText(text.getText().toString());
        if(dataBase.getNote(note.getId())==null) dataBase.insertNote(note);
        else dataBase.updateNote(note);
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        back(view);
    }

    public void setColor(View view){
        String tag=(String)view.getTag();
        Drawable drawableText, drawableButton;

        switch (tag){
            case "red":
                drawableText=ResourcesCompat.getDrawable(getResources(), R.drawable.selector_text_red, null);
                drawableButton=ResourcesCompat.getDrawable(getResources(), R.drawable.selector_red, null);
                note.setColor(Note.RED);
                break;

            case "blue":
                drawableText=ResourcesCompat.getDrawable(getResources(), R.drawable.selector_text_blue, null);
                drawableButton=ResourcesCompat.getDrawable(getResources(), R.drawable.selector_blue, null);
                note.setColor(Note.BLUE);
                break;

            case "green":
                drawableText=ResourcesCompat.getDrawable(getResources(), R.drawable.selector_text_green, null);
                drawableButton=ResourcesCompat.getDrawable(getResources(), R.drawable.selector_green, null);
                note.setColor(Note.GREEN);
                break;

            case "yellow":
                drawableText=ResourcesCompat.getDrawable(getResources(), R.drawable.selector_text_yellow, null);
                drawableButton=ResourcesCompat.getDrawable(getResources(), R.drawable.selector_yellow, null);
                note.setColor(Note.YELLOW);
                break;

            default:
                drawableText=ResourcesCompat.getDrawable(getResources(), R.drawable.selector_text_gray, null);
                drawableButton=ResourcesCompat.getDrawable(getResources(), R.drawable.selector_gray, null);
                note.setColor(Note.GRAY);
        }
        buttonColor.setBackground(drawableButton);
        text.setBackground(drawableText);
        menu.dismiss();
    }

    public void setHidden(View view){
        note.setHidden(!note.isHidden());
        if(note.isHidden()) buttonColor.setText("\u26bf");
        else buttonColor.setText("");
        menu.dismiss();
    }

    public void select(View view){
        select=!select;
        if(select) listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        else{
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.requestLayout();
            listView.clearChoices();
        }
        menu.dismiss();
    }

    public void lock(View view){
        SharedPreferences.Editor editor=prefs.edit();
        editor.putBoolean(LOCK, true);
        editor.apply();
        finish();
    }

    public void lockSelected(View view){
        int len=listView.getCount();
        SparseBooleanArray checked=listView.getCheckedItemPositions();

        for(int i=0;i<len;++i){
            if(checked.get(i)){
                Note item=items.get(i);
                item.setHidden(true);
                dataBase.updateNote(item);
            }
        }
        select=false;
        menu.dismiss();
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.requestLayout();
        listView.clearChoices();
        loadData();
    }

    public void unlockSelected(View view){
        int len=listView.getCount();
        SparseBooleanArray checked=listView.getCheckedItemPositions();

        for(int i=0;i<len;++i){
            if(checked.get(i)){
                Note item=items.get(i);
                item.setHidden(false);
                dataBase.updateNote(item);
            }
        }
        select=false;
        menu.dismiss();
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.requestLayout();
        listView.clearChoices();
        loadData();
    }


    private class ListAdapter extends ArrayAdapter<Note>{
        ListAdapter() {
            super(MainActivity.this, R.layout.list_item, items);
        }

        @Override
        @NonNull
        public View getView(int position, View view, @NonNull  ViewGroup parent){
            if(view==null) view=MainActivity.this.getLayoutInflater().inflate(R.layout.list_item, parent, false);
            Note currentNote=items.get(position);

            int c=currentNote.getColor();
            if(c==Note.RED) view.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_red, null));
            else if(c==Note.GREEN) view.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_green, null));
            else if(c==Note.BLUE) view.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_blue, null));
            else if(c==Note.YELLOW) view.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_yellow, null));
            else view.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_gray, null));

            TextView dateView=view.findViewById(R.id.item_date);
            if(currentNote.isHidden()) dateView.setText(""+currentNote.getDate()+" \u26bf");
            else dateView.setText(currentNote.getDate());

            TextView textView=view.findViewById(R.id.item_text);
            textView.setText(currentNote.getText());

            return view;
        }
    }

    private class ListListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(!select){
                note=(Note)listView.getItemAtPosition(i);
                openNote();
            }
        }
    }


    private void showKeyboard(View view){
        view.requestFocus();
        InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void hideKeyboard(View view){
        InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setMainContentView(){
        setContentView(R.layout.activity_main);
        listView=(ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListListener());
        loadData();
        view=VIEW_MAIN;
    }

    private void loadData(){
        items.clear();
        ArrayList<Note> tmp=dataBase.getAllNotes();

        if(showPrivate)  items.addAll(tmp);
        else{
            for(int i=tmp.size()-1;i>=0;--i){
                Note ntmp=tmp.get(i);
                if(!ntmp.isHidden()) items.add(ntmp);
            }
        }
        adapter.notifyDataSetChanged();
    }

}
