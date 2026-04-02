package vvojtyuk.hexeditor.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final MainMenuBar mainMenuBar = new MainMenuBar();
    private final JToolBar toolBar = new JToolBar();
    private final HexTable hexTable = new HexTable();

    public MainFrame(){
        initFrame();
        setJMenuBar(mainMenuBar);
        add(new JTable(hexTable));

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
        toolBar.add(new JLabel("Строк"));
    }

}
