package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Paint;
import android.graphics.Path;

public class DrawingPlayerState {
    public DrawingTool tool = new DrawingToolBrush(DrawingToolBrush.DEFAULT);
    public Path path = new Path();
    public Paint paint = new Paint();

    public DrawingPlayerState(){
        paint.setColor(0);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(DrawingToolBrush.DEFAULT);
    }
}
