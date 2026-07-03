package com.example.wgucapstone;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.wgucapstone.util.DateRangeValidator;

import org.junit.Test;

/**
 * Test plan (see capstone Task 3 Section D write-up):
 *
 * Test                                          | Expected                                   | Pass?
 * ----------------------------------------------|---------------------------------------------|------
 * Parse a valid MM/dd/yyyy date                  | isValidDate returns true                     | see below
 * Parse an invalid/malformed date                | isValidDate returns false                    | see below
 * End date after start date                      | isEndAfterStart returns true                 | see below
 * End date equal to or before start date          | isEndAfterStart returns false                | see below
 * Date within an inclusive range                  | isWithinRange returns true                   | see below
 * Date outside a range                            | isWithinRange returns false                  | see below
 */
public class DateValidationTest {

    @Test
    public void validDate_isAccepted() {
        assertTrue(DateRangeValidator.isValidDate("06/15/2026"));
    }

    @Test
    public void malformedDate_isRejected() {
        assertFalse(DateRangeValidator.isValidDate("13/40/2026"));
        assertFalse(DateRangeValidator.isValidDate("not-a-date"));
        assertFalse(DateRangeValidator.isValidDate(""));
    }

    @Test
    public void endDateAfterStartDate_isAccepted() {
        assertTrue(DateRangeValidator.isEndAfterStart("06/10/2026", "06/15/2026"));
    }

    @Test
    public void endDateNotAfterStartDate_isRejected() {
        assertFalse(DateRangeValidator.isEndAfterStart("06/15/2026", "06/15/2026"));
        assertFalse(DateRangeValidator.isEndAfterStart("06/15/2026", "06/10/2026"));
    }

    @Test
    public void dateWithinRange_isAccepted() {
        assertTrue(DateRangeValidator.isWithinRange("06/12/2026", "06/10/2026", "06/15/2026"));
        // boundaries are inclusive
        assertTrue(DateRangeValidator.isWithinRange("06/10/2026", "06/10/2026", "06/15/2026"));
        assertTrue(DateRangeValidator.isWithinRange("06/15/2026", "06/10/2026", "06/15/2026"));
    }

    @Test
    public void dateOutsideRange_isRejected() {
        assertFalse(DateRangeValidator.isWithinRange("06/20/2026", "06/10/2026", "06/15/2026"));
        assertFalse(DateRangeValidator.isWithinRange("06/01/2026", "06/10/2026", "06/15/2026"));
    }
}
