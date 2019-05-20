import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;

public class BAC_coder {
	
	public static int[] code(AlphabetIntervals alphabetIntervals) {//TODO: ta metoda ma zakodowa� ci�g znak�w 
		int[] s=alphabetIntervals.getFileContent();

		// inicjalizacja
		// ustalamy pocz�tkowe granice przedzia�u - dla dost�pnych 2^m warto�ci po m 0 i 1 w zapisie dw�jkowym
		final int m = 8; // d�ugo�� s�owa
		// maksymalna warto�� - je�eli wybieramy sobie dowoln� d�ugo�� s�owa,
		// trzeba pami�ta� o zastosowaniu maski bitowej do wyniku przesuni�cia bitowego
		final int max = (int)Math.pow(2,m) - 1;
		final int half = 0b1 << (m-1); // w praktyce: maska dla najstarszego bitu s�owa
		final int quat = 0b1 << (m-2);// w praktyce: maska dla drugiego najstarszego bitu s�owa

		int d = 0,dm1=0; // ustalamy doln� granic� na (0...0)
		int g = max, gm1=255; // ustalamy g�rn� granic� na (1...1)
		int ln=0; // licznik niedomiaru

		Double pia=0.0;
		Double pib=1.0;

		// PLACEHOLDER wyjscie.append(wartosc_bitu) --- wypisywanie wyj�cia
		StringBuilder wyjscie=new StringBuilder();

		for(int i=1;i<s.length;i++)
		{
			//r=gm1-dm1+1;//R = G - D + 1
			int r = g - d + 1; // obliczamy szeroko�� przedzia�u

			// N(k) to suma liczby wyst�pie� symboli 1..k
			// N - ca�kowita liczba symboli w kodowanym ci�gu
			// pobranie kolejnego symbolu s[i]
			int old_d = d;
			d = (int)Math.floor((double)old_d + (double)r * alphabetIntervals.getAlphabetElementInterval(s[i]).leftVal());//D = D + R � N[k-1]/N
			g = (int)Math.floor((double)old_d + (double)r * alphabetIntervals.getAlphabetElementInterval(s[i]).rightVal() - 1);//G = D + R � N[k]/N - 1

			// dop�ki warunek #1 lub warunek #2 spe�nione
			while((d & half) == (g & half) || ((d >> (m - 2)) == 0b01 && ((g >> (m - 2)) == 0b01))) {
				// warunek #1 - Je�li b <- MSB w d i g jest jednakowy:
				if ((d & half) == (g & half)) {
					int b = (d & half) >> (m - 1); // r�wny MSB s��w, do wys�ania na wyj�cie
					// d - przesuni�cie w lewo o 1 i (implicite) uzupe�nienie zerem
					d = (d << 1) & max;
					// g - przesuni�cie w lewo o 1 i uzupe�nienie jedynk�
					g = ((g << 1) | 1) & max;
					//WYS�ANIE b
					wyjscie.append(b);
					// je�li licznik LN > 0, wy�lij LN bit�w (1 ? b ); LN = 0, --- tj. (1 - b) jako realizacja negacji jednobitowej warto�ci
					// Sayood: while(Scale3 > 0)
					while (ln > 0) {
						wyjscie.append(1 - b);
						ln--;
					}

				}
				// warunek #2
				// wyk�ad: Je�li D = 0x01... a G = 0x10...: --- rozumiem, �e jest to zapis w systemie binarnym (!)
				// Sayood: warunek E_3 tj. nast�puj�ce mapowanie zwi�kszaj�ce dwukrotnie szeroko�� przedzia�u:
				// [0.25, 0.75) -> [0,1), E_3(x) = 2(x - 0.25)
				// trzeba to prze�o�y� na implementacj� binarn� ca�kowitoliczbow�
				if ((d >> (m - 2)) == 0b01 && ((g >> (m - 2)) == 0b10)) {
					//przesu� w lewo bity obu rejestr�w z wyj�tkiem najbardziej
					//znacz�cych i uzupe�nij rejestry; LN = LN + 1
					// d w lewo i 0 na LSB
					// czy tak wygl�da "przesuni�cie w lewo z wyj�tkiem MSB"?
					// complement (new) MSB of d and g --- czyli zgadza si� z wyk�adem
					// realizacja tutaj: przesuni�cie w lewo ale zamaskowanie (nowego) MSB i alternatywa ze starym MSB
					d = ((d << 1) & (max >> 1)) | (d & (half));
					// g w lewo i 1 na LSB
					g = (((g << 1) | 1) & (max >> 1)) | (d & (half));

					ln++;
				}
			}
		}
		// Je�li nie ma wi�cej symboli zako�czenie: dopisz do wyj�cia wszystkie znacz�ce bity z d
		int sb = 1 << (m-1);
		if(d > 0) { // s� znacz�ce bity
			while ((sb & d) == 0)
				sb >>= 1;
			while (sb > 0) {
				wyjscie.append((sb & d) > 0 ? 1 : 0);
				sb >>= 1;
			}
		}
		// i co teras? --- dos�a� zera do pe�nych bajt�w?
		System.out.println(wyjscie.length()+": "+wyjscie);
		return s;
	}
	
	/**
	 * Metoda odczyta plik wej�ciowy, zakoduje go i zapisze
	 * @param fileReader
	 * @param outFileName
	 */
	public static void codeFromFileToFile(String inFileName, String outFileName) {
		PGMFileReader fileReader;
		try {
			fileReader = new PGMFileReader(inFileName);
			AlphabetIntervals alphabetIntervals = new AlphabetIntervals(fileReader);//TODO: s�abo, �e budowanie struktury nie jest oddzielone od czytania pliku
			alphabetIntervals.printAlphabetIntervals();//tu bierzemy zwarto�� ca�ego pliku
			int[] output=code(alphabetIntervals);//wi�c tu te� kodujemy zawarto�� ca�ego pliku
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
			System.err.println("B��d odczytu danych z pliku!!!");
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
    		outFileName="encoded.txt";
    	codeFromFileToFile(inFileName,outFileName);
    }
}
