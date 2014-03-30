package com.realtime_draw.realtimedraw.app.filesys;

import java.nio.ByteBuffer;

public enum DrawingAction {
	USE_COORD{
        DrawingActionUseCoord fromByteBuffer(ByteBuffer byteBuffer){
            short x = byteBuffer.getShort();// x 2bytes short
            short y = byteBuffer.getShort();// y 2bytes short
            return new DrawingActionUseCoord(x, y);
        }
    },
    PICK_COLOR{
        DrawingActionPickColor fromByteBuffer(ByteBuffer byteBuffer){
            return new DrawingActionPickColor(byteBuffer.getInt());
        }
    },
    PICK_TOOL{
        DrawingActionPickTool fromByteBuffer(ByteBuffer byteBuffer){
            return new DrawingActionPickTool(byteBuffer.getInt());
        }
    };
    abstract DrawingActionInterface fromByteBuffer(ByteBuffer byteBuffer);
	public static DrawingAction fromByte(byte value){
		return DrawingAction.values()[value];
	}
};
