package com.realtime_draw.realtimedraw.app.filesys;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class DrawingFrameGroup {
    private int encodedSize;
    private ArrayList<DrawingFrame> frames = new ArrayList<>();
    private byte[] keyFrame;
    private int timeIndex = -1;

    private DrawingFrameGroup() {
        encodedSize = 14;
    }

    public DrawingFrameGroup(Bitmap start) {
        long g = System.nanoTime();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        start.compress(Bitmap.CompressFormat.PNG, 100, baos);
        keyFrame = baos.toByteArray();
        encodedSize = keyFrame.length + 14;
        long end = System.nanoTime();
        System.out.println("Compressed to png: " + ((end - g) / 1000000) + " milliseconds");
    }

    public static DrawingFrameGroup emptyGroup() {
        DrawingFrameGroup group = new DrawingFrameGroup();
        group.encodedSize = 2;
        return group;
    }

    public static DrawingFrameGroup decode(InputStream inputStream) throws Exception {
        DataInputStream dis = new DataInputStream(inputStream);
        short framesNumber = dis.readShort();// framesNumber 2bytes short
        if (framesNumber == 0) {
            return emptyGroup();
        }
        DrawingFrameGroup group = new DrawingFrameGroup();
        dis.readInt();// encodedSize 4bytes int
        group.timeIndex = dis.readInt();// timeIndex 4bytes int
        int size = dis.readInt(); // keyFrameSize 4bytes int
        group.encodedSize += size;
        group.keyFrame = new byte[size];
        dis.readFully(group.keyFrame);
        while (framesNumber > 0) {
            try {
                group.appendFrame(DrawingFrame.decode(inputStream));
            } catch (Exception e) {
                e.printStackTrace();
            }
            --framesNumber;
        }

        return group;
    }

    public void encode(OutputStream stream) throws Exception {
        if (frames.size() == 0) {
            stream.write((byte) 0);
            stream.write((byte) 0);
            return;
        }
        if (timeIndex < 0) {
            throw new RuntimeException("DrawingFrameGroup has invalid timeIndex");
        }
        short framesNumber = (short) frames.size();
        stream.write((byte) (framesNumber >> 8));
        stream.write((byte) (framesNumber));
        stream.write((byte) (encodedSize >> 24));
        stream.write((byte) (encodedSize >> 16));
        stream.write((byte) (encodedSize >> 8));
        stream.write((byte) (encodedSize));
        stream.write((byte) (timeIndex >> 24));
        stream.write((byte) (timeIndex >> 16));
        stream.write((byte) (timeIndex >> 8));
        stream.write((byte) (timeIndex));
        int keyFrameLength = keyFrame.length;
        stream.write((byte) (keyFrameLength >> 24));
        stream.write((byte) (keyFrameLength >> 16));
        stream.write((byte) (keyFrameLength >> 8));
        stream.write((byte) (keyFrameLength));
        stream.write(keyFrame);
        for (int i = 0; i < framesNumber; ++i) {
            frames.get(framesNumber).encode(stream);
        }
    }

    public synchronized void appendFrame(DrawingFrame frame) {
        encodedSize += frame.getEncodedSize();
        frames.add(frame);
    }

    public synchronized void insertFrame(DrawingFrame frame) throws Exception {
        if (isGroupEmpty()) {
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
        if (isGroupEmpty()) {
            throw new Exception("Empty group cannot have keyframe");
        }
        return BitmapFactory.decodeByteArray(keyFrame, 0, keyFrame.length);
    }

    public int getEncodedSize() {
        return encodedSize;
    }

    public boolean isGroupEmpty() {
        return frames.isEmpty();
    }

    public int getTimeIndex() {
        return timeIndex;
    }

    public void setTimeIndex(int timeIndex) {
        this.timeIndex = timeIndex;
    }

    public ArrayList<DrawingFrame> getFrames() {
        return frames;
    }

    @Override
    public String toString() {
        String s = "{" + encodedSize + ", {";
        boolean x = true;
        for (DrawingFrame frame : frames) {
            if (x) {
                x = false;
            } else {
                s += ", ";
            }
            s += frame;
        }
        s += "}, " + keyFrame.length + ", " + timeIndex + "}";
        return s;
    }
}
