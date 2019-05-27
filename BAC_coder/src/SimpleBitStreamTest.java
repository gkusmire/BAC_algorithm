import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class SimpleBitStreamTest {

    @Test
    void test() {
        BitStream str = new BitStream();
        str.put(1);
        str.put(0);
        str.put(1);
        assertTrue(str.getLength() == 3);
        assertTrue(Arrays.equals(str.asArray(),new Integer[] { 0b101 }));
    }

}
