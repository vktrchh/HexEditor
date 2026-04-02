package vvojtyuk.hexeditor.ui;

import vvojtyuk.hexeditor.io.FileByteReader;

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

    private final JTable offsetTable = new JTable(new OffsetTable(hexVisibleTable));
    private final JScrollPane scrollPane = new JScrollPane(jHexTable);

    public MainFrame(){
        initFrame();
        setJMenuBar(mainMenuBar);
        initTables();
        initLayout();
        mainMenuBar.getOpenItem().addActionListener(e -> openFile());
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

    public void initAction(){}

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
        } catch (IOException e){
            return;
        }
    }
}
