package com.realtime_draw.realtimedraw.app.filesys;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Date;

public class Drawing {
	private Date created;
	private ArrayList<DrawingFrame> frames = new ArrayList<DrawingFrame>();

	public Date getCreated() {
		return created;
	}

	public Drawing() {
		created = new Date();
	}

	public static Drawing fromByteBuffer(ByteBuffer byteBuffer) throws Exception {
		Drawing drawing = new Drawing();
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		drawing.created = new Date(byteBuffer.getLong() * 1000);// timestamp
																// 8bytes long
		int framesNumber = byteBuffer.getInt();// framesNumber 4bytes int
		if (framesNumber < 0) {
			throw new Exception("Number of frames is negative");
		}
		while (framesNumber != 0) {
			drawing.frames.add(DrawingFrame.fromByteBuffer(byteBuffer));
			--framesNumber;
		}
		return drawing;
	}

	public byte[] toByteArray() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ByteBuffer byteBuffer = ByteBuffer.allocate(8);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		byteBuffer.putLong(created.getTime() / 1000);
		baos.write(byteBuffer.array());

		byteBuffer = ByteBuffer.allocate(4);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		byteBuffer.putInt(frames.size());
		baos.write(byteBuffer.array());

		for (int i = 0; i < frames.size(); ++i) {
			frames.get(i).toBytes(baos);
		}
		return baos.toByteArray();
	}

	public void appendFrame(DrawingFrame frame) {
		frames.add(frame);
	}

	public void insertFrame(DrawingFrame frame) {
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

}
