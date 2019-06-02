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
		// dla wyjaœnienia algorytmu nale¿y zapoznaæ siê najpierw z algorytmem kodera
		// dzia³a na analogicznej zasadzie
		// d³ugoœæ oryginalnego ci¹gu
		long totalCount = fileReader.getWidth() * fileReader.getHeight();

		// inicjalizacja
		// ustalamy pocz¹tkowe granice przedzia³u - dla dostêpnych 2^m wartoœci po m 0 i 1 w zapisie dwójkowym
        // TODO powi¹zanie z m kodera - czy konieczne?
		final int m = 24; // d³ugoœæ s³owa
		// maksymalna wartoœæ - je¿eli wybieramy sobie dowoln¹ d³ugoœæ s³owa,
		// trzeba pamiêtaæ o zastosowaniu maski bitowej do wyniku przesuniêcia bitowego
		final long max = (int)Math.pow(2,m) - 1;
		final long half = 0b1 << (m-1);
		final long quat = 0b1 << (m-2);

		long d = 0;   // ustalamy doln¹ granicê na (0...0)
		long g = max; // ustalamy górn¹ granicê na (1...1)

		long t = 0b0; // dekodowane s³owo (?)

		List<Integer> wyjscie = new ArrayList<>();

		fileReader.rewind();
		// wczytanie m bitów z wejœcia do s³owa t
		// UWAGA: kolejnoœæ wczytywania bitów z/do rejestru: MSB->LSB
		for(int i = 0; i < m; i++) {
			t = (t<<1) | fileReader.get();
		}
		System.out.println("Pierwsze s³owo = "+t);

		int count = 0;

		while(!fileReader.eof() && count < totalCount) { // dopóki s¹ symbole
			int k = 0; // indeks dekodowanego symbolu
			long r = g - d + 1; // obliczamy szerokoœæ przedzia³u

			while(k < fileReader.getNumber() && Math.floor(((double)(t - d + 1) * totalCount - 1) / r) >= fileReader.getAlphabetElementInterval(fileReader.getNthSymbol(k)).rightVal()) // leftVal bo od pocz¹tku
				k++;
			k = Math.min(k,fileReader.getNumber()-1);
            if(k >= fileReader.getNumber()) throw new ArrayIndexOutOfBoundsException("Nie ma takiego symbolu!");
			// zdekoduj symbol x k-ty z linii prawdopodobieñstw
			int x = fileReader.getNthSymbol(k);
			wyjscie.add(x);
			count++;

			long old_d = d;
			Pair<Integer, Integer> elem = fileReader.getAlphabetElementInterval(x); // zakres wystêpowania symbolu x
			//d = old_d + (int)Math.floor((double)r * ((double)elem.leftVal() / (double)totalCount));
			//g = old_d + (int)Math.floor((double)r * ((double)elem.rightVal() / (double)totalCount)) - 1;
			d = old_d + (r * elem.leftVal())/totalCount;
			g = old_d + (r * elem.rightVal())/totalCount - 1;
			if(d > g) throw(new ArithmeticException("d>g! Za ma³a dok³adnoœæ numeryczna!"));

			// dopóki warunek #1 lub warunek #2 spe³nione
			while( ( (d & half) == (g & half) || (((d >> (m - 2)) & 0b11) == 0b01 && (((g >> (m - 2)) & 0b11) == 0b10))) ) {
				// warunek #1
				if ((d & half) == (g & half)) {
					long b = (d & half) >> (m - 1); // równy MSB s³ów, do wys³ania na wyjœcie
					// d - przesuniêcie w lewo o 1 i (implicite) uzupe³nienie zerem
					d = (d << 1) & max;
					// g - przesuniêcie w lewo o 1 i uzupe³nienie jedynk¹
					g = ((g << 1) | 1) & max;
					// wczytanie nastêpnego bitu ze strumienia w miejsce MSB
					int val = 0;
					if(!fileReader.eof()) val = fileReader.get();
					t  = ((t<<1) & max) + val;

				}
				// warunek #2
				else {
					// przesuñ w lewo bity obu rejestrów z wyj¹tkiem najbardziej
					// znacz¹cych i uzupe³nij rejestry d i g w lewo: d 0 na LSB, g 1 na LSB
                    // complement (new) MSB of g, d, t
					d = ((d << 1) & (max >> 1)) | (d & (half));
					g = (((g << 1) | 1) & (max >> 1)) | (g & (half));
					// j.w. i wczytaj nastêpny bit ze strumienia wejœciowego na LSB
					int val = 0;
					if(!fileReader.eof()) val = fileReader.get();
					t = (((t << 1) & (max>>1)) | (t & half)) + val;
				}
			}
		}
		System.out.println("ITERACJE "+count);

		return wyjscie.toArray(Integer[]::new);
	}
	
	public static void decodeFromFileToFile(String inFileName,String outFileName) {
		//Rozumiem, ¿e informacjê o szerokoœci i wysokoœci mamy zapisan¹ w zakodowanych danych
		//i statystyka te¿ zostanie z nich odtworzona
		File inFile = new File(inFileName);
		StringBuilder sb=new StringBuilder();
		BACFileReader fileReader;

		try {
			 fileReader = new BACFileReader("test.bac");
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("B³¹d odczytu danych z pliku!!!");
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
					throw(new IOException("Nieprawido³owa wartoœæ!"));
				array[i/fileReader.getWidth()][i%fileReader.getWidth()] = result[i];
			}

			PGMFileWriter outFile = new PGMFileWriter();
			outFile.write(array,new File(outFileName));

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("B³¹d odczytu danych z pliku!!!");
			return;
		}
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
