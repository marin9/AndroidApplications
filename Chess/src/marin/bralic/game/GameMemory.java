package marin.bralic.game;

import java.util.ArrayList;
import java.util.List;

public class GameMemory {		
	private List<Character> turnList;
	private List<Pieces[][]> boardList;
	private List<Boolean> whiteCleftList;
	private List<Boolean> whiteCrightList;
	private List<Boolean> blackCleftList;
	private List<Boolean> blackCrightList;
	private List<Integer> whiteApList;
	private List<Integer> blackApList;
	
	
	private List<Integer> sum;
	
	
	public GameMemory(){
		turnList=new ArrayList<Character>();
		boardList=new ArrayList<Pieces[][]>();
		whiteCleftList=new ArrayList<Boolean>();
		whiteCrightList=new ArrayList<Boolean>();
		blackCleftList=new ArrayList<Boolean>();
		blackCrightList=new ArrayList<Boolean>();
		whiteApList=new ArrayList<Integer>();
		blackApList=new ArrayList<Integer>();
		
		sum=new ArrayList<Integer>();
	}
		
	public void clear(){
		turnList.clear();
		boardList.clear();
		
		whiteCleftList.clear();
		whiteCrightList.clear();
		blackCleftList.clear();
		blackCrightList.clear();
		whiteApList.clear();
		blackApList.clear();
		
		sum.clear();
	}
	
	public boolean add(char tu, Pieces[][] bo, boolean wCr, boolean wCl, boolean bCr, boolean bCl, int wAp, int bAp){
		int s=0;
		
		for(int n=0;n<sum.size();++n){
			s=sum.get(n);			
			if(compare(tu, bo, wCr, wCl, bCr, bCl, wAp, bAp,     
					turnList.get(n).charValue(), boardList.get(n), whiteCleftList.get(n), whiteCrightList.get(n), blackCleftList.get(n), blackCrightList.get(n), whiteApList.get(n), blackApList.get(n))){
				
				++s;
				sum.set(n, s);
				
				if(s>=3) return true;
				else return false;
			}							
		}
		
		turnList.add(tu);	
		whiteCleftList.add(wCr);
		whiteCrightList.add(wCl);
		blackCleftList.add(bCr);
		blackCrightList.add(bCl);
		whiteApList.add(wAp);
		blackApList.add(bAp);			
		sum.add(1);	
		Pieces[][] pom=new Pieces[8][8];
		for(int a=0;a<8;++a){
			for(int b=0;b<8;++b){
				pom[a][b]=bo[a][b];
			}
		}
		boardList.add(pom);
		
		return false;
	}
	
	
	private boolean compare(char tu1, Pieces[][] bo1, boolean wCr1, boolean wCl1, boolean bCr1, boolean bCl1, int wAp1, int bAp1,    
			                char tu2, Pieces[][] bo2, boolean wCr2, boolean wCl2, boolean bCr2, boolean bCl2, int wAp2, int bAp2){	
		
		if(tu1!=tu2 || wCr1!=wCr2 || wCl1!=wCl2 || bCr1!=bCr2 || bCl1!=bCl2 || wAp1!=wAp2 || bAp1!=bAp2) return false;
		
		for(int i=0;i<8;++i){
			for(int j=0;j<8;++j){
				if(bo1[i][j]!=bo2[i][j]){
					return false;
				}
			}
		}
		
		return true;
	}	

}
