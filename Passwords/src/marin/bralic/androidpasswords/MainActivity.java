package marin.bralic.androidpasswords;

import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity implements OnClickListener, View.OnKeyListener{
	private final static int DIALOG_TEXT_SIZE=25;
	
	private Button okButton, exitButton;
	private EditText keyText, keyText2;
	private TextView textLabel;
	
	private SharedPreferences saved_settings;
	private String key;
	private int mod=1; //1-new key, 0-normal run, -1 runned
	
	
	//application after run
	private Button[] button=new Button[6];
	ListView list;
    private View oldSelectedView=null;
	private int selected=-1;
	private DataBase dataBase;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_layout);
		
		textLabel=(TextView)findViewById(R.id.textView1);
		keyText=(EditText)findViewById(R.id.keyText);
	    keyText2=(EditText)findViewById(R.id.keyText2); 
	    keyText.setOnKeyListener(this);
	    keyText2.setOnKeyListener(this);
	    
		saved_settings=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());		
		key=saved_settings.getString("KEY", "-1");
		if(!key.equals("-1")){
			keyText2.setVisibility(View.GONE);
			mod=0;
		}
		
	    okButton=(Button)findViewById(R.id.okButton);
	    okButton.setTransformationMethod(null);
	    exitButton=(Button)findViewById(R.id.exitButton);	
	    exitButton.setTransformationMethod(null);
		
	    okButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
			    if(mod==1){
			    	String firstKey=keyText.getText().toString();
			    	String secondKey=keyText2.getText().toString();
			    	
			    	if(!firstKey.equals(secondKey)){
			    	    textLabel.setText("Key1 and key2 are diffrent, insert again:");
			    	    return;
			    	}
			    	
			    	SharedPreferences.Editor editor=saved_settings.edit();
			    	editor.putString("KEY", firstKey);
			    	editor.commit();
			    	
			    	runApplication();
			    }else{
			    	if(key.equals(keyText.getText().toString())) runApplication();
			    	else textLabel.setText("Wrong key, insert again:");
			    }

			    InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);				
				input.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			}	    	
	    });
	    exitButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				System.exit(0);
			}	    	
	    });
	}
	
	
	private void runApplication(){
		mod=-1;
        setContentView(R.layout.application_layout);
		
		setButtons();
		
		dataBase=new DataBase(this, saved_settings);
		list=(ListView)findViewById(R.id.listView1);
		list.setAdapter(dataBase.getAdapter());
		list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selected=arg2;
				if(oldSelectedView!=null) oldSelectedView.setBackgroundColor(Color.BLACK);
				
				arg1.setBackgroundColor(Color.rgb(40, 40, 60));
				oldSelectedView=arg1;
				dataBase.show_hide(selected);
			}			
		});
	}
	
	private void setButtons(){
		button[0]=(Button)findViewById(R.id.addNewButton);
		button[1]=(Button)findViewById(R.id.editButton);
		button[2]=(Button)findViewById(R.id.deleteButton);
		button[3]=(Button)findViewById(R.id.changeKeyButton);
		button[4]=(Button)findViewById(R.id.shButton);
		button[5]=(Button)findViewById(R.id.exitButton2);
		
		for(int i=0;i<6;++i){
			button[i].setTransformationMethod(null);
			button[i].setOnClickListener(this);
		}
	}

    private void showMyDialog(String title, String message){
    	AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainActivity.this);
		dlgAlert.setMessage(message);
		dlgAlert.setTitle(title);
		dlgAlert.setPositiveButton("OK", null);
		dlgAlert.create().show();
    }
	
	@Override
	public void onClick(View arg0) {
		int id=arg0.getId();
		
		if(id==button[0].getId()){
			//add
			AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(this);			
			
			LinearLayout layout=new LinearLayout(MainActivity.this);
			layout.setOrientation(LinearLayout.VERTICAL);
			dialogBuilder.setTitle("New Password");					
						
			final EditText input1 = new EditText(MainActivity.this);
			input1.setHint("Insert password name: ");
			input1.setTextSize(DIALOG_TEXT_SIZE);
			layout.addView(input1);
			
			final EditText input2 = new EditText(MainActivity.this);
			input2.setHint("Insert password: ");
			input2.setTextSize(DIALOG_TEXT_SIZE);
			input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			layout.addView(input2);	
			
			LinearLayout buttonsLayout=new LinearLayout(MainActivity.this);
			buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
			layout.addView(buttonsLayout);
			
			LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);
			
			final Button okBtn=new Button(MainActivity.this);
			okBtn.setText("Ok");
			okBtn.setBackgroundColor(Color.GREEN);	
			okBtn.setLayoutParams(params);
			final Button cancelBtn=new Button(MainActivity.this);
			cancelBtn.setText("Cancel");
			cancelBtn.setBackgroundColor(Color.RED);
			cancelBtn.setLayoutParams(params);
			buttonsLayout.addView(okBtn);
			buttonsLayout.addView(cancelBtn);
			
			dialogBuilder.setView(layout);
			
			final AlertDialog dialog=dialogBuilder.create();
			
			okBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					if(input1.getText().toString().equals("") || input2.getText().toString().equals("")){
						showMyDialog("Warning", "You are not insert name or password.");
                        return;
					}
					if(input1.getText().toString().contains(" ") || input2.getText().toString().contains(" ")){
						showMyDialog("Warning", "Must not contain space.");
                        return;
					}
						
					if(!dataBase.containsPasswordWithName(input1.getText().toString())){
						dataBase.addNewPassword(input1.getText().toString(), input2.getText().toString());
						dialog.cancel();
					}
					else showMyDialog("Warning", "This name is exist.");
				}				
			});
			
			cancelBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					dialog.cancel();	
				}				
			});
						
			dialog.show();			
			
		}else if(id==button[1].getId()){
			//edit
			if(selected==-1) return;
			
            AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(this);			
			
			LinearLayout layout=new LinearLayout(MainActivity.this);
			layout.setOrientation(LinearLayout.VERTICAL);
			dialogBuilder.setTitle("Edit password");					
						
			final EditText input1 = new EditText(MainActivity.this);
			input1.setHint("Insert password name: ");
			input1.setTextSize(DIALOG_TEXT_SIZE);
			layout.addView(input1);
			
			final EditText input2 = new EditText(MainActivity.this);
			input2.setHint("Insert password: ");
			input2.setTextSize(DIALOG_TEXT_SIZE);
			input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			layout.addView(input2);	
			
			LinearLayout buttonsLayout=new LinearLayout(MainActivity.this);
			buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
			layout.addView(buttonsLayout);
			
			LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);
			
			final Button okBtn=new Button(MainActivity.this);
			okBtn.setText("Ok");
			okBtn.setBackgroundColor(Color.GREEN);	
			okBtn.setLayoutParams(params);
			final Button cancelBtn=new Button(MainActivity.this);
			cancelBtn.setText("Cancel");
			cancelBtn.setBackgroundColor(Color.RED);
			cancelBtn.setLayoutParams(params);
			buttonsLayout.addView(okBtn);
			buttonsLayout.addView(cancelBtn);
			
			dialogBuilder.setView(layout);
			
			final AlertDialog dialog=dialogBuilder.create();
			
			input1.setText(dataBase.getItemNameAt(selected));
			final String oldName=input1.getText().toString();
			input2.setText(dataBase.getItemPasswordAt(selected));
			
			okBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					if(input1.getText().toString().equals("") || input2.getText().toString().equals("")){
						showMyDialog("Warning", "You are not insert name or password.");
                        return;
					}
					if(input1.getText().toString().contains(" ") || input2.getText().toString().contains(" ")){
						showMyDialog("Warning", "Must not contain space.");
                        return;
					}
											
					if(!dataBase.containsPasswordWithName(input1.getText().toString()) || input1.getText().toString().equals(oldName)){
						dataBase.deletePassword(selected);
						dataBase.addNewPassword(input1.getText().toString(), input2.getText().toString(), selected);
						dialog.cancel();
					}
					else showMyDialog("Warning", "This name is exist.");
				}				
			});
			
			cancelBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					dialog.cancel();	
				}				
			});
								
			dialog.show();	
			
		}else if(id==button[2].getId()){
			//delete
			if(selected==-1 || dataBase.getItemNumber()<=selected) return;
			final String name=dataBase.getItemNameAt(selected); 
			
			AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
			dialog.setTitle("Delete password:  "+name);
			dialog.setMessage("Are you sure to delete password: "+name);
			dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dataBase.deletePassword(selected);
				}
			});	
			dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
			dialog.create().show();
			
		}else if(id==button[3].getId()){
			//change
			final String key=saved_settings.getString("KEY", null);
			
            AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(this);			
			
			LinearLayout layout=new LinearLayout(MainActivity.this);
			layout.setOrientation(LinearLayout.VERTICAL);
			dialogBuilder.setTitle("Change key");					
						
			final EditText input1 = new EditText(MainActivity.this);
			input1.setHint("Insert old key: ");
			input1.setTextSize(DIALOG_TEXT_SIZE);
			layout.addView(input1);
			
			final EditText input2 = new EditText(MainActivity.this);
			input2.setHint("Insert new key: ");
			input2.setTextSize(DIALOG_TEXT_SIZE);
			input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			layout.addView(input2);	
			
			final EditText input3 = new EditText(MainActivity.this);
			input3.setHint("Insert new key again: ");
			input3.setTextSize(DIALOG_TEXT_SIZE);
			input3.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			layout.addView(input3);	
			
			LinearLayout buttonsLayout=new LinearLayout(MainActivity.this);
			buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
			layout.addView(buttonsLayout);
			
			LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);
			
			final Button okBtn=new Button(MainActivity.this);
			okBtn.setText("Ok");
			okBtn.setBackgroundColor(Color.GREEN);	
			okBtn.setLayoutParams(params);
			final Button cancelBtn=new Button(MainActivity.this);
			cancelBtn.setText("Cancel");
			cancelBtn.setBackgroundColor(Color.RED);
			cancelBtn.setLayoutParams(params);
			buttonsLayout.addView(okBtn);
			buttonsLayout.addView(cancelBtn);
			
			dialogBuilder.setView(layout);
			
			final AlertDialog dialog=dialogBuilder.create();
			
			okBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					if(input1.getText().toString().equals(key)){
						if(!input2.getText().toString().equals(input3.getText().toString())){
							showMyDialog("Warning", "New key1 and new key2 are diffrerent");
							return;
						}
						SharedPreferences.Editor editor=saved_settings.edit();
						editor.remove("KEY");
						editor.putString("KEY", input3.getText().toString());
						editor.commit();
						dialog.cancel();
					}else{
						showMyDialog("Warning", "Old key are wrong.");						
					}
				}				
			});
			
			cancelBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {dialog.cancel();}				
			});					
			
			dialog.show();	
		}else if(id==button[4].getId()){
			//sh
			dataBase.show_hide(-1);
		}else if(id==button[5].getId()){
			//exit
			System.exit(0);
		}		
	}


	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_ENTER && mod!=-1){
			okButton.performClick();
		}
		return false;
	}



}
