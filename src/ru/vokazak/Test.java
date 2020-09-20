package ru.vokazak;

public class Test {

    public static void main(String[] args) {
        new SingleAddressMachine(new Parser(new Lexer("Input.txt").getLexemList()).getCommandList());
    }
}
