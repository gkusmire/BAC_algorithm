import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;


public class AlphabetIntervals implements Serializable {//TODO: mo¿e ta klasa powina siê inaczej nazywaæ, bo jest pojemnikiem na strukturê
	
	private final int[] fileContent;
	
	public static final int width=512;
	public static final int length=512;

    public AlphabetIntervals(PGMFileReader fileReader) throws IOException {
    	fileContent=new int[width*length];
        setAlphabetIntervals(fileReader);
    }

    public Pair<Double, Double> getAlphabetElementInterval(Integer elem) {

        return alphabetIntervals.get(elem);
    }

    public Double getAlphabetElementFreq(Integer elem) {

        return alphabetIntervals.get(elem).rightVal() - alphabetIntervals.get(elem).leftVal();
    }

    public void printAlphabetIntervals() {

        System.out.println("AlphabetIntervals:");
        for(Integer i : alphabetIntervals.keySet()) {

            System.out.println(i + " <"+alphabetIntervals.get(i).leftVal()+";"+alphabetIntervals.get(i).rightVal()+">");
        }
        System.out.println("End of alphabet");
    }

    private void setAlphabetIntervals(PGMFileReader fileReader) throws IOException {

        HistogramCounter counter = new HistogramCounter();

        fillInHistogramCounter(counter, fileReader);
        setIntervalsBasedOnHistogramCounter(counter);
    }

    private void setIntervalsBasedOnHistogramCounter(HistogramCounter counter) {

        double allElementsNumber = counter.getAllElementsNumber();
        double beginningOfInterval = 0.0;
        double elementInterval;
        double endOfInterval;

        alphabetIntervals = counter.getHistogramValues();

        for(int str : alphabetIntervals.keySet()) {

            Pair<Double, Double> pair = alphabetIntervals.get(str);
            elementInterval = pair.leftVal();
            endOfInterval = beginningOfInterval + elementInterval;

            if(endOfInterval > allElementsNumber){
                endOfInterval = allElementsNumber;
            }
            // dodanie wartoœci
            values.add(str);

            alphabetIntervals.replace(str, new Pair<>(beginningOfInterval, endOfInterval));

            beginningOfInterval = endOfInterval;
        }


    }

    private void fillInHistogramCounter(HistogramCounter counter, PGMFileReader fileReader) throws IOException {

        double iterNb = fileReader.getWidth() * fileReader.getHeight();

        //counter.addElement(fileReader.getWidth());
        //counter.addElement(fileReader.getHeight());

        for (int i = 0; i < iterNb; ++i) {
        	int element=fileReader.getElement();
            counter.addElement(element);
            fileContent[i]=element;
        }
    }
    
    public int[] getFileContent() {//TODO nie podoba mi siê taka organizacja, ¿e to AlphabetIntervals czyta plik
    	return fileContent;
    }

    public int getNumber() { return alphabetIntervals.size(); }

    public int getNthSymbol(int index) {
        return values.get(index);
    }
    // lista u¿ywanych wartoœci - w dekoderze potrzebny jest dostêp jako n-ty element z linii prawdopodobieñstw
    private List<Integer> values = new ArrayList<>();
    private Map<Integer, Pair<Double, Double>> alphabetIntervals;
}
