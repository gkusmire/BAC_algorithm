import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class BAC_decoder {
	
	public static String decode(String s) {
		return s;
	}
	
	public static void decodeFromFileToFile(String inFileName,String outFileName) {
		//Rozumiem, ¿e informacjê o szerokoœci i wysokoœci mamy zapisan¹ w zakodowanych danych
		//i statystyka te¿ zostanie z nich odtworzona
		File inFile = new File(inFileName); 
		StringBuilder sb=new StringBuilder(); 
		try {
			BufferedReader br = new BufferedReader(new FileReader(inFile)); 
			String st; 
			while ((st = br.readLine()) != null) 
				sb.append(st);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("B³¹d odczytu danych z pliku!!!");
		}
		String output=decode(sb.toString());
		int width=output.length();//TODO: pobraæ szerokoœæ
		int height=1;//TODO: pobraæ szerokoœæ
		int image[][]=new int[width][height];
		byte bytes[]=output.getBytes();
		for(int j=0;j<height;j++)
		{
			for(int i=0;i<width;i++)
			{
				image[i][j]=bytes[i+j*width];//TODO: sprawdziæ, czy obraz nie wyszed³ jakoœ obrócony
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
