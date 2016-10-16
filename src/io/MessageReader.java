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

    private InputStream in;
    //byte[] buffer;
    private int size;
    private COBSReader cobsReader;
    private boolean close, messageReady;
    private ByteBuffer buffer, stuffed, unstuffed;
    private byte[] rawStuff;

    private static final int unstuffedMessageLength = 254;

    public MessageReader(InputStream in){
        close = false;
        messageReady = false;
        this.in = in;
        rawStuff = new byte[unstuffedMessageLength + 2];
        buffer = ByteBuffer.wrap(rawStuff);
        stuffed = ByteBuffer.allocate(unstuffedMessageLength + 2);
        unstuffed = ByteBuffer.allocate(unstuffedMessageLength);
        cobsReader = new COBSReader(in);
    }

    @Override
    public void run(){
        try {
            while (!close) {
                if (cobsReader.read(stuffed, unstuffed)) {
                    messageReady = true;
                }
                Thread.sleep(10);
            }


            in.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setClose(Boolean close){
        this.close = close;
    }
    public boolean getMessageReady(){
        return messageReady;
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

    }*/

    public byte[] getMessage() {

        int length = (int)unstuffed.get(0);
        byte[] ret = new byte[length];
        for (int i=0; i<length; ++i) {
            ret[i] = unstuffed.get();
            System.out.print(ret[i]);
            System.out.print(" ");

        }
        System.out.println("");
        //int index = 0;
        /*for(int i = length; i < unstuffed.limit(); i ++){
            unstuffed.put(index++, unstuffed.get());
            unstuffed.put(i, (byte)0);
        }
        unstuffed.position(unstuffed.position() - length);*/
        unstuffed.clear();
        messageReady = false;
        return ret;
    }
}
