package com.checkpointfrontend.calendar;

import java.util.ArrayList;
import java.util.List;

public class YearJSONFormat {
    private String yearNum;
    private List<MonthJSONFormat> months;
    public YearJSONFormat() {
        months = new ArrayList<>();
    }
    public String getYearNum() { return yearNum; }
    public void setYearNum(String yearNum) { this.yearNum = yearNum; }

    public List<MonthJSONFormat> getMonths() { return months; }
    public void setMonths(List<MonthJSONFormat> months) { this.months = months; }
}
