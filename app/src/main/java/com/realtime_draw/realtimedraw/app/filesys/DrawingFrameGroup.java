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
    private byte[] keyFrame_byte;
    private Bitmap keyFrame_bitmap;
    private int timeIndex = -1;

    public DrawingFrameGroup(Bitmap start) {
        encodedSize = 14;
        keyFrame_bitmap = start;
        byte[] a = null;
        if(start != null) {
            long g = System.nanoTime();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            start.compress(Bitmap.CompressFormat.PNG, 100, baos);
            long end = System.nanoTime();
            a=baos.toByteArray();
            System.out.println("Compressed to png: " + ((end - g) / 1000000) + " milliseconds");
            encodedSize += a.length;
        }
        keyFrame_byte = a;
    }

    public DrawingFrameGroup() {
        this(null);
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
        group.keyFrame_byte = new byte[size];
        dis.readFully(group.keyFrame_byte);
        long g = System.nanoTime();
        group.keyFrame_bitmap = BitmapFactory.decodeByteArray(group.keyFrame_byte, 0, group.keyFrame_byte.length).copy(Bitmap.Config.ARGB_8888, true);
        long end = System.nanoTime();
        System.out.println("Decompressed from png: " + ((end - g) / 1000000) + " milliseconds");
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
        int keyFrameLength = keyFrame_byte.length;
        stream.write((byte) (keyFrameLength >> 24));
        stream.write((byte) (keyFrameLength >> 16));
        stream.write((byte) (keyFrameLength >> 8));
        stream.write((byte) (keyFrameLength));
        stream.write(keyFrame_byte);
        for (int i = 0; i < framesNumber; ++i) {
            frames.get(i).encode(stream);
        }
    }

    public synchronized void appendFrame(DrawingFrame frame) {
        encodedSize += frame.getEncodedSize();
        frames.add(frame);
    }

    public Bitmap getKeyFrameBitmap() throws Exception {
        if (isGroupEmpty()) {
            throw new Exception("Empty group cannot have keyframe");
        }
        return keyFrame_bitmap;
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
        s += "}, " + keyFrame_byte.length + ", " + timeIndex + "}";
        return s;
    }
}
