package cli.backend.textformatters;

import java.util.List;

public class BoxPadder {

    private static final String TOP_LEFT = "╔";
    private static final String TOP_RIGHT = "╗";
    private static final String BOTTOM_LEFT = "╚";
    private static final String BOTTOM_RIGHT = "╝";
    private static final String HORIZONTAL = "═";
    private static final String VERTICAL = "║";

    private static final String TITLE_PAD = "-";

    private static final int WIDTH = 45;

    private static final StringBuilder sb = new StringBuilder();

    public static String format(List<String> lines, String title) {
        if (lines == null || lines.isEmpty() || title == null) {
            return "";
        }

        sb.setLength(0);

        String spacedTitle = " " + title + " ";

        int totalPadding = Math.max(0, WIDTH - spacedTitle.length());
        int leftPadding = totalPadding / 2;
        int rightPadding = totalPadding - leftPadding;

        String centeredTitle = TITLE_PAD.repeat(leftPadding) + spacedTitle
                + TITLE_PAD.repeat(rightPadding);


        // top border
        sb.append(TOP_LEFT)
            .repeat(HORIZONTAL, WIDTH)
            .append(TOP_RIGHT).append("\n");

        addTitle(centeredTitle);
        addLines(lines);

        // bottom border
        sb.append(BOTTOM_LEFT)
            .repeat(HORIZONTAL, WIDTH)
            .append(BOTTOM_RIGHT).append("\n");

        return sb.toString();


    }

    private static void addLines(List<String> lines) {
        for (String line : lines) {
            sb.append(VERTICAL)
                .append(" ")
                .append(String.format("%-" + (WIDTH - 1) + "s", line))
                .append(VERTICAL)
                .append("\n");
        }
    }

    private static void addTitle(String title) {
        sb.append(VERTICAL)
            .append(String.format("%-" + WIDTH + "s", title))
            .append(VERTICAL)
            .append("\n");

        // empty row after title
        sb.append(VERTICAL)
                .repeat(" ", WIDTH)
                .append(VERTICAL)
                .append("\n");
    }

}