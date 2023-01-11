package com.example.ip_1;

import android.content.res.Resources;

public class Inst {
    private Inst() {

    }

    public static int Dpi = 0;
    static int screenWidth = 0;
    static int screenHeight = 0;
    static int screenWidthDp = 0;
    static int screenHeightDp = 0;

    public static void initDpi(int dpi, int width, int height) {
        try{
            Dpi = Math.round(Resources.getSystem().getDisplayMetrics().density);
        } catch (Exception e) {
            Dpi = dpi;
        }
        try{
            screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        } catch (Exception e) {
            screenWidth = width;
        }
        try{
            screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        } catch (Exception e) {
            screenHeight = height;
        }
        screenWidthDp = screenWidth / Dpi;
        screenHeightDp = screenHeight / Dpi;
    }

    public static int makePx(int valueInDp) { return valueInDp * Dpi; }

    public static void changePosition(int x, int y, Circle ball, PlaygroundBorder border, FinishGround finish, StartPos start) {
        int newCx = ball.cx - x;
        int newCy = ball.cy + y;
        int r = ball.r;

        if (newCy - r >= border.top) {
            if (newCy + r <= border.bottom) {
                ball.cy = newCy;
            }
        } else {
            if (newCx - r >= finish.left && newCx + r <= finish.right) {
                ball.cx = newCx;
            }
            if (ball.cx - r >= finish.left && ball.cx + r <= finish.right) {
                if (newCy - r > 0) {
                    ball.cy = newCy;
                } else {
                    ball.cx = start.x;
                    ball.cy = start.y;
                }
            }
        }

        if (newCx - r >= border.left && newCx + r <= border.right) {
            if (ball.cy - r >= border.top) {
                ball.cx = newCx;
            }
        }
    }
}
