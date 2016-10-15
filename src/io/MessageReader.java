package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Michael Haines and Tanner Hobson on 2/10/2015.
 */

public class MessageReader {

    private InputStreamReader isr;
    byte[] buffer;
    int size;


    public MessageReader(InputStream in){
        this.isr = new InputStreamReader(in);
        buffer = new byte[256];
        size = 0;
    }

    public void readIfAvailable() throws IOException{
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
    }
}
