package vvojtyuk.hexeditor.document;

import vvojtyuk.hexeditor.io.FileByteReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHexDocument implements HexDocument{
    private final FileByteReader byteReader;
    private final AddBuffer addBuffer = new AddBuffer();
    private final List<Segment> segments = new ArrayList<Segment>();

    private long logicalLength;
    private boolean modified;

    public FileHexDocument(File file) throws IOException {
        this.byteReader = new FileByteReader(file);
        this.segments.add(new Segment(SegmentType.SOURCE, 0, byteReader.length()));
        this.logicalLength = byteReader.length();
    }

    @Override
    public long length() throws IOException {
        return logicalLength;
    }

    @Override
    public byte readByte(long offset) throws IOException {
        if (offset < 0 || offset >= logicalLength) {
            throw new IllegalArgumentException("Смещение вне границ документа.");
        }

        long documentPosition = 0;

        for (Segment segment : segments) {
            long segmentLength = segment.getLength();
            long segmentEndInDocument = documentPosition + segmentLength;

            if (offset < segmentEndInDocument) {
                long offsetInsideSegment = offset - documentPosition;
                long realOffset = segment.getStart() + offsetInsideSegment;

                if (segment.getType() == SegmentType.SOURCE) {
                    return byteReader.readByte(realOffset);
                } else {
                    return addBuffer.readByte(realOffset);
                }
            }

            documentPosition = segmentEndInDocument;
        }

        throw new IOException("Не удалось прочитать байт.");
    }

    @Override
    public void writeByte(long offset, byte value) throws IOException {
        if(offset >= logicalLength || offset < 0){
            throw new IllegalArgumentException();
        }
        if(readByte(offset) == value){
            return;
        }

    }

    @Override
    public void delete(long startOffset, long length, DeleteOption option) throws IOException {
    }

    @Override
    public void insert(long offset, byte[] data, InsertOption option) throws IOException {
    }

    @Override
    public void saveTo(File file) throws IOException {
        try (FileOutputStream out = new FileOutputStream(file)) {
            final int bufferSize = 8192;
            byte[] temp = new byte[bufferSize];

            for (Segment segment : segments) {
                long remaining = segment.getLength();
                long sourceOffset = segment.getStart();

                while (remaining > 0) {
                    int chunk = (int) Math.min(bufferSize, remaining);

                    for (int i = 0; i < chunk; i++) {
                        long absoluteOffset = sourceOffset + i;

                        if (segment.getType() == SegmentType.SOURCE) {
                            temp[i] = byteReader.readByte(absoluteOffset);
                        } else {
                            temp[i] = addBuffer.readByte(absoluteOffset);
                        }
                    }

                    out.write(temp, 0, chunk);
                    sourceOffset += chunk;
                    remaining -= chunk;
                }
            }
        }
    }



    @Override
    public void close() throws IOException {
        byteReader.close();
    }
}
