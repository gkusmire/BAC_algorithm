import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class SimpleCodeAndDecodeTest {

	@Test
	void test() throws IOException {
		final String s1="laplace_10.pgm";//albo co kto chce
		final String s2="test.bac";
		final String s3="out.pgm";
		Integer[] out = BAC_coder.codeFromFileToFile(s1, s2);
		PGMFileReader fileReader;
		fileReader = new PGMFileReader(s1);
		AlphabetIntervals alphabetIntervals = new AlphabetIntervals(fileReader);

		BAC_decoder.decodeFromFileToFile(s2, s3);
		byte[] f1 = Files.readAllBytes(Path.of(s1));
		byte[] ft = Files.readAllBytes(Path.of(s2));
		byte[] f2 = Files.readAllBytes(Path.of(s3));
		double d=0;
		double mean_distance=0;
		String s="";
		/*
		for(int i=0;i<f1.length;i++) {
			if(f1[i]!=f2[i])
			{
				d++;
				s+=i+":"+f1[i]+"->"+f2[i]+",";
			}
			mean_distance+=f1[i]-f2[i];
		}
		//odczyt zapis wymaga poprawienia, bo czasem przekrêca wartoœci
		// za bardzo obci¹¿a//System.out.println(s);
		System.out.println("d="+d);
		System.out.println("Stopieñ zgodnoœci: "+((f1.length-d)/f1.length));
		System.out.println("Œrednia ró¿nica wartoœci: "+(mean_distance/f1.length));
		*/
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
