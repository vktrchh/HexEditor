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

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        long offset = hexVisibleTable.getByteOffset(rowIndex, columnIndex);

        try {
            if (offset >= hexDocument.length()) {
                return;
            }

            byte newValue = parseHexByte(aValue);
            hexDocument.writeByte(offset, newValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при записи байта: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        long offset = hexVisibleTable.getByteOffset(rowIndex, columnIndex);

        try {
            return offset < hexDocument.length();
        } catch (IOException e) {
            return false;
        }
    }

    private byte parseHexByte(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Значение не введено.");
        }

        String text = value.toString().trim().toUpperCase();

        if (text.length() != 2) {
            throw new IllegalArgumentException("Байт должен состоять из двух hex-символов.");
        }

        try {
            int parsed = Integer.parseInt(text, 16);
            return (byte) parsed;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректное hex-значение: " + text);
        }
    }
}
