package com.example.ip_1;

public class FinishGround {
    public final int top;
    public final int left;
    public final int right;
    
    public FinishGround(int top, int width) {
        this.top = Inst.makePx(top);
        this.left = Inst.makePx(width / 2 - 30);
        this.right = Inst.makePx(width / 2 + 30);
    }
}
