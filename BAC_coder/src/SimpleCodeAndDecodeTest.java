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
		BAC_coder.codeFromFileToFile(s1, s2);
		BAC_decoder.decodeFromFileToFile(s2, s3);
		byte[] f1 = Files.readAllBytes(Path.of(s1));
		byte[] ft = Files.readAllBytes(Path.of(s2));
		byte[] f2 = Files.readAllBytes(Path.of(s3));
		double d=0;
		double mean_distance=0;
		for(int i=0;i<f1.length;i++) {
			if(f1[i]!=f2[i])
			{
				d++;
			}
			mean_distance+=f1[i]-f2[i];
		}
		//odczyt zapis wymaga poprawienia, bo czasem przekrêca wartoœci
		System.out.println("Stopieñ zgodnoœci: "+((f1.length-d)/f1.length));
		System.out.println("Œrednia ró¿nica wartoœci: "+(mean_distance/f1.length));
		assertTrue(Arrays.equals(f1, f2));
	}

}
