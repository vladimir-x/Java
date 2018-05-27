package ru.dude.simplepeg;

import ru.dude.simplepeg.entity.State;

/**
 * Parser for SimplePEG constructions
 * <p>
 * Created by dude on 29.10.2017.
 */
public class SpegParser {

    State state;
    RdParser rdParser;


    SpegParser(State state) {
        this.state = state;
        rdParser = new RdParser();
    }

    /**
     * All SPEG
     * @return
     */
    public Executable peg() {

        //return  parsingBody();

        return rdParser.sequence("peg_parser",
                rdParser.zeroOrMore(spacesBreaks()),
                parsingHeader(),
                rdParser.oneOrMore("spaces",spacesBreaks()),
                parsingBody(),
                rdParser.parseEndOfFile()
        );


    }

    /**
     * HEADER only
     * @return
     */
    public Executable parsingHeader() {
        return rdParser.sequence("header",
                rdParser.parseString("GRAMMAR"),
                rdParser.oneOrMore("spaces",spacesBreaks()),
                rdParser.oneOrMore("rulenames",ruleName())
        );
    }


    /**
     * BODy only
     * @return
     */
    public Executable parsingBody() {
        return rdParser.oneOrMore("body",
                rdParser.orderedChoise(
                        parsingRule(),
                        rdParser.oneOrMore("spaces",spacesBreaks())
                )
        );
    }

    /**
     * Space symbols filter
     * @return
     */
    private Executable spacesBreaks() {
        return rdParser.parseRegexp("[\\s]");
    }




    /**
     * Parce rule
     *
     * TODO: (not ready)
     * @return
     */
    private Executable parsingRule() {
        return rdParser.sequence("rule",

                ruleName(),
                rdParser.zeroOrMore(spacesBreaks()),
                rdParser.parseString("->"),
                rdParser.zeroOrMore(spacesBreaks()),
                ruleExpression(),
                rdParser.zeroOrMore(spacesBreaks()),
                rdParser.parseString(";"),
                rdParser.zeroOrMore(spacesBreaks())

        );
    }

    /**
     * Parse rule name
     *
     * js parsing_rule_name
     * @return
     */
    private Executable ruleName() {
        return rdParser.sequence("rule_name",
                rdParser.parseRegexp("[a-zA-Z_]"),
                rdParser.zeroOrMore(rdParser.parseRegexp("[a-zA-Z0-9_]"))
        );
    }

    /**
     * Parse rule Expression
     *
     * js parsing_expression
     * @return
     */
    public Executable ruleExpression() {
        return rdParser.orderedChoise(
                parsingSequence(),
                parsingOrderedChoice(),
                parsingSubExpression()
        );
    }



    private Executable parsingSequence() {

        return rdParser.sequence("sequence",
                rdParser.orderedChoise(
                        parsingOrderedChoice(),
                        parsingSubExpression()
                ),
                rdParser.oneOrMore("sequence_args",
                        rdParser.sequence("sequence_arg",
                                rdParser.oneOrMore("spaces",spacesBreaks()),
                                rdParser.orderedChoise(
                                        parsingOrderedChoice(),
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
                                rdParser.oneOrMore("spaces",spacesBreaks()),
                                rdParser.parseString("/"),
                                rdParser.oneOrMore("spaces",spacesBreaks()),
                                parsingSubExpression()
                        )
                )
        );
    }

    private Executable parsingSubExpression() {
        return rdParser.sequence("sub_expression",

                rdParser.zeroOrMore(rdParser.sequence("tags",
                        tag(),
                        rdParser.parseString(":")

                )),
                rdParser.orderedChoise(
                        parsingNot(),
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
                rdParser.zeroOrMore( rdParser.parseRegexp("[a-zA-Z0-9_]"))
        );
    }


    private Executable parsingGroup() {
        return rdParser.sequence("group",
                rdParser.parseString("("),
                rdParser.zeroOrMore(spacesBreaks()),
                rdParser.rec(this),
                rdParser.zeroOrMore(spacesBreaks()),
                rdParser.parseString(")")
        );
    }

    private Executable parsingAtomicExpression() {
        return rdParser.orderedChoise(
                parseString(),
                parseRegex(),
                rdParser.parseEndOfFile(),
                parsingRuleCall()
        );
    }


    /**
     * js parsing_rule_call()
     * @return
     */
    private Executable parsingRuleCall() {
        return ruleName();
    }

    private Executable parseString() {
        return rdParser.sequence("string",
                rdParser.parseString("\""),
                rdParser.oneOrMore("string",rdParser.orderedChoise(
                        rdParser.parseString("\\\\"),
                        rdParser.parseString("\\\""),
                        rdParser.parseRegexp("[^\"]")
                )),
                rdParser.parseString("\"")
        );
    }

    private Executable parseRegex() {
        return rdParser.orderedChoise(
                rdParser.sequence("regex",
                        rdParser.parseString("["),
                        rdParser.optional(rdParser.parseString("^")),
                        rdParser.oneOrMore("regex[]",rdParser.orderedChoise(
                                rdParser.parseString("\\]"),
                                rdParser.parseString("\\["),
                                rdParser.parseRegexp("[^\\]]")
                        )),
                        rdParser.parseString("]")
                ),
                rdParser.parseString(".")
        );
    }


    private Executable parsingNot(){
        return rdParser.sequence("not",
                rdParser.parseString("!"),
                rdParser.orderedChoise(
                        parsingGroup(),
                        parsingAtomicExpression()
                )
        );
    }

    private Executable parsingAnd(){
        return rdParser.sequence("and",
                rdParser.parseString("&"),
                rdParser.orderedChoise(
                        parsingGroup(),
                        parsingAtomicExpression()
                )
        );
    }

    private Executable parsingOneOrMore(){
        return rdParser.sequence("one_or_more",
                rdParser.orderedChoise(
                        parsingGroup(),
                        parsingAtomicExpression()
                ),
                rdParser.parseString("+")
        );
    }


    private Executable parsingZeroOrMore(){
        return rdParser.sequence("zero_or_more",
                rdParser.orderedChoise(
                        parsingGroup(),
                        parsingAtomicExpression()
                ),
                rdParser.parseString("*")
        );
    }

    private Executable parsingOptional(){
        return rdParser.sequence("optional",
                rdParser.orderedChoise(
                        parsingGroup(),
                        parsingAtomicExpression()
                ),
                rdParser.parseString("?")
        );
    }

}
