package adventofcode2023.days;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;

public class Day15 extends AoCDay{
    public Day15(InputStream file) {
        super(file);
    }

    public int hashString(String s) {
        int value = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            value = (17 * (value + c)) % 256;
        }
        return value;
    }

    @Override
    public Object runPart1() {
        BigInteger total = BigInteger.ZERO;
        for (String s : input.get(0).split(",")) {
            total = total.add(BigInteger.valueOf(hashString(s)));
        }
        return total;
    }

    @Override
    public Object runPart2() {
        HashMap<Integer, LinkedList<String>> boxes = new HashMap<>(256);
        HashMap<String, Integer> valueMap = new HashMap<>();
        for (String s : input.get(0).split(",")) {
            if (s.contains("=")) {
                String letters = s.substring(0, s.length() - 2);
                int focalLength = Integer.parseInt(s.substring(s.length()-1));

                int hash = hashString(letters);
                if (!boxes.containsKey(hash)) boxes.put(hash, new LinkedList<>());
                if (!boxes.get(hash).contains(letters)) {
                    boxes.get(hash).add(letters);
                }
                valueMap.put(letters, focalLength);

            } else {
                String letters = s.substring(0, s.length() - 1);

                int hash = hashString(letters);
                if (!boxes.containsKey(hash)) boxes.put(hash, new LinkedList<>());
                else {
                    boxes.get(hash).remove(letters);
                    valueMap.remove(letters);
                }
            }
        }

        BigInteger total = BigInteger.ZERO;
        for (Integer box : boxes.keySet()) {
            for (int i = 0; i < boxes.get(box).size(); i++) {
                int val = (box + 1) * (i + 1) * valueMap.get(boxes.get(box).get(i));
                total = total.add(BigInteger.valueOf(val));
            }
        }

        return total;
    }
}
