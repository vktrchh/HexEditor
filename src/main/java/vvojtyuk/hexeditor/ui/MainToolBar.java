package vvojtyuk.hexeditor.ui;

import javax.swing.*;


class MainToolBar extends JToolBar {
    private final JButton startButton = new JButton("|<");
    private final JButton endButton = new JButton("|>");
    private final JButton pageUpButton = new JButton("<<");
    private final JButton pageDownButton = new JButton(">>");

    private final JTextField bytesInRowField = new JTextField("16", 4);
    private final JTextField visibleRowsField = new JTextField("16", 4);

    public MainToolBar() {
        setFloatable(false);

        add(startButton);
        add(pageUpButton);
        add(pageDownButton);
        add(endButton);

        addSeparator();

        add(new JLabel("Байт в строке"));
        add(bytesInRowField);

        addSeparator();

        add(new JLabel("Строк"));
        add(visibleRowsField);
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getEndButton() {
        return endButton;
    }

    public JButton getPageUpButton() {
        return pageUpButton;
    }

    public JButton getPageDownButton() {
        return pageDownButton;
    }

    public JTextField getBytesInRowField() {
        return bytesInRowField;
    }

    public JTextField getVisibleRowsField() {
        return visibleRowsField;
    }
}
