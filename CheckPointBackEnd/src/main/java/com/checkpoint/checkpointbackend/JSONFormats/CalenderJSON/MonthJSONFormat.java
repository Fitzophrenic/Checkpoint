package com.checkpoint.checkpointbackend.JSONFormats.CalenderJSON;

import java.util.List;

public class MonthJSONFormat {
        private String monthName;
        private List<DateJSONFormat> days;

        public String getMonthName() { return monthName; }
        public void setMonthName(String monthName) { this.monthName = monthName; }

        public List<DateJSONFormat> getDays() { return days; }
        public void setDays(List<DateJSONFormat> days) { this.days = days; }
}
