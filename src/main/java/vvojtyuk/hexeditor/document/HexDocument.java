package vvojtyuk.hexeditor.document;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public interface HexDocument extends Closeable {
    long length() throws IOException;
    byte readByte(long offset) throws IOException;
    void writeByte(long offset, byte value) throws IOException;
    void delete(long startOffset, long length, DeleteOption option) throws IOException;
    void insert(long offset, byte[] data, InsertOption option) throws IOException;
    void saveTo(File file) throws IOException;
    @Override
    void close() throws IOException;
}
