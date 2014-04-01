package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Canvas;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DrawingActionUseCoord implements DrawingActionInterface {
	private short x_;
	private short y_;
	
	public DrawingActionUseCoord(short x, short y){
		x_ = x;
		y_ = y;
	}
	
	public short x(){
		return x_;
	}
	
	public short y(){
		return y_;
	}
	
	public void encode(OutputStream stream) throws IOException{
		stream.write((byte) DrawingActionEnum.USE_COORD.ordinal());
        stream.write((byte)(x_>> 8));
        stream.write((byte)(x_    ));
        stream.write((byte)(y_>> 8));
        stream.write((byte)(y_    ));
	}

    public int getEncodedSize(){
        return 5;
    }

    public DrawingActionEnum getType(){
        return DrawingActionEnum.USE_COORD;
    }

    public void drawOnCanvas(Canvas canvas, DrawingPlayerState state){
        state.tool.useCoords(canvas, state, x_, y_);
    }
}
