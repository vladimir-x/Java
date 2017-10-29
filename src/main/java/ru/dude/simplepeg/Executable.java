package ru.dude.simplepeg;

/**
 * For implements in PEG expression
 * Created by dude on 29.10.2017.
 */
public interface Executable {

    /**
     * Execute PEG expression
     * @return
     * @throws ParseInputException
     */
    boolean exec()throws ParseInputException;
}
