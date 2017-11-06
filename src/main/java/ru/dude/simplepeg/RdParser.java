package ru.dude.simplepeg;

import ru.dude.simplepeg.entity.PegNode;
import ru.dude.simplepeg.entity.ResultType;
import ru.dude.simplepeg.entity.SpegTypes;
import ru.dude.simplepeg.entity.State;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for PEG business expressions
 * <p>
 * Created by dude on 29.10.2017.
 */
public class RdParser {


    public RdParser() {

    }

    public Executable parseString(final String str) {

        return new Executable() {
            @Override
            public PegNode exec(State state) {
                PegNode res = new PegNode();
                res.setType(SpegTypes.STRING);

                int endPos = state.getPosition() + str.length();
                if (endPos <= state.getTextData().length() &&
                        state.getTextData().substring(state.getPosition(), endPos).equals(str)) {

                    res.addMatch(str);
                    res.setStartPosition(state.getPosition());
                    res.setEndPosition(state.appendPosition(str.length()));
                    res.setResultType(ResultType.OK);
                } else {
                    res.setResultType(ResultType.ERROR);
                    res.setError(" parseString " + str + " lastPos = " + state.getPosition() + " unexpected " + state.atPos());
                }
                return res;
            }
        };
    }

    public Executable parseRegexp(final String regexp) {
        return new Executable() {
            @Override
            public PegNode exec(State state) {

                PegNode res = new PegNode();
                res.setType(SpegTypes.REGEXP);

                Pattern pattern = Pattern.compile(regexp);
                Matcher matcher = pattern.matcher(state.getTextData());
                if (matcher.find(state.getPosition()) && state.getPosition() == matcher.start()) {
                    //нашли и у места , где каретка
                    String founded = matcher.group();

                    res.addMatch(founded);
                    res.setStartPosition(matcher.start());
                    res.setEndPosition(state.appendPosition(founded.length()));
                    res.setResultType(ResultType.OK);
                } else {
                    res.setResultType(ResultType.ERROR);
                    res.setError(" parseRegexp " + regexp + " lastPos = " + state.getPosition() + " unexpected " + state.atPos());
                }
                return res;
            }
        };
    }

    public Executable sequence(final Executable... execs) {
        return new Executable() {

            @Override
            public PegNode exec(State state) {
                PegNode res = new PegNode();
                res.setType(SpegTypes.SEQUENCE);

                for (Executable exec : execs) {
                    PegNode pegNode = exec.exec(state);

                    switch (pegNode.getResultType()) {
                        case OK:
                            res.appendChild(pegNode);
                            res.setResultType(ResultType.OK);
                            break;
                        case ERROR:
                            res.setResultType(ResultType.ERROR);
                            res.setError(" sequence " + " lastPos = " + state.getPosition() + " unexpected " + state.atPos());
                            return res;
                    }
                }

                if (res.getChildrens().size() == 0) {
                    res.setResultType(ResultType.EMPTY);
                }
                return res;
            }
        };

    }

    public Executable orderedChoise(final Executable... execs) {
        return new Executable() {

            @Override
            public PegNode exec(State state) {
                PegNode res = new PegNode();
                res.setType(SpegTypes.ORDERED_CHOICE);

                for (Executable exec : execs) {
                    State statecp = state.copy();
                    PegNode pegNode = exec.exec(statecp);

                    switch (pegNode.getResultType()) {
                        case OK:
                            res.appendChild(pegNode);
                            res.setResultType(ResultType.OK);
                            res.setError("");
                            state.setPosition(statecp.getPosition());
                            return res;
                        case EMPTY:
                            res.setResultType(ResultType.EMPTY);
                            res.setError("");
                            return res;
                        case ERROR:
                            res.setResultType(ResultType.ERROR);
                            res.setError(" orderedChoise " + " lastPos = " + state.getPosition() + " unexpected " + state.atPos());

                    }
                }
                return res;
            }
        };

    }

    public Executable oneOrMore(final Executable exec) {
        return new Executable() {

            @Override
            public PegNode exec(State state) {
                PegNode res = new PegNode();
                res.setType(SpegTypes.ONE_OR_MORE);

                PegNode pegNode;
                while ((pegNode = exec.exec(state)).getResultType().equals(ResultType.OK)) {
                    res.appendChild(pegNode);
                }

                if (res.getChildrens().size() > 0) {
                    res.setResultType(ResultType.OK);
                } else {
                    res.setResultType(ResultType.ERROR);
                    res.setError(" oneOrMore " + " lastPos = " + state.getPosition() + " unexpected " + state.atPos());
                }
                return res;
            }
        };
    }

    public Executable zeroOrMore(final Executable exec) {
        return new Executable() {

            @Override
            public PegNode exec(State state) {
                PegNode res = new PegNode();
                res.setType(SpegTypes.ZERO_OR_MORE);

                PegNode pegNode;
                while ((pegNode = exec.exec(state)).getResultType().equals(ResultType.OK)) {
                    res.appendChild(pegNode);
                }

                if (res.getChildrens().size() == 0) {
                    res.setResultType(ResultType.EMPTY);
                } else {
                    res.setResultType(ResultType.OK);
                }
                return res;
            }
        };

    }

    public Executable optional(final Executable exec) {
        return new Executable() {

            @Override
            public PegNode exec(State state) {
                PegNode res = new PegNode();
                res.setType(SpegTypes.OPTIONAL);

                PegNode pegNode = exec.exec(state);
                if (pegNode.getResultType().equals(ResultType.OK)) {
                    res.setResultType(ResultType.OK);
                    res.appendChild(pegNode);
                } else {
                    res.setResultType(ResultType.EMPTY);
                }

                return res;
            }
        };

    }

    public Executable parseEndOfFile() {
        return new Executable() {

            @Override
            public PegNode exec(State state) {

                PegNode res = new PegNode();
                res.setType(SpegTypes.END_OF_FILE);
                if (state.getPosition() >= state.getTextData().length()) {

                    res.setStartPosition(state.getPosition());
                    res.setEndPosition(state.getPosition());
                } else {
                    res.setResultType(ResultType.ERROR);
                    res.setError(" parseEndOfFile " + " lastPos = " + state.getPosition() + " unexpected " + state.atPos());
                }
                return res;
            }
        };
    }

    /**
     *
     * @param spParser
     * @return
     */
    public Executable rec(final SpegParser spParser) {
        return new Executable() {

            @Override
            public PegNode exec(State state) {
                Executable recExec = spParser.ruleExpression();
                return recExec.exec(state);
            }
        };
    }



}
