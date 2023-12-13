package adventofcode2023.days;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Day13 extends AoCDay {

    List<List<String>> patterns;

    public Day13(InputStream file) {
        super(file);
        patterns = new ArrayList<>();
        patterns.add(new ArrayList<>());
        for (String line : input) {
            if (line.isBlank()) {
                patterns.add(new ArrayList<>());
            } else {
                patterns.get(patterns.size() - 1).add(line);
            }
        }
    }

    public boolean reflectsAcrossRow(int row, List<String> pattern) {
        for (int i = 0; row + i + 1 < pattern.size() && row - i >= 0; i++) {
            if (!pattern.get(row + i + 1).equals(pattern.get(row-i))) return false;
        }
        return true;
    }

    public boolean almostReflectsAcrossRow(int row, List<String> pattern) {
        boolean mismatch = false;
        for (int i = 0; row + i + 1 < pattern.size() && row - i >= 0; i++) {
            for (int j = 0; j < pattern.get(row).length(); j++) {
                if (pattern.get(row + i + 1).charAt(j) != pattern.get(row-i).charAt(j)) {
                    if (!mismatch) {
                        mismatch = true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return mismatch;
    }

    public boolean reflectAcrossCol(int col, List<String> pattern) {
        for (int i = 0; i + col + 1 < pattern.get(0).length() && col - i >= 0; i++) {
            for (String s : pattern) {
                if (s.charAt(col + i + 1) != s.charAt(col - i)) return false;
            }
        }
        return true;
    }

    public boolean almostReflectsAcrossCol(int col, List<String> pattern) {
        boolean mismatch = false;
        for (int i = 0; i + col + 1 < pattern.get(0).length() && col - i >= 0; i++) {
            for (String s : pattern) {
                if (s.charAt(col + i + 1) != s.charAt(col - i)) {
                    if (!mismatch)
                        mismatch = true;
                    else
                        return false;
                }
            }
        }
        return mismatch;
    }

    @Override
    public Object runPart1() {
        BigInteger colSum = BigInteger.ZERO;
        BigInteger rowSum = BigInteger.ZERO;

        for (List<String> pattern : patterns) {
            boolean found = false;
            for (int row = 0; row < pattern.size()-1; row++) {
                if (reflectsAcrossRow(row, pattern)) {
                    rowSum = rowSum.add(BigInteger.valueOf(row+1));
                    found = true;
                    break;
                }
            }

            if (found) {
                continue;
            }

            for (int col = 0; col < pattern.get(0).length()-1; col++) {
                if (reflectAcrossCol(col, pattern)) {
                    colSum = colSum.add(BigInteger.valueOf(col+1));
                    break;
                }
            }
        }


        return rowSum.multiply(BigInteger.valueOf(100)).add(colSum);
    }

    @Override
    public Object runPart2() {
        BigInteger colSum = BigInteger.ZERO;
        BigInteger rowSum = BigInteger.ZERO;

        for (List<String> pattern : patterns) {
            boolean found = false;
            for (int row = 0; row < pattern.size()-1; row++) {
                if (almostReflectsAcrossRow(row, pattern)) {
                    rowSum = rowSum.add(BigInteger.valueOf(row+1));
                    found = true;
                    break;
                }
            }

            if (found) {
                continue;
            }

            for (int col = 0; col < pattern.get(0).length()-1; col++) {
                if (almostReflectsAcrossCol(col, pattern)) {
                    colSum = colSum.add(BigInteger.valueOf(col+1));
                    break;
                }
            }
        }


        return rowSum.multiply(BigInteger.valueOf(100)).add(colSum);
    }
}
