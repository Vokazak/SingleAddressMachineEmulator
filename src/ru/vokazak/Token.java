package ru.vokazak;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Token {

    CMD_LOAD ("LOAD"),
    CMD_PUT ("PUT"),
    CMD_GET ("GET"),
    CMD_INC ("INC"),
    CMD_COMP ("COMP"),
    CMD_CALL ("CALL"),

    LABEL ("[a-z]+:"),
    ADDR ("\\[\\d+\\]"),
    INTEGER ("\\d+"),
    LABEL_NAME ("[a-z]+"),

    COMMENT ("//[\\r\\n]!"),

    END ("END");

    private final Pattern pattern;

    Token(String regex) {
        pattern = Pattern.compile("^" + regex);
    }

    int endOfMatch(String s) {
        Matcher m = pattern.matcher(s);

        if (m.find()) {
            return m.end();
        }
        return -1;
    }
}