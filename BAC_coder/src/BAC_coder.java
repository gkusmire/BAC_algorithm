import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BAC_coder {
	
	public static int[] code(AlphabetIntervals alphabetIntervals) {//TODO: ta metoda ma zakodowa� ci�g znak�w 
		int[] s=alphabetIntervals.getFileContent();
		// inicjalizacja
		// ustalamy pocz�tkowe granice przedzia�u - dla dost�pnych 2^m warto�ci po m 0 i 1 w zapisie dw�jkowym
		// m=8    12345678
		int d = 0b00000000,dm1=0; // ustalamy doln� granic� na (0...0)
		int g = 0b11111111,gm1=255; // ustalamy g�rn� granic� na (1...1)
		int r=g-d+1,rm1=g-d+1;
		int ln=0; // licznik niedomiaru
		Double pia=0.0;
		Double pib=1.0;
		StringBuilder wyjscie=new StringBuilder();
		//Algorytm na slajdach jest s�abo zapisany, trudnop ustali�, co oznaczaj� symbole
		for(int i=1;i<s.length;i++)
		{
			//r=gm1-dm1+1;//R = G - D + 1
			r = g - d + 1; // obliczamy szeroko�� przedzia�u

			// N(k) to suma liczby wyst�pie� symboli 1..k
			// N - ca�kowita liczba symboli w kodowanym ci�gu
			int old_d = d;
			d = Math.floor(old_d + r * alphabetIntervals.getAlphabetElementInterval(s[i].left));//D = D + R � N[k-1]/N
			g = Math.floor(old_d + r * alphabetIntervals.getAlphabetElementInterval(s[i].right) - 1);//G = D + R � N[k]/N - 1

			rm1=r;
			dm1=d;
			gm1=g;

			// warunek #1 - Je�li najstarszy bit b w D i G jest jednakowy:
			if((d & 0x80000000) == (g & 0x80000000))
			{
				d<<=1;
				g<<=1;
				g+=1;
				wyjscie.append((d & 0x80000000)>>31);//o takie wys�anie na wyjscie chodzi?
				
				if(ln>0)
				{
					//wy�lij ln bit�w z k�d? do k�d? (1-b) - co to niby ma znaczy�?
					//chodzi o najstarsze bity d, czy g?
					//trzeba robi� na d albo g jakie� przesuni�cie, czy te wys�ane bity zostaj�?
					//wyjscie.append(
					ln=0;
				}
			}
			// warunek #2 - Je�li D = 0x01... a G = 0x10...:
			//tutaj te� zupe�ny be�kot, czy chodzi o na�o�enie maski bitowej AND?
			if((d & 0x40000000) == 1 && (g & 0x80000000) == 1)
			{
				ln=ln+1;
			}
		}
		// Je�li nie ma wi�cej symboli zako�czenie: dopisz do wyj�cia wszystkie znacz�ce bity z d
		// TODO --,,--
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
