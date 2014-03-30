package com.realtime_draw.realtimedraw.app.filesys;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DrawingActionPickTool implements DrawingActionInterface {
    private int tool_;

    public DrawingActionPickTool(int tool){ tool_ = tool; }

    public void toBytes(ByteArrayOutputStream baos) throws Exception {
        baos.write((byte) DrawingAction.PICK_TOOL.ordinal());
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        byteBuffer.putInt(tool_);
        baos.write(byteBuffer.array());
    }

    public int getTool(){
        return tool_;
    }

    public int getEncodedSize(){
        return 4;
    }
}
