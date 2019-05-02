public class Pair <Elem1Type, Elem2Type> {

    public Pair(Elem1Type _leftVal, Elem2Type _rightVal) {

        leftVal = _leftVal;
        rightVal = _rightVal;
    }

    public Elem1Type leftVal() {

        return leftVal;
    }

    public Elem2Type rightVal() {

        return rightVal;
    }

    private Elem1Type leftVal;
    private Elem2Type rightVal;
}
