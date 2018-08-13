package ru.dude.simplepeg;

import ru.dude.simplepeg.entity.PegNode;
import ru.dude.simplepeg.entity.State;

import java.io.InputStream;
import java.util.Map;

/**
 * Parser for SimplePEG constructions
 * <p>
 * Created by dude on 29.10.2017.
 */
public class SpegParser {

    private Map<String, PegNode> rules;
    State state;
    RdExecutor rdExecutor;


    public SpegParser(State state) {
        this.state = state;
        this.rdExecutor = new RdExecutor();
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

        return rdExecutor.sequence("peg_parser",
                rdExecutor.zeroOrMore("spaces", spacesBreaks()),
                parsingHeader(),
                rdExecutor.oneOrMore("spaces", spacesBreaks()),
                parsingBody(),
                rdExecutor.parseEndOfFile()
        );


    }

    /**
     * HEADER only
     *
     * @return
     */
    public Executable parsingHeader() {
        return rdExecutor.sequence("header",
                rdExecutor.parseString("GRAMMAR"),
                rdExecutor.oneOrMore("spaces", spacesBreaks()),
                rdExecutor.oneOrMore("rulenames", ruleName())
        );
    }


    /**
     * BODy only
     *
     * @return
     */
    public Executable parsingBody() {
        return rdExecutor.oneOrMore("body",
                rdExecutor.orderedChoice("rule_lines",
                        parsingRule(),
                        rdExecutor.oneOrMore("spaces", spacesBreaks())
                )
        );
    }

    /**
     * Space symbols filter
     *
     * @return
     */
    private Executable spacesBreaks() {
        return rdExecutor.parseRegexp("[\\s]");
    }


    /**
     * Parce rule
     * <p>
     *
     * @return
     */
    private Executable parsingRule() {
        return rdExecutor.sequence("rule",

                ruleName(),
                rdExecutor.zeroOrMore("spaces", spacesBreaks()),
                rdExecutor.parseString("->"),
                rdExecutor.zeroOrMore("spaces", spacesBreaks()),
                ruleExpression(),
                rdExecutor.zeroOrMore("spaces", spacesBreaks()),
                rdExecutor.parseString(";"),
                rdExecutor.zeroOrMore("spaces", spacesBreaks())

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
        return rdExecutor.sequence("rule_name",
                rdExecutor.parseRegexp("[a-zA-Z_]"),
                rdExecutor.zeroOrMore("", rdExecutor.parseRegexp("[a-zA-Z0-9_]"))
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
        return rdExecutor.orderedChoice("rule_expression",
                parsingSequence(),
                parsingOrderedChoice(),
                parsingSubExpression()
        );
    }


    private Executable parsingSequence() {

        return rdExecutor.sequence("sequence",
                rdExecutor.orderedChoice(
                        "", parsingOrderedChoice(),
                        parsingSubExpression()
                ),
                rdExecutor.oneOrMore("sequence_args",
                        rdExecutor.sequence("sequence_arg",
                                rdExecutor.oneOrMore("spaces", spacesBreaks()),
                                rdExecutor.orderedChoice(
                                        "", parsingOrderedChoice(),
                                        parsingSubExpression()
                                )
                        )
                )
        );
    }

    private Executable parsingOrderedChoice() {
        return rdExecutor.sequence("ordered_choise",
                parsingSubExpression(),
                rdExecutor.oneOrMore("ordered_choise_args",
                        rdExecutor.sequence("ordered_choise_arg",
                                rdExecutor.zeroOrMore("spaces", spacesBreaks()),
                                rdExecutor.parseString("/"),
                                rdExecutor.zeroOrMore("spaces", spacesBreaks()),
                                parsingSubExpression()
                        )
                )
        );
    }

    private Executable parsingSubExpression() {
        return rdExecutor.sequence("sub_expression",

                rdExecutor.zeroOrMore("", rdExecutor.sequence("tags",
                        tag(),
                        rdExecutor.parseString(":")

                )),
                rdExecutor.orderedChoice(
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
        return rdExecutor.sequence("tag_name",
                rdExecutor.parseRegexp("[a-zA-Z_]"),
                rdExecutor.zeroOrMore("", rdExecutor.parseRegexp("[a-zA-Z0-9_]"))
        );
    }


    private Executable parsingGroup() {
        return rdExecutor.sequence("group",
                rdExecutor.parseString("("),
                rdExecutor.zeroOrMore("spaces", spacesBreaks()),
                rdExecutor.rec(this),
                rdExecutor.zeroOrMore("spaces", spacesBreaks()),
                rdExecutor.parseString(")")
        );
    }

    private Executable parsingAtomicExpression() {
        return rdExecutor.orderedChoice(
                "", parseString(),
                parseRegex(),
                rdExecutor.parseEndOfFile(),
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
        return rdExecutor.sequence("string",
                rdExecutor.parseString("\""),
                rdExecutor.oneOrMore("string", rdExecutor.orderedChoice(
                        "", rdExecutor.parseString("\\\\"),
                        rdExecutor.parseString("\\\""),
                        rdExecutor.parseRegexp("[^\"]")
                )),
                rdExecutor.parseString("\"")
        );
    }

    private Executable parseRegex() {
        return rdExecutor.orderedChoice(
                "", rdExecutor.sequence("regex",
                        rdExecutor.parseString("["),
                        rdExecutor.optional(rdExecutor.parseString("^")),
                        rdExecutor.oneOrMore("regex[]", rdExecutor.orderedChoice(
                                "", rdExecutor.parseString("\\]"),
                                rdExecutor.parseString("\\["),
                                rdExecutor.parseRegexp("[^\\]]")
                        )),
                        rdExecutor.parseString("]")
                ),
                rdExecutor.parseString(".")
        );
    }


    private Executable parsingNot() {
        return rdExecutor.sequence("not",
                rdExecutor.parseString("!"),
                rdExecutor.orderedChoice(
                        "", parsingGroup(),
                        parsingAtomicExpression()
                )
        );
    }

    private Executable parsingAnd() {
        return rdExecutor.sequence("and",
                rdExecutor.parseString("&"),
                rdExecutor.orderedChoice(
                        "", parsingGroup(),
                        parsingAtomicExpression()
                )
        );
    }

    private Executable parsingOneOrMore() {
        return rdExecutor.sequence("one_or_more",
                rdExecutor.orderedChoice(
                        "", parsingGroup(),
                        parsingAtomicExpression()
                ),
                rdExecutor.parseString("+")
        );
    }


    private Executable parsingZeroOrMore() {
        return rdExecutor.sequence("zero_or_more",
                rdExecutor.orderedChoice(
                        "", parsingGroup(),
                        parsingAtomicExpression()
                ),
                rdExecutor.parseString("*")
        );
    }

    private Executable parsingOptional() {
        return rdExecutor.sequence("optional",
                rdExecutor.orderedChoice(
                        "", parsingGroup(),
                        parsingAtomicExpression()
                ),
                rdExecutor.parseString("?")
        );
    }


}
