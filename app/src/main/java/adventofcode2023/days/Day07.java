package adventofcode2023.days;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;
import java.util.List;

public class Day07 extends AoCDay {
    public Day07(InputStream file) {
        super(file);
    }

    public static class Card {
        public static final Card J = new Card("J");
        private final String value;
        public static boolean withJokers = false;
        public Card(String value) {
            this.value = value;
        }

        public int getCardValue() {
            if (value.matches("\\d")) return Integer.parseInt(value);
            switch (value) {
                case "T" -> {
                    return 10;
                }
                case "J" -> {
                    return withJokers ? 0 : 11;
                }
                case "Q" -> {
                    return 12;
                }
                case "K" -> {
                    return 13;
                }
                case "A" -> {
                    return 14;
                }
            }
            throw new Error("Unable to find card value");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Card card = (Card) o;
            return Objects.equals(value, card.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static class Hand implements Comparable<Hand> {
        List<Card> cards;
        Map<Card, Integer> cardCounts;
        public static boolean withJokers;

        public enum HandValue {
            HIGH_CARD,
            PAIR,
            TWO_PAIR,
            THREE_OF_A_KIND,
            FULL_HOUSE,
            FOUR_OF_A_KIND,
            FIVE_OF_A_KIND,
        }

        public Hand(ArrayList<Card> cards) {
            this.cards = cards;
            this.cardCounts = new HashMap<>();
            for (Card c : cards) {
                cardCounts.putIfAbsent(c, 0);
                cardCounts.computeIfPresent(c, (k, v) -> v + 1);
            }
        }

        public boolean isFiveOfAKind() {
            return cardCounts.containsValue(5);
        }

        public boolean isFourOfAKind() {
            return cardCounts.containsValue(4);
        }

        public boolean isFullHouse() {
            return cardCounts.containsValue(3) && cardCounts.containsValue(2);
        }

        public boolean isThreeOfAKind() {
            return cardCounts.containsValue(3);
        }

        public boolean isTwoPair() {
            // TwoPair can never be the best hand with jokers
            int count = 0;
            for (int n : cardCounts.values()) {
                if (n == 2) {
                    count++;
                }
            }
            return count == 2;
        }

        public boolean isPair() {
            return cardCounts.containsValue(2);
        }

        public HandValue getHandValue() {
            if (withJokers) {
                Card largest = null;
                int total = 0;
                for (Card c : cardCounts.keySet()) {
                    if (!Objects.equals(c.value, Card.J.value)) {
                        if (cardCounts.get(c) > total) {
                            largest = c;
                            total = cardCounts.get(c);
                        }
                    }
                }

                if (largest == null) {
                    return HandValue.FIVE_OF_A_KIND;
                }

                cardCounts.computeIfPresent(largest, (k, v) -> v + cardCounts.getOrDefault(Card.J, 0));
                cardCounts.remove(Card.J);
            }
            if (this.isFiveOfAKind()) return HandValue.FIVE_OF_A_KIND;
            if (this.isFourOfAKind()) return HandValue.FOUR_OF_A_KIND;
            if (this.isFullHouse()) return HandValue.FULL_HOUSE;
            if (this.isThreeOfAKind()) return HandValue.THREE_OF_A_KIND;
            if (this.isTwoPair()) return HandValue.TWO_PAIR;
            if (this.isPair()) return HandValue.PAIR;
            return HandValue.HIGH_CARD;
        }

        @Override
        public String toString() {
            return "Hand{" +
                    "cards=" + cards +
                    '}';
        }

        @Override
        public int compareTo(Hand o) {
            assert o.getClass() == this.getClass();
            int c = this.getHandValue().compareTo((o).getHandValue());
            if (c != 0) {
                return c;
            }
            for (int i = 0; i < this.cards.size(); i++) {
                if (this.cards.get(i).getCardValue() > o.cards.get(i).getCardValue()) return 1;
                if (this.cards.get(i).getCardValue() < o.cards.get(i).getCardValue()) return -1;
            }
            return 0;
        }
    }

    @Override
    public Object runPart1() {
        return getTotalWinnings(readHands());
    }

    @Override
    public Object runPart2() {
        Card.withJokers = true;
        Hand.withJokers = true;
        return getTotalWinnings(readHands());
//        String[] cardHands = new String[] {"J3AJA", "J42JK"};
//        for (String cardHand : cardHands) {
//            ArrayList<Card> cards = new ArrayList<>();
//            for (int i = 0; i < 5; i++) {
//                cards.add(new Card(cardHand.substring(i, i+1)));
//            }
//            Hand hand = new Hand(cards);
//            System.out.println(hand);
//            System.out.println(hand.getHandValue());
//            System.out.println(hand.cardCounts);
//            System.out.println();
//        }
//        return null;
    }

    public BigInteger getTotalWinnings(TreeMap<Hand, BigInteger> hands) {
        BigInteger i = BigInteger.ONE;
        BigInteger total = BigInteger.ZERO;
        for (Hand hand : hands.navigableKeySet()) {
            System.out.printf("%s: %s   %s\n", i, hand, hands.get(hand));
            total = total.add(i.multiply(hands.get(hand)));
            i = i.add(BigInteger.ONE);
        }

        return total;
    }

    public TreeMap<Hand, BigInteger> readHands() {
        TreeMap<Hand, BigInteger> hands = new TreeMap<>();
        for (String line : input) {
            String[] tokens = line.split(" ");
            ArrayList<Card> cards = new ArrayList<>(5);
            for (int i = 0; i < 5; i++) {
                cards.add(new Card(tokens[0].substring(i, i+1)));
            }
            hands.put(new Hand(cards), new BigInteger(tokens[1]));
        }

        return hands;
    }
}
