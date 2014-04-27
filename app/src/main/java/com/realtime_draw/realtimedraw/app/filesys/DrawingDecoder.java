package com.realtime_draw.realtimedraw.app.filesys;

import java.io.EOFException;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;

public class DrawingDecoder {
    private InputStream in;
    private LinkedBlockingQueue<DrawingFrameGroup> buffer = new LinkedBlockingQueue<>(6);
    public ReaderStatus status = null;
    private Exception exception = null;

    private Thread reader = new Thread(new Runnable() {
        @Override
        synchronized public void run() {
            if(status != null){
                exception = new Exception("already finished decoding");
                status = ReaderStatus.ERROR;
                return;
            }
            status = ReaderStatus.RUNNING;
            try {
                while(true) {
                    synchronized (buffer) {
                        buffer.put(DrawingFrameGroup.decode(in));
                    }
                }
            } catch (EOFException e) {
                status = ReaderStatus.FINISHED;
                return;
            } catch (Exception e){
                status = ReaderStatus.ERROR;
                exception = e;
            }
        }
    });

    public DrawingDecoder(InputStream inputStream) {
        in = inputStream;
        reader.start();
    }

    public synchronized DrawingFrameGroup getDrawingFrameGroup() throws Exception {
        synchronized (buffer) {
            if (status == ReaderStatus.ERROR) {
                throw exception;
            }
            if (buffer.size() == 0)
                if (status == ReaderStatus.FINISHED) {
                    status = ReaderStatus.ERROR;
                    throw exception = new EOFException();
                } else {
                    synchronized (reader) {
                        reader.wait();
                    }
                }
            return buffer.take();
        }
    }

}
