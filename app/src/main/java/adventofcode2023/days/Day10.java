package adventofcode2023.days;

import adventofcode2023.util.Direction.Cardinal;
import adventofcode2023.util.Pair;

import java.io.InputStream;
import java.util.*;

import static adventofcode2023.util.Direction.moveOne;

public class Day10 extends AoCDay{
    Pair start;
    HashMap<Pair, Character> map;
    ArrayList<Pair> loopPath;
    public Day10(InputStream file) {
        super(file);
        map = new HashMap<>();
        loopPath = new ArrayList<>();

        for (int y = 0; y < input.size(); y++) {
            for (int x = 0; x < input.get(y).length(); x++) {
                char value = input.get(y).charAt(x);
                switch (value) {
                    case '.':
                        break;
                    case 'S':
                        start = Pair.fromLong(x, y);
                    default:
                        map.put(Pair.fromLong(x, y), value);
                }
            }
        }
    }

    public int followLoop() {
        loopPath.add(start);

        for (Cardinal d: Cardinal.values()) {
            Pair adj = moveOne(start, d);
            if (map.containsKey(adj) && isConnected(adj, start)) {
                loopPath.add(adj);
                break;
            }
        }

        while (!loopPath.get(loopPath.size()-1).equals(start)) {
            getNextNode(loopPath);
        }

        return loopPath.size() >> 1;
    }

    public int countInside() {
        HashSet<Pair> left = new HashSet<>(), right = new HashSet<>();
        HashSet<Pair> loopMembers = new HashSet<>(loopPath);
        Cardinal facing = null;

        for (Cardinal d: Cardinal.values()) {
            Pair adj = moveOne(start, d);
            if (adj.equals(loopPath.get(1))) {
                facing = d;
                break;
            }
        }

        for (Pair p : loopPath.subList(1, loopPath.size()-1)) {
            Cardinal prev = facing;
            assert facing != null;
            Cardinal next = getNextCardinal(facing, p);

            Pair[] adj = new Pair[]{
                    moveOne(p, Cardinal.NORTH),
                    moveOne(p, Cardinal.EAST),
                    moveOne(p, Cardinal.SOUTH),
                    moveOne(p, Cardinal.WEST)
            };

            if (prev == Cardinal.NORTH && next == Cardinal.NORTH) {
                left.add(adj[Cardinal.WEST.ordinal()]);
                right.add(adj[Cardinal.EAST.ordinal()]);
            } else if (prev == Cardinal.NORTH && next == Cardinal.EAST) {
                left.add(adj[Cardinal.NORTH.ordinal()]);
                left.add(adj[Cardinal.WEST.ordinal()]);
            } else if (prev == Cardinal.NORTH && next == Cardinal.WEST) {
                right.add(adj[Cardinal.NORTH.ordinal()]);
                right.add(adj[Cardinal.EAST.ordinal()]);
            } else if (prev == Cardinal.EAST && next == Cardinal.EAST) {
                left.add(adj[Cardinal.NORTH.ordinal()]);
                right.add(adj[Cardinal.SOUTH.ordinal()]);
            } else if (prev == Cardinal.EAST && next == Cardinal.SOUTH) {
                left.add(adj[Cardinal.EAST.ordinal()]);
                left.add(adj[Cardinal.NORTH.ordinal()]);
            } else if (prev == Cardinal.EAST && next == Cardinal.NORTH) {
                right.add(adj[Cardinal.EAST.ordinal()]);
                right.add(adj[Cardinal.SOUTH.ordinal()]);
            } else if (prev == Cardinal.SOUTH && next == Cardinal.SOUTH) {
                left.add(adj[Cardinal.EAST.ordinal()]);
                right.add(adj[Cardinal.WEST.ordinal()]);
            } else if (prev == Cardinal.SOUTH && next == Cardinal.WEST) {
                left.add(adj[Cardinal.SOUTH.ordinal()]);
                left.add(adj[Cardinal.EAST.ordinal()]);
            } else if (prev == Cardinal.SOUTH && next == Cardinal.EAST) {
                right.add(adj[Cardinal.SOUTH.ordinal()]);
                right.add(adj[Cardinal.WEST.ordinal()]);
            } else if (prev == Cardinal.WEST && next == Cardinal.WEST) {
                left.add(adj[Cardinal.SOUTH.ordinal()]);
                right.add(adj[Cardinal.NORTH.ordinal()]);
            } else if (prev == Cardinal.WEST && next == Cardinal.NORTH) {
                left.add(adj[Cardinal.WEST.ordinal()]);
                left.add(adj[Cardinal.SOUTH.ordinal()]);
            } else if (prev == Cardinal.WEST && next == Cardinal.SOUTH) {
                right.add(adj[Cardinal.WEST.ordinal()]);
                right.add(adj[Cardinal.NORTH.ordinal()]);
            }
            facing = next;
        }

        loopPath.forEach(left::remove);
        loopPath.forEach(right::remove);
        left = floodSearch(left, new HashSet<>(loopMembers));
        right = floodSearch(right, new HashSet<>(loopMembers));

        loopPath.forEach(left::remove);
        loopPath.forEach(right::remove);

        if (left.stream().anyMatch(this::isOutside)) {
            return right.size();
        }

        return left.size();
    }

    public void getNextNode(ArrayList<Pair> cardinals) {
        Pair last = cardinals.get(cardinals.size()-1);
        Pair prevLast = cardinals.get(cardinals.size()-2);
        ArrayList<Pair> adj = new ArrayList<>(connected(last));
        adj.remove(prevLast);
        cardinals.add(adj.get(0));
    }

    public List<Pair> connected(Pair p) {
        return mapStringToCardinals(map.get(p)).stream().map((d -> moveOne(p, d))).toList();
    }

    public List<Cardinal> mapStringToCardinals(Character c) {
        return switch (c) {
            case 'F' -> Arrays.stream(new Cardinal[]{Cardinal.SOUTH, Cardinal.EAST}).toList();
            case 'J' -> Arrays.stream(new Cardinal[]{Cardinal.WEST, Cardinal.NORTH}).toList();
            case '|' -> Arrays.stream(new Cardinal[]{Cardinal.NORTH, Cardinal.SOUTH}).toList();
            case '-' -> Arrays.stream(new Cardinal[]{Cardinal.EAST, Cardinal.WEST}).toList();
            case '7' -> Arrays.stream(new Cardinal[]{Cardinal.WEST, Cardinal.SOUTH}).toList();
            case 'L' -> Arrays.stream(new Cardinal[]{Cardinal.NORTH, Cardinal.EAST}).toList();
            default -> new ArrayList<>();
        };
    }

    public Cardinal getNextCardinal(Cardinal d, Pair p) {
        ArrayList<Cardinal> cardinals = new ArrayList<>(mapStringToCardinals(map.get(p)));
        switch (d) {
            case NORTH -> cardinals.remove(Cardinal.SOUTH);
            case EAST -> cardinals.remove(Cardinal.WEST);
            case SOUTH -> cardinals.remove(Cardinal.NORTH);
            case WEST -> cardinals.remove(Cardinal.EAST);
        }
        return cardinals.get(0);
    }

    public boolean isConnected(Pair p1, Pair p2) {
        return connected(p1).contains(p2);
    }

    public boolean isOutside(Pair p) {
        return p.x().intValue() < 0 ||
                p.y().intValue() < 0 ||
                p.y().intValue() >= input.size() ||
                p.x().intValue() >= input.get(p.y().intValue()).length();
    }

    public HashSet<Pair> floodSearch(HashSet<Pair> candidates, HashSet<Pair> visited) {
        while (!candidates.isEmpty()) {
            Pair curr = candidates.iterator().next();
            visited.add(curr);
            candidates.remove(curr);

            for (Cardinal d : Cardinal.values()) {
                Pair adj = moveOne(curr, d);
                if (visited.contains(adj)) {
                    continue;
                }
                candidates.add(adj);
                if (isOutside(adj)) {
                    visited.add(adj);
                    return visited;
                }
            }
        }
        return visited;
    }

    @Override
    public Object runPart1() {
        return followLoop();
    }

    @Override
    public Object runPart2() {
        return countInside();
    }
}
