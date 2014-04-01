package com.realtime_draw.realtimedraw.app.filesys;

import java.nio.ByteBuffer;

public enum DrawingActionEnum {
	USE_COORD{
        DrawingActionUseCoord decode(ByteBuffer byteBuffer){
            short x = byteBuffer.getShort();// x 2bytes short
            short y = byteBuffer.getShort();// y 2bytes short
            return new DrawingActionUseCoord(x, y);
        }
    },
    PICK_COLOR{
        DrawingActionPickColor decode(ByteBuffer byteBuffer){
            return new DrawingActionPickColor(byteBuffer.getInt());
        }
    },
    PICK_TOOL{
        DrawingActionPickTool decode(ByteBuffer byteBuffer){
            DrawingToolEnum tool = DrawingToolEnum.decodeType(byteBuffer.get());// tool 1byte byte
            DrawingToolInterface drawingToolInterface = tool.decode(byteBuffer);
            return new DrawingActionPickTool(drawingToolInterface);
        }
    };
    abstract DrawingActionInterface decode(ByteBuffer byteBuffer);
	public static DrawingActionEnum decodeType(byte value){
		return DrawingActionEnum.values()[value];
	}
};
