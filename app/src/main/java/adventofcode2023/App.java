/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package adventofcode2023;

import adventofcode2023.days.*;

import java.io.InputStream;

public class App {

    public static void main(String[] args) {
        int currentDay = 7;
        AoCDay d;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (args.length > 0) {
            currentDay = Integer.parseInt(args[0]);
        }

        InputStream inputStream = classLoader.getResourceAsStream(String.format("day%02d", currentDay));

        switch (currentDay) {
            case 1 -> d = new Day01(inputStream);
            case 2 -> d = new Day02(inputStream);
            case 3 -> d = new Day03(inputStream);
            case 4 -> d = new Day04(inputStream);
            case 5 -> d = new Day05(inputStream);
            case 6 -> d = new Day06(inputStream);
            case 7 -> d = new Day07(inputStream);
            default -> throw(new Error("Not implemented"));
        }

        d.run();
    }
}
