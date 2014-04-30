package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DrawingToolBrush extends DrawingTool {
    public static final short SMALL=10, MEDIUM=20, LARGE=30, DEFAULT = MEDIUM;

    private short dimension_;

    public DrawingToolBrush(short dimension){
        dimension_=dimension;
    }

    public short getDimension(){
        return dimension_;
    }

    public void setDimension(short dimension){
        dimension_=dimension;
    }

    public DrawingToolEnum getType(){
        return DrawingToolEnum.BRUSH;
    }

    public void useCoords(Canvas canvas, DrawingPlayerState state, short x, short y, UseCoordEnum type) {
        switch (type) {
            case TOUCH_DOWN:
                state.path.moveTo(x, y);
                break;
            case TOUCH_MOVE:
                state.path.lineTo(x, y);
                break;
            case TOUCH_UP:
                state.path.lineTo(x, y);
                canvas.drawPath(state.path, state.paint);
                state.path.reset();
                break;
        }
        //canvas.drawPath(state.path, state.paint);
    }

    @Override
    protected void encodeSubClass(OutputStream stream) throws IOException {
        stream.write((byte)(dimension_>> 8));
        stream.write((byte)(dimension_    ));
    }

    @Override
    protected int getEncodedSubClassSize() {
        return 2;
    }

    @Override
    public String toString() {
        return super.toString()+" "+dimension_;
    }
}
//TODO: custom shapes; now painting circles
