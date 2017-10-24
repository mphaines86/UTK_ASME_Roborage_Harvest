package io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by michael on 8/3/16 and heavily based on Andy's code. Sorry Andy!!!
 */
public class COBSWriter {

    OutputStream out;
    private byte[] rawStuff;
    private ByteBuffer stuffedOutput;

    public COBSWriter(OutputStream out, int unstuffedMessageLength){
        this.out = out;
        rawStuff = new byte[unstuffedMessageLength + 2];
        stuffedOutput = ByteBuffer.wrap(rawStuff);
    }

    public void write(ByteBuffer message) throws IOException{
        stuffBytes(message, stuffedOutput);
        serialComm.output.write(rawStuff, 0, stuffedOutput.remaining()); //TODO: Fix Encapsulation
        //out.write(rawStuff, 0, stuffedOutput.remaining());
        System.out.println(Arrays.toString(rawStuff));
        stuffedOutput.clear();
        //System.out.println("wrote data");
    }

    private static void stuffBytes(ByteBuffer source, ByteBuffer destination){
        int length = source.remaining();
        if (length > 254){
            System.err.println("Source length cannot be greater then 254 characters");
            return;
        }
        if (length == 0){
            System.err.println("Cannot Stuff empty source");
        }

        int index = 0;
        int codeIndex = 0;
        byte current;

        destination.put((byte)0);
        while(index++ < length){
            current = source.get();
            if (current == 0){
                destination.put(codeIndex, (byte)(index - codeIndex));
                destination.put((byte)0);
                codeIndex = index;
            }
            else {
                destination.put(current);
            }
        }
        destination.put(codeIndex, (byte)(index-codeIndex));
        destination.put((byte)0);

        destination.flip();
    }
}
