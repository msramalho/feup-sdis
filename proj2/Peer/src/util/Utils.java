package src.util;

public class Utils {

    public static String capitalize(String original) {
        return original.substring(0, 1).toUpperCase() + original.substring(1).toLowerCase();
    }

    public static boolean isInt(String source) {
        try {
            Integer.parseInt(source);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
