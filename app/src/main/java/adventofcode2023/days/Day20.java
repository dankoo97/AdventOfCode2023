package adventofcode2023.days;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;

public class Day20 extends AoCDay {
    public Day20(InputStream file) {
        super(file);

        HashSet<Conjunction> conjunctions = new HashSet<>();

        for (String line : input) {
            String[] token = line.split(" -> ");
            char buttonType = line.charAt(0);
            List<String> children = Arrays.stream(token[1].split(", ")).toList();

            switch (buttonType) {
                case '%' -> new FlipFlop(token[0].substring(1), children);
                case '&' -> conjunctions.add(new Conjunction(token[0].substring(1), children));
                case 'b' -> new Broadcast(token[0], children);
                default -> throw new IllegalStateException("Unexpected value: " + buttonType);
            }
        }

        for (ButtonModule b : ButtonModule.buttonModuleMap.values()) {
            List<ButtonModule> temp = b.allChildModules();
            temp.retainAll(conjunctions);
            for (ButtonModule conjunction : temp) {
                ((Conjunction) conjunction).addParent(b);
            }
        }
    }

    public static class ButtonModule {
        public static BigInteger highPulses = BigInteger.ZERO;
        public static BigInteger lowPulses = BigInteger.ZERO;
        public static ArrayList<PulseOp> pulseQueue = new ArrayList<>();
        String name;
        public static HashMap<String, Boolean> watch = new HashMap<>();

        public record PulseOp(ButtonModule parent, ButtonModule child, boolean value) {
            public void pulse() {
                if (value) {
                    highPulses = highPulses.add(BigInteger.ONE);
                } else {
                    lowPulses = lowPulses.add(BigInteger.ONE);
                }
                if (parent != null && watch.containsKey(parent.name) && Objects.equals(child.name, "zg")) {
                    watch.put(parent.name, value || watch.get(parent.name));
                }
                if (child != null) {
//                    System.out.printf("%s -%s> %s\n", parent == null ? "PUSH" : parent.name, value ? "high" : "low", child.name);
                    child.op(parent, value);
                }
                nextPulse();
            }
        }

        public static HashMap<String, ButtonModule> buttonModuleMap = new HashMap<>();

        List<String> children;
        boolean value;
        public ButtonModule(String name, List<String> children) {
            this.children = children;
            this.value = false;
            this.name = name;
            buttonModuleMap.put(name.strip(), this);
        }

        public void op(ButtonModule parent, boolean value) {
            throw new Error("Unimplemented");
        }

        public List<ButtonModule> allChildModules() {
            return new ArrayList<>(children.stream().map(child -> buttonModuleMap.get(child)).toList());
        }

        @Override
        public String toString() {
            return "ButtonModule{" +
                    "name=" + name +
                    ", children=" + children +
                    ", value=" + value +
                    '}';
        }

        public static void nextPulse() {
            if (!pulseQueue.isEmpty()) {
                PulseOp p = pulseQueue.remove(0);
                p.pulse();
            }
        }

        public static HashSet<String> valueMap() {
            return new HashSet<>(ButtonModule.buttonModuleMap.values().stream().map((bm) -> String.format("%s=%s", bm.name, bm.value)).toList());
        }
    }

    public static class Broadcast extends ButtonModule {
        public Broadcast(String name, List<String> children) {
            super(name, children);
        }

        @Override
        public void op(ButtonModule parent, boolean value) {
            for (ButtonModule child : allChildModules()) {
                try {
                    pulseQueue.add(new PulseOp(this, child, value));
                } catch (NullPointerException ignored) { }
            }
        }
    }

    public static class FlipFlop extends ButtonModule {

        public FlipFlop(String name, List<String> children) {
            super(name, children);
        }

        @Override
        public void op(ButtonModule parent, boolean value) {
            if (!value) {
                this.value = !this.value;
                for (ButtonModule child : this.allChildModules()) {
                    try {
                        pulseQueue.add(new PulseOp(this, child, this.value));
                    } catch (NullPointerException ignored) { }
                }
            }
        }
    }

    public static class Conjunction extends ButtonModule {
        HashMap<ButtonModule, Boolean> parentValues;
        public Conjunction(String name, List<String> children) {
            super(name, children);
            parentValues = new HashMap<>();
        }

        public void addParent(ButtonModule parent) {
            parentValues.put(parent, false);
        }

        @Override
        public void op(ButtonModule parent, boolean value) {
            parentValues.put(parent, value);
            this.value = parentValues.values().stream().reduce(true, (token, item) -> token && item);
            for (ButtonModule child : allChildModules()) {
                try {
                    pulseQueue.add(new PulseOp(this, child, !this.value));
                } catch (NullPointerException ignored) { }
            }
        }
    }

    public static class Button extends ButtonModule {
        public Button(String name, List<String> children) {
            super(name, children);
        }

        @Override
        public void op(ButtonModule parent, boolean value) {
            for (ButtonModule child : allChildModules()) {
                try {
                    pulseQueue.add(new PulseOp(this, child, this.value));
                } catch (NullPointerException ignored) {}
            }
        }
    }

    public void reset() {
        for (ButtonModule b : ButtonModule.buttonModuleMap.values()) {
            b.value = false;
        }
    }

    public BigInteger runUntilXReceivesY() {
        BigInteger i = BigInteger.ZERO;
        ButtonModule button = ButtonModule.buttonModuleMap.get("button");
        ButtonModule broadcaster = ButtonModule.buttonModuleMap.get("broadcaster");
        HashMap<String, BigInteger> multiplierMap = new HashMap<>();

        for (ButtonModule parent : ((Conjunction) ButtonModule.buttonModuleMap.get("zg")).parentValues.keySet()) {
            ButtonModule.watch.put(parent.name, false);
            multiplierMap.put(parent.name, BigInteger.ZERO);
        }

        while (!ButtonModule.watch.values().stream().reduce(true, (a, b) -> a && b) && i.compareTo(BigInteger.valueOf(10000)) < 0) {
            ButtonModule.pulseQueue.add(new ButtonModule.PulseOp(button, broadcaster, false));
            i = i.add(BigInteger.ONE);
            ButtonModule.nextPulse();
            for (String module : ButtonModule.watch.keySet()) {
                if (ButtonModule.watch.get(module) && multiplierMap.get(module).equals(BigInteger.ZERO)) {
                    multiplierMap.put(module, i);
                }
            }
        }

        System.out.println(multiplierMap);
        return multiplierMap.values().stream().reduce(BigInteger.ONE, BigInteger::multiply);
    }

    @Override
    public Object runPart1() {
        Button b = new Button("button", List.of("broadcaster"));
        for (int i = 0; i < 1000; i++) {
            ButtonModule.pulseQueue.add(new ButtonModule.PulseOp(b, ButtonModule.buttonModuleMap.get("broadcaster"), false));
            ButtonModule.nextPulse();
        }
        System.out.printf("%s * %s\n", ButtonModule.lowPulses, ButtonModule.highPulses);
        return ButtonModule.lowPulses.multiply(ButtonModule.highPulses);
    }

    @Override
    public Object runPart2() {
        reset();
        return runUntilXReceivesY();
    }
}
