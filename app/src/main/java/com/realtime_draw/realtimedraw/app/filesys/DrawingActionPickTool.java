package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Canvas;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DrawingActionPickTool extends DrawingAction {
    private DrawingTool tool_;

    public DrawingActionPickTool(DrawingTool tool) {
        tool_ = tool;
    }

    @Override
    protected void encodeSubClass(OutputStream outputStream) throws IOException {
        tool_.encode(outputStream);
    }

    @Override
    protected int getEncodedSubClassSize() {
        return 1 + tool_.getEncodedSize();
    }

    public DrawingTool getTool() {
        return tool_;
    }

    public DrawingActionEnum getType() {
        return DrawingActionEnum.PICK_TOOL;
    }

    public void drawOnCanvas(Canvas canvas, DrawingPlayerState state) {
        state.tool = tool_;
    }

}
