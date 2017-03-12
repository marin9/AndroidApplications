package tictactoe.game;

public class Game5 implements IGame{
	private Sign[] board, pom_board;
	private int n, bestMove;
	private Status status;
	
	
	public Game5(){
		board=new Sign[25];
		pom_board=new Sign[25];
		reset();
	}
	
	@Override
	public void reset(){
		status=Status.X_TURN;
		n=0;
		for(int i=0;i<25;++i){
			board[i]=pom_board[i]=Sign.N;			
		}
	}
	
	@Override
	public Status getStatus(){
		return status;
	}
	
	@Override
	public Sign getSign(int x, int y){
		return pom_board[y*5+x];
	}
	
	@Override
	public void move(int x, int y){
		if(status!=Status.X_TURN && status!=Status.O_TURN || board[y*5+x]!=Sign.N) return;
		
		if(status==Status.X_TURN){
			board[y*5+x]=pom_board[y*5+x]=Sign.X;
			status=Status.O_TURN;
		}else{
			board[y*5+x]=pom_board[y*5+x]=Sign.O;
			status=Status.X_TURN;
		}
		++n;
				
		if(isXwin()) status=Status.X_WIN;
		else if(isOwin()) status=Status.O_WIN;
		else if(n==25) status=Status.DRAW;
	}
	
		
	
	private boolean isXwin(){
		
		if(board[0]==Sign.X && board[1]==Sign.X && board[2]==Sign.X && board[3]==Sign.X) return true;
		else if(board[5]==Sign.X && board[6]==Sign.X && board[7]==Sign.X && board[8]==Sign.X) return true;
		else if(board[10]==Sign.X && board[11]==Sign.X && board[12]==Sign.X && board[13]==Sign.X) return true;
		else if(board[15]==Sign.X && board[16]==Sign.X && board[17]==Sign.X && board[18]==Sign.X) return true;
		else if(board[20]==Sign.X && board[21]==Sign.X && board[22]==Sign.X && board[23]==Sign.X) return true;
		else if(board[1]==Sign.X && board[2]==Sign.X && board[3]==Sign.X && board[4]==Sign.X) return true;
		else if(board[6]==Sign.X && board[7]==Sign.X && board[8]==Sign.X && board[9]==Sign.X) return true;
		else if(board[11]==Sign.X && board[12]==Sign.X && board[13]==Sign.X && board[14]==Sign.X) return true;
		else if(board[16]==Sign.X && board[17]==Sign.X && board[18]==Sign.X && board[19]==Sign.X) return true;
		else if(board[21]==Sign.X && board[22]==Sign.X && board[23]==Sign.X && board[24]==Sign.X) return true;
		
		else if(board[0]==Sign.X && board[5]==Sign.X && board[10]==Sign.X && board[15]==Sign.X) return true;
		else if(board[1]==Sign.X && board[6]==Sign.X && board[11]==Sign.X && board[16]==Sign.X) return true;
		else if(board[2]==Sign.X && board[7]==Sign.X && board[12]==Sign.X && board[17]==Sign.X) return true;
		else if(board[3]==Sign.X && board[8]==Sign.X && board[13]==Sign.X && board[18]==Sign.X) return true;
		else if(board[4]==Sign.X && board[9]==Sign.X && board[14]==Sign.X && board[19]==Sign.X) return true;
		else if(board[5]==Sign.X && board[10]==Sign.X && board[15]==Sign.X && board[20]==Sign.X) return true;
		else if(board[6]==Sign.X && board[11]==Sign.X && board[16]==Sign.X && board[21]==Sign.X) return true;
		else if(board[7]==Sign.X && board[12]==Sign.X && board[17]==Sign.X && board[22]==Sign.X) return true;
		else if(board[8]==Sign.X && board[13]==Sign.X && board[18]==Sign.X && board[23]==Sign.X) return true;
		else if(board[9]==Sign.X && board[14]==Sign.X && board[19]==Sign.X && board[24]==Sign.X) return true;
		
		else if(board[0]==Sign.X && board[6]==Sign.X && board[12]==Sign.X && board[18]==Sign.X) return true;
		else if(board[5]==Sign.X && board[11]==Sign.X && board[17]==Sign.X && board[23]==Sign.X) return true;
		else if(board[1]==Sign.X && board[7]==Sign.X && board[13]==Sign.X && board[19]==Sign.X) return true;
		else if(board[6]==Sign.X && board[12]==Sign.X && board[18]==Sign.X && board[24]==Sign.X) return true;		
		
		else if(board[4]==Sign.X && board[8]==Sign.X && board[12]==Sign.X && board[16]==Sign.X) return true;
		else if(board[9]==Sign.X && board[13]==Sign.X && board[17]==Sign.X && board[21]==Sign.X) return true;
		else if(board[3]==Sign.X && board[7]==Sign.X && board[11]==Sign.X && board[15]==Sign.X) return true;
		else if(board[8]==Sign.X && board[12]==Sign.X && board[16]==Sign.X && board[20]==Sign.X) return true;
		
		return false;
	}
	
	private boolean isOwin(){

		if(board[0]==Sign.O && board[1]==Sign.O && board[2]==Sign.O && board[3]==Sign.O) return true;
		else if(board[5]==Sign.O && board[6]==Sign.O && board[7]==Sign.O && board[8]==Sign.O) return true;
		else if(board[10]==Sign.O && board[11]==Sign.O && board[12]==Sign.O && board[13]==Sign.O) return true;
		else if(board[15]==Sign.O && board[16]==Sign.O && board[17]==Sign.O && board[18]==Sign.O) return true;
		else if(board[20]==Sign.O && board[21]==Sign.O && board[22]==Sign.O && board[23]==Sign.O) return true;
		else if(board[1]==Sign.O && board[2]==Sign.O && board[3]==Sign.O && board[4]==Sign.O) return true;
		else if(board[6]==Sign.O && board[7]==Sign.O && board[8]==Sign.O && board[9]==Sign.O) return true;
		else if(board[11]==Sign.O && board[12]==Sign.O && board[13]==Sign.O && board[14]==Sign.O) return true;
		else if(board[16]==Sign.O && board[17]==Sign.O && board[18]==Sign.O && board[19]==Sign.O) return true;
		else if(board[21]==Sign.O && board[22]==Sign.O && board[23]==Sign.O && board[24]==Sign.O) return true;
		
		else if(board[0]==Sign.O && board[5]==Sign.O && board[10]==Sign.O && board[15]==Sign.O) return true;
		else if(board[1]==Sign.O && board[6]==Sign.O && board[11]==Sign.O && board[16]==Sign.O) return true;
		else if(board[2]==Sign.O && board[7]==Sign.O && board[12]==Sign.O && board[17]==Sign.O) return true;
		else if(board[3]==Sign.O && board[8]==Sign.O && board[13]==Sign.O && board[18]==Sign.O) return true;
		else if(board[4]==Sign.O && board[9]==Sign.O && board[14]==Sign.O && board[19]==Sign.O) return true;
		else if(board[5]==Sign.O && board[10]==Sign.O && board[15]==Sign.O && board[20]==Sign.O) return true;
		else if(board[6]==Sign.O && board[11]==Sign.O && board[16]==Sign.O && board[21]==Sign.O) return true;
		else if(board[7]==Sign.O && board[12]==Sign.O && board[17]==Sign.O && board[22]==Sign.O) return true;
		else if(board[8]==Sign.O && board[13]==Sign.O && board[18]==Sign.O && board[23]==Sign.O) return true;
		else if(board[9]==Sign.O && board[14]==Sign.O && board[19]==Sign.O && board[24]==Sign.O) return true;
		
		else if(board[0]==Sign.O && board[6]==Sign.O && board[12]==Sign.O && board[18]==Sign.O) return true;
		else if(board[5]==Sign.O && board[11]==Sign.O && board[17]==Sign.O && board[23]==Sign.O) return true;
		else if(board[1]==Sign.O && board[7]==Sign.O && board[13]==Sign.O && board[19]==Sign.O) return true;
		else if(board[6]==Sign.O && board[12]==Sign.O && board[18]==Sign.O && board[24]==Sign.O) return true;		
		
		else if(board[4]==Sign.O && board[8]==Sign.O && board[12]==Sign.O && board[16]==Sign.O) return true;
		else if(board[9]==Sign.O && board[13]==Sign.O && board[17]==Sign.O && board[21]==Sign.O) return true;
		else if(board[3]==Sign.O && board[7]==Sign.O && board[11]==Sign.O && board[15]==Sign.O) return true;
		else if(board[8]==Sign.O && board[12]==Sign.O && board[16]==Sign.O && board[20]==Sign.O) return true;	
		
		return false;
	}

	@Override
	public void playBestMove() {
		bestMove=-1;
		minimax(0, status==Status.X_TURN? true : false, -100, 100);
		move(bestMove%5, bestMove/5);
		return;
	} 
	
	
	private int minimax(int depth, boolean x_turn, int alpha, int beta){
		if(isXwin()) return 26-depth;
		else if(isOwin()) return depth-26;
		else if(n==25 || depth==9) return 0;
		
		int tmpScore, bestScore= x_turn? -26 : 26;
		
		for(int i=0;i<25;++i){	 			
			if(board[i]==Sign.N){    	
				board[i]= x_turn? Sign.X : Sign.O;
				++n;

				if(Thread.currentThread().isInterrupted()) return 119;
				tmpScore=minimax(depth+1, !x_turn, alpha, beta);
				if(tmpScore==119){
					bestMove=0;
					return 119;
				}
				if(x_turn && tmpScore>bestScore || !x_turn && tmpScore<bestScore){
					bestScore=tmpScore;
					
					if(x_turn){
						alpha= bestScore>alpha? bestScore: alpha;
					}else{
						beta= bestScore<beta? bestScore: beta;
					}
					
					if(depth==0) bestMove=i;
				}

				board[i]=Sign.N;	
				--n;
			}	
			
			if(beta<=alpha) break;
		} 		
		
		return bestScore;	   			
	}

}
