package com.example.ip_1;

import android.content.res.Resources;

public class Inst {
    private Inst() {

    }
    public final static Integer Dpi = Math.round(Resources.getSystem().getDisplayMetrics().density);
    public static int makePx(int valueInDp) {
        return valueInDp * Dpi;
    }
}
