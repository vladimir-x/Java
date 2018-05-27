package ru.dude.simplepeg;

import ru.dude.simplepeg.entity.CheckResult;
import ru.dude.simplepeg.entity.PegNode;
import ru.dude.simplepeg.entity.State;

import java.util.HashMap;
import java.util.Map;

public class RuleProcessor {

    Map<String, PegNode> rules;

    PegNode firstRule;

    RdParser rdParser;

    public RuleProcessor(PegNode grammarTree) {
        rdParser = new RdParser();
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

        SpegParser spegParser = new SpegParser(state);

        Executable exec = spegParser.execRule(rule);
        PegNode res = exec.exec(state);
        return res;
    }

}
