package marin.tetris;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private GameView game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        game=(GameView)findViewById(R.id.surfaceView);
        game.start();
    }


    public void menu(View view){
        game.menu();
    }

    public void sound(View view){
        game.sound();
    }

    public void pause(View view){
        game.pause();
    }

    public void rotate(View view){
        game.rotate();
    }

    public void left(View view){
        game.left();
    }

    public void right(View view){
        game.right();
    }

    public void down(View view){
        game.down();
    }
}
