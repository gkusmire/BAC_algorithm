import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;

public class BAC_coder {
	
	public static Integer[] code(AlphabetIntervals alphabetIntervals) throws ArithmeticException {//TODO: ta metoda ma zakodowa� ci�g znak�w
		int[] s=alphabetIntervals.getFileContent();

		// inicjalizacja
		// ustalamy pocz�tkowe granice przedzia�u - dla dost�pnych 2^m warto�ci po m 0 i 1 w zapisie dw�jkowym
		final int m = 30; // d�ugo�� s�owa
		// maksymalna warto�� - je�eli wybieramy sobie dowoln� d�ugo�� s�owa,
		// trzeba pami�ta� o zastosowaniu maski bitowej do wyniku przesuni�cia bitowego
		long _max = 0;
		for(int i = 0; i < m; i++)
			_max = (_max<<1) | 1;
		System.out.println((_max));
		final long MAXVAL = _max;
		final long half = 0b1 << (m-1); // w praktyce: maska dla najstarszego bitu s�owa
		final long quat = 0b1 << (m-2);// w praktyce: maska dla drugiego najstarszego bitu s�owa

		long d = 0; // ustalamy doln� granic� na (0...0)
		long g = MAXVAL; // ustalamy g�rn� granic� na (1...1)
		long ln=0; // licznik niedomiaru; Sayood: Scale3
		final long totalCount = s.length;

		BitStream wyjscie = new BitStream();

		// sprawdzenie zakresu typu liczbowego - UWAGA: r musi mie� o 1 bit wi�cej ni� d i g
		if((MAXVAL << 1) <= MAXVAL) throw(new ArithmeticException("Niewystarczaj�ca d�ugo�� typu liczbowego!"));
		if(totalCount > MAXVAL) throw(new ArithmeticException("Niewystarczaj�ca d�ugo�� typu liczbowego!"));

		for(int i=0;i<totalCount;i++)
		{
			long r = g - d + 1; // obliczamy szeroko�� przedzia�u

			// N(k) to suma liczby wyst�pie� symboli 1..k
			// N - ca�kowita liczba symboli w kodowanym ci�gu
			// pobranie kolejnego symbolu s[i]
			long old_d = d;
			Pair<Integer, Integer> ai = alphabetIntervals.getAlphabetElementInterval(s[i]);

			d = old_d + (r * ai.leftVal())/totalCount;//D = D + R � N[k-1]/N
			g = old_d + (r * ai.rightVal())/totalCount - 1;//G = D + R � N[k]/N - 1

            if(d > g) throw(new ArithmeticException("d>g! Za ma�a dok�adno�� numeryczna!"));

			// dop�ki warunek #1 lub warunek #2 spe�nione
			while(((d & half) == (g & half)) || ((d & half) < (g & half) && (d & quat) > (g & quat) )) {
				// warunek #1 - Je�li b <- MSB w d i g jest jednakowy:
				if ((d & half) == (g & half)) {
					int b = (d & half) > 0 ? 1 : 0; // r�wny MSB s��w, do wys�ania na wyj�cie
					// d - przesuni�cie w lewo o 1 i (implicite) uzupe�nienie zerem
					d = (d << 1) & MAXVAL;
					// g - przesuni�cie w lewo o 1 i uzupe�nienie jedynk�
					g = ((g << 1) | 1) & MAXVAL;
					//WYS�ANIE b
					wyjscie.put(b);
					// je�li licznik LN > 0, wy�lij LN bit�w (1 - b)
					while (ln > 0) {
						wyjscie.put(1 - b);
						ln--;
					}
				} else {
				// warunek #2 d=0b01... i g=0b10...
                    // przesu� w lewo bity obu rejestr�w z wyj�tkiem najbardziej znacz�cych, uzupe�nij rejestry
					// d w lewo i 0 na LSB
					d = ((d << 1) & (MAXVAL >> 1)) | (d & (half));
					// g w lewo i 1 na LSB
					g = (((g << 1) | 1) & (MAXVAL >> 1)) | (g & (half));
					ln++;
				}
            }
		}
		// Je�li nie ma wi�cej symboli zako�czenie: dopisz do wyj�cia wszystkie znacz�ce bity z d
		int sb = 1 << (m-1);
		System.out.println("KO�CZENIE KODOWANIE d="+d+", ln="+ln);
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
		if(d > 0) { // s� znacz�ce bity
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
		// i co teras? --- dos�a� zera do pe�nych bajt�w, czy te� koniecznie musi by� r�wnie� podzielne przez d�ugo�� s�owa?
        // s�owa --- nie ma b��d�w z eof
		while(wyjscie.getLength()%8 != 0 || wyjscie.getLength()%m != 0)
		{
			wyjscie.put(0);
		}
		System.out.println("KODER: wyprowadzono " + wyjscie.getLength() + " bit�w / " + wyjscie.asArray().length + " bajt�w. m="+m);
		return wyjscie.asArray();
	}
	
	/**
	 * Metoda odczyta plik wej�ciowy, zakoduje go i zapisze
	 * @param inFileName
	 * @param outFileName
	 */
	public static Integer[] codeFromFileToFile(String inFileName, String outFileName) {
		PGMFileReader fileReader;
		BACFileWriter fileWriter;
		Integer [] output ={};
		try {
			fileReader = new PGMFileReader(inFileName);
			AlphabetIntervals alphabetIntervals = new AlphabetIntervals(fileReader);//TODO: s�abo, �e budowanie struktury nie jest oddzielone od czytania pliku
			//alphabetIntervals.printAlphabetIntervals();//tu bierzemy zwarto�� ca�ego pliku
			output=code(alphabetIntervals);//wi�c tu te� kodujemy zawarto�� ca�ego pliku
			BACFileWriter.write(output,alphabetIntervals,new File(outFileName));

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("B��d odczytu danych z pliku!!!");
		}
		return output;
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
    		outFileName="encoded.txt";
    	codeFromFileToFile(inFileName,outFileName);
    }
}
