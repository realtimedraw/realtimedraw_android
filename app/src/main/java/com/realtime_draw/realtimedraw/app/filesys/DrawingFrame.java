package com.realtime_draw.realtimedraw.app.filesys;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DrawingFrame {
	private int timeIndex_;
	private DrawingActionInterface drawingAction_;
    private int encodedSize;

	public DrawingFrame(int timeIndex, DrawingActionInterface drawingAction) {
		timeIndex_ = timeIndex;
		drawingAction_ = drawingAction;
        encodedSize = 5 + drawingAction.getEncodedSize();
	}

	public static DrawingFrame fromByteBuffer(ByteBuffer byteBuffer) {
		int timeIndex = byteBuffer.getInt();// timeIndex 4bytes int
		DrawingAction action = DrawingAction.fromByte(byteBuffer.get());// action
																		// 1byte
																		// byte
        DrawingActionInterface drawingAction = action.fromByteBuffer(byteBuffer);
		return new DrawingFrame(timeIndex, drawingAction);
	}

	public void toBytes(ByteArrayOutputStream baos) throws Exception {
		ByteBuffer byteBuffer = ByteBuffer.allocate(4);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		byteBuffer.putInt(timeIndex_);
		baos.write(byteBuffer.array());
		drawingAction_.toBytes(baos);
	}

	public int getTimeIndex() {
		return timeIndex_;
	}

	public DrawingActionInterface getDrawingAction() {
		return drawingAction_;
	}

    public int getEncodedSize(){
        return encodedSize;
    }
}
