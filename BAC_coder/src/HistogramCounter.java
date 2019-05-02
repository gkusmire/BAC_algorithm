import java.util.*;

public class HistogramCounter {

    public HistogramCounter() {

        map = new HashMap<>();
    }

    public void addElement(String elem) {

        if(!map.containsKey(elem)) {
            map.put(elem, new Pair<>(1.0, 0.0));
        }
        else {
            Pair<Double, Double> pair = map.get(elem);

            map.replace(elem, new Pair<>(pair.leftVal()+1, 0.0));
        }
        numberOfAllElements += 1;
    }

    public int getUniqueElementsNumber() {

        return map.size();
    }

    public long getAllElementsNumber(){

        return numberOfAllElements;
    }

    public Map<String, Pair<Double, Double>> getHistogramValues() {

        return map;
    }

    private Map<String, Pair<Double, Double>> map;
    private long numberOfAllElements;
}
