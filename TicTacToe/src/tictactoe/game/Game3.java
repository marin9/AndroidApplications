package tictactoe.game;

import java.util.Random;

public class Game3 implements IGame{
	private Random rand;
	private Sign[] board, pom_board;
	private int n, bestMove;
	private Status status;
	
	
	public Game3(){		
		board=new Sign[9];
		pom_board=new Sign[9];
		rand=new Random();
		reset();
	}
	
	@Override
	public void reset(){		
		status=Status.X_TURN;
		n=0;
		for(int i=0;i<9;++i){
			board[i]=pom_board[i]=Sign.N;			
		}
	}
	
	@Override
	public Status getStatus(){
		return status;
	}
	
	@Override
	public Sign getSign(int x, int y){		
		return pom_board[y*3+x];
	}
	
	@Override
	public void move(int x, int y) {
		if(status!=Status.X_TURN && status!=Status.O_TURN || board[y*3+x]!=Sign.N) return;
		
		if(status==Status.X_TURN){
			board[y*3+x]=pom_board[y*3+x]=Sign.X;
			status=Status.O_TURN;
		}else{
			board[y*3+x]=pom_board[y*3+x]=Sign.O;
			status=Status.X_TURN;
		}
		++n;
				
		if(isXwin()) status=Status.X_WIN;
		else if(isOwin()) status=Status.O_WIN;
		else if(n==9) status=Status.DRAW;
	}
	
	
	
	private boolean isXwin(){
		
		if(board[0]==Sign.X && board[1]==Sign.X && board[2]==Sign.X) return true;
		else if(board[3]==Sign.X && board[4]==Sign.X && board[5]==Sign.X) return true;
		else if(board[6]==Sign.X && board[7]==Sign.X && board[8]==Sign.X) return true;
		
		else if(board[0]==Sign.X && board[3]==Sign.X && board[6]==Sign.X) return true;
		else if(board[1]==Sign.X && board[4]==Sign.X && board[7]==Sign.X) return true;
		else if(board[2]==Sign.X && board[5]==Sign.X && board[8]==Sign.X) return true;
		
		else if(board[0]==Sign.X && board[4]==Sign.X && board[8]==Sign.X) return true;
		else if(board[2]==Sign.X && board[4]==Sign.X && board[6]==Sign.X) return true;
		
		return false;
	}
	
	private boolean isOwin(){
		
		if(board[0]==Sign.O && board[1]==Sign.O && board[2]==Sign.O) return true;
		else if(board[3]==Sign.O && board[4]==Sign.O && board[5]==Sign.O) return true;
		else if(board[6]==Sign.O && board[7]==Sign.O && board[8]==Sign.O) return true;
		
		else if(board[0]==Sign.O && board[3]==Sign.O && board[6]==Sign.O) return true;
		else if(board[1]==Sign.O && board[4]==Sign.O && board[7]==Sign.O) return true;
		else if(board[2]==Sign.O && board[5]==Sign.O && board[8]==Sign.O) return true;
		
		else if(board[0]==Sign.O && board[4]==Sign.O && board[8]==Sign.O) return true;
		else if(board[2]==Sign.O && board[4]==Sign.O && board[6]==Sign.O) return true;
		
		return false;
	}

	@Override
	public void playBestMove() {
		if(n==0){
			int[] moves=new int[]{0, 2, 6, 8};
			int i=Math.abs(rand.nextInt())%4;
			bestMove=moves[i];	
			move(bestMove%3, bestMove/3);
		}else{
			bestMove=-1;
			minimax(0, status==Status.X_TURN? true : false, -100, 100);
			move(bestMove%3, bestMove/3);			
		}	
		return;
	} 
	
	//time=9
	private int minimax(int depth, boolean x_turn, int alpha, int beta){
		if(isXwin()) return 10-depth;
		else if(isOwin()) return depth-10;
		else if(n==9) return 0;
		
		int tmpScore, bestScore= x_turn? -10 : 10;
		
		for(int i=0;i<9;++i){	 			
			if(board[i]==Sign.N){    	
				board[i]= x_turn? Sign.X : Sign.O;
				++n;

				tmpScore=minimax(depth+1, !x_turn, alpha, beta);
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
	
	/*
	 * time=70ms
	 private int minimax(int depth, boolean x_turn){
		if(isXwin()) return 10-depth;
		else if(isOwin()) return depth-10;
		else if(n==9) return 0;
		
		int tmpScore, bestScore= x_turn? -20 : 20;
		
		for(int i=0;i<9;++i){	 			
			if(board[i]==Sign.N){    	
				board[i]= x_turn? Sign.X : Sign.O;
				++n;

				tmpScore=minimax(depth+1, !x_turn);
				if(x_turn && tmpScore>bestScore || !x_turn && tmpScore<bestScore){
					bestScore=tmpScore;
					if(depth==0) bestMove=i;
				}

				board[i]=Sign.N;	
				--n;
			}	    
		} 		
		
		return bestScore;	   			
	}
	 * 
	 */
	
	/*time=250ms
	private int minimax(int depth, boolean x_turn){
		if(isXwin()) return 10-depth;
		else if(isOwin()) return depth-10;
		else if(n==9) return 0;
		
		List<Integer> moves=new ArrayList<Integer>();
		List<Integer> score=new ArrayList<Integer>();
		++depth;
		
		for(int i=0;i<9;++i){	 			
			if(board[i]!=Sign.N) continue;	    	
			else{    		
				if(x_turn) board[i]=Sign.X;	    		
				else board[i]=Sign.O;
				++n;

				score.add(minimax(depth, !x_turn));	    		
				moves.add(i);
				board[i]=Sign.N;	
				--n;
			}	    
		} 
		
				
		if(x_turn){	    	
			int max_score=score.get(0).intValue(), index=0;
			
			for(int j=0;j<score.size();++j){
				if(score.get(j).intValue()>max_score){
					index=j;
					max_score=score.get(j).intValue();
				}
			}
			
			bestMove=moves.get(index).intValue();	    	
			return max_score;	    
			
		}else{	    	
			int min_score=score.get(0).intValue(), index=0;
			
			for(int j=0;j<score.size();++j){
				if(score.get(j).intValue()<min_score){
					index=j;
					min_score=score.get(j).intValue();
				}
			}
			
			bestMove=moves.get(index).intValue();	    	
			return min_score;	    
		}		
	}*/


}
