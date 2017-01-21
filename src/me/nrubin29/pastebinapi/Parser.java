package me.nrubin29.pastebinapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Parser {

    private String[] args;
    private ArrayList<String> keys = new ArrayList<String>();
    private HashMap<String, String> vals = new HashMap<String, String>();

    protected Parser(String[] args) {
        this.args = args;
    }

    public void addKey(String... keyarray) {
        keys.addAll(Arrays.asList(keyarray));
    }

    public HashMap<String, String> parse() {
        for (String arg : args) {
            arg = arg.substring(arg.indexOf("_") + 1, arg.lastIndexOf("<"));
            for (String key : keys) {
                if (arg.startsWith("key")) vals.put(key, sub(arg));
            }
        }
        return vals;
    }

    private String sub(String str) {
        return str.substring(str.indexOf(">") + 1);
    }
}