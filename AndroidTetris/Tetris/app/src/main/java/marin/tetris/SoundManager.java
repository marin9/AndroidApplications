package marin.tetris;

import android.content.Context;
import android.media.MediaPlayer;

class SoundManager {
    private boolean soundOn;
    private MediaPlayer music;
    private MediaPlayer button;
    private MediaPlayer fullRow;
    private MediaPlayer gameOver;
    private MediaPlayer highScore;


    SoundManager(Context context){
        soundOn=false;
        music=MediaPlayer.create(context, R.raw.music);
        button=MediaPlayer.create(context, R.raw.btn);
        fullRow=MediaPlayer.create(context, R.raw.line);
        gameOver=MediaPlayer.create(context, R.raw.gameover);
        highScore=MediaPlayer.create(context, R.raw.highscore);
    }


    void setSoundStatus(boolean status){
        soundOn=status;
        if(!status){
            music.pause();
            music.seekTo(0);
            gameOver.pause();
            gameOver.seekTo(0);
            highScore.pause();
            highScore.seekTo(0);
        }
    }

    boolean getSoundStatus(){
        return soundOn;
    }

    void playMusic(){
        if(soundOn){
            music.setLooping(true);
            music.start();
        }
    }

    void playGameOver(){
        if(soundOn){
            gameOver.start();
        }
    }

    void playHighScore(){
        if(soundOn){
            highScore.start();
        }
    }

    void stopHighScore(){
        highScore.pause();
        highScore.seekTo(0);
    }

    void stopMusic(){
        music.pause();
        music.seekTo(0);
    }

    void playButton(){
        if(soundOn){
            button.start();
        }
    }

    void playFullRow(final int n){
        if(soundOn && n!=0){
            fullRow.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                int i=1;
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if(i<n){
                        ++i;
                        mediaPlayer.start();
                    }
                }
            });
            fullRow.start();
        }
    }

}