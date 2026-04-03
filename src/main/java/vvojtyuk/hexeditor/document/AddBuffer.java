package vvojtyuk.hexeditor.document;

import java.util.Arrays;
/*
    Класс для хранения перезаписанных и добавленных байтов
 */
public class AddBuffer {
    private byte[] data = new byte[1024];
    private int size = 0;

    public long append(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return size;
        }

        ensureCapacity(size + bytes.length);

        long start = size;
        System.arraycopy(bytes, 0, data, size, bytes.length);
        size += bytes.length;

        return start;
    }

    private void ensureCapacity(int requiredCapacity) {
        if (requiredCapacity <= data.length) {
            return;
        }

        int newCapacity = data.length;
        while (newCapacity < requiredCapacity) {
            newCapacity *= 2;
        }

        data = Arrays.copyOf(data, newCapacity);
    }

    public byte readByte(long offset) {
        if (offset < 0 || offset >= size) {
            throw new IllegalArgumentException("Смещение вне границ");
        }

        return data[(int) offset];
    }
}
