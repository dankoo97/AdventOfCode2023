package adventofcode2023.days;

import adventofcode2023.util.Direction;
import adventofcode2023.util.Pair;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;

import static adventofcode2023.util.Direction.move;

public class Day18 extends AoCDay {
    Pair pos;
    TreeMap<BigInteger, PriorityQueue<BigInteger>> xValues;

    BigInteger minX, minY, maxX, maxY;

    public record Instruction(Direction.Cardinal direction, BigInteger steps) {}

    public Instruction readPlain(String line) {
        String[] tokens = line.split(" ");
        Direction.Cardinal d = switch (tokens[0]) {
            case "U" -> Direction.Cardinal.NORTH;
            case "R" -> Direction.Cardinal.EAST;
            case "D" -> Direction.Cardinal.SOUTH;
            case "L" -> Direction.Cardinal.WEST;
            default -> throw new IllegalStateException("Unexpected value: " + tokens[0]);
        };
        BigInteger steps = new BigInteger(tokens[1]);
        return new Instruction(d, steps);
    }

    public Instruction readColor(String line) {
        String color = line.split(" ")[2];
        BigInteger steps = new BigInteger(color.substring(2, 7), 16);
        Direction.Cardinal d = switch (color.charAt(7)) {
            case '0' -> Direction.Cardinal.EAST;
            case '1' -> Direction.Cardinal.SOUTH;
            case '2' -> Direction.Cardinal.WEST;
            case '3' -> Direction.Cardinal.NORTH;
            default -> throw new IllegalStateException("Unexpected value: " + color.charAt(6));
        };
        return new Instruction(d, steps);
    }

    public Day18(InputStream file) {
        super(file);

        pos = Pair.ORIGIN;
        xValues = new TreeMap<>();
    }

    public BigInteger digTrench(boolean isColor) {
        BigInteger total = BigInteger.ZERO;

        for (String line : input) {
            Instruction instruction;
            if (!isColor) {
                instruction = readPlain(line);
            } else {
                instruction = readColor(line);
            }
            Pair next = move(pos, instruction.direction, instruction.steps);

            total = total.add(pos.getDistance(next));
            total = total.add(pos.x().multiply(next.y())).subtract(next.x().multiply(pos.y()));

            pos = next;
        }

        total = total.add(pos.x().subtract(pos.y())).shiftRight(1);

        // Origin is not included, so add one
        return total.add(BigInteger.ONE);
    }

    public void reset() {
        pos = Pair.ORIGIN;
        xValues = new TreeMap<>();

        minX = BigInteger.ZERO;
        minY = BigInteger.ZERO;
        maxX = BigInteger.ZERO;
        maxY = BigInteger.ZERO;
    }

    @Override
    public Object runPart1() {
        reset();
        return digTrench(false);
    }

    @Override
    public Object runPart2() {
        reset();
        return digTrench(true);
    }
}
