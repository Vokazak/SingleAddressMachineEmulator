package ru.vokazak;

public class Test {

    public static void main(String[] args) {

        Lexer lexer = new Lexer("F:/Projects/VM/Input.txt");

        System.out.println("Lexical Analysis:");
        while (!lexer.isExhausted()) {
            System.out.printf("%-10s : %s \n", lexer.currentLexema(), lexer.currentToken());
            lexer.moveAhead();
        }

        if (!lexer.isSuccessful())
            System.out.println(lexer.errorMessage());

    }
}
