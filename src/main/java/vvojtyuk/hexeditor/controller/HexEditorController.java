package vvojtyuk.hexeditor.controller;

import vvojtyuk.hexeditor.document.FileHexDocument;
import vvojtyuk.hexeditor.document.HexDocument;
import vvojtyuk.hexeditor.ui.ByteInfoPanel;
import vvojtyuk.hexeditor.ui.HexTable;
import vvojtyuk.hexeditor.ui.HexVisibleTable;
import vvojtyuk.hexeditor.ui.OffsetTable;
import vvojtyuk.hexeditor.util.NavigationToHexTable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class HexEditorController {
    private final Component parent;
    private final HexVisibleTable hexVisibleTable;
    private final HexTable hexTable;
    private final OffsetTable offsetTableModel;
    private final JTable hexJTable;
    private final JTable offsetJTable;
    private final ByteInfoPanel byteInfoPanel;
    private final NavigationToHexTable navigationToHexTable;

    private HexDocument hexDocument;

    public HexEditorController(
            Component parent, //для диалогов
            HexVisibleTable hexVisibleTable,
            HexTable hexTable,
            OffsetTable offsetTableModel,
            JTable hexJTable,
            JTable offsetJTable,
            ByteInfoPanel byteInfoPanel,
            NavigationToHexTable navigationToHexTable
    ) {
        this.parent = parent;
        this.hexVisibleTable = hexVisibleTable;
        this.hexTable = hexTable;
        this.offsetTableModel = offsetTableModel;
        this.hexJTable = hexJTable;
        this.offsetJTable = offsetJTable;
        this.byteInfoPanel = byteInfoPanel;
        this.navigationToHexTable = navigationToHexTable;
    }

    public void moveToStart() {
        setHexVisibleTableOffset(navigationToHexTable.moveToStart());
    }

    public void movePageUp() {
        setHexVisibleTableOffset(navigationToHexTable.movePageUp());
    }

    public void movePageDown() {
        setHexVisibleTableOffset(navigationToHexTable.movePageDown());
    }

    public void moveToEnd() {
        setHexVisibleTableOffset(navigationToHexTable.moveToEnd());
    }

    public void openFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(parent);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        try {
            if (hexDocument != null) {
                hexDocument.close();
            }

            hexDocument = new FileHexDocument(file);
            hexTable.setFileByteReader(hexDocument);
            navigationToHexTable.setFileByteReader(hexDocument);

            setHexVisibleTableOffset(0);
            byteInfoPanel.clearSelectionInfo();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    parent,
                    "Не удалось открыть файл",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void applyHexVisibleTableSettings(String bytesInRowText, String visibleRowsText) {
        try {
            int bytesInRow = Integer.parseInt(bytesInRowText.trim());
            int visibleRows = Integer.parseInt(visibleRowsText.trim());

            hexVisibleTable.setBytesInRow(bytesInRow);
            hexVisibleTable.setVisibleRows(visibleRows);

            long normalizedOffset =
                    navigationToHexTable.normalizeOffset(hexVisibleTable.getTableOffset());
            hexVisibleTable.setTableOffset(normalizedOffset);

            hexTable.fireTableStructureChanged();
            offsetTableModel.fireTableDataChanged();

            offsetJTable.getColumnModel().getColumn(0).setPreferredWidth(80);

            for (int i = 0; i < hexJTable.getColumnModel().getColumnCount(); i++) {
                hexJTable.getColumnModel().getColumn(i).setPreferredWidth(42);
            }

            byteInfoPanel.setViewOffset(hexVisibleTable.getTableOffset());
            updateSelectedByteInfo();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    parent,
                    "Введите целые числа",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    parent,
                    e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void updateSelectedByteInfo() {
        if (hexDocument == null) {
            byteInfoPanel.clearSelectionInfo();
            return;
        }

        int row = hexJTable.getSelectedRow();
        int column = hexJTable.getSelectedColumn();

        if (row < 0 || column < 0) {
            byteInfoPanel.clearSelectionInfo();
            return;
        }

        long offset = hexVisibleTable.getByteOffset(row, column);

        try {
            if (offset >= hexDocument.length()) {
                byteInfoPanel.clearSelectionInfo();
                return;
            }

            byte value = hexDocument.readByte(offset);
            byte[] block2 = readByteBlock(offset, 2);
            byte[] block4 = readByteBlock(offset, 4);
            byte[] block8 = readByteBlock(offset, 8);

            byteInfoPanel.showSelection(offset, value, block2, block4, block8);
        } catch (IOException e) {
            byteInfoPanel.clearSelectionInfo();
        }
    }

    private void setHexVisibleTableOffset(long offset) {
        hexVisibleTable.setTableOffset(offset);
        hexTable.fireTableDataChanged();
        offsetTableModel.fireTableDataChanged();
        byteInfoPanel.setViewOffset(hexVisibleTable.getTableOffset());
        updateSelectedByteInfo();
    }

    private byte[] readByteBlock(long startOffset, int size) throws IOException {
        if (hexDocument == null || startOffset < 0) {
            return null;
        }

        if (startOffset + size > hexDocument.length()) {
            return null;
        }

        byte[] data = new byte[size];

        for (int i = 0; i < size; i++) {
            data[i] = hexDocument.readByte(startOffset + i);
        }

        return data;
    }

}
