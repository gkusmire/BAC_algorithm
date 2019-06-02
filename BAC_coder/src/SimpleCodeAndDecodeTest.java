import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class SimpleCodeAndDecodeTest {

	@Test
	void test() throws IOException {
		final String s1="uniform.pgm";//albo co kto chce
		final String s2="test.bac";
		final String s3="out.pgm";
		Integer[] out = BAC_coder.codeFromFileToFile(s1, s2);
		PGMFileReader fileReader;
		fileReader = new PGMFileReader(s1);
		AlphabetIntervals alphabetIntervals = new AlphabetIntervals(fileReader);

		BAC_decoder.decodeFromFileToFile(s2, s3);

		double d=0;
		double mean_distance=0;
		String s="";

		PGMFileReader testIn, testOut;
		testIn = new PGMFileReader(s1);
		testOut = new PGMFileReader(s3);
		int dataSize = alphabetIntervals.getFileContent().length;
		for(int i=0;i<dataSize;i++) {
			int inVal = testIn.getElement();
			int outVal = testOut.getElement();
			if(inVal != outVal)
			{
				d++;
				s+=i+":"+inVal+"->"+outVal+",";
			}
			mean_distance+=(inVal - outVal);
		}
		// (?) odczyt zapis wymaga poprawienia, bo czasem przekrêca wartoœci
		System.out.println(s);
		System.out.println("d="+d);
		System.out.println("Stopieñ zgodnoœci: "+((dataSize-d)/dataSize));
		System.out.println("Œrednia ró¿nica wartoœci: "+(mean_distance/dataSize));

		//assertTrue(Arrays.equals(f1, f2));

		BACFileReader bReader = new BACFileReader(s2);

		// Sprawdzenie czy odczytane z pliku statystyki s¹ takie jak zapisywane
		assertTrue(alphabetIntervals.getNumber() == bReader.getNumber());

		for(int i = 0; i<bReader.getNumber();i++) {
			int symbol = alphabetIntervals.getNthSymbol(i);
			Pair<Integer,Integer> aiSrc = alphabetIntervals.getAlphabetElementInterval(symbol);
			Pair<Integer,Integer> aiDst = bReader.getAlphabetElementInterval(symbol);

			assertTrue(aiSrc.leftVal() - aiDst.leftVal() == 0);
			assertTrue(aiSrc.rightVal() - aiDst.rightVal() == 0);
			assertTrue(alphabetIntervals.getNthSymbol(i) == bReader.getNthSymbol(i));
		}

	}

}
