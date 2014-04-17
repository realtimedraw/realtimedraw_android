package com.realtime_draw.realtimedraw.app.filesys;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DrawingFrame {
	private short timeIndex_;
	private DrawingActionInterface drawingAction_;
    private int encodedSize;

	public DrawingFrame(short timeIndex, DrawingActionInterface drawingAction) {
		timeIndex_ = timeIndex;
		drawingAction_ = drawingAction;
        encodedSize = 2 + drawingAction.getEncodedSize();
	}

	public static DrawingFrame decode(ByteBuffer byteBuffer) {
		short timeIndex = byteBuffer.getShort();// timeIndex 2bytes short
		DrawingActionEnum action = DrawingActionEnum.decodeType(byteBuffer.get());// action
																		// 1byte
																		// byte
        DrawingActionInterface drawingAction = action.decode(byteBuffer);
		return new DrawingFrame(timeIndex, drawingAction);
	}

	public void encode(OutputStream stream) throws IOException {
        stream.write((byte)(timeIndex_>> 8));
        stream.write((byte)(timeIndex_    ));
		drawingAction_.encode(stream);
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
