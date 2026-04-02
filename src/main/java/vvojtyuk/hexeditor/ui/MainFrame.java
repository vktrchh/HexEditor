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
    }

    public void initAction(){
        mainMenuBar.getOpenItem().addActionListener(e -> openFile());

        toolBar.getStartButton().addActionListener(e -> setHexVisibleTableOffset(navigationToHexTable.moveToStart()));

        toolBar.getPageUpButton().addActionListener(e -> setHexVisibleTableOffset(navigationToHexTable.movePageUp()));

        toolBar.getPageDownButton().addActionListener(e -> setHexVisibleTableOffset(navigationToHexTable.movePageDown()));

        toolBar.getEndButton().addActionListener(e -> setHexVisibleTableOffset(navigationToHexTable.moveToEnd()));
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
        } catch (IOException e){
            return;
        }
    }

    private void setHexVisibleTableOffset(long offset){
        hexVisibleTable.setTableOffset(offset);
        hexTable.fireTableDataChanged();
        offsetTableModel.fireTableDataChanged();
    }
}
