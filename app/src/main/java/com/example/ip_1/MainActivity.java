package com.example.ip_1;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public SensorManager sensorManager;
    public DrawView playField;
    public Sensor sensorAccel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playField = new DrawView(this);
        setContentView(playField);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listener, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
        playField.startDraw();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);
        playField.stopDraw();
    }

    static class Circle {
        public Integer cx;
        public Integer cy;
        public Integer r;

        public Circle(int x, int y, int rad) {
            cx = x;
            cy = y;
            r = rad;
        }
    }

    static class DrawView extends SurfaceView implements SurfaceHolder.Callback {
        private DrawThread drawThread;
        private final Bitmap baseBitmap;
        private final Circle ball;
        private Timer timer;

        public float[] valuesAccel = new float[2];

        private final static Integer Dpi = Math.round(Resources.getSystem().getDisplayMetrics().density);
        private final static Integer screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        private final static Integer screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        private final static Integer screenWidthDp = screenWidth / Dpi;
        private final static Integer screenHeightDp = screenHeight / Dpi;

        private final static Integer topBorder = makePx(10);
        private final static Integer leftBorder = makePx(10);
        private final static Integer rightBorder = makePx(screenWidthDp - 10);
        private final static Integer bottomBorder = makePx(screenHeightDp - 40);

        private final static Integer finishLeftBorder = makePx(screenWidthDp / 2 - 30);
        private final static Integer finishRightBorder = makePx(screenWidthDp / 2 + 30);

        private final static Integer startBallX = makePx(screenWidthDp / 2);
        private final static Integer startBallY = makePx(screenHeightDp - 60);

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            drawThread = new DrawThread(getHolder());
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
            Rect rect = new Rect(leftBorder, topBorder, rightBorder, bottomBorder);
            baseCanvas.drawRect(rect, playField);

            Paint finishPen = new Paint();
            finishPen.setColor(Color.GREEN);
            Rect finishGround = new Rect(finishLeftBorder, 0, finishRightBorder, topBorder);
            baseCanvas.drawRect(finishGround, finishPen);
            ball = new Circle(startBallX, startBallY, 40);

            getHolder().addCallback(this);
        }

        private static int makePx(int valueInDp) {
            return valueInDp * Dpi;
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

        private void stopDraw() {
            timer.cancel();
        }

        private void changePosition(Integer x, Integer y) {
            Integer newCx = ball.cx - x;
            Integer newCy = ball.cy + y;
            Integer r = ball.r;


            if(newCy - r >= topBorder) {
                if(newCy + r  <= bottomBorder) {
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

            if(newCx - r >= leftBorder && newCx + r <= rightBorder) {
                if(ball.cy - r >= topBorder) {
                    ball.cx = newCx;
                }
            }
        }

        class DrawThread extends Thread {
            private boolean running = false;
            private final SurfaceHolder surfaceHolder;

            public DrawThread(SurfaceHolder surfaceHolder) {
                this.surfaceHolder = surfaceHolder;
            }

            public void setRunning(boolean running) {
                this.running = running;
            }

            @Override
            public void run() {
                Canvas canvas;
                Rect rect = new Rect(0, 0, screenWidth, screenHeight);
                Paint pen = new Paint();
                while (running) {
                    canvas = null;
                    try {
                        canvas = surfaceHolder.lockCanvas(null);
                        if (canvas == null)
                            continue;
                        canvas.drawBitmap(baseBitmap, null, rect, null);
                        canvas.drawCircle(ball.cx, ball.cy, ball.r, pen);
                    } finally {
                        if (canvas != null) {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }
        }
    }

    public SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                playField.valuesAccel[0] = event.values[0];
                playField.valuesAccel[1] = event.values[1];
            }
        }
    };
}