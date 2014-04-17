package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Canvas;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DrawingActionPickColor implements DrawingActionInterface {
	private int color_;

	public DrawingActionPickColor(int color) {
		color_ = color;
	}

	public int getColor() {
		return color_;
	}

	public void encode(OutputStream stream) throws IOException {
		stream.write((byte) DrawingActionEnum.PICK_COLOR.ordinal());
        stream.write((byte)(color_>>24));
        stream.write((byte)(color_>>16));
        stream.write((byte)(color_>> 8));
        stream.write((byte)(color_    ));
	}
    
    public int getEncodedSize(){
        return 5;
    }

    public DrawingActionEnum getType(){
        return DrawingActionEnum.PICK_COLOR;
    }

    public void drawOnCanvas(Canvas canvas, DrawingPlayerState state){
        state.color = color_;
    }
}
