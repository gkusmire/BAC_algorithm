import java.util.*;

public class HistogramCounter {

    public HistogramCounter() {
        numberOfAllElements = 0;
        map = new HashMap<>();
    }

    public void addElement(Integer elem) {

        if(!map.containsKey(elem)) {
            map.put(elem, new Pair<>(1, 0));
        }
        else {
            Pair<Integer,Integer> pair = map.get(elem);

            map.replace(elem, new Pair<>(pair.leftVal()+1, 0));
        }
        numberOfAllElements += 1;
    }

    public int getUniqueElementsNumber() {

        return map.size();
    }

    public Integer getAllElementsNumber(){

        return numberOfAllElements;
    }

    public Map<Integer, Pair<Integer, Integer>> getHistogramValues() {
        return map;
    }

    private Map<Integer, Pair<Integer, Integer>> map;
    private Integer numberOfAllElements;
}
