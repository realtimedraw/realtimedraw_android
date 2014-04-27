package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Canvas;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DrawingActionPickColor extends DrawingAction {
	private int color_;

	public DrawingActionPickColor(int color) {
		color_ = color;
	}

	public int getColor() {
		return color_;
	}

    public DrawingActionEnum getType(){
        return DrawingActionEnum.PICK_COLOR;
    }

    public void drawOnCanvas(Canvas canvas, DrawingPlayerState state){
        state.color = color_;
    }

    @Override
    protected void encodeSubClass(OutputStream outputStream) throws IOException {
        outputStream.write((byte)(color_>>24));
        outputStream.write((byte)(color_>>16));
        outputStream.write((byte)(color_>> 8));
        outputStream.write((byte)(color_    ));
    }

    @Override
    protected int getEncodedSubClassSize() {
        return 4;
    }
}
