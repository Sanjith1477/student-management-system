package ui.shell;

/**
 * A page hosted inside {@link MainFrame}'s content area. refresh() is
 * called every time the page becomes visible so it can reload data
 * from its manager(s) -- keeps every screen showing current file data
 * without needing a shared in-memory cache.
 */
public interface Page {
    void refresh();
}
