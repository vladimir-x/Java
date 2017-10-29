package ru.dude.simplepeg;

import ru.dude.simplepeg.entity.PegNode;
import ru.dude.simplepeg.entity.SpegTypes;
import ru.dude.simplepeg.entity.State;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for PEG business expressions
 *
 * Created by dude on 29.10.2017.
 */
public class RdParser {

    /**
     * Object with processing data
     */
    State state;

    public RdParser(State state) {
        this.state = state;
    }

    public PegNode parseString(final String str) {

        return new PegNode() {
            @Override
            public boolean exec() throws ParseInputException {

                int endPos = state.getPosition() + str.length();
                if (endPos <= state.getTextData().length() &&
                        state.getTextData().substring(state.getPosition(),endPos).equals(str)) {
                    setType(SpegTypes.STRING);
                    addMatch(str);
                    setStartPosition(state.getPosition());
                    setEndPosition(state.appendPosition(str.length()));
                    return true;
                } else {
                    System.err.println(" parseString " + str + " lastPos = " + state.getPosition());
                    return false;
                    //throw new ParseInputException(" parseString " + str + " lastPos = " + state.getPosition());
                }
            }
        };
    }

    public PegNode parseRegexp(final String regexp) {
        return new PegNode() {
            @Override
            public boolean exec() throws ParseInputException {

                Pattern pattern = Pattern.compile(regexp);
                Matcher matcher = pattern.matcher(state.getTextData());
                if (matcher.find(state.getPosition()) && state.getPosition() == matcher.start()) {
                    //нашли и у места , где каретка
                    String founded = matcher.group();
                    setType(SpegTypes.REGEXP);
                    addMatch(founded);
                    setStartPosition(matcher.start());
                    setEndPosition(state.appendPosition(founded.length()));
                    return true;
                } else {
                    System.err.println(" parseRegexp " + regexp + " lastPos = " + state.getPosition());
                    return false;
                }
            }
        };
    }

    public PegNode sequence(final PegNode... nodes) {
        return new PegNode() {

            @Override
            public boolean exec() throws ParseInputException {
                setType(SpegTypes.SEQUENCE);

                for (PegNode pegNode : nodes) {
                    if (pegNode.exec()) {
                        appendChild(pegNode);
                    } else {
                        return false;
                    }
                }
                return true;
            }
        };

    }

    public PegNode orderedChoise(final PegNode... nodes) {
        return new PegNode() {

            @Override
            public boolean exec() throws ParseInputException {
                setType(SpegTypes.ORDERED_CHOICE);

                for (PegNode pegNode : nodes) {
                    if (pegNode.exec()) {
                        appendChild(pegNode);
                        return true;
                    }
                }
                return false;
            }
        };

    }

    public PegNode oneOrMore(PegNode node) {
        return oneOrZeroOrMore(false, node);

    }

    public PegNode zeroOrMore(PegNode node) {
        return oneOrZeroOrMore(true, node);

    }

    private PegNode oneOrZeroOrMore(final boolean canZero, final PegNode node) {
        return new PegNode() {

            @Override
            public boolean exec() throws ParseInputException {
                setType(canZero ? SpegTypes.ZERO_OR_MORE : SpegTypes.ONE_OR_MORE);
                while (node.exec()) {
                    appendChild(node.copyTruncate());
                }
                return canZero || getChildrens().size() > 0;
            }
        };

    }

    public PegNode parseEndOfFile(){
        return new PegNode() {

            @Override
            public boolean exec() throws ParseInputException {

                if (state.getPosition()>=state.getTextData().length()){
                    setType(SpegTypes.END_OF_FILE);
                    setStartPosition(state.getPosition());
                    setEndPosition(state.getPosition());
                    return true;
                } else {
                    System.err.println(" parseEndOfFile " + " lastPos = " + state.getPosition());
                    return false;
                }
            }
        };
    }

}
