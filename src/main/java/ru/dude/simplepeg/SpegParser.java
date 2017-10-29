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
        rdParser = new RdParser(state);
    }

    /**
     * All SPEG
     * @return
     */
    public PegNode peg() {

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
    public PegNode parsingHeader() {
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
    public PegNode parsingBody() {
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
    private PegNode spacesBreaks() {
        return rdParser.parseRegexp("[\\s]");
    }

    /**
     * Parce rule name
     * @return
     */
    private PegNode ruleName() {
        return rdParser.sequence(
                rdParser.parseRegexp("[a-zA-Z_]"),
                rdParser.zeroOrMore(rdParser.parseRegexp("[a-zA-Z0-9_]"))
        );
    }


    /**
     * Parce rule
     *
     * TODO: (not ready)
     * @return
     */
    private PegNode parsingRule() {
        return rdParser.sequence(
                rdParser.zeroOrMore(rdParser.parseRegexp("[a-zA-Z0-9_]")),
                rdParser.zeroOrMore(spacesBreaks()),
                rdParser.parseString("->"),
                rdParser.zeroOrMore(spacesBreaks()),
                rdParser.zeroOrMore(rdParser.parseRegexp("[a-zA-Z0-9_]"))

        );
    }


}