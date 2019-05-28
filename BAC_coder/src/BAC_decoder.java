import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BAC_decoder {
	
	public static String decode(String s, AlphabetIntervals alphabetIntervals) {
		//int[] s=alphabetIntervals.getFileContent();
		// PLACEHOLDER - interwa�y ze �r�d�a

		// pobranie statystyki

		int totalCount = alphabetIntervals.getFileContent().length;

		// dla wyja�nienia algorytmu nale�y zapozna� si� najpierw z algorytmem kodera
		// dzia�a na analogicznej zasadzie

		// inicjalizacja
		// ustalamy pocz�tkowe granice przedzia�u - dla dost�pnych 2^m warto�ci po m 0 i 1 w zapisie dw�jkowym
		final int m = 8; // d�ugo�� s�owa
		// maksymalna warto�� - je�eli wybieramy sobie dowoln� d�ugo�� s�owa,
		// trzeba pami�ta� o zastosowaniu maski bitowej do wyniku przesuni�cia bitowego
		final int max = (int)Math.pow(2,m) - 1;
		final int half = 0b1 << (m-1);
		final int quat = 0b1 << (m-2);

		int d = 0;   // ustalamy doln� granic� na (0...0)
		int g = max; // ustalamy g�rn� granic� na (1...1)

		int k = 0; // numer dekodowanego bitu
		int t = 0b0; // dekodowane s�owo (?)

		Double pia=0.0;
		Double pib=1.0;

		// PLACEHOLDER wyjscie.append(wartosc_bitu) --- wypisywanie wyj�cia
		StringBuilder wyjscie=new StringBuilder();

		// wczytanie m bit�w z wej�cia do s�owa t
		int i;
		for(i = 0; i<m && i < s.length();++i)
			t = (t<<1) + (s.charAt(k++)=='1' ? 1 : 0 );
		//

		for(;  i < s.length();++i) {
			k = 0; // indeks dekodowanego symbolu
			while( Math.floor(((t - d + 1) * totalCount - 1) / (g - d + 1)) >= alphabetIntervals.getAlphabetElementInterval(alphabetIntervals.getNthSymbol(k)).leftVal()) // leftVal bo od pocz�tku
				k++;
			// zdekoduj symbol x k-ty z linii prawdopodobie�stw
			int x = alphabetIntervals.getNthSymbol(k);

			//r=gm1-dm1+1;//R = G - D + 1
			int r = g - d + 1; // obliczamy szeroko�� przedzia�u
			int old_d = d;
			Pair<Integer, Integer> elem = alphabetIntervals.getAlphabetElementInterval(x); // zakres wyst�powania symbolu x
			d = (int)Math.floor((double)old_d + (double)r * elem.leftVal());
			g = (int)Math.floor((double)old_d + (double)r * elem.rightVal() - 1);

			// dop�ki warunek #1 lub warunek #2 spe�nione
			while((d & half) == (g & half) || ((d >> (m - 2)) == 0b01 && ((g >> (m - 2)) == 0b01))) {
				// warunek #1
				if ((d & half) == (g & half)) {
					int b = (d & half) >> (m - 1); // r�wny MSB s��w, do wys�ania na wyj�cie
					// d - przesuni�cie w lewo o 1 i (implicite) uzupe�nienie zerem
					d = (d << 1) & max;
					// g - przesuni�cie w lewo o 1 i uzupe�nienie jedynk�
					g = ((g << 1) | 1) & max;
					// wczytanie nast�pnego bitu ze strumienia w miejsce MSB
					t  = ((t<<1) & max) + s.charAt(k);
					k++;
				}
				// warunek #2
				if ((d >> (m - 2)) == 0b01 && ((g >> (m - 2)) == 0b10)) {
					//przesu� w lewo bity obu rejestr�w z wyj�tkiem najbardziej
					//znacz�cych i uzupe�nij rejestry d i g w lewo: d 0 na LSB, g 1 na LSB
					d = ((d << 1) & (max >> 1)) | (d & (half));
					g = (((g << 1) | 1) & (max >> 1)) | (g & (half));
					// s�owo t w lewo o 1 bit i wczytaj nast�pny bit ze strumienia wej�ciowego na LSB
					t = ((t << 1) & max) + s.charAt(k);
					k++;
					// complement (new) MSB of g, d, t
					// TODO
				}
			}
		}

		// i co teras? --- dos�a� zera do pe�nych bajt�w?
		for(int j = 0;j<wyjscie.length() % 8; ++j)
			wyjscie.append(0);
		System.out.println(wyjscie.length()+": "+wyjscie);
		return s;
	}
	
	public static void decodeFromFileToFile(String inFileName,String outFileName, AlphabetIntervals alphabetIntervals) {
		//Rozumiem, �e informacj� o szeroko�ci i wysoko�ci mamy zapisan� w zakodowanych danych
		//i statystyka te� zostanie z nich odtworzona
		File inFile = new File(inFileName);
		StringBuilder sb=new StringBuilder(); 
		byte bytes[];
		try {
			bytes=Files.readAllBytes(Path.of(inFileName));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("B��d odczytu danych z pliku!!!");
			return;
		}
		int width=512;//TODO: pobra� szeroko��, na razie ustalona jak w danych wej�ciowych
		int height=512;//TODO: pobra� szeroko��
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
			System.err.println("B��d zapisu danych do pliku!!!");
		}
	}

	public static void main(String[] args) {
		if(args.length<1)
    	{
    		System.out.println("Program ma za pierwszy argument nazw� pliku wej�ciowego i za drugi opcjonalny nazw� pliku wyj�ciowego!");
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
