package com.example.ip_1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Timer;
import java.util.TimerTask;

class DrawView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawThread drawThread;
    final Bitmap baseBitmap;
    final Circle ball;
    private Timer timer;

    public float[] valuesAccel = new float[2];

    final static Integer screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    final static Integer screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    private final static Integer screenWidthDp = screenWidth / Inst.Dpi;
    private final static Integer screenHeightDp = screenHeight / Inst.Dpi;
    private final static PlaygroundBorder border = new PlaygroundBorder(10, screenWidthDp, screenHeightDp);

    private final static Integer finishLeftBorder = Inst.makePx(screenWidthDp / 2 - 30);
    private final static Integer finishRightBorder = Inst.makePx(screenWidthDp / 2 + 30);

    private final static Integer startBallX = Inst.makePx(screenWidthDp / 2);
    private final static Integer startBallY = Inst.makePx(screenHeightDp - 60);

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new DrawThread(getHolder(), this);
        drawThread.setRunning(true);
        drawThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        drawThread.setRunning(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.e("e", e.getMessage());
            }
        }
    }

    public DrawView(Context context) {
        super(context);
        baseBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);

        Canvas baseCanvas = new Canvas(baseBitmap);
        baseCanvas.drawColor(Color.RED);

        Paint playField = new Paint();
        playField.setColor(Color.WHITE);
        Rect rect = new Rect(border.left, border.top, border.right, border.bottom);
        baseCanvas.drawRect(rect, playField);

        Paint finishPen = new Paint();
        finishPen.setColor(Color.GREEN);
        Rect finishGround = new Rect(finishLeftBorder, 0, finishRightBorder, border.top);
        baseCanvas.drawRect(finishGround, finishPen);
        ball = new Circle(startBallX, startBallY, 40);

        getHolder().addCallback(this);
    }



    public void startDraw() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                changePosition((int)Math.ceil(valuesAccel[0]), (int)Math.ceil(valuesAccel[1]));
            }
        };
        timer.schedule(task, 1000, 16);
    }

    public void stopDraw() {
        timer.cancel();
    }

    private void changePosition(int x, int y) {
        Integer newCx = ball.cx - x;
        Integer newCy = ball.cy + y;
        Integer r = ball.r;

        if(newCy - r >= border.top) {
            if(newCy + r  <= border.bottom) {
                ball.cy = newCy;
            }
        } else  {
            if(newCx - r >= finishLeftBorder && newCx + r <= finishRightBorder) {
                ball.cx = newCx;
            }
            if(ball.cx - r >= finishLeftBorder && ball.cx + r <=  finishRightBorder) {
                if(newCy - r > 0) {
                    ball.cy = newCy;
                } else {
                    ball.cx = startBallX;
                    ball.cy = startBallY;
                }
            }
        }

        if(newCx - r >= border.left && newCx + r <= border.right) {
            if(ball.cy - r >= border.top) {
                ball.cx = newCx;
            }
        }
    }
}
