package com.example.ip_1;

public class StartPos {
    public final int x;
    public final int y;
    public StartPos(int x, int y) {
        this.x = Inst.makePx(x);
        this.y = Inst.makePx(y);
    }
}
