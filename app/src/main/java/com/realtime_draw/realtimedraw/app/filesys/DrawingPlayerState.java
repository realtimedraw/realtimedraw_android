package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Paint;
import android.graphics.Path;

public class DrawingPlayerState {
    public int color = 0;
    public DrawingTool tool = new DrawingToolBrush((short)1);
    public Path drawPath = new Path();
    public Paint drawPaint = new Paint();
}
