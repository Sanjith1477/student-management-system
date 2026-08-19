package utils;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/** Small helper for exporting tabular data to a CSV file chosen via a save dialog. */
public final class CsvExporter {

    private CsvExporter() { }

    /** Opens a save dialog and writes header + rows as CSV. Returns true on success. */
    public static boolean export(Component parent, String suggestedName, String[] header, List<String[]> rows) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(suggestedName));
        int result = chooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) return false;

        java.io.File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new java.io.File(file.getParentFile(), file.getName() + ".csv");
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println(String.join(",", escapeAll(header)));
            for (String[] row : rows) {
                writer.println(String.join(",", escapeAll(row)));
            }
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, "Could not write file: " + e.getMessage(),
                    "Export Failed", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static String[] escapeAll(String[] values) {
        String[] out = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            out[i] = escape(values[i]);
        }
        return out;
    }

    private static String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
