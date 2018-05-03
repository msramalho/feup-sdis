package src.util;

public class Logger {
    private static int maxSize = 0;
    private String context;

    /**
     * If object -> autodetect name
     * If String -> use string value
     *
     * @param source the object
     */
    public Logger(Object source) {
        context = source.getClass().equals(String.class) ? source.toString() : source.getClass().getSimpleName();
    }

    Logger(Object source, String extra) {
        this(source);
        this.context += ":" + extra;
    }

    public void print(String message) { System.out.println(format(message)); }

    public void err(String message) { System.err.println(format(message)); }

    private String format(String message) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        maxSize = Math.max(maxSize, context.length());
        // the 3 means this is called from 3 -> 2 (print) -> 1 (format) ->  0 (getStackTrace)
        StackTraceElement relevant = stackTraceElements[3];
        return String.format("[%" + maxSize + "s->%s:%3d] - %s", context, relevant.getMethodName(), relevant.getLineNumber(), message);
    }
}
