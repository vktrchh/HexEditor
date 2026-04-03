package vvojtyuk.hexeditor.io;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
public class FileByteReader {
    private final RandomAccessFile raf;
    public FileByteReader(File file) throws IOException{
        raf = new RandomAccessFile(file, "r");
    }
    public long length() throws IOException{
        return raf.length();
    }
    public byte readByte(long offset) throws IOException{
        raf.seek(offset);
        return raf.readByte();
    }
    public void close() throws IOException{
        raf.close();
    }
}