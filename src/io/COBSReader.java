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
    private ByteBuffer buffer;
    private int bufferSize;
    private boolean validMessage = true;

    private static final int unstuffedMessageLength = 254;

    public COBSReader(InputStream in) {

            this.in = in;
            this.bufferSize = bufferSize;
            rawBuffer = new byte[256];
            buffer = ByteBuffer.wrap(rawBuffer);
    }


    public boolean read(ByteBuffer stuffed, ByteBuffer unstuffed) throws IOException {
        //add timeout later

        if (in.available() > 0) {
            int numRead = in.read(rawBuffer);
            buffer.clear();
            buffer.limit(numRead);
            while (buffer.hasRemaining()) {
                byte current = buffer.get();

                //System.out.println(current);
                if (stuffed.hasRemaining()) {
                    stuffed.put(current);
                } else {
                    System.err.println("Message length exceeded in reader.");
                    validMessage = false;
                }

                if (current == 0) {
                    if (validMessage) {
                        stuffed.flip();
                        if (!unstuffBytes(stuffed, unstuffed)) {
                            System.err.println("Invalid Message. Message could not be unstuffed");
                        } else {
                            //Utility.printBytes(unstuffed);
                            validMessage = true;
                            stuffed.clear();
                            return true;
                        }
                    }
                    validMessage = true;
                    stuffed.clear();
                    unstuffed.clear();

                }

            }
        }
        return false;
    }

    public static boolean unstuffBytes(ByteBuffer source, ByteBuffer dest) {
        int length = source.remaining();
        //System.out.println(length);

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
        while (index < length) {
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