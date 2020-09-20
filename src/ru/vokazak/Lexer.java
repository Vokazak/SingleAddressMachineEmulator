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
    private String errorMessage = "";
    private Set<Character> blankChars = new HashSet<>();

    private ArrayList<Lexem> lexemes = new ArrayList<>();

    Lexer(String filePath) {
        try (Stream<String> st = Files.lines(Paths.get(filePath))) {
            st.forEach(input::append);
        } catch (IOException ex) {
            exhausted = true;
            errorMessage = "Could not read file: " + filePath;
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

        if (!errorMessage.isEmpty())
            System.out.println(errorMessage);

    }

    ArrayList<Lexem> getLexemList() {
        return lexemes;
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
            errorMessage = "Unexpected symbol: '" + input.charAt(0) + "'";
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