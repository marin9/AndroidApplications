package mi.musicshareplayer;

import android.media.MediaPlayer;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Player {	
	public static boolean is_play;
	private Application activity;
	private MediaPlayer player;	
	private String path;
	private int progress, duration;
	private boolean pause;
	private Thread seek_thread;
	private SeekBar seekBar;
	private TextView time;
	private TextView trackName;
	private boolean run;
	
	
	
	public Player(Application act, SeekBar seekBar, TextView time, TextView trackName){
		run=true;
		activity=act;
		this.seekBar=seekBar;
		this.seekBar.setOnSeekBarChangeListener(new SListener());
		this.time=time;
		this.trackName=trackName;
		trackName.setText(":)");
		seek_thread=new SThread();
		
		player=new MediaPlayer();		
		pause=false;
		progress=0;
		path="";
		is_play=false;
	}
			
	
	public void play(Song song){
		if(player.isPlaying() && !path.equals(song)){
			try {
				player.stop();
				player.release();
				player=new MediaPlayer();
				path=song.getPath();
				player.setDataSource(path);
				trackName.setText(song.getName());
				player.prepare();
				duration=player.getDuration();
				player.start();
				player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {			
					@Override
					public void onCompletion(MediaPlayer mp) {
						if(activity.getNext()!=null) play(activity.getNext());	
					}
				});
			    is_play=true;
				pause=false;	
				if(!seek_thread.isAlive()) seek_thread.start();
			} catch (Exception e) {}				
		}else if(!player.isPlaying()){			
			try {
				player.stop();
				player.release();
				player=new MediaPlayer();
				path=song.getPath();				
			    player.setDataSource(path);
			    trackName.setText(song.getName());
			    player.prepare();
			    duration=player.getDuration();
			    player.start();
			    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {			
					@Override
					public void onCompletion(MediaPlayer mp) {
						if(activity.getNext()!=null) play(activity.getNext());	
					}
				});
			    is_play=true;
			    pause=false;
			    if(!seek_thread.isAlive()) seek_thread.start();
			    
			} catch (Exception e) {}
		}	
	}
	
	public void pause(){	    
		is_play=false;
		player.pause();
		progress=player.getCurrentPosition();
		pause=true;
	}
	
	public void stop(){
		is_play=false;
		run=false;
		player.stop();
		player.release();
		try {
			seek_thread.join();
		} catch (InterruptedException e) {}
	}
	
	public void resume(){
		if(pause){			
			player.seekTo(progress);
			player.start();
			pause=false;
			is_play=true;
		}
	}
		
	public int getAudioS(){
		return player.getAudioSessionId();
	}
		
	public boolean isStarted(){
		return seek_thread.isAlive();
	}
	
	
	private class SListener implements OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			progress=(int)Math.round(((double)arg0.getProgress()/1000)*duration);		
		    player.seekTo(progress);
		}
		
	}
	
	private class SThread extends Thread{	
		
		public SThread(){
			seekBar.setMax(1000);
		}
		
		@Override
		public void run(){
			while(run){		

			    try{
			    	progress=player.getCurrentPosition();
			    }catch(IllegalStateException e){}
			    
				seekBar.setProgress(Math.round(((float)progress/(float)duration)*1000));

				activity.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						int h=(int)((progress/(1000*60*60))%24);
						int min=(int)((progress/(1000*60))%60);
						int sec=(int)(progress/1000)%60 ;						
						
						if(h==0) time.setText(String.format("%02d:%02d", min, sec));
						else time.setText(String.format("%02d:%02d:%02d", h, min, sec));					
					}				
				});
				
				try{  Thread.sleep(500);  }catch(InterruptedException e){}
			}
		}
	}

}
