package ru.vokazak;

import java.util.ArrayList;

class Parser {
    private ArrayList<Lexem> lexemeList;
    private int globalIndex;
    private boolean isExhausted;

    private String parserErrorMessage = "";
    private String log = "";

    private ArrayList<String> commandList = new ArrayList<>();
    private ArrayList<Loop> loops = new ArrayList();

    Parser(ArrayList<Lexem> lexemList) {
        this.lexemeList = lexemList;
        globalIndex = 0;
        isExhausted = false;

        while (!isExhausted) {
            if (!parserErrorMessage.isEmpty())
                break;
            parse();
        }

        checkLoops();
    }

    boolean isParserErrorMessage() {
        return !parserErrorMessage.isEmpty();
    }

    public String getParserErrorMessage() {
        return parserErrorMessage;
    }

    ArrayList<String> getCommandList() {
        return commandList;
    }

    void printCommandList() {
        System.out.println("\nCommand list:");
        for (int i = 0; i < commandList.size(); i++) {
            System.out.println("\tCommand: " + commandList.get(i) + " (index: " + i + ")");
        }
    }

    String getParserLog() {
        log = log.concat("\nCommand list:\n");
        for (int i = 0; i < commandList.size(); i++) {
            log = log.concat("\tCommand: " + commandList.get(i) + " (index: " + i + ")\n");
        }
        return log;
    }

    private String getBinaryString(String decimalString) {
        int decimal = Integer.parseInt(decimalString, 10);
        if (decimal < 32) {
            String binaryString = Integer.toBinaryString(decimal);
            while (binaryString.length() < 5)
                binaryString = "0".concat(binaryString);
            return binaryString;
        } else {
            parserErrorMessage = "Parser error: Argument must be less than 31)";
            return "";
        }
    }

    private void setIndirectAddress(String command) {
        if (lexemeList.get(globalIndex).getToken() == Token.ADDR) {
            String value = lexemeList.get(globalIndex).getValue();
            value = value.substring(1, value.length() - 1);
            commandList.add(command + getBinaryString(value) + "11111");
        } else parserErrorMessage = "Parser error: Expected integer, found " + lexemeList.get(globalIndex).getValue();
    }

    private void setDirectAddress(String command) {
       globalIndex ++;
        if (lexemeList.get(globalIndex).getToken() == Token.INTEGER) {
            commandList.add(command + getBinaryString(lexemeList.get(globalIndex).getValue()) + "00000");
        } else setIndirectAddress(command);
    }

    private void checkData() {
        globalIndex ++;
        if (lexemeList.get(globalIndex).getToken() == Token.INTEGER) {
            commandList.add("00000000" + getBinaryString(lexemeList.get(globalIndex).getValue()));
        } else parserErrorMessage = "Parser error: Expected integer, found " + lexemeList.get(globalIndex).getValue();
    }

    private void checkLabel() {
        String loopName = lexemeList.get(globalIndex).getValue();
        loopName = loopName.substring(0, loopName.length() - 1);
        loops.add(new Loop(loopName, commandList.size()));

        for (int i = 0; i < commandList.size(); i++) {
            if (commandList.get(i).substring(3).equals(loopName)) {
                commandList.set(i, "101" + getBinaryString(String.valueOf(commandList.size())) + "00000");
            }
        }
    }

    private void setJumpAddress() {
        boolean found = false;
        globalIndex ++;
        if (lexemeList.get(globalIndex).getToken() == Token.LABEL_NAME) {
            if (!loops.isEmpty()) {
                for (Loop loop : loops) {
                    if (lexemeList.get(globalIndex).getValue().equals(loop.getName())) {
                        commandList.add("101" + getBinaryString(String.valueOf(loop.getJumpAddress())) + "00000");
                        found = true;
                    }
                }
            }

            if (!found) {
                commandList.add("101" + lexemeList.get(globalIndex).getValue());
            }
        } else parserErrorMessage = "Parser error: Expected loop name, found " + lexemeList.get(globalIndex).getValue();
    }
    
    private void checkLoops() {
        if (!loops.isEmpty()) {
            for (String command : commandList) {
                if (command.substring(0, 3).equals("101") && (command.length() != 13 || !command.substring(8).equals("00000"))) {
                    parserErrorMessage = "Parser error: Loops are incorrect";
                    break;
                }
            }
        }
    }

    private void parse() {

        if (lexemeList.size() <= globalIndex) {
            isExhausted = true;
            return;
        }

        Token currentToken = lexemeList.get(globalIndex).getToken();

        if (currentToken == Token.CMD_LOAD && parserErrorMessage.isEmpty())
            checkData();
        else if (currentToken == Token.CMD_PUT && parserErrorMessage.isEmpty())
            setDirectAddress("001");
        else if (currentToken == Token.CMD_GET && parserErrorMessage.isEmpty())
            setDirectAddress("010");
        else if (currentToken == Token.CMD_INC && parserErrorMessage.isEmpty())
            commandList.add("0110000000000");
        else if (currentToken == Token.CMD_COMP && parserErrorMessage.isEmpty())
            setDirectAddress("100");
        else if (currentToken == Token.CMD_JMP && parserErrorMessage.isEmpty())
            setJumpAddress();
        else if (currentToken == Token.LABEL && parserErrorMessage.isEmpty())
            checkLabel();
        else if (currentToken == Token.END && parserErrorMessage.isEmpty())
            commandList.add("1110000000000");
        else
            parserErrorMessage = "Parser error: Found an argument without command";

        globalIndex ++;
    }

}
