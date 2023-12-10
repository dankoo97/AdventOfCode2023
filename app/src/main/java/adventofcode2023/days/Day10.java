package adventofcode2023.days;

import adventofcode2023.util.Pair;

import java.io.InputStream;
import java.util.*;

public class Day10 extends AoCDay{
    public enum Direction {
        NORTH, EAST, SOUTH, WEST
    }
    Pair start;
    HashMap<Pair, String> map;
    ArrayList<Pair> loopPath;
    public Day10(InputStream file) {
        super(file);
        map = new HashMap<>();
        loopPath = new ArrayList<>();

        for (int y = 0; y < input.size(); y++) {
            for (int x = 0; x < input.get(y).length(); x++) {
                String value = input.get(y).substring(x, x+1);
                switch (value) {
                    case ".":
                        break;
                    case "S":
                        start = new Pair(x, y);
                    default:
                        map.put(new Pair(x, y), value);
                }
            }
        }
    }

    public int followLoop() {
        loopPath.add(start);

        for (Direction d: Direction.values()) {
            Pair adj = getFromDirection(start, d);
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
        Direction facing = null;

        for (Direction d: Direction.values()) {
            Pair adj = getFromDirection(start, d);
            if (adj.equals(loopPath.get(1))) {
                facing = d;
                break;
            }
        }

        for (Pair p : loopPath.subList(1, loopPath.size()-1)) {
            Direction prev = facing;
            Direction next = getNextDirection(facing, p);

            Pair[] adj = new Pair[]{
                    getFromDirection(p, Direction.NORTH),
                    getFromDirection(p, Direction.EAST),
                    getFromDirection(p, Direction.SOUTH),
                    getFromDirection(p, Direction.WEST)
            };

            if (prev == Direction.NORTH && next == Direction.NORTH) {
                left.add(adj[Direction.WEST.ordinal()]);
                right.add(adj[Direction.EAST.ordinal()]);
            } else if (prev == Direction.NORTH && next == Direction.EAST) {
                left.add(adj[Direction.NORTH.ordinal()]);
                left.add(adj[Direction.WEST.ordinal()]);
            } else if (prev == Direction.NORTH && next == Direction.WEST) {
                right.add(adj[Direction.NORTH.ordinal()]);
                right.add(adj[Direction.EAST.ordinal()]);
            } else if (prev == Direction.EAST && next == Direction.EAST) {
                left.add(adj[Direction.NORTH.ordinal()]);
                right.add(adj[Direction.SOUTH.ordinal()]);
            } else if (prev == Direction.EAST && next == Direction.SOUTH) {
                left.add(adj[Direction.EAST.ordinal()]);
                left.add(adj[Direction.NORTH.ordinal()]);
            } else if (prev == Direction.EAST && next == Direction.NORTH) {
                right.add(adj[Direction.EAST.ordinal()]);
                right.add(adj[Direction.SOUTH.ordinal()]);
            } else if (prev == Direction.SOUTH && next == Direction.SOUTH) {
                left.add(adj[Direction.EAST.ordinal()]);
                right.add(adj[Direction.WEST.ordinal()]);
            } else if (prev == Direction.SOUTH && next == Direction.WEST) {
                left.add(adj[Direction.SOUTH.ordinal()]);
                left.add(adj[Direction.EAST.ordinal()]);
            } else if (prev == Direction.SOUTH && next == Direction.EAST) {
                right.add(adj[Direction.SOUTH.ordinal()]);
                right.add(adj[Direction.WEST.ordinal()]);
            } else if (prev == Direction.WEST && next == Direction.WEST) {
                left.add(adj[Direction.SOUTH.ordinal()]);
                right.add(adj[Direction.NORTH.ordinal()]);
            } else if (prev == Direction.WEST && next == Direction.NORTH) {
                left.add(adj[Direction.WEST.ordinal()]);
                left.add(adj[Direction.SOUTH.ordinal()]);
            } else if (prev == Direction.WEST && next == Direction.SOUTH) {
                right.add(adj[Direction.WEST.ordinal()]);
                right.add(adj[Direction.NORTH.ordinal()]);
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

    public void getNextNode(ArrayList<Pair> direction) {
        Pair last = direction.get(direction.size()-1);
        Pair prevLast = direction.get(direction.size()-2);
        ArrayList<Pair> adj = new ArrayList<>(connected(last));
        adj.remove(prevLast);
        direction.add(adj.get(0));
    }

    public List<Pair> connected(Pair p) {
        return mapStringToDirections(map.get(p)).stream().map((d -> getFromDirection(p, d))).toList();
    }

    public List<Direction> mapStringToDirections(String s) {
        return switch (s) {
            case "F" -> Arrays.stream(new Direction[]{Direction.SOUTH, Direction.EAST}).toList();
            case "J" -> Arrays.stream(new Direction[]{Direction.WEST, Direction.NORTH}).toList();
            case "|" -> Arrays.stream(new Direction[]{Direction.NORTH, Direction.SOUTH}).toList();
            case "-" -> Arrays.stream(new Direction[]{Direction.EAST, Direction.WEST}).toList();
            case "7" -> Arrays.stream(new Direction[]{Direction.WEST, Direction.SOUTH}).toList();
            case "L" -> Arrays.stream(new Direction[]{Direction.NORTH, Direction.EAST}).toList();
            default -> new ArrayList<>();
        };
    }

    public Direction getNextDirection(Direction d, Pair p) {
        ArrayList<Direction> directions = new ArrayList<>(mapStringToDirections(map.get(p)));
        switch (d) {
            case NORTH -> directions.remove(Direction.SOUTH);
            case EAST -> directions.remove(Direction.WEST);
            case SOUTH -> directions.remove(Direction.NORTH);
            case WEST -> directions.remove(Direction.EAST);
        }
        return directions.get(0);
    }

    public boolean isConnected(Pair p1, Pair p2) {
        return connected(p1).contains(p2);
    }

    public boolean isOutside(Pair p) {
        return p.getX() < 0 ||
                p.getY() < 0 ||
                p.getY() > input.size() ||
                p.getX() > input.get(0).length();
    }

    public HashSet<Pair> floodSearch(HashSet<Pair> candidates, HashSet<Pair> visited) {
        while (!candidates.isEmpty()) {
            Pair curr = candidates.iterator().next();
            visited.add(curr);
            candidates.remove(curr);

            for (Direction d : Direction.values()) {
                Pair adj = getFromDirection(curr, d);
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

    public static Pair getFromDirection(Pair p, Direction d) {
        return switch (d) {
            case NORTH -> new Pair(p.getX(), p.getY() - 1);
            case EAST -> new Pair(p.getX() + 1, p.getY());
            case SOUTH -> new Pair(p.getX(), p.getY() + 1);
            case WEST -> new Pair(p.getX() - 1, p.getY());
        };
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
