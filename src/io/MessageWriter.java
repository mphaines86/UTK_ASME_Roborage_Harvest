package io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import messaging.IMessage;

/**
 * Created by michael on 8/6/16. May also be based not so heavily off of stuff from Andy. Maybe...
 */
public class MessageWriter implements Runnable{

    private COBSWriter cobsWriter;
    private OutputStream output;
    private Boolean writeData;
    private Boolean close;
    private byte[] data;

    private static final int messageLength = 254;

    public MessageWriter(OutputStream output){
        close = false;
        writeData = false;
        this.output = output;
        cobsWriter = new COBSWriter(output, messageLength + 2);
        data = new byte[messageLength];
    }

    @Override
    public void run() {
        byte[] rawBuffer = new byte[messageLength];
        ByteBuffer buffer = ByteBuffer.wrap(rawBuffer);
        try{
            while(!close){
                if(writeData) {
                    buffer.put(data);
                    buffer.flip();
                    cobsWriter.write(buffer);
                    buffer.clear();
                    writeData = false;
                }
                Thread.sleep(10);
            }
            output.close();

        }catch (IOException e) {
            e.printStackTrace();
        }catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    public void writeMessage(IMessage msg){

        data = msg.getBytes();
        writeData = true;
        //System.out.println("Gotta message!!!");
    }

    public void setClose(Boolean close){
        this.close = close;
    }
}
