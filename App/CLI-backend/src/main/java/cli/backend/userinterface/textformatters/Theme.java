package cli.backend.userinterface.textformatters;

public class Theme {

    public static final int[] PRIMARY_GRADIENT_START = {212, 0, 255};
    public static final int[] PRIMARY_GRADIENT_END = {0, 229, 255};

    // constant related to the WIDTH in BoxPadder used for all the menus
    public static final int MAX_TEXT_WIDTH = 42;

    public static final int HEADER_WIDTH = 47;

    private static final String RAW_LOGO = """
        ██████╗ ███████╗██╗  ████████╗ █████╗ ███████╗██████╗  █████╗  ██████╗███████╗
        ██╔══██╗██╔════╝██║  ╚══██╔══╝██╔══██╗██╔════╝██╔══██╗██╔══██╗██╔════╝██╔════╝
        ██║  ██║█████╗  ██║     ██║   ███████║███████╗██████╔╝███████║██║     █████╗
        ██║  ██║██╔══╝  ██║     ██║   ██╔══██║╚════██║██╔═══╝ ██╔══██║██║     ██╔══╝
        ██████╔╝███████╗███████╗██║   ██║  ██║███████║██║     ██║  ██║╚██████╗███████╗
        ╚═════╝ ╚══════╝╚══════╝╚═╝   ╚═╝  ╚═╝╚══════╝╚═╝     ╚═╝  ╚═╝ ╚═════╝╚══════╝""";

    public static final String LOGO = Color.applyGradientToText(
            RAW_LOGO,
            PRIMARY_GRADIENT_START,
            PRIMARY_GRADIENT_END
    );

    public static final String PROMPT = Color.textBrightCyan("» ");

    public static String formatUsername(String username) {
        return Color.textCyan(username);
    }

    public static String formatTopic(String topic) {
        if (topic == null) return "N/A";

        String colorTopic = switch (topic.toLowerCase()) {
            case "food" -> Color.textYellow(topic);
            case "gaming" -> Color.textRed(topic);
            case "science" -> Color.textBrightMagenta(topic);
            case "art" -> Color.textGreen(topic);
            case "tech" -> Color.textBrightBlue(topic);
            default -> topic;
        };

        return colorTopic;
    }

    public static String header(String title) {
        String paddedTitle = " " + title + " ";
        int dashesCount = Math.max(0, (HEADER_WIDTH - paddedTitle.length()) / 2);

        String dashes = "-".repeat(dashesCount);

        return "\n" + dashes + paddedTitle + dashes;
    }

    public static String formatNSFW(String NSFW){
        String fullNSFW="NSFW: "+NSFW;
        String coloredNSFW=switch (NSFW){
            case "Yes"->Color.textOrange(fullNSFW);
            default -> fullNSFW;
        };
        return coloredNSFW;
    }

    public static String footer() {
        return "-".repeat(HEADER_WIDTH) + "\n";
    }
}
