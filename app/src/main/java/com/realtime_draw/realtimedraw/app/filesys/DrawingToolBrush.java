package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DrawingToolBrush implements DrawingToolInterface {
    private short dimension_;

    public DrawingToolBrush(short dimension){
        dimension_=dimension;
    }

    public void encode(OutputStream stream) throws IOException {
        stream.write(DrawingToolEnum.BRUSH.ordinal());
        stream.write((byte)(dimension_>> 8));
        stream.write((byte)(dimension_    ));
    }

    public int getEncodedSize() {
        return 3;
    }

    public short getDimension(){
        return dimension_;
    }

    public DrawingToolEnum getType(){
        return DrawingToolEnum.BRUSH;
    }

    public void useCoords(Canvas canvas, DrawingPlayerState state, short x, short y) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(state.color);
        canvas.drawCircle(x, y, dimension_, paint);
    }
}
//TODO: custom shapes; now painting circles
