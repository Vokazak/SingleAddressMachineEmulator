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

    private String machineErrorMessage = "";
    private String log = "\nMachine work:\n";

    SingleAddressMachine(ArrayList<String> commandMemory) {
        this.commandMemory = commandMemory;
        //System.out.println(commandMemory);

        for (int i = 0; i < MEMORY_SIZE; i++) {
            dataMemory.add(0);
        }

        runCommand(0);
    }


    public String getMachineErrorMessage() {
        return machineErrorMessage;
    }

    boolean isMachineErrorMessage() {
        return !machineErrorMessage.isEmpty();
    }

    private boolean accIsValid() {
        if (acc > 31) {
            machineErrorMessage = "Machine error: Value in ACC got out of bounds ( must be less than 31)";
            return false;
        } else return true;
    }

    String getMachineLog() {
        return log;
    }

    private boolean addressIsValid(int address) {
        if (address >= MEMORY_SIZE) {
            machineErrorMessage = "Machine error: Address should be lees than " + MEMORY_SIZE;
            return false;
        } else return true;
    }

    private void runCommand(int commandIndex) {
        if (!machineErrorMessage.isEmpty())
            return;

        String command = commandMemory.get(commandIndex);
        String commandName = command.substring(0, 3);
        String data = command.substring(3);
        int address = Integer.parseInt(data.substring(0, ADDRESS_LENGTH), 2);
        int info = Integer.parseInt(data.substring(ADDRESS_LENGTH), 2);

        switch (Integer.parseInt(commandName)) {
            case 0:
                //System.out.println("LOAD");
                log = log.concat("LOAD\n");
                acc = info;
                //System.out.println("\tAcc: " + acc);
                log = log.concat("\tAcc: " + acc + "\n");
                runCommand(commandIndex + 1);
                break;

            case 1:
                //System.out.println("PUT " + acc + " to cell " + address);
                log = log.concat("PUT " + acc + " to cell " + address + "\n");
                if (!addressIsValid(address))
                    break;
                dataMemory.set(address, acc);
                //System.out.println("\tData memory: " + dataMemory);
                log = log.concat("\tData memory: " + dataMemory + "\n");
                runCommand(commandIndex + 1);
                break;

            case 10:
                 //System.out.println("GET (" + address + ") ");
                 log = log.concat("GET (" + address + ")\n");
                 if (!addressIsValid(address))
                     break;
                 if (info == 0)
                     acc = dataMemory.get(address);
                 else acc = dataMemory.get(dataMemory.get(address));
                 //System.out.println("\tAcc: " + acc);
                 log = log.concat("\tAcc:" + acc + "\n");
                 runCommand(commandIndex + 1);
                 break;

            case 11:
                //System.out.println("INC");
                log = log.concat("INC\n");
                acc ++;
                if (!accIsValid())
                    break;
                //System.out.println("\tAcc: " + acc);
                log = log.concat("\tAcc: " + acc + "\n");
                runCommand(commandIndex + 1);
                break;

            case 100:
                //System.out.println("COMP");
                log = log.concat("COMP\n");
                if (!addressIsValid(address))
                    break;
                if (info == 0)
                    compare(dataMemory.get(address));
                else compare(dataMemory.get(dataMemory.get(address)));
                //System.out.println("\tLess: " + flagLess + ", Equals: " + flagEquals + ", Greater: " + flagGreater);
                log = log.concat("\tLess: " + flagLess + ", Equals: " + flagEquals + ", Greater: " + flagGreater  + "\n");
                if (!flagLess)
                    runCommand(commandIndex + 1);
                else runCommand(commandIndex + 2);
                break;

            case 101:
                 //System.out.println("JUMP");
                 log = log.concat("JUMP\n");
                 runCommand(address);
                 break;

            case 111:
                //System.out.println("DONE");
                log = log.concat("DONE\n");
                return;

            default:
                System.out.println("Unknown command");
                machineErrorMessage = "Machine error: Unknown command " + commandName;
                break;
        }

    }

    private void compare(int data) {
        //System.out.println("\tAcc: " + acc + ", Data: " + data);
        log = log.concat("\tAcc: " + acc + ", Data: " + data + "\n");
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
