package tictactoe.game;

public interface IGame {
		
	public Status getStatus();
	public Sign getSign(int x, int y);
	
	public void reset();
	public void move(int x, int y);
    public void playBestMove();

}
