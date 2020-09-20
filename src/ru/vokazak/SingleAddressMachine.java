package ru.vokazak;

import java.util.ArrayList;

public class SingleAddressMachine {

    private final int ADDRESS_LENGTH = 5;
    private final int DATA_LENGTH = 5;
    private final int MEMORY_SIZE = 20;

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

        runCommand(0);

    }

    private void runCommand(int commandIndex) {
        String command = commandMemory.get(commandIndex); //TODO command out of command list
        String commandName = command.substring(0, 3);
        String data = command.substring(3);
        int address = Integer.parseInt(data.substring(0, ADDRESS_LENGTH), 2); //TODO address out of bounds
        int info = Integer.parseInt(data.substring(ADDRESS_LENGTH), 2);

        switch (Integer.parseInt(commandName)) {
            case 0:
                System.out.println("LOAD");
                acc = info;
                System.out.println("\tAcc: " + acc);
                runCommand(commandIndex + 1);
                break;

            case 1:
                System.out.println("PUT " + acc + " to cell " + address);
                dataMemory.set(address, acc);
                System.out.println("\tData memory: " + dataMemory);
                runCommand(commandIndex + 1);
                break;

            case 10:
                System.out.println("GET (" + address + ") ");
                if (info == 0)
                    acc = dataMemory.get(address);
                else acc = dataMemory.get(dataMemory.get(address));
                System.out.println("\tAcc: " + acc);
                runCommand(commandIndex + 1);
                break;

            case 11:
                System.out.println("INC");
                acc ++;
                System.out.println("\tAcc: " + acc); //TODO value out of range (>31)
                runCommand(commandIndex + 1);
                break;

            case 100:
                System.out.println("COMP");
                if (info == 0)
                    compare(dataMemory.get(address));
                else compare(dataMemory.get(dataMemory.get(address)));
                System.out.println("\tLess: " + flagLess + ", Equals: " + flagEquals + ", Greater: " + flagGreater);
                if (!flagLess)
                    runCommand(commandIndex + 1);
                else runCommand(commandIndex + 2);
                break;

            case 101:
                System.out.println("JUMP");
                runCommand(address);
                break;

            case 111:
                System.out.println("DONE");
                return;

            default:
                System.out.println("BRUH");
                break;
        }
    }

    private void compare(int data) {
        System.out.println("\tAcc: " + acc + ", Data: " + data);
        switch (Integer.compare(data, acc)) {
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
