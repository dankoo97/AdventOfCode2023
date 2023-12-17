package adventofcode2023.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class Direction {
    public enum Cardinal {
        NORTH, EAST, SOUTH, WEST
    }

    public enum OneDimensionalDirection {
        LEFT, RIGHT
    }

    public static Pair moveOne(Pair pos, Cardinal d) {
        return switch (d) {
            case NORTH -> new Pair(pos.x(), pos.y()-1);
            case EAST -> new Pair(pos.x()+1, pos.y());
            case SOUTH -> new Pair(pos.x(), pos.y()+1);
            case WEST -> new Pair(pos.x()-1, pos.y());
        };
    }

    public static HashMap<Pair, Character> readCharMap(List<String> input) {
        return readCharMap(input, new ArrayList<>());
    }

    public static HashMap<Pair, Character> readCharMap(List<String> input, List<Character> ignore) {
        HashMap<Pair, Character> myMap = new HashMap<>();
        for (int y = 0; y < input.size(); y++) {
            for (int x = 0; x < input.get(y).length(); x++) {
                char c = input.get(y).charAt(x);
                if (!ignore.contains(c)) myMap.put(new Pair(x, y), c);
            }
        }
        return myMap;
    }

    public static HashMap<Pair, Integer> readIntMap(List<String> input) {
        HashMap<Pair, Integer> myMap = new HashMap<>();
        for (int y = 0; y < input.size(); y++) {
            for (int x = 0; x < input.get(y).length(); x++) {
                int c = Integer.parseInt(String.valueOf(input.get(y).charAt(x)));
                myMap.put(new Pair(x, y), c);
            }
        }
        return myMap;
    }
}
