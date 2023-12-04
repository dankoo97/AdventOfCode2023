package adventofcode2023.days;

import adventofcode2023.util.Numbers;

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

public class Day01 extends AoCDay {
    private final List<Integer> lines;
    
    public Day01(InputStream file) {
        super(file);
        this.lines = new ArrayList<>();
    }

    public List<Integer> getLines() {
        return lines;
    }

    public void parseInputPlain() {
        lines.clear();
        for (String line : input) {
            String noLetters = line.replaceAll("[a-zA-Z]", "");
            lines.add(Integer.valueOf(noLetters.substring(0, 1).concat(noLetters.substring(noLetters.length() - 1))));
        }
    }

    public void parseInputWithReplacements() {
        lines.clear();
        for (String line : input) {
//            Some words can overlap, therefore we need to keep some letters on either side
            String replace = line
                    .replaceAll("one", "o1e")
                    .replaceAll("two", "t2o")
                    .replaceAll("three", "t3e")
                    .replaceAll("four", "f4r")
                    .replaceAll("five", "f5e")
                    .replaceAll("six", "s6x")
                    .replaceAll("seven", "s7n")
                    .replaceAll("eight", "e8t")
                    .replaceAll("nine", "n9e")
                    .replaceAll("zero", "z0o")
                    .replaceAll("[a-zA-Z]", "");

            lines.add(Integer.valueOf(replace.substring(0, 1).concat(replace.substring(replace.length() - 1))));
        }
    }

    @Override
    public Integer runPart1() {
        this.parseInputPlain();
        return Numbers.sumIntegerArray(getLines());
    }

    @Override
    public Integer runPart2() {
        parseInputWithReplacements();
        return Numbers.sumIntegerArray(getLines());
    }
}
