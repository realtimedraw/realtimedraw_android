package com.realtime_draw.realtimedraw.app.filesys;

import java.io.ByteArrayOutputStream;
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

	public void toBytes(ByteArrayOutputStream baos) throws Exception {
		baos.write((byte) DrawingAction.PICK_COLOR.ordinal());
		ByteBuffer byteBuffer = ByteBuffer.allocate(4);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		byteBuffer.putInt(color_);
		baos.write(byteBuffer.array());
	}
    
    public int getEncodedSize(){
        return 4;
    }
}
