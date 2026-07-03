package com.example.wgucapstone.entities;

/**
 * Common supertype for anything that can appear on the Trip Report:
 * a {@link Vacation} or an {@link Excursion}. The report screen renders a
 * single {@code List<TripItem>} and calls these methods polymorphically —
 * each subclass supplies its own type label, date info, and detail text.
 */
public abstract class TripItem {

    private final String title;

    protected TripItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    /** e.g. "Vacation" or "Excursion" */
    public abstract String getType();

    /** e.g. a date range for a vacation, or a single date for an excursion */
    public abstract String getDateInfo();

    /** e.g. the hotel name, or the parent vacation's title */
    public abstract String getDetail();
}
