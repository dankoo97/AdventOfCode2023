package adventofcode2023.days;

import adventofcode2023.util.Pair;

import java.io.InputStream;
import java.util.*;

public class Day03 extends AoCDay {
    private final Map<Pair, String> symbolMap;
    private final Map<Pair, Integer> numberMap;
    private final Set<Pair> gears;

    public Day03(InputStream file) {
        super(file);

        symbolMap = new HashMap<>();
        numberMap = new HashMap<>();
        gears = new HashSet<>();
    }

    public void getSymbols() {
        for (int i = 0; i < input.size(); i++) {
            for (int j = 0; j < input.get(i).length(); j++) {
                if (input.get(i).substring(j, j+1).matches("[^.0-9]")) {
                    symbolMap.put(new Pair(i, j), input.get(i).substring(j, j+1));
                }
                if (input.get(i).substring(j, j+1).matches("\\*")) {
                    gears.add(new Pair(i, j));
                }
            }
        }
    }

    public void fromMapGetNumbers() {
        for (Pair coord: symbolMap.keySet()) {
            for (Pair adj: checkAdjacent(coord)) {
                expandNum(adj);
            }
        }
    }

    private Pair expandNum(Pair coord) {
        int start = coord.getY(), end = coord.getY() + 1;
        while (start >= 0 && input.get(coord.getX()).substring(start, end).matches("^\\d+$")) {
            start--;
        }
        start++;

        while (end <= input.get(coord.getX()).length() && input.get(coord.getX()).substring(start, end).matches("^\\d+$")) {
            end++;
        }
        end--;

        Pair key = new Pair(coord.getX(), start);
        Integer value = Integer.parseInt(input.get(coord.getX()).substring(start, end));
        if (!numberMap.containsKey(key)) {
            numberMap.put(key, value);
        }

        return key;
    }

    public ArrayList<Pair> checkAdjacent(Pair coord) {
        ArrayList<Pair> adj = new ArrayList<>();
        for (Pair p : coord.getAdjacent()) {
            try {
                if (input.get(p.getX()).substring(p.getY(), p.getY() + 1).matches("\\d")) {
                    adj.add(p);
                }
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
        return adj;
    }

    @Override
    public Object runPart1() {
        this.getSymbols();
        this.fromMapGetNumbers();

        int s = 0;
        for (Integer n : numberMap.values()) {
            s += n;
        }

        return s;
    }

    @Override
    public Object runPart2() {
        this.getSymbols();
        this.fromMapGetNumbers();

        int s = 0;

        for (Pair g : gears) {
            HashSet<Pair> adj = new HashSet<>();
            for (Pair coord : checkAdjacent(g)) {
                adj.add(expandNum(coord));
            }

            if (adj.size() == 2) {
                int m = 1;
                for (Pair c : adj) {
                    m *= numberMap.get(c);
                }
                s += m;
            }
        }

        return s;
    }

}
