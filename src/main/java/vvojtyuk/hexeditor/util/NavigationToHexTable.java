package vvojtyuk.hexeditor.util;

import vvojtyuk.hexeditor.io.FileByteReader;
import vvojtyuk.hexeditor.ui.HexVisibleTable;

import java.io.IOException;

public class NavigationToHexTable {
    private final HexVisibleTable hexVisibleTable;
    private FileByteReader fileByteReader;

    public NavigationToHexTable(HexVisibleTable hexVisibleTable){
        this.hexVisibleTable = hexVisibleTable;
    }

    public void setFileByteReader(FileByteReader fileByteReader){
        this.fileByteReader = fileByteReader;
    }

    public long getMaxHexVisibleTableOffset() {
        if(fileByteReader == null){
            return 0;
        }

        try {
            long fileLength = fileByteReader.length();
            long pageSize = hexVisibleTable.getPageBytesSize();

            long maxOffset = fileLength - pageSize;

            return maxOffset - (maxOffset % hexVisibleTable.getBytesInRow());
        } catch (IOException e){
            return 0;
        }
    }

    public long normalizeOffset(long offset){
        long maxOffset = getMaxHexVisibleTableOffset();
        long normalOffset = Math.max(0, Math.min(offset, maxOffset));
        return normalOffset - (normalOffset % hexVisibleTable.getBytesInRow());
    }

    public long moveToStart() {
        return 0;
    }

    public long moveToEnd() {
        return getMaxHexVisibleTableOffset();
    }

    public long movePageUp() {
        long newOffset = hexVisibleTable.getTableOffset() - hexVisibleTable.getPageBytesSize();
        return normalizeOffset(newOffset);
    }

    public long movePageDown() {
        long newOffset = hexVisibleTable.getTableOffset() + hexVisibleTable.getPageBytesSize();
        return normalizeOffset(newOffset);
    }
}
