package vvojtyuk.hexeditor.ui;

import javax.swing.*;
import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/*
    Информационная панель. Показывает смещение, знаковое и беззнаковое представление байта,
    блоки по 2/4/8 байт(little-endian)
 */

public class ByteInfoPanel extends JPanel {
    private final JLabel viewOffsetLabel = new JLabel("View offset: 00000000");
    private final JLabel selectedOffsetLabel = new JLabel("Selected: --");
    private final JLabel selectedHexLabel = new JLabel("Hex: --");
    private final JLabel selectedUnsignedLabel = new JLabel("Unsigned: --");
    private final JLabel selectedSignedLabel = new JLabel("Signed: --");

    private final JLabel int16Label = new JLabel("Int16: --");
    private final JLabel uint16Label = new JLabel("UInt16: --");

    private final JLabel int32Label = new JLabel("Int32: --");
    private final JLabel uint32Label = new JLabel("UInt32: --");
    private final JLabel floatLabel = new JLabel("Float: --");

    private final JLabel int64Label = new JLabel("Int64: --");
    private final JLabel uint64Label = new JLabel("UInt64: --");
    private final JLabel doubleLabel = new JLabel("Double: --");

    public ByteInfoPanel() {
        setLayout(new GridLayout(2, 1));

        JPanel firstRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        firstRow.add(viewOffsetLabel);
        firstRow.add(Box.createHorizontalStrut(20));
        firstRow.add(selectedOffsetLabel);
        firstRow.add(Box.createHorizontalStrut(20));
        firstRow.add(selectedHexLabel);
        firstRow.add(Box.createHorizontalStrut(20));
        firstRow.add(selectedUnsignedLabel);
        firstRow.add(Box.createHorizontalStrut(20));
        firstRow.add(selectedSignedLabel);

        JPanel secondRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        secondRow.add(int16Label);
        secondRow.add(Box.createHorizontalStrut(15));
        secondRow.add(uint16Label);
        secondRow.add(Box.createHorizontalStrut(15));
        secondRow.add(int32Label);
        secondRow.add(Box.createHorizontalStrut(15));
        secondRow.add(uint32Label);
        secondRow.add(Box.createHorizontalStrut(15));
        secondRow.add(floatLabel);
        secondRow.add(Box.createHorizontalStrut(15));
        secondRow.add(int64Label);
        secondRow.add(Box.createHorizontalStrut(15));
        secondRow.add(uint64Label);
        secondRow.add(Box.createHorizontalStrut(15));
        secondRow.add(doubleLabel);

        add(firstRow);
        add(secondRow);

        clearSelectionInfo();
    }

    public void setViewOffset(long offset) {
        viewOffsetLabel.setText(String.format("View offset: %08X", offset));
    }

    public void clearSelectionInfo() {
        selectedOffsetLabel.setText("Selected: --");
        selectedHexLabel.setText("Hex: --");
        selectedUnsignedLabel.setText("Unsigned: --");
        selectedSignedLabel.setText("Signed: --");

        int16Label.setText("Int16: --");
        uint16Label.setText("UInt16: --");

        int32Label.setText("Int32: --");
        uint32Label.setText("UInt32: --");
        floatLabel.setText("Float: --");

        int64Label.setText("Int64: --");
        uint64Label.setText("UInt64: --");
        doubleLabel.setText("Double: --");
    }

    public void showSelection(long selectedOffset, byte value, byte[] block2, byte[] block4, byte[] block8) {
        int unsignedValue = value & 0xFF;

        selectedOffsetLabel.setText(String.format("Selected: %08X", selectedOffset));
        selectedHexLabel.setText(String.format("Hex: %02X", unsignedValue));
        selectedUnsignedLabel.setText("Unsigned: " + unsignedValue);
        selectedSignedLabel.setText("Signed: " + value);

        if (block2 != null) {
            ByteBuffer bb2 = ByteBuffer.wrap(block2).order(ByteOrder.LITTLE_ENDIAN);
            short int16Value = bb2.getShort();
            int uint16Value = int16Value & 0xFFFF;

            int16Label.setText("Int16: " + int16Value);
            uint16Label.setText("UInt16: " + uint16Value);
        } else {
            int16Label.setText("Int16: --");
            uint16Label.setText("UInt16: --");
        }

        if (block4 != null) {
            ByteBuffer bb4 = ByteBuffer.wrap(block4).order(ByteOrder.LITTLE_ENDIAN);
            int int32Value = bb4.getInt();
            long uint32Value = int32Value & 0xFFFFFFFFL;
            float floatValue = bb4.getFloat(0);

            int32Label.setText("Int32: " + int32Value);
            uint32Label.setText("UInt32: " + uint32Value);
            floatLabel.setText("Float: " + floatValue);
        } else {
            int32Label.setText("Int32: --");
            uint32Label.setText("UInt32: --");
            floatLabel.setText("Float: --");
        }

        if (block8 != null) {
            ByteBuffer bb8 = ByteBuffer.wrap(block8).order(ByteOrder.LITTLE_ENDIAN);
            long int64Value = bb8.getLong();
            String uint64Value = Long.toUnsignedString(int64Value);
            double doubleValue = bb8.getDouble(0);

            int64Label.setText("Int64: " + int64Value);
            uint64Label.setText("UInt64: " + uint64Value);
            doubleLabel.setText("Double: " + doubleValue);
        } else {
            int64Label.setText("Int64: --");
            uint64Label.setText("UInt64: --");
            doubleLabel.setText("Double: --");
        }
    }
}