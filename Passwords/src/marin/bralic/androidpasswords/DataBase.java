package marin.bralic.androidpasswords;

import java.util.ArrayList;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class DataBase {
	
	private ActionBarActivity mainActivity;
	private SharedPreferences saved_items;
	
	private ArrayList<ListItem> passwordsList;
	private ArrayList<ListItem> pomList;
	private ArrayAdapter<ListItem> adapter;
	
	

	public DataBase(ActionBarActivity mainActivity, SharedPreferences savedItems){
		this.mainActivity=mainActivity;
		saved_items=savedItems;
		passwordsList=new ArrayList<ListItem>();	
		pomList=new ArrayList<ListItem>();
		loadItems();
				
		adapter=new ListAdapter();	
		show_hide(-1);
	}
	
	public void show_hide(int index){
		if(index==-1){
			pomList.clear();
			for(ListItem item: passwordsList){
				String password=item.getPassword();
				
				String hidenPassword="";
				for(int i=0;i<password.length();++i){
					hidenPassword=hidenPassword+"*";
				}
				
				pomList.add(new ListItem(item.getName(), hidenPassword));
			}
			adapter.notifyDataSetChanged();
		}else{
			ListItem item=pomList.get(index);

			if(item.getPassword().contains("*")){
				pomList.remove(index);
				pomList.add(index, passwordsList.get(index));
				adapter.notifyDataSetChanged();
			}else{				
                String password=item.getPassword();
				
				String hidenPassword="";
				for(int i=0;i<password.length();++i){
					hidenPassword=hidenPassword+"*";
				}
				
				pomList.remove(index);
				pomList.add(index, new ListItem(item.getName(), hidenPassword));
				adapter.notifyDataSetChanged();
			}
		}
		
		
	}
	
	public boolean containsPasswordWithName(String passwordName){
		ListItem item=new ListItem(passwordName, "");
		if(passwordsList.contains(item)) return true;
		else return false;
	}
	
	public String getItemNameAt(int index){
		String name=passwordsList.get(index).getName();
		return name;
	}
	
	public String getItemPasswordAt(int index){
		String password=passwordsList.get(index).getPassword();
		return password;
	}
	
	public ArrayAdapter<ListItem> getAdapter(){
		return adapter;
	}
	
	public int getItemNumber(){
		return passwordsList.size();
	}
	
	public void addNewPassword(String name, String password){
		passwordsList.add(new ListItem(name, password));
		pomList.add(new ListItem(name, password));
		adapter.notifyDataSetChanged();
	    saveChanges();
	}
	
	public void addNewPassword(String name, String password, int index){
		passwordsList.add(index, new ListItem(name, password));
		pomList.add(index, new ListItem(name, password));
		adapter.notifyDataSetChanged();
	    saveChanges();
	}

	public void deletePassword(int i){
		passwordsList.remove(i);
		pomList.remove(i);
		adapter.notifyDataSetChanged();
		saveChanges();
	}
	
	
	private void saveChanges(){
		String key=saved_items.getString("KEY", null);
		
		SharedPreferences.Editor editor=saved_items.edit();
		editor.clear();
		editor.putString("KEY", key);
		
		Integer i=0;
		
		for(ListItem item: passwordsList){			
			editor.putString(i.toString(), (item.getName()+" "+item.getPassword()));
			++i;
		}
		
		editor.commit();
	}
	
	private void loadItems(){
		String item="-1";
		Integer i=0;
		
		do{
			item=saved_items.getString(i.toString(), "-1");
			if(!item.equals("-1")){
				String[] items=item.split(" ");
				passwordsList.add(new ListItem(items[0], items[1]));
			}
			++i;
		}while(!item.equals("-1"));
	}
	
	
	private class ListAdapter extends ArrayAdapter<ListItem>{
		public ListAdapter() {
			super(mainActivity, R.layout.list_layout, pomList);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			
			View itemView=convertView;			
			if(itemView==null) itemView=mainActivity.getLayoutInflater().inflate(R.layout.list_layout, parent, false);
			
			
			ListItem currentItem=pomList.get(position);
			
			final TextView nameText=(TextView)itemView.findViewById(R.id.textView1);
			nameText.setText(currentItem.getName());
			final TextView passwordText=(TextView)itemView.findViewById(R.id.textView2);
			passwordText.setText(currentItem.getPassword());
			
			
			return itemView;	
		}
	}
	
}
