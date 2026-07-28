package org.example.userinterface.textformatters;

public class Capitalise {

    private static final StringBuilder sb = new StringBuilder();

    public static String format(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        sb.setLength(0);

        boolean convertNext = true;

        for (char ch : str.toCharArray()) {
            if (Character.isWhitespace(ch)) {
                convertNext = true;
                sb.append(ch);
            } else if (convertNext) {
                sb.append(Character.toTitleCase(ch));
                convertNext = false;
            } else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }

}
