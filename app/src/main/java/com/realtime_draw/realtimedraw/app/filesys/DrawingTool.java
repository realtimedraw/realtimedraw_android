package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Canvas;

import java.io.IOException;
import java.io.OutputStream;

public abstract class DrawingTool {
    final public void encode(OutputStream stream) throws IOException {
        getType().encode(stream);
    }

    final public int getEncodedSize(){
        return 1+getEncodedSubClassSize();
    }

    abstract public DrawingToolEnum getType();

    abstract public void useCoords(Canvas canvas, DrawingPlayerState state, short x, short y, UseCoordEnum type);

    abstract protected void encodeSubClass(OutputStream outputStream) throws IOException;

    abstract protected int getEncodedSubClassSize();

    @Override
    public String toString() {
        return ""+getType();
    }
}
