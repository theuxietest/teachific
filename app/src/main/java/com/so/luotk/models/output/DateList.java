package com.so.luotk.models.output;

import lombok.Data;

@Data
public class DateList {
    private String date;
    private String absentPresent;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAbsentPresent() {
        return absentPresent;
    }

    public void setAbsentPresent(String absentPresent) {
        this.absentPresent = absentPresent;
    }
}
