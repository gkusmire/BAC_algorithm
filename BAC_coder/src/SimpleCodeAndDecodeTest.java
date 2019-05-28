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
		final String s2="c.txt";
		final String s3="out.pgm";
		Integer[] out = BAC_coder.codeFromFileToFile(s1, s2);
		PGMFileReader fileReader;
		fileReader = new PGMFileReader(s1);
		AlphabetIntervals alphabetIntervals = new AlphabetIntervals(fileReader);

		BAC_decoder.decodeFromFileToFile(s2, s3,alphabetIntervals);
		byte[] f1 = Files.readAllBytes(Path.of(s1));
		byte[] ft = Files.readAllBytes(Path.of(s2));
		byte[] f2 = Files.readAllBytes(Path.of(s3));
		double d=0;
		double mean_distance=0;
		String s="";
		for(int i=0;i<f1.length;i++) {
			if(f1[i]!=f2[i])
			{
				d++;
				s+=i+":"+f1[i]+"->"+f2[i]+",";
			}
			mean_distance+=f1[i]-f2[i];
		}
		//odczyt zapis wymaga poprawienia, bo czasem przekr�ca warto�ci
		System.out.println(s);
		System.out.println("d="+d);
		System.out.println("Stopie� zgodno�ci: "+((f1.length-d)/f1.length));
		System.out.println("�rednia r�nica warto�ci: "+(mean_distance/f1.length));
		assertTrue(Arrays.equals(f1, f2));
	}

}
