package marin.bralic.mvideo;

import java.io.File;
import java.util.ArrayList;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.VideoView;


@SuppressLint({ "DefaultLocale", "NewApi", "ClickableViewAccessibility"})
@SuppressWarnings("deprecation")
public class MainActivity extends Activity implements OnClickListener {
	
	EditText searchTextEdit;
	
	VideoView videoView;
	SeekBar seekBar;
	TextView timeTextView;
	TextView fileNameTextView;
	
	Button[] button=new Button[4];
	
	ListView listView;
	ArrayList<String> fileNameList;
	ArrayList<String> filePathList;
	ArrayAdapter<String> adapter;
	
	ArrayList<String> pomFileNameList;
	ArrayList<String> pomFilePathList;
	
	
	final static int SEEKBARMAX=1000;
	long progress;
	int indexPlay=0;
	boolean pause=false, changedFile=false;
	
	Thread seekBarThread;
	@SuppressLint("UseValueOf")
	Integer monitor=new Integer(0);
	
	int searchTextHeight, searchButtonHeight, videoHeight, videoWidth;
		
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    
	    
	    searchTextEdit=(EditText)findViewById(R.id.searchText);	    
	    
	    videoView=(VideoView)findViewById(R.id.videoView1);
	    videoView.setSoundEffectsEnabled(true);
	   
	    seekBar=(SeekBar)findViewById(R.id.seekBar);
	    seekBar.setMax(SEEKBARMAX);	
	    seekBarThread=new Thread();
	    timeTextView=(TextView)findViewById(R.id.timeText);
	    fileNameTextView=(TextView)findViewById(R.id.textView1);
	      
	    fileNameList=new ArrayList<String>();
	    filePathList=new ArrayList<String>();
	    pomFileNameList=new ArrayList<String>();
	    pomFilePathList=new ArrayList<String>();
        fillList(Environment.getExternalStorageDirectory());
        fillList(new File("/mnt/extSdCard/"));
        saveList();      
	    
	    adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, fileNameList);	      
	    listView=(ListView)findViewById(R.id.list);
	    listView.setAdapter(adapter);    	
	    
	    setButtons();
	    setListeners();
	    
	    DisplayMetrics display=new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(display);
	    if(display.heightPixels<500)
	         videoView.setLayoutParams(new LinearLayout.LayoutParams(videoView.getRight(), 90));
	}
      
    private void setListeners(){
    	 searchTextEdit.addTextChangedListener(new TextWatcher(){	    	
 			@Override
 			public void afterTextChanged(Editable arg0) {}
 			
 			@Override
 			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {	}
 			
 			@Override
 			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
 				indexPlay=0;
 				if(searchTextEdit.getText().toString().contains("Searc")) searchTextEdit.setText("");
 				
 				int length=pomFileNameList.size();
 				String word=searchTextEdit.getText().toString();
 				
 				//filter
 				fileNameList.clear();
 				filePathList.clear();
 				for(int i=0;i<length;++i) if(pomFileNameList.get(i).toLowerCase().contains(word.toLowerCase())){
 					fileNameList.add(pomFileNameList.get(i));
 					filePathList.add(pomFilePathList.get(i));
 				}
 				
 				adapter.notifyDataSetChanged();
 				searchTextEdit.clearFocus();
 			}
 	    });
    	 
    	 
    	 videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {			
 			@Override
 			public void onCompletion(MediaPlayer mp) {
 				button[2].performClick();
 			}
 		 });
    	 videoView.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				button[1].performClick();
				return false;
			}    		 
    	 });
    	 
    	 seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				synchronized(monitor){
				    progress=Math.round(((double)arg0.getProgress()/SEEKBARMAX)*videoView.getDuration());		
				    videoView.seekTo((int)progress);

				    int h=Math.round((float)videoView.getCurrentPosition()/1000/60/60);
				    int min=Math.round((float)((videoView.getCurrentPosition()/1000)-h*60*60)/60);
				    int sec=Math.round((float)((videoView.getCurrentPosition()/1000)-min*60));
				
				    if(h==0) timeTextView.setText(String.format("%02d:%02d", min, sec));
				    else timeTextView.setText(String.format("%02d:%02d:%02d", h, min, sec));
    	        }
			}   		 
    	 });
    	 
    	
    	listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    		@Override
    		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {	
    			indexPlay=arg2;
    			videoPlayStop(1);				
    		}	    	
    	});

    }
    
    private void setButtons(){
    	button[0]=(Button)findViewById(R.id.button1);
    	button[1]=(Button)findViewById(R.id.button2);
    	button[2]=(Button)findViewById(R.id.button3);  
    	button[3]=(Button)findViewById(R.id.button4); 
    	for(int i=0;i<4;++i) button[i].setOnClickListener(this);
    }
    
    
	private void videoPlayStop(int mod){ //0 stop, 1 start, 2 resume
    	if(mod==0){
    		videoView.pause();
    		pause=true;
    		button[1].setBackground(this.getResources().getDrawable(R.drawable.ic_play)); 		
    	}else if (mod==1){
    		if(fileNameList.isEmpty()) return;
    		pause=false;
		    videoView.setVideoPath(filePathList.get(indexPlay));
		    button[1].setBackground(this.getResources().getDrawable(R.drawable.ic_pause));
		    fileNameTextView.setText(fileNameList.get(indexPlay));
		    runThread();
		    videoView.start();
		    changedFile=false;
    	}else if(mod==2){
    		pause=false;
    		button[1].setBackground(this.getResources().getDrawable(R.drawable.ic_pause));
 		    
 		    if(changedFile){
 		    	videoView.setVideoPath(filePathList.get(indexPlay));
 		    	fileNameTextView.setText(fileNameList.get(indexPlay));
 		    	changedFile=false;
 		    }
 		    videoView.start();
    	}  	
    }
       
    private void runThread(){
    	seekBarThread.interrupt();   	
    	seekBarThread=new Thread(){
    		@Override
    		public void run(){
    			try{
    			while(true){   				
    				Thread.sleep(300);
    				if(!videoView.isPlaying()) continue;
    				
    				synchronized(monitor){
    				progress=videoView.getCurrentPosition();

    				MainActivity.this.runOnUiThread(new Runnable(){
						@Override
						public void run() {						
							seekBar.setProgress(Math.round(((float)progress/(float)videoView.getDuration())*SEEKBARMAX));

							int h=Math.round((float)videoView.getCurrentPosition()/1000/60/60);
							int min=Math.round((float)((videoView.getCurrentPosition()/1000)-h*60*60)/60);
							int sec=Math.round((float)((videoView.getCurrentPosition()/1000)-min*60));
							
							if(h==0) timeTextView.setText(String.format("%02d:%02d", min, sec));
							else timeTextView.setText(String.format("%02d:%02d:%02d", h, min, sec));
						}    					
    				});
    				}
    			}  
    			}catch(InterruptedException e){}
    		}
    	};
    	seekBarThread.start();     	
    }
  
    private void fillList(File file){
    	if (file.isDirectory() && file.listFiles() != null) {
			for (File names : file.listFiles()) {
				if (names.isFile() && !names.isHidden()) {	
					String ext=names.getName().substring(names.getName().indexOf(".")+1, names.getName().length());
					if(ext.equals("mp4")){				
						filePathList.add(names.getAbsolutePath());
						fileNameList.add(names.getName());							
					}
				} else if (names.isDirectory() && !names.isHidden()){
					fillList(names);
				}
			}
		}
    }

    private void saveList(){
    	int length=fileNameList.size();
		for(int i=0;i<length;++i){
			pomFileNameList.add(fileNameList.get(i));
			pomFilePathList.add(filePathList.get(i));
		}
    }
    
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        DisplayMetrics display=new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(display);
	    
	    if(getApplication().getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
	    	videoHeight=videoView.getHeight();
	    	videoWidth=videoView.getWidth();
	    	searchTextHeight=searchTextEdit.getHeight();
	    	searchButtonHeight=button[3].getHeight();
	    	
            videoView.setLayoutParams(new LinearLayout.LayoutParams(display.widthPixels, display.heightPixels));
            searchTextEdit.setLayoutParams(new LinearLayout.LayoutParams(searchTextEdit.getWidth(),0));
            button[3].setLayoutParams(new LinearLayout.LayoutParams(button[3].getWidth(),0));
	    }else{
	    	videoView.setLayoutParams(new LinearLayout.LayoutParams(videoWidth, videoHeight));
	        searchTextEdit.setLayoutParams(new LinearLayout.LayoutParams(searchTextEdit.getWidth(), searchTextHeight));
	        button[3].setLayoutParams(new LinearLayout.LayoutParams(button[3].getWidth(), searchButtonHeight));
	    }
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public void onClick(View arg0) {
		int id=arg0.getId();
		
		//previous
		if(id==button[0].getId()){	
			if(fileNameList.isEmpty()) return;
			--indexPlay;
			changedFile=true;
			if(indexPlay<0) indexPlay=filePathList.size()-1;	
			fileNameTextView.setText(fileNameList.get(indexPlay));
			if(!pause) videoPlayStop(1);
		//play	
		}else if(id==button[1].getId()){
			if(fileNameList.isEmpty()) return;
			if(pause) videoPlayStop(2);
			else if(videoView.isPlaying()) videoPlayStop(0);			 
			else videoPlayStop(1);			
		//next	
		}else if(id==button[2].getId()){
			if(fileNameList.isEmpty()) return;
			++indexPlay;
			changedFile=true;
			if(indexPlay>=filePathList.size()) indexPlay=0;	
			fileNameTextView.setText(fileNameList.get(indexPlay));
			if(!pause) videoPlayStop(1);
		//refresh
		}else if(id==button[3].getId()){
			indexPlay=0;
			fileNameList.clear();
			filePathList.clear();
			pomFileNameList.clear();
			pomFilePathList.clear();
			fillList(Environment.getExternalStorageDirectory());
	        fillList(new File("/mnt/extSdCard/"));
	        saveList();
	        adapter.notifyDataSetChanged();
		}
	}
}
