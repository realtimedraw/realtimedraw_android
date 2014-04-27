package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DrawingToolBrush extends DrawingTool {
    private short dimension_;

    public DrawingToolBrush(short dimension){
        dimension_=dimension;
    }

    public short getDimension(){
        return dimension_;
    }

    public DrawingToolEnum getType(){
        return DrawingToolEnum.BRUSH;
    }

    public void useCoords(Canvas canvas, DrawingPlayerState state, short x, short y, UseCoordEnum type) {
        switch (type) {
            case TOUCH_DOWN:
                state.drawPath.moveTo(x, y);
                break;
            case TOUCH_MOVE:
                state.drawPath.lineTo(x, y);
                break;
            case TOUCH_UP:
                state.drawPath.lineTo(x, y);
                canvas.drawPath(state.drawPath, state.drawPaint);
                state.drawPath.reset();
                break;
        }



        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(state.color);
        canvas.drawCircle(x, y, dimension_, paint);
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
}
//TODO: custom shapes; now painting circles
