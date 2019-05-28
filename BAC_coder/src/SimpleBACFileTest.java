import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class SimpleBACFileTest {

    @Test
    void test() throws IOException {
        final String s1="uniform.pgm";//albo co kto chce
        final String s2="c.txt";
        final String s3="out.pgm";

        BACFileReader BACReader = new BACFileReader("test.bac");
        BACFileWriter BACWriter = new BACFileWriter();


        Integer[] out = BAC_coder.codeFromFileToFile(s1, s2);
        PGMFileReader fileReader;
        fileReader = new PGMFileReader(s1);
        AlphabetIntervals alphabetIntervals = new AlphabetIntervals(fileReader);

        System.out.println("Wymiary: " + BACReader.getWidth() + "x"+BACReader.getHeight());
        System.out.println("D³ugoœæ: " + BACReader.getDataSize());

        // wczytanie skompresowanego ci¹gu
        for (int i = 0; i < BACReader.getDataSize(); ++i) {
            int element=BACReader.getElement();
        }


        assertTrue(BACReader.getWidth() == 512);
        assertTrue(BACReader.getHeight() == 512);
    }

}
