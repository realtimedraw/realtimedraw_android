package com.realtime_draw.realtimedraw.app.filesys;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public enum DrawingToolEnum {
    BRUSH {
        public DrawingToolBrush decode(InputStream inputStream) throws Exception {
            byte buff[] = new byte[2];
            for(int i=0; i<buff.length; ++i){
                int x = inputStream.read();
                if(x==-1)
                    throw new Exception("EOS received");
                buff[i] = (byte)x;
            }
            ByteBuffer byteBuffer = ByteBuffer.wrap(buff);
            return new DrawingToolBrush(byteBuffer.getShort());
        }
    };
    abstract public DrawingTool decode(InputStream inputStream) throws Exception;
    public static DrawingToolEnum decodeType(byte value){
        return DrawingToolEnum.values()[value];
    }

    public void encode(OutputStream outputStream) throws IOException {
        outputStream.write(ordinal());
    }
}
