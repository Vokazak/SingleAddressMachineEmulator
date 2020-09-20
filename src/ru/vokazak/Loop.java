package ru.vokazak;

public class Loop {
    private String loopName;
    private int jumpAddress;

    Loop(String loopName, int jumpAddress) {
        this.jumpAddress = jumpAddress;
        this.loopName = loopName;
    }

    public String getName() {
        return loopName;
    }

    public int getJumpAddress() {
        return jumpAddress;
    }


}
