import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BACFileWriter {

    /**
     * Magic number representing the binary BAC file type.
     */
    private static final String MAGIC = "BAC";

    /**
     * The maximum gray value.
     */
    private static final int MAXVAL = 255;

    /**
     * Writes a grayscale image to a file in PGM format.
     * @param image a two-dimensional byte array representation of the image
     * @param file the file to write to
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static void write(final Integer[] data, AlphabetIntervals image, final File file) throws IOException {

        final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
        try {
            // nag��wek
            stream.write(MAGIC.getBytes());
            stream.write("\n".getBytes());
            // rozmiar s�owa sekwencji
            stream.write(Integer.toString(image.getSymbolSize()).getBytes());
            stream.write("\n".getBytes());
            // d�ugo�� oryginalnej sekwencji
            stream.write(Integer.toString(image.getFileContent().length/image.getWidth()).getBytes());
            stream.write(" ".getBytes());
            // szeroko�� obrazka
            stream.write(Integer.toString(image.getWidth()).getBytes());
            stream.write("\n".getBytes());
            // d�ugo�� sekwencji skompresowanej
            // jednostka --- bajt???
            stream.write(Integer.toString(data.length).getBytes());
            stream.write("\n".getBytes());
            // statystyki
            stream.write(Integer.toString(image.getNumber()).getBytes());
            for(int i = 0; i < image.getNumber(); ++i)
                stream.write((" "+image.getNthSymbol(i) + " " + image.getAlphabetElementCount(image.getNthSymbol(i))).getBytes());

            stream.write("\n".getBytes());
            for (int i = 0; i < data.length; ++i) {
                    final int p = data[i];

                    stream.write(data[i]);

            }
        } finally {
            stream.close();
        }
    }
}
