package marin.bralic.game;

public enum Pieces {
	
	KING_W(), QUEEN_W(), ROCK_W(), HUNTER_W(), HORSE_W(), PAWN_W(),

	KING_B(), QUEEN_B(), ROCK_B(), HUNTER_B(), HORSE_B(), PAWN_B(),
	
	FREE();
	
	public static boolean isWhite(Pieces p){
		if(p==KING_W || p==QUEEN_W || p==ROCK_W || p==HUNTER_W || p==HORSE_W || p==PAWN_W) return true;
		else return false;
	}
	
	public static boolean isBlack(Pieces p){
		if(p==KING_B || p==QUEEN_B || p==ROCK_B || p==HUNTER_B || p==HORSE_B || p==PAWN_B) return true;
		else return false;
	}
}
