package ru.vokazak;

public class Test {

    public static void main(String[] args) {
        new Parser(new Lexer("Input.txt").getLexemList());
    }
}
