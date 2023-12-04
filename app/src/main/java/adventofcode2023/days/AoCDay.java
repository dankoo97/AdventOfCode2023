package adventofcode2023.days;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class AoCDay {
    final List<String> input;

    protected AoCDay(InputStream file) {
        Scanner scanner = new Scanner(file);
        input = new ArrayList<>();

        while (scanner.hasNextLine()) {
            input.add(scanner.nextLine());
        }
        scanner.close();
    }

    public void run() {
        System.out.println(runPart1());
        System.out.println(runPart2());
    }

    public abstract Object runPart1();
    public abstract Object runPart2();

}
