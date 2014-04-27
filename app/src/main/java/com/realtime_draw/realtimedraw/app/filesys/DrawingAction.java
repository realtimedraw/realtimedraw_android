package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Canvas;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

abstract public class DrawingAction {
    final public void encode(OutputStream outputStream) throws IOException {
        getType().encode(outputStream);
        encodeSubClass(outputStream);
    }

    public static DrawingAction decode(InputStream inputStream) throws Exception {
        int x = inputStream.read();
        if (x == -1)
            throw new Exception("EOS received");
        return DrawingActionEnum.decodeType((byte) x).decode(inputStream);
    }

    final public int getEncodedSize(){
        return 1+getEncodedSubClassSize();
    }

    abstract public DrawingActionEnum getType();

    abstract public void drawOnCanvas(Canvas canvas, DrawingPlayerState state);

    abstract protected void encodeSubClass(OutputStream outputStream) throws IOException;

    abstract protected int getEncodedSubClassSize();

    @Override
    public String toString() {
        return ""+getType();
    }
}
