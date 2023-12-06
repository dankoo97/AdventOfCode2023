package adventofcode2023.days;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.*;

public class Day05 extends AoCDay {
    ArrayList<BigInteger> seeds;
    ArrayList<SeedMap> allMaps;

    public interface BigIntUnaryOperator {
        BigInteger applyAsBigInt(BigInteger bigInteger);
    }

    public Day05 (InputStream inputStream) {
        super(inputStream);
    }

    public static class SeedMap {
        public TreeMap<BigInteger, BigInteger> remaps;
        public List<BigIntUnaryOperator> functions;
        public SeedMap() {
            remaps = new TreeMap<>();
            functions = new ArrayList<>();
        }

        public void addFunction(BigInteger[] values, BigIntUnaryOperator operator) {
            remaps.put(values[0], values[1]);
            functions.add(operator);
        }

        public BigInteger getLocationFromMap(BigInteger seed) {
            BigInteger curr = seed;
            for (BigIntUnaryOperator f : functions) {
                curr = f.applyAsBigInt(curr);
                if (!curr.equals(seed)) {
                    return curr;
                }
            }
            return seed;
        }
    }

    public void readMap(SeedMap seedMap, String mapObject) {
        String[] tokens = mapObject.split(" ");
        BigInteger destination = new BigInteger(tokens[0]);
        BigInteger source =  new BigInteger(tokens[1]);
        BigInteger rangeLength =  new BigInteger(tokens[2]);

        seedMap.addFunction(
                new BigInteger[]{ source, rangeLength },
                (BigInteger n) -> {
                    if (liesWithinRange(n, source, source.add(rangeLength))) {
                        return destination.add(n).subtract(source);
                    }
                    return n;
        });
    }

    public void generateMaps() {
        allMaps = new ArrayList<>();

        for (String m : input.subList(1, input.size())) {
            if (m.endsWith(" map:")) {
                allMaps.add(new SeedMap());
            } else if (!m.trim().isEmpty()) {
                readMap(allMaps.get(allMaps.size()-1), m);
            }
        }
    }

    public BigInteger getLocation(BigInteger seed) {
        BigInteger curr = seed;
        for (SeedMap seedMap : allMaps) {
            curr = seedMap.getLocationFromMap(curr);
        }
        return curr;
    }

    public void getSeeds() {
        seeds = new ArrayList<>();
        for (String m : input.get(0).trim().split(" ")) {
            try {
                seeds.add(new BigInteger(m));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public static boolean liesWithinRange(BigInteger value, BigInteger start, BigInteger end) {
        return start.compareTo(value) <= 0 && end.compareTo(value) > 0;
    }

    @Override
    public Object runPart1() {
        getSeeds();
        generateMaps();
        BigInteger smallest = new BigInteger("999999999999999");
        for (BigInteger seed : seeds) {
            BigInteger location = getLocation(seed);
            if (location.min(smallest).equals(location)) {
                smallest = location;
            }
        }
        return smallest;
    }

    @Override
    public Object runPart2() {
        getSeeds();
        generateMaps();
        BigInteger smallest = new BigInteger("999999999999999");
        ArrayList<BigInteger> curr = seeds;

        for (SeedMap seedMap : allMaps) {
            ArrayList<BigInteger> next = new ArrayList<>();
            while (!curr.isEmpty()) {
                BigInteger start = curr.get(0);
                BigInteger range = curr.get(1);

                curr.remove(0);
                curr.remove(0);

                BigInteger key = seedMap.remaps.floorKey(start);
                if (key != null) {
                    BigInteger keyRange = seedMap.remaps.get(key);
                    if (liesWithinRange(start, key, key.add(keyRange)) && (liesWithinRange(start.add(range), key, key.add(keyRange)) || start.add(range).equals(key.add(keyRange)))) {
                        next.add(seedMap.getLocationFromMap(start));
                        next.add(range);

                        continue;
                    } else if (liesWithinRange(start, key, key.add(keyRange)) && !start.equals(key.add(keyRange))) {
                        BigInteger rangeSplit = key.add(keyRange).subtract(start);

                        next.add(seedMap.getLocationFromMap(start));
                        next.add(rangeSplit);

                        curr.add(start.add(rangeSplit));
                        curr.add(range.subtract(rangeSplit));
                        continue;
                    }
                }
                key = seedMap.remaps.ceilingKey(start);

                if (key != null && liesWithinRange(key, start, start.add(range))) {
                    next.add(seedMap.getLocationFromMap(start));
                    next.add(key.subtract(start));

                    curr.add(key);
                    curr.add(start.add(range).subtract(key));

                } else {
                    next.add(seedMap.getLocationFromMap(start));
                    next.add(range);
                }
            }

            curr = next;
        }

        for (int i = 0; i < curr.size(); i += 2) {
            smallest = smallest.min(curr.get(i));
        }

        return smallest;
    }
}