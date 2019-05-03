import java.io.IOException;
import java.util.Map;

public class AlphabetIntervals {

    public AlphabetIntervals(PGMFileReader fileReader) throws IOException {

        setAlphabetIntervals(fileReader);
    }

    public Pair<Double, Double> getAlphabetElementInterval(Integer elem) {

        return alphabetIntervals.get(elem);
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
            elementInterval = pair.leftVal()/allElementsNumber;
            endOfInterval = beginningOfInterval + elementInterval;

            if(endOfInterval > 1.0){
                endOfInterval = 1.0;
            }

            alphabetIntervals.replace(str, new Pair<>(beginningOfInterval, endOfInterval));

            beginningOfInterval = endOfInterval;
        }
    }

    private void fillInHistogramCounter(HistogramCounter counter, PGMFileReader fileReader) throws IOException {

        double iterNb = fileReader.getWidth() * fileReader.getHeight();

        counter.addElement(fileReader.getWidth());
        counter.addElement(fileReader.getHeight());

        for (int i = 0; i < iterNb; ++i) {
            counter.addElement(fileReader.getElement());
        }
    }

    private Map<Integer, Pair<Double, Double>> alphabetIntervals;
}
