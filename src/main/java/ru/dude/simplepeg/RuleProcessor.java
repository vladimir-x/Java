package ru.dude.simplepeg;

import ru.dude.simplepeg.entity.CheckResult;
import ru.dude.simplepeg.entity.PegNode;
import ru.dude.simplepeg.entity.ResultType;
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

        PegNode lines = grammarTree.child("body");
        for (PegNode ch : lines.getChildrens()) {
            if (ch.getExecName().equals("rule_lines")){
                PegNode rule = ch.child("rule");
                String ruleName = rule.child("rule_name").getMatch().toString();
                PegNode ruleExpression = rule.child("rule_expression");
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

        PegNode resultExec = executeRule(firstRule, textState);

        if (resultExec.getResultType() == ResultType.OK){
            return CheckResult.ok();
        }

        return CheckResult.error(resultExec.getError());
    }

    private PegNode executeRule(PegNode rule, State state){

        SpegParser spegParser = new SpegParser(state,rules);



        Executable exec = spegParser.applyRule(rule);
        PegNode res = exec.exec(state);
        return res;
    }





}
