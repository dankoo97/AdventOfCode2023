package adventofcode2023.days;

import adventofcode2023.util.Pair;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;

public class Day22 extends AoCDay {
    List<Brick> bricks;
    HashMap<ThreeDimensionalPoint, Brick> isOccupiedBy;
    HashMap<Brick, HashSet<Brick>> disintegrationCount = new HashMap<>();
    public Day22(InputStream file) {
        super(file);
        bricks = new ArrayList<>(input.size());
        isOccupiedBy = new HashMap<>();

        for (String line : input) {
            String[] tokens = line.split("~");
            Brick b = new Brick(
                    ThreeDimensionalPoint.fromString(tokens[0]),
                    ThreeDimensionalPoint.fromString(tokens[1])
            );
            bricks.add(b);
        }

        allBricksFall();
        bricks.sort(Brick::compareTo);
        isOccupiedBy = new HashMap<>();
        for (Brick b : bricks) {
            for (ThreeDimensionalPoint p : b.getOccupiedSpace()) {
                isOccupiedBy.put(p, b);
            }
        }

        for (Brick b : bricks) {
            Brick rise = b.fallX(BigInteger.valueOf(-1));
            for (ThreeDimensionalPoint p : rise.getOccupiedSpace()) {
                if (isOccupiedBy.containsKey(p) && !isOccupiedBy.get(p).equals(b)) {
                    b.directlySupporting.add(isOccupiedBy.get(p));
                }
            }
        }

        for (int i = bricks.size()-1; i >= 0; i--) {
            HashSet<Brick> disintegrated = tryToDisintegrate(bricks.get(i));
            disintegrationCount.put(bricks.get(i), disintegrated);
        }
    }

    public record ThreeDimensionalPoint(BigInteger x, BigInteger y, BigInteger z) {
        public static ThreeDimensionalPoint fromString(String s) {
            String[] tokens = s.split(",");
            return new ThreeDimensionalPoint(
                    new BigInteger(tokens[0]),
                    new BigInteger(tokens[1]),
                    new BigInteger(tokens[2])
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ThreeDimensionalPoint that = (ThreeDimensionalPoint) o;
            return Objects.equals(x, that.x) && Objects.equals(y, that.y) && Objects.equals(z, that.z);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }
    }
    public static class Brick implements Comparable<Brick> {
        HashSet<ThreeDimensionalPoint> occupiedSpace;
        ThreeDimensionalPoint a, b;
        HashSet<Brick> directlySupporting;
        public Brick(ThreeDimensionalPoint a, ThreeDimensionalPoint b) {
            this.a = a;
            this.b = b;
            occupiedSpace = findOccupiedSpace();
            directlySupporting = new HashSet<>();
        }
        private HashSet<ThreeDimensionalPoint> findOccupiedSpace() {
            HashSet<ThreeDimensionalPoint> occupied = new HashSet<>();
            for (BigInteger x = a.x().min(b.x()); x.compareTo(a.x().max(b.x())) <= 0; x = x.add(BigInteger.ONE)) {
                for (BigInteger y = a.y().min(b.y()); y.compareTo(a.y().max(b.y())) <= 0; y = y.add(BigInteger.ONE)) {
                    for (BigInteger z = a.z().min(b.z()); z.compareTo(a.z().max(b.z())) <= 0; z = z.add(BigInteger.ONE)) {
                        occupied.add(new ThreeDimensionalPoint(x, y, z));
                    }
                }
            }
            return occupied;
        }

        public HashSet<ThreeDimensionalPoint> getOccupiedSpace() {
            return occupiedSpace;
        }

        public Brick fall() {
            return this.fallX(BigInteger.ONE);
        }

        public Brick fallX(BigInteger x) {
            return new Brick(
                    new ThreeDimensionalPoint(a.x(), a.y(), a.z().subtract(x)),
                    new ThreeDimensionalPoint(b.x(), b.y(), b.z().subtract(x))
            );
        }

        public boolean isOnGround() {
            return a.z().equals(BigInteger.ONE) || b.z().equals(BigInteger.ONE);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Brick brick = (Brick) o;
            return Objects.equals(a, brick.a) && Objects.equals(b, brick.b);
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }

        @Override
        public String toString() {
            return "Brick{" +
                    "occupiedSpace=" + occupiedSpace +
                    ", a=" + a +
                    ", b=" + b +
                    '}';
        }

        @Override
        public int compareTo(Brick o) {
            return a.z().min(b.z()).compareTo(o.a.z().min(o.b.z()));
        }
    }

    public void allBricksFall() {
        HashMap<Pair, Integer> zLevel = new HashMap<>();
        List<Brick> newBricks = new ArrayList<>(bricks.size());
        for (Brick b : bricks.stream().sorted().toList()) {
            int z = Integer.MIN_VALUE;
            for (ThreeDimensionalPoint p : b.getOccupiedSpace()) {
                Pair pair = new Pair(p.x(), p.y());
                z = Integer.max(z, zLevel.getOrDefault(pair, 0));
            }
            z++;
            Brick fallen = b.fallX(b.a.z().min(b.b.z()).subtract(BigInteger.valueOf(z)));
            newBricks.add(fallen);
            for (ThreeDimensionalPoint p : b.getOccupiedSpace()) {
                Pair pair = new Pair(p.x(), p.y());
                zLevel.put(pair, z + (b.a.z().subtract(b.b.z())).abs().intValueExact());
            }
        }
        bricks = newBricks;
    }

    public HashSet<Brick> tryToDisintegrate(Brick brick) {
        HashSet<ThreeDimensionalPoint> occupied = new HashSet<>(isOccupiedBy.keySet());
        occupied.removeAll(brick.getOccupiedSpace());
        HashSet<Brick> disintegrated = new HashSet<>();
        HashSet<Brick> supportedBricks = (HashSet<Brick>) brick.directlySupporting.clone();

        while (!supportedBricks.isEmpty()) {
            Brick b = supportedBricks.iterator().next();
            supportedBricks.remove(b);

            Brick fallen = b.fall();
            HashSet<ThreeDimensionalPoint> others = (HashSet<ThreeDimensionalPoint>) occupied.clone();
            others.removeAll(b.getOccupiedSpace());
            others.retainAll(fallen.getOccupiedSpace());

            if (others.isEmpty()) {
                disintegrated.add(b);
                occupied.removeAll(b.getOccupiedSpace());
                supportedBricks.addAll(b.directlySupporting);
                supportedBricks.removeAll(disintegrated);
                if (disintegrationCount.containsKey(b)) {
                    disintegrated.addAll(disintegrationCount.get(b));
                    for (Brick other : disintegrationCount.get(b)) {
                        occupied.removeAll(other.getOccupiedSpace());
                        supportedBricks.addAll(other.directlySupporting);
                    }
                    supportedBricks.removeAll(disintegrationCount.get(b));
                }
            }
        }

        return disintegrated;
    }

    @Override
    public Object runPart1() {
        return disintegrationCount.values().stream().filter(HashSet::isEmpty).count();
    }

    @Override
    public Object runPart2() {
        return disintegrationCount.values().stream().map(HashSet::size).reduce(0, Integer::sum);
    }
}
