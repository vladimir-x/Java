package ru.dude.simplepeg.entity;

/**
 * SPEG names.
 *
 * Имя узла может нести семантическое значение узла (например header или only_letter_rule).
 * Здесь собраны имена узлов, определяемых при парсинге грамматики, и используемые для валидации по грамматике.
 */
public enum SpegNames {
    NAME_OTHER(""),
    NAME_STRING("string"),
    NAME_REGEX("regex"),
    NAME_SEQUENCE("sequence"),
    NAME_ORDERED_CHOCE("ordered_choice"),
    NAME_ONE_OR_MORE("one_or_more"),
    NAME_ZERO_OR_MORE("zero_or_more"),
    NAME_NOT("not"),
    NAME_AND("and"),
    NAME_OPTIONAL("optional"),
    NAME_RULE_EXPRESSION("rule_expression"),
    ;

    private String spegName;

    SpegNames(String spegName) {

        this.spegName = spegName;
    }

    public String getSpegName() {
        return spegName;
    }

    public static SpegNames bySpegName(String spegName) {
        for (SpegNames sn : values()) {
            if (sn.getSpegName().equals(spegName)) {
                return sn;
            }
        }
        return NAME_OTHER;
    }
}
