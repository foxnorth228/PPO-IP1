package com.example.ip_1;

import static com.example.ip_1.Inst.changePosition;
import static com.example.ip_1.Inst.initDpi;
import static org.junit.Assert.*;

import org.junit.Test;

public class InstTest {

    @Test
    public void changePositionTest() {
        initDpi(2, 200, 200);
        //border (20, 20), (380, 320)
        PlaygroundBorder border = new PlaygroundBorder(10, 200, 200);
        //finish (140, 0) (260, 20)
        FinishGround finish = new FinishGround(10, 100);
        //startPos (200, 240)
        StartPos start = new StartPos(100, 120);
        // ball
        Circle ball = new Circle(start.x, start.y, 10);
        changePosition(10, -10, ball, border, finish, start);
        assertEquals(ball.cx, 190);
        assertEquals(ball.cy, 230);
        //because of work accelerator
        //positive x - move left, negative x - move right
        // x - move x to some pixels from current position
        // y - move y to some pixels from current position
        changePosition(10, -10, ball, border, finish, start);
        assertEquals(ball.cx, 180);
        assertEquals(ball.cy, 220);
        changePosition(10, -10, ball, border, finish, start);
        assertEquals(ball.cx, 170);
        assertEquals(ball.cy, 210);
        changePosition(140, -180, ball, border, finish, start);
        assertEquals(ball.cx, 30);
        assertEquals(ball.cy, 30);
        changePosition(1, 0, ball, border, finish, start);
        assertEquals(ball.cx, 30);
        assertEquals(ball.cy, 30);
        changePosition(0, -1, ball, border, finish, start);
        assertEquals(ball.cx, 30);
        assertEquals(ball.cy, 30);
        changePosition(-340, 280, ball, border, finish, start);
        assertEquals(ball.cx, 370);
        assertEquals(ball.cy, 310);
        changePosition(0, 1, ball, border, finish, start);
        assertEquals(ball.cx, 370);
        assertEquals(ball.cy, 310);
        changePosition(-11, 0, ball, border, finish, start);
        assertEquals(ball.cx, 370);
        assertEquals(ball.cy, 310);
    }
}