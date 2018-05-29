package ru.dude.simplepeg;

import ru.dude.simplepeg.entity.PegNode;
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

    RdParser rdParser;

    SpegParser() {
        rdParser = new RdParser();
    }

    public static PegNode createAndExec(String grammar) {
        State state = new State(grammar);

        SpegParser spegParser = new SpegParser();
        return spegParser.peg().exec(state);
    }

    public static PegNode createAndExec(InputStream grammarIS) {
        State state = new State(grammarIS);

        SpegParser spegParser = new SpegParser();
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


    public Executable parsingSequence() {

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

    public Executable parsingOrderedChoice() {
        return rdParser.sequence("ordered_choise",
                parsingSubExpression(),
                rdParser.oneOrMore("ordered_choise_args",
                        rdParser.sequence("ordered_choise_arg",
                                rdParser.oneOrMore("spaces", spacesBreaks()),
                                rdParser.parseString("/"),
                                rdParser.oneOrMore("spaces", spacesBreaks()),
                                parsingSubExpression()
                        )
                )
        );
    }

    public Executable parsingSubExpression() {
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


    public Executable tag() {
        return rdParser.sequence("tag_name",
                rdParser.parseRegexp("[a-zA-Z_]"),
                rdParser.zeroOrMore("", rdParser.parseRegexp("[a-zA-Z0-9_]"))
        );
    }


    public Executable parsingGroup() {
        return rdParser.sequence("group",
                rdParser.parseString("("),
                rdParser.zeroOrMore("spaces", spacesBreaks()),
                rdParser.rec(this),
                rdParser.zeroOrMore("spaces", spacesBreaks()),
                rdParser.parseString(")")
        );
    }

    public Executable parsingAtomicExpression() {
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

    public Executable parseString() {
        return rdParser.sequence("string_expr",
                rdParser.parseString("\""),
                rdParser.oneOrMore("string", rdParser.orderedChoice(
                        "", rdParser.parseString("\\\\"),
                        rdParser.parseString("\\\""),
                        rdParser.parseRegexp("[^\"]")
                )),
                rdParser.parseString("\"")
        );
    }

    public Executable parseRegex() {
        return rdParser.orderedChoice(
                "", rdParser.sequence("regex_expr",
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


    public Executable parsingNot() {
        return rdParser.sequence("not_exr",
                rdParser.parseString("!"),
                rdParser.orderedChoice(
                        "", parsingGroup(),
                        parsingAtomicExpression()
                )
        );
    }

    public Executable parsingAnd() {
        return rdParser.sequence("and_expr",
                rdParser.parseString("&"),
                rdParser.orderedChoice(
                        "", parsingGroup(),
                        parsingAtomicExpression()
                )
        );
    }

    public Executable parsingOneOrMore() {
        return rdParser.sequence("one_or_more_exr",
                rdParser.orderedChoice(
                        "", parsingGroup(),
                        parsingAtomicExpression()
                ),
                rdParser.parseString("+")
        );
    }


    public Executable parsingZeroOrMore() {
        return rdParser.sequence("zero_or_more_expr",
                rdParser.orderedChoice(
                        "", parsingGroup(),
                        parsingAtomicExpression()
                ),
                rdParser.parseString("*")
        );
    }

    public Executable parsingOptional() {
        return rdParser.sequence("optional_expr",
                rdParser.orderedChoice(
                        "", parsingGroup(),
                        parsingAtomicExpression()
                ),
                rdParser.parseString("?")
        );
    }

    public RdParser getRdParser() {
        return rdParser;
    }
}
