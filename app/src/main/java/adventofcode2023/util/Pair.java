package adventofcode2023.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pair {
    int x, y;

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return x == pair.x && y == pair.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
            return String.format("%s, %s", x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<Pair> getAdjacent() {
        List<Pair> adjacent = new ArrayList<>();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i != 0 || j != 0) {
                    adjacent.add(new Pair(x + i, y + j));
                }
            }
        }
        return adjacent;
    }
}
