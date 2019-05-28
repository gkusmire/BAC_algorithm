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
		final int m = 20; // d³ugoœæ s³owa
		// maksymalna wartoœæ - je¿eli wybieramy sobie dowoln¹ d³ugoœæ s³owa,
		// trzeba pamiêtaæ o zastosowaniu maski bitowej do wyniku przesuniêcia bitowego
		final int MAXVAL = (int)Math.pow(2,m) - 1;
		final int half = 0b1 << (m-1); // w praktyce: maska dla najstarszego bitu s³owa
		final int quat = 0b1 << (m-2);// w praktyce: maska dla drugiego najstarszego bitu s³owa

		int d = 0; // ustalamy doln¹ granicê na (0...0)
		int g = MAXVAL; // ustalamy górn¹ granicê na (1...1)
		int ln=0; // licznik niedomiaru
		final int totalCount = s.length;

		BitStream wyjscie = new BitStream();

		for(int i=1;i<s.length;i++)
		{
			int r = g - d + 1; // obliczamy szerokoœæ przedzia³u

			// N(k) to suma liczby wyst¹pieñ symboli 1..k
			// N - ca³kowita liczba symboli w kodowanym ci¹gu
			// pobranie kolejnego symbolu s[i]
			int old_d = d;
			Pair<Integer, Integer> ai = alphabetIntervals.getAlphabetElementInterval(s[i]);

			d = old_d + (int)Math.floor((double)r * (double)ai.leftVal()/(double)totalCount);//D = D + R · N[k-1]/N
			g = old_d + (int)Math.floor((double)r * (double)ai.rightVal()/(double)totalCount) - 1;//G = D + R · N[k]/N - 1

            if(d > g) throw(new ArithmeticException("d>g! Za ma³a dok³adnoœæ numeryczna!"));

			// dopóki warunek #1 lub warunek #2 spe³nione
			while(((d & half) == (g & half)) || ((d & half) < (g & half) && (d & quat) > (g & quat) )) {
				// warunek #1 - Jeœli b <- MSB w d i g jest jednakowy:
				if ((d & half) == (g & half)) {
					int b = (d & half) >> (m - 1); // równy MSB s³ów, do wys³ania na wyjœcie
					// d - przesuniêcie w lewo o 1 i (implicite) uzupe³nienie zerem
					d = (d << 1) & MAXVAL;
					// g - przesuniêcie w lewo o 1 i uzupe³nienie jedynk¹
					g = ((g << 1) | 1) & MAXVAL;
					//WYS£ANIE b
					wyjscie.put(b);
					// jeœli licznik LN > 0, wyœlij LN bitów (1 - b ); LN = 0, --- tj. (1 - b) jako realizacja negacji jednobitowej wartoœci
					// Sayood: while(Scale3 > 0)
					while (ln > 0) {
						wyjscie.put(1 - b);
						ln--;
					}
				}

                // warunek #2
				// wyk³ad: Jeœli D = 0x01... a G = 0x10...: --- rozumiem, ¿e jest to zapis w systemie binarnym (!)
				// Sayood: warunek E_3 tj. nastêpuj¹ce mapowanie zwiêkszaj¹ce dwukrotnie szerokoœæ przedzia³u:
				// [0.25, 0.75) -> [0,1), E_3(x) = 2(x - 0.25)
				// trzeba to prze³o¿yæ na implementacjê binarn¹ ca³kowitoliczbow¹
				if ((d & half) < (g & half) && (d & quat) > (g & quat) ) {
					//przesuñ w lewo bity obu rejestrów z wyj¹tkiem najbardziej
					//znacz¹cych i uzupe³nij rejestry; LN = LN + 1
					// d w lewo i 0 na LSB
					// czy tak wygl¹da "przesuniêcie w lewo z wyj¹tkiem MSB"?
					// complement (new) MSB of d and g --- czyli zgadza siê z wyk³adem
					// realizacja tutaj: przesuniêcie w lewo ale zamaskowanie (nowego) MSB i alternatywa ze starym MSB
					d = ((d << 1) & (MAXVAL >> 1)) | (d & (half));
					// g w lewo i 1 na LSB
					g = (((g << 1) | 1) & (MAXVAL >> 1)) | (g & (half));

					ln++;
				}
            }
		}
		// Jeœli nie ma wiêcej symboli zakoñczenie: dopisz do wyjœcia wszystkie znacz¹ce bity z d
		int sb = 1 << (m-1);
		if(d > 0) { // s¹ znacz¹ce bity
			while ((sb & d) == 0)
				sb >>= 1;
			while (sb > 0) {
				wyjscie.put((sb & d) > 0 ? 1 : 0);
				sb >>= 1;
			}
		}
		// i co teras? --- dos³aæ zera do pe³nych bajtów?
		for(int i = 0;i<wyjscie.getLength() % 8; ++i)
			wyjscie.put(0);
		System.out.println("Wyprowadzono " + wyjscie.getLength() + " bitów / " + wyjscie.asArray().length + " bajtów.");
		return wyjscie.asArray();
	}
	
	/**
	 * Metoda odczyta plik wejœciowy, zakoduje go i zapisze
	 * @param fileReader
	 * @param outFileName
	 */
	public static Integer[] codeFromFileToFile(String inFileName, String outFileName) {
		PGMFileReader fileReader;
		BACFileWriter fileWriter;
		Integer [] output ={};
		try {
			fileReader = new PGMFileReader(inFileName);
			fileWriter = new BACFileWriter();
			AlphabetIntervals alphabetIntervals = new AlphabetIntervals(fileReader);//TODO: s³abo, ¿e budowanie struktury nie jest oddzielone od czytania pliku
			alphabetIntervals.printAlphabetIntervals();//tu bierzemy zwartoœæ ca³ego pliku
			output=code(alphabetIntervals);//wiêc tu te¿ kodujemy zawartoœæ ca³ego pliku
			FileOutputStream fos = new FileOutputStream(outFileName);
			byte[] ca=new byte[output.length];
			for(int i=0;i<output.length;i++)
			{
				ca[i]=(byte)output[i].intValue();
				if(output[i]<0 || output[i]>255)
					System.err.println("output[i]="+output[i]);
				if((ca[i]&0xFF) != output[i]) {
					System.err.println("ca[i]&0xFF="+(ca[i]&0xFF)+" output[i]="+output[i]);
				}
			}
			fileWriter.write(output,alphabetIntervals,new File("test.bac"));
			fos.write(ca,0,ca.length);
			fos.flush();
			fos.close();
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
