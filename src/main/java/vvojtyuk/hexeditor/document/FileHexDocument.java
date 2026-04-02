package vvojtyuk.hexeditor.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileHexDocument implements HexDocument{
    private final RandomAccessFile raf;
    private final File file;

    public FileHexDocument(File file) throws IOException {
        this.file = file;
        this.raf = new RandomAccessFile(file, "rw");
    }

    public File getFile(){
        return file;
    }

    @Override
    public long length() throws IOException {
        return raf.length();
    }

    @Override
    public byte readByte(long offset) throws IOException {
        raf.seek(offset);
        return raf.readByte();
    }

    @Override
    public void writeByte(long offset, byte value) throws IOException {
        raf.seek(offset);
        raf.writeByte(value);
    }

    @Override
    public void delete(long startOffset, long length, DeleteOption option) throws IOException {

    }

    @Override
    public void insert(long offset, byte[] data, InsertOption option) throws IOException {

    }

    @Override
    public void saveTo(File file) throws IOException {

    }

    @Override
    public void close() throws IOException {
        raf.close();
    }
}
