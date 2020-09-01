package com.example.hush.snake;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;

public class SnakeActivity extends Activity{

    SnakeEngine snakeEngine;

    public SnakeActivity(){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        DisplayMetrics disp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(disp);

        snakeEngine = new SnakeEngine(this,disp.widthPixels, disp.heightPixels);

        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(snakeEngine);
    }

    @Override
    public void onPause() {
        super.onPause();
        snakeEngine.pause();
    }
    @Override
    public void onResume() {
        super.onResume();
        snakeEngine.resume();
    }

}
