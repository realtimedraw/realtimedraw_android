package com.realtime_draw.realtimedraw.app.filesys;

import java.nio.ByteBuffer;

public enum DrawingToolEnum {
    BRUSH {
        DrawingToolInterface decode(ByteBuffer byteBuffer) {
            return new DrawingToolBrush(byteBuffer.getShort());
        }
    };

    abstract DrawingToolInterface decode(ByteBuffer byteBuffer);
    public static DrawingToolEnum decodeType(byte value){
        return DrawingToolEnum.values()[value];
    }
}
