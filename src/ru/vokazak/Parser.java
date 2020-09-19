package ru.vokazak;

import java.util.ArrayList;

public class Parser {
    private ArrayList<Lexem> lexemList;
    private int globalIndex;
    private boolean syntaxError = false;

    ArrayList<String> commands = new ArrayList<>();

    Parser(ArrayList<Lexem> lexemList) {
        this.lexemList = lexemList;
        globalIndex = 0;
        for (int i = 0; i < lexemList.size()/2; i++) {
            parse();
            System.out.println("Index: " + globalIndex);
        }
        System.out.println(commands);
    }

    private String getBinaryString(String decimalString) {
        String binaryStirng = Integer.toBinaryString(Integer.parseInt(decimalString, 10));
        if (binaryStirng.length() < 5)
            while (binaryStirng.length() <5)
                binaryStirng = "0".concat(binaryStirng);
        return binaryStirng;
    }

    private void checkAddress(String command) {
       globalIndex ++;
        if (lexemList.get(globalIndex).getToken() == Token.INTEGER) {
            commands.add(command + getBinaryString(lexemList.get(globalIndex).getValue()) + "xxxxx");
            globalIndex++;
        } else syntaxError = true;
    }

    private void checkData(String command) {
        globalIndex ++;
        if (lexemList.get(globalIndex).getToken() == Token.INTEGER) {
            commands.add(command + "xxxxx" + getBinaryString(lexemList.get(globalIndex).getValue()));
            globalIndex++;
        } else syntaxError = true;
    }

    private void parse() {
        if (lexemList.get(globalIndex).getToken() == Token.CMD_LOAD && !syntaxError)
            checkData("000");
        else if (lexemList.get(globalIndex).getToken() == Token.CMD_PUT && !syntaxError)
            checkAddress("001");
        else if (lexemList.get(globalIndex).getToken() == Token.CMD_GET && !syntaxError)
            checkAddress("010");
        else if (lexemList.get(globalIndex).getToken() == Token.CMD_INC && !syntaxError) {
            globalIndex ++;
            commands.add("011" + lexemList.get(globalIndex).getValue());
            globalIndex ++;
        } else if (lexemList.get(globalIndex).getToken() == Token.CMD_COMP && !syntaxError)
            checkAddress("100");
        else if (lexemList.get(globalIndex).getToken() == Token.CMD_CALL && !syntaxError) {
            globalIndex ++;
            commands.add("101" + lexemList.get(globalIndex).getValue());
            globalIndex ++;
        } else if (lexemList.get(globalIndex).getToken() == Token.LABEL_NAME && !syntaxError) {
            globalIndex ++;
            commands.add("110" + lexemList.get(globalIndex).getValue());
            globalIndex ++;
        } else if (lexemList.get(globalIndex).getToken() == Token.END && !syntaxError) {
            globalIndex ++;
            commands.add("111" + "END");
            globalIndex ++;
        } else syntaxError = true;

        System.out.println(commands);
    }

}