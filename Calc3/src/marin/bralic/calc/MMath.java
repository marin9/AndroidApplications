package marin.bralic.calc;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Locale;

import marin.bralic.calc.calculators.ProgrammerCalculator;

public class MMath {
	public static final String mp="1.672621637*10^(-27)";
	public static final String mn="1.674927211*10^(-27)";
	public static final String me="9.10938215*10^(-31)";
	public static final String mmi="1.8835313*10^(-28)";
	public static final String a0="5.291772086*10^(-11)";
	public static final String h="6.62606896*10^(-34)";
	public static final String mN="5.05078324*10^(-27)";
	public static final String mB="9.27400915^(-24)";
	public static final String h_="1.054571628*10^(-34)";
	public static final String alfa="7.297352538*10^(-3)";
	public static final String re="2.817940289*10^(-15)";
	public static final String lambda_c="2.426310218*10^(-12)";
	public static final String R_inf="10973731.57";
	public static final String u="1.660538782*10^(-27)";
	public static final String F="96485.3399";
	public static final String e="1.602176487*10^(-19)";
	public static final String NA="6.02214179*10^(23)";
	public static final String k="1.3806504*10^(-23)";
	public static final String Vm="0.022413996";
	public static final String R="8.314472";
	public static final String c="299792458";
	public static final String epsilon0="8.854187817*10^(-12)";
	public static final String mi0="1.256637061*10^(-6)";
	public static final String g="9.80665";
	public static final String G="6.67428*10^(-11)";
	public static final String fi0="2.067833667*10^(-15)";
	public static final String G0="7.7480917*10^(-5)";
	public static final String Z0="376.7303135";
	public static final String t0="273.15";
	public static final String atm="101325";
	
	
	public static boolean parsiraj(String n){
		if(n.equals("")) return false;
		char[] niz=n.toCharArray();		
		int p=0, l=niz.length;
		String stanje="S";
		String ulaz="", znak1, znak2, znak3, znak4, znak5;
		
		while(p<l){
			znak1=""+niz[p];
			
			if(p+2<=l) znak2=""+niz[p]+niz[p+1];
			else znak2="";
			
			if(p+3<=l) znak3=""+niz[p]+niz[p+1]+niz[p+2];
			else znak3="";
			
			if(p+4<=l) znak4=""+niz[p]+niz[p+1]+niz[p+2]+niz[p+3];
			else znak4="";
			
			if(p+5<=l) znak5=""+niz[p]+niz[p+1]+niz[p+2]+niz[p+3]+niz[p+4];
			else znak5="";
			
			
			if(znak5.equals("asin(") || znak5.equals("acos(") || znak5.equals("atan(") || znak5.equals("fact(")){
				ulaz="f1";
				p+=5;
			}else if(znak4.equals("sin(") || znak4.equals("cos(") || znak4.equals("tan(") || znak4.equals("abs(") ||
					 znak4.equals("log(") || znak4.equals("and(") || znak4.equals("xor(") || znak4.equals("not(")){
				ulaz="f1";
				p+=4;
			}else if(znak3.equals("ln(") || znak3.equals("or(") || znak3.equals("e^(") || znak3.equals("ans")){
				ulaz="f1";
				if(znak3.equals("ans")) ulaz="k";
				p+=3;
			}else if(znak2.equals("^(") || znak2.equals("<<") || znak2.equals(">>") || znak2.equals("\u221A(")){
				ulaz="op";
				if(znak2.equals("^(")) ulaz="f2";
				if(znak2.equals("\u221A(")) ulaz="f1";
				p+=2;
			}else if(znak1.charAt(0)>='0' && znak1.charAt(0)<='9' || znak1.charAt(0)>='A' && znak1.charAt(0)<='F' || 
					 znak1.equals("e") || znak1.equals("\u03C0") || znak1.equals("*") || znak1.equals("/") || znak1.equals("%") ||
					 znak1.equals("+") || znak1.equals("-") || znak1.equals("(") || znak1.equals(")") || znak1.equals("<") || 
					 znak1.equals(">") || znak1.equals(".")){
				ulaz="br";
				if(znak1.equals("*") || znak1.equals("/") || znak1.equals("%") || znak1.equals("<") ||znak1.equals(">")) ulaz="op";
				else if(znak1.equals("+") | znak1.equals("-")) ulaz="p";
				else if(znak1.equals("\u03C0") || znak1.equals("e")) ulaz="k";
				else if(znak1.equals(")")) ulaz="bc";
				else if(znak1.equals("(")) ulaz="f1";	
				else if(znak1.equals(".")) ulaz="t";
				p+=1;
			}else{
				return false;
			}
			
			stanje=nextState(stanje, ulaz);	
			if(stanje==null) return false;
		}
		
		return stanje.equals("BR") || stanje.equals("BC") || stanje.equals("K");		
	}
	
	private static String nextState(String st, String u){
		if(st.equals("S")){
			if(u.equals("br") || u.equals("f1") || u.equals("k") || u.equals("p")) return u.toUpperCase(Locale.getDefault());
			else return null;
		}else if(st.equals("BR")){
			if(u.equals("br") || u.equals("op") || u.equals("t") || u.equals("f2") || u.equals("bc") || u.equals("p")) return u.toUpperCase(Locale.getDefault());
			else return null;
		}else if(st.equals("OP")){
			if(u.equals("br") || u.equals("f1") || u.equals("k")) return u.toUpperCase(Locale.getDefault());
			else return null;
		}else if(st.equals("T")){
			if(u.equals("br")) return u.toUpperCase(Locale.getDefault());
			else return null;
		}else if(st.equals("F1")){
			if(u.equals("br") || u.equals("f1") || u.equals("k") || u.equals("p")) return u.toUpperCase(Locale.getDefault());
			else return null;
		}else if(st.equals("F2")){
			if(u.equals("br") || u.equals("f1") || u.equals("k") || u.equals("p")) return u.toUpperCase(Locale.getDefault());
			else return null;
		}else if(st.equals("K")){
			if(u.equals("br") || u.equals("op") || u.equals("t") || u.equals("f2") || u.equals("bc") || u.equals("p")) return u.toUpperCase(Locale.getDefault());
			else return null;
		}else if(st.equals("BC")){
			if(u.equals("op") || u.equals("f2") || u.equals("bc") || u.equals("p")) return u.toUpperCase(Locale.getDefault());
			else return null;
		}else if(st.equals("P")){
			if(u.equals("br") || u.equals("f1") || u.equals("k")) return u.toUpperCase(Locale.getDefault());
			else return null;
		}else{
			return null;
		}
	}
	
	
	
	public static double eval(String expression, boolean deg){		
		LinkedList<String> list=new LinkedList<String>();
		String izraz="";
		char[] c=expression.toCharArray();
		int l=c.length;
					
		for(int i=0;i<l;++i){
		    if(i==0 && c[i]=='-') izraz=izraz+"-"; //minus na pocetku
		    else if(i==0 && c[i]=='+') izraz=izraz+"+"; //plus na pocetku
		    else if(i!=0 && c[i-1]>='0' && c[i-1]<='9' && c[i]=='e') izraz=izraz+"E"; //znanstveni zapis
		    else if(i!=0 && c[i-1]=='e' && c[i]=='-') izraz=izraz+"-"; //negativan eksponent
		    else if(c[i]>='0' && c[i]<='9' || c[i]=='.') izraz=izraz+c[i]; //broj
			else{
				if(!izraz.equals("")) list.add(izraz);
				izraz="";
				
				if(c[i]=='('){
					String izraz2="";
					
					//find last close')'
					int x=c.length-1, opened=0, y;
					for(y=i+1;y<x;++y){
						
						if(c[y]==')'){
							--opened;
							if(opened==-1) break;
						}else if(c[y]=='(') ++opened;
					}
					
					for(i=i+1;i<y;++i) izraz2=izraz2+c[i];
					
					double pomRez=eval(izraz2, deg);
					list.add(""+pomRez);
				}
				
				//Functions				
				//Abs
				else if(c[i]=='a' && c[i+1]=='b' && c[i+2]=='s' && c[i+3]=='('){
					String izraz2="";

					int x=c.length-1, y, opened=0;
					for(y=i+4;y<x;++y){
						
						if(c[y]==')'){
							--opened;
							if(opened==-1) break;
						}else if(c[y]=='(') ++opened;
					}
					
					for(i=i+4;i<y;++i) izraz2=izraz2+c[i];
					
					double pomRez=Math.abs(eval(izraz2, deg));
						
					list.add(""+pomRez);
				}
				
				//Fact
				else if(c[i]=='f' && c[i+1]=='a' && c[i+2]=='c' && c[i+3]=='t' && c[i+4]=='('){
					String izraz2="";

					int x=c.length-1, y, opened=0;
					for(y=i+5;y<x;++y){
						
						if(c[y]==')'){
							--opened;
							if(opened==-1) break;
						}else if(c[y]=='(') ++opened;
					}
					
					for(i=i+5;i<y;++i) izraz2=izraz2+c[i];
					
					double pomRez=Math.abs(Math.round(eval(izraz2, deg)));
					if(pomRez<250) pomRez=fact(Math.abs(Math.round(pomRez)));
					else throw new NumberFormatException("To big factorial.");
						
					list.add(""+pomRez);
				}
				
				//sin
				else if(i+2<l && c[i]=='s' && c[i+1]=='i' && c[i+2]=='n' && c[i+3]=='('){
                    String izraz2="";

					int x=c.length-1, y, opened=0;
					for(y=i+4;y<x;++y){
						
						if(c[y]==')'){
							--opened;
							if(opened==-1) break;
						}else if(c[y]=='(') ++opened;
					}
					
					for(i=i+4;i<y;++i) izraz2=izraz2+c[i];
					
					double pomRez;
					if(deg) pomRez=Math.sin(Math.toRadians(eval(izraz2, deg)));
					else pomRez=Math.sin(eval(izraz2, deg));											
					
					list.add(""+pomRez);
					
			    //cos
				}else if(i+2<l && c[i]=='c' && c[i+1]=='o' && c[i+2]=='s' && c[i+3]=='('){
                    String izraz2="";

					int x=c.length-1, y, opened=0;
					for(y=i+4;y<x;++y){
						
						if(c[y]==')'){
							--opened;
							if(opened==-1) break;
						}else if(c[y]=='(') ++opened;
					}
					
					for(i=i+4;i<y;++i) izraz2=izraz2+c[i];
					
					double pomRez;
					if(deg) pomRez=Math.cos(Math.toRadians(eval(izraz2, deg)));
					else pomRez=Math.cos(eval(izraz2, deg));
					list.add(""+pomRez);
				}	
				
				//tan
				else if(i+2<l && c[i]=='t' && c[i+1]=='a' && c[i+2]=='n' && c[i+3]=='('){
	                String izraz2="";

					int x=c.length-1, y, opened=0;
					for(y=i+4;y<x;++y){
						
						if(c[y]==')'){
							--opened;
							if(opened==-1) break;
						}else if(c[y]=='(') ++opened;
					}
					
					for(i=i+4;i<y;++i) izraz2=izraz2+c[i];
					
					double pomRez;
					if(deg) pomRez=Math.tan(Math.toRadians(eval(izraz2, deg)));
					else pomRez=Math.tan(eval(izraz2, deg));
					list.add(""+pomRez);
				}
				
				//asin
				else if(i+3<l && c[i]=='a' && c[i+1]=='s' && c[i+2]=='i' && c[i+3]=='n' && c[i+4]=='('){
	                String izraz2="";

					int x=c.length-1, y, opened=0;
					for(y=i+5;y<x;++y){
						
						if(c[y]==')'){
							--opened;
							if(opened==-1) break;
						}else if(c[y]=='(') ++opened;
					}
					
					for(i=i+5;i<y;++i) izraz2=izraz2+c[i];
					
					double pomRez;
					if(deg) pomRez=Math.toDegrees(Math.asin(eval(izraz2, deg)));
					else pomRez=Math.asin(eval(izraz2, deg));
					list.add(""+pomRez);
				}
				
				//acos
				else if(i+3<l && c[i]=='a' && c[i+1]=='c' && c[i+2]=='o' && c[i+3]=='s' && c[i+4]=='('){
	                String izraz2="";

					int x=c.length-1, y, opened=0;
					for(y=i+5;y<x;++y){
						
						if(c[y]==')'){
							--opened;
							if(opened==-1) break;
						}else if(c[y]=='(') ++opened;
					}
					
					for(i=i+5;i<y;++i) izraz2=izraz2+c[i];
					
					double pomRez;
					if(deg) pomRez=Math.toDegrees(Math.acos(eval(izraz2, deg)));
					else pomRez=Math.acos(eval(izraz2, deg));
					list.add(""+pomRez);
				}
				
				//atan
				else if(i+3<l && c[i]=='a' && c[i+1]=='t' && c[i+2]=='a' && c[i+3]=='n' && c[i+4]=='('){
	                String izraz2="";

					int x=c.length-1, y, opened=0;
					for(y=i+5;y<x;++y){
						
						if(c[y]==')'){
							--opened;
							if(opened==-1) break;
						}else if(c[y]=='(') ++opened;
					}
					
					for(i=i+5;i<y;++i) izraz2=izraz2+c[i];
					
					double pomRez;
					if(deg) pomRez=Math.toDegrees(Math.atan(eval(izraz2, deg)));
					else pomRez=Math.atan(eval(izraz2, deg));
					list.add(""+pomRez);
				}
				
				//sqrt1
				else if(i+4<l && c[i]=='s' && c[i+1]=='q' && c[i+2]=='r'  && c[i+3]=='t' && c[i+4]=='('){
	                String izraz2="";

					int x=c.length-1, y, opened=0;
					for(y=i+5;y<x;++y){
						
						if(c[y]==')'){
							--opened;
							if(opened==-1) break;
						}else if(c[y]=='(') ++opened;
					}
					
					for(i=i+5;i<y;++i) izraz2=izraz2+c[i];
					
					double pomRez=Math.sqrt(eval(izraz2, deg));
					list.add(""+pomRez);
				}
				
				//sqrt2 (\\u)
				else if(c[i]==8730 && c[i+1]=='('){
	                String izraz2="";

					int x=c.length-1, y, opened=0;
					for(y=i+2;y<x;++y){
						
						if(c[y]==')'){
							--opened;
							if(opened==-1) break;
						}else if(c[y]=='(') ++opened;
					}
					
					for(i=i+2;i<y;++i) izraz2=izraz2+c[i];
					
					double pomRez=Math.sqrt(eval(izraz2, deg));
					list.add(""+pomRez);
				}
				
				//ln
				else if(i+1<l && c[i]=='l' && c[i+1]=='n' && c[i+2]=='('){
	                String izraz2="";

					int x=c.length-1, y, opened=0;
					for(y=i+3;y<x;++y){
						
						if(c[y]==')'){
							--opened;
							if(opened==-1) break;
						}else if(c[y]=='(') ++opened;
					}
					
					for(i=i+3;i<y;++i) izraz2=izraz2+c[i];

					double pomRez=Math.log(eval(izraz2, deg));
					list.add(""+pomRez);
				}
				
				//Log
				else if(c[i]=='l' && c[i+1]=='o' && c[i+2]=='g' && c[i+3]=='('){ 
					String izraz2="";
					int x=c.length-1, y, opened=0;										

					for(y=i+4;y<x;++y){						
						if(c[y]==')'){
							--opened;
							if(opened==-1) break;
						}else if(c[y]=='(') ++opened;
					}
					
					for(i=i+4;i<y;++i) izraz2=izraz2+c[i];
					
					double pomRez=Math.log10(eval(izraz2, deg));						
					list.add(""+pomRez);
				}
				
				//upTo
				else if(c[i]=='^' && c[i+1]=='('){
	                String izraz2="";

					int x=c.length-1, y, opened=0;
					for(y=i+2;y<x;++y){
						
						if(c[y]==')'){
							--opened;
							if(opened==-1) break;
						}else if(c[y]=='(') ++opened;
					}
					
					for(i=i+2;i<y;++i) izraz2=izraz2+c[i];

					double pomRez=eval(izraz2, deg);
					list.add("^");
					list.add(""+pomRez);
				}
				
				//Basic operators
				else if(c[i]=='+' || c[i]=='-' || c[i]=='*' || c[i]=='/') list.add(""+c[i]);		
			}
		}			
		if(!izraz.equals("")) list.add(izraz);
		
		//UpTo
		int n=list.size();
		while(list.contains("^")){
			for(int j=0;j<n;++j){			
				if(list.get(j).equals("^")){
					double num1=Double.parseDouble(list.get(j-1));
					double num2=Double.parseDouble(list.get(j+1));
					double rez=Math.pow(num1, num2);
					
					list.set(j-1, ""+rez);
					list.remove(j);
					j=j-1;
					list.remove(j+1);		
					
					n=list.size();
				}
			}
		}		
		
		//Mull and div
		while(list.contains("*") || list.contains("/")){
			for(int j=0;j<n;++j){			
				if(list.get(j).equals("*") || list.get(j).equals("/")){
					double num1=Double.parseDouble(list.get(j-1));
					double num2=Double.parseDouble(list.get(j+1));
					double rez;
					
					if(list.get(j).equals("*")) rez=num1*num2;
					else rez=num1/num2;
					
					list.set(j-1, ""+rez);
					list.remove(j);
					j=j-1;
					list.remove(j+1);		
					
					n=list.size();
				}
			}
		}
		
		//Add and sub
		while(list.contains("+") || list.contains("-")){
			for(int k=0;k<n-1;++k){			
				if(list.get(k).equals("+") || list.get(k).equals("-")){
					double num1=Double.parseDouble(list.get(k-1));
					double num2=Double.parseDouble(list.get(k+1));				
					double rez;
					
					if(list.get(k).equals("+")) rez=num1+num2;
					else rez=num1-num2;
					
					list.set(k-1, ""+rez);
					list.remove(k);
					k=k-1;
					list.remove(k+1);		
					
					n=list.size();
				}
			}
		}						
			
		return Double.parseDouble(list.get(0));
	}

	public static double round(String number, int round){
		if(!number.contains(".")) return Double.parseDouble(number);
		
		if(round==0){
			return Math.round(Double.parseDouble(number));
		}else{
			char[] c=number.toCharArray();
			String nFormat="";
			int l=c.length;
			
			for(int i=0;i<l;++i) 
				if(c[i]=='.'){
					nFormat=nFormat+".";
					for(int j=i+1;j<=i+round;++j){
						nFormat=nFormat+"#";
					}
					break;
				}
				else nFormat=nFormat+"#";
					
			
			if(number.contains("E")){
				String dec=number.substring(0, number.indexOf("E"));
				String exp=number.substring(number.indexOf("E"), number.length());
				
				DecimalFormat format=new DecimalFormat(nFormat);					
			    String result=format.format(Double.parseDouble(dec));
			    result=result.replace(',', '.');
				return Double.parseDouble(result+exp);
			}else{
				DecimalFormat format=new DecimalFormat(nFormat);					
			    String result=format.format(Double.parseDouble(number));
			    result=result.replace(',', '.');
				return Double.parseDouble(result);
			}		
		}			    
		
	}
	
		
	public static long eval(String expression, ProgrammerCalculator.Base base){		
		LinkedList<String> list=new LinkedList<String>();
		String izraz="";
		char[] c=expression.toCharArray();
		int l=c.length;
					
		for(int i=0;i<l;++i){
		    if(i==0 && c[i]=='-') izraz=izraz+"-"; //minus na pocetku
		    else if(i==0 && c[i]=='+') izraz=izraz+"+"; //plus na pocetku
		    else if(c[i]>='0' && c[i]<='9' || c[i]=='A' || c[i]=='B' || c[i]=='C' || c[i]=='D' || c[i]=='E' || c[i]=='F') izraz=izraz+c[i]; //broj
			else{
				
				if(!izraz.equals("")){
					if(base==ProgrammerCalculator.Base.HEX) list.add(""+Long.parseLong(izraz, 16));
					if(base==ProgrammerCalculator.Base.OCT) list.add(""+Long.parseLong(izraz, 8));
					if(base==ProgrammerCalculator.Base.DEC) list.add(""+Long.parseLong(izraz, 10));
					if(base==ProgrammerCalculator.Base.BIN) list.add(""+Long.parseLong(izraz, 2));
				}
				izraz="";
				
				if(c[i]=='('){
					String izraz2="";
					
					//find last close')'
					int x=c.length-1, opened=0, y;
					for(y=i+1;y<x;++y){
						
						if(c[y]==')'){
							--opened;
							if(opened==-1) break;
						}else if(c[y]=='(') ++opened;
					}
					
					for(i=i+1;i<y;++i) izraz2=izraz2+c[i];
					
					long pomRez=eval(izraz2, base);
					list.add(""+pomRez);

				}
				
				//not
				else if(i+2<l && c[i]=='n' && c[i+1]=='o' && c[i+2]=='t' && c[i+3]=='('){
	                String izraz2="";

					int x=c.length-1, y, opened=0;
					for(y=i+4;y<x;++y){
						
						if(c[y]==')'){
							--opened;
							if(opened==-1) break;
						}else if(c[y]=='(') ++opened;
					}
					
					for(i=i+4;i<y;++i) izraz2=izraz2+c[i];
					
					long pomRez=~eval(izraz2, base);
					list.add(""+pomRez);
				}
				
				//Basic operators
				else if(c[i]=='<' && c[i+1]=='<' || c[i]=='>' && c[i+1]=='>' || c[i]=='o' && c[i+1]=='r'){
					list.add(""+c[i]+c[i+1]);
					++i;
				}else if(c[i]=='+' || c[i]=='-' || c[i]=='*' || c[i]=='/' || c[i]=='%' || c[i]=='<' || c[i]=='>') list.add(""+c[i]);				
				else if(c[i]=='a' && c[i+1]=='n' && c[i+2]=='d' || c[i]=='x' && c[i+1]=='o' && c[i+2]=='r'){
					list.add(""+c[i]+c[i+1]+c[i+2]);	
					i=i+2;
				}else throw new NumberFormatException("Syntax error.");			
			}
		}	
		
		
		if(!izraz.equals("")){
			if(base==ProgrammerCalculator.Base.HEX) list.add(""+Long.parseLong(izraz, 16));
			if(base==ProgrammerCalculator.Base.OCT) list.add(""+Long.parseLong(izraz, 8));
			if(base==ProgrammerCalculator.Base.DEC) list.add(""+Long.parseLong(izraz, 10));
			if(base==ProgrammerCalculator.Base.BIN) list.add(""+Long.parseLong(izraz, 2));
		}
		
		int n=list.size();	
		
		//RoR, RoL
		while(list.contains("<<") || list.contains(">>")){
			for(int j=0;j<n;++j){			
				if(list.get(j).equals("<<") || list.get(j).equals(">>")){
					long num1=Long.parseLong(list.get(j-1));
					long num2=Long.parseLong(list.get(j+1));
					long rez=0;										
					
					if(list.get(j).equals("<<")) rez=Long.rotateLeft(num1, (int)num2);
					else rez=Long.rotateRight(num1, (int)num2);
					
					list.set(j-1, ""+rez);
					list.remove(j);
					j=j-1;
					list.remove(j+1);		
					
					n=list.size();
				}
			}
		}		
		
		//Mod, Ls, Rs
		while(list.contains("%") || list.contains("<") || list.contains(">")){
			for(int j=0;j<n;++j){			
				if(list.get(j).equals("%") || list.get(j).equals("<") || list.get(j).equals(">")){
					long num1=Long.parseLong(list.get(j-1));
					long num2=Long.parseLong(list.get(j+1));
					long rez;
										
					if(list.get(j).equals("<")) rez=num1<<num2;
					else if(list.get(j).equals(">")) rez=num1>>num2;
					else rez=num1%num2;
					
					list.set(j-1, ""+rez);
					list.remove(j);
					j=j-1;
					list.remove(j+1);		
					
					n=list.size();
				}
			}
		}		
		
		//and, or, xor
		while(list.contains("and") || list.contains("or") || list.contains("xor")){
			for(int j=0;j<n;++j){			
				if(list.get(j).equals("and") || list.get(j).equals("or") || list.get(j).equals("xor")){
					long num1=Long.parseLong(list.get(j-1));
					long num2=Long.parseLong(list.get(j+1));
					long rez;
										
					if(list.get(j).equals("and")) rez=num1&num2;
					else if(list.get(j).equals("or")) rez=num1|num2;
					else rez=num1^num2;
					
					list.set(j-1, ""+rez);
					list.remove(j);
					j=j-1;
					list.remove(j+1);		
					
					n=list.size();
				}
			}
		}		
		
		//Mull and div
		while(list.contains("*") || list.contains("/")){
			for(int j=0;j<n;++j){			
				if(list.get(j).equals("*") || list.get(j).equals("/")){
					long num1=Long.parseLong(list.get(j-1));
					long num2=Long.parseLong(list.get(j+1));
					long rez;
					
					if(list.get(j).equals("*")) rez=num1*num2;
					else rez=num1/num2;
					
					list.set(j-1, ""+rez);
					list.remove(j);
					j=j-1;
					list.remove(j+1);		
					
					n=list.size();
				}
			}
		}
		
		//Add and sub
		while(list.contains("+") || list.contains("-")){
			for(int k=0;k<n-1;++k){			
				if(list.get(k).equals("+") || list.get(k).equals("-")){
					long num1=Long.parseLong(list.get(k-1));
					long num2=Long.parseLong(list.get(k+1));				
					long rez;
					
					if(list.get(k).equals("+")) rez=num1+num2;
					else rez=num1-num2;
					
					list.set(k-1, ""+rez);
					list.remove(k);
					k=k-1;
					list.remove(k+1);		
					
					n=list.size();
				}
			}
		}						
			
		return Long.parseLong(list.get(0));						
	}
	
	
	public static String convertConstantc(String expression, double ans){
		String newExpression=expression.replace("e", ""+Math.E);
		newExpression=newExpression.replace("\u03C0", ""+Math.PI);
		newExpression=newExpression.replace("ans", ""+ans);
		return newExpression;
	}
	
	private static long fact(long n){
		if(n==1 || n==0) return 1;
		else return n*fact(n-1);
	}
	

	public static String equ_x1(double a, double b, double c, int rd){
		double x, y;
		if(rd==-1 || rd>9) rd=9;
		
		x=round(""+((-1)*b/(2*a)), rd);
		y=b*b-4*a*c;
		if(y<0){
			y=Math.sqrt(Math.abs(y))/(2*a);
			y=round(""+y, rd);
			return ""+x+" + "+y+" i";
		}else{
			y=Math.sqrt(y)/(2*a);
			y=round(""+(x+y), rd);
			return ""+y;
		}
	}
	
	public static String equ_x2(double a, double b, double c, int rd){
        double x, y;
        if(rd==-1 || rd>9) rd=9;
		
        x=round(""+((-1)*b/(2*a)), rd);
		y=b*b-4*a*c;
		if(y<0){
			y=Math.sqrt(Math.abs(y))/(2*a);
			y=round(""+y, rd);
			return ""+x+" - "+y+" i";
		}else{
			y=Math.sqrt(y)/(2*a);
			y=round(""+(x-y), rd);
			return ""+y;
		}
	}
	
}
