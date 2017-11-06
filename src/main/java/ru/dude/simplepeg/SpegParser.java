package ru.dude.simplepeg;

import ru.dude.simplepeg.entity.PegNode;
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

        return rdParser.sequence(
                rdParser.zeroOrMore(spacesBreaks()),
                parsingHeader(),
                rdParser.oneOrMore(spacesBreaks()),
                parsingBody(),
                rdParser.parseEndOfFile()
        );

    }

    /**
     * HEADER only
     * @return
     */
    public Executable parsingHeader() {
        return rdParser.sequence(
                rdParser.parseString("GRAMMAR"),
                rdParser.oneOrMore(spacesBreaks()),
                rdParser.oneOrMore(ruleName())
        );
    }


    /**
     * BODy only
     * @return
     */
    public Executable parsingBody() {
        return rdParser.oneOrMore(
                rdParser.orderedChoise(
                        parsingRule(),
                        rdParser.oneOrMore(spacesBreaks())
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
        return rdParser.sequence(

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
        return rdParser.sequence(
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

        return rdParser.sequence(
                rdParser.orderedChoise(
                        parsingOrderedChoice(),
                        parsingSubExpression()
                ),
                rdParser.oneOrMore(
                        rdParser.sequence(
                                rdParser.oneOrMore(spacesBreaks()),
                                rdParser.orderedChoise(
                                        parsingOrderedChoice(),
                                        parsingSubExpression()
                                )
                        )
                )
        );
    }

    private Executable parsingOrderedChoice() {
        return rdParser.sequence(
                parsingSubExpression(),
                rdParser.oneOrMore(
                        rdParser.sequence(
                                rdParser.oneOrMore(spacesBreaks()),
                                rdParser.parseString("/"),
                                rdParser.oneOrMore(spacesBreaks()),
                                parsingSubExpression()
                        )
                )
        );
    }

    private Executable parsingSubExpression() {
        return rdParser.orderedChoise(
                parsingNot(),
                parsingAnd(),
                parsingOptional(),
                parsingOneOrMore(),
                parsingZeroOrMore(),
                parsingGroup(),
                parsingAtomicExpression()

        );

/*
        return rdParser.sequence(

                rdParser.zeroOrMore(rdParser.sequence(
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
        */
    }


    private Executable tag() {
        return rdParser.sequence(
                rdParser.parseRegexp("[a-zA-Z_]"),
                rdParser.zeroOrMore( rdParser.parseRegexp("[a-zA-Z0-9_]"))
        );
    }


    private Executable parsingGroup() {
        return rdParser.sequence(
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
        return rdParser.sequence(
                rdParser.parseString("\""),
                rdParser.oneOrMore(rdParser.orderedChoise(
                        rdParser.parseString("\\\\"),
                        rdParser.parseString("\\\""),
                        rdParser.parseRegexp("[^\"]")
                )),
                rdParser.parseString("\"")
        );
    }

    private Executable parseRegex() {
        return rdParser.orderedChoise(
                rdParser.sequence(
                        rdParser.parseString("["),
                        rdParser.optional(rdParser.parseString("^")),
                        rdParser.oneOrMore(rdParser.orderedChoise(
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
        return rdParser.sequence(
                rdParser.parseString("!"),
                rdParser.orderedChoise(
                        parsingGroup(),
                        parsingAtomicExpression()
                )
        );
    }

    private Executable parsingAnd(){
        return rdParser.sequence(
                rdParser.parseString("&"),
                rdParser.orderedChoise(
                        parsingGroup(),
                        parsingAtomicExpression()
                )
        );
    }

    private Executable parsingOneOrMore(){
        return rdParser.sequence(
                rdParser.orderedChoise(
                        parsingGroup(),
                        parsingAtomicExpression()
                ),
                rdParser.parseString("+")
        );
    }


    private Executable parsingZeroOrMore(){
        return rdParser.sequence(
                rdParser.orderedChoise(
                        parsingGroup(),
                        parsingAtomicExpression()
                ),
                rdParser.parseString("*")
        );
    }

    private Executable parsingOptional(){
        return rdParser.sequence(
                rdParser.orderedChoise(
                        parsingGroup(),
                        parsingAtomicExpression()
                ),
                rdParser.parseString("?")
        );
    }

}
