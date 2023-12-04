package adventofcode2023.days;

import java.io.InputStream;

public class Day02 extends AoCDay {

    public static class Game {
        int id;
        int red, green, blue;
        public Game(int id) {
            this.id = id;
            red = 0;
            green = 0;
            blue = 0;
        }

        public void updateMaximum(int red, int green, int blue) {
            this.red = Math.max(red, this.red);
            this.green = Math.max(green, this.green);
            this.blue = Math.max(blue, this.blue);
        }

        public int getPowerOfGame() {
            return red * green * blue;
        }

        public static Game readGame(String gameString) {

            String[] gameSplit = gameString.split(":");
            int id = Integer.parseInt(gameSplit[0].substring(5));
            String[] gameSets = gameSplit[1].trim().split(";");

            Game game = new Game(id);

            for (String set : gameSets) {
                int red = 0, green = 0, blue = 0;
                String[] colors = set.trim().split(",");
                for (String color : colors) {
                    color = color.trim();
                    if (color.endsWith("blue")) {
                        blue = Integer.parseInt(color.replaceAll(" blue", ""));
                    } else if (color.endsWith("green")) {
                        green = Integer.parseInt(color.replaceAll(" green", ""));
                    } else if (color.endsWith("red")) {
                        red = Integer.parseInt(color.replaceAll(" red", ""));
                    } else {
                        throw new Error("Could not read colors");
                    }
                }
                game.updateMaximum(red, green, blue);
            }

            return game;
        }

        public boolean isValid(int maxRed, int maxGreen, int maxBlue) {
            return red <= maxRed && green <= maxGreen && blue <= maxBlue;
        }
    }

    public Day02(InputStream file) {
        super(file);
    }

    @Override
    public Object runPart1() {
        int s = 0;
        for (String gameString : input) {
            Game g = Game.readGame(gameString);
            if (g.isValid(12, 13, 14)) {
                s += g.id;
            }
        }

        return s;
    }

    @Override
    public Object runPart2() {
        int s = 0;
        for (String gameString : input) {
            Game g = Game.readGame(gameString);
            s += g.getPowerOfGame();
        }

        return s;
    }
}
