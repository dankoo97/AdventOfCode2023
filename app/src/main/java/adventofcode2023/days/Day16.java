package adventofcode2023.days;

import adventofcode2023.util.Pair;
import adventofcode2023.util.Direction.Cardinal;

import java.io.InputStream;
import java.util.*;

import static adventofcode2023.util.Direction.moveOne;
import static adventofcode2023.util.Direction.readCharMap;

public class Day16 extends AoCDay {
    public record BeamParticle(Pair pos, Cardinal d) {

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
    HashMap<Pair, Character> map;
    HashSet<Pair> energized;
    List<BeamParticle> beams;
    HashSet<BeamParticle> visited;
    public Day16(InputStream file) {
        super(file);
        beams = new ArrayList<>();
        energized = new HashSet<>();
        visited = new HashSet<>();

        map = readCharMap(input, List.of('.'));
    }

    public HashSet<BeamParticle> step(BeamParticle bp) {
        energized.add(bp.pos);
        HashSet<BeamParticle> particleSet = new HashSet<>();

        Pair newPos = moveOne(bp.pos, bp.d);

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
            case NORTH -> new BeamParticle(bp.pos, c == '/' ? Cardinal.EAST : Cardinal.WEST);
            case EAST -> new BeamParticle(bp.pos, c == '/' ? Cardinal.NORTH : Cardinal.SOUTH);
            case SOUTH -> new BeamParticle(bp.pos, c == '/' ? Cardinal.WEST : Cardinal.EAST);
            case WEST -> new BeamParticle(bp.pos, c == '/' ? Cardinal.SOUTH : Cardinal.NORTH);
        };
    }

    public BeamParticle[] splitBeam(BeamParticle bp) {
        char c = map.get(bp.pos);
        if (c == '|' && (bp.d == Cardinal.EAST || bp.d == Cardinal.WEST)) {
            return new BeamParticle[]{
                    new BeamParticle(bp.pos, Cardinal.NORTH),
                    new BeamParticle(bp.pos, Cardinal.SOUTH)
            };
        } else if (c == '-' && (bp.d == Cardinal.NORTH || bp.d == Cardinal.SOUTH)) {
            return new BeamParticle[]{
                    new BeamParticle(bp.pos, Cardinal.EAST),
                    new BeamParticle(bp.pos, Cardinal.WEST)
            };
        }
        return new BeamParticle[]{bp};
    }

    public boolean isOutside(Pair pos) {
        return pos.x() < 0 || pos.y() < 0 ||
                pos.y() >= input.size() ||
                pos.x() >= input.get(pos.y()).length();
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
        BeamParticle start = new BeamParticle(new Pair(-1, 0), Cardinal.EAST);
        runFromStart(start);

        energized.remove(start.pos);
        return energized.size();
    }

    @Override
    public Object runPart2() {
        reset();
        long maxVal = 0;

        for (int i = 0; i < input.size(); i++) {
            for (Cardinal d : Cardinal.values()) {
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
