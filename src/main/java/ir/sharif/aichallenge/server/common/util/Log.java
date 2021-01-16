package ir.sharif.aichallenge.server.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Logging API.
 */
public final class Log {

    public static final int VERBOSE = 0;
    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARN = 3;
    public static final int ERROR = 4;

    public static final boolean DEV_MODE = System.getenv("AICDev") == null;
    public static final int LOG_LEVEL = DEV_MODE ? VERBOSE : WARN;

    private static final String[] LEVELS = {
            "Verbose", "Debug", "Info", "Warning", "Error"
    };

    private Log() {
    }

    public static void v(String tag, String msg) {
        log(VERBOSE, tag, msg);
    }

    public static void v(String tag, String msg, Throwable tr) {
        log(VERBOSE, tag, msg, tr);
    }

    public static void d(String tag, String msg) {
        log(DEBUG, tag, msg);
    }

    public static void d(String tag, String msg, Throwable tr) {
        log(DEBUG, tag, msg, tr);
    }

    public static void i(String tag, String msg) {
        log(INFO, tag, msg);
    }

    public static void i(String tag, String msg, Throwable tr) {
        log(INFO, tag, msg, tr);
    }

    public static void w(String tag, String msg) {
        log(WARN, tag, msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        log(WARN, tag, msg, tr);
    }

    public static void e(String tag, String msg) {
        log(ERROR, tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        log(ERROR, tag, msg, tr);
    }

    public static String getStackTraceString(Throwable tr) {
        if (tr == null)
            return "";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }
    public static void log(int priority, String tag, String msg) {
        if (priority < LOG_LEVEL)
            return;
        if (DEV_MODE) {
            System.err.printf("%s (%s): %s%n", LEVELS[priority], tag, msg);
        } else {
            System.err.println(LEVELS[priority] + ": " + msg);
        }
    }

    public static void log(int priority, String tag, String msg, Throwable tr) {
        if (priority < LOG_LEVEL)
            return;
        if (DEV_MODE) {
            System.err.printf("%s (%s): %s%n", LEVELS[priority], tag, msg + '\n' + getStackTraceString(tr));
        } else {
            System.err.println(LEVELS[priority] + ": " + msg);
        }
    }

}