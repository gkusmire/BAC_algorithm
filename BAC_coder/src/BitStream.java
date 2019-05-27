import java.util.ArrayList;

// klasa s�u�y do zapisu danych bit po bicie
// przechowywane s� one w s�owach okre�lonej d�ugo�ci
// i tylko tak mo�na je pobra�
// do ustalenia rzeczywistej liczby wykorzystanych bit�w s�u�y funkcja getLength()

public class BitStream {
    public BitStream() {
        length = 0;
        list = new ArrayList<>();
    }

    public void put(Integer val) {
        // TODO tylko 0 i 1 dopuszczalne
        val = (val == 0 ? 0 : 1);

        if(length % bitsPerWord == 0) {
            list.add(val);
        } else {
            list.set(list.size() - 1, list.get(list.size() - 1) | (val << (length % bitsPerWord)));
        }
        length++;
    }

    public Integer[] asArray() {
        return list.toArray(Integer[]::new);
    }

    public Integer getLength() { return length; };
    Integer length;
    final Integer bitsPerWord = Integer.BYTES * 8;
    private ArrayList<Integer> list;
}
