package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Canvas;

import java.io.IOException;
import java.io.OutputStream;

public interface DrawingToolInterface {
    public void encode(OutputStream stream) throws IOException;
    public int getEncodedSize();
    public DrawingToolEnum getType();
    public void useCoords(Canvas canvas, DrawingPlayerState state, short x, short y);
}
