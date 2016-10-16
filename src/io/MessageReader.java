package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

/**
 * Created by Michael Haines and Tanner Hobson on 2/10/2015.
 */

public class MessageReader implements Runnable{

    private InputStreamReader isr;
    //byte[] buffer;
    private int size;
    private COBSReader cobsReader;
    private boolean close = false;
    private ByteBuffer buffer, stuffed, unstuffed;
    private byte[] rawStuff;
    private BufferedReader bufferedReader;

    private static final int unstuffedMessageLength = 254;

    public MessageReader(BufferedReader in){
        bufferedReader = in;
        rawStuff = new byte[unstuffedMessageLength + 2];
        buffer = ByteBuffer.wrap(rawStuff);
        stuffed = ByteBuffer.allocate(unstuffedMessageLength + 2);
        unstuffed = ByteBuffer.allocate(unstuffedMessageLength);
        cobsReader = new COBSReader();
    }

    @Override
    public void run(){
        try {
            while (!close) {
                System.out.println("hello");
                while (bufferedReader.ready()) {
                    buffer.put((byte) bufferedReader.read());
                    System.out.println(buffer.remaining());
                    buffer.clear();
                }
                Thread.sleep(10);
            }
            bufferedReader.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public void readIfAvailable() throws IOException{
        while(isr.ready()){
            buffer[size++] = (byte)isr.read();
        }
    }

    public boolean messageReady(){
        if (size <= 0 ){
            return false;
        }
        int length = (int)buffer[0];

        return size >= length;

    }

    public byte[] getMessage() {

        int length = (int)buffer[0];
        byte[] ret = new byte[length];

        for (int i=0; i<size; ++i) {
            if (i < length) { // length = 2, size = 5
                ret[i] = buffer[i];
            } else {
                buffer[i - length] = buffer[i];
            }
        }

        size -= length;

        return ret;
    }*/
}
