package ru.vokazak;

import java.util.ArrayList;

class Parser {
    private ArrayList<Lexem> lexemList;
    private int globalIndex;
    private boolean syntaxError;
    private boolean isExhausted;

    private ArrayList<String> commandList = new ArrayList<>();
    private ArrayList<Loop> loops = new ArrayList();

    Parser(ArrayList<Lexem> lexemList) {
        this.lexemList = lexemList;
        globalIndex = 0;
        syntaxError = false;
        isExhausted = false;

        while (!isExhausted) {
            if (syntaxError)
                break;
            parse();
        }
        /*
        checkLoops();

        for (Loop loop: loops) {
            System.out.println("Name: " + loop.getName());
            System.out.println("Jump address: " + loop.getJumpAddress());
        }

        for (int i = 0; i < commandList.size(); i++) {
            System.out.println("Command: " + commandList.get(i) + " (index: " + i + ")");
        }

         */
    }

    ArrayList<String> getCommandList() {
        return commandList;
    }

    private String getBinaryString(String decimalString) {
        String binaryStirng = Integer.toBinaryString(Integer.parseInt(decimalString, 10));
        while (binaryStirng.length() <5)
            binaryStirng = "0".concat(binaryStirng);
        return binaryStirng;
    }

    private void setIndirectAddress(String command) {
        if (lexemList.get(globalIndex).getToken() == Token.ADDR) {
            String value = lexemList.get(globalIndex).getValue();
            value = value.substring(1, value.length() - 1);
            commandList.add(command + getBinaryString(value) + "11111");
        } else syntaxError = true;
    }

    private void setDirectAddress(String command) {
       globalIndex ++;
        if (lexemList.get(globalIndex).getToken() == Token.INTEGER) {
            commandList.add(command + getBinaryString(lexemList.get(globalIndex).getValue()) + "00000");
        } else setIndirectAddress(command);
    }

    private void checkData() {
        globalIndex ++;
        if (lexemList.get(globalIndex).getToken() == Token.INTEGER) {
            commandList.add("00000000" + getBinaryString(lexemList.get(globalIndex).getValue()));
        } else syntaxError = true;
    }

    private void checkLabel() {
        String loopName = lexemList.get(globalIndex).getValue();
        loopName = loopName.substring(0, loopName.length() - 1);
        loops.add(new Loop(loopName, commandList.size()));

        for (int i = 0; i < commandList.size(); i++) {
            if (commandList.get(i).substring(3).equals(loopName)) {
                commandList.set(i, "101" + getBinaryString(String.valueOf(commandList.size())) + "00000");
                //commandList.remove(i + 1);
            }
        }
        //commandList.add("110" + loopName);
    }

    private void setJumpAddress() {
        boolean found = false;
        globalIndex ++;
        if (lexemList.get(globalIndex).getToken() == Token.LABEL_NAME) {
            if (!loops.isEmpty()) {
                for (Loop loop : loops) {
                    if (lexemList.get(globalIndex).getValue().equals(loop.getName())) {
                        commandList.add("101" + getBinaryString(String.valueOf(loop.getJumpAddress())) + "00000");
                        found = true;
                    }
                }
            }

            if (!found) {
                commandList.add("101" + lexemList.get(globalIndex).getValue());
            }
        } else syntaxError = true;
    }
    
    private void checkLoops() { //TODO check loops
        if (!loops.isEmpty()) {
            for (String command : commandList) {
                if (command.contains("[a-z]+"))
                    syntaxError = true;
            }
            if (syntaxError) System.out.println("Loops are incorrect");
        } else System.out.println("Loops are not found");
    }

    private void parse() {

        if (lexemList.size() <= globalIndex) {
            isExhausted = true;
            return;
        }

        Token currentToken = lexemList.get(globalIndex).getToken();

        if (currentToken == Token.CMD_LOAD && !syntaxError)
            checkData();
        else if (currentToken == Token.CMD_PUT && !syntaxError) //TODO: index out of bounds when put
            setDirectAddress("001");
        else if (currentToken == Token.CMD_GET && !syntaxError) //TODO: index out of bounds when get
            setDirectAddress("010");
        else if (currentToken == Token.CMD_INC && !syntaxError)
            commandList.add("0110000000000");
        else if (currentToken == Token.CMD_COMP && !syntaxError)
            setDirectAddress("100");
        else if (currentToken == Token.CMD_JMP && !syntaxError)
            setJumpAddress();
        else if (currentToken == Token.LABEL && !syntaxError)
            checkLabel();
        else if (currentToken == Token.END && !syntaxError)
            commandList.add("1110000000000");
        else
            syntaxError = true;

        globalIndex ++;
    }

}
