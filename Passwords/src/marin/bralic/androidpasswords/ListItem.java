package marin.bralic.androidpasswords;

import android.annotation.SuppressLint;

public class ListItem {
	String name;
	String password;
	
	public ListItem(String name, String password){
		this.name=name;
		this.password=password;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	public boolean equals(Object object){
		if(object!=null)
			if(((ListItem)object).getName().toLowerCase().equals(name.toLowerCase())) return true;			
		return false;
	}

}
