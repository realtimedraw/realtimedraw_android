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
        public void run() {
            synchronized (this) {
                if (status != null) {
//                    exception = new Exception("reader: already finished");
                    status = ReaderStatus.ERROR;
                    return;
                }
                status = ReaderStatus.RUNNING;
            }
            try {
                while(true) {
                    synchronized (this) {
                        buffer.put(DrawingFrameGroup.decode(in));
//                        System.out.println("reader: notifying...");
                        synchronized (reader) {
                            reader.notify();
                        }
                    }
                }
            } catch (EOFException e) {
                synchronized (this) {
                    status = ReaderStatus.FINISHED;
//                    System.out.println("reader: finished");
                    synchronized (reader) {
                        reader.notify();
                    }
                }
            } catch (Exception e){
                synchronized (this) {
                    status = ReaderStatus.ERROR;
                    exception = e;
//                    System.out.println("reader: error");
                    synchronized (reader) {
                        reader.notify();
                    }
                }
            }
        }
    }, "DrawingDecoder");

    public DrawingDecoder(InputStream inputStream) {
        in = inputStream;
        reader.start();
    }

    public DrawingFrameGroup getDrawingFrameGroup() throws Exception {
        synchronized (this) {
            if (status == ReaderStatus.ERROR) {
                throw exception;
            }
            if (buffer.size() == 0) {
                if (status == ReaderStatus.FINISHED) {
                    status = ReaderStatus.ERROR;
                    System.out.println("decoder: Get last DFG");
                    throw exception = new EOFException();
                }
            }else
                return buffer.take();
        }
        synchronized (reader) {
//            System.out.println("decoder: waiting for reader...");
            reader.wait();
//            System.out.println("decoder: notified");
            return getDrawingFrameGroup();
        }
    }

}
