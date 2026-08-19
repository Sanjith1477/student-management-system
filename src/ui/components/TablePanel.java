package ui.components;

import ui.theme.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * Reusable "data listing" component: a title + search box + optional
 * filter/action toolbar at the top, and a sortable JTable underneath
 * that automatically shows an empty state when there is no data.
 * Every directory/history screen in the app (Student Directory, Teacher
 * Directory, Course Management, Attendance History, Enrollments) is
 * built on top of this one component instead of duplicating JTable
 * plumbing everywhere.
 */
public class TablePanel extends RoundedPanel {

    private final DefaultTableModel model;
    private final JTable table;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField searchField;
    private final CardLayout centerLayout = new CardLayout();
    private final JPanel centerHost = new JPanel(centerLayout);
    private final JPanel toolbarRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    private final JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

    public TablePanel(String title, String[] columns, int[] searchableColumns) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD));

        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(Theme.BODY);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0xE9, 0xEC, 0xFB));
        table.setSelectionForeground(Theme.TEXT_PRIMARY);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(Theme.NEUTRAL_BG);
        table.getTableHeader().setForeground(Theme.TEXT_SECONDARY);
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, Theme.SPACE_MD, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.H3);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);

        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search...");
        searchField.setPreferredSize(new Dimension(220, 32));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_STRONG, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applySearch(searchableColumns); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applySearch(searchableColumns); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applySearch(searchableColumns); }
        });

        JPanel topLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topLeft.setOpaque(false);
        topLeft.add(titleLabel);

        JPanel searchWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchWrap.setOpaque(false);
        searchWrap.add(searchField);

        filterRow.setOpaque(false);
        toolbarRight.setOpaque(false);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.add(topLeft, BorderLayout.WEST);
        titleRow.add(toolbarRight, BorderLayout.EAST);

        JPanel filterBar = new JPanel(new BorderLayout());
        filterBar.setOpaque(false);
        filterBar.add(searchWrap, BorderLayout.WEST);
        filterBar.add(filterRow, BorderLayout.EAST);

        header.add(titleRow, BorderLayout.NORTH);
        header.add(filterBar, BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(Color.WHITE);

        centerHost.setOpaque(false);
        centerHost.add(scroll, "table");
        centerHost.add(new EmptyState("No records to show", "Try adjusting your filters or add a new entry."), "empty");

        add(header, BorderLayout.NORTH);
        add(centerHost, BorderLayout.CENTER);
    }

    private void applySearch(int[] cols) {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text), cols));
            } catch (Exception ignored) { }
        }
        refreshEmptyState();
    }

    private void refreshEmptyState() {
        boolean empty = table.getRowCount() == 0;
        centerLayout.show(centerHost, empty ? "empty" : "table");
    }

    public void addToolbarButton(JComponent c) {
        toolbarRight.add(c);
    }

    public void addFilterComponent(JComponent c) {
        filterRow.add(c);
    }

    public DefaultTableModel getModel() { return model; }
    public JTable getTable() { return table; }
    public TableRowSorter<DefaultTableModel> getSorter() { return sorter; }

    public void setRows(Object[][] rows) {
        model.setRowCount(0);
        for (Object[] row : rows) model.addRow(row);
        refreshEmptyState();
    }

    /** Returns the model row index for a selected view row (accounts for sorting/filtering). */
    public int selectedModelRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return -1;
        return table.convertRowIndexToModel(viewRow);
    }
}
