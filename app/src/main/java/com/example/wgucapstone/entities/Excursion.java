package com.example.wgucapstone.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "excursion")
public class Excursion {

    @PrimaryKey(autoGenerate = true)
    private int excursionID;
    private String title;
    private String date;
    private int vacationID;
    private String category;

    @Ignore
    public Excursion(int excursionID, String title, String date, int vacationID) {
        this(excursionID, title, date, vacationID, null);
    }

    public Excursion(int excursionID, String title, String date, int vacationID, String category) {
        this.excursionID = excursionID;
        this.title = title;
        this.date = date;
        this.vacationID = vacationID;
        this.category = category;
    }

    public int getExcursionID() {
        return excursionID;
    }

    public void setExcursionID(int id) {
        this.excursionID = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getVacationID() {
        return vacationID;
    }

    public void setVacationID(int id) {
        this.vacationID = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
