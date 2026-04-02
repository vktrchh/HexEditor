package vvojtyuk.hexeditor.ui;

import javax.swing.table.AbstractTableModel;

public class HexTable extends AbstractTableModel {

    @Override
    public int getRowCount() {
        return 10;
    }

    @Override
    public int getColumnCount() {
        return 10;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return null;
    }
}
