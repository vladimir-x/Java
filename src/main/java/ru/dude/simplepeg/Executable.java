package ru.dude.simplepeg;

import ru.dude.simplepeg.entity.PegNode;

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
    PegNode exec();
}
