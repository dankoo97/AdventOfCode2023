package adventofcode2023.days;

import adventofcode2023.util.Pair;
import adventofcode2023.util.Direction.Cardinal;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import static adventofcode2023.util.Direction.moveOne;

public class Day14 extends AoCDay {
    HashSet<Pair> roundedRocks;
    HashSet<Pair> cubeRocks;

    public Day14(InputStream file) {
        super(file);
        roundedRocks = new HashSet<>();
        cubeRocks = new HashSet<>();

        for (int y = 0; y < input.size(); y++) {
            for (int x = 0; x < input.get(y).length(); x++) {
                switch (input.get(y).charAt(x)) {
                    case 'O' -> roundedRocks.add(Pair.fromLong(x, y));
                    case '#' -> cubeRocks.add(Pair.fromLong(x, y));
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

    public void moveTillStopped(Pair pos, Cardinal d) {
        Pair rock = pos;
        Pair adj = moveOne(pos, d);
        while (canMoveRock(rock, adj)) {
            moveRock(rock, adj);
            rock = adj;
            adj = moveOne(pos, d);
        }
    }

    public boolean isValid(Pair p) {
        return p.x().intValue() >= 0 && p.x().intValue() < input.get(0).length() &&
                p.y().intValue() >= 0 && p.y().intValue() < input.size();
    }

    public BigInteger calculateLoad() {
        BigInteger total = BigInteger.ZERO;

        for (Pair p : roundedRocks) {
            total = total.add(BigInteger.valueOf(input.size() - p.y().intValue()));
        }

        return total;
    }

    public void runXCycles(BigInteger x) {
        HashMap<HashSet<Pair>, BigInteger> seen = new HashMap<>();
        for (BigInteger i = BigInteger.ZERO; i.compareTo(x) < 0; i = i.add(BigInteger.ONE)) {
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
        for (Cardinal d : new Cardinal[]{Cardinal.NORTH, Cardinal.WEST, Cardinal.SOUTH, Cardinal.EAST}) {
            HashSet<Pair> movableRocks;
            do {
                movableRocks = new HashSet<>();

                for (Pair p : roundedRocks) {
                    Pair adj = moveOne(p, d);
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
                Pair p = Pair.fromLong(x, y);
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
                Pair adj = moveOne(p, Cardinal.NORTH);
                if (canMoveRock(p, adj)) movableRocks.add(p);
            }

            for (Pair p : movableRocks) {
                moveTillStopped(p, Cardinal.NORTH);
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
