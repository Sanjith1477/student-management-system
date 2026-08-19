package ui.theme;

import javax.swing.*;
import java.awt.*;

/**
 * Central design system for the app: one place that owns the color
 * palette, typography and spacing so every screen looks consistent.
 * This is a small hand-built "flat" theme layer (colors + custom
 * components) rather than a third-party look-and-feel library, so the
 * project keeps compiling with nothing but the JDK.
 */
public final class Theme {

    private Theme() { }

    // ---- Palette: navy/indigo primary, teal accent, light neutral background ----
    public static final Color PRIMARY_DARK = new Color(0x1E, 0x29, 0x3B);   // navy (sidebar)
    public static final Color PRIMARY = new Color(0x4F, 0x46, 0xE5);        // indigo (brand/actions)
    public static final Color PRIMARY_HOVER = new Color(0x43, 0x38, 0xCA);
    public static final Color ACCENT_TEAL = new Color(0x14, 0xB8, 0xA6);    // teal accent
    public static final Color ACCENT_TEAL_HOVER = new Color(0x0D, 0x9C, 0x8D);

    public static final Color BG = new Color(0xF3, 0xF5, 0xFA);            // app background
    public static final Color CARD_BG = Color.WHITE;
    public static final Color SIDEBAR_BG = PRIMARY_DARK;
    public static final Color SIDEBAR_ITEM_HOVER = new Color(0x2A, 0x37, 0x4F);
    public static final Color SIDEBAR_ITEM_ACTIVE = PRIMARY;

    public static final Color BORDER = new Color(0xE3, 0xE7, 0xEF);
    public static final Color BORDER_STRONG = new Color(0xCB, 0xD2, 0xE0);

    public static final Color TEXT_PRIMARY = new Color(0x1A, 0x20, 0x2C);
    public static final Color TEXT_SECONDARY = new Color(0x64, 0x6E, 0x82);
    public static final Color TEXT_MUTED = new Color(0x9A, 0xA3, 0xB4);
    public static final Color TEXT_ON_DARK = new Color(0xF1, 0xF4, 0xFA);
    public static final Color TEXT_ON_DARK_MUTED = new Color(0x9C, 0xA8, 0xC2);

    public static final Color SUCCESS = new Color(0x16, 0xA3, 0x4A);
    public static final Color SUCCESS_BG = new Color(0xE7, 0xF8, 0xEE);
    public static final Color DANGER = new Color(0xDC, 0x26, 0x26);
    public static final Color DANGER_BG = new Color(0xFD, 0xEA, 0xEA);
    public static final Color WARNING = new Color(0xD9, 0x77, 0x06);
    public static final Color WARNING_BG = new Color(0xFE, 0xF3, 0xE2);
    public static final Color INFO = new Color(0x2A, 0x64, 0xE0);
    public static final Color INFO_BG = new Color(0xE9, 0xEF, 0xFD);
    public static final Color NEUTRAL_BG = new Color(0xEE, 0xF0, 0xF5);

    // ---- Typography ----
    private static final String FONT_FAMILY = "Segoe UI";

    public static Font font(int style, int size) {
        return new Font(Font.SANS_SERIF, style, size);
    }

    public static final Font H1 = font(Font.BOLD, 24);
    public static final Font H2 = font(Font.BOLD, 18);
    public static final Font H3 = font(Font.BOLD, 15);
    public static final Font BODY = font(Font.PLAIN, 13);
    public static final Font BODY_BOLD = font(Font.BOLD, 13);
    public static final Font SMALL = font(Font.PLAIN, 12);
    public static final Font SMALL_BOLD = font(Font.BOLD, 12);
    public static final Font TINY = font(Font.PLAIN, 11);
    public static final Font METRIC = font(Font.BOLD, 28);

    // ---- Spacing ----
    public static final int SPACE_XS = 4;
    public static final int SPACE_SM = 8;
    public static final int SPACE_MD = 16;
    public static final int SPACE_LG = 24;
    public static final int SPACE_XL = 32;

    public static final int RADIUS = 12;
    public static final int RADIUS_SM = 8;
    public static final int RADIUS_PILL = 999;

    /** Global Swing tweaks that keep every screen visually consistent. */
    public static void install() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        UIManager.put("control", BG);
        UIManager.put("Panel.background", BG);
        UIManager.put("OptionPane.background", CARD_BG);
        UIManager.put("OptionPane.messageFont", BODY);
        UIManager.put("Button.font", BODY_BOLD);
        UIManager.put("Label.font", BODY);
        UIManager.put("TextField.font", BODY);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("PasswordField.font", BODY);
        UIManager.put("ComboBox.font", BODY);
        UIManager.put("Table.font", BODY);
        UIManager.put("Table.rowHeight", 30);
        UIManager.put("TableHeader.font", SMALL_BOLD);
        UIManager.put("ToolTip.font", SMALL);
        UIManager.put("TabbedPane.font", BODY_BOLD);
    }

    public static Color statusColor(double percentage) {
        if (percentage < 75) return DANGER;
        if (percentage < 85) return WARNING;
        return SUCCESS;
    }
}
