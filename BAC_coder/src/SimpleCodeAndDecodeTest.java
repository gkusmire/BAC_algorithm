import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class SimpleCodeAndDecodeTest {

	@Test
	void test() throws IOException {
		final String s1="apollonian_gasket.ascii.pgm";//albo co kto chce
		final String s2="c.txt";
		final String s3="out.pgm";
		BAC_coder.codeFromFileToFile(s1, s2);
		BAC_decoder.decodeFromFileToFile(s2, s3);
		byte[] f1 = Files.readAllBytes(Path.of(s1));
		byte[] f2 = Files.readAllBytes(Path.of(s3));
		assertTrue(Arrays.equals(f1, f2));
	}

}
