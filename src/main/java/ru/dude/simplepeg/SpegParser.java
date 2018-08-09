package ru.dude.simplepeg;

import ru.dude.simplepeg.entity.PegNode;
import ru.dude.simplepeg.entity.ResultType;
import ru.dude.simplepeg.entity.SpegTypes;
import ru.dude.simplepeg.entity.State;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Parser for SimplePEG constructions
 * <p>
 * Created by dude on 29.10.2017.
 */
public class SpegParser {

    private Map<String, PegNode> rules;
    State state;
    RdParser rdParser;


    public SpegParser(State state) {
        this.state = state;
        this.rdParser = new RdParser();
    }

    public SpegParser(State state, Map<String, PegNode> rules) {
        this(state);
        this.rules = rules;
    }


    public static PegNode createAndExec(String grammar) {
        State state = new State(grammar);

        SpegParser spegParser = new SpegParser(state);
        return spegParser.peg().exec(state);
    }

    public static PegNode createAndExec(InputStream grammarIS) {
        State state = new State(grammarIS);

        SpegParser spegParser = new SpegParser(state);
        return spegParser.peg().exec(state);
    }

    /**
     * All SPEG
     *
     * @return
     */
    public Executable peg() {

        //return  parsingBody();

        return rdParser.sequence("peg_parser",
                rdParser.zeroOrMore("spaces", spacesBreaks()),
                parsingHeader(),
                rdParser.oneOrMore("spaces", spacesBreaks()),
                parsingBody(),
                rdParser.parseEndOfFile()
        );


    }

    /**
     * HEADER only
     *
     * @return
     */
    public Executable parsingHeader() {
        return rdParser.sequence("header",
                rdParser.parseString("GRAMMAR"),
                rdParser.oneOrMore("spaces", spacesBreaks()),
                rdParser.oneOrMore("rulenames", ruleName())
        );
    }


    /**
     * BODy only
     *
     * @return
     */
    public Executable parsingBody() {
        return rdParser.oneOrMore("body",
                rdParser.orderedChoice("rule_lines",
                        parsingRule(),
                        rdParser.oneOrMore("spaces", spacesBreaks())
                )
        );
    }

    /**
     * Space symbols filter
     *
     * @return
     */
    private Executable spacesBreaks() {
        return rdParser.parseRegexp("[\\s]");
    }


    /**
     * Parce rule
     * <p>
     * TODO: (not ready)
     *
     * @return
     */
    private Executable parsingRule() {
        return rdParser.sequence("rule",

                ruleName(),
                rdParser.zeroOrMore("spaces", spacesBreaks()),
                rdParser.parseString("->"),
                rdParser.zeroOrMore("spaces", spacesBreaks()),
                ruleExpression(),
                rdParser.zeroOrMore("spaces", spacesBreaks()),
                rdParser.parseString(";"),
                rdParser.zeroOrMore("spaces", spacesBreaks())

        );
    }

    /**
     * Parse rule name
     * <p>
     * js parsing_rule_name
     *
     * @return
     */
    private Executable ruleName() {
        return rdParser.sequence("rule_name",
                rdParser.parseRegexp("[a-zA-Z_]"),
                rdParser.zeroOrMore("", rdParser.parseRegexp("[a-zA-Z0-9_]"))
        );
    }

    /**
     * Parse rule Expression
     * <p>
     * js parsing_expression
     *
     * @return
     */
    public Executable ruleExpression() {
        return rdParser.orderedChoice("rule_expression",
                parsingSequence(),
                parsingOrderedChoice(),
                parsingSubExpression()
        );
    }


    private Executable parsingSequence() {

        return rdParser.sequence("sequence",
                rdParser.orderedChoice(
                        "", parsingOrderedChoice(),
                        parsingSubExpression()
                ),
                rdParser.oneOrMore("sequence_args",
                        rdParser.sequence("sequence_arg",
                                rdParser.oneOrMore("spaces", spacesBreaks()),
                                rdParser.orderedChoice(
                                        "", parsingOrderedChoice(),
                                        parsingSubExpression()
                                )
                        )
                )
        );
    }

    private Executable parsingOrderedChoice() {
        return rdParser.sequence("ordered_choise",
                parsingSubExpression(),
                rdParser.oneOrMore("ordered_choise_args",
                        rdParser.sequence("ordered_choise_arg",
                                rdParser.zeroOrMore("spaces", spacesBreaks()),
                                rdParser.parseString("/"),
                                rdParser.zeroOrMore("spaces", spacesBreaks()),
                                parsingSubExpression()
                        )
                )
        );
    }

    private Executable parsingSubExpression() {
        return rdParser.sequence("sub_expression",

                rdParser.zeroOrMore("", rdParser.sequence("tags",
                        tag(),
                        rdParser.parseString(":")

                )),
                rdParser.orderedChoice(
                        "", parsingNot(),
                        parsingAnd(),
                        parsingOptional(),
                        parsingOneOrMore(),
                        parsingZeroOrMore(),
                        parsingGroup(),
                        parsingAtomicExpression()
                )

        );

    }


    private Executable tag() {
        return rdParser.sequence("tag_name",
                rdParser.parseRegexp("[a-zA-Z_]"),
                rdParser.zeroOrMore("", rdParser.parseRegexp("[a-zA-Z0-9_]"))
        );
    }


    private Executable parsingGroup() {
        return rdParser.sequence("group",
                rdParser.parseString("("),
                rdParser.zeroOrMore("spaces", spacesBreaks()),
                rdParser.rec(this),
                rdParser.zeroOrMore("spaces", spacesBreaks()),
                rdParser.parseString(")")
        );
    }

    private Executable parsingAtomicExpression() {
        return rdParser.orderedChoice(
                "", parseString(),
                parseRegex(),
                rdParser.parseEndOfFile(),
                parsingRuleCall()
        );
    }


    /**
     * js parsing_rule_call()
     *
     * @return
     */
    private Executable parsingRuleCall() {
        return ruleName();
    }

    private Executable parseString() {
        return rdParser.sequence("string",
                rdParser.parseString("\""),
                rdParser.oneOrMore("string", rdParser.orderedChoice(
                        "", rdParser.parseString("\\\\"),
                        rdParser.parseString("\\\""),
                        rdParser.parseRegexp("[^\"]")
                )),
                rdParser.parseString("\"")
        );
    }

    private Executable parseRegex() {
        return rdParser.orderedChoice(
                "", rdParser.sequence("regex",
                        rdParser.parseString("["),
                        rdParser.optional(rdParser.parseString("^")),
                        rdParser.oneOrMore("regex[]", rdParser.orderedChoice(
                                "", rdParser.parseString("\\]"),
                                rdParser.parseString("\\["),
                                rdParser.parseRegexp("[^\\]]")
                        )),
                        rdParser.parseString("]")
                ),
                rdParser.parseString(".")
        );
    }


    private Executable parsingNot() {
        return rdParser.sequence("not",
                rdParser.parseString("!"),
                rdParser.orderedChoice(
                        "", parsingGroup(),
                        parsingAtomicExpression()
                )
        );
    }

    private Executable parsingAnd() {
        return rdParser.sequence("and",
                rdParser.parseString("&"),
                rdParser.orderedChoice(
                        "", parsingGroup(),
                        parsingAtomicExpression()
                )
        );
    }

    private Executable parsingOneOrMore() {
        return rdParser.sequence("one_or_more",
                rdParser.orderedChoice(
                        "", parsingGroup(),
                        parsingAtomicExpression()
                ),
                rdParser.parseString("+")
        );
    }


    private Executable parsingZeroOrMore() {
        return rdParser.sequence("zero_or_more",
                rdParser.orderedChoice(
                        "", parsingGroup(),
                        parsingAtomicExpression()
                ),
                rdParser.parseString("*")
        );
    }

    private Executable parsingOptional() {
        return rdParser.sequence("optional",
                rdParser.orderedChoice(
                        "", parsingGroup(),
                        parsingAtomicExpression()
                ),
                rdParser.parseString("?")
        );
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
                        return rdParser.parseString(unQuotedStr).exec(state);
                    case "regex":
                        return rdParser.parseRegexp(fullStr).exec(state);
                    case "sequence":
                        return rdParser.sequence("applySequence", childExecs).exec(state);
                    case "ordered_choise":
                        return rdParser.orderedChoice("applyOrderedChoise", childExecs).exec(state);
                    case "one_or_more":
                        return  rdParser.oneOrMore("applyOneOrMore", childExecs[0]).exec(state);
                    case "zero_or_more":
                        return rdParser.zeroOrMore("applyZeroOrMore", childExecs[0]).exec(state);
                    case "not":
                        return rdParser.not(childExecs[1]).exec(state);
                    //case "and": //return rdParser.(childExecs[1]).exec(state);
                    case "optional":
                        return rdParser.optional(childExecs[0]).exec(state);
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

                        return rdParser.sequence("deafultSequence", childExecs).exec(state);
                }
            }
        };
    }
}
