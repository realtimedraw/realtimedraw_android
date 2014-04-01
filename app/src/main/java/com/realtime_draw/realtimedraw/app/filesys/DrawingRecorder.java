package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Date;

public class DrawingRecorder {
    private boolean isRecording;
    private long startMillis;
    private ByteArrayOutputStream drawingStream;
    private int framesNumber;
    private short keyFramesNumber;
    private Bitmap startingFrame;

    public DrawingRecorder(){
        isRecording = false;
    }

    public synchronized void startRecording(Bitmap initialFrame) throws Exception {
        if(isRecording)
            throw new Exception("Already recording");
        drawingStream = new ByteArrayOutputStream();
        isRecording=true;
        framesNumber=0;
        startingFrame = initialFrame.copy(Bitmap.Config.ARGB_8888, true);
        startMillis = System.currentTimeMillis();
        keyFramesNumber = 1;
    }

    public synchronized void stopRecording() throws Exception {
        if(!isRecording)
            throw new Exception("Not recording");
        isRecording=false;
    }

    public synchronized void appendNowAction(DrawingActionInterface action) throws Exception {
        if(!isRecording)
            throw new Exception("Not recording");
        int timeIndex = (int) (System.currentTimeMillis() - startMillis);
System.out.println("append " + timeIndex);
        drawingStream.write((byte)(timeIndex>>24));
        drawingStream.write((byte)(timeIndex>>16));
        drawingStream.write((byte)(timeIndex>> 8));
        drawingStream.write((byte)(timeIndex    ));
        action.encode(drawingStream);
        ++framesNumber;
        keyFramesNumber = (short)(timeIndex/1000 + 1);
    }

    public synchronized void save(OutputStream stream) throws Exception {
        if(isRecording)
            throw new Exception("Cannot save while recording");
        ByteBuffer byteBuffer = ByteBuffer.wrap(drawingStream.toByteArray());
        (Drawing.headerOnly(new Date(startMillis), keyFramesNumber)).encodeHeader(stream);
        DrawingPlayer player = new DrawingPlayer(startingFrame);
        DrawingFrameGroup group = new DrawingFrameGroup(startingFrame);
        int i = framesNumber, timeIndex;
        short groupIndex, groupsNumber = 1, lastKeyGroupIndex = 1;
        while (i>0){
            timeIndex = ((((int)byteBuffer.get() & 0xFF)<<8 | (int)byteBuffer.get() & 0xFF)<<8 | (int)byteBuffer.get() & 0xFF)<<8 | (int)byteBuffer.get() & 0xFF;

System.out.println("load " + timeIndex);
            groupIndex = (short)(timeIndex/1000 + 1);
            timeIndex = timeIndex%1000;
            if(groupIndex>groupsNumber) {
                group.encode(stream);
                while (groupIndex > 1 + groupsNumber) {
                    DrawingFrameGroup.encodeEmptyGroupFromReference(stream, lastKeyGroupIndex);
                    ++groupsNumber;
                }
                group = new DrawingFrameGroup(player.getCurrentFrame());
                ++groupsNumber;
            }
            DrawingActionEnum act = DrawingActionEnum.decodeType(byteBuffer.get());
            DrawingActionInterface action = act.decode(byteBuffer);
            //player.playAction(action);
            group.appendFrame(new DrawingFrame((short)timeIndex, action));
            lastKeyGroupIndex=groupsNumber;
            --i;
        }
        group.encode(stream);
    }
}
