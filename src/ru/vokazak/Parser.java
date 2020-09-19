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
        for (int i = 0; i < lexemList.size(); i++) {
            if (syntaxError)
                break;
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

    private void checkDAddress (String command) {
        if (lexemList.get(globalIndex).getToken() == Token.ADDR) {
            String value = lexemList.get(globalIndex).getValue();
            value = value.substring(1, value.length() - 1);
            commands.add(command + getBinaryString(value) + "xxxxx");
            globalIndex++;
            syntaxError = false;
        } else syntaxError = true;
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
        else if (lexemList.get(globalIndex).getToken() == Token.CMD_GET && !syntaxError) {
            checkAddress("010");
            if (syntaxError)
                checkDAddress("010");
        } else if (lexemList.get(globalIndex).getToken() == Token.CMD_INC && !syntaxError) {
            //globalIndex ++;
            commands.add("011" + lexemList.get(globalIndex).getValue());
            globalIndex ++;
        } else if (lexemList.get(globalIndex).getToken() == Token.CMD_COMP && !syntaxError) {
            checkAddress("100");
            if (syntaxError)
                checkDAddress("100");
        } else if (lexemList.get(globalIndex).getToken() == Token.CMD_CALL && !syntaxError) {
            globalIndex ++;
            commands.add("101" + lexemList.get(globalIndex).getValue());
            globalIndex ++;
        } else if (lexemList.get(globalIndex).getToken() == Token.LABEL_NAME && !syntaxError) {
            //globalIndex ++;
            commands.add("110" + lexemList.get(globalIndex).getValue());
            globalIndex ++;
        } else if (lexemList.get(globalIndex).getToken() == Token.LABEL && !syntaxError) {
            //globalIndex ++;
            commands.add("111" + lexemList.get(globalIndex).getValue());
            globalIndex ++;
        } else {
            syntaxError = true;
            System.out.println("Error: " + syntaxError);
            System.out.println(commands);
        }
    }

}
