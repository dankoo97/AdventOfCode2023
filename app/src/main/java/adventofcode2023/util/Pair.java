package adventofcode2023.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Pair(BigInteger x, BigInteger y) {

    public static final Pair ORIGIN = new Pair(BigInteger.ZERO, BigInteger.ZERO);
    public static Pair fromLong(long x, long y) {
        return new Pair(BigInteger.valueOf(x), BigInteger.valueOf(y));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return Objects.equals(x, pair.x) && Objects.equals(y, pair.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("Pair(x=%s, y=%s)", x, y);
    }

    public List<Pair> getAdjacent() {
        List<Pair> adjacent = new ArrayList<>();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i != 0 || j != 0) {
                    adjacent.add(new Pair(x.add(BigInteger.valueOf(i)), y.add(BigInteger.valueOf(j))));
                }
            }
        }
        return adjacent;
    }

    public BigInteger getDistance(Pair other) {
        return x.subtract(other.x).abs().add(y.subtract(other.y).abs());
    }
}
