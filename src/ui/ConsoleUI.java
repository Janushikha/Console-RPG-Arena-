package ui;

/*
 * Polish pass, not a GoF design pattern: small static helpers that turn plain text into ANSI
 * colored health bars and boxed panels. Kept as pure functions with no dependency on Character
 * or any other class, so any part of the game (Day 1-3 alike) could use them without coupling.
 * Most modern terminals (Windows Terminal, VS Code's integrated terminal, macOS/Linux terminals)
 * understand these escape codes; a terminal that doesn't will just show the raw codes.
 */
public class ConsoleUI {

    public static final String RESET = "[0m";
    public static final String BOLD = "[1m";
    public static final String RED = "[31m";
    public static final String GREEN = "[32m";
    public static final String YELLOW = "[33m";
    public static final String CYAN = "[36m";

    private ConsoleUI() {
    }

    // Renders something like: [########----] 70/100, colored red/yellow/green by how full it is.
    public static String healthBar(int current, int max, int width) {
        int filled = max <= 0 ? 0 : (int) Math.round((double) current / max * width);
        filled = Math.max(0, Math.min(width, filled));

        String color = current <= max / 3 ? RED : (current <= (max * 2) / 3 ? YELLOW : GREEN);

        StringBuilder bar = new StringBuilder();
        bar.append(color).append('[');
        for (int i = 0; i < width; i++) {
            bar.append(i < filled ? '#' : '-');
        }
        bar.append(']').append(RESET);
        bar.append(' ').append(current).append('/').append(max);
        return bar.toString();
    }

    // Renders a simple boxed panel with a title and a list of body lines. Lines may contain
    // ANSI color codes (e.g. from healthBar()) — those are invisible on screen but still count
    // as characters in the raw String, so width/padding is measured on the visible text only,
    // otherwise colored lines would throw off the box's right-hand border.
    public static void printBox(String title, String[] lines) {
        int width = visibleLength(title);
        for (String line : lines) {
            width = Math.max(width, visibleLength(line));
        }

        String border = CYAN + "+" + repeat('-', width + 2) + "+" + RESET;
        System.out.println(border);
        System.out.println(CYAN + "| " + RESET + BOLD + pad(title, width) + RESET + CYAN + " |" + RESET);
        System.out.println(border);
        for (String line : lines) {
            System.out.println(CYAN + "| " + RESET + pad(line, width) + CYAN + " |" + RESET);
        }
        System.out.println(border);
    }

    private static String repeat(char c, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    private static int visibleLength(String s) {
        return stripAnsi(s).length();
    }

    private static String stripAnsi(String s) {
        return s.replaceAll("\\[[0-9;]*m", "");
    }

    private static String pad(String s, int width) {
        StringBuilder sb = new StringBuilder(s);
        int visible = visibleLength(s);
        while (visible < width) {
            sb.append(' ');
            visible++;
        }
        return sb.toString();
    }
}
