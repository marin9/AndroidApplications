package marin.bralic.calc;

public interface IDisplay {	
	public void addText(String t);
	public void setText(String t);
	public String getText();
	public int getBracket();
	
	public void setToBegin();
	public void setToEnd();
	public void left();
	public void right();	
	public void back();
	public void clear();
	
	public void setScfMod(boolean scf);
	public void setShift(boolean shf);
	public void setRad(boolean rad);
	public void setRound(int n);	
	public void setError(boolean tf);
	public void setMod(Display.Mod m);
	public void setVarSelected(int n);
	public void setUpDown(int s);
	public void setDoubleAns(String x1, String x2);
}
