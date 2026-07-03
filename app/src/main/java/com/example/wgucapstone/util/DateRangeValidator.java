package com.example.wgucapstone.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Pure-Java date validation shared by VacationDetails and ExcursionDetails.
 * Kept free of Android framework classes so it can run as a fast local JUnit
 * test with no emulator/Robolectric needed.
 */
public final class DateRangeValidator {

    public static final String DATE_FORMAT = "MM/dd/yyyy";

    private DateRangeValidator() {}

    private static SimpleDateFormat formatter() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        sdf.setLenient(false);
        return sdf;
    }

    /** Returns true only for strictly valid MM/dd/yyyy dates (e.g. rejects 13/40/2026). */
    public static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return false;
        try {
            formatter().parse(dateStr.trim());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /** Returns true if endStr is strictly after startStr. Both must already be valid dates. */
    public static boolean isEndAfterStart(String startStr, String endStr) {
        try {
            Date start = formatter().parse(startStr.trim());
            Date end = formatter().parse(endStr.trim());
            return end.after(start);
        } catch (ParseException e) {
            return false;
        }
    }

    /** Returns true if dateStr falls within [rangeStartStr, rangeEndStr], inclusive. */
    public static boolean isWithinRange(String dateStr, String rangeStartStr, String rangeEndStr) {
        try {
            Date date = formatter().parse(dateStr.trim());
            Date rangeStart = formatter().parse(rangeStartStr.trim());
            Date rangeEnd = formatter().parse(rangeEndStr.trim());
            return !date.before(rangeStart) && !date.after(rangeEnd);
        } catch (ParseException e) {
            return false;
        }
    }
}
