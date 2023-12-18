package adventofcode2023.days;

import adventofcode2023.util.Direction;
import adventofcode2023.util.Pair;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Stream;

import static adventofcode2023.util.Direction.moveOne;
import static adventofcode2023.util.Direction.readIntMap;

public class Day17 extends AoCDay {
    HashMap<Pair, Integer> map;
    HashMap<HeatPathPoint, BigInteger> visited;
    public Day17(InputStream file) {
        super(file);

        map = readIntMap(input);
        visited = new HashMap<>();
    }

    public record HeatPath(List<Pair> path, BigInteger value, Direction.Cardinal direction) implements Comparable<HeatPath> {
        @Override
        public int compareTo(HeatPath o) {
            return this.value.compareTo(o.value);
        }
    }

    public record HeatPathPoint(Pair pos, Direction.Cardinal d, int steps) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HeatPathPoint that = (HeatPathPoint) o;
            return steps == that.steps && Objects.equals(pos, that.pos) && d == that.d;
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, d, steps);
        }
    }

    public BigInteger bfs(Pair start, Pair end, int min, int max) {
        PriorityQueue<HeatPath> heatPaths = new PriorityQueue<>(HeatPath::compareTo);
        heatPaths.add(new HeatPath(List.of(start), BigInteger.ZERO, Direction.Cardinal.SOUTH));
        heatPaths.add(new HeatPath(List.of(start), BigInteger.ZERO, Direction.Cardinal.EAST));

        while (!heatPaths.isEmpty()) {
            HeatPath curr = heatPaths.poll();

            if (curr.path.get(curr.path.size()-1).equals(end)) {
                return curr.value;
            }

            Direction.Cardinal[] directions = switch (curr.direction) {
                case NORTH, SOUTH -> new Direction.Cardinal[]{Direction.Cardinal.EAST, Direction.Cardinal.WEST};
                case EAST, WEST -> new Direction.Cardinal[]{Direction.Cardinal.NORTH, Direction.Cardinal.SOUTH};
            };

            for (Direction.Cardinal d : directions) {
                Pair pos = curr.path.get(curr.path.size()-1);
                List<Pair> path = curr.path;
                BigInteger v = curr.value;
                for (int i = 0; i < min; i++) {
                    pos = moveOne(pos, d);
                    path = Stream.concat(path.stream(), Stream.of(pos)).toList();
                    if (!map.containsKey(pos)) {
                        break;
                    }

                    v = BigInteger.valueOf(map.get(pos)).add(v);
                }
                for (int i = min; i < max; i++) {
                    pos = moveOne(pos, d);
                    path = Stream.concat(path.stream(), Stream.of(pos)).toList();
                    if (!map.containsKey(pos)) {
                        break;
                    }

                    v = BigInteger.valueOf(map.get(pos)).add(v);
                    HeatPathPoint hpp = new HeatPathPoint(pos, d, i);

                    if (!visited.containsKey(hpp) || visited.get(hpp).compareTo(v) > 0) {
                        heatPaths.add(new HeatPath(path, v, d));
                        visited.put(hpp, v);
                    }
                }
            }
        }
        return BigInteger.ZERO;
    }

    @Override
    public Object runPart1() {
        Pair start = Pair.ORIGIN;
        Pair end = Pair.fromLong(input.get(0).length()-1, input.size()-1);
        return bfs(start, end, 0, 3);
    }

    @Override
    public Object runPart2() {
        Pair start = Pair.ORIGIN;
        Pair end = Pair.fromLong(input.get(0).length()-1, input.size()-1);
        return bfs(start, end, 3, 10);
    }
}
