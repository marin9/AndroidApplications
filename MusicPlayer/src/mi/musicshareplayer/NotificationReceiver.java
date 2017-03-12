package mi.musicshareplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

public class NotificationReceiver extends BroadcastReceiver{
	private static Application app;	
	
	public NotificationReceiver(Application a){
		app=a;
	}
	
	public NotificationReceiver(){
		super();
	}

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		String action=arg1.getAction();
		if(action.equals("A_PREV")) app.prev(new View(arg0));
		else if(action.equals("A_PLAY_STOP")) app.start_stop(new View(arg0));
		else if(action.equals("A_NEXT")) app.next(new View(arg0));
		else if(action.equals("A_EXIT")) app.exit();					
	}	
}
