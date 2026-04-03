package vvojtyuk.hexeditor.ui;

import javax.swing.*;

public class MainMenuBar extends JMenuBar{
    private final JMenuItem openItem = new JMenuItem("Открыть");
    private final JMenuItem saveItem = new JMenuItem("Сохранить");
    private final JMenuItem saveAsItem = new JMenuItem("Сохранить как");

    private final JMenuItem copyItem = new JMenuItem("Копировать");
    private final JMenuItem cutItem = new JMenuItem("Вырезать");
    private final JMenuItem pasteItem = new JMenuItem("Вставить");
    private final JMenuItem pasteOverwriteItem = new JMenuItem("Заменить");
    private final JMenuItem insertHexItem = new JMenuItem("Вставить hex");
    private final JMenuItem deleteItem = new JMenuItem("Удалить");
    private final JMenuItem makeZeroItem = new JMenuItem("Обнулить");

    private final JMenuItem searchItem = new JMenuItem("Поиск");

    public MainMenuBar(){
        JMenu fileMenu = new JMenu("Файл");
        JMenu editMenu = new JMenu("Редактирование");
        JMenu searchMenu = new JMenu("Поиск");

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);

        editMenu.add(copyItem);
        editMenu.add(cutItem);
        editMenu.add(pasteItem);
        editMenu.add(pasteOverwriteItem);
        editMenu.add(insertHexItem);
        editMenu.add(deleteItem);
        editMenu.add(makeZeroItem);

        searchMenu.add(searchItem);

        add(fileMenu);
        add(editMenu);
        add(searchMenu);
    }

    public JMenuItem getOpenItem(){
        return openItem;
    }

    public JMenuItem getSaveItem(){
        return saveItem;
    }

    public JMenuItem getSaveAsItem(){
        return saveAsItem;
    }

}
