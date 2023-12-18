package adventofcode2023.days;

import adventofcode2023.util.Numbers;
import adventofcode2023.util.Pair;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;

public class Day11 extends AoCDay {
    ArrayList<Pair> galaxies;
    HashSet<Integer> emptyRows;
    HashSet<Integer> emptyCols;
    public Day11(InputStream file) {
        super(file);
        galaxies = new ArrayList<>();
        emptyRows = new HashSet<>();
        emptyCols = new HashSet<>();

        expandRows();
        expandCols();
        findGalaxies();
    }

    private void findGalaxies() {
        for (int y = 0; y < input.size(); y++) {
            for (int x = 0; x < input.get(y).length(); x++) {
                if (input.get(y).charAt(x) == '#') {
                    galaxies.add(new Pair(BigInteger.valueOf(x), BigInteger.valueOf(y)));
                }
            }
        }
    }

    private void expandCols() {
        for (int i = 0; i < input.size(); i++) {
            final int finalI = i;
            if (input.stream().filter(s -> s.substring(finalI, finalI+1).matches("#")).toList().isEmpty()) emptyCols.add(i);
        }
    }

    private void expandRows() {
        for (int i = 0; i < input.size(); i++) {
            if (input.get(i).matches("^\\.+$")) emptyRows.add(i);
        }
    }

    public HashSet<HashSet<Pair>> getPairs() {
        HashSet<HashSet<Pair>> myPairs = new HashSet<>();
        for(int i = 0; i < galaxies.size()-1; i++) {
            for(int j = i+1; j < galaxies.size(); j++) {
                myPairs.add(new HashSet<>(List.of(new Pair[]{galaxies.get(i), galaxies.get(j)})));
            }
        }
        return myPairs;
    }

    public BigInteger getDistanceBetween(Pair p1, Pair p2, BigInteger expansion) {
        int missingRows = emptyRows.stream().filter(integer -> Numbers.between(integer, p1.y().longValue(), p2.y().longValue())).toList().size();
        int missingCols = emptyCols.stream().filter(integer -> Numbers.between(integer, p1.x().longValue(), p2.x().longValue())).toList().size();
        return p1.getDistance(p2).add(expansion.subtract(BigInteger.ONE).multiply(BigInteger.valueOf(missingRows + missingCols)));
    }

    @Override
    public Object runPart1() {
        HashSet<HashSet<Pair>> myPairs = getPairs();
        BigInteger sum = BigInteger.ZERO;
        for (HashSet<Pair> pair : myPairs) {
            Iterator<Pair> it = pair.iterator();
            sum = sum.add(getDistanceBetween(it.next(), it.next(), BigInteger.TWO));
        }
        return sum;
    }

    @Override
    public Object runPart2() {
        HashSet<HashSet<Pair>> myPairs = getPairs();
        BigInteger sum = BigInteger.ZERO;
        for (HashSet<Pair> pair : myPairs) {
            Iterator<Pair> it = pair.iterator();
            sum = sum.add(getDistanceBetween(it.next(), it.next(), BigInteger.valueOf(1_000_000)));
        }
        return sum;
    }
}
