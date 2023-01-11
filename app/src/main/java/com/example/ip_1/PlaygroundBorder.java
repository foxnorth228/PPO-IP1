package com.example.ip_1;

public class PlaygroundBorder {
    public final int left;
    public final int right;
    public final int top;
    public final int bottom;

    public PlaygroundBorder(int border, int width, int height) {
        left = Inst.makePx(border);
        right = Inst.makePx(width - border);
        top = Inst.makePx(border);
        bottom = Inst.makePx(height - border * 4);
    }
}
