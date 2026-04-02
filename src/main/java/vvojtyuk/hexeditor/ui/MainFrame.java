package vvojtyuk.hexeditor.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(){
        initFrame();
    }

    public void initFrame(){
        setTitle("Hex Editor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
    }

}
