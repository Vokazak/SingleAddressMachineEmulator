package ru.vokazak;

import java.util.ArrayList;

class Parser {
    private ArrayList<Lexem> lexemeList;
    private int globalIndex;
    private boolean isExhausted;

    private String parserErrorMessage;
    private String log;

    private ArrayList<String> commandList;
    private ArrayList<Loop> loops;

    Parser(ArrayList<Lexem> lexemeList) {
        commandList = new ArrayList<>();
        loops = new ArrayList();

        parserErrorMessage = "";
        log = "";

        this.lexemeList = lexemeList;
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

    String getParserLog() {
        log = log.concat("Command list:\n");
        for (int i = 0; i < commandList.size(); i++) {
            log = log.concat("\tCommand: " + commandList.get(i) + " (index: " + i + ")\n");
        }
        return log;
    }

    private String getBinaryString(String decimalString) {
        int decimal = Integer.parseInt(decimalString, 10);
        if (decimal < Math.pow(2, SAM.FIELD_LENGTH)) {
            String binaryString = Integer.toBinaryString(decimal);
            while (binaryString.length() < SAM.FIELD_LENGTH)
                binaryString = "0".concat(binaryString);
            return binaryString;
        } else {
            parserErrorMessage = "Parser error: Argument must be less than " + (int) Math.pow(2, SAM.FIELD_LENGTH) + ")";
            return "";
        }
    }

    private void setIndirectAddress(String command) {
        if (lexemeList.get(globalIndex).getToken() == Token.ADDR) {
            String value = lexemeList.get(globalIndex).getValue();
            value = value.substring(1, value.length() - 1);
            commandList.add(command + getBinaryString(value) + generateString(SAM.FIELD_LENGTH, true));
        } else parserErrorMessage = "Parser error: Expected integer, found " + lexemeList.get(globalIndex).getValue();
    }

    private void setDirectAddress(String command) {
       globalIndex ++;
        if (lexemeList.get(globalIndex).getToken() == Token.INTEGER) {
            commandList.add(command + getBinaryString(lexemeList.get(globalIndex).getValue()) + generateString(SAM.FIELD_LENGTH, false));
        } else setIndirectAddress(command);
    }

    private void checkData() {
        globalIndex ++;
        if (lexemeList.get(globalIndex).getToken() == Token.INTEGER) {
            commandList.add(generateString(SAM.COMMAND_LENGTH + SAM.FIELD_LENGTH, false) + getBinaryString(lexemeList.get(globalIndex).getValue()));
        } else parserErrorMessage = "Parser error: Expected integer, found " + lexemeList.get(globalIndex).getValue();
    }

    private void checkLabel() {
        String loopName = lexemeList.get(globalIndex).getValue();
        loopName = loopName.substring(0, loopName.length() - 1);
        loops.add(new Loop(loopName, commandList.size()));

        for (int i = 0; i < commandList.size(); i++) {
            if (commandList.get(i).substring(SAM.COMMAND_LENGTH).equals(loopName)) {
                commandList.set(i, "0101" + getBinaryString(String.valueOf(commandList.size())) + generateString(SAM.FIELD_LENGTH, false));
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
                        commandList.add("0101" + getBinaryString(String.valueOf(loop.getJumpAddress())) + generateString(SAM.FIELD_LENGTH, false));
                        found = true;
                    }
                }
            }

            if (!found) {
                commandList.add("0101" + lexemeList.get(globalIndex).getValue());
            }
        } else parserErrorMessage = "Parser error: Expected loop name, found " + lexemeList.get(globalIndex).getValue();
    }
    
    private void checkLoops() {
        if (!loops.isEmpty())
            for (String command : commandList)
                if (command.substring(0, SAM.COMMAND_LENGTH).equals("0101") &&
                        (command.length() != (SAM.COMMAND_LENGTH + SAM.FIELD_LENGTH * 2)
                                || !command.substring(SAM.COMMAND_LENGTH + SAM.FIELD_LENGTH).equals(generateString(SAM.FIELD_LENGTH, false)))) {
                    parserErrorMessage = "Parser error: Loops are incorrect";
                    break;
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
            setDirectAddress("0001");
        else if (currentToken == Token.CMD_GET && parserErrorMessage.isEmpty())
            setDirectAddress("0010");
        else if (currentToken == Token.CMD_INC && parserErrorMessage.isEmpty())
            commandList.add("0011" + generateString(SAM.FIELD_LENGTH * 2, false));
        else if (currentToken == Token.CMD_COMP && parserErrorMessage.isEmpty())
            setDirectAddress("0100");
        else if (currentToken == Token.CMD_JMP && parserErrorMessage.isEmpty())
            setJumpAddress();
        else if (currentToken == Token.LABEL && parserErrorMessage.isEmpty())
            checkLabel();
        else if (currentToken == Token.CMD_ADD && parserErrorMessage.isEmpty())
            setDirectAddress("0110");
        else if (currentToken == Token.CMD_MUL && parserErrorMessage.isEmpty())
            setDirectAddress("1001");
        else if (currentToken == Token.CMD_PUT2 && parserErrorMessage.isEmpty())
            setDirectAddress("1010");
        else if (currentToken == Token.END && parserErrorMessage.isEmpty())
            commandList.add("0111" + generateString(SAM.FIELD_LENGTH * 2, false));
        else if (currentToken != Token.COMMENT)
            parserErrorMessage = "Parser error: Found an argument without command";

        globalIndex ++;
    }

    private String generateString(int length, boolean one) {
        String d;
        if (one)
            d = "1";
        else d = "0";

        String s = "";
        for (int i = 0; i < length; i++) {
            s = s.concat(d);
        }

        return s;
    }

}
