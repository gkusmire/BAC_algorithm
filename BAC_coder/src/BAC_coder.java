import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;

public class BAC_coder {
	
	public static Integer[] code(AlphabetIntervals alphabetIntervals) throws ArithmeticException {//TODO: ta metoda ma zakodowaæ ci¹g znaków
		int[] s=alphabetIntervals.getFileContent();

		// inicjalizacja
		// ustalamy pocz¹tkowe granice przedzia³u - dla dostêpnych 2^m wartoœci po m 0 i 1 w zapisie dwójkowym
		final int m = 30; // d³ugoœæ s³owa
		// maksymalna wartoœæ - je¿eli wybieramy sobie dowoln¹ d³ugoœæ s³owa,
		// trzeba pamiêtaæ o zastosowaniu maski bitowej do wyniku przesuniêcia bitowego
		long _max = 0;
		for(int i = 0; i < m; i++)
			_max = (_max<<1) | 1;
		System.out.println((_max));
		final long MAXVAL = _max;
		final long half = 0b1 << (m-1); // w praktyce: maska dla najstarszego bitu s³owa
		final long quat = 0b1 << (m-2);// w praktyce: maska dla drugiego najstarszego bitu s³owa

		long d = 0; // ustalamy doln¹ granicê na (0...0)
		long g = MAXVAL; // ustalamy górn¹ granicê na (1...1)
		long ln=0; // licznik niedomiaru; Sayood: Scale3
		final long totalCount = s.length;

		BitStream wyjscie = new BitStream();

		// sprawdzenie zakresu typu liczbowego - UWAGA: r musi mieæ o 1 bit wiêcej ni¿ d i g
		if((MAXVAL << 1) <= MAXVAL) throw(new ArithmeticException("Niewystarczaj¹ca d³ugoœæ typu liczbowego!"));
		if(totalCount > MAXVAL) throw(new ArithmeticException("Niewystarczaj¹ca d³ugoœæ typu liczbowego!"));

		for(int i=0;i<totalCount;i++)
		{
			long r = g - d + 1; // obliczamy szerokoœæ przedzia³u

			// N(k) to suma liczby wyst¹pieñ symboli 1..k
			// N - ca³kowita liczba symboli w kodowanym ci¹gu
			// pobranie kolejnego symbolu s[i]
			long old_d = d;
			Pair<Integer, Integer> ai = alphabetIntervals.getAlphabetElementInterval(s[i]);

			d = old_d + (r * ai.leftVal())/totalCount;//D = D + R · N[k-1]/N
			g = old_d + (r * ai.rightVal())/totalCount - 1;//G = D + R · N[k]/N - 1

            if(d > g) throw(new ArithmeticException("d>g! Za ma³a dok³adnoœæ numeryczna!"));

			// dopóki warunek #1 lub warunek #2 spe³nione
			while(((d & half) == (g & half)) || ((d & half) < (g & half) && (d & quat) > (g & quat) )) {
				// warunek #1 - Jeœli b <- MSB w d i g jest jednakowy:
				if ((d & half) == (g & half)) {
					int b = (d & half) > 0 ? 1 : 0; // równy MSB s³ów, do wys³ania na wyjœcie
					// d - przesuniêcie w lewo o 1 i (implicite) uzupe³nienie zerem
					d = (d << 1) & MAXVAL;
					// g - przesuniêcie w lewo o 1 i uzupe³nienie jedynk¹
					g = ((g << 1) | 1) & MAXVAL;
					//WYS£ANIE b
					wyjscie.put(b);
					// jeœli licznik LN > 0, wyœlij LN bitów (1 - b)
					while (ln > 0) {
						wyjscie.put(1 - b);
						ln--;
					}
				} else {
				// warunek #2 d=0b01... i g=0b10...
                    // przesuñ w lewo bity obu rejestrów z wyj¹tkiem najbardziej znacz¹cych, uzupe³nij rejestry
					// d w lewo i 0 na LSB
					d = ((d << 1) & (MAXVAL >> 1)) | (d & (half));
					// g w lewo i 1 na LSB
					g = (((g << 1) | 1) & (MAXVAL >> 1)) | (g & (half));
					ln++;
				}
            }
		}
		// Jeœli nie ma wiêcej symboli zakoñczenie: dopisz do wyjœcia wszystkie znacz¹ce bity z d
		int sb = 1 << (m-1);
		System.out.println("KOÑCZENIE KODOWANIE d="+d+", ln="+ln);
		// wyprowadzanie ln

		int i = 0;
		for(;i<ln;i++) {
			wyjscie.put((d&half) > 0 ? 1 : 0);
			d<<=1;
		}
		while(ln>0) {
			wyjscie.put(1);
			ln--;
		}
		for(;i<m;i++) {
			wyjscie.put((d&half) > 0 ? 1 : 0);
			d<<=1;
		}
		/*
		while (ln > 0) {
			int val = 1 - ((d&half) > 0 ? 1 : 0);
			System.out.print(val);
			wyjscie.put(val);
			ln--;
		}
		/*
		if(ln>0) {
			while ((sb & ln) == 0 && sb>0)
				sb >>= 1;
			while(sb > 0) {
				int val = (sb & ln) > 0 ? 1 : 0;
				System.out.print(val);
				wyjscie.put(val);
				sb >>= 1;
			}
		}

		System.out.println("; ");
		sb = 1 << (m-1);
		// wyprowadzanie d
		if(d > 0) { // s¹ znacz¹ce bity
			//while ((sb & d) == 0)
			//	sb >>= 1;
			while (sb > 0) {
				int val = (sb & d) > 0 ? 1 : 0;
				System.out.print(val);
				wyjscie.put(val);
				sb >>= 1;
			}
		} else wyjscie.put(0);
		System.out.println(" ");
*/
		// i co teras? --- dos³aæ zera do pe³nych bajtów, czy te¿ koniecznie musi byæ równie¿ podzielne przez d³ugoœæ s³owa?
        // s³owa --- nie ma b³êdów z eof
		while(wyjscie.getLength()%8 != 0 || wyjscie.getLength()%m != 0)
		{
			wyjscie.put(0);
		}
		System.out.println("KODER: wyprowadzono " + wyjscie.getLength() + " bitów / " + wyjscie.asArray().length + " bajtów. m="+m);
		return wyjscie.asArray();
	}
	
	/**
	 * Metoda odczyta plik wejœciowy, zakoduje go i zapisze
	 * @param inFileName
	 * @param outFileName
	 */
	public static Integer[] codeFromFileToFile(String inFileName, String outFileName) {
		PGMFileReader fileReader;
		BACFileWriter fileWriter;
		Integer [] output ={};
		try {
			fileReader = new PGMFileReader(inFileName);
			AlphabetIntervals alphabetIntervals = new AlphabetIntervals(fileReader);//TODO: s³abo, ¿e budowanie struktury nie jest oddzielone od czytania pliku
			//alphabetIntervals.printAlphabetIntervals();//tu bierzemy zwartoœæ ca³ego pliku
			output=code(alphabetIntervals);//wiêc tu te¿ kodujemy zawartoœæ ca³ego pliku
			BACFileWriter.write(output,alphabetIntervals,new File(outFileName));

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("B³¹d odczytu danych z pliku!!!");
		}
		return output;
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
    		outFileName="encoded.txt";
    	codeFromFileToFile(inFileName,outFileName);
    }
}
