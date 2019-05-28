import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BAC_decoder {
	
	public static Integer[] decode(Integer [] s, BACFileReader fileReader) {
		//int[] s=alphabetIntervals.getFileContent();
		// PLACEHOLDER - interwa³y ze Ÿród³a

		// pobranie statystyki

		int totalCount = fileReader.getWidth() * fileReader.getHeight();

		// dla wyjaœnienia algorytmu nale¿y zapoznaæ siê najpierw z algorytmem kodera
		// dzia³a na analogicznej zasadzie

		// inicjalizacja
		// ustalamy pocz¹tkowe granice przedzia³u - dla dostêpnych 2^m wartoœci po m 0 i 1 w zapisie dwójkowym
		final int m = 20; // d³ugoœæ s³owa
		// maksymalna wartoœæ - je¿eli wybieramy sobie dowoln¹ d³ugoœæ s³owa,
		// trzeba pamiêtaæ o zastosowaniu maski bitowej do wyniku przesuniêcia bitowego
		final int max = (int)Math.pow(2,m) - 1;
		final int half = 0b1 << (m-1);
		final int quat = 0b1 << (m-2);

		int d = 0;   // ustalamy doln¹ granicê na (0...0)
		int g = max; // ustalamy górn¹ granicê na (1...1)

		int k = 0; // numer dekodowanego bitu
		int t = 0b0; // dekodowane s³owo (?)

		// PLACEHOLDER wyjscie.append(wartosc_bitu) --- wypisywanie wyjœcia
		List<Integer> wyjscie = new ArrayList<>();

		// wczytanie m bitów z wejœcia do s³owa t
		int i;
		for(i = 0; i<m && i < s.length;++i) {
			t = s[k];
			k++;
		}
		//

		for(;  i < s.length;++i) {
			k = 0; // indeks dekodowanego symbolu
			while((int) Math.floor(((float)(t - d + 1) * (float)totalCount - 1) / (float)(g - d + 1)) >= fileReader.getAlphabetElementInterval(fileReader.getNthSymbol(k)).leftVal()) // leftVal bo od pocz¹tku
				k++;
			// zdekoduj symbol x k-ty z linii prawdopodobieñstw
			int x = fileReader.getNthSymbol(k);
			wyjscie.add(x);

			//r=gm1-dm1+1;//R = G - D + 1
			int r = g - d + 1; // obliczamy szerokoœæ przedzia³u
			int old_d = d;
			Pair<Integer, Integer> elem = fileReader.getAlphabetElementInterval(x); // zakres wystêpowania symbolu x
			d = (int)Math.floor((double)old_d + (double)r * elem.leftVal());
			g = (int)Math.floor((double)old_d + (double)r * elem.rightVal() - 1);

			// dopóki warunek #1 lub warunek #2 spe³nione
			while((d & half) == (g & half) || ((d >> (m - 2)) == 0b01 && ((g >> (m - 2)) == 0b01))) {
				// warunek #1
				if ((d & half) == (g & half)) {
					int b = (d & half) >> (m - 1); // równy MSB s³ów, do wys³ania na wyjœcie
					// d - przesuniêcie w lewo o 1 i (implicite) uzupe³nienie zerem
					d = (d << 1) & max;
					// g - przesuniêcie w lewo o 1 i uzupe³nienie jedynk¹
					g = ((g << 1) | 1) & max;
					// wczytanie nastêpnego bitu ze strumienia w miejsce MSB
					t  = ((t<<1) & max) + s[k];
					k++;
				}
				// warunek #2
				if ((d >> (m - 2)) == 0b01 && ((g >> (m - 2)) == 0b10)) {
					//przesuñ w lewo bity obu rejestrów z wyj¹tkiem najbardziej
					//znacz¹cych i uzupe³nij rejestry d i g w lewo: d 0 na LSB, g 1 na LSB
					d = ((d << 1) & (max >> 1)) | (d & (half));
					g = (((g << 1) | 1) & (max >> 1)) | (g & (half));
					// s³owo t w lewo o 1 bit i wczytaj nastêpny bit ze strumienia wejœciowego na LSB
					t = ((t << 1) & max) + s[k];
					k++;
					// complement (new) MSB of g, d, t
					// TODO
				}
			}
		}

		return s;
	}
	
	public static void decodeFromFileToFile(String inFileName,String outFileName) {
		//Rozumiem, ¿e informacjê o szerokoœci i wysokoœci mamy zapisan¹ w zakodowanych danych
		//i statystyka te¿ zostanie z nich odtworzona
		File inFile = new File(inFileName);
		StringBuilder sb=new StringBuilder();
		BACFileReader fileReader;
		byte bytes[];
		try {
			bytes=Files.readAllBytes(Path.of(inFileName));
			 fileReader = new BACFileReader("test.bac");
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("B³¹d odczytu danych z pliku!!!");
			return;
		}
		int width=fileReader.getWidth();
		int height=fileReader.getHeight();
		System.out.println("Odczytane wymiary: " + width + "x"+height);
		int dataSize = fileReader.getDataSize();
		// TODO alphabetIntervals na podstawie fileReader
		decode(fileReader.getData(),fileReader);
		// TODO zapis zdekodowanego ci¹gu do pliku
/*
		int image[][]=new int[width][height];
		for(int j=0;j<height;j++)
		{
			for(int i=0;i<width;i++)
			{
				image[j][i]=(bytes[i+j*width] & 0xFF);
				if(image[j][i]<0 || image[j][i]>255)
					System.err.println("image[j][i]="+image[j][i]);
			}
		}
		try {
			PGMFileWriter.write(image, new File(outFileName));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("B³¹d zapisu danych do pliku!!!");
		}
		*/
	}

	public static void main(String[] args) {
		if(args.length<1)
    	{
    		System.out.println("Program ma za pierwszy argument nazwê pliku wejœciowego i za drugi opcjonalny nazwê pliku wyjœciowego!");
    		return;
    	}
		String inFileName=args[0];
    	String outFileName;
    	if(args.length>1)
    		outFileName=args[0];
    	else
    		outFileName="decoded.pgm";
    	//decodeFromFileToFile(inFileName,outFileName);
	}

}
