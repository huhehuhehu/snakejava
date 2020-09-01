package com.example.hush.snake;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class SnakeEngine extends SurfaceView implements Runnable {
    private Context context;

    private volatile boolean isPlaying = false;

    private Thread thread = null;

    private int dir;
    private final int UP = 0;
    private final int RIGHT = 1;
    private final int DOWN = 2;
    private final int LEFT = 3;
    private final int HEAD = 0;

    private boolean lockDIR;

    private Random random;

    private final long ping = 250;
    private long nextFrameTime;

    private int screenX;
    private int screenY;

    private int maxX;
    private int maxY;

    private int[] snakeX;
    private int[] snakeY;

    private int score;

    private int foodX;
    private int foodY;

    private float touchX;
    private float touchY;

    private int length;

    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private Paint paint;

    private Bitmap map;
    private Bitmap food;
    private Bitmap body;
    private Bitmap[] head = new Bitmap[4];

    private final int OFFSET = 30;

    public SnakeEngine(Context context, int width, int height) {
        super(context);

        this.context = context;
        screenX = width;
        screenY = height;

        maxX = (screenX-OFFSET)/OFFSET;
        maxY = (screenY)/OFFSET;

        random = new Random();

        snakeX = new int[maxX*maxY];
        snakeY = new int[maxX*maxY];

        map = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(map);
        surfaceHolder = getHolder();
        paint = new Paint();

        //load sprites
        body = BitmapFactory.decodeResource(context.getResources(), R.drawable.body);
        food = BitmapFactory.decodeResource(context.getResources(), R.drawable.food);
        head[UP]=  BitmapFactory.decodeResource(context.getResources(), R.drawable.headup);
        head[LEFT]=  BitmapFactory.decodeResource(context.getResources(), R.drawable.headleft);
        head[RIGHT]=  BitmapFactory.decodeResource(context.getResources(), R.drawable.headright);
        head[DOWN]=  BitmapFactory.decodeResource(context.getResources(), R.drawable.headdown);

        newGame();
    }

    //initialise start of game
    private void newGame(){
        dir = RIGHT;
        lockDIR = false;

        score = 0;

        length = 3;

        for(int i = 0; i < length; i++){
            snakeX[i] = (5-i)*OFFSET;
            snakeY[i] = 5*OFFSET;
        }

        do{
            foodX = random.nextInt(maxX) * OFFSET;
            foodY = random.nextInt(maxY) * OFFSET;
        }while(!isLegit());

        nextFrameTime = System.currentTimeMillis() + ping;
        isPlaying = true;
        draw();
    }

    private void draw(){
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            //draw background
            canvas.drawColor(Color.argb(255, 255, 255, 255));

            paint.setColor(Color.argb(255, 0, 0, 0));
            // Scale the HUD text
            paint.setTextSize(90);
            canvas.drawText("Score:" + score, 10, 70, paint);

            drawFood();
            drawHead();
            for(int i = 1; i < length; i++){
                drawBody(i);
            }

            surfaceHolder.unlockCanvasAndPost(canvas);
            }
    }

    //DRAWERS
    private void drawBody(int i){
        canvas.drawBitmap(body, snakeX[i], snakeY[i], paint);
    }

    private void drawHead(){
        switch(dir){
            case UP:
                canvas.drawBitmap(head[UP], snakeX[HEAD], snakeY[HEAD], paint);
                break;
            case DOWN:
                canvas.drawBitmap(head[DOWN], snakeX[HEAD], snakeY[HEAD], paint);
                break;
            case LEFT:
                canvas.drawBitmap(head[LEFT], snakeX[HEAD], snakeY[HEAD], paint);
                break;
            case RIGHT:
                canvas.drawBitmap(head[RIGHT], snakeX[HEAD], snakeY[HEAD], paint);
                break;
            default:
                break;
        }
    }

    private void drawFood(){
        canvas.drawBitmap(food, foodX, foodY, paint);
    }

    private void spawnFood(){
        do{
            foodX = random.nextInt(maxX) * OFFSET;
            foodY = random.nextInt(maxY) * OFFSET;

        }while(!isLegit());
    }

    //CHECKERS
    private boolean isEaten(){
        if(snakeX[HEAD] == foodX && snakeY[HEAD] == foodY) return true;
        return false;
    }

    //check if new food location is valid
    private boolean isLegit(){
        for(int i = length-1; i>=0; i--){
            if(foodX == snakeX[i] && foodY == snakeY[i]) return false;
        }
        return true;
    }

    //returns true if game over
    private boolean isDead(){
        if(snakeX[HEAD] < 0) return true;
        if(snakeY[HEAD] < 0) return true;
        if(snakeX[HEAD] > screenX-OFFSET) return true;
        if(snakeY[HEAD] > screenY) return true;

        if(length >3){
            for(int i = 4; i < length; i++){
                if(snakeX[HEAD] == snakeX[i] && snakeY[HEAD] == snakeY[i]) return true;
            }
        }
        return false;
    }

    //Check if enough delay passed
    private boolean updateRequired(){
        if(System.currentTimeMillis() > nextFrameTime) {
            nextFrameTime = System.currentTimeMillis() + ping;
            return true;
        }
        return false;
    }

    //Update new positions
    public void update(){
        //move body positions
        for(int i = length; i > 0; i--){
            snakeX[i] = snakeX[i-1];
            snakeY[i] = snakeY[i-1];
        }

        //move head position
        switch(dir){
            case UP:
                snakeY[HEAD] = snakeY[HEAD] - OFFSET;
                break;
            case DOWN:
                snakeY[HEAD] = snakeY[HEAD] + OFFSET;
                break;
            case LEFT:
                snakeX[HEAD] = snakeX[HEAD] - OFFSET;
                break;
            case RIGHT:
                snakeX[HEAD] = snakeX[HEAD] + OFFSET;
                break;
            default:
                break;
        }

        //check if next instance food is eaten
        if(isEaten()){
            eat();
        }
    }

    private void eat(){
        length++;
        score++;
        spawnFood();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN && !lockDIR) {
            touchX = event.getX();
            touchY = event.getY();

            if (touchY <= screenY / 3) {
                if(dir != DOWN){
                    dir = UP;
                    lockDIR = true;
                }
            } else if (touchY >= 2 * screenY / 3) {
                if(dir != UP) {
                    dir = DOWN;
                    lockDIR = true;
                }
            } else if (touchX <= screenX / 2) {
                if(dir != RIGHT){
                    dir = LEFT;
                    lockDIR = true;
                }
            } else {
                if(dir != LEFT){
                    dir = RIGHT;
                    lockDIR = true;
                }
            }
        }
        return true;
    }

    @Override
    public void run() {
        while (isPlaying) {
            if(updateRequired()) {
                update();
                if(isDead()){
                    //back to menu screen
                    ((Activity) context).finish();
                    return;
                }
                draw();
                lockDIR = false;
            }
        }
    }

    public void pause() {
        isPlaying = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }



}
