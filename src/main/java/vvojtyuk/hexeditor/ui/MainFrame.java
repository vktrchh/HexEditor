package vvojtyuk.hexeditor.ui;

import vvojtyuk.hexeditor.io.FileByteReader;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainFrame extends JFrame {
    private final MainMenuBar mainMenuBar = new MainMenuBar();
    private final JToolBar toolBar = new JToolBar();

    private final HexVisibleTable hexVisibleTable = new HexVisibleTable();

    private FileByteReader fileByteReader;
    private final HexTable hexTable = new HexTable(hexVisibleTable);
    private final JTable jHexTable = new JTable(hexTable);
    private final OffsetTable offsetTable = new OffsetTable(hexVisibleTable);

    private final JTextField bytesInRow = new JTextField("16", 4);
    private final JTextField visibleRows = new JTextField("16", 4);

    public MainFrame(){
        initFrame();
        setJMenuBar(mainMenuBar);
        add(new JTable(offsetTable));
        add(toolBar, BorderLayout.NORTH);
        add(new JScrollPane(jHexTable));
        initToolBar();
        mainMenuBar.getOpenItem().addActionListener(e -> openFile());
    }

    public void initFrame(){
        setTitle("Hex Editor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
    }

    public void initToolBar(){
        toolBar.add(new JLabel("Байт в строке"));
        toolBar.add(bytesInRow);
        toolBar.add(new JLabel("Строк"));
        toolBar.add(visibleRows);
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
        } catch (IOException e){
            return;
        }
    }
}
