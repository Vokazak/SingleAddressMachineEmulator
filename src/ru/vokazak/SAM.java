package ru.vokazak;

import java.util.ArrayList;

public class SAM {

    public static final int COMMAND_LENGTH = 4;
    public static final int FIELD_LENGTH = 7;
    public static final int MEMORY_SIZE = 20;

    private ArrayList<Integer> dataMemory;
    private ArrayList<String> commandMemory;

    private int acc;
    private int acc2;

    private boolean flagLess;
    private boolean flagEquals;
    private boolean flagGreater;
    private boolean flagOvf;

    private String machineErrorMessage;
    private String log;

    SAM(ArrayList<String> commandMemory) {
        this.commandMemory = commandMemory;
        machineErrorMessage = "";
        log = "Machine work:\n";

        dataMemory = new ArrayList<>();
        for (int i = 0; i < MEMORY_SIZE; i++) {
            dataMemory.add(0);
        }

        acc = 0;
        acc2 = 0;
        flagLess = false;
        flagEquals = false;
        flagGreater = false;
        flagOvf = false;

        runCommand(0);
    }

    public ArrayList<Integer> getDataMemory() {
        return dataMemory;
    }

    public String getMachineErrorMessage() {
        return machineErrorMessage;
    }

    boolean isMachineErrorMessage() {
        return !machineErrorMessage.isEmpty();
    }

    private boolean accIsValid() {
        int v = (int) Math.pow(2, FIELD_LENGTH) - 1;
        if (acc > v) {
            machineErrorMessage = "Machine error: Value in ACC is out of bounds ( must be less than " + v + ")";
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

        String command = "";
        try {
            command = commandMemory.get(commandIndex);
        } catch (IndexOutOfBoundsException e) {
            machineErrorMessage = "Machine error: Trying to get out of command list (maybe program does not have END)";
            return;
        }

        String commandName = command.substring(0, COMMAND_LENGTH);
        String data = command.substring(COMMAND_LENGTH);
        int address = Integer.parseInt(data.substring(0, FIELD_LENGTH), 2);
        int info = Integer.parseInt(data.substring(FIELD_LENGTH), 2);

        switch (Integer.parseInt(commandName)) {
            case 0:
                if (!accIsValid())
                    break;
                log = log.concat("LOAD\n");
                acc = info;
                log = log.concat("\tAcc: " + acc + "\n");
                runCommand(commandIndex + 1);
                break;

            case 1:
                log = log.concat("PUT " + acc + " to cell " + address + "\n");
                if (!addressIsValid(address))
                    break;
                dataMemory.set(address, acc);
                log = log.concat("\tData memory: " + dataMemory + "\n");
                runCommand(commandIndex + 1);
                break;

            case 10:
                 log = log.concat("GET (" + address + ")\n");
                 if (!addressIsValid(address))
                     break;
                 if (info == 0)
                     acc = dataMemory.get(address);
                 else acc = dataMemory.get(dataMemory.get(address));
                 log = log.concat("\tAcc: " + acc + "\n");
                 runCommand(commandIndex + 1);
                 break;

            case 11:
                log = log.concat("INC\n");
                acc ++;
                if (!accIsValid())
                    break;
                log = log.concat("\tAcc: " + acc + "\n");
                runCommand(commandIndex + 1);
                break;

            case 100:
                log = log.concat("COMP\n");
                if (!addressIsValid(address))
                    break;
                if (info == 0)
                    compare(dataMemory.get(address));
                else compare(dataMemory.get(dataMemory.get(address)));

                log = log.concat("\tLess: " + flagLess + ", Equals: " + flagEquals + ", Greater: " + flagGreater  + "\n");
                if (!flagLess)
                    runCommand(commandIndex + 1);
                else runCommand(commandIndex + 2);
                break;

            case 101:
                 log = log.concat("JUMP\n");
                 try {
                     runCommand(address);
                 } catch (StackOverflowError e) {
                     machineErrorMessage = "StackOverflowError (check loop exits)";
                     return;
                 }
                 break;

            case 110:
                log = log.concat("ADD\n");
                if (!addressIsValid(address))
                    break;
                if (info == 0)
                    add(dataMemory.get(address));
                else add(dataMemory.get(dataMemory.get(address)));

                if (!flagOvf)
                    runCommand(commandIndex + 1);
                else runCommand(commandIndex + 2);
                break;

            case 1001:
                log = log.concat("MUL\n");
                if (!addressIsValid(address))
                    break;
                if (info == 0)
                    mul(dataMemory.get(address));
                else mul(dataMemory.get(dataMemory.get(address)));

                log = log.concat("\tACC: " + acc + ", ACC2: " + acc2 + ", flagOVF: " + flagOvf + "\n");

                if (!flagOvf)
                    runCommand(commandIndex + 1);
                else runCommand(commandIndex + 2);
                break;

            case 1010:
                log = log.concat("PUT from ACC2 (MPUT) " + acc2 + " to cell " + address + "\n");
                if (!addressIsValid(address))
                    break;
                dataMemory.set(address, acc2);
                log = log.concat("\tData memory: " + dataMemory + "\n");
                runCommand(commandIndex + 1);
                break;

            case 111:
                log = log.concat("DONE\n");
                return;

            default:
                machineErrorMessage = "Machine error: Unknown command " + commandName;
                break;
        }

    }

    private void mul(int data) {
        String result = Integer.toBinaryString(data * acc);

        if (result.length() > FIELD_LENGTH) {
            flagOvf = true;
            acc = Integer.valueOf(result.substring(result.length() - FIELD_LENGTH), 2);
            acc2 = Integer.valueOf(result.substring(0, result.length() - FIELD_LENGTH), 2);
        } else {
            acc = Integer.valueOf(result, 2);
            flagOvf = false;}
    }

    private void add(int data) {
        String result = Integer.toBinaryString(data + acc);

        if (result.length() > FIELD_LENGTH) {
            flagOvf = true;
            result = result.substring(1);
        } else flagOvf = false;

        acc = Integer.valueOf(result, 2);
        log = log.concat("ACC: " + acc + ", OVF flag: " + flagOvf + "\n");
    }

    private void compare(int data) {
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
