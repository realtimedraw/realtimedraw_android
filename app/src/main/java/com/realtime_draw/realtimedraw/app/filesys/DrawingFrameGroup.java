package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class DrawingFrameGroup {
    private int encodedSize;
    private ArrayList<DrawingFrame> frames = new ArrayList<DrawingFrame>();
    private byte[] keyFrame;
    private short referencingGroup;

    private DrawingFrameGroup(){
        encodedSize = 6;
        referencingGroup = -1;
    }

    public DrawingFrameGroup(Bitmap start){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        start.compress(Bitmap.CompressFormat.PNG, 100, baos);
        keyFrame = baos.toByteArray();
        encodedSize = keyFrame.length + 6;
        referencingGroup = -1;
    }

    public static DrawingFrameGroup emptyGroup(short referenceGroup){
        DrawingFrameGroup group = new DrawingFrameGroup();
        group.encodedSize=2;
        group.referencingGroup = referenceGroup;
        return group;
    }

    public static DrawingFrameGroup decode(byte[] bytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        short framesNumber = byteBuffer.getShort();// framesNumber 2bytes short
        DrawingFrameGroup group = null;
        if(framesNumber>0){
            group = emptyGroup(framesNumber);
        }else {
            group = new DrawingFrameGroup();
            int size = byteBuffer.getInt(); // size 4bytes int
            group.encodedSize += size;
            group.keyFrame = new byte[size];
            byteBuffer.get(group.keyFrame, 0, size);
            while (framesNumber < 0) {
                try {
                    group.appendFrame(DrawingFrame.decode(byteBuffer));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ++framesNumber;
            }
        }
        return group;
    }

    public static void encodeEmptyGroupFromReference(OutputStream stream, short referenceGroup) throws IOException {
        stream.write((byte)(referenceGroup>> 8));
        stream.write((byte)(referenceGroup    ));
    }

    public void encode(OutputStream stream) throws IOException {
        if(referencingGroup>0) {
            stream.write((byte)(referencingGroup>> 8));
            stream.write((byte)(referencingGroup    ));
            return;
        }
        short framesNumber = (short)-frames.size();
        stream.write((byte)(framesNumber>> 8));
        stream.write((byte)(framesNumber    ));
        int keyFrameLength = keyFrame.length;
        stream.write((byte)(keyFrameLength>>24));
        stream.write((byte)(keyFrameLength>>16));
        stream.write((byte)(keyFrameLength>> 8));
        stream.write((byte)(keyFrameLength    ));
        stream.write(keyFrame);
        framesNumber = (short)frames.size();
        while (framesNumber>0){
            frames.get(framesNumber).encode(stream);
            --framesNumber;
        }
    }

    public synchronized void appendFrame(DrawingFrame frame) throws Exception {
        if(isGroupEmpty()){
            throw new Exception("Empty group can only reference to another group");
        }
        encodedSize += frame.getEncodedSize();
        frames.add(frame);
    }

    public synchronized void insertFrame(DrawingFrame frame) throws Exception {
        if(isGroupEmpty()){
            throw new Exception("Empty group can only reference to another group");
        }
        encodedSize += frame.getEncodedSize();
        addFrameBS(frame, 0, frames.size());
    }

    private void addFrameBS(DrawingFrame frame, int left, int right) {
        if (right < left) {
            frames.add(right, frame);
            return;
        }
        int mid = (left + right) / 2;
        if (frames.get(mid).getTimeIndex() < frame.getTimeIndex()) {
            addFrameBS(frame, mid + 1, right);
            return;
        }
        addFrameBS(frame, left, mid - 1);
    }

    public Bitmap getKeyFrame() throws Exception {
        if(isGroupEmpty()){
            throw new Exception("Empty group can only reference to another group");
        }
        return BitmapFactory.decodeByteArray(keyFrame, 0, keyFrame.length);
    }

    public int getEncodedSize() {
        return encodedSize;
    }

    public boolean isGroupEmpty(){
        return referencingGroup>0;
    }

    public short getReferencingGroup() throws Exception{
        if(!isGroupEmpty()){
            throw new Exception("Not an empty group");
        }
        return referencingGroup;
    }
}
