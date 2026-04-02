package vvojtyuk.hexeditor.ui;

import vvojtyuk.hexeditor.document.HexDocument;

import javax.swing.table.AbstractTableModel;
import java.io.IOException;

public class HexTable extends AbstractTableModel {
    private HexDocument hexDocument;
    private final HexVisibleTable hexVisibleTable;

    public HexTable(HexVisibleTable hexVisibleTable){
        this.hexVisibleTable = hexVisibleTable;
    }

    public void setFileByteReader(HexDocument hexDocument) {
        this.hexDocument = hexDocument;
        fireTableDataChanged();
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
    public String getColumnName(int column){
        return String.format("%02X", column);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (hexDocument == null) {
            return "";
        }
        long offset = hexVisibleTable.getByteOffset(rowIndex, columnIndex);
        try{
            if(offset >= hexDocument.length()) {
                return "";
            }
            byte value = hexDocument.readByte(offset);
            return String.format("%02X", value & 0xFF);
        } catch (IOException e){
            return "..";
        }
    }
}
