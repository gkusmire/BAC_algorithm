import java.io.IOException;

public class BAC_coder {

    public static void main(String[] args) {

        try {
            PGMFileReader fileReader = new PGMFileReader("normal_50.pgm");
            AlphabetIntervals alphabetIntervals = new AlphabetIntervals(fileReader);

            alphabetIntervals.printAlphabetIntervals();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Błąd odczytu danych z pliku!!!");
        }
    }
}
