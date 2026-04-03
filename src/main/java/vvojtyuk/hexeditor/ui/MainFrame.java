package vvojtyuk.hexeditor.ui;

import vvojtyuk.hexeditor.controller.HexEditorController;
import vvojtyuk.hexeditor.util.NavigationToHexTable;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final MainMenuBar mainMenuBar = new MainMenuBar();
    private final MainToolBar toolBar = new MainToolBar();
    private final ByteInfoPanel byteInfoPanel = new ByteInfoPanel();

    private final HexVisibleTable hexVisibleTable = new HexVisibleTable();

    private final HexTable hexTable = new HexTable(hexVisibleTable);
    private final JTable jHexTable = new JTable(hexTable);

    private final OffsetTable offsetTableModel = new OffsetTable(hexVisibleTable);
    private final JTable offsetTable = new JTable(offsetTableModel);
    private final JScrollPane scrollPane = new JScrollPane(jHexTable);

    private final NavigationToHexTable navigationToHexTable = new NavigationToHexTable(hexVisibleTable);
    private final HexEditorController controller = new HexEditorController(
            this,
            hexVisibleTable,
            hexTable,
            offsetTableModel,
            jHexTable,
            offsetTable,
            byteInfoPanel,
            navigationToHexTable
    );

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
        jHexTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

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
        mainMenuBar.getOpenItem().addActionListener(e -> controller.openFile());
        mainMenuBar.getSaveItem().addActionListener(e -> controller.saveCurrentFile());
        mainMenuBar.getSaveAsItem().addActionListener(e -> controller.saveFileAs());

        mainMenuBar.getCopyItem().addActionListener(e -> controller.copySelection());
        mainMenuBar.getCutItem().addActionListener(e -> controller.cutSelection());
        mainMenuBar.getPasteItem().addActionListener(e -> controller.pasteClipboard());
        mainMenuBar.getPasteOverwriteItem().addActionListener(e -> controller.pasteOverwrite());
        mainMenuBar.getInsertHexItem().addActionListener(e -> controller.insertHex());
        mainMenuBar.getDeleteItem().addActionListener(e -> controller.deleteSelection());
        mainMenuBar.getMakeZeroItem().addActionListener(e -> controller.zeroFillSelection());

        mainMenuBar.getSearchItem().addActionListener(e -> controller.showSearchDialog());

        toolBar.getStartButton().addActionListener(e -> controller.moveToStart());
        toolBar.getPageUpButton().addActionListener(e -> controller.movePageUp());
        toolBar.getPageDownButton().addActionListener(e -> controller.movePageDown());
        toolBar.getEndButton().addActionListener(e -> controller.moveToEnd());

        hexTable.addTableModelListener(e -> controller.updateSelectedByteInfo());

        toolBar.getBytesInRowField().addActionListener(e ->
                controller.applyHexVisibleTableSettings(
                        toolBar.getBytesInRowField().getText(),
                        toolBar.getVisibleRowsField().getText()
                )
        );

        toolBar.getVisibleRowsField().addActionListener(e ->
                controller.applyHexVisibleTableSettings(
                        toolBar.getBytesInRowField().getText(),
                        toolBar.getVisibleRowsField().getText()
                )
        );

        jHexTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                controller.updateSelectedByteInfo();
            }
        });

        jHexTable.getColumnModel().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                controller.updateSelectedByteInfo();
            }
        });
    }

}
