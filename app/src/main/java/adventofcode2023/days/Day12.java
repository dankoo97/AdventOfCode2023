package adventofcode2023.days;

import adventofcode2023.util.Numbers;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;


public class Day12 extends AoCDay {

    public static class ArrangementMapHelper {
        String row;
        List<Integer> values;
        public ArrangementMapHelper(String row, List<Integer> values) {
            this.row = combineTokens(breakIntoTokens(row));
            this.values = values;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ArrangementMapHelper that = (ArrangementMapHelper) o;
            return Objects.equals(row, that.row) && Objects.equals(values, that.values);
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, values);
        }

        @Override
        public String toString() {
            return "ArrangementMapHelper{" +
                    "row='" + row + '\'' +
                    ", values=" + values +
                    '}';
        }
    }

    static HashMap<ArrangementMapHelper, BigInteger> arrangementMap = new HashMap<>();
    public Day12(InputStream file) {
        super(file);
    }

    public static BigInteger countArrangements(List<String> row, List<Integer> damaged) {
        if (damaged.isEmpty()) {
            if (DEBUG) {
                System.out.println(row);
                System.out.println(damaged);
                System.out.println(1);
                System.out.println();
            }
            return row.stream().anyMatch(item -> item.contains("#")) ? BigInteger.ZERO : BigInteger.ONE;
        }

        if (Numbers.sumIntegerArray(damaged) > row.stream().mapToInt(String::length).reduce(0, Integer::sum)) {
            if (DEBUG) {
                System.out.println(row);
                System.out.println(damaged);
                System.out.println(0);
                System.out.println();
            }
            return BigInteger.ZERO;
        }

        ArrangementMapHelper key = new ArrangementMapHelper(combineTokens(row), damaged);

        if (arrangementMap.containsKey(key)) {
            return arrangementMap.get(key);
        }

        if (row.size() == 1) {
            BigInteger count = countOneToMany(row.get(0), damaged);

            if (DEBUG) {
                System.out.println(row);
                System.out.println(damaged);
                System.out.println(count);
                System.out.println();
            }

            return count;
        }

        if (damaged.size() == 1) {
            BigInteger count = countManyToOne(row, damaged.get(0));

            if (DEBUG) {
                System.out.println(row);
                System.out.println(damaged);
                System.out.println(count);
                System.out.println();
            }

            return count;
        }

        BigInteger count = BigInteger.ZERO;
        for (int i = row.get(0).contains("#") ? 1 : 0; i <= damaged.size(); i++) {
            BigInteger first = countArrangements(row.subList(0, 1), damaged.subList(0, i));
            BigInteger second = countArrangements(row.subList(1, row.size()), damaged.subList(i, damaged.size()));

            count = count.add(first.multiply(second));
        }


        if (DEBUG) {
            System.out.println(row);
            System.out.println(damaged);
            System.out.println(count);
            System.out.println();
        }

        arrangementMap.put(key, count);
        return count;
    }

    public static String combineTokens(List<String> row) {
        return row.stream().reduce("", (token, item) -> token.concat(".").concat(item)).substring(1);
    }

    public static List<String> breakIntoTokens(String row) {
        return Arrays.stream(row.split("\\.+")).filter(item -> !item.isBlank()).toList();
    }

    public static BigInteger countOneToOneMatches(String row, int damaged) {

        ArrangementMapHelper key = new ArrangementMapHelper(row, List.of(damaged));
        if (arrangementMap.containsKey(key)) {
            return arrangementMap.get(key);
        }

        if (row.length() < damaged) {
            return BigInteger.ZERO;
        }

        if (!row.contains("#")) {
            BigInteger count = BigInteger.valueOf(row.length() - damaged + 1);
            arrangementMap.put(key, count);
            return count;
        }

        BigInteger count = BigInteger.ZERO;
        for (int i = 0; i <= row.length() - damaged; i++) {
            String matcher = String.format("^\\?{%s}[#?]{%s}\\?*$", i, damaged);
            if (row.matches(matcher)) count = count.add(BigInteger.ONE);
        }

        arrangementMap.put(key, count);
        return count;
    }

    public static BigInteger countOneToMany(String row, List<Integer> damaged) {
        if (damaged.isEmpty()) {
            return BigInteger.ONE;
        }

        if (row.isBlank()) {
            return BigInteger.ZERO;
        }

        ArrangementMapHelper key = new ArrangementMapHelper(row, damaged);
        if (arrangementMap.containsKey(key)) {
            return arrangementMap.get(key);
        }

        if (damaged.size() == 1) {
            BigInteger count = countOneToOneMatches(row, damaged.get(0));
            arrangementMap.put(key, count);
            return count;
        }

        if (damaged.stream().reduce(0, Integer::sum) + damaged.size() - 1 > row.length()) {
            arrangementMap.put(key, BigInteger.ZERO);
            return BigInteger.ZERO;
        }

        int i = 0;
        BigInteger count = BigInteger.ZERO;
        while (row.startsWith("?".repeat(i))) {
            String matcher = String.format("^\\?{%s}[#?]{%s}\\?.*", i, damaged.get(0));
            if (row.matches(matcher)) {
                count = count.add(countOneToMany(row.substring(i + damaged.get(0) + 1), damaged.subList(1, damaged.size())));
            }
            i++;
        }

        arrangementMap.put(key, count);
        return count;

    }

    public static BigInteger countManyToOne(List<String> row, int damaged) {
        ArrangementMapHelper key = new ArrangementMapHelper(combineTokens(row), List.of(damaged));

        if (arrangementMap.containsKey(key)) {
            return arrangementMap.get(key);
        }

        List<String> reduced = row.stream().filter(item -> item.contains("#")).toList();

        if (reduced.size() > 1) {
            arrangementMap.put(key, BigInteger.ZERO);
            return BigInteger.ZERO;
        }

        if (reduced.size() == 1) {
            BigInteger count = countOneToOneMatches(reduced.get(0), damaged);
            arrangementMap.put(key, count);
            return count;
        }

        BigInteger count = BigInteger.ZERO;
        for (String s : row) {
            count = count.add(countOneToOneMatches(s, damaged));
        }

        arrangementMap.put(key, count);
        return count;
    }

    @Override
    public Object runPart1() {
        BigInteger count = BigInteger.ZERO;

        for (String line : input) {
            String[] tokens = line.split(" ");
            List<String> row = breakIntoTokens(tokens[0]);
            List<Integer> damaged = Arrays.stream(tokens[1].split(",")).toList().stream().mapToInt(Integer::parseInt).boxed().collect(Collectors.toCollection(ArrayList::new));
            BigInteger increment = countArrangements(row, damaged);

            if (DEBUG) {
                System.out.println(row);
                System.out.println(damaged);
                System.out.println(increment);
                System.out.println();
            }


            count = count.add(increment);
        }

        if (DEBUG) {
            for (ArrangementMapHelper key : arrangementMap.keySet()) {
                System.out.println(key);
                System.out.println(arrangementMap.get(key));
                System.out.println();
            }
        }

        return count;
    }

    @Override
    public Object runPart2() {
        BigInteger count = BigInteger.ZERO;

        for (String line : input) {
            String[] tokens = line.split(" ");
            List<String> row = breakIntoTokens("?".concat(tokens[0]).repeat(5).substring(1));
            List<Integer> damaged = Arrays.stream(tokens[1].concat(",").repeat(5).split(",")).toList().stream().mapToInt(Integer::parseInt).boxed().collect(Collectors.toCollection(ArrayList::new));

            BigInteger increment = countArrangements(row, damaged);

            if (DEBUG) {
                System.out.println(row);
                System.out.println(damaged);
                System.out.println(increment);
                System.out.println();
            }


            count = count.add(increment);
        }
        return count;
    }

    static final boolean DEBUG = false;
}
