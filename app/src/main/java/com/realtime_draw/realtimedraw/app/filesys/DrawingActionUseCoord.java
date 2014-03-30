package com.realtime_draw.realtimedraw.app.filesys;

import java.io.ByteArrayOutputStream;
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
	
	public void toBytes(ByteArrayOutputStream baos) throws Exception{
		baos.write((byte) DrawingAction.USE_COORD.ordinal());
		ByteBuffer byteBuffer = ByteBuffer.allocate(2);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		byteBuffer.putShort(x_);
		baos.write(byteBuffer.array());
		byteBuffer = ByteBuffer.allocate(2);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		byteBuffer.putShort(y_);
		baos.write(byteBuffer.array());
	}

    public int getEncodedSize(){
        return 4;
    }
}
