package marin.bralic.application;

public class Time extends Thread{
	private long time;
	private volatile boolean count, run;	
	
	public Time(){
		time=0;
		run=true;
	}
	
	@Override
	public void run(){
		while(run){
			
			try {  Thread.sleep(100);  } catch (InterruptedException e) {}
			
			if(count) ++time;
		}
		
	}

	public long getTsec(){
		return time/10;
	}
	
	public void resetT(){
		pauseT();
		time=0;
	}
	
	public void pauseT(){
		count=false;
	}
	
	public void resumeT(){
		count=true;
	}
	
	public void finish(){
		run=false;
	}
	
}
