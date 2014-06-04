package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Canvas;

import java.io.IOException;
import java.io.OutputStream;

public class DrawingActionUseCoord extends DrawingAction {
	private short x_;
	private short y_;
    private UseCoordEnum type_;
	
	public DrawingActionUseCoord(short x, short y, UseCoordEnum type){
		x_ = x;
		y_ = y;
        type_ = type;
	}
	
	public short x(){
		return x_;
	}
	
	public short y(){
		return y_;
	}

    public UseCoordEnum type(){
        return type_;
    }

    public DrawingActionEnum getType(){
        return DrawingActionEnum.USE_COORD;
    }

    public void drawOnCanvas(Canvas canvas, DrawingPlayerState state){
        state.tool.useCoords(canvas, state, x_, y_, type_);
    }

    @Override
    protected void encodeSubClass(OutputStream stream) throws IOException {
        stream.write((byte)(x_>> 8));
        stream.write((byte)(x_    ));
        stream.write((byte)(y_>> 8));
        stream.write((byte)(y_    ));
        type_.encode(stream);
    }

    @Override
    protected int getEncodedSubClassSize() {
        return 5;
    }

    @Override
    public String toString() {
        return super.toString()+" "+x_+","+y_+" "+type_;
    }
}
