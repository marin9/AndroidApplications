package marin.bralic.storageanalyze;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

@SuppressLint("DefaultLocale")
public class MainActivity extends Activity{	
	private List<String> pictureExt;
	private List<String> musicExt;
	private List<String> videoExt;
	private List<String> documentsExt;
	private List<String> applicationExt;
	
	private RadioGroup radioButtons;
	
	private ProgressBar progress;
	private TextView messageText;
	
	private ProgressBar pictureProgress;
	private ProgressBar musicProgress;
	private ProgressBar videoProgress;
	private ProgressBar applicationProgress;
	private ProgressBar documentProgress;
	private ProgressBar othersProgress;
	private TextView pictureText;
	private TextView musicText;
	private TextView videoText;
	private TextView applicationText;
	private TextView documentsText;
	private TextView othersText;
	
	private TextView totalSpaceText;
	private TextView freeSpaceText;

	
	private SearchThread searchThread;
	private File file;
	private long pictureSize, musicSize, videoSize, documentSize, applicationSize, othersSize;
	private long total, free;
	private volatile long used;
	private volatile boolean stop, finish;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);		
		loadContents();
	}
		
	private void loadContents(){
		pictureExt=new LinkedList<String>(Arrays.asList(".png", ".jpg", ".jpeg", ".jfif", ".exif", ".gif", ".rif", ".bmp", ".tif"));
		musicExt=new LinkedList<String>(Arrays.asList(".mp3", ".act", ".dct", ".wma", ".asiff", ".acc", ".mpc", ".amr", ".flac", ".ogg", ".m4a", ".vox", ".wav", ".m4p"));
		videoExt=new LinkedList<String>(Arrays.asList(".mp4", ".webm", ".mpv", ".mkv", ".avi", ".asf", ".3gpp", ".flv", ".mov", ".mpeg", ".ogv", ".mpg", ".divx", ".wmv", ".mp2", ".f4v", ".3gp", ".mpeg4"));
		documentsExt=new LinkedList<String>(Arrays.asList(".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx"));
		applicationExt=new LinkedList<String>(Arrays.asList(".apk"));		
		
		radioButtons=(RadioGroup)findViewById(R.id.radioGroup1);
		
		progress=(ProgressBar)findViewById(R.id.progress);
		messageText=(TextView)findViewById(R.id.message_text);
		
		pictureProgress=(ProgressBar)findViewById(R.id.picture_progress);
		musicProgress=(ProgressBar)findViewById(R.id.music_progress);
		videoProgress=(ProgressBar)findViewById(R.id.video_progress);
		applicationProgress=(ProgressBar)findViewById(R.id.application_progress);
		documentProgress=(ProgressBar)findViewById(R.id.documents_progress);
		othersProgress=(ProgressBar)findViewById(R.id.others_progress);
		
		pictureText=(TextView)findViewById(R.id.picture_text);
		musicText=(TextView)findViewById(R.id.music_text);
		videoText=(TextView)findViewById(R.id.video_text);
		applicationText=(TextView)findViewById(R.id.application_text);
		documentsText=(TextView)findViewById(R.id.documents_text);
		othersText=(TextView)findViewById(R.id.others_text);
		
		totalSpaceText=(TextView)findViewById(R.id.totalSpaceText);
		freeSpaceText=(TextView)findViewById(R.id.freeSpaceText);

	}
			
	public void start(View view){
		if(searchThread!=null && searchThread.isAlive()) return;
		else{
			stop=false;
			int n=radioButtons.indexOfChild(radioButtons.findViewById(radioButtons.getCheckedRadioButtonId()));
			
			if(n==0) file=new File("/storage/sdcard0");			
			else if(n==1) file=new File("/storage/extSdCard");	
			
			searchThread=new SearchThread(file);
			searchThread.start();			
			messageText.setText("Wait...  ");		
		}
	}
	
	public void stop(View view){		
		if(searchThread!=null && searchThread.isAlive()){
			stop=true;
			searchThread.interrupt();	
			try{   searchThread.join();   }catch(InterruptedException e) {}		
			messageText.setText("Stoped.  ");
		}		
	}
	
	
	
	private class SearchThread extends Thread{
		private File file;
		private Thread progressThread;
		
		
		public SearchThread(File file){
			this.file=file;
			progressThread=new ProgressThread(file);
		}
		
		@Override
		public void run(){
			pictureSize=musicSize=videoSize=documentSize=applicationSize=othersSize=total=used=free=0;
			progressThread.start();
			finish=true;
			
			try{
				calculate(file);
			}catch(Exception e){
				finish=false;
			}
						
			try{   
				stop=true;
				progressThread.join();   }catch(InterruptedException e){}
			
			MainActivity.this.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					total=file.getTotalSpace();
					free=file.getFreeSpace();	
					
					totalSpaceText.setText("\n  Total space: "+uljepsaj(total));
					freeSpaceText.setText("  Free space: "+uljepsaj(free));

					pictureProgress.setProgress((int)Math.round(((double)pictureSize/used) * 100));
					musicProgress.setProgress((int)Math.round(((double)musicSize/used) * 100));
					videoProgress.setProgress((int)Math.round(((double)videoSize/used) * 100));
					applicationProgress.setProgress((int)Math.round(((double)applicationSize/used) * 100));
					documentProgress.setProgress((int)Math.round(((double)documentSize/used) * 100));
					othersProgress.setProgress((int)Math.round(((double)othersSize/used) * 100));
					
					pictureText.setText("  Picture:  "+uljepsaj(pictureSize));
					musicText.setText("\n  Music:  "+uljepsaj(musicSize));
					videoText.setText("\n  Video:  "+uljepsaj(videoSize));
					applicationText.setText("\n  Application:  "+uljepsaj(applicationSize));
					documentsText.setText("\n  Documents:  "+uljepsaj(documentSize));
					othersText.setText("\n  Others:  "+uljepsaj(othersSize));
					
					if(finish) messageText.setText("Finish.  ");
					else messageText.setText("Stoped.  ");
				}				
			});	
			
		}	
				
	}
	
	private class ProgressThread extends Thread{
		private long usedSpace;
		
		public ProgressThread(File file){
			usedSpace=file.getUsableSpace();
		}
		
		@Override
		public void run(){
			while(!stop){			
				MainActivity.this.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						progress.setProgress((int)Math.round(((double)used/usedSpace)*100));
						messageText.setText("Wait...  "+Math.round(((double)used/usedSpace)*100)+" %");
					}					
				});
				
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {}				
			}
			
			MainActivity.this.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					progress.setProgress(100);
				}					
			});
			
		}
	}
	
	
	public void calculate(File file) throws Exception {
		if (file.listFiles() == null) return;
		for (File names : file.listFiles()) {
			if(stop) throw new Exception();
			if (names.isFile()) {
				String ext;
				try{
					ext = names.getName().substring(names.getName().lastIndexOf("."), names.getName().length()).toLowerCase();
				}catch(Exception e){
					ext="--:--";
				}

				if (pictureExt.contains(ext)) pictureSize+=names.length();
				else if (musicExt.contains(ext)) musicSize+=names.length();
				else if (videoExt.contains(ext)) videoSize+=names.length();
				else if (documentsExt.contains(ext)) documentSize+=names.length();
				else if(applicationExt.contains(ext)) applicationSize+=names.length();
				else othersSize = othersSize + names.length();									
				
				used+=names.length();
				
			} else if (names.isDirectory()) {
				calculate(names);
			}
		}
	}
		
	private String uljepsaj(long sizeL){
    	float size=(float)sizeL;
    	if(size>1000000000){
    		size=size/1000000000;
    		return ""+String.format("%4.1f", size)+" GB";
    	}else if(size>1000000){
    		size=size/1000000;
    		return ""+String.format("%4.1f", size)+" MB";
    	}else if(size>1000){
    		size=size/1000;
    		return ""+String.format("%4.1f", size)+" kB";
    	}else{
    		return ""+String.format("%4.1f", size)+" B";
    	}

	}
	
}
