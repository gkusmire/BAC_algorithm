import java.io.*;
import java.util.Scanner;

public class BACFileReader {
    private int dataSize;
    public BACFileReader(String _fileName) throws IOException {

        fileName = _fileName;
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

        setWidthAndHeightOfPicture(fileName);
        initPGMPictureReader(fileName);
    }

    private void setWidthAndHeightOfPicture(String fileName) throws IOException {

        fileInputStream = new FileInputStream(fileName);
        Scanner scanner = new Scanner(fileInputStream);

        scanner.nextLine(); //skip first line of header


        String bytesPerWord = scanner.nextLine();
        String pictureSize = scanner.nextLine();
        String _dataSize = scanner.nextLine();

        fileInputStream.close();

        String[] sizeString = pictureSize.split(" ");

        int BytesPerWord = Integer.parseInt(bytesPerWord);
        System.out.println("BPW: " + BytesPerWord);
        width = Integer.parseInt(sizeString[0]);
        height = Integer.parseInt(sizeString[1]);
        dataSize = Integer.parseInt(_dataSize);
    }

    private void initPGMPictureReader(String fileName) throws IOException {

        fileInputStream = new FileInputStream(fileName);
        dis = new DataInputStream(fileInputStream);

        skipHeader();
    }

    private void skipHeader() throws IOException {

        int numnewlines = 4;
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
