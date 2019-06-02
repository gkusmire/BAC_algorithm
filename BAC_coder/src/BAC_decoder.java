import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BAC_decoder {
	
	public static Integer[] decode(BACFileReader fileReader) throws IOException,ArithmeticException{
		// dla wyja�nienia algorytmu nale�y zapozna� si� najpierw z algorytmem kodera
		// dzia�a na analogicznej zasadzie
		// d�ugo�� oryginalnego ci�gu
		int totalCount = fileReader.getWidth() * fileReader.getHeight();

		// inicjalizacja
		// ustalamy pocz�tkowe granice przedzia�u - dla dost�pnych 2^m warto�ci po m 0 i 1 w zapisie dw�jkowym
        // TODO powi�zanie z m kodera - czy konieczne?
		final int m = 24; // d�ugo�� s�owa
		// maksymalna warto�� - je�eli wybieramy sobie dowoln� d�ugo�� s�owa,
		// trzeba pami�ta� o zastosowaniu maski bitowej do wyniku przesuni�cia bitowego
		final int max = (int)Math.pow(2,m) - 1;
		final int half = 0b1 << (m-1);
		final int quat = 0b1 << (m-2);

		int d = 0;   // ustalamy doln� granic� na (0...0)
		int g = max; // ustalamy g�rn� granic� na (1...1)

		int t = 0b0; // dekodowane s�owo (?)

		List<Integer> wyjscie = new ArrayList<>();

		fileReader.rewind();
		// wczytanie m bit�w z wej�cia do s�owa t
		// UWAGA: kolejno�� wczytywania bit�w z/do rejestru: MSB->LSB
		for(int i = 0; i < m; i++) {
			t = (t<<1) | fileReader.get();
		}
		System.out.println("Pierwsze s�owo = "+t);

		int count = 0;

		while(count < totalCount) { // dop�ki s� symbole
			int k = 0; // indeks dekodowanego symbolu
			long r = g - d + 1; // obliczamy szeroko�� przedzia�u

			while((int) Math.floor(((float)(t - d + 1) * (float)totalCount - 1) / r) >= fileReader.getAlphabetElementInterval(fileReader.getNthSymbol(k)).rightVal()) // leftVal bo od pocz�tku
				k++;
            if(k >= fileReader.getNumber()) throw new ArrayIndexOutOfBoundsException("Nie ma takiego symbolu!");
			// zdekoduj symbol x k-ty z linii prawdopodobie�stw
			int x = fileReader.getNthSymbol(k);
			wyjscie.add(x);
			count++;

			int old_d = d;
			Pair<Integer, Integer> elem = fileReader.getAlphabetElementInterval(x); // zakres wyst�powania symbolu x
			//d = old_d + (int)Math.floor((double)r * ((double)elem.leftVal() / (double)totalCount));
			//g = old_d + (int)Math.floor((double)r * ((double)elem.rightVal() / (double)totalCount)) - 1;
			d = old_d + (int)((r * elem.leftVal())/totalCount);
			g = old_d + (int)((r * elem.rightVal())/totalCount) - 1;
			if(d > g) throw(new ArithmeticException("d>g! Za ma�a dok�adno�� numeryczna!"));

			// dop�ki warunek #1 lub warunek #2 spe�nione
			while( ( (d & half) == (g & half) || (((d >> (m - 2)) & 0b11) == 0b01 && (((g >> (m - 2)) & 0b11) == 0b10))) ) {
				// warunek #1
				if ((d & half) == (g & half)) {
					int b = (d & half) >> (m - 1); // r�wny MSB s��w, do wys�ania na wyj�cie
					// d - przesuni�cie w lewo o 1 i (implicite) uzupe�nienie zerem
					d = (d << 1) & max;
					// g - przesuni�cie w lewo o 1 i uzupe�nienie jedynk�
					g = ((g << 1) | 1) & max;
					// wczytanie nast�pnego bitu ze strumienia w miejsce MSB
					t  = ((t<<1) & max) + fileReader.get();

				}
				// warunek #2
				else {
					// przesu� w lewo bity obu rejestr�w z wyj�tkiem najbardziej
					// znacz�cych i uzupe�nij rejestry d i g w lewo: d 0 na LSB, g 1 na LSB
                    // complement (new) MSB of g, d, t
					d = ((d << 1) & (max >> 1)) | (d & (half));
					g = (((g << 1) | 1) & (max >> 1)) | (g & (half));
					// j.w. i wczytaj nast�pny bit ze strumienia wej�ciowego na LSB
					t = (((t << 1) & (max>>1)) | (t & half)) + fileReader.get();
				}
			}
		}
		System.out.println("ITERACJE "+count);

		return wyjscie.toArray(Integer[]::new);
	}
	
	public static void decodeFromFileToFile(String inFileName,String outFileName) {
		//Rozumiem, �e informacj� o szeroko�ci i wysoko�ci mamy zapisan� w zakodowanych danych
		//i statystyka te� zostanie z nich odtworzona
		File inFile = new File(inFileName);
		StringBuilder sb=new StringBuilder();
		BACFileReader fileReader;

		try {
			 fileReader = new BACFileReader("test.bac");
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("B��d odczytu danych z pliku!!!");
			return;
		}
		int width=fileReader.getWidth();
		int height=fileReader.getHeight();
		System.out.println("Odczytane wymiary: " + width + "x"+height);

		try {
			Integer[] result = decode(fileReader);
			System.out.println("ZDEKODOWANE " + result.length + ": " + result);

			int[][] array = new int[fileReader.getWidth()][fileReader.getHeight()];
			System.out.println("test dekodowania "+result.length);

			for(int i=0; i<result.length; i++){
				if(result[i] < 0 || result[i] > 255)
					throw(new IOException("Nieprawido�owa warto��!"));
				array[i/fileReader.getWidth()][i%fileReader.getWidth()] = result[i];
			}

			PGMFileWriter outFile = new PGMFileWriter();
			outFile.write(array,new File(outFileName));

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("B��d odczytu danych z pliku!!!");
			return;
		}
		// TODO zapis zdekodowanego ci�gu do pliku
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
			System.err.println("B��d zapisu danych do pliku!!!");
		}
		*/
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
