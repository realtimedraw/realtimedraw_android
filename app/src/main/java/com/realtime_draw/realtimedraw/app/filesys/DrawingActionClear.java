package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Canvas;

import java.io.IOException;
import java.io.OutputStream;

public class DrawingActionClear extends DrawingAction {
    public DrawingActionEnum getType(){
        return DrawingActionEnum.CLEAR;
    }

    public void drawOnCanvas(Canvas canvas, DrawingPlayerState state){
        canvas.drawColor(state.paint.getColor());
    }

    @Override
    protected void encodeSubClass(OutputStream outputStream) throws IOException {}

    @Override
    protected int getEncodedSubClassSize() {
        return 0;
    }
}
