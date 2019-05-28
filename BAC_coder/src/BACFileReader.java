import java.io.*;
import java.util.Scanner;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class BACFileReader {
    private int dataSize;
    private List<Integer> data;
    public BACFileReader(String _fileName) throws IOException {

        fileName = _fileName;
        data = new ArrayList<>();
        init(fileName);
    }

    public int getElement() throws IOException {

        return dis.readUnsignedByte();//ta metoda zwraca 8 bitów, wiêc dla porz¹dku zwracaamy byte
    }

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
            System.out.println("Symbol: " +symbol +" x " + count);
        }

    }

    public Integer[] getData() { return data.toArray(Integer[]::new); }

    private void initReader(String fileName) throws IOException {

        fileInputStream = new FileInputStream(fileName);
        dis = new DataInputStream(fileInputStream);

        skipHeader();

        for(int i = 0; i<getDataSize();++i)
            data.add(getElement());
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

    private FileInputStream fileInputStream;
    private DataInputStream dis;
    private Scanner scanner;
    private String fileName;
    private int width, height;
}
