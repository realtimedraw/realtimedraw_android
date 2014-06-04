package com.realtime_draw.realtimedraw.app.filesys;

import java.io.IOException;
import java.io.OutputStream;

public enum UseCoordEnum {
    TOUCH_UP, TOUCH_MOVE, TOUCH_DOWN;

    public static UseCoordEnum decodeType(byte value) {
        return UseCoordEnum.values()[value];
    }

    public void encode(OutputStream outputStream) throws IOException {
        outputStream.write(ordinal());
    }
}
