import java.awt.geom.PathIterator;
import java.io.*;
import java.util.*;

public class BACFileReader {
    private int dataSize;
    final int bitsPerWord = 8;
    final int max = 255;
    private List<Integer> data;
    public BACFileReader(String _fileName) throws IOException {
        _get_index = 0;
        alphabetIntervals = new HashMap<>();
        fileName = _fileName;
        data = new ArrayList<>();
        init(fileName);
    }

    public int getElement() throws IOException {

        return dis.readUnsignedByte();//ta metoda zwraca 8 bit�w, wi�c dla porz�dku zwracaamy byte
    }

    // pobieranie bit�w
    public int get() throws IOException {
        if(eof())
            throw(new IOException("Przekroczono rozmiar danych!"));
        if(_get_index % bitsPerWord == 0) {
            _input = data.get(_get_index/bitsPerWord) & max;
        } else
            _input = _input >>1;
        int bit = _input & 0b1;
        _get_index++;
        return bit;
    }
    public Boolean eof() {
        Boolean result = _get_index/bitsPerWord >= data.size();
        if(result) System.out.println("EOF at "+_get_index + " -> " + _get_index/bitsPerWord);
        return result;
    }
    public void rewind() { _get_index = 0; }
    private int _get_index, _input;

    public int getBitSize() { return dataSize*bitsPerWord; }
    public int getDataSize() { return dataSize; }

    public int getWidth() {

        return width;
    }

    public int getHeight() {

        return height;
    }

    private void init(String fileName) throws IOException {

        readBACHeader(fileName);
        initReader(fileName);
    }

    private void readBACHeader(String fileName) throws IOException {

        fileInputStream = new FileInputStream(fileName);
        Scanner scanner = new Scanner(fileInputStream);

        scanner.nextLine(); //skip first line of header


        String bytesPerWord = scanner.nextLine();
        String pictureSize = scanner.nextLine();
        String _dataSize = scanner.nextLine();
        // ile symboli i te symbole (jako czytelne liczby)
        String [] _stats = scanner.nextLine().split(" ");

        fileInputStream.close();

        String[] sizeString = pictureSize.split(" ");

        int BytesPerWord = Integer.parseInt(bytesPerWord);
        width = Integer.parseInt(sizeString[0]);
        height = Integer.parseInt(sizeString[1]);
        dataSize = Integer.parseInt(_dataSize);
        // parsowanie statystyk
        Integer[] stats =  Arrays.stream(_stats).map(Integer::parseInt).toArray(Integer[]::new);
        if(stats[0] * 2 + 1 != stats.length)
            throw new IOException("Niepoprawny format statystyk!");
        // TODO zapis statystyk do sensownej reprezentacji
        for(int i = 0; i<stats[0]; ++i) {
            int symbol = stats[1 + i * 2];
            int count = stats[1 + i * 2 + 1];
            // System.out.println("Symbol: " +symbol +" x " + count);

        }
        fillAlphabetIntervals(Arrays.copyOfRange(stats, 1, stats.length));

    }

    private void initReader(String fileName) throws IOException {

        fileInputStream = new FileInputStream(fileName);
        dis = new DataInputStream(fileInputStream);

        skipHeader();

        for(int i = 0; i<getDataSize(); i++) {
            int elem = (int) getElement();
            data.add(elem);
        }
    }

    private void skipHeader() throws IOException {

        int numnewlines = 5;
        while (numnewlines > 0) {
            char c;
            do {
                c = (char)(dis.readUnsignedByte());
            } while (c != '\n');
            numnewlines--;
        }
    }

    public Pair<Integer, Integer> getAlphabetElementInterval(Integer elem) {

        return alphabetIntervals.get(elem);
    }

    private void fillAlphabetIntervals(Integer[] input) {
        int lo = 0, hi = 0;
        for(int i = 0; i < input.length/2; i++) {
            lo = hi;
            hi += input[2 * i + 1];
            Integer symbol = input[2 * i];
            alphabetIntervals.put(symbol, new Pair<>(lo,hi));
            //System.out.println(i + " : " + lo + " --- " + hi);
            values.add(symbol);
        }
    }

    private FileInputStream fileInputStream;
    private DataInputStream dis;
    private Scanner scanner;
    private String fileName;
    private int width, height;

    public int getNumber() { return alphabetIntervals.size(); }

    public int getNthSymbol(int index) {
        return values.get(index);
    }
    // lista u�ywanych warto�ci - w dekoderze potrzebny jest dost�p jako n-ty element z linii prawdopodobie�stw
    private List<Integer> values = new ArrayList<>();
    private Map<Integer, Pair<Integer, Integer>> alphabetIntervals;
}
