package ru.vokazak;

import java.util.ArrayList;

public class SingleAddressMachine {

    private final int COMMAND_LENGTH = 3;
    private final int ADDRESS_LENGTH = 5;
    private final int DATA_LENGTH = 5;
    private final int MEMORY_SIZE = 10;

    private ArrayList<Integer> dataMemory = new ArrayList<>();
    private ArrayList<String> commandMemory = new ArrayList<>();

    private int acc = 0;
    private boolean flagLess = false;
    private boolean flagEquals = false;
    private boolean flagGreater = false;


    SingleAddressMachine(ArrayList<String> commandMemory) {
        this.commandMemory = commandMemory;
        System.out.println(commandMemory);


        for (int i = 0; i < MEMORY_SIZE; i++) {
            dataMemory.add(0);
        }
/*
        decodeString("000xxxxx11011");
        decodeString("00100001xxxxx");
        decodeString("000xxxxx01010");
        decodeString("00100010xxxxx");
        decodeString("10000001");
        decodeString("011xxxxxxxxxx");
 */
    }

    private void decodeString(String string) {
        decodeCommand(string.substring(0, COMMAND_LENGTH), string.substring(COMMAND_LENGTH));
    }

    private void decodeCommand(String command, String s) {
        int address = Integer.parseInt(s.substring(0, ADDRESS_LENGTH), 2);

        switch (Integer.parseInt(command)) {
            case 0:
                System.out.println("LOAD");
                acc = Integer.parseInt(s.substring(ADDRESS_LENGTH), 2);;
                System.out.println("Acc: " + acc);
                break;
            case 1:
                System.out.println("PUT");
                dataMemory.add(address, acc);
                System.out.println("Data memory: " + dataMemory);
                break;
            case 10:
                System.out.println("GET");
                acc = dataMemory.get(address);
                System.out.println("Acc: " + acc);
                break;
            case 11:
                System.out.println("INC");
                acc++;
                System.out.println("Acc: " + acc);
                break;
            case 100:
                System.out.println("COMP");
                compare(dataMemory.get(address));
                System.out.println("Less: " + flagLess + ", Equals: " + flagEquals + ", Greater: " + flagGreater);
                break;
            default:
                System.out.println("BRUH");
                break;
        }
    }

    private void compare(int data) {
        System.out.println("Acc: " + acc + ", Data: " + data);
        switch (Integer.compare(acc, data)) {
            case -1:
                flagLess = true;
                flagEquals = false;
                flagGreater = false;
                break;
            case 0:
                flagLess = false;
                flagEquals = true;
                flagGreater = false;
                break;
            case 1:
                flagLess = false;
                flagEquals = false;
                flagGreater = true;
                break;
        }
    }

}
