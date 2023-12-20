package adventofcode2023.days;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

public class Day19 extends AoCDay {
    public static class Rule {
        char var, op;
        BigInteger constant;
        String result;

        public Rule (char var, char op, BigInteger constant, String result) {
            this.var = var;
            this.op = op;
            this.constant = constant;
            this.result = result;

        }
        public boolean apply(MachinePart mp) {
            Function<BigInteger, Boolean> op =  a -> switch (this.op) {
                case '<' -> a.compareTo(constant) < 0;
                case '>' -> a.compareTo(constant) > 0;
                default -> throw new IllegalStateException("Unexpected value: " + this.op);
            };

            return switch (this.var) {
                case 'x' -> op.apply(mp.x);
                case 'm' -> op.apply(mp.m);
                case 'a' -> op.apply(mp.a);
                case 's' -> op.apply(mp.s);
                default -> throw new IllegalStateException("Unexpected value: " + this.var);
            };
        }

        @Override
        public String toString() {
            return "Rule{" +
                    "var=" + var +
                    ", op=" + op +
                    ", constant=" + constant +
                    ", result='" + result + '\'' +
                    '}';
        }
    }

    public static class RuleApplication {
        HashMap<Quality, Constriction> constrictions;
        public RuleApplication(HashMap<Quality, Constriction> constrictions) {
            this.constrictions = constrictions;
        }

        public record Constriction(int min, int max){
            @Override
            public String toString() {
                return "Constriction{" +
                        "min=" + min +
                        ", max=" + max +
                        '}';
            }
        }
        public enum Quality { X, M, A, S }

        public RuleApplication updateConstrictions(Quality q, Constriction c) {
            HashMap<Quality, Constriction> m = new HashMap<>(this.constrictions);
            m.put(q, c);
            return new RuleApplication(m);
        }

        public BigInteger countPossible() {
            BigInteger i = BigInteger.ONE;
            for (Quality q : constrictions.keySet()) {
                int min = constrictions.get(q).min;
                int max = constrictions.get(q).max;
                if (min > max) return BigInteger.ZERO;

                i = i.multiply(BigInteger.valueOf(max + 1 - min));
            }
            return i;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RuleApplication that = (RuleApplication) o;
            return Objects.equals(constrictions, that.constrictions);
        }

        @Override
        public int hashCode() {
            return Objects.hash(constrictions);
        }

        @Override
        public String toString() {
            return "RuleApplication{" +
                    "constrictions=" + constrictions +
                    '}';
        }
    }

    public static class Workflow {
        List<Rule> rules;
        String defaultResult;
        public Workflow(List<Rule> rules, String defaultResult) {
            this.rules = rules;
            this.defaultResult = defaultResult;
        }
        public String applyWorkFlow(MachinePart mp) {
            for (Rule rule : rules) {
                if (rule.apply(mp)) {
                    return rule.result;
                }
            }
            return defaultResult;
        }

        @Override
        public String toString() {
            return "Workflow{" +
                    "rules=" + rules +
                    ", defaultResult='" + defaultResult + '\'' +
                    '}';
        }
    }
    HashMap<String, Workflow> workflows;
    ArrayList<MachinePart> machineParts;
    public record MachinePart(BigInteger x, BigInteger m, BigInteger a, BigInteger s) {
        BigInteger sum() {
            return x.add(m).add(a).add(s);
        }

        @Override
        public String toString() {
            return "MachinePart{" +
                    "x=" + x +
                    ", m=" + m +
                    ", a=" + a +
                    ", s=" + s +
                    '}';
        }
    }
    public Day19(InputStream file) {
        super(file);

        machineParts = new ArrayList<>();
        workflows = new HashMap<>();

        for (String line : input) {
            if (line.startsWith("{")) {
                // Is machine part
                MachinePart mp = getMachinePart(line);
                machineParts.add(mp);

            } else if (line.endsWith("}")) {
                // is rule
                String ruleName = line.substring(0, line.indexOf('{'));
                List<String> rulesStrings = Arrays.stream(line.substring(line.indexOf('{') + 1, line.length() - 1).split(",")).toList();
                List<Rule> rules = new ArrayList<>();
                for (String ruleString : rulesStrings.subList(0, rulesStrings.size()-1)) {
                    char variable = ruleString.charAt(0);
                    char compareOp = ruleString.charAt(1);
                    BigInteger constant = new BigInteger(ruleString.substring(2, ruleString.indexOf(':')));
                    String next = ruleString.substring(ruleString.indexOf(':')+1);

                    Rule r = new Rule(variable, compareOp, constant, next);
                    rules.add(r);
                }
                workflows.put(ruleName, new Workflow(rules, rulesStrings.get(rulesStrings.size()-1)));
            }
            // Is blank line
        }
    }

    private static MachinePart getMachinePart(String line) {
        String[] tokens = line.substring(1, line.length()-1).split(",");
        BigInteger x = BigInteger.ZERO, m = BigInteger.ZERO, a = BigInteger.ZERO, s = BigInteger.ZERO;
        for (String t : tokens) {
            String[] assign = t.split("=");
            switch (assign[0]) {
                case "x" -> x = new BigInteger(assign[1]);
                case "m" -> m = new BigInteger(assign[1]);
                case "a" -> a = new BigInteger(assign[1]);
                case "s" -> s = new BigInteger(assign[1]);
            }
        }
        return new MachinePart(x, m, a, s);
    }

    public List<MachinePart> findValidParts() {
        List<MachinePart> accepted = new ArrayList<>();
        for (MachinePart machinePart : machineParts) {
            String ruleName = "in";
            while (!ruleName.matches("[AR]")) {
                ruleName = workflows.get(ruleName).applyWorkFlow(machinePart);
            }

            if (ruleName.equals("A")) {
                accepted.add(machinePart);
            }
        }
        return accepted;
    }

    @Override
    public Object runPart1() {
        BigInteger total = BigInteger.ZERO;
        for (MachinePart mp : findValidParts()) {
            total = total.add(mp.sum());
        }
        return total;
    }

    @Override
    public Object runPart2() {
        HashMap<RuleApplication.Quality, RuleApplication.Constriction> constrictions = new HashMap<>();
        int min = 1, max = 4000;
        for (RuleApplication.Quality q : RuleApplication.Quality.values()) {
            constrictions.put(q, new RuleApplication.Constriction(min, max));
        }
        RuleApplication start = new RuleApplication(constrictions);
        BigInteger total = BigInteger.ZERO;

        HashMap<RuleApplication, String> map = new HashMap<>();
        map.put(start, "in");
        ArrayList<RuleApplication> ruleApplications = new ArrayList<>(List.of(start));

        while (!ruleApplications.isEmpty()) {
            RuleApplication curr = ruleApplications.remove(0);
            Workflow wf = workflows.get(map.get(curr));
            System.out.println(wf);

            for (Rule rule : wf.rules) {
                System.out.println(curr);

                RuleApplication.Quality q = switch (rule.var) {
                    case 'x' -> RuleApplication.Quality.X;
                    case 'm' -> RuleApplication.Quality.M;
                    case 'a' -> RuleApplication.Quality.A;
                    case 's' -> RuleApplication.Quality.S;
                    default -> throw new IllegalStateException("Unexpected value: " + rule.var);
                };
                RuleApplication.Constriction a, b;
                switch (rule.op) {
                    case '<' -> {
                        a = new RuleApplication.Constriction(curr.constrictions.get(q).min, rule.constant.intValueExact()-1);
                        b = new RuleApplication.Constriction(rule.constant.intValueExact(), curr.constrictions.get(q).max);
                    }
                    case '>' -> {
                        a = new RuleApplication.Constriction(rule.constant.intValueExact()+1, curr.constrictions.get(q).max);
                        b = new RuleApplication.Constriction(curr.constrictions.get(q).min, rule.constant.intValueExact());
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + rule.op);
                }
                
                RuleApplication next = curr.updateConstrictions(q, a);
                BigInteger count = next.countPossible();
                if (Objects.equals(rule.result, "A")) {
                    total = total.add(count);
                } else if (count.compareTo(BigInteger.ZERO) > 0 && !rule.result.equals("R")) {
                    map.put(next, rule.result);
                    ruleApplications.add(next);
                }
                curr = curr.updateConstrictions(q, b);
            }
            System.out.println(curr);
            BigInteger count = curr.countPossible();
            if (wf.defaultResult.equals("A")) {
                total = total.add(count);
            } else if (count.compareTo(BigInteger.ZERO) > 0 && !wf.defaultResult.equals("R")) {
                map.put(curr, wf.defaultResult);
                ruleApplications.add(curr);
            }
        }

        return total;
    }
}
