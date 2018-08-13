package ru.dude.simplepeg;

import ru.dude.simplepeg.entity.CheckResult;
import ru.dude.simplepeg.entity.PegNode;
import ru.dude.simplepeg.entity.ResultType;
import ru.dude.simplepeg.entity.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * For text checking
 */
public class RuleProcessor {

    /**
     * Разобранные правила
     */
    Map<String, PegNode> rules;

    /**
     * Первое правило
     */
    PegNode firstRule;

    RdExecutor rdExecutor;

    public RuleProcessor(PegNode grammarTree) {
        rdExecutor = new RdExecutor();
        selectRules(grammarTree);
    }

    private void selectRules(PegNode grammarTree) {
        rules = new HashMap<>();
        firstRule = null;

        PegNode lines = grammarTree.child("body");
        for (PegNode ch : lines.getChildrens()) {
            if (ch.getExecName().equals("rule_lines")) {
                PegNode rule = ch.child("rule");
                String ruleName = rule.child("rule_name").getMatch().toString();
                PegNode ruleExpression = rule.child("rule_expression");
                if (firstRule == null) {
                    firstRule = ruleExpression;
                }
                rules.put(ruleName, ruleExpression);
            }
        }
    }

    public CheckResult check(String text) {
        if (firstRule == null || rules == null || rules.size() == 0) {
            return CheckResult.error("rules init failure");
        }

        State textState = new State(text);

        PegNode resultExec = executeRule(firstRule, textState);

        if (resultExec.getResultType() == ResultType.OK) {
            return CheckResult.ok();
        }

        return CheckResult.error(resultExec.getError());
    }

    private PegNode executeRule(PegNode rule, State state) {

        Executable exec = applyRule(rule);
        PegNode res = exec.exec(state);
        return res;
    }


    public Executable applyRule(final PegNode rule) {
        return new Executable() {

            @Override
            public PegNode exec(State state) {

                Executable[] childExecs = null;

                if (rule.getChildrens() != null) {
                    List<Executable> childExecsList = new ArrayList<>();
                    for (PegNode childNode : rule.getChildrens()) {
                        childExecsList.add(applyRule(childNode));
                    }
                    childExecs = childExecsList.toArray(new Executable[0]);
                }

                String fullStr = rule.getMatch().toString();
                String unQuotedStr = fullStr;
                if (fullStr.length() > 1) {
                    unQuotedStr = fullStr.substring(1, fullStr.length() - 1);
                }

                PegNode emptyRes = new PegNode();
                emptyRes.setResultType(ResultType.EMPTY);

                if (rule.getExecName() == null) {
                    return emptyRes;
                }

                switch (rule.getExecName()) {
                    case "string":
                        return rdExecutor.parseString(unQuotedStr).exec(state);
                    case "regex":
                        return rdExecutor.parseRegexp(fullStr).exec(state);
                    case "sequence":
                        return rdExecutor.sequence("applySequence", childExecs).exec(state);
                    case "ordered_choise":
                        return rdExecutor.orderedChoice("applyOrderedChoise", childExecs).exec(state);
                    case "one_or_more":
                        return rdExecutor.oneOrMore("applyOneOrMore", childExecs[0]).exec(state);
                    case "zero_or_more":
                        return rdExecutor.zeroOrMore("applyZeroOrMore", childExecs[0]).exec(state);
                    case "not":
                        return rdExecutor.not(childExecs[1]).exec(state);
                    //case "and": //return rdExecutor.(childExecs[1]).exec(state);
                    case "optional":
                        return rdExecutor.optional(childExecs[0]).exec(state);
                    case "rule_expression":
                    default:
                        if (rules.containsKey(fullStr)) {
                            return applyRule(rules.get(fullStr)).exec(state);
                        }


                        if (childExecs.length == 0) {
                            return emptyRes;
                        }

                        if (childExecs.length == 1) {
                            return childExecs[0].exec(state);
                        }

                        return rdExecutor.sequence("deafultSequence", childExecs).exec(state);
                }
            }
        };
    }


}
