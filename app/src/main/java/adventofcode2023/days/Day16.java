package adventofcode2023.days;

import adventofcode2023.util.Pair;

import java.io.InputStream;
import java.util.*;

public class Day16 extends AoCDay {
    public record BeamParticle(Pair pos, Direction d) {

        @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                BeamParticle that = (BeamParticle) o;
                return Objects.equals(pos, that.pos) && d == that.d;
            }

        @Override
            public String toString() {
                return "BeamParticle{" +
                        "pos=" + pos +
                        ", d=" + d +
                        '}';
            }
        }
    public enum Direction {
        NORTH, EAST, SOUTH, WEST
    }
    HashMap<Pair, Character> map;
    HashSet<Pair> energized;
    List<BeamParticle> beams;
    HashSet<BeamParticle> visited;
    public Day16(InputStream file) {
        super(file);
        map = new HashMap<>();
        beams = new ArrayList<>();
        energized = new HashSet<>();
        visited = new HashSet<>();

        for (int y = 0; y < input.size(); y++) {
            for (int x = 0; x < input.get(y).length(); x++) {
                char c = input.get(y).charAt(x);
                if (c != '.') map.put(new Pair(x, y), c);
            }
        }
    }

    public HashSet<BeamParticle> step(BeamParticle bp) {
        energized.add(bp.pos);
        HashSet<BeamParticle> particleSet = new HashSet<>();

        Pair newPos = null;
        switch (bp.d) {
            case NORTH -> newPos = new Pair(bp.pos.getX(), bp.pos.getY()-1);
            case EAST -> newPos = new Pair(bp.pos.getX()+1, bp.pos.getY());
            case SOUTH -> newPos = new Pair(bp.pos.getX(), bp.pos.getY()+1);
            case WEST -> newPos = new Pair(bp.pos.getX()-1, bp.pos.getY());
        }

        if (isOutside(newPos)) {
            return particleSet;
        }

        BeamParticle next = new BeamParticle(newPos, bp.d);

        if (map.containsKey(newPos)) {
            char c = map.get(newPos);
            if (c == '|' || c == '-') {
                for (BeamParticle other : splitBeam(next)) {
                    if (!visited.contains(other)) {
                        visited.add(other);
                        particleSet.add(other);
                    }
                }
                return particleSet;
            } else if (c == '\\' || c == '/') {
                BeamParticle other = reflectBeam(next);
                if (!visited.contains(other)) {
                    visited.add(other);
                    particleSet.add(other);
                }
                return particleSet;
            }
        }
        if (!visited.contains(next)) {
            visited.add(next);
            particleSet.add(next);
        }
        return particleSet;
    }

    public BeamParticle reflectBeam(BeamParticle bp) {
        char c = map.get(bp.pos);
        return switch (bp.d) {
            case NORTH -> new BeamParticle(bp.pos, c == '/' ? Direction.EAST : Direction.WEST);
            case EAST -> new BeamParticle(bp.pos, c == '/' ? Direction.NORTH : Direction.SOUTH);
            case SOUTH -> new BeamParticle(bp.pos, c == '/' ? Direction.WEST : Direction.EAST);
            case WEST -> new BeamParticle(bp.pos, c == '/' ? Direction.SOUTH : Direction.NORTH);
        };
    }

    public BeamParticle[] splitBeam(BeamParticle bp) {
        char c = map.get(bp.pos);
        if (c == '|' && (bp.d == Direction.EAST || bp.d == Direction.WEST)) {
            return new BeamParticle[]{
                    new BeamParticle(bp.pos, Direction.NORTH),
                    new BeamParticle(bp.pos, Direction.SOUTH)
            };
        } else if (c == '-' && (bp.d == Direction.NORTH || bp.d == Direction.SOUTH)) {
            return new BeamParticle[]{
                    new BeamParticle(bp.pos, Direction.EAST),
                    new BeamParticle(bp.pos, Direction.WEST)
            };
        }
        return new BeamParticle[]{bp};
    }

    public boolean isOutside(Pair pos) {
        return pos.getX() < 0 || pos.getY() < 0 ||
                pos.getY() >= input.size() ||
                pos.getX() >= input.get(pos.getY()).length();
    }

    public String toEnergizedMap() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < input.size(); y++) {
            for (int x = 0; x < input.get(y).length(); x++) {
                if (energized.contains(new Pair(x, y))) {
                    sb.append('#');
                } else {
                    sb.append('.');
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public Object runPart1() {
        // At x=0, y=0 is a reflector
        BeamParticle start = new BeamParticle(new Pair(-1, 0), Direction.EAST);
        runFromStart(start);

        energized.remove(start.pos);
        return energized.size();
    }

    @Override
    public Object runPart2() {
        reset();
        long maxVal = 0;

        for (int i = 0; i < input.size(); i++) {
            for (Direction d : Direction.values()) {
                BeamParticle start = null;
                switch (d) {
                    case NORTH -> start = new BeamParticle(new Pair(i, input.size()), d);
                    case EAST -> start = new BeamParticle(new Pair(0, i), d);
                    case SOUTH -> start = new BeamParticle(new Pair(i, 0), d);
                    case WEST -> start = new BeamParticle(new Pair(input.size(), i), d);
                }

                runFromStart(start);
                energized.remove(start.pos);
                maxVal = Math.max(maxVal, energized.size());
                reset();
            }
        }
        return maxVal;
    }

    public void runFromStart(BeamParticle start) {
        beams.add(start);
        visited.add(start);

        while (!beams.isEmpty()) {
            BeamParticle bp = beams.get(0);
            beams.remove(0);

            HashSet<BeamParticle> newBeams = step(bp);
            beams.addAll(newBeams);
        }
    }

    public void reset() {
        beams = new ArrayList<>();
        visited = new HashSet<>();
        energized = new HashSet<>();
    }
}
