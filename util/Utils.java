package util;

public class Utils {
    public static String[] safeSplit(String line, char sep){
        // simple split that doesn't attempt to handle quoted commas for simplicity
        return line.split(String.valueOf(sep));
    }
}
