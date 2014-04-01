package com.realtime_draw.realtimedraw.app.filesys;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Date;

public class Drawing {
	private Date created;
	private int headerEncodedSize;
    private short keyFramesNumber;
    private ArrayList<DrawingFrameGroup> groups;

	public Drawing() {
		headerEncodedSize = 10;
		created = new Date();
        keyFramesNumber=0;
	}

    public static Drawing headerOnly(Date created, short keyFramesNumber){
        Drawing drawing = new Drawing();
        drawing.created=created;
        drawing.keyFramesNumber=keyFramesNumber;
        return drawing;
    }

	public static Drawing decodeHeader(byte[] bytes) throws Exception {
		Drawing drawing = new Drawing();
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
		drawing.created = new Date(byteBuffer.getLong() * 1000);// timestamp
																// 8bytes long
        drawing.keyFramesNumber = byteBuffer.getShort();//keyFramesNumber 2bytes short
		return drawing;
	}

	public void encodeHeader(OutputStream stream) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		byteBuffer.putLong(created.getTime() / 1000);
		stream.write(byteBuffer.array());

        byteBuffer.clear();
        byteBuffer.putShort(keyFramesNumber);
        stream.write(byteBuffer.array());
	}

    public void setGroup(short index, DrawingFrameGroup group){
        groups.set(index, group);
    }

    public DrawingFrameGroup getGroup(short index){
        return groups.get(index);
    }

    public synchronized void appendGroup(DrawingFrameGroup group){
        groups.add(group);
        ++keyFramesNumber;
    }

    public int getHeaderEncodedSize(){
        return headerEncodedSize;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date d){
        created = d;
    }

    public short getKeyFramesNumber(){
        return keyFramesNumber;
    }

    public ArrayList<DrawingFrameGroup> getGroups(){
        return groups;
    }
}
