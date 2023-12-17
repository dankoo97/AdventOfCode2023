package adventofcode2023.days;

import adventofcode2023.util.Direction.OneDimensionalDirection;

import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

public class Day08 extends AoCDay {
    ArrayList<OneDimensionalDirection> directions;
    HashMap<String, String[]> path;

    public Day08(InputStream file) {
        super(file);

        directions = new ArrayList<>();
        for (int i = 0; i < input.get(0).length(); i++) {
            String token = input.get(0).substring(i, i+1);
            switch (token) {
                case "L" -> directions.add(OneDimensionalDirection.LEFT);
                case "R" -> directions.add(OneDimensionalDirection.RIGHT);
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
        public static ArrayList<OneDimensionalDirection> directions;
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

            @Override
            public String toString() {
                return String.format("VisitedNode(node=%s, directionStep=%s)", node, directionsStep);
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
            OneDimensionalDirection d = directions.get((path.size()-1) % directions.size());
            String nextNode = nodeMap.get(currentNode)[d.ordinal()];
            path.add(nextNode);
        }

        public BigInteger runUntilNodeMatches(String regex) {
            while (!path.get(path.size()-1).matches(regex)) {
                step();
            }
            return BigInteger.valueOf(path.size()-1);
        }

        public void runXSteps(BigInteger x) {
            for (int i = 0; i < x.intValueExact(); i++) {
                step();
            }
        }


        public BigInteger runUntilCycle() {
            while (true) {
                step();
                VisitedNode visitedNode = new VisitedNode(getLast(), (path.size()-1) % directions.size());
                if (visitedNodes.containsKey(visitedNode)) {
                    System.out.printf("%s %s\n", visitedNode, visitedNodes.get(visitedNode));
                    System.out.println(path.subList(0, visitedNodes.get(visitedNode).intValue() + 5));
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
        NodePath.directions = this.directions;
        NodePath.nodeMap = this.path;
        NodePath nodePath = new NodePath("AAA");
        // Number of steps, not number of nodes visited
        return nodePath.runUntilNodeMatches("ZZZ");
//        return null;
    }

    @Override
    public Object runPart2() {
        List<NodePath> nodePaths = new ArrayList<>(path.keySet().stream().filter((node) -> node.matches("..A")).map(NodePath::new).toList());
        ArrayList<BigInteger> start = new ArrayList<>();
        ArrayList<BigInteger> offsets = new ArrayList<>();
        ArrayList<BigInteger> cycles = new ArrayList<>();
        for (NodePath node : nodePaths) {
            BigInteger cycleStart = node.runUntilCycle().subtract(BigInteger.ONE);
            BigInteger placeInCycle = node.findAllMatchingBetween("..Z", cycleStart.intValueExact(), node.path.size());
            BigInteger cycleSize = BigInteger.valueOf(node.path.size()).subtract(cycleStart);

//            node.runXSteps(cycleSize);
            System.out.println(node.path.subList(node.path.size()-20, node.path.size()));

            start.add(cycleStart);
            // Directions size is 281, so we want the number of direction cycles
            offsets.add(placeInCycle.divide(BigInteger.valueOf(281)));
            cycles.add(cycleSize);
        }

        BigInteger multiplier = BigInteger.ONE;
        for (BigInteger i : offsets) {
            multiplier = multiplier.multiply(i);
        }

        BigInteger sum = BigInteger.ZERO;
        for (int i = 0; i < offsets.size(); i++) {
            BigInteger y = multiplier.divide(offsets.get(i));
            BigInteger z = y.modInverse(offsets.get(i));
            sum = sum.add(cycles.get(i).multiply(y).multiply(z));
        }

        System.out.println(start);
        System.out.println(offsets);
        System.out.println(cycles);

        System.out.println(sum);
        System.out.println(multiplier);
        return sum.mod(multiplier.multiply(BigInteger.valueOf(281 * 6)));
    }
}
