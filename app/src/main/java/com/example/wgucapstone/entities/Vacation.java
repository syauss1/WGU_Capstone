package com.example.wgucapstone.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vacation")
public class Vacation {

    @PrimaryKey(autoGenerate = true)
    private int vacationID;
    private String title;
    private String hotel;
    private String startDate;
    private String endDate;

    public Vacation(int vacationID, String title, String hotel, String startDate, String endDate) {
        this.vacationID = vacationID;
        this.title = title;
        this.hotel = hotel;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getVacationID() {
        return vacationID;
    }

    public void setVacationID(int id) {
        this.vacationID = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String d) {
        this.startDate = d;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String d) {
        this.endDate = d;
    }
}
