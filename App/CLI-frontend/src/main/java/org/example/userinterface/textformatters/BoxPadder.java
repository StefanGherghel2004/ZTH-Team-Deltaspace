package org.example.userinterface.textformatters;

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

    private static final StringBuilder stringBuilder = new StringBuilder();

    public static String format(List<String> lines, String title) {
        if (lines == null || lines.isEmpty() || title == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append(TOP_LEFT)
                .repeat(HORIZONTAL, WIDTH)
                .append(TOP_RIGHT).append("\n");

        int maxTitleTextWidth = WIDTH - 4;
        List<String> titleLines = TextWrapper.wrap(title, maxTitleTextWidth);

        for (String titleLine : titleLines) {
            addTitleLine(sb, titleLine);
        }

        sb.append(VERTICAL)
                .repeat(" ", WIDTH)
                .append(VERTICAL)
                .append("\n");

        addLines(sb, lines);

        sb.append(BOTTOM_LEFT)
                .repeat(HORIZONTAL, WIDTH)
                .append(BOTTOM_RIGHT).append("\n");

        return sb.toString();
    }

    public static String formatWithGradientBorder(List<String> lines, String title, int[] startRGB, int[] endRGB) {

        String plainBox = format(lines, title);

        return Color.applyBorderGradientToText(plainBox, startRGB, endRGB);
    }

    private static void addTitleLine(StringBuilder stringBuilder, String titleLine) {
        String spacedTitle = " " + titleLine + " ";

        int totalPadding = Math.max(0, WIDTH - spacedTitle.length());
        int leftPadding = totalPadding / 2;
        int rightPadding = totalPadding - leftPadding;

        String formattedTitle = TITLE_PAD.repeat(leftPadding)
                + spacedTitle
                + TITLE_PAD.repeat(rightPadding);

        stringBuilder.append(VERTICAL)
                .append(formattedTitle)
                .append(VERTICAL)
                .append("\n");
    }

    private static void addLines(StringBuilder stringBuilder, List<String> lines) {
        for (String line : lines) {
            int visibleLength = Color.stripAnsi(line).length();
            int spacesNeeded = Math.max(0, (WIDTH - 1) - visibleLength);

            stringBuilder.append(VERTICAL)
                    .append(" ")
                    .append(line)
                    .repeat(" ", spacesNeeded)
                    .append(VERTICAL)
                    .append("\n");
        }
    }
}