package me.nrubin29.pastebinapi;

public enum AccountType {
    NORMAL(0), PRO(1);

    public static AccountType valueOf(int i) {
        switch(i) {
            case 0: return NORMAL;
            case 1: return PRO;
            default: return null;
        }
    }

    private int i;

    AccountType(int i) {
        this.i = i;
    }

    public int getI() {
        return i;
    }
}