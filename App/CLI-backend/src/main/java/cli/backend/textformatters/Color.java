package cli.backend.textformatters;

public class Color {

    private static final String RESET = "\u001b[0m";

    private static final String TEXT_RED = "\u001b[31m";
    private static final String TEXT_GREEN = "\u001b[32m";
    private static final String TEXT_YELLOW = "\u001b[33m";

    private static final String TEXT_BLACK = "\u001b[30m";
    private static final String TEXT_WHITE = "\u001b[37m";
    private static final String TEXT_BRIGHT_WHITE = "\u001b[37;1m";

    private static final String TEXT_BLUE = "\u001b[34m";
    private static final String TEXT_MAGENTA = "\u001b[35m";
    private static final String TEXT_CYAN = "\u001b[36m";

    private static final String TEXT_BRIGHT_BLUE = "\u001b[34;1m";
    private static final String TEXT_BRIGHT_MAGENTA = "\u001b[35;1m";
    private static final String TEXT_BRIGHT_CYAN = "\u001b[36;1m";

    private static final String BG_RED = "\u001b[41m";
    private static final String BG_GREEN = "\u001b[42m";
    private static final String BG_YELLOW = "\u001b[43m";

    private static final String BG_BLACK = "\u001b[40m";
    private static final String BG_WHITE = "\u001b[47m";

    private static final String BG_BLUE = "\u001b[44m";
    private static final String BG_MAGENTA = "\u001b[45m";
    private static final String BG_CYAN = "\u001b[46m";

    public static String textRGB(int r, int g, int b, String message) {
        String rgbCode = String.format("\u001b[38;2;%d;%d;%dm", r, g, b);
        return apply(rgbCode, message);
    }

    private static String apply(String code, String message) {
        return code + message + RESET;
    }

    // text color methods
    public static String textRed(String m) {
        return apply(TEXT_RED, m);
    }

    public static String textGreen(String m) {
        return apply(TEXT_GREEN, m);
    }

    public static String textYellow(String m) {
        return apply(TEXT_YELLOW, m);
    }

    public static String textBlack(String m) {
        return apply(TEXT_BLACK, m);
    }

    public static String textWhite(String m) {
        return apply(TEXT_WHITE, m);
    }

    public static String textBrightWhite(String m) {
        return apply(TEXT_BRIGHT_WHITE, m);
    }

    public static String textBlue(String m) {
        return apply(TEXT_BLUE, m);
    }

    public static String textBrightBlue(String m) {
        return apply(TEXT_BRIGHT_BLUE, m);
    }

    public static String textMagenta(String m) {
        return apply(TEXT_MAGENTA, m);
    }

    public static String textBrightMagenta(String m) {
        return apply(TEXT_BRIGHT_MAGENTA, m);
    }

    public static String textCyan(String m) {
        return apply(TEXT_CYAN, m);
    }

    public static String textBrightCyan(String m) {
        return apply(TEXT_BRIGHT_CYAN, m);
    }


    // background color methods
    public static String bgRed(String m) {
        return apply(BG_RED, m);
    }

    public static String bgGreen(String m) {
        return apply(BG_GREEN, m);
    }

    public static String bgYellow(String m) {
        return apply(BG_YELLOW, m);
    }

    public static String bgBlack(String m) {
        return apply(BG_BLACK, m);
    }

    public static String bgWhite(String m) {
        return apply(BG_WHITE, m);
    }

    public static String bgBlue(String m) {
        return apply(BG_BLUE, m);
    }

    public static String bgMagenta(String m) {
        return apply(BG_MAGENTA, m);
    }

    public static String bgCyan(String m) {
        return apply(BG_CYAN, m);
    }
}