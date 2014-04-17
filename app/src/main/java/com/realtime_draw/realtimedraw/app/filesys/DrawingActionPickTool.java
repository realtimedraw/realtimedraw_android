package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Canvas;

import java.io.IOException;
import java.io.OutputStream;

public class DrawingActionPickTool implements DrawingActionInterface {
    private DrawingToolInterface tool_;

    public DrawingActionPickTool(DrawingToolInterface tool){ tool_ = tool; }

    public void encode(OutputStream stream) throws IOException {
        stream.write((byte) DrawingActionEnum.PICK_TOOL.ordinal());
        tool_.encode(stream);
    }

    public DrawingToolInterface getTool(){
        return tool_;
    }

    public int getEncodedSize(){
        return 1+tool_.getEncodedSize();
    }

    public DrawingActionEnum getType(){
        return DrawingActionEnum.PICK_TOOL;
    }

    public void drawOnCanvas(Canvas canvas, DrawingPlayerState state){
        state.tool = tool_;
    }
}
