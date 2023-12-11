package adventofcode2023.util;

import java.math.BigInteger;
import java.util.List;

public class Numbers {

    public static Integer sumIntegerArray(List<Integer> numberList) {
        Integer s = 0;
        for (Integer n : numberList) {
            s += n;
        }

        return s;
    }

    public static Float sumFloatArray(List<Float> numberList) {
        Float s = 0f;
        for (Float n : numberList) {
            s += n;
        }
        return s;
    }

    public static BigInteger sumBigIntegerArray(List<BigInteger> numberList) {
        BigInteger s = BigInteger.ZERO;
        for (BigInteger n : numberList) {
            s = s.add(n);
        }
        return s;
    }

    public static boolean between(long value, long start, long end) {
        if (start > end) {
            return between(value, end, start);
        }
        return start < value && value < end;
    }
}
