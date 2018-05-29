package ru.dude.simplepeg;

import ru.dude.simplepeg.entity.CheckResult;
import ru.dude.simplepeg.entity.PegNode;
import ru.dude.simplepeg.entity.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleProcessor {

    Map<String, PegNode> rules;

    PegNode firstRule;

    RdParser rdParser;
    SpegParser sParser;

    public RuleProcessor(PegNode grammarTree) {
        sParser = new SpegParser();
        rdParser = sParser.getRdParser();
        selectRules(grammarTree);
    }

    private void selectRules(PegNode grammarTree) {
        rules = new HashMap<>();
        firstRule = null;

        PegNode lines = grammarTree.child("body").child("rule_lines");
        for (PegNode ch : lines.getChildrens()) {
            if (ch.getExecName().equals("rule")){
                String ruleName = ch.child("rule_name").getMatch().toString();
                PegNode ruleExpression = ch.child("rule_expression");
                if (firstRule == null){
                    firstRule = ruleExpression;
                }
                rules.put(ruleName,ruleExpression);
            }
        }
    }

    public CheckResult check(String text) {
        if (firstRule == null || rules == null || rules.size() == 0) {
            return CheckResult.error("rules init failure");
        }

        State textState = new State(text);

        executeRule(firstRule,textState);


        return null;

    }

    private PegNode executeRule(PegNode rule, State state){

        Executable exec = makeRuleExec(rule);
        PegNode res = exec.exec(state);
        return res;
    }


    public Executable makeRuleExec(final PegNode rule) {

        PegNode subRule = null;
        if (rule.getExecName().equals("sub_expression")) {

            subRule = rules.get(rule.getMatch().toString());
            if (subRule.getSubRuleExec() != null) {
                return subRule.getSubRuleExec();
            }
        }


        List<Executable> childExec = new ArrayList<>();

        for (PegNode child : rule.getChildrens()) {
            childExec.add(makeRuleExec(child));
        }

        Executable res = null;
        switch (rule.getType()) {
            case STRING:
                sParser.parseString(); break;
                //res = rdParser.parseString(); break;
            case REGEXP:
                //res = rdParser.parseRegexp(); break;
            case ORDERED_CHOICE:
                res = rdParser.orderedChoice("er_rodered_choice",
                                             childExec.toArray(new Executable[]{})
                ); break;
            case SEQUENCE:
                res = rdParser.sequence("er_sequence",
                                        childExec.toArray(new Executable[]{})
                ); break;
            case ONE_OR_MORE:
                res = rdParser.oneOrMore("er_one_or_more",childExec.get(0));
                break;
            case ZERO_OR_MORE:
                res = rdParser.oneOrMore("er_zero_or_more",childExec.get(0));
                break;

            case OPTIONAL:
                res = rdParser.oneOrMore("er_optional",childExec.get(0));
                break;
            case END_OF_FILE:
                res = rdParser.parseEndOfFile();
                break;

            default:
                return rdParser.empty();

        }

        if (subRule!=null){
            subRule.setSubRuleExec(res);
        }
        return res;

    }
}
