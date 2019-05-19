import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BAC_decoder {
	
	public static String decode(String s) {
		return s;
	}
	
	public static void decodeFromFileToFile(String inFileName,String outFileName) {
		//Rozumiem, ¿e informacjê o szerokoœci i wysokoœci mamy zapisan¹ w zakodowanych danych
		//i statystyka te¿ zostanie z nich odtworzona
		File inFile = new File(inFileName); 
		StringBuilder sb=new StringBuilder(); 
		byte bytes[];
		try {
			bytes=Files.readAllBytes(Path.of(inFileName));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("B³¹d odczytu danych z pliku!!!");
			return;
		}
		int width=512;//TODO: pobraæ szerokoœæ, na razie ustalona jak w danych wejœciowych
		int height=512;//TODO: pobraæ szerokoœæ
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
    	decodeFromFileToFile(inFileName,outFileName);
	}

}
