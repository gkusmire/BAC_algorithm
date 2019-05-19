import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class BAC_coder {
	
	public static byte[] code(AlphabetIntervals alphabetIntervals) {//TODO: ta metoda ma zakodowa� ci�g znak�w 
		byte[] s=alphabetIntervals.getFileContent();
		//TODO: tu wstawi� implementacj� alogrytmu kodowania
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
			byte[] output=code(alphabetIntervals);//wi�c tu te� kodujemy zawarto�� ca�ego pliku
			BufferedWriter writer = new BufferedWriter(new FileWriter(outFileName));
			writer.write(new String(output));
			writer.close();
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
