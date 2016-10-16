package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

/**
 * Created by michael on 8/3/16 but more like created by Andy. Sorry Andy!!!
 */
public class COBSReader {

    private InputStream in;
    private byte[] rawBuffer;
    private ByteBuffer buffer2, stuffed, unstuffed;
    private int bufferSize, unstuffedLength;
    private boolean validMessage = true;

    private Parser parser;

    private InputStreamReader isr;
    private byte[] buffer;
    private int size;


    public COBSReader(){
    }

    public static boolean unstuffBytes(ByteBuffer source, ByteBuffer dest) {
        int length = source.remaining();

        if (dest.remaining() + 2 < length) {
            System.err.println("Source length greater than dest length");
            return false;
        }
        if (256 < length) {
            System.err.println("Source length greater than 256 not suppported");
            return false;
        }
        if (2 >= length) {
            System.err.println("Empty source");
            return false;
        }

        int index = 0;
        while (index < length - 1) {
            int stop = (source.get() & 0xFF) - 1 + index;
            while (index++ < stop) {
                dest.put(source.get());
            }
            if (index + 1 != length) {
                dest.put((byte) 0);
            }
        }

        dest.flip();

        return true;
    }

}