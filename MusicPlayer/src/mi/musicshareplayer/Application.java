package mi.musicshareplayer;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class Application extends Activity{
	private NotificationManager notif;
	@SuppressWarnings("unused")
	private NotificationReceiver receiver;
	private Random random;
    private EditText searchTextEdit;
    
    private ListView listView;
	private List<Song> files_list;
	private SArrayAdapter files_adapter;
    
	private SeekBar seekBar;	
	private TextView timeText;
	private TextView trackName;
	private Button playButton, repeatButton, shufleButton;
	
	private Player player;
	private int indexPlay;
	private boolean shufle, repeat;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_layout);
		receiver=new NotificationReceiver(this);
		
		random=new Random();
		searchTextEdit=(EditText)findViewById(R.id.editText1);
		searchTextEdit.addTextChangedListener(new EListener());
		
		seekBar=(SeekBar)findViewById(R.id.seekBar);
		timeText=(TextView)findViewById(R.id.timeText);
		trackName=(TextView)findViewById(R.id.nameText);
		
		playButton=(Button)findViewById(R.id.button2); 
		shufleButton=(Button)findViewById(R.id.button5); 
		repeatButton=(Button)findViewById(R.id.button4); 
		
		files_list=new LinkedList<Song>();
		
		fillList(Environment.getExternalStorageDirectory(), null);
        fillList(new File("/mnt/extSdCard/"), null);
        if(files_list.isEmpty()) Toast.makeText(this, "No music", Toast.LENGTH_SHORT).show();
		    
		files_adapter = new SArrayAdapter(this, R.layout.list_view, files_list);	      
		listView=(ListView)findViewById(R.id.list);
		listView.setAdapter(files_adapter);   
		
		player=new Player(this, seekBar, timeText, trackName);
		shufle=repeat=false;		
		
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				indexPlay=arg2;
				player.play(files_list.get(arg2));
				playButton.setBackgroundResource(R.drawable.pause);
				Toast.makeText(Application.this, "Play", Toast.LENGTH_SHORT).show();
			}	    	
    	});				
				
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);		
		showNotification(false);
	}
	
	@Override
	protected void onDestroy(){
		player.stop();
		notif.cancel(0);
		super.onDestroy();
	}
		
	private void showNotification(boolean play){
		RemoteViews v=new RemoteViews(getPackageName(), R.layout.notif_layout);
		if(play) v.setInt(R.id.bn2, "setBackgroundResource", R.drawable.c_pause);
		
	    Intent i0=new Intent(this, Application.class);
        i0.setAction(Intent.ACTION_MAIN);
        i0.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pi0=PendingIntent.getActivity(this, 0, i0, 0);	         
	    v.setOnClickPendingIntent(R.id.bn0, pi0);
	    
	    Intent i1=new Intent(this, NotificationReceiver.class);
	    i1.setAction("A_PREV");
        PendingIntent pi1=PendingIntent.getBroadcast(this, 0, i1, 0);	         
	    v.setOnClickPendingIntent(R.id.bn1, pi1);
	    
	    Intent i2=new Intent(this, NotificationReceiver.class);
	    i2.setAction("A_PLAY_STOP");
        PendingIntent pi2=PendingIntent.getBroadcast(this, 0, i2, 0);	         
	    v.setOnClickPendingIntent(R.id.bn2, pi2);
	    
	    Intent i3=new Intent(this, NotificationReceiver.class);
	    i3.setAction("A_NEXT");
        PendingIntent pi3=PendingIntent.getBroadcast(this, 0, i3, 0);	         
	    v.setOnClickPendingIntent(R.id.bn3, pi3);
	    
	    Intent i4=new Intent(this, NotificationReceiver.class);
	    i4.setAction("A_EXIT");
        PendingIntent pi4=PendingIntent.getBroadcast(this, 0, i4, 0);	         
	    v.setOnClickPendingIntent(R.id.bn4, pi4);
	    
		Notification.Builder b=new Notification.Builder(this);
		b.setSmallIcon(R.drawable.ic_launcher);
		b.setOngoing(true);
		b.setContent(v);
		
		if(notif!=null) notif.cancel(0);
		notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notif.notify(0, b.build());
	}	
		
	@SuppressLint("DefaultLocale")
	private void fillList(File file, String mask){
    	if (file.isDirectory() && file.listFiles() != null) {
			for (File names : file.listFiles()) {
				if (names.isFile() && !names.isHidden()) {	
					String ext=names.getName().substring(names.getName().indexOf(".")+1, names.getName().length());
					if(ext.equals("mp3")){	
						Song s=new Song(names.getAbsolutePath());
						if(mask!=null && mask.length()<2 && s.getName().length()>=mask.length() && s.getName().toLowerCase().substring(0, mask.length()).equals(mask.toLowerCase())) files_list.add(s);
						else if(mask!=null && mask.length()>=2 && s.getName().toLowerCase().contains(mask.toLowerCase())) files_list.add(s);
						else if(mask==null) files_list.add(s);
					}
				} else if (names.isDirectory() && !names.isHidden()){
					fillList(names, mask);
				}
			}
		}
    }
	  
	
	
	public void refresh(View view){
		searchTextEdit.setText("");
		fillList(Environment.getExternalStorageDirectory(), null);
        fillList(new File("/mnt/extSdCard/"), null);
        files_adapter.notifyDataSetChanged();
		searchTextEdit.clearFocus();
	}
	
	public void start_stop(View view){
		if(!player.isStarted()){
			player.play(files_list.get(indexPlay));
			if(Player.is_play){
				playButton.setBackgroundResource(R.drawable.pause);
				showNotification(true);
			}
		}else if(Player.is_play){
			player.pause();
			playButton.setBackgroundResource(R.drawable.play);
			showNotification(false);
		}else if(!files_list.isEmpty()){
			player.resume();
			playButton.setBackgroundResource(R.drawable.pause);
			showNotification(true);
		}		
	}
	
	public void prev(View view){
		if(files_list.isEmpty()) return;
		--indexPlay;
		if(indexPlay<0) indexPlay=0;		
		player.play(files_list.get(indexPlay));
		if(Player.is_play){
			playButton.setBackgroundResource(R.drawable.pause);
			showNotification(true);
		}
	}
	
	public void next(View view){
		if(files_list.isEmpty()) return;
		if(!shufle){
			++indexPlay;
			if(indexPlay>=files_list.size()) indexPlay=0;		
			player.play(files_list.get(indexPlay));
		}else{
			indexPlay=(int)Math.abs(random.nextInt()%files_list.size());
			player.play(files_list.get(indexPlay));
		}
		if(Player.is_play){
			playButton.setBackgroundResource(R.drawable.pause);
			showNotification(true);
		}
	}
	
	public void shufle(View view){
		shufle=!shufle;
		if(shufle) shufleButton.setBackgroundResource(R.drawable.shufle_on);
		else shufleButton.setBackgroundResource(R.drawable.shufle_off);
	}
	
	public void repeat(View view){
		repeat=!repeat;
		if(repeat) repeatButton.setBackgroundResource(R.drawable.repeat_on);
		else repeatButton.setBackgroundResource(R.drawable.repeat_off);
	}
	
	public Song getNext(){
		if(files_list.isEmpty()) return null;
		if(repeat) return files_list.get(indexPlay);
		else if(shufle){
			indexPlay=(int)Math.abs(random.nextInt()%files_list.size());
			return files_list.get(indexPlay);
		}else{
			indexPlay=indexPlay+1;
			if(indexPlay>=files_list.size()) indexPlay=0;			
			return files_list.get(indexPlay);
		}		
	}

	public void exit(){
		Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		sendBroadcast(it); 
		finish();
	}
	
	
	private class EListener implements TextWatcher{

		@Override
		public void afterTextChanged(Editable arg0) {}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			String m=searchTextEdit.getText().toString();
			files_list.clear();
			
			fillList(Environment.getExternalStorageDirectory(), m);
	        fillList(new File("/mnt/extSdCard/"), m);
			
			files_adapter.notifyDataSetChanged();
			searchTextEdit.clearFocus();
		}		
	}
	
	private class SArrayAdapter extends ArrayAdapter<Song>{
		private final Context context;
		private final List<Song> list;
		
		public SArrayAdapter(Context context, int resource, List<Song> objects) {
			super(context, resource, objects);
			this.context=context;
			list=objects;		
		}
		
		
		@SuppressLint("ViewHolder")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {			
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View view = inflater.inflate(R.layout.list_view, parent, false);
			
		    Song s=list.get(position);		    
		    TextView text=(TextView)view.findViewById(R.id.nameText);
		    text.setText(s.getName());
		    
			return view;
		}			
		
	}
}
