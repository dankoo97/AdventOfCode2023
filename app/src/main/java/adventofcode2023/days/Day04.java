package adventofcode2023.days;

import adventofcode2023.util.Numbers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Day04 extends AoCDay{
    public Day04(InputStream file) {
        super(file);
    }

    public static class Card {
        public static HashMap<Integer, Card> cardMap = new HashMap<>();
        public static HashMap<Integer, Integer> cardInstanceCount = new HashMap<>();
        int id;
        HashSet<Integer> winningNumbers;
        HashSet<Integer> numbersFound;
        public Card(String cardInput) {
            winningNumbers = new HashSet<>();
            numbersFound = new HashSet<>();
            String[] cardSplit = cardInput.split(":");
            id = Integer.parseInt(cardSplit[0].substring(5).trim());
            String[] numbers = cardSplit[1].split("\\|");

            for (String s : numbers[0].trim().split(" +")) {
                winningNumbers.add(Integer.parseInt(s));
            }

            for (String s: numbers[1].trim().split(" +")) {
                numbersFound.add(Integer.parseInt(s));
            }

            cardMap.put(id, this);
        }

        public int getScore() {
            int n = countWinningNumbers();
            if (n == 0) {
                return 0;
            }
            return 1 << (n - 1);
        }

        public int countWinningNumbers() {
            HashSet<Integer> intersection = new HashSet<>(winningNumbers);
            intersection.retainAll(numbersFound);
            return intersection.size();
        }

        public void addMoreCards() {
            int n = countWinningNumbers();
            cardInstanceCount.putIfAbsent(id, 1);
            for (int i = 1; i <= n; i++) {
                cardInstanceCount.put(id + i, cardInstanceCount.getOrDefault(id + i, 1) + cardInstanceCount.get(id));
            }
        }

    }

    @Override
    public Object runPart1() {
        ArrayList<Integer> cardScores = new ArrayList<>();
        for (String cardInput : input) {
            Card card = new Card(cardInput);
            cardScores.add(card.getScore());
        }
        return Numbers.sumIntegerArray(cardScores);
    }

    @Override
    public Object runPart2() {
        for (String cardInput : input) {
            new Card(cardInput).addMoreCards();
        }

        return Numbers.sumIntegerArray(new ArrayList<>(Card.cardInstanceCount.values()));
    }
}
