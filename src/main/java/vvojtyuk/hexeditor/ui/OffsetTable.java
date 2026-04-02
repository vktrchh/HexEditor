package vvojtyuk.hexeditor.ui;

import javax.swing.table.AbstractTableModel;

public class OffsetTable extends AbstractTableModel {
    private final HexVisibleTable visibleTable;

    public OffsetTable(HexVisibleTable visibleTable){
        this.visibleTable = visibleTable;
    }

    @Override
    public int getRowCount() {
        return visibleTable.getVisibleRows();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        long offset = visibleTable.getRowOffset(rowIndex);
        return String.format("%08X", offset);
    }

    @Override
    public String getColumnName(int column){
        return "Offset";
    }
}
