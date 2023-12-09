package adventofcode2023.days;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class Day09 extends AoCDay{
    public Day09(InputStream file) {
        super(file);

    }

    public static boolean allZeros(ArrayList<Integer> nums) {
        for (int n : nums) {
            if (n != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object runPart1() {
        BigInteger sum = BigInteger.ZERO;
        for (String line : input) {
            ArrayList<Integer> nums = new ArrayList<>(Arrays.stream(line.split("\\s")).map(Integer::parseInt).toList());
            ArrayList<ArrayList<Integer>> iter = new ArrayList<>();
            iter.add(nums);

            while (!allZeros(iter.get(iter.size()-1))) {
                ArrayList<Integer> prev = iter.get(iter.size()-1);
                ArrayList<Integer> thisIter = new ArrayList<>();
                for (int i = 0; i < prev.size()-1; i++) {
                    thisIter.add(prev.get(i+1) - prev.get(i));
                }
                iter.add(thisIter);
            }

            for (int i = iter.size()-1; i > 0; i--) {
                iter.get(i-1).add(iter.get(i).get(iter.get(i).size()-1) + iter.get(i-1).get(iter.get(i-1).size()-1));
            }

            sum = sum.add(BigInteger.valueOf(iter.get(0).get(iter.get(0).size()-1)));
        }
        return sum;
    }

    @Override
    public Object runPart2() {
        BigInteger sum = BigInteger.ZERO;
        for (String line : input) {
            ArrayList<Integer> nums = new ArrayList<>(Arrays.stream(line.split("\\s")).map(Integer::parseInt).toList());
            ArrayList<ArrayList<Integer>> iter = new ArrayList<>();
            iter.add(nums);

            while (!allZeros(iter.get(iter.size()-1))) {
                ArrayList<Integer> prev = iter.get(iter.size()-1);
                ArrayList<Integer> thisIter = new ArrayList<>();
                for (int i = 0; i < prev.size()-1; i++) {
                    thisIter.add(prev.get(i+1) - prev.get(i));
                }
                iter.add(thisIter);
            }

            for (int i = iter.size()-1; i > 0; i--) {
                iter.get(i-1).add(0, iter.get(i-1).get(0) - iter.get(i).get(0));
            }

            sum = sum.add(BigInteger.valueOf(iter.get(0).get(0)));
        }
        return sum;
    }
}
