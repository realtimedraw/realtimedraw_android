package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Canvas;

import java.io.IOException;
import java.io.OutputStream;

public interface DrawingActionInterface {
	public void encode(OutputStream stream) throws IOException;//Note: MUST be FAST
    public int getEncodedSize();
    public DrawingActionEnum getType();
    public void drawOnCanvas(Canvas canvas, DrawingPlayerState state);
}
