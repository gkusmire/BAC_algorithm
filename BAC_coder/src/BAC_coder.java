import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;

public class BAC_coder {
	
	public static int[] code(AlphabetIntervals alphabetIntervals) {//TODO: ta metoda ma zakodowaæ ci¹g znaków 
		int[] s=alphabetIntervals.getFileContent();

		// inicjalizacja
		// ustalamy pocz¹tkowe granice przedzia³u - dla dostêpnych 2^m wartoœci po m 0 i 1 w zapisie dwójkowym
		final int m = 8; // d³ugoœæ s³owa
		// maksymalna wartoœæ - je¿eli wybieramy sobie dowoln¹ d³ugoœæ s³owa,
		// trzeba pamiêtaæ o zastosowaniu maski bitowej do wyniku przesuniêcia bitowego
		final int max = (int)Math.pow(2,m) - 1;
		final int half = 0b1 << (m-1); // w praktyce: maska dla najstarszego bitu s³owa
		final int quat = 0b1 << (m-2);// w praktyce: maska dla drugiego najstarszego bitu s³owa

		int d = 0,dm1=0; // ustalamy doln¹ granicê na (0...0)
		int g = max, gm1=255; // ustalamy górn¹ granicê na (1...1)
		int ln=0; // licznik niedomiaru

		Double pia=0.0;
		Double pib=1.0;

		// PLACEHOLDER wyjscie.append(wartosc_bitu) --- wypisywanie wyjœcia
		StringBuilder wyjscie=new StringBuilder();

		for(int i=1;i<s.length;i++)
		{
			//r=gm1-dm1+1;//R = G - D + 1
			int r = g - d + 1; // obliczamy szerokoœæ przedzia³u

			// N(k) to suma liczby wyst¹pieñ symboli 1..k
			// N - ca³kowita liczba symboli w kodowanym ci¹gu
			// pobranie kolejnego symbolu s[i]
			int old_d = d;
			d = (int)Math.floor((double)old_d + (double)r * alphabetIntervals.getAlphabetElementInterval(s[i]).leftVal());//D = D + R · N[k-1]/N
			g = (int)Math.floor((double)old_d + (double)r * alphabetIntervals.getAlphabetElementInterval(s[i]).rightVal() - 1);//G = D + R · N[k]/N - 1

			// dopóki warunek #1 lub warunek #2 spe³nione
			while((d & half) == (g & half) || ((d >> (m - 2)) == 0b01 && ((g >> (m - 2)) == 0b01))) {
				// warunek #1 - Jeœli b <- MSB w d i g jest jednakowy:
				if ((d & half) == (g & half)) {
					int b = (d & half) >> (m - 1); // równy MSB s³ów, do wys³ania na wyjœcie
					// d - przesuniêcie w lewo o 1 i (implicite) uzupe³nienie zerem
					d = (d << 1) & max;
					// g - przesuniêcie w lewo o 1 i uzupe³nienie jedynk¹
					g = ((g << 1) | 1) & max;
					//WYS£ANIE b
					wyjscie.append(b);
					// jeœli licznik LN > 0, wyœlij LN bitów (1 ? b ); LN = 0, --- tj. (1 - b) jako realizacja negacji jednobitowej wartoœci
					// Sayood: while(Scale3 > 0)
					while (ln > 0) {
						wyjscie.append(1 - b);
						ln--;
					}

				}
				// warunek #2
				// wyk³ad: Jeœli D = 0x01... a G = 0x10...: --- rozumiem, ¿e jest to zapis w systemie binarnym (!)
				// Sayood: warunek E_3 tj. nastêpuj¹ce mapowanie zwiêkszaj¹ce dwukrotnie szerokoœæ przedzia³u:
				// [0.25, 0.75) -> [0,1), E_3(x) = 2(x - 0.25)
				// trzeba to prze³o¿yæ na implementacjê binarn¹ ca³kowitoliczbow¹
				if ((d >> (m - 2)) == 0b01 && ((g >> (m - 2)) == 0b10)) {
					//przesuñ w lewo bity obu rejestrów z wyj¹tkiem najbardziej
					//znacz¹cych i uzupe³nij rejestry; LN = LN + 1
					// d w lewo i 0 na LSB
					// czy tak wygl¹da "przesuniêcie w lewo z wyj¹tkiem MSB"?
					// complement (new) MSB of d and g --- czyli zgadza siê z wyk³adem
					// realizacja tutaj: przesuniêcie w lewo ale zamaskowanie (nowego) MSB i alternatywa ze starym MSB
					d = ((d << 1) & (max >> 1)) | (d & (half));
					// g w lewo i 1 na LSB
					g = (((g << 1) | 1) & (max >> 1)) | (d & (half));

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
				wyjscie.append((sb & d) > 0 ? 1 : 0);
				sb >>= 1;
			}
		}
		// i co teras? --- dos³aæ zera do pe³nych bajtów?
		System.out.println(wyjscie.length()+": "+wyjscie);
		return s;
	}
	
	/**
	 * Metoda odczyta plik wejœciowy, zakoduje go i zapisze
	 * @param fileReader
	 * @param outFileName
	 */
	public static void codeFromFileToFile(String inFileName, String outFileName) {
		PGMFileReader fileReader;
		try {
			fileReader = new PGMFileReader(inFileName);
			AlphabetIntervals alphabetIntervals = new AlphabetIntervals(fileReader);//TODO: s³abo, ¿e budowanie struktury nie jest oddzielone od czytania pliku
			alphabetIntervals.printAlphabetIntervals();//tu bierzemy zwartoœæ ca³ego pliku
			int[] output=code(alphabetIntervals);//wiêc tu te¿ kodujemy zawartoœæ ca³ego pliku
			FileOutputStream fos = new FileOutputStream(outFileName);
			byte[] ca=new byte[output.length];
			for(int i=0;i<output.length;i++)
			{
				ca[i]=(byte)output[i];
				if(output[i]<0 || output[i]>255)
					System.err.println("output[i]="+output[i]);
				if((ca[i]&0xFF) != output[i]) {
					System.err.println("ca[i]&0xFF="+(ca[i]&0xFF)+" output[i]="+output[i]);
				}
			}
			fos.write(ca,0,ca.length);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("B³¹d odczytu danych z pliku!!!");
		}
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
