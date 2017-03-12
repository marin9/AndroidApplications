package mi.musicshareplayer;

public class Song {
	private String name;
	private String ext;
	private String path;
	
	public Song(String path){
		int l=path.length();
		this.path=path;
		ext=path.substring(path.lastIndexOf("."), l);
		name=path.substring(path.lastIndexOf('/')+1, l-4);
	}
	
	public Song(Song new_song){
		name=new_song.getName();
		path=new_song.getPath();
		ext=new_song.getExt();
	}
	
	public String getName(){
		return name;
	}
	
	public String getExt(){
		return ext;
	}
	
	public String getPath(){
		return path;
	}
	


}
