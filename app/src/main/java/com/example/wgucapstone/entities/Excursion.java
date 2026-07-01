package com.example.d308vacationplanner.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "excursion")
public class Excursion {

    @PrimaryKey(autoGenerate = true)
    private int excursionID;
    private String title;
    private String date;
    private int vacationID;

    public Excursion(int excursionID, String title, String date, int vacationID) {
        this.excursionID = excursionID;
        this.title = title;
        this.date = date;
        this.vacationID = vacationID;
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
}
