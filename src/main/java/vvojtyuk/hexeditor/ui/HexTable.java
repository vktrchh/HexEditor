package vvojtyuk.hexeditor.ui;

import vvojtyuk.hexeditor.io.FileByteReader;

import javax.swing.table.AbstractTableModel;
import java.io.IOException;

public class HexTable extends AbstractTableModel {
    private FileByteReader fileByteReader;
    private final HexVisibleTable hexVisibleTable;

    public HexTable(HexVisibleTable hexVisibleTable){
        this.hexVisibleTable = hexVisibleTable;
    }

    public void setFileByteReader(FileByteReader fileByteReader) throws IOException{
        this.fileByteReader = fileByteReader;
    }

    @Override
    public int getRowCount() {
        return hexVisibleTable.getVisibleRows();
    }

    @Override
    public int getColumnCount() {
        return hexVisibleTable.getBytesInRow();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (fileByteReader == null) {
            return "";
        }
        long offset = hexVisibleTable.getByteOffset(rowIndex, columnIndex);
        try{
            byte value = fileByteReader.readByte(offset);
            return String.format("%02X", value & 0xFF);
        } catch (IOException e){
            return "..";
        }
    }
}
