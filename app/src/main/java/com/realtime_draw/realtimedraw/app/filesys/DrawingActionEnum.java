package com.realtime_draw.realtimedraw.app.filesys;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public enum DrawingActionEnum {
    USE_COORD {
        public DrawingActionUseCoord decode(InputStream inputStream) throws Exception {
            DataInputStream dis = new DataInputStream(inputStream);
            short x = dis.readShort();// x 2bytes short
            short y = dis.readShort();// y 2bytes short
            UseCoordEnum type = UseCoordEnum.decodeType(dis.readByte());
            return new DrawingActionUseCoord(x, y, type);
        }
    },
    PICK_COLOR {
        public DrawingActionPickColor decode(InputStream inputStream) throws Exception {
            return new DrawingActionPickColor(new DataInputStream(inputStream).readInt());
        }
    },
    PICK_TOOL {
        public DrawingActionPickTool decode(InputStream inputStream) throws Exception {
            return new DrawingActionPickTool(DrawingToolEnum.decodeType(new DataInputStream(inputStream).readByte()).decode(inputStream));
        }
    },
    CLEAR{
        @Override
        public DrawingAction decode(InputStream inputStream) throws Exception {
            return new DrawingActionClear();
        }
    };

    abstract public DrawingAction decode(InputStream inputStream) throws Exception;

    public static DrawingActionEnum decodeType(byte value) {
        return DrawingActionEnum.values()[value];
    }

    public void encode(OutputStream outputStream) throws IOException {
        outputStream.write(ordinal());
    }
}
