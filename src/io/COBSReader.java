package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by michael on 8/4/16 and heavily based on Andy's code. Sorry Andy!!!
 */
public class COBSReader {

    private InputStream in;
    private byte[] rawBuffer;
    private ByteBuffer buffer, stuffed, unstuffed;
    private int bufferSize, unstuffedLength;
    private boolean validMessage;
}
