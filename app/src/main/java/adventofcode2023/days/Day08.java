package adventofcode2023.days;

import org.w3c.dom.Node;

import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

public class Day08 extends AoCDay {
    public enum Direction {
        LEFT, RIGHT
    }
    ArrayList<Direction> directions;
    HashMap<String, String[]> path;

    public Day08(InputStream file) {
        super(file);

        directions = new ArrayList<>();
        for (int i = 0; i < input.get(0).length(); i++) {
            String token = input.get(0).substring(i, i+1);
            switch (token) {
                case "L" -> directions.add(Direction.LEFT);
                case "R" -> directions.add(Direction.RIGHT);
            }
        }

        path = new HashMap<>();
        for (int i = 2; i < input.size(); i++) {
            String node = input.get(i).substring(0, 3);
            String left = input.get(i).substring(7, 10);
            String right = input.get(i).substring(12, 15);
            path.put(node, new String[]{left, right});
        }
    }

    public static class NodePath {
        ArrayList<String> path;
        public static ArrayList<Direction> directions;
        HashMap<VisitedNode, BigInteger> visitedNodes;
        public static HashMap<String, String[]> nodeMap = new HashMap<>();

        public static class VisitedNode implements Serializable {
            String node;
            int directionsStep;

            public VisitedNode(String node, int directionStep) {
                this.node = node;
                this.directionsStep = directionStep;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                VisitedNode that = (VisitedNode) o;
                return directionsStep == that.directionsStep && Objects.equals(node, that.node);
            }

            @Override
            public int hashCode() {
                return Objects.hash(node, directionsStep);
            }
        }
        public NodePath(String start) {
            path = new ArrayList<>();
            path.add(start);
            visitedNodes = new HashMap<>();
            visitedNodes.put(new VisitedNode(start, 0), BigInteger.ZERO);
        }

        public void step() {
            String currentNode = path.get(path.size()-1);
            Direction d = directions.get((path.size()-1) % directions.size());
            String nextNode = nodeMap.get(currentNode)[d.ordinal()];
            path.add(nextNode);
        }

        public BigInteger runUntilNodeMatches(String regex) {
            while (!path.get(path.size()-1).matches(regex)) {
                step();
            }
            return BigInteger.valueOf(path.size()-1);
        }


        public BigInteger runUntilCycle() {
            while (true) {
                step();
                VisitedNode visitedNode = new VisitedNode(getLast(), (path.size()-1) % directions.size());
                if (visitedNodes.containsKey(visitedNode)) {
                    return visitedNodes.get(visitedNode);
                }
                visitedNodes.put(visitedNode, BigInteger.valueOf(path.size()-1));
            }
        }

        public String getLast() {
            return path.get(path.size()-1);
        }

        public BigInteger findAllMatchingBetween(String regex, int start, int end) {
            for (int i = start; i < end; i++) {
                if (path.get(i).matches(regex)) {
                    return BigInteger.valueOf(i);
                }
            }
            return BigInteger.ZERO;
        }
    }

    @Override
    public Object runPart1() {
        NodePath nodePath = new NodePath("AAA");
        NodePath.directions = this.directions;
        NodePath.nodeMap = this.path;

        // Number of steps, not number of nodes visited
//        return nodePath.runUntilNodeMatches("ZZZ");
        return null;
    }

    @Override
    public Object runPart2() {
        List<NodePath> nodePaths = new ArrayList<>(path.keySet().stream().filter((node) -> node.matches("..A")).map(NodePath::new).toList());
        BigInteger multiplier = BigInteger.ONE;
        BigInteger multiplierCycleSize = BigInteger.ONE;
        for (NodePath node : nodePaths) {
            BigInteger cycleStart = node.runUntilCycle().subtract(BigInteger.ONE);
            BigInteger placeInCycle = node.findAllMatchingBetween("..Z", cycleStart.intValueExact(), node.path.size());
            BigInteger cycleSize = BigInteger.valueOf(node.path.size()).subtract(cycleStart);
            if (multiplier.equals(BigInteger.ONE)) {
                multiplier = placeInCycle;
                multiplierCycleSize = cycleSize;
            } else {
                multiplier.modInverse()
            }

        }

        return multiplier;
    }
}
