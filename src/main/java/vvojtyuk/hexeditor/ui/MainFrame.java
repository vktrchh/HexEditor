package vvojtyuk.hexeditor.ui;

import vvojtyuk.hexeditor.io.FileByteReader;
import vvojtyuk.hexeditor.util.NavigationToHexTable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainFrame extends JFrame {
    private final MainMenuBar mainMenuBar = new MainMenuBar();
    private final MainToolBar toolBar = new MainToolBar();
    private final ByteInfoPanel byteInfoPanel = new ByteInfoPanel();

    private final HexVisibleTable hexVisibleTable = new HexVisibleTable();

    private FileByteReader fileByteReader;

    private final HexTable hexTable = new HexTable(hexVisibleTable);
    private final JTable jHexTable = new JTable(hexTable);

    private final OffsetTable offsetTableModel = new OffsetTable(hexVisibleTable);
    private final JTable offsetTable = new JTable(offsetTableModel);
    private final JScrollPane scrollPane = new JScrollPane(jHexTable);

    private final NavigationToHexTable navigationToHexTable = new NavigationToHexTable(hexVisibleTable);

    public MainFrame(){
        initFrame();
        setJMenuBar(mainMenuBar);
        initTables();
        initAction();
        initLayout();
    }

    public void initFrame(){
        setTitle("Hex Editor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
    }

    public void initTables(){
        jHexTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jHexTable.setCellSelectionEnabled(true);
        jHexTable.setRowSelectionAllowed(true);
        jHexTable.setColumnSelectionAllowed(true);

        offsetTable.setEnabled(false);
        offsetTable.setFocusable(false);
        offsetTable.setRowSelectionAllowed(false);
        offsetTable.setCellSelectionEnabled(false);

        scrollPane.setRowHeaderView(offsetTable);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, offsetTable.getTableHeader());

        offsetTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        offsetTable.setPreferredScrollableViewportSize(new Dimension(80, 0));
        scrollPane.getRowHeader().setPreferredSize(new Dimension(80, 0));
    }

    private void initLayout(){
        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(byteInfoPanel, BorderLayout.SOUTH);
    }

    public void initAction(){
        mainMenuBar.getOpenItem().addActionListener(e -> openFile());

        toolBar.getStartButton().addActionListener(e -> setHexVisibleTableOffset(navigationToHexTable.moveToStart()));
        toolBar.getPageUpButton().addActionListener(e -> setHexVisibleTableOffset(navigationToHexTable.movePageUp()));
        toolBar.getPageDownButton().addActionListener(e -> setHexVisibleTableOffset(navigationToHexTable.movePageDown()));
        toolBar.getEndButton().addActionListener(e -> setHexVisibleTableOffset(navigationToHexTable.moveToEnd()));

        toolBar.getBytesInRowField().addActionListener(e -> applyHexVisibleTableSettings());
        toolBar.getVisibleRowsField().addActionListener(e -> applyHexVisibleTableSettings());

        jHexTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectedByteInfo();
            }
        });

        jHexTable.getColumnModel().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectedByteInfo();
            }
        });
    }

    public void openFile(){
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if(result != JFileChooser.APPROVE_OPTION){
            return;
        }
        File file = chooser.getSelectedFile();

        try {
            if (fileByteReader != null) {
                fileByteReader.close();
            }

            fileByteReader = new FileByteReader(file);
            hexTable.setFileByteReader(fileByteReader);
            navigationToHexTable.setFileByteReader(fileByteReader);
            setHexVisibleTableOffset(0);
            byteInfoPanel.clearSelectionInfo();
        } catch (IOException e){
            return;
        }
    }

    private void setHexVisibleTableOffset(long offset){
        hexVisibleTable.setTableOffset(offset);
        hexTable.fireTableDataChanged();
        offsetTableModel.fireTableDataChanged();
        byteInfoPanel.setViewOffset(hexVisibleTable.getTableOffset());
        updateSelectedByteInfo();
    }

    private void applyHexVisibleTableSettings() {
        try {
            int bytesInRow = Integer.parseInt(toolBar.getBytesInRowField().getText().trim());
            int visibleRows = Integer.parseInt(toolBar.getVisibleRowsField().getText().trim());

            hexVisibleTable.setBytesInRow(bytesInRow);
            hexVisibleTable.setVisibleRows(visibleRows);

            long normalizedOffset = navigationToHexTable.normalizeOffset(hexVisibleTable.getTableOffset());
            hexVisibleTable.setTableOffset(normalizedOffset);

            hexTable.fireTableStructureChanged();
            offsetTableModel.fireTableDataChanged();

            offsetTable.getColumnModel().getColumn(0).setPreferredWidth(80);

            for (int i = 0; i < jHexTable.getColumnModel().getColumnCount(); i++) {
                jHexTable.getColumnModel().getColumn(i).setPreferredWidth(42);
            }
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(
                    this,
                    "Введите целые числа",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private byte[] readByteBlock(long startOffset, int size) throws IOException {
        if (fileByteReader == null || startOffset < 0) {
            return null;
        }

        if (startOffset + size > fileByteReader.length()) {
            return null;
        }

        byte[] data = new byte[size];

        for (int i = 0; i < size; i++) {
            data[i] = fileByteReader.readByte(startOffset + i);
        }

        return data;
    }

    private void updateSelectedByteInfo() {
        if (fileByteReader == null) {
            byteInfoPanel.clearSelectionInfo();
            return;
        }

        int row = jHexTable.getSelectedRow();
        int column = jHexTable.getSelectedColumn();

        if (row < 0 || column < 0) {
            byteInfoPanel.clearSelectionInfo();
            return;
        }

        long offset = hexVisibleTable.getByteOffset(row, column);

        try {
            if (offset >= fileByteReader.length()) {
                byteInfoPanel.clearSelectionInfo();
                return;
            }

            byte value = fileByteReader.readByte(offset);
            byte[] block2 = readByteBlock(offset, 2);
            byte[] block4 = readByteBlock(offset, 4);
            byte[] block8 = readByteBlock(offset, 8);

            byteInfoPanel.showSelection(offset, value, block2, block4, block8);
        } catch (IOException e) {
            byteInfoPanel.clearSelectionInfo();
        }
    }
}
