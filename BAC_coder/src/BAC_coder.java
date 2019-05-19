import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class BAC_coder {
	
	public static byte[] code(AlphabetIntervals alphabetIntervals) {//TODO: ta metoda ma zakodowaæ ci¹g znaków 
		byte[] s=alphabetIntervals.getFileContent();
		//TODO: tu wstawiæ implementacjê alogrytmu kodowania
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
			byte[] output=code(alphabetIntervals);//wiêc tu te¿ kodujemy zawartoœæ ca³ego pliku
			BufferedWriter writer = new BufferedWriter(new FileWriter(outFileName));
			writer.write(new String(output));
			writer.close();
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
