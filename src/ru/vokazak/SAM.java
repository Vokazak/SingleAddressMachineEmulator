package ru.vokazak;

import java.util.ArrayList;

public class SAM {

    public static final int COMMAND_LENGTH = 4;
    public static final int FIELD_LENGTH = 7;
    public static final int MEMORY_SIZE = 20;

    private final ArrayList<Integer> dataMemory;
    private final ArrayList<String> commandMemory;

    private int acc;
    private int acc2;

    private boolean flagLess;
    private boolean flagEquals;
    private boolean flagGreater;
    private boolean flagOvf;

    private String machineErrorMessage;
    private final StringBuilder log;

    SAM(ArrayList<String> commandMemory) {
        this.commandMemory = commandMemory;
        machineErrorMessage = "";
        log = new StringBuilder("Machine work:\n");

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
        return log.toString();
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
                log.append("LOAD\n");
                acc = info;
                log.append("\tAcc: ").append(acc).append("\n");
                runCommand(commandIndex + 1);
                break;

            case 1:
                log.append("PUT ").append(acc).append(" to cell ").append(address).append("\n");
                if (!addressIsValid(address))
                    break;
                dataMemory.set(address, acc);
                log.append("\tData memory: ").append(dataMemory).append("\n");
                runCommand(commandIndex + 1);
                break;

            case 10:
                 log.append("GET (").append(address).append(")\n");
                 if (!addressIsValid(address))
                     break;
                 if (info == 0)
                     acc = dataMemory.get(address);
                 else acc = dataMemory.get(dataMemory.get(address));
                 log.append("\tAcc: ").append(acc).append("\n");
                 runCommand(commandIndex + 1);
                 break;

            case 11:
                log.append("INC\n");
                acc ++;
                if (!accIsValid())
                    break;
                log.append("\tAcc: ").append(acc).append("\n");
                runCommand(commandIndex + 1);
                break;

            case 100:
                log.append("COMP\n");
                if (!addressIsValid(address))
                    break;
                if (info == 0)
                    compare(dataMemory.get(address));
                else compare(dataMemory.get(dataMemory.get(address)));

                log.append("\tLess: ").append(flagLess).append(", Equals: ").append(flagEquals).append(", Greater: ").append(flagGreater).append("\n");
                if (!flagLess)
                    runCommand(commandIndex + 1);
                else runCommand(commandIndex + 2);
                break;

            case 101:
                 log.append("JUMP\n");
                 try {
                     runCommand(address);
                 } catch (StackOverflowError e) {
                     machineErrorMessage = "StackOverflowError (check loop exits)";
                     return;
                 }
                 break;

            case 110:
                log.append("ADD\n");
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
                log.append("MUL\n");
                if (!addressIsValid(address))
                    break;
                if (info == 0)
                    mul(dataMemory.get(address));
                else mul(dataMemory.get(dataMemory.get(address)));

                log.append("\tACC: ").append(acc).append(", ACC2: ").append(acc2).append(", flagOVF: ").append(flagOvf).append("\n");

                if (!flagOvf)
                    runCommand(commandIndex + 1);
                else runCommand(commandIndex + 2);
                break;

            case 1010:
                log.append("PUT from ACC2 (MPUT) ").append(acc2).append(" to cell ").append(address).append("\n");
                if (!addressIsValid(address))
                    break;
                dataMemory.set(address, acc2);
                log.append("\tData memory: ").append(dataMemory).append("\n");
                runCommand(commandIndex + 1);
                break;

            case 111:
                log.append("DONE\n");
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
        log.append("ACC: ").append(acc).append(", OVF flag: ").append(flagOvf).append("\n");
    }

    private void compare(int data) {
        log.append("\tAcc: ").append(acc).append(", Data: ").append(data).append("\n");
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
