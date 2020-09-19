package ru.vokazak;

public class Lexem {
    private Token token;
    private String value;

    Lexem(Token token, String value) {
        this.token = token;
        this.value = value;
    }

    public Token getToken() {
        return token;
    }

    public String getValue() {
        return value;
    }
}
