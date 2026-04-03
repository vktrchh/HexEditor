package vvojtyuk.hexeditor.controller;

import vvojtyuk.hexeditor.document.*;
import vvojtyuk.hexeditor.search.HexSearch;
import vvojtyuk.hexeditor.search.MaskPattern;
import vvojtyuk.hexeditor.ui.ByteInfoPanel;
import vvojtyuk.hexeditor.ui.HexTable;
import vvojtyuk.hexeditor.ui.HexVisibleTable;
import vvojtyuk.hexeditor.ui.OffsetTable;
import vvojtyuk.hexeditor.util.NavigationToHexTable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class HexEditorController {
    private final Component parent;
    private final HexVisibleTable hexVisibleTable;
    private final HexTable hexTable;
    private final OffsetTable offsetTableModel;
    private final JTable hexJTable;
    private final JTable offsetJTable;
    private final ByteInfoPanel byteInfoPanel;
    private final NavigationToHexTable navigationToHexTable;
    private final HexSearch hexSearch = new HexSearch();

    private final HexClipboard clipboard = new HexClipboard();
    private File currentFile;
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

    //методы навигации
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

    //методы открытия и сохраниния файлов
    public void openFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(parent);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        loadFile(file);
    }

    private void loadFile(File file) {
        try {
            if (hexDocument != null) {
                hexDocument.close();
            }

            hexDocument = new FileHexDocument(file);
            currentFile = file;

            hexTable.setHexDocument(hexDocument);
            navigationToHexTable.setHexDocument(hexDocument);

            setHexVisibleTableOffset(0);
            byteInfoPanel.clearSelectionInfo();
        } catch (IOException e) {
            showErrorMessage("Не удалось открыть файл");
        }
    }

    public void saveFileAs() {
        if (hexDocument == null) {
            showErrorMessage("Ошибка при сохранении файла");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        int result = chooser.showSaveDialog(parent);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        try {
            saveDocumentToTarget(file);
        } catch (IOException e) {
            showErrorMessage("Ошибка при сохранении файла");
        }
    }

    public void saveCurrentFile() {
        if (hexDocument == null) {
            showErrorMessage("Сначала откройте файл");
            return;
        }

        if (currentFile == null) {
            saveFileAs();
            return;
        }

        try {
            saveDocumentToTarget(currentFile);
        } catch (IOException e) {
            showErrorMessage("Ошибка при сохранении файла");
        }
    }

    private void saveDocumentToTarget(File targetFile) throws IOException {
        boolean overwriteCurrent =
                currentFile != null && currentFile.getCanonicalFile().equals(targetFile.getCanonicalFile());

        if (!overwriteCurrent) {
            hexDocument.saveTo(targetFile);
            loadFile(targetFile);
            return;
        }

        File parentDir = targetFile.getAbsoluteFile().getParentFile();
        if (parentDir == null) {
            parentDir = new File(".");
        }

        File tempFile = File.createTempFile("hexedit_", ".tmp", parentDir);

        hexDocument.saveTo(tempFile);

        hexDocument.close();

        Path tempPath = tempFile.toPath();
        Path targetPath = targetFile.toPath();

        try {
            Files.move(
                    tempPath,
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (IOException e) {
            Files.move(
                    tempPath,
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        loadFile(targetFile);
    }

    //методы настройки и отображения UI
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
            showErrorMessage("Введите целые числа");
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

    private void refreshDocumentView() {
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

    //методы выделения
    private long getSelectionStartOffset() throws IOException {
        int[] rows = hexJTable.getSelectedRows();
        int[] columns = hexJTable.getSelectedColumns();

        if (rows.length == 0 || columns.length == 0) {
            throw new IOException("Нет выделения.");
        }

        int minRow = rows[0];
        int minColumn = columns[0];

        for (int row : rows) {
            if (row < minRow) {
                minRow = row;
            }
        }

        for (int column : columns) {
            if (column < minColumn) {
                minColumn = column;
            }
        }

        long startOffset = hexVisibleTable.getByteOffset(minRow, minColumn);
        if (startOffset >= hexDocument.length()) {
            throw new IOException("Выделение вне границ документа.");
        }

        return startOffset;
    }

    private long getSelectionEndOffset() throws IOException {
        int[] rows = hexJTable.getSelectedRows();
        int[] columns = hexJTable.getSelectedColumns();

        if (rows.length == 0 || columns.length == 0) {
            throw new IOException("Нет выделения.");
        }

        int maxRow = rows[0];
        int maxColumn = columns[0];

        for (int row : rows) {
            if (row > maxRow) {
                maxRow = row;
            }
        }

        for (int column : columns) {
            if (column > maxColumn) {
                maxColumn = column;
            }
        }

        long endOffset = hexVisibleTable.getByteOffset(maxRow, maxColumn);
        long maxDocumentOffset = hexDocument.length() - 1;

        return Math.min(endOffset, maxDocumentOffset);
    }

    private long getSelectionLength() throws IOException {
        long start = getSelectionStartOffset();
        long end = getSelectionEndOffset();
        return end - start + 1;
    }

    private byte[] readBytes(long startOffset, long length) throws IOException {
        if (length <= 0) {
            return new byte[0];
        }

        byte[] data = new byte[(int) length];

        for (int i = 0; i < length; i++) {
            data[i] = hexDocument.readByte(startOffset + i);
        }

        return data;
    }

    //методы редактирования
    public void copySelection() {
        if (hexDocument == null) {
            showWarningMessage("Сначала откройте файл.");
            return;
        }

        try {
            long startOffset = getSelectionStartOffset();
            long length = getSelectionLength();
            clipboard.setBytes(readBytes(startOffset, length));
        } catch (IOException e) {
            showWarningMessage("Сначала выделите байт или диапазон байтов.");
        }
    }

    public void cutSelection() {
        if (hexDocument == null) {
            showWarningMessage("Сначала откройте файл.");
            return;
        }

        try {
            long startOffset = getSelectionStartOffset();
            long length = getSelectionLength();

            clipboard.setBytes(readBytes(startOffset, length));
            hexDocument.delete(startOffset, length, DeleteOption.SHIFT_LEFT);

            refreshDocumentView();
        } catch (IOException e) {
            showErrorMessage("Ошибка при вырезании: " + e.getMessage());
        }
    }

    public void deleteSelection() {
        if (hexDocument == null) {
            showWarningMessage("Сначала откройте файл.");
            return;
        }

        try {
            long startOffset = getSelectionStartOffset();
            long length = getSelectionLength();

            hexDocument.delete(startOffset, length, DeleteOption.SHIFT_LEFT);
            refreshDocumentView();
        } catch (IOException e) {
            showErrorMessage("Ошибка при удалении: " + e.getMessage());
        }
    }

    public void zeroFillSelection() {
        if (hexDocument == null) {
            showWarningMessage("Сначала откройте файл.");
            return;
        }

        try {
            long startOffset = getSelectionStartOffset();
            long length = getSelectionLength();

            hexDocument.delete(startOffset, length, DeleteOption.ZERO_FILL);
            refreshDocumentView();
        } catch (IOException e) {
            showErrorMessage("Ошибка при обнулении: " + e.getMessage());
        }
    }

    public void pasteClipboard() {
        if (hexDocument == null) {
            showWarningMessage("Сначала откройте файл.");
            return;
        }

        if (clipboard.hasData()) {
            showWarningMessage("Буфер обмена пуст.");
            return;
        }

        try {
            long offset = getSelectionStartOffset();
            hexDocument.insert(offset, clipboard.getBytes(), InsertOption.SHIFT_RIGHT);
            refreshDocumentView();
        } catch (IOException e) {
            showErrorMessage("Ошибка при вставке: " + e.getMessage());
        }
    }

    public void pasteOverwrite() {
        if (hexDocument == null) {
            showWarningMessage("Сначала откройте файл.");
            return;
        }

        if (clipboard.hasData()) {
            showWarningMessage("Буфер обмена пуст.");
            return;
        }

        try {
            long offset = getSelectionStartOffset();
            hexDocument.insert(offset, clipboard.getBytes(), InsertOption.OVERWRITE);
            refreshDocumentView();
        } catch (IOException e) {
            showErrorMessage("Ошибка при вставке с заменой: " + e.getMessage());
        }
    }

    public void insertHex() {
        if (hexDocument == null) {
            showWarningMessage("Сначала откройте файл.");
            return;
        }

        String input = JOptionPane.showInputDialog(
                parent,
                "Введите hex-байты без",
                ""
        );

        if (input == null) {
            return;
        }

        input = input.trim();
        if (input.isEmpty()) {
            return;
        }

        try {
            byte[] data = parseHexBytes(input);
            long offset = getSelectionStartOffset();
            hexDocument.insert(offset, data, InsertOption.SHIFT_RIGHT);
            refreshDocumentView();
        } catch (Exception e) {
            showErrorMessage("Ошибка при вставке hex: " + e.getMessage());
        }
    }

    private byte[] parseHexBytes(String text) {
        String normalized = text.trim().replaceAll("\\s+", " ");
        String[] parts = normalized.split(" ");

        byte[] result = new byte[parts.length];

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();

            if (part.length() != 2) {
                throw new IllegalArgumentException("Каждый байт должен состоять из двух hex-символов.");
            }

            int value = Integer.parseInt(part, 16);
            result[i] = (byte) value;
        }

        return result;
    }

    //методы сообщений
    private void showErrorMessage(String message){
        JOptionPane.showMessageDialog(
                parent,
                message,
                "Ошибка",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showWarningMessage(String  message){
        JOptionPane.showMessageDialog(
                parent,
                message,
                "Предупреждение",
                JOptionPane.WARNING_MESSAGE
        );
    }


    //Методы поиска
    public void showSearchDialog() {
        if (hexDocument == null) {
            showWarningMessage("Сначала откройте файл.");
            return;
        }

        String input = JOptionPane.showInputDialog(
                parent,
                "Введите байты или маску. ?? — любой байт",
                "Поиск",
                JOptionPane.QUESTION_MESSAGE
        );

        if (input == null) {
            return;
        }

        input = input.trim();
        if (input.isEmpty()) {
            showWarningMessage("Введите последовательность для поиска.");
            return;
        }

        try {
            MaskPattern pattern = hexSearch.parseMaskPattern(input);
            long foundOffset = hexSearch.findMaskedPattern(hexDocument, pattern);

            if (foundOffset < 0) {
                showWarningMessage("Совпадение не найдено.");
                return;
            }

            navigateToFoundOffset(foundOffset);
        } catch (IllegalArgumentException e) {
            showWarningMessage(e.getMessage());
        } catch (IOException e) {
            showErrorMessage("Ошибка при поиске: " + e.getMessage());
        }
    }
    private void navigateToFoundOffset(long foundOffset) {
        long rowStartOffset = foundOffset - (foundOffset % hexVisibleTable.getBytesInRow());
        setHexVisibleTableOffset(rowStartOffset);
        selectSingleByte(foundOffset);
    }

    private void selectSingleByte(long offset) {
        long pageStart = hexVisibleTable.getTableOffset();
        long relativeOffset = offset - pageStart;

        if (relativeOffset < 0) {
            return;
        }

        int bytesInRow = hexVisibleTable.getBytesInRow();
        int row = (int) (relativeOffset / bytesInRow);
        int column = (int) (relativeOffset % bytesInRow);

        if (row < 0 || row >= hexVisibleTable.getVisibleRows()) {
            return;
        }

        hexJTable.clearSelection();
        hexJTable.changeSelection(row, column, false, false);
        updateSelectedByteInfo();
    }
}
