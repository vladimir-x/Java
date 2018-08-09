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
                res.setExecName(str);

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

    /**
     * Последовательно выполняет все exec.
     * Возвращает ERROR, если вернулся хотя бы один ERROR
     * EMPTY - если все exec вернули EMPTY
     * OK - если все exec вернули OK(минимум один) или EMPTY
     *
     * @param execName
     * @param execs
     * @return
     */
    public Executable sequence(final String execName, final Executable... execs) {
        return new Executable() {

            @Override
            public PegNode exec(State state) {
                PegNode res = new PegNode();
                res.setType(SpegTypes.SEQUENCE);
                res.setExecName(execName);

                for (Executable exec : execs) {
                    State stateCp = state.copy();
                    PegNode pegNode = exec.exec(stateCp);

                    switch (pegNode.getResultType()) {
                        case OK:
                            res.appendChild(pegNode);
                            res.setResultType(ResultType.OK);
                            state.setPosition(stateCp.getPosition());
                            break;
                        case EMPTY:

                            break;
                        case ERROR:
                            res.setResultType(ResultType.ERROR);
                            res.setError(" sequence " + execName + " lastPos = " + stateCp.getPosition() + " unexpected " + stateCp.atPos());
                            state.setPosition(stateCp.getPosition());
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

    /**
     * Последовательное выполнение. Первая, полнившаяся OK возвращается как результат.
     * Если вернулись все пустые: возвращает EMPTY
     * Если в результатах exec были ERROR или EMPTY - возвращает ERROR
     *
     * @param execs
     * @return
     */
    public Executable orderedChoice(final String execName, final Executable... execs) {
        return new Executable() {

            @Override
            public PegNode exec(State state) {
                PegNode res = new PegNode();
                res.setType(SpegTypes.ORDERED_CHOICE);
                res.setExecName(execName);

                boolean hasEmpty = false;
                boolean hasError = false;

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
                            hasEmpty = true;
                            //return res;
                            break;
                        case ERROR:
                            res.setResultType(ResultType.ERROR);
                            res.setError(" orderedChoice " + " lastPos = " + state.getPosition() + " unexpected " + state.atPos());
                            hasError = true;
                            break;

                    }
                }

                if (hasError) {
                    res.setResultType(ResultType.ERROR);
                } else {
                    if (hasEmpty) {
                        res.setResultType(ResultType.EMPTY);
                    }
                }
                return res;
            }
        };

    }

    /**
     * Выплняет exec, добавляя OK руезльутаты в child
     * Возвращает OK если добавленых child > 0 иначе ERROR
     *
     * @param execName
     * @param exec
     * @return
     */
    public Executable oneOrMore(final String execName, final Executable exec) {
        return new Executable() {

            @Override
            public PegNode exec(State state) {
                PegNode res = new PegNode();
                res.setType(SpegTypes.ONE_OR_MORE);
                res.setExecName(execName);

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

    /**
     * Выплняет exec, добавляя OK руезльутаты в child
     * Возвращает OK если добавленых child > 0, EMPTY если child = 0
     *
     * @param execName
     * @param exec
     * @return
     */
    public Executable zeroOrMore(final String execName, final Executable exec) {
        return new Executable() {

            @Override
            public PegNode exec(State state) {
                PegNode res = new PegNode();
                res.setType(SpegTypes.ZERO_OR_MORE);
                res.setExecName(execName);

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

    /**
     * Возвращает OK если результат exec, иначе EMPTY
     *
     * @param exec
     * @return
     */
    public Executable optional(final Executable exec) {
        return new Executable() {

            @Override
            public PegNode exec(State state) {
                PegNode res = new PegNode();
                res.setType(SpegTypes.OPTIONAL);

                State statecp = state.copy();
                PegNode pegNode = exec.exec(statecp);
                if (pegNode.getResultType().equals(ResultType.OK)) {
                    res.setResultType(ResultType.OK);
                    res.appendChild(pegNode);
                    state.setPosition(statecp.getPosition());
                } else {
                    res.setResultType(ResultType.EMPTY);
                }

                return res;
            }
        };

    }

    /**
     * Предикат not.
     * По хорошему, внутри он должен двигать position... оставлю пока так
     * @param exec
     * @return
     */
    public Executable not(final Executable exec) {
        return new Executable() {

            @Override
            public PegNode exec(State state) {
                PegNode res = new PegNode();
                res.setType(SpegTypes.NOT);

                State statecp = state.copy();
                PegNode pegNode = exec.exec(statecp);

                switch (pegNode.getResultType()) {
                    case OK:

                        res.setResultType(ResultType.ERROR);
                        res.setError(" not " + " lastPos = " + statecp.getPosition() + " unexpected " + statecp.atPos());

                        break;
                    case ERROR:
                    default:
                        res.setResultType(ResultType.OK);
                        res.appendChild(pegNode);
                        state.setPosition(statecp.getPosition());
                        break;
                }
                return res;
            }
        };

    }

    /**
     * OK если достигнут конец строки данных
     *
     * @return
     */
    public Executable parseEndOfFile() {
        return new Executable() {

            @Override
            public PegNode exec(State state) {

                PegNode res = new PegNode();
                res.setType(SpegTypes.END_OF_FILE);
                if (state.getPosition() >= state.getTextData().length()) {

                    res.setResultType(ResultType.OK);
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
