package ru.vokazak;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

class Lexer {
    private StringBuilder input = new StringBuilder();
    private Token token;
    private String lexeme;
    private boolean exhausted = false;
    private String lexerErrorMessage = "";
    private Set<Character> blankChars = new HashSet<>();

    private ArrayList<Lexem> lexemes = new ArrayList<>();

    Lexer(String filePath) {
        try (Stream<String> st = Files.lines(Paths.get(filePath))) {
            st.forEach(input::append);
        } catch (IOException ex) {
            exhausted = true;
            lexerErrorMessage = "Lexer error: Could not read file: " + filePath;
            return;
        }

        blankChars.add('\r');
        blankChars.add('\n');
        blankChars.add((char) 8);   //BS (backspace)
        blankChars.add((char) 9);   //HT (horizontal tabulation)
        blankChars.add((char) 11);  //VT (vertical tabulation)
        blankChars.add((char) 12);  //FF (form feed - перевод страницы)
        blankChars.add((char) 32);  //space

        moveAhead();

        while (!exhausted) {
            lexemes.add(new Lexem(token, lexeme));
            moveAhead();
        }

    }

    ArrayList<Lexem> getLexemeList() {
        return lexemes;
    }

    void printLexemeList() {
        System.out.println("Lexeme list:");
        for (Lexem lexeme: lexemes)
            System.out.println("\t" + lexeme.getValue());
    }

    boolean isLexerErrorMessage() {
        if (!lexerErrorMessage.isEmpty()) {
            return true;
        } else return false;
    }

    String getLexerErrorMessage() {
        return lexerErrorMessage;
    }

    private void moveAhead() {
        if (exhausted) {
            return;
        }

        if (input.length() == 0) {
            exhausted = true;
            return;
        }

        ignoreWhiteSpaces();

        if (findNextToken()) {
            return;
        }

        exhausted = true;

        if (input.length() > 0) {
            lexerErrorMessage = "Lexer error: Unexpected symbol: '" + input.charAt(0) + "'";
        }
    }

    private void ignoreWhiteSpaces() {
        int charsToDelete = 0;

        while (blankChars.contains(input.charAt(charsToDelete))) {
            charsToDelete ++;
        }

        if (charsToDelete > 0) {
            input.delete(0, charsToDelete);
        }
    }

    private boolean findNextToken() {
        for (Token t : Token.values()) {
            int end = t.endOfMatch(input.toString());

            if (end != -1) {
                token = t;
                lexeme = input.substring(0, end);
                input.delete(0, end);
                return true;
            }
        }
        return false;
    }

}