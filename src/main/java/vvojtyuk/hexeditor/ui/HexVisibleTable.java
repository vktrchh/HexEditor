package vvojtyuk.hexeditor.ui;

/*
    Видимое окно приложения
 */

public class HexVisibleTable {
    private long tableOffset = 0;
    private int bytesInRow = 16;
    private int visibleRows = 16;

    public long getTableOffset() {
        return tableOffset;
    }

    public void setTableOffset(long tableOffset) {
        this.tableOffset = (tableOffset >= 0) ? tableOffset : 0;
    }

    public int getBytesInRow() {
        return bytesInRow;
    }

    public void setBytesInRow(int bytesPerRow) {
        if(bytesPerRow <= 0){
            throw new IllegalArgumentException("Количество байтов должно быть больше нуля");
        }

        this.bytesInRow = bytesPerRow;
    }

    public int getVisibleRows() {
        return visibleRows;
    }

    public void setVisibleRows(int visibleRows) {
        if(visibleRows <= 0){
            throw new IllegalArgumentException("Количество отображаемых строк должно быть больше нуля");
        }

        this.visibleRows = visibleRows;
    }

    public long getRowOffset(int rowIndex) {
        return tableOffset + (long) rowIndex * bytesInRow;
    }

    public long getByteOffset(int rowIndex, int columnIndex) {
        return getRowOffset(rowIndex) + columnIndex;
    }

    public int getPageBytesSize() {
        return visibleRows * bytesInRow;
    }
}
