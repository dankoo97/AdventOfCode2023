package adventofcode2023.days;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Day06 extends AoCDay{
    List<BigInteger> time;
    List<BigInteger> dist;
    public Day06(InputStream file) {
        super(file);
        time = new ArrayList<>();
        dist = new ArrayList<>();

        for (String token : input.get(0).split("\\s")) {
            try {
                time.add(new BigInteger(token));
            } catch (NumberFormatException ignored) {
            }
        }

        for (String token : input.get(1).split("\\s")) {
            try {
                dist.add(new BigInteger(token));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public BigInteger getDistanceForTimeButtonHeld(BigInteger time, BigInteger totalTime) {
        assert time.compareTo(totalTime) <= 0;
        assert time.compareTo(BigInteger.ZERO) >= 0;
        return time.multiply(totalTime.subtract(time));
    }

    public BigInteger guessBestTime(BigInteger totalTime) {
        return totalTime.shiftRight(1);
    }

    public BigInteger smashNum(String input) {
        return new BigInteger(input.replaceAll("\\D", ""));
    }

    @Override
    public Object runPart1() {
        BigInteger total = BigInteger.ONE;
        for (int i = 0; i < time.size(); i++) {
            BigInteger count = BigInteger.ZERO;
            BigInteger initialGuess = guessBestTime(time.get(i));
            BigInteger j = initialGuess;
            while (j.compareTo(time.get(i)) <= 0 && getDistanceForTimeButtonHeld(j, time.get(i)).compareTo(dist.get(i)) > 0) {
                j = j.add(BigInteger.ONE);
                count = count.add(BigInteger.ONE);
            }

            j = initialGuess.subtract(BigInteger.ONE);
            while (j.compareTo(BigInteger.ZERO) >= 0 && getDistanceForTimeButtonHeld(j, time.get(i)).compareTo(dist.get(i)) > 0) {
                j = j.subtract(BigInteger.ONE);
                count = count.add(BigInteger.ONE);
            }

            total = total.multiply(count);
        }
        return total;
    }

    @Override
    public Object runPart2() {
        BigInteger t = smashNum(input.get(0));
        BigInteger d = smashNum(input.get(1));

        BigInteger total = BigInteger.ONE;

        // Binary search
        BigInteger initialGuess = guessBestTime(t);

        // Lower bound
        BigInteger bottom = BigInteger.ZERO, top = initialGuess;
        while (top.subtract(bottom).compareTo(BigInteger.TWO) > 0) {
            BigInteger medium = bottom.add(top).shiftRight(1);
            if (getDistanceForTimeButtonHeld(medium, t).compareTo(d) > 0) {
                top = medium;
            } else {
                bottom = medium;
            }
        }

        for (BigInteger i = bottom; i.compareTo(top) <= 0; i = i.add(BigInteger.ONE)) {
            if (getDistanceForTimeButtonHeld(i, t).compareTo(d) > 0) {
                total = total.add(initialGuess.subtract(i));
                break;
            }
        }

        bottom = initialGuess;
        top = t;
        while (top.subtract(bottom).compareTo(BigInteger.TWO) > 0) {
            BigInteger medium = bottom.add(top).shiftRight(1);
            if (getDistanceForTimeButtonHeld(medium, t).compareTo(d) < 0) {
                top = medium;
            } else {
                bottom = medium;
            }
        }

        for (BigInteger i = top; i.compareTo(bottom) >= 0; i = i.subtract(BigInteger.ONE)) {
            if (getDistanceForTimeButtonHeld(i, t).compareTo(d) > 0) {
                total = total.add(i.subtract(initialGuess));
                break;
            }
        }


        return total;
    }
}
