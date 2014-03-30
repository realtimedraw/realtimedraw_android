package com.realtime_draw.realtimedraw.app.filesys;

import java.io.ByteArrayOutputStream;

public interface DrawingActionInterface {
	public void toBytes(ByteArrayOutputStream baos) throws Exception;
    public int getEncodedSize();
}
