package marin.bralic.game;

public class Game {
	public static int MOVE=0;
	public static int CHESS=1;
	public static int MATE=2;
    public static int PAT=3;
	public static int DRAW=4;
	
	private int status;
	private char turn;
	private Pieces[][] board;
	private GameMemory memory;
	
	private boolean castlingW_left, castlingW_right, castlingB_left, castlingB_right;
	private int enPassantByW, enPassantByB;

	private int  moveNumber, moveUk;
	private int wKingX, wKingY, bKingX, bKingY;	
	private boolean[][] allowedMoves;
	private int availableMoviesNumber;
	
	private volatile boolean selected;
	private int selectedX, selectedY;
	
	
	public Game(){
		board=new Pieces[8][8];
		allowedMoves=new boolean[8][8];
		memory=new GameMemory();
		
		clearBoard();
	}
	
	public void clearBoard(){
		for(int i=0;i<8;++i){
			for(int j=0;j<8;++j){
				board[i][j]=Pieces.FREE;
			}
		}
	}
	
	public void reset(){
		clearBoard();
		clearAllowed();
		memory.clear();
		
		status=MOVE;
		turn='w';
		
		board[0][0]=board[7][0]=Pieces.ROCK_B;
		board[1][0]=board[6][0]=Pieces.HORSE_B;
		board[2][0]=board[5][0]=Pieces.HUNTER_B;
		board[3][0]=Pieces.QUEEN_B;
		board[4][0]=Pieces.KING_B;
		
		for(int i=0;i<8;++i){
			board[i][1]=Pieces.PAWN_B;
			board[i][6]=Pieces.PAWN_W;
		}
		
		board[0][7]=board[7][7]=Pieces.ROCK_W;
		board[1][7]=board[6][7]=Pieces.HORSE_W;
		board[2][7]=board[5][7]=Pieces.HUNTER_W;
		board[3][7]=Pieces.QUEEN_W;
		board[4][7]=Pieces.KING_W;
		
		castlingW_left=castlingW_right=castlingB_left=castlingB_right=true;
		enPassantByW=enPassantByB=-1;		
		moveNumber=moveUk=0;
		
		wKingX=bKingX=4;
		wKingY=7;
		bKingY=0;

		selected=false;
		selectedX=selectedY=-1;		
		
	}

	private void clearAllowed(){
		for(int i=0;i<8;++i){
			for(int j=0;j<8;++j){
				allowedMoves[i][j]=false;
			}
		}
	}
	
	public int getUkMoveNUmber(){
		return moveUk;
	}
	
	
	public int getStatus(){
		return status;
	}
	
	public char getTurn(){
		return turn;
	}
			
	public boolean isSelected(){
		return selected;
	}
	
	public int getSelectedX(){
		return selectedX;
	}
	
	public int getSelectedY(){
		return selectedY;
	}
	
	public Pieces getPieces(int x, int y){
		return board[x][y];
	}
		
	public boolean isAllowed(int x, int y){
		return allowedMoves[x][y];
	}
	
	public void select(int x, int y, char your_color){
		if(your_color=='w' && Pieces.isBlack(board[x][y])   ||  
		   your_color=='b' && Pieces.isWhite(board[x][y])  ||   
		   your_color!=getTurn() 	 ||   
		   board[x][y]==Pieces.FREE  ||  selected || status==MATE || status==DRAW || status==PAT) return;		

		selectedX=x;
		selectedY=y;		
		selected=true;
		getAllowed(x, y);
	}
	
	public void disselect(){
		clearAllowed();
		selected=false;
	}
	
	public int move(int x, int y, Pieces newPieces) throws IllegalMoveException{			
		if(x==selectedX && y==selectedY || !selected || !allowedMoves[x][y]){	
			selected=false;
			clearAllowed();
			throw new IllegalMoveException("Can't move to this position: "+x+", "+y);
		}		
		selected=false;
		
		if(turn=='b'){
			++moveNumber;			
		}		
		++moveUk;
		
		if(board[selectedX][selectedY]==Pieces.PAWN_B || board[selectedX][selectedY]==Pieces.PAWN_W) moveNumber=0;
		
		//en passant enabled
		if(turn=='w'){
			if(board[selectedX][selectedY]==Pieces.PAWN_W && (selectedY-y)==2) enPassantByB=x;
			else enPassantByB=-1;
		}else{
			if(board[selectedX][selectedY]==Pieces.PAWN_B && (y-selectedY)==2) enPassantByW=x;
			else enPassantByW=-1;
		}
		
		
		//remove castling
		if(board[selectedX][selectedY]==Pieces.ROCK_B && selectedX==0 && selectedY==0) castlingB_left=false;
		else if(board[selectedX][selectedY]==Pieces.ROCK_B && selectedX==7 && selectedY==0) castlingB_right=false;
		else if(board[selectedX][selectedY]==Pieces.ROCK_W && selectedX==0 && selectedY==7) castlingW_left=false;
		else if(board[selectedX][selectedY]==Pieces.ROCK_W && selectedX==7 && selectedY==7) castlingW_right=false;
		else if(board[selectedX][selectedY]==Pieces.KING_B && selectedX==4 && selectedY==0) castlingB_right=castlingB_left=false;
		else if(board[selectedX][selectedY]==Pieces.KING_W && selectedX==4 && selectedY==7) castlingW_right=castlingW_left=false;
		
		//move
		if(board[x][y]!=Pieces.FREE && turn=='w'){
			moveNumber=0;
		}else if(board[x][y]!=Pieces.FREE && turn=='b'){
			moveNumber=0;
		}
		board[x][y]=board[selectedX][selectedY];
		board[selectedX][selectedY]=Pieces.FREE;
		//promote pawn
		if(board[x][y]==Pieces.PAWN_B && y==7){
			if(newPieces==Pieces.ROCK_B || newPieces==Pieces.ROCK_W) board[x][y]=Pieces.ROCK_B;
			else if(newPieces==Pieces.HORSE_B || newPieces==Pieces.HORSE_W) board[x][y]=Pieces.HORSE_B;
			else if(newPieces==Pieces.HUNTER_B || newPieces==Pieces.HUNTER_W) board[x][y]=Pieces.HUNTER_B;
			else{
				board[x][y]=Pieces.QUEEN_B;
			}
		}else if(board[x][y]==Pieces.PAWN_W && y==0){
			if(newPieces==Pieces.ROCK_B || newPieces==Pieces.ROCK_W) board[x][y]=Pieces.ROCK_W;
			else if(newPieces==Pieces.HORSE_B || newPieces==Pieces.HORSE_W) board[x][y]=Pieces.HORSE_W;
			else if(newPieces==Pieces.HUNTER_B || newPieces==Pieces.HUNTER_W) board[x][y]=Pieces.HUNTER_W;
			else{
				board[x][y]=Pieces.QUEEN_W;
			}
		}
		//refresh king position
		if(board[x][y]==Pieces.KING_W){
			wKingX=x;
			wKingY=y;
		}
		if(board[x][y]==Pieces.KING_B){
			bKingX=x;
			bKingY=y;
		}
		//en passant eat
		if(board[x][y]==Pieces.PAWN_W && board[x][y+1]==Pieces.PAWN_B){
			moveNumber=0;
			board[x][y+1]=Pieces.FREE;
		}else if(board[x][y]==Pieces.PAWN_B && board[x][y-1]==Pieces.PAWN_W){
			moveNumber=0;
			board[x][y-1]=Pieces.FREE;
		}
				
		//castling
		if(selectedX==4 && selectedY==0 && x==2 && y==0){
			board[0][0]=Pieces.FREE;
			board[3][0]=Pieces.ROCK_B;
		}else if(selectedX==4 && selectedY==0 && x==6 && y==0){
			board[7][0]=Pieces.FREE;
			board[5][0]=Pieces.ROCK_B;
		}else if(selectedX==4 && selectedY==7 && x==2 && y==7){
			board[0][7]=Pieces.FREE;
			board[3][7]=Pieces.ROCK_W;
		}else if(selectedX==4 && selectedY==7 && x==6 && y==7){
			board[7][7]=Pieces.FREE;
			board[5][7]=Pieces.ROCK_W;
		}
		 
		//change turn
		if(turn=='w') turn='b';
		else turn='w';	
		
		//refresh status
		clearAllowed();
		if(turn=='w'){
			countAvailableMoves('w');
			if(isFieldAttackedByBlack(wKingX, wKingY) && availableMoviesNumber==0) status=MATE;
			else if(isFieldAttackedByBlack(wKingX, wKingY) && availableMoviesNumber>0) status=CHESS;
			else if(!isFieldAttackedByBlack(wKingX, wKingY) && availableMoviesNumber==0) status=PAT;
			else if(moveNumber>50) status=DRAW;
			else status=MOVE;
		}else{
			countAvailableMoves('b');
			if(isFieldAttackedByWhite(bKingX, bKingY) && availableMoviesNumber==0) status=MATE;
			else if(isFieldAttackedByWhite(bKingX, bKingY) && availableMoviesNumber>0) status=CHESS;
			else if(!isFieldAttackedByWhite(bKingX, bKingY) && availableMoviesNumber==0) status=PAT;
			else if(moveNumber>50) status=DRAW;
			else status=MOVE;	
		}			
		
		if(status==MOVE && isDraw()) status=DRAW;
		
		clearAllowed();		
		return status;
	}
		
	
	private boolean isDraw(){
		int wL=0, wS=0;
		int bL=0, bS=0;
				
		boolean threeSameTurn=memory.add(turn, board, castlingW_right, castlingW_left, castlingB_right, castlingB_left, enPassantByW, enPassantByB);	
		if(threeSameTurn) return true;
		
		for(int i=0;i<8;++i){
			for(int j=0;j<8;++j){
				if(board[i][j]==Pieces.PAWN_B || board[i][j]==Pieces.PAWN_W || 
				   board[i][j]==Pieces.ROCK_B || board[i][j]==Pieces.ROCK_W || 
				   board[i][j]==Pieces.QUEEN_B || board[i][j]==Pieces.QUEEN_W){
					return false;
				}else if(board[i][j]==Pieces.HUNTER_W) ++wL;
				else if(board[i][j]==Pieces.HUNTER_B) ++bL;
				else if(board[i][j]==Pieces.HORSE_W) ++wS;
				else if(board[i][j]==Pieces.HORSE_B) ++bS;				
			}
		}				
		
		if(bL>1 || wL>1 || wS>1 || bS>1 || (wL==1 && wS==1) || (bL==1 && bS==1)) return false;
		
		return true;
	}
	
	private void countAvailableMoves(char c){
		availableMoviesNumber=0;
				
		if(c=='w'){
			for(int i=0;i<8;++i){
				for(int j=0;j<8;++j){
					if(board[i][j]!=Pieces.FREE && Pieces.isWhite(board[i][j])){
						availableMoviesNumber=availableMoviesNumber+getAllowed(i, j);	
					}
				}
			}		
		}else{
			for(int i=0;i<8;++i){
				for(int j=0;j<8;++j){
					if(board[i][j]!=Pieces.FREE && Pieces.isBlack(board[i][j])){
						availableMoviesNumber=availableMoviesNumber+getAllowed(i, j);
					}
				}
			}		
		}
	}
	
	private int getAllowed(int x, int y){    
		int s=0;
		//Pawn movies
		if(board[x][y]==Pieces.PAWN_W){
			if(y-1>=0 && board[x][y-1]==Pieces.FREE) allowedMoves[x][y-1]=true; 
			if(y==6 && board[x][y-1]==Pieces.FREE && board[x][y-2]==Pieces.FREE) allowedMoves[x][y-2]=true; 
			if(x-1>=0 && y-1>=0 && (Pieces.isBlack(board[x-1][y-1]) || y==3 && enPassantByW==(x-1))) allowedMoves[x-1][y-1]=true; 
			if(x+1<=7 && y-1>=0 && (Pieces.isBlack(board[x+1][y-1]) || y==3 && enPassantByW==(x+1))) allowedMoves[x+1][y-1]=true; 					
			
		}else if(board[x][y]==Pieces.PAWN_B){
			if(y+1<=7 && board[x][y+1]==Pieces.FREE) allowedMoves[x][y+1]=true; 
			if(y==1 && board[x][y+1]==Pieces.FREE && board[x][y+2]==Pieces.FREE) allowedMoves[x][y+2]=true; 
			if(x-1>=0 && y+1<=7 && (Pieces.isWhite(board[x-1][y+1]) || y==4 && enPassantByB==(x-1))) allowedMoves[x-1][y+1]=true; 
			if(x+1<=7 && y+1<=7 && (Pieces.isWhite(board[x+1][y+1]) || y==4 && enPassantByB==(x+1))) allowedMoves[x+1][y+1]=true; 
			
		//Horse movies	
		}else if(board[x][y]==Pieces.HORSE_W){
			if(x-2>=0 && y-1>=0 && (board[x-2][y-1]==Pieces.FREE || Pieces.isBlack(board[x-2][y-1]))) allowedMoves[x-2][y-1]=true;	
			if(x-1>=0 && y-2>=0 && (board[x-1][y-2]==Pieces.FREE || Pieces.isBlack(board[x-1][y-2]))) allowedMoves[x-1][y-2]=true;
			if(x+1<=7 && y-2>=0 && (board[x+1][y-2]==Pieces.FREE || Pieces.isBlack(board[x+1][y-2]))) allowedMoves[x+1][y-2]=true;
			if(x+2<=7 && y-1>=0 && (board[x+2][y-1]==Pieces.FREE || Pieces.isBlack(board[x+2][y-1]))) allowedMoves[x+2][y-1]=true;
			if(x+2<=7 && y+1<=7 && (board[x+2][y+1]==Pieces.FREE || Pieces.isBlack(board[x+2][y+1]))) allowedMoves[x+2][y+1]=true;
			if(x+1<=7 && y+2<=7 && (board[x+1][y+2]==Pieces.FREE || Pieces.isBlack(board[x+1][y+2]))) allowedMoves[x+1][y+2]=true;
			if(x-1>=0 && y+2<=7 && (board[x-1][y+2]==Pieces.FREE || Pieces.isBlack(board[x-1][y+2]))) allowedMoves[x-1][y+2]=true;
			if(x-2>=0 && y+1<=7 && (board[x-2][y+1]==Pieces.FREE || Pieces.isBlack(board[x-2][y+1]))) allowedMoves[x-2][y+1]=true;			
		}else if(board[x][y]==Pieces.HORSE_B){
			if(x-2>=0 && y-1>=0 && (board[x-2][y-1]==Pieces.FREE || Pieces.isWhite(board[x-2][y-1]))) allowedMoves[x-2][y-1]=true;	
			if(x-1>=0 && y-2>=0 && (board[x-1][y-2]==Pieces.FREE || Pieces.isWhite(board[x-1][y-2]))) allowedMoves[x-1][y-2]=true;
			if(x+1<=7 && y-2>=0 && (board[x+1][y-2]==Pieces.FREE || Pieces.isWhite(board[x+1][y-2]))) allowedMoves[x+1][y-2]=true;
			if(x+2<=7 && y-1>=0 && (board[x+2][y-1]==Pieces.FREE || Pieces.isWhite(board[x+2][y-1]))) allowedMoves[x+2][y-1]=true;
			if(x+2<=7 && y+1<=7 && (board[x+2][y+1]==Pieces.FREE || Pieces.isWhite(board[x+2][y+1]))) allowedMoves[x+2][y+1]=true;
			if(x+1<=7 && y+2<=7 && (board[x+1][y+2]==Pieces.FREE || Pieces.isWhite(board[x+1][y+2]))) allowedMoves[x+1][y+2]=true;
			if(x-1>=0 && y+2<=7 && (board[x-1][y+2]==Pieces.FREE || Pieces.isWhite(board[x-1][y+2]))) allowedMoves[x-1][y+2]=true;
			if(x-2>=0 && y+1<=7 && (board[x-2][y+1]==Pieces.FREE || Pieces.isWhite(board[x-2][y+1]))) allowedMoves[x-2][y+1]=true;		
				
		//Hunter move
		}else if(board[x][y]==Pieces.HUNTER_W || board[x][y]==Pieces.HUNTER_B){
			int px=x-1;
			int py=y-1;
			
			while(px>=0 && py>=0){
				if(board[px][py]==Pieces.FREE) allowedMoves[px][py]=true; 
				if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.HUNTER_W || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.HUNTER_B){
					allowedMoves[px][py]=true; 
					break;
				}else if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.HUNTER_B || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.HUNTER_W){
					break;
				}
				--px;
				--py;
			}				
						
			px=x+1;
			py=y-1;
			
			while(px<=7 && py>=0){
				if(board[px][py]==Pieces.FREE) allowedMoves[px][py]=true; 
				if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.HUNTER_W || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.HUNTER_B){
					allowedMoves[px][py]=true; 
					break;
				}else if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.HUNTER_B || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.HUNTER_W){
					break;
				}
				++px;
				--py;
			}
			
			px=x+1;
			py=y+1;
			
			while(px<=7 && py<=7){
				if(board[px][py]==Pieces.FREE) allowedMoves[px][py]=true; 
				if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.HUNTER_W || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.HUNTER_B){
					allowedMoves[px][py]=true; 
					break;
				}else if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.HUNTER_B || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.HUNTER_W){
					break;
				}
				++px;
				++py;
			}
			
			px=x-1;
			py=y+1;
			
			while(px>=0 && py<=7){
				if(board[px][py]==Pieces.FREE) allowedMoves[px][py]=true; 
				if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.HUNTER_W || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.HUNTER_B){
					allowedMoves[px][py]=true; 	
					break;
				}else if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.HUNTER_B || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.HUNTER_W){
					break;
				}
				--px;
				++py;
			}
			
		//Rock move	
		}else if(board[x][y]==Pieces.ROCK_W || board[x][y]==Pieces.ROCK_B){
			int px=x;
			int py=y-1;
			
			while(py>=0){
				if(board[px][py]==Pieces.FREE) allowedMoves[px][py]=true; 
				if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.ROCK_W || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.ROCK_B){
					allowedMoves[px][py]=true; 	
					break;
				}else if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.ROCK_B || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.ROCK_W){
					break;
				}
				--py;
			}
			
			px=x+1;
			py=y;
			
			while(px<=7){
				if(board[px][py]==Pieces.FREE) allowedMoves[px][py]=true; 
				if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.ROCK_W || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.ROCK_B){
					allowedMoves[px][py]=true; 
					break;
				}else if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.ROCK_B || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.ROCK_W){
					break;
				}
				++px;
			}
			
			px=x;
			py=y+1;
			
			while(py<=7){
				if(board[px][py]==Pieces.FREE) allowedMoves[px][py]=true; 
				if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.ROCK_W || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.ROCK_B){
					allowedMoves[px][py]=true; 	
					break;
				}else if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.ROCK_B || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.ROCK_W){
					break;
				}
				++py;
			}
			
			px=x-1;
			py=y;
			
			while(px>=0){
				if(board[px][py]==Pieces.FREE) allowedMoves[px][py]=true; 
				if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.ROCK_W || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.ROCK_B){
					allowedMoves[px][py]=true; 
					break;
				}else if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.ROCK_B || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.ROCK_W){
					break;
				}
				--px;
			}
			
		//Queen move
		}else if(board[x][y]==Pieces.QUEEN_W || board[x][y]==Pieces.QUEEN_B){
			int px=x-1;
			int py=y-1;
			
			while(px>=0 && py>=0){
				if(board[px][py]==Pieces.FREE) allowedMoves[px][py]=true; 
				if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.QUEEN_W || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.QUEEN_B){
					allowedMoves[px][py]=true; 
					break;
				}else if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.QUEEN_B || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.QUEEN_W){
					break;
				}
				--px;
				--py;
			}				
						
			px=x+1;
			py=y-1;
			
			while(px<=7 && py>=0){
				if(board[px][py]==Pieces.FREE) allowedMoves[px][py]=true; 
				if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.QUEEN_W || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.QUEEN_B){
					allowedMoves[px][py]=true; 	
					break;
				}else if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.QUEEN_B || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.QUEEN_W){
					break;
				}
				++px;
				--py;
			}
			
			px=x+1;
			py=y+1;
			
			while(px<=7 && py<=7){
				if(board[px][py]==Pieces.FREE) allowedMoves[px][py]=true; 
				if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.QUEEN_W || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.QUEEN_B){
					allowedMoves[px][py]=true; 	
					break;
				}else if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.QUEEN_B || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.QUEEN_W){
					break;
				}
				++px;
				++py;
			}
			
			px=x-1;
			py=y+1;
			
			while(px>=0 && py<=7){
				if(board[px][py]==Pieces.FREE) allowedMoves[px][py]=true; 
				if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.QUEEN_W || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.QUEEN_B){
					allowedMoves[px][py]=true; 
					break;
				}else if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.QUEEN_B || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.QUEEN_W){
					break;
				}
				--px;
				++py;
			}
			
			
			px=x;
			py=y-1;
			
			while(py>=0){
				if(board[px][py]==Pieces.FREE) allowedMoves[px][py]=true; 
				if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.QUEEN_W || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.QUEEN_B){
					allowedMoves[px][py]=true; 
					break;
				}else if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.QUEEN_B || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.QUEEN_W){
					break;
				}
				--py;
			}
			
			px=x+1;
			py=y;
			
			while(px<=7){
				if(board[px][py]==Pieces.FREE) allowedMoves[px][py]=true; 
				if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.QUEEN_W || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.QUEEN_B){
					allowedMoves[px][py]=true; 	
					break;
				}else if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.QUEEN_B || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.QUEEN_W){
					break;
				}
				++px;
			}
			
			px=x;
			py=y+1;
			
			while(py<=7){
				if(board[px][py]==Pieces.FREE) allowedMoves[px][py]=true; 
				if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.QUEEN_W || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.QUEEN_B){
					allowedMoves[px][py]=true; 	
					break;
				}else if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.QUEEN_B || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.QUEEN_W){
					break;
				}
				++py;
			}
			
			px=x-1;
			py=y;
			
			while(px>=0){
				if(board[px][py]==Pieces.FREE) allowedMoves[px][py]=true; 
				if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.QUEEN_W || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.QUEEN_B){
					allowedMoves[px][py]=true; 	
					break;
				}else if(Pieces.isBlack(board[px][py]) && board[x][y]==Pieces.QUEEN_B || Pieces.isWhite(board[px][py]) && board[x][y]==Pieces.QUEEN_W){
					break;
				}
				--px;
			}

		}else if(board[x][y]==Pieces.KING_W){	
			if(x-1>=0 && y-1>=0 && ((board[x-1][y-1]==Pieces.FREE ||  Pieces.isBlack(board[x-1][y-1])) && !isFieldAttackedByBlack(x-1, y-1))) allowedMoves[x-1][y-1]=true;
			if(y-1>=0 && ((board[x][y-1]==Pieces.FREE || Pieces.isBlack(board[x][y-1])) &&  !isFieldAttackedByBlack(x, y-1))) allowedMoves[x][y-1]=true;
			if(x+1<=7 && y-1>=0 && ((board[x+1][y-1]==Pieces.FREE || Pieces.isBlack(board[x+1][y-1])) && !isFieldAttackedByBlack(x+1, y-1))) allowedMoves[x+1][y-1]=true;
			if(x+1<=7 && ((board[x+1][y]==Pieces.FREE || Pieces.isBlack(board[x+1][y])) && !isFieldAttackedByBlack(x+1, y))) allowedMoves[x+1][y]=true;
			if(x+1<=7 && y+1<=7 && ((board[x+1][y+1]==Pieces.FREE || Pieces.isBlack(board[x+1][y+1])) && !isFieldAttackedByBlack(x+1, y+1))) allowedMoves[x+1][y+1]=true;
			if(y+1<=7 && ((board[x][y+1]==Pieces.FREE || Pieces.isBlack(board[x][y+1])) && !isFieldAttackedByBlack(x, y+1))) allowedMoves[x][y+1]=true;
			if(x-1>=0 && y+1<=7 && ((board[x-1][y+1]==Pieces.FREE || Pieces.isBlack(board[x-1][y+1])) && !isFieldAttackedByBlack(x-1, y+1))) allowedMoves[x-1][y+1]=true;
			if(x-1>=0 && ((board[x-1][y]==Pieces.FREE || Pieces.isBlack(board[x-1][y])) && !isFieldAttackedByBlack(x-1, y))) allowedMoves[x-1][y]=true;
			
			if(y==7 && x==4   &&  board[7][7]==Pieces.ROCK_W && !isFieldAttackedByBlack(4, 7) && !isFieldAttackedByBlack(5, 7) && !isFieldAttackedByBlack(6, 7) && castlingW_right && board[5][7]==Pieces.FREE && board[6][7]==Pieces.FREE) allowedMoves[x+2][y]=true;
			if(y==7 && x==4   &&  board[0][7]==Pieces.ROCK_W && !isFieldAttackedByBlack(4, 7) && !isFieldAttackedByBlack(3, 7) && !isFieldAttackedByBlack(2, 7) && castlingW_left && board[3][7]==Pieces.FREE && board[2][7]==Pieces.FREE && board[1][7]==Pieces.FREE) allowedMoves[x-2][y]=true;
			
		}else if(board[x][y]==Pieces.KING_B){
			if(x-1>=0 && y-1>=0 && ((board[x-1][y-1]==Pieces.FREE ||  Pieces.isWhite(board[x-1][y-1])) && !isFieldAttackedByWhite(x-1, y-1))) allowedMoves[x-1][y-1]=true;
			if(y-1>=0 && ((board[x][y-1]==Pieces.FREE || Pieces.isWhite(board[x][y-1])) &&  !isFieldAttackedByWhite(x, y-1))) allowedMoves[x][y-1]=true;
			if(x+1<=7 && y-1>=0 && ((board[x+1][y-1]==Pieces.FREE || Pieces.isWhite(board[x+1][y-1])) && !isFieldAttackedByWhite(x+1, y-1))) allowedMoves[x+1][y-1]=true;
			if(x+1<=7 && ((board[x+1][y]==Pieces.FREE || Pieces.isWhite(board[x+1][y])) && !isFieldAttackedByWhite(x+1, y))) allowedMoves[x+1][y]=true;
			if(x+1<=7 && y+1<=7 && ((board[x+1][y+1]==Pieces.FREE || Pieces.isWhite(board[x+1][y+1])) && !isFieldAttackedByWhite(x+1, y+1))) allowedMoves[x+1][y+1]=true;
			if(y+1<=7 && ((board[x][y+1]==Pieces.FREE || Pieces.isWhite(board[x][y+1])) && !isFieldAttackedByWhite(x, y+1))) allowedMoves[x][y+1]=true;
			if(x-1>=0 && y+1<=7 && ((board[x-1][y+1]==Pieces.FREE || Pieces.isWhite(board[x-1][y+1])) && !isFieldAttackedByWhite(x-1, y+1))) allowedMoves[x-1][y+1]=true;
			if(x-1>=0 && ((board[x-1][y]==Pieces.FREE || Pieces.isWhite(board[x-1][y])) && !isFieldAttackedByWhite(x-1, y))) allowedMoves[x-1][y]=true;
			
			if(y==0 && x==4   &&  board[7][0]==Pieces.ROCK_B  && !isFieldAttackedByWhite(4, 0) && !isFieldAttackedByWhite(5, 0) && !isFieldAttackedByWhite(6, 0) && castlingB_right && board[5][0]==Pieces.FREE && board[6][0]==Pieces.FREE) allowedMoves[x+2][y]=true;
			if(y==0 && x==4   &&  board[0][0]==Pieces.ROCK_B  && !isFieldAttackedByWhite(4, 0) && !isFieldAttackedByWhite(3, 0) && !isFieldAttackedByWhite(2, 0) && castlingB_left && board[3][0]==Pieces.FREE && board[2][0]==Pieces.FREE && board[1][0]==Pieces.FREE) allowedMoves[x-2][y]=true;
		}		
		
		for(int i=0;i<8;++i){
			for(int j=0;j<8;++j){				
				if(allowedMoves[i][j]==false) continue;
				
				//save state
				Pieces pomSecond=board[i][j];
				Pieces pomFirst=board[x][y];
				int pom_wKingX=wKingX, pom_wKingY=wKingY, pom_bKingX=bKingX, pom_bKingY=bKingY;				
				
				//do move
				if(board[x][y]==Pieces.KING_W){
					pom_wKingX=i;
					pom_wKingY=j;
				}else if(board[x][y]==Pieces.KING_B){
					pom_bKingX=i;
					pom_bKingY=j;
				}
				board[i][j]=board[x][y];
				board[x][y]=Pieces.FREE;
				
				if(turn=='w') turn='b';
				else turn='w';
		    	
				//if
				if(turn=='w' && isFieldAttackedByWhite(pom_bKingX, pom_bKingY) || turn=='b' && isFieldAttackedByBlack(pom_wKingX, pom_wKingY)) allowedMoves[i][j]=false;						
				
				//back state
				board[i][j]=pomSecond;
				board[x][y]=pomFirst;
				if(turn=='w') turn='b';
				else turn='w';
				
				
				if(allowedMoves[i][j]) ++s;
			}
		}
		
		return s;
	}
	
	private boolean isFieldAttackedByWhite(int x, int y){
		//Pawn attack
		if(x-1>=0 && y+1<=7 && board[x-1][y+1]==Pieces.PAWN_W) return true;
		if(x+1<=7 && y+1<=7 && board[x+1][y+1]==Pieces.PAWN_W) return true;
		
		//Horse attack
		if(x-2>=0 && y-1>=0 && board[x-2][y-1]==Pieces.HORSE_W) return true;
		if(x-1>=0 && y-2>=0 && board[x-1][y-2]==Pieces.HORSE_W) return true;
		if(x+1<=7 && y-2>=0 && board[x+1][y-2]==Pieces.HORSE_W) return true;
		if(x+2<=7 && y-1>=0 && board[x+2][y-1]==Pieces.HORSE_W) return true;
		if(x+2<=7 && y+1<=7 && board[x+2][y+1]==Pieces.HORSE_W) return true;
		if(x+1<=7 && y+2<=7 && board[x+1][y+2]==Pieces.HORSE_W) return true;
		if(x-1>=0 && y+2<=7 && board[x-1][y+2]==Pieces.HORSE_W) return true;
		if(x-2>=0 && y+1<=7 && board[x-2][y+1]==Pieces.HORSE_W) return true;
		
		//Hunter attack
		int px=x-1, py=y-1;		
		while(px>=0 && py>=0 && !Pieces.isBlack(board[px][py])){
			if(Pieces.isWhite(board[px][py]) && board[px][py]!=Pieces.HUNTER_W && board[px][py]!=Pieces.QUEEN_W) break;
			if(Pieces.isWhite(board[px][py]) && (board[px][py]==Pieces.HUNTER_W || board[px][py]==Pieces.QUEEN_W)) return true;		
			--px;
			--py;
		}	
		
        px=x+1; py=y-1;		
		while(px<=7 && py>=0 && !Pieces.isBlack(board[px][py])){
			if(Pieces.isWhite(board[px][py]) && board[px][py]!=Pieces.HUNTER_W && board[px][py]!=Pieces.QUEEN_W) break;
			if(Pieces.isWhite(board[px][py]) && (board[px][py]==Pieces.HUNTER_W || board[px][py]==Pieces.QUEEN_W)) return true;
			++px;
			--py;
		}	
		
		px=x+1; py=y+1;		
		while(px<=7 && py<=7 && !Pieces.isBlack(board[px][py])){
			if(Pieces.isWhite(board[px][py]) && board[px][py]!=Pieces.HUNTER_W && board[px][py]!=Pieces.QUEEN_W) break;
			if(Pieces.isWhite(board[px][py]) && (board[px][py]==Pieces.HUNTER_W || board[px][py]==Pieces.QUEEN_W)) return true;	
			++px;
			++py;
		}	
		
		px=x-1; py=y+1;		
		while(px>=0 && py<=7 && !Pieces.isBlack(board[px][py])){
			if(Pieces.isWhite(board[px][py]) && board[px][py]!=Pieces.HUNTER_W && board[px][py]!=Pieces.QUEEN_W) break;
			if(Pieces.isWhite(board[px][py]) && (board[px][py]==Pieces.HUNTER_W || board[px][py]==Pieces.QUEEN_W)) return true;
			--px;
			++py;
		}	
		
		//Rock attack
		px=x; py=y-1;		
		while(py>=0 && !Pieces.isBlack(board[px][py])){
			if(Pieces.isWhite(board[px][py]) && board[px][py]!=Pieces.ROCK_W && board[px][py]!=Pieces.QUEEN_W) break;
			if(Pieces.isWhite(board[px][py]) && (board[px][py]==Pieces.ROCK_W || board[px][py]==Pieces.QUEEN_W)) return true;
			--py;
		}
		
		px=x+1; py=y;		
		while(px<=7 && !Pieces.isBlack(board[px][py])){
			if(Pieces.isWhite(board[px][py]) && board[px][py]!=Pieces.ROCK_W && board[px][py]!=Pieces.QUEEN_W) break;
			if(Pieces.isWhite(board[px][py]) && (board[px][py]==Pieces.ROCK_W || board[px][py]==Pieces.QUEEN_W)) return true;
			++px;
		}
		
		px=x; py=y+1;		
		while(py<=7 && !Pieces.isBlack(board[px][py])){
			if(Pieces.isWhite(board[px][py]) && board[px][py]!=Pieces.ROCK_W && board[px][py]!=Pieces.QUEEN_W) break;
			if(Pieces.isWhite(board[px][py]) && (board[px][py]==Pieces.ROCK_W || board[px][py]==Pieces.QUEEN_W)) return true;
			++py;
		}
		
		px=x-1; py=y;		
		while(px>=0 && !Pieces.isBlack(board[px][py])){
			if(Pieces.isWhite(board[px][py]) && board[px][py]!=Pieces.ROCK_W && board[px][py]!=Pieces.QUEEN_W) break;
			if(Pieces.isWhite(board[px][py]) && (board[px][py]==Pieces.ROCK_W || board[px][py]==Pieces.QUEEN_W)) return true;
			--px;
		}	

		//King attack
		if(x-1>=0 && y-1>=0 && board[x-1][y-1]==Pieces.KING_W) return true; 
		if(y-1>=0 && board[x][y-1]==Pieces.KING_W) return true; 
		if(x+1<=7 && y-1>=0 && board[x+1][y-1]==Pieces.KING_W) return true; 
		if(x+1<=7 && board[x+1][y]==Pieces.KING_W) return true; 
		if(x+1<=7 && y+1<=7 && board[x+1][y+1]==Pieces.KING_W) return true; 
		if(y+1<=7 && board[x][y+1]==Pieces.KING_W) return true; 
		if(x-1>=0 && y+1<=7 && board[x-1][y+1]==Pieces.KING_W) return true; 
		if(x-1>=0 && board[x-1][y]==Pieces.KING_W) return true; 		
				
		return false;
	}
	
    private boolean isFieldAttackedByBlack(int x, int y){	
		//Pawn attack
		if(x-1>=0 && y-1>=0 && board[x-1][y-1]==Pieces.PAWN_B) return true;
		if(x+1<=7 && y-1>=0 && board[x+1][y-1]==Pieces.PAWN_B) return true;
		
		//Horse attack
		if(x-2>=0 && y-1>=0 && board[x-2][y-1]==Pieces.HORSE_B) return true;
		if(x-1>=0 && y-2>=0 && board[x-1][y-2]==Pieces.HORSE_B) return true;
		if(x+1<=7 && y-2>=0 && board[x+1][y-2]==Pieces.HORSE_B) return true;
		if(x+2<=7 && y-1>=0 && board[x+2][y-1]==Pieces.HORSE_B) return true;
		if(x+2<=7 && y+1<=7 && board[x+2][y+1]==Pieces.HORSE_B) return true;
		if(x+1<=7 && y+2<=7 && board[x+1][y+2]==Pieces.HORSE_B) return true;
		if(x-1>=0 && y+2<=7 && board[x-1][y+2]==Pieces.HORSE_B) return true;
		if(x-2>=0 && y+1<=7 && board[x-2][y+1]==Pieces.HORSE_B) return true;
		
		//Hunter attack
		int px=x-1, py=y-1;		
		while(px>=0 && py>=0 && !Pieces.isWhite(board[px][py])){
			if(Pieces.isBlack(board[px][py]) && board[px][py]!=Pieces.HUNTER_B && board[px][py]!=Pieces.QUEEN_B) break;
			if(Pieces.isBlack(board[px][py]) && (board[px][py]==Pieces.HUNTER_B || board[px][py]==Pieces.QUEEN_B)) return true;		
			--px;
			--py;
		}	
		
        px=x+1; py=y-1;		
		while(px<=7 && py>=0 && !Pieces.isWhite(board[px][py])){
			if(Pieces.isBlack(board[px][py]) && board[px][py]!=Pieces.HUNTER_B && board[px][py]!=Pieces.QUEEN_B) break;
			if(Pieces.isBlack(board[px][py]) && (board[px][py]==Pieces.HUNTER_B || board[px][py]==Pieces.QUEEN_B)) return true;
			++px;
			--py;
		}	
		
		px=x+1; py=y+1;		
		while(px<=7 && py<=7 && !Pieces.isWhite(board[px][py])){
			if(Pieces.isBlack(board[px][py]) && board[px][py]!=Pieces.HUNTER_B && board[px][py]!=Pieces.QUEEN_B) break;
			if(Pieces.isBlack(board[px][py]) && (board[px][py]==Pieces.HUNTER_B || board[px][py]==Pieces.QUEEN_B)) return true;	
			++px;
			++py;
		}	
		
		px=x-1; py=y+1;		
		while(px>=0 && py<=7 && !Pieces.isWhite(board[px][py])){
			if(Pieces.isBlack(board[px][py]) && board[px][py]!=Pieces.HUNTER_B && board[px][py]!=Pieces.QUEEN_B) break;
			if(Pieces.isBlack(board[px][py]) && (board[px][py]==Pieces.HUNTER_B || board[px][py]==Pieces.QUEEN_B)) return true;
			--px;
			++py;
		}	
		
		//Rock attack
		px=x; py=y-1;		
		while(py>=0 && !Pieces.isWhite(board[px][py])){
			if(Pieces.isBlack(board[px][py]) && board[px][py]!=Pieces.ROCK_B && board[px][py]!=Pieces.QUEEN_B) break;
			if(Pieces.isBlack(board[px][py]) && (board[px][py]==Pieces.ROCK_B || board[px][py]==Pieces.QUEEN_B)) return true;
			--py;
		}
		
		px=x+1; py=y;		
		while(px<=7 && !Pieces.isWhite(board[px][py])){
			if(Pieces.isBlack(board[px][py]) && board[px][py]!=Pieces.ROCK_B && board[px][py]!=Pieces.QUEEN_B) break;
			if(Pieces.isBlack(board[px][py]) && (board[px][py]==Pieces.ROCK_B || board[px][py]==Pieces.QUEEN_B)) return true;
			++px;
		}
		
		px=x; py=y+1;		
		while(py<=7 && !Pieces.isWhite(board[px][py])){
			if(Pieces.isBlack(board[px][py]) && board[px][py]!=Pieces.ROCK_B && board[px][py]!=Pieces.QUEEN_B) break;
			if(Pieces.isBlack(board[px][py]) && (board[px][py]==Pieces.ROCK_B || board[px][py]==Pieces.QUEEN_B)) return true;
			++py;
		}
		
		px=x-1; py=y;		
		while(px>=0 && !Pieces.isWhite(board[px][py])){
			if(Pieces.isBlack(board[px][py]) && board[px][py]!=Pieces.ROCK_B && board[px][py]!=Pieces.QUEEN_B) break;
			if(Pieces.isBlack(board[px][py]) && (board[px][py]==Pieces.ROCK_B || board[px][py]==Pieces.QUEEN_B)) return true;
			--px;
		}	

		//King attack
		if(x-1>=0 && y-1>=0 && board[x-1][y-1]==Pieces.KING_B) return true; 
		if(y-1>=0 && board[x][y-1]==Pieces.KING_B) return true; 
		if(x+1<=7 && y-1>=0 && board[x+1][y-1]==Pieces.KING_B) return true; 
		if(x+1<=7 && board[x+1][y]==Pieces.KING_B) return true; 
		if(x+1<=7 && y+1<=7 && board[x+1][y+1]==Pieces.KING_B) return true; 
		if(y+1<=7 && board[x][y+1]==Pieces.KING_B) return true; 
		if(x-1>=0 && y+1<=7 && board[x-1][y+1]==Pieces.KING_B) return true; 
		if(x-1>=0 && board[x-1][y]==Pieces.KING_B) return true; 		
				
		return false;
	}	

}
