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
    private int timeIndex = -1;

    private DrawingFrameGroup(){
        encodedSize = 14;
        referencingGroup = -1;
    }

    public DrawingFrameGroup(Bitmap start){
long g = System.nanoTime();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        start.compress(Bitmap.CompressFormat.PNG, 100, baos);
        keyFrame = baos.toByteArray();
        encodedSize = keyFrame.length + 14;
        referencingGroup = -1;
long end = System.nanoTime();
System.out.println("Compressed to png: " + ((end - g) / 1000000) + " milliseconds");
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
            byteBuffer.getInt();// encodedSize 4bytes int
            group.timeIndex = byteBuffer.getInt();// timeIndex 4bytes int
            int size = byteBuffer.getInt(); // keyFrameSize 4bytes int
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

    public void encode(OutputStream stream) throws Exception {
        if(referencingGroup>0) {
            encodeEmptyGroupFromReference(stream, referencingGroup);
            return;
        }
        if(timeIndex <0){
            throw new RuntimeException("DrawingFrameGroup has invalid timeIndex");
        }
        short framesNumber = (short)-frames.size();
        stream.write((byte)(framesNumber>> 8));
        stream.write((byte)(framesNumber    ));
        stream.write((byte)(encodedSize>>24));
        stream.write((byte)(encodedSize>>16));
        stream.write((byte)(encodedSize>> 8));
        stream.write((byte)(encodedSize    ));
        stream.write((byte)(timeIndex >>24));
        stream.write((byte)(timeIndex >>16));
        stream.write((byte)(timeIndex >> 8));
        stream.write((byte)(timeIndex));
        int keyFrameLength = keyFrame.length;
        stream.write((byte)(keyFrameLength>>24));
        stream.write((byte)(keyFrameLength>>16));
        stream.write((byte)(keyFrameLength>> 8));
        stream.write((byte)(keyFrameLength    ));
        stream.write(keyFrame);
        framesNumber = (short)frames.size();
        while (framesNumber>0){
            --framesNumber;
            frames.get(framesNumber).encode(stream);
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

    public int getTimeIndex() {
        return timeIndex;
    }

    public void setTimeIndex(int timeIndex) {
        this.timeIndex = timeIndex;
    }
}
