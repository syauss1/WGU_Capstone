package com.example.wgucapstone.entities;

public class VacationTripItem extends TripItem {

    private final Vacation vacation;

    public VacationTripItem(Vacation vacation) {
        super(vacation.getTitle());
        this.vacation = vacation;
    }

    @Override
    public String getType() {
        return "Vacation";
    }

    @Override
    public String getDateInfo() {
        return vacation.getStartDate() + " – " + vacation.getEndDate();
    }

    @Override
    public String getDetail() {
        return "Hotel: " + vacation.getHotel();
    }
}
