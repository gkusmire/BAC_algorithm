import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BAC_coder {
	
	public static int[] code(AlphabetIntervals alphabetIntervals) {//TODO: ta metoda ma zakodowaæ ci¹g znaków 
		int[] s=alphabetIntervals.getFileContent();
		int d=0,dm1=0;
		int g=255,gm1=255;
		int r=g-d+1,rm1=g-d+1;
		int ln=0;
		Double pia=0.0;
		Double pib=1.0;
		StringBuilder wyjscie=new StringBuilder();
		//Algorytm na slajdach jest s³abo zapisany, trudnop ustaliæ, co oznaczaj¹ symbole
		for(int i=1;i<s.length;i++)
		{
			r=gm1-dm1+1;//R = G - D + 1
			//absolutnie nie rozumiem, co tu ma oznaczaæ to N i k
			d=(int) (dm1+rm1*alphabetIntervals.getAlphabetElementInterval(s[i]).leftVal());//D = D + R · N[k-1]/N
			g=(int) (dm1+rm1*alphabetIntervals.getAlphabetElementInterval(s[i]).rightVal());//G = D + R · N[k]/N - 1
			
			rm1=r;
			dm1=d;
			gm1=g;
			
			if((d & 0x80000000) == (g & 0x80000000))
			{
				d<<=1;
				g<<=1;
				g+=1;
				wyjscie.append((d & 0x80000000)>>31);//o takie wys³anie na wyjscie chodzi?
				
				if(ln>0)
				{
					//wyœlij ln bitów z k¹d? do k¹d? (1-b) - co to niby ma znaczyæ?
					//chodzi o najstarsze bity d, czy g?
					//trzeba robiæ na d albo g jakieœ przesuniêcie, czy te wys³ane bity zostaj¹?
					//wyjscie.append(
					ln=0;
				}
			}
			
			//tutaj te¿ zupe³ny be³kot, czy chodzi o na³o¿enie maski bitowej AND?
			if((d & 0x40000000) == 1 && (g & 0x80000000) == 1)
			{
				ln=ln+1;
			}
		}
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
