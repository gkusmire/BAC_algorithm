import java.util.Map;

public class AlphabetIntervals {

    public AlphabetIntervals(FileReader fileReader) {

        setAlphabetIntervals(fileReader);
    }

    public Pair<Double, Double> getAlphabetElementInterval(String elem) {

        return alphabetIntervals.get(elem);
    }

    private void setAlphabetIntervals(FileReader fileReader) {

        HistogramCounter counter = new HistogramCounter();

        fillInHistogramCounter(counter, fileReader);
        setIntervalsBasedOnHistogramCounter(counter);
    }

    private void setIntervalsBasedOnHistogramCounter(HistogramCounter counter) {

        double allElementsNumber = counter.getAllElementsNumber();
        double beginningOfInterval = 0.0;
        double elementInterval = 0.0;
        double endOfInterval = 0.0;
        long nbOfElements = counter.getAllElementsNumber();

        alphabetIntervals = counter.getHistogramValues();

        for(String str : alphabetIntervals.keySet()) {

            Pair<Double, Double> pair = alphabetIntervals.get(str);
            elementInterval = pair.leftVal()/allElementsNumber;
            endOfInterval = beginningOfInterval + elementInterval;

            if(nbOfElements == 1){
                endOfInterval = 1.0;
            }

            alphabetIntervals.replace(str, new Pair<>(beginningOfInterval, endOfInterval));
            System.out.println(str+" : "+ beginningOfInterval+"  "+endOfInterval);

            beginningOfInterval = endOfInterval;
            nbOfElements -= 1;
        }
    }

    private void fillInHistogramCounter(HistogramCounter counter, FileReader fileReader) {

        fileReader.setCursorAtTheBeginningOfFile();

        while(!fileReader.isEOF()) {

            counter.addElement(fileReader.getElement());
        }
    }


    private Map<String, Pair<Double, Double>> alphabetIntervals;
}
