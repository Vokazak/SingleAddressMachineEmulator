package ru.vokazak;

public class Test {

    public static void main(String[] args) {
        //new SingleAddressMachine(new Parser(new Lexer("Input.txt").getLexemeList()).getCommandList());

        Lexer lexer = new Lexer("Input.txt");

        if (!lexer.isLexerErrorMessage()) {
            lexer.printLexemeList();

            Parser parser = new Parser(lexer.getLexemeList());

            if (!parser.isParserErrorMessage()) {
                parser.printCommandList();
            } else System.out.println(parser.getParserErrorMessage());

        } else System.out.println(lexer.getLexerErrorMessage());


    }
}
