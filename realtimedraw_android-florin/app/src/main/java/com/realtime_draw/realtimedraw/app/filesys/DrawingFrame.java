package com.realtime_draw.realtimedraw.app.filesys;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DrawingFrame {
	private short timeIndex_;
	private DrawingAction drawingAction_;
    private int encodedSize;

	public DrawingFrame(short timeIndex, DrawingAction drawingAction) {
		timeIndex_ = timeIndex;
		drawingAction_ = drawingAction;
        encodedSize = 2 + drawingAction.getEncodedSize();
	}

	public static DrawingFrame decode(InputStream inputStream) throws Exception {
		short timeIndex = new DataInputStream(inputStream).readShort();// timeIndex 2bytes short
		return new DrawingFrame(timeIndex, DrawingAction.decode(inputStream));
	}

	public void encode(OutputStream stream) throws IOException {
        stream.write((byte)(timeIndex_>> 8));
        stream.write((byte)(timeIndex_    ));
		drawingAction_.encode(stream);
	}

	public int getTimeIndex() {
		return timeIndex_;
	}

	public DrawingAction getDrawingAction() {
		return drawingAction_;
	}

    public int getEncodedSize(){
        return encodedSize;
    }

    @Override
    public String toString() {
        return "{"+timeIndex_+", "+drawingAction_+", "+encodedSize+"}";
    }
}
