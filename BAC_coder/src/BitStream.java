import java.util.ArrayList;

// klasa s³u¿y do zapisu danych bit po bicie
// przechowywane s¹ one w s³owach okreœlonej d³ugoœci
// i tylko tak mo¿na je pobraæ
// do ustalenia rzeczywistej liczby wykorzystanych bitów s³u¿y funkcja getLength()

public class BitStream {
    public BitStream() {
        length = 0;
        list = new ArrayList<>();
        rewind();
    }
    public void rewind() { getPos = 0; }

    public void put(int val) {
        // TODO tylko 0 i 1 dopuszczalne
        val = (val == 0 ? 0 : 1);

        if(length % bitsPerWord == 0) {
            list.add(val);
        } else {
            list.set(list.size() - 1, list.get(list.size() - 1) | (val << (length % bitsPerWord)));
        }
        length++;
    }

    public int get(int index) {
        int bit = ((list.get(index/bitsPerWord) >> (index % bitsPerWord)) & 0b1);
        getPos++;
        return bit;
    }

    public Integer[] asArray() {
        return list.toArray(Integer[]::new);
    }

    public Integer getLength() { return length; };
    int length;
    final Integer bitsPerWord = 8;
    private ArrayList<Integer> list;
    int getPos;
}
