package com.example.ip_1;

import android.content.Context;
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

    private final static PlaygroundBorder border = new PlaygroundBorder(10, Inst.screenWidthDp, Inst.screenHeightDp);
    private final static FinishGround finish = new FinishGround(10, Inst.screenWidthDp);
    private final static StartPos start = new StartPos(Inst.screenWidthDp / 2, Inst.screenHeightDp - 60);

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
        baseBitmap = Bitmap.createBitmap(Inst.screenWidth, Inst.screenHeight, Bitmap.Config.ARGB_8888);

        Canvas baseCanvas = new Canvas(baseBitmap);
        baseCanvas.drawColor(Color.RED);

        Paint playField = new Paint();
        playField.setColor(Color.WHITE);
        Rect rect = new Rect(border.left, border.top, border.right, border.bottom);
        baseCanvas.drawRect(rect, playField);

        Paint finishPen = new Paint();
        finishPen.setColor(Color.GREEN);
        Rect finishGround = new Rect(finish.left, 0, finish.right, border.top);
        baseCanvas.drawRect(finishGround, finishPen);
        ball = new Circle(start.x, start.y, 40);

        getHolder().addCallback(this);
    }

    public void startDraw() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Inst.changePosition((int)Math.ceil(valuesAccel[0]), (int)Math.ceil(valuesAccel[1]), ball, border, finish, start);
            }
        };
        timer.schedule(task, 1000, 16);
    }

    public void stopDraw() {
        timer.cancel();
    }
}
