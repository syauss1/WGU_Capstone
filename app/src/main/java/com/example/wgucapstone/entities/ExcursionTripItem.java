package com.example.wgucapstone.entities;

public class ExcursionTripItem extends TripItem {

    private final Excursion excursion;
    private final String vacationTitle;

    public ExcursionTripItem(Excursion excursion, String vacationTitle) {
        super(excursion.getTitle());
        this.excursion = excursion;
        this.vacationTitle = vacationTitle;
    }

    @Override
    public String getType() {
        return "Excursion";
    }

    @Override
    public String getDateInfo() {
        return excursion.getDate();
    }

    @Override
    public String getDetail() {
        String category = excursion.getCategory();
        String prefix = (category != null && !category.isEmpty()) ? category + " · " : "";
        return prefix + "Part of: " + vacationTitle;
    }
}
