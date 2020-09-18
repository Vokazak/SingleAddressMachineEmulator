package ru.vokazak;

import java.util.ArrayList;

public class VM {

    private static final int COMMAND_LENGTH = 3;
    private static final int ADDRESS_LENGTH = 5;
    private static final int DATA_LENGTH = 5;
    private static final int MEMORY_SIZE = 10;

    private static ArrayList<Integer> dataMemory = new ArrayList<>();

    private static int acc = 0;
    private static boolean flagLess = false;
    private static boolean flagEquals = false;
    private static boolean flagGreater = false;

    public static void main(String[] args) {

        for (int i = 0; i < MEMORY_SIZE; i++) {
            dataMemory.add(0);
        }

        //Integer.toBinaryString(x)
        decodeString("000xxxxx11011");
        decodeString("00100001xxxxx");
        decodeString("000xxxxx01010");
        decodeString("00100010xxxxx");
        decodeString("10000001");
        decodeString("011xxxxxxxxxx");
        //decodeString("01000001");
    }

    private static void decodeString(String string) {
        decodeCommand(string.substring(0, COMMAND_LENGTH), string.substring(COMMAND_LENGTH));
    }

    private static void decodeCommand(String command, String data) {

        int dat = 0;
        try {
            dat = Integer.parseInt(data.substring(0, ADDRESS_LENGTH), 2);
        } catch (NumberFormatException e) {}

        try {
            dat = Integer.parseInt(data.substring(ADDRESS_LENGTH), 2);
        } catch (NumberFormatException e) {}

        switch (Integer.parseInt(command)) {
            case 0:
                System.out.println("LOAD");
                acc = dat;
                System.out.println("Acc: " + acc);
                break;
            case 1:
                System.out.println("PUT");
                dataMemory.add(dat, acc);
                System.out.println("Data memory: " + dataMemory);
                break;
            case 10:
                System.out.println("GET");
                acc = dataMemory.get(dat);
                System.out.println("Acc: " + acc);
                break;
            case 11:
                System.out.println("INC");
                acc++;
                System.out.println("Acc: " + acc);
                break;
            case 100:
                System.out.println("COMP");
                compare(dataMemory.get(dat));
                System.out.println("Less: " + flagLess + ", Equals: " + flagEquals + ", Greater: " + flagGreater);
                break;
            default:
                System.out.println("BRUH");
                break;
        }
    }

    private static void compare(int data) {
        System.out.println("Acc: " + acc + ", Data: " + data);
        int i = Integer.compare(acc, data);
        switch (i) {
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
