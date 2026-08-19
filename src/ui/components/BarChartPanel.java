package ui.components;

import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * A minimal, dependency-free bar chart drawn with Java2D. Used for the
 * attendance trend and department/year breakdown visuals so reports
 * aren't plain text/number lists.
 */
public class BarChartPanel extends JComponent {

    public static class Bar {
        public final String label;
        public final double value; // 0-100 expected, but any positive value works
        public final Color color;
        public Bar(String label, double value, Color color) {
            this.label = label; this.value = value; this.color = color;
        }
    }

    private List<Bar> bars;
    private double maxValue;
    private String valueSuffix = "";

    public BarChartPanel(List<Bar> bars, double maxValue) {
        this.bars = bars;
        this.maxValue = maxValue <= 0 ? 1 : maxValue;
        setOpaque(false);
        setPreferredSize(new Dimension(400, 200));
    }

    public void setValueSuffix(String suffix) { this.valueSuffix = suffix; }

    public void setBars(List<Bar> bars, double maxValue) {
        this.bars = bars;
        this.maxValue = maxValue <= 0 ? 1 : maxValue;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(Theme.TINY);

        if (bars == null || bars.isEmpty()) {
            g2.setColor(Theme.TEXT_MUTED);
            g2.setFont(Theme.SMALL);
            g2.drawString("No data yet", 8, getHeight() / 2);
            g2.dispose();
            return;
        }

        int n = bars.size();
        int padBottom = 26;
        int padTop = 10;
        int chartHeight = getHeight() - padBottom - padTop;
        int gap = 14;
        int barWidth = Math.max(18, (getWidth() - gap * (n + 1)) / n);

        int x = gap;
        for (Bar bar : bars) {
            double ratio = Math.max(0, Math.min(1, bar.value / maxValue));
            int h = (int) Math.round(chartHeight * ratio);
            int y = padTop + (chartHeight - h);

            g2.setColor(new Color(bar.color.getRed(), bar.color.getGreen(), bar.color.getBlue(), 30));
            g2.fillRoundRect(x, padTop, barWidth, chartHeight, 6, 6);

            g2.setColor(bar.color);
            g2.fillRoundRect(x, y, barWidth, Math.max(h, 3), 6, 6);

            g2.setColor(Theme.TEXT_SECONDARY);
            String valText = formatValue(bar.value) + valueSuffix;
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(valText, x + barWidth / 2 - fm.stringWidth(valText) / 2, Math.max(y - 4, fm.getHeight()));

            g2.setColor(Theme.TEXT_MUTED);
            String label = bar.label;
            if (fm.stringWidth(label) > barWidth + gap) {
                while (label.length() > 3 && fm.stringWidth(label + "\u2026") > barWidth + gap) {
                    label = label.substring(0, label.length() - 1);
                }
                label = label + "\u2026";
            }
            g2.drawString(label, x + barWidth / 2 - fm.stringWidth(label) / 2, getHeight() - 8);

            x += barWidth + gap;
        }
        g2.dispose();
    }

    private String formatValue(double v) {
        if (v == Math.floor(v)) return String.valueOf((int) v);
        return String.format("%.1f", v);
    }
}
