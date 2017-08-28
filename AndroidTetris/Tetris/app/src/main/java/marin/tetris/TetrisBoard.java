package marin.tetris;

import java.util.Random;
@SuppressWarnings("ManualArrayCopy")
class TetrisBoard {
    private final int COLUMNS=10;
    private final int ROWS=20;
    private final int[][][] O;
    private final int[][][] I;
    private final int[][][] S;
    private final int[][][] Z;
    private final int[][][] J;
    private final int[][][] L;
    private final int[][][] T;

    private SoundManager soundManager;
    private Random rand;
    private int[][] board;
    private int[][] current;
    private int orientation;
    private int currentX;
    private int currentY;

    private boolean gameOver;
    private boolean pause;
    private boolean showNext;
    private int next;
    private int drop;
    private int score;
    private int lines;
    private int level;
    private int _level;
    private int count;


    TetrisBoard(SoundManager soundManager){
        this.soundManager=soundManager;
        rand=new Random();
        board=new int[ROWS][COLUMNS];
        current=new int[4][4];

        O=new int[][][]{{  {0, 0, 0, 0}, {0, 1, 1, 0}, {0, 1, 1, 0}, {0, 0, 0, 0}   }};

        I=new int[][][]{    {{0, 0, 0, 0}, {2, 2, 2, 2}, {0, 0, 0, 0}, {0, 0, 0, 0}},
                            {{0, 0, 2, 0}, {0, 0, 2, 0}, {0, 0, 2, 0}, {0, 0, 2, 0}} };

        S=new int[][][]{    {{0, 0, 0, 0}, {0, 0, 3, 3}, {0, 3, 3, 0}, {0, 0, 0, 0}},
                            {{0, 0, 3, 0}, {0, 0, 3, 3}, {0, 0, 0, 3}, {0, 0, 0, 0}} };

        Z=new int[][][]{    {{0, 0, 0, 0}, {0, 4, 4, 0}, {0, 0, 4, 4}, {0, 0, 0, 0}},
                             {{0, 0, 0, 4}, {0, 0, 4, 4}, {0, 0, 4, 0}, {0, 0, 0, 0}} };

        L=new int[][][]{{  {0, 0, 0, 0}, {0, 5, 5, 5}, {0, 5, 0, 0}, {0, 0, 0, 0}   },
                        {  {0, 0, 5, 0}, {0, 0, 5, 0}, {0, 0, 5, 5}, {0, 0, 0, 0}   },
                        {  {0, 0, 0, 5}, {0, 5, 5, 5}, {0, 0, 0, 0}, {0, 0, 0, 0}   },
                        {  {0, 5, 5, 0}, {0, 0, 5, 0}, {0, 0, 5, 0}, {0, 0, 0, 0}   }};


        J=new int[][][]{{  {0, 0, 0, 0}, {0, 6, 6, 6}, {0, 0, 0, 6}, {0, 0, 0, 0}   },
                        {  {0, 0, 6, 6}, {0, 0, 6, 0}, {0, 0, 6, 0}, {0, 0, 0, 0}   },
                        {  {0, 6, 0, 0}, {0, 6, 6, 6}, {0, 0, 0, 0}, {0, 0, 0, 0}   },
                        {  {0, 0, 6, 0}, {0, 0, 6, 0}, {0, 6, 6, 0}, {0, 0, 0, 0}   }};

        T=new int[][][]{{  {0, 0, 0, 0}, {0, 7, 7, 7}, {0, 0, 7, 0}, {0, 0, 0, 0}   },
                        {  {0, 0, 7, 0}, {0, 0, 7, 7}, {0, 0, 7, 0}, {0, 0, 0, 0}   },
                        {  {0, 0, 7, 0}, {0, 7, 7, 7}, {0, 0, 0, 0}, {0, 0, 0, 0}   },
                        {  {0, 0, 7, 0}, {0, 7, 7, 0}, {0, 0, 7, 0}, {0, 0, 0, 0}   }};
    }

    // level argument: [0-9]
    void reset(int level, boolean show_next){
        this.level=level+1;
        showNext=show_next;

        for(int y=0;y<ROWS;++y){
            for(int x=0;x<COLUMNS;++x){
                board[y][x]=0;
            }
        }

        for(int y=0;y<4;++y){
            for(int x=0;x<4;++x){
                current[y][x]=0;
            }
        }

        this.next=Math.abs(rand.nextInt())%7+1;
        gameOver=false;
        pause=false;
        score=0;
        lines=0;
        _level=1;

        int actualLevel=Math.max(level, _level);
        count=50*(11-actualLevel);
    }

    void step(){
        if(gameOver || pause) return;
        count-=50;
        if(count>0) return;

        int actualLevel=Math.max(level, _level);
        count=50*(11-actualLevel);

        if(current[1][2]!=0){
            if(fallingPossible()){
                ++currentY;
                drop=currentY;
            }else{
                fixToBoard();
                checkFullRows();
            }
        }else{
            if(!createNewPieces()) gameOver=true;
        }
    }

    void rotate(){
        if(gameOver || pause || current[1][2]==0) return;

        int[][] _current=new int[4][4];
        int len;
        int currentType=current[1][2];

        if(currentType==1) len=1;
        else if(currentType>=2 && currentType<=4) len=2;
        else len=4;

        int _orientation=(orientation+1)%len;
        for(int y=0;y<4;++y){
            for(int x=0;x<4;++x){
                if(currentType==1)      _current[y][x]=O[_orientation][y][x];
                else if(currentType==2) _current[y][x]=I[_orientation][y][x];
                else if(currentType==3) _current[y][x]=S[_orientation][y][x];
                else if(currentType==4) _current[y][x]=Z[_orientation][y][x];
                else if(currentType==5) _current[y][x]=L[_orientation][y][x];
                else if(currentType==6) _current[y][x]=J[_orientation][y][x];
                else if(currentType==7) _current[y][x]=T[_orientation][y][x];
            }
        }

        if(!collision(currentX, currentY, _current)){
            orientation=_orientation;
            for(int y=0;y<4;++y){
                for(int x=0;x<4;++x){
                    current[y][x]=_current[y][x];
                }
            }
        }
    }

    void down(){
        if(gameOver || pause || current[1][2]==0) return;
        drop=currentY;

        while(fallingPossible()) ++currentY;
    }

    void left(){
        if(gameOver || pause || current[1][2]==0) return;
        if(!collision(currentX-1, currentY, current)) --currentX;
    }

    void right(){
        if(gameOver || pause || current[1][2]==0) return;
        if(!collision(currentX+1, currentY, current)) ++currentX;
    }

    void setPause(boolean pause){
        this.pause=pause;
    }

    boolean isPaused(){
        return pause;
    }

    boolean isGameOver(){
        return gameOver;
    }

    int getCell(int x, int y){
        return board[y][x];
    }

    int getScore(){
        return score;
    }

    int getLevel(){
        return Math.max(_level, level);
    }

    int getCurrentX(){
        return currentX;
    }

    int getCurrentY(){
        return currentY;
    }

    final int[][] getCurrentType(){
        return current;
    }

    final int[][] getNext(){
        if(!showNext) return null;
        switch(next){
            case 1: return O[0];
            case 2: return I[0];
            case 3: return S[0];
            case 4: return Z[0];
            case 5: return L[0];
            case 6: return J[0];
            case 7: return T[0];
            default: return null;
        }
    }


    private boolean fallingPossible(){
        return !collision(currentX, currentY+1, current);
    }

    private boolean collision(int _x, int _y, int[][] _current){
        for(int y=0;y<4;++y){
            for(int x=0;x<4;++x){
                int tx=_x+x;
                int ty=_y+y;

                if(_current[y][x]==0) continue;
                if(tx<0 || ty<0 || tx>=COLUMNS || ty>=ROWS) return true;
                if(board[ty][tx]!=0)  return true;
            }
        }
        return false;
    }

    private boolean createNewPieces(){
        for(int y=0;y<4;++y){
            for(int x=0;x<4;++x){
                current[y][x]=0;
            }
        }

        int pieces=next;
        next=Math.abs(rand.nextInt())%7+1;
        orientation=0;
        drop=-1;
        currentY=-1;
        currentX=3;

        switch(pieces){
            case 1: //O
                current[1][1]=1;
                current[1][2]=1;
                current[2][1]=1;
                current[2][2]=1;
                break;

            case 2: //I
                current[1][0]=2;
                current[1][1]=2;
                current[1][2]=2;
                current[1][3]=2;
                break;

            case 3: //S
                current[1][2]=3;
                current[1][3]=3;
                current[2][1]=3;
                current[2][2]=3;
                break;

            case 4: //Z
                current[1][1]=4;
                current[1][2]=4;
                current[2][2]=4;
                current[2][3]=4;
                break;

            case 5: //L
                current[1][1]=5;
                current[1][2]=5;
                current[1][3]=5;
                current[2][1]=5;
                break;

            case 6: //J
                current[1][1]=6;
                current[1][2]=6;
                current[1][3]=6;
                current[2][3]=6;
                break;

            case 7: //T
                current[1][1]=7;
                current[1][2]=7;
                current[1][3]=7;
                current[2][2]=7;
                break;
        }

        for(int y=1;y<4;++y){
            for(int x=0;x<4;++x){
                if(current[y][x]!=0 && board[currentY+y][currentX+x]!=0) return false;
            }
        }
        return true;
    }

    private void fixToBoard(){
        for(int y=0;y<4;++y){
            for(int x=0;x<4;++x){
                if(current[y][x]!=0){
                    board[currentY+y][currentX+x]=current[y][x];
                    current[y][x]=0;
                }
            }
        }

        score += 18-drop;
        score += 3*Math.max(level-1, _level-1);
        if(!showNext) score+=5;
    }

    private void checkFullRows(){
        int n=0;
        for(int y=ROWS-1;y>=0;--y){
            boolean full=true;
            for(int x=0;x<COLUMNS;++x){
                if(board[y][x]==0){
                    full=false;
                    break;
                }
            }

            if(full){
                ++n;
                ++lines;
                if(lines<=0) _level=1;
                else if(lines>=1 && lines<=90) _level=1+((lines-1)/10);
                else if(lines>=91) _level=10;

                for(int yy=y-1;yy>=0;--yy){
                    for(int x=0;x<COLUMNS;++x){
                        board[yy+1][x]=board[yy][x];
                    }
                }
                for(int x=0;x<COLUMNS;++x){
                    board[0][x]=0;
                }
                ++y;
            }
        }
        soundManager.playFullRow(n);
    }

}

