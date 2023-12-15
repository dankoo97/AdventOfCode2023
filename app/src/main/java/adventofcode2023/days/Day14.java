package adventofcode2023.days;

import adventofcode2023.util.Pair;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class Day14 extends AoCDay {
    public enum Direction {
        NORTH, WEST, SOUTH, EAST
    }
    HashSet<Pair> roundedRocks;
    HashSet<Pair> cubeRocks;

    public static class CacheKey {
        Pair pos;
        Direction d;
        public CacheKey(Pair pos, Direction d) {
            this.pos = pos;
            this.d = d;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return Objects.equals(pos, cacheKey.pos) && d == cacheKey.d;
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, d);
        }

        @Override
        public String toString() {
            return "CacheKey{" +
                    "pos=" + pos +
                    ", d=" + d +
                    '}';
        }
    }

    public Day14(InputStream file) {
        super(file);
        roundedRocks = new HashSet<>();
        cubeRocks = new HashSet<>();

        for (int y = 0; y < input.size(); y++) {
            for (int x = 0; x < input.get(y).length(); x++) {
                switch (input.get(y).charAt(x)) {
                    case 'O' -> roundedRocks.add(new Pair(x, y));
                    case '#' -> cubeRocks.add(new Pair(x, y));
                }
            }
        }
    }

    public void moveRock(Pair start, Pair end) {
        roundedRocks.remove(start);
        roundedRocks.add(end);
    }

    public boolean canMoveRock(Pair start, Pair end) {
        if (!roundedRocks.contains(start)) {
            return false;
        }
        return isEmpty(end) && isValid(end);
    }

    public boolean isEmpty(Pair p) {
        return !(cubeRocks.contains(p) || roundedRocks.contains(p));
    }

    public Pair getMovement(Pair pos, Direction d) {
        Pair adj;
        switch (d) {
            case NORTH -> adj = new Pair(pos.getX(), pos.getY()-1);
            case EAST -> adj = new Pair(pos.getX()+1, pos.getY());
            case SOUTH -> adj = new Pair(pos.getX(), pos.getY()+1);
            case WEST -> adj = new Pair(pos.getX()-1, pos.getY());
            default -> throw new Error("Unable to parse direction");
        }
        return adj;
    }

    public Pair moveTillStopped(Pair pos, Direction d) {
        Pair rock = pos;
        Pair adj = getMovement(pos, d);
        while (canMoveRock(rock, adj)) {
            moveRock(rock, adj);
            rock = adj;
            adj = getMovement(pos, d);
        }

        return rock;
    }

    public boolean isValid(Pair p) {
        return p.getX() >= 0 && p.getX() < input.get(0).length() &&
                p.getY() >= 0 && p.getY() < input.size();
    }

    public BigInteger calculateLoad() {
        BigInteger total = BigInteger.ZERO;

        for (Pair p : roundedRocks) {
            total = total.add(BigInteger.valueOf(input.size() - p.getY()));
        }

        return total;
    }

    public void runXCycles(BigInteger x) {
        HashMap<HashSet<Pair>, BigInteger> seen = new HashMap<>();
        for (BigInteger i = BigInteger.ZERO; i.compareTo(x) < 0; i = i.add(BigInteger.ONE)) {
            if (i.mod(BigInteger.valueOf(1_000_000)).equals(BigInteger.ZERO)) {
                System.out.println(i);
            }
            runCycle();
            if (seen.containsKey(roundedRocks)) {
                System.out.println(seen.get(roundedRocks));
                System.out.println(i);
                System.out.println();

            } else {
                seen.put(roundedRocks, i);
            }

        }
    }

    public void runCycle() {
        for (Direction d : Direction.values()) {
            HashSet<Pair> movableRocks;
            do {
                movableRocks = new HashSet<>();

                for (Pair p : roundedRocks) {
                    Pair adj = getMovement(p, d);
                    if (canMoveRock(p, adj)) movableRocks.add(p);
                }

                for (Pair p : movableRocks) {
                    moveTillStopped(p, d);
                }

            } while (!movableRocks.isEmpty());
        }
    }

    public String toMap() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < input.size(); y++) {
            for (int x = 0; x < input.get(y).length(); x++) {
                Pair p = new Pair(x, y);
                if (roundedRocks.contains(p)) {
                    sb.append("O");
                } else if (cubeRocks.contains(p)) {
                    sb.append("#");
                } else {
                    sb.append(".");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public Object runPart1() {
        HashSet<Pair> movableRocks;
        do {
            movableRocks = new HashSet<>();

            for (Pair p : roundedRocks) {
                Pair adj = getMovement(p, Direction.NORTH);
                if (canMoveRock(p, adj)) movableRocks.add(p);
            }

            for (Pair p : movableRocks) {
                moveTillStopped(p, Direction.NORTH);
            }

        } while (!movableRocks.isEmpty());

        return calculateLoad();
    }

    @Override
    public Object runPart2() {
        // Cycle size is 63 (from 91 to 153 repeating); 1b % 63 == 55; 55 + 63 == 118
        BigInteger x = BigInteger.valueOf(118);
        runXCycles(x);

        return calculateLoad();
    }
}
