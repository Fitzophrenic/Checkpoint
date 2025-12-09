package com.checkpointfrontend.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.checkpointfrontend.httpClientCheckPoint;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class CalendarScreen extends Stage{
    private YearMonth currentMonth = YearMonth.now();
    private final GridPane calendarGrid = new GridPane();
    private final Label monthLabel = new Label();
    private Scene calendarScreen;
    private Scene DayScene;

    private final HBox header = new HBox();
    private VBox calendarArea;
    private final YearsJSONFormat calendarData;
    private httpClientCheckPoint client;
    private String currentUser;
    private String projectID;
    private final boolean isProject;
    public CalendarScreen(Stage window, String currentUser, httpClientCheckPoint client) {
        this.setTitle("Personal Calender");
        this.client = client;
        this.currentUser = currentUser;
        isProject = false;
        calendarData = CalendarJSONConverter.convertFromMap(client.getUserCalendar(currentUser));
        System.out.println(calendarData.getYears().getClass());
        initCalendarScreen();
    }
    public CalendarScreen(Stage window, String currentUser, String projectID, httpClientCheckPoint client) {
        this.setTitle("Project Calender");
        this.client = client;
        this.projectID = projectID;
        this.currentUser = currentUser;
        isProject = true;
        calendarData = CalendarJSONConverter.convertFromMap(client.getProjectCalendar(projectID));
        System.out.println(calendarData.getYears().getClass());
        initCalendarScreen();
    }
    private void initCalendarScreen() {

        calendarGrid.setHgap(5);
        calendarGrid.setVgap(5);
        
        for (int i = 0; i < 7; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 7);
            col.setHgrow(Priority.ALWAYS);

            calendarGrid.getColumnConstraints().add(col);
        }
        for (int i = 0; i < 6; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / 6);
            row.setVgrow(Priority.ALWAYS);

            calendarGrid.getRowConstraints().add(row);

        }

        
        monthLabel.setText(currentMonth.toString());
        monthLabel.getStyleClass().add("monthLabel");
        Button previousMonth = new Button("<");
        header.getChildren().add(previousMonth);
        
        header.getChildren().add(monthLabel);
        Button nextMonth = new Button(">");
        header.getChildren().add(nextMonth);
        header.getStyleClass().add("header");
        fillCalendar(currentMonth);
        calendarArea = new VBox(header, calendarGrid);
        calendarScreen = new Scene(calendarArea,900, 600);
        calendarScreen.getStylesheets().add(getClass().getResource("/com/checkpointfrontend/Calendar.css").toExternalForm());

        calendarGrid.setGridLinesVisible(true);
        previousMonth.setOnMouseClicked(e -> {
            currentMonth = currentMonth.minusMonths(1);
            calendarGrid.getChildren().clear();
            fillCalendar(currentMonth);
            monthLabel.setText(currentMonth.toString());
            calendarGrid.setGridLinesVisible(false);
            calendarGrid.setGridLinesVisible(true);

        });
        nextMonth.setOnMouseClicked(e -> {
            currentMonth = currentMonth.plusMonths(1);
            calendarGrid.getChildren().clear();
            fillCalendar(currentMonth);
            monthLabel.setText(currentMonth.toString());
            calendarGrid.setGridLinesVisible(false);
            calendarGrid.setGridLinesVisible(true);
        });

        this.setOnCloseRequest(event -> {
            System.out.println(CalendarJSONConverter.convertToString(calendarData));
            if(isProject) {
                // client.updateProjectCalendar(currentUser, projectID, CalendarJSONConverter.convertToString(calendarData));
                return;
            }
            client.updateUserCalendar(currentUser, CalendarJSONConverter.convertToString(calendarData));
        });
        this.setScene(calendarScreen);
        this.show();
    }
    private boolean checkIfYearExistsInJSON(String yearToCheck){
        for(YearJSONFormat yearCheck : calendarData.getYears()) {
            if(yearCheck.getYearNum().equals(yearToCheck)) {
                return true;
            }
        }
        return false;
    }
    private boolean checkIfYearExistsInJSON(String yearToCheck, YearsJSONFormat check){
        for(YearJSONFormat yearCheck : check.getYears()) {
            if(yearCheck.getYearNum().equals(yearToCheck)) {
                return true;
            }
        }
        return false;
    }
    public void onReturn() {
        calendarGrid.getChildren().clear();
        fillCalendar(currentMonth);
        monthLabel.setText(currentMonth.toString());
        calendarGrid.setGridLinesVisible(false);
        calendarGrid.setGridLinesVisible(true);
        System.out.println(CalendarJSONConverter.convertToString(calendarData));
    }
    private boolean checkIfYearMonthExistsInJSON(YearMonth yearMonth) {
        String[] dateSeperate = yearMonth.toString().split("-");
        String year = dateSeperate[0];
        String monthName = yearMonth.getMonth().name();
        if(!checkIfYearExistsInJSON(year)) {
            return false;
        }
        YearJSONFormat yer = getYearJSONFormat(year);
        for(MonthJSONFormat monthCheck : yer.getMonths()) {
            if(monthCheck.getMonthName().equals(monthName)) {
                return true;
            }
        }
        return false;
    }
    private boolean checkIfYearMonthExistsInJSON(YearMonth yearMonth, YearJSONFormat yearJson) {
        String monthName = yearMonth.getMonth().name();
        for(MonthJSONFormat monthCheck : yearJson.getMonths()) {
            if(monthCheck.getMonthName().equals(monthName)) {
                return true;
            }
        }
        return false;
    }
    private boolean checkIfYearMonthDayExistsInJSON(LocalDate yearMonthDay) {
        String year = String.valueOf(yearMonthDay.getYear());
        String monthName = yearMonthDay.getMonth().name();
        String day = String.valueOf(yearMonthDay.getDayOfMonth());
        if (!checkIfYearExistsInJSON(year)) {
            return false;
        }
        YearJSONFormat yearJSON = getYearJSONFormat(year);
        for (MonthJSONFormat month : yearJSON.getMonths()) {
            if (month.getMonthName().equals(monthName)) {

                for (DateJSONFormat d : month.getDays()) {
                    if (d.getDate().equals(day)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }
    private boolean checkIfYearMonthDayExistsInJSON(LocalDate yearMonthDay, MonthJSONFormat mont) {
            String day = String.valueOf(yearMonthDay.getDayOfMonth());
            for (DateJSONFormat d : mont.getDays()) {
                if (d.getDate().equals(day)) {
                    return true;
                }
            }
            return false;
        }
        
    
    private YearJSONFormat getYearJSONFormat(String year){
        for(YearJSONFormat yearCheck : calendarData.getYears()) {
            if(yearCheck.getYearNum().equals(year)) {
                return yearCheck;
            }
        }
        return new YearJSONFormat();
    }
    private MonthJSONFormat getMonthJSONFormat(YearMonth month) {
        String[] dateSeperate = month.toString().split("-");
        String year = dateSeperate[0];
        String monthName = month.getMonth().name();
        if(!checkIfYearExistsInJSON(year)) {
            return new MonthJSONFormat();
        }
        YearJSONFormat yer = getYearJSONFormat(year);
        for(MonthJSONFormat monthCheck : yer.getMonths()) {
            if(monthCheck.getMonthName().equals(monthName)) {
                return monthCheck;
            }
        }
        return new MonthJSONFormat();
    }
    private DateJSONFormat getDateJSONFormat(LocalDate date) {
        YearMonth ym = YearMonth.from(date);
        if(!checkIfYearMonthDayExistsInJSON(date)) {
            return new DateJSONFormat();
        }
        MonthJSONFormat yer = getMonthJSONFormat(ym);
        for(DateJSONFormat monthCheck : yer.getDays()) {
            if(monthCheck.getDate().equals(date.getDayOfMonth()+"")) {
                return monthCheck;
            }
        }
        return new DateJSONFormat();
    }
    @SuppressWarnings("null")
    private void fillCalendar(YearMonth monthToFill) {

        String[] daysOfTheWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        for (int i = 0; i < daysOfTheWeek.length; i++) {
            Label dayLabel = new Label(daysOfTheWeek[i]);
            dayLabel.getStyleClass().add("days");
            calendarGrid.add(dayLabel, i, 0);
            GridPane.setHalignment(dayLabel, HPos.CENTER);
            GridPane.setValignment(dayLabel, VPos.CENTER);
        }

        int lengthOfMonth = monthToFill.lengthOfMonth();
        String[] dateSeperate = monthToFill.toString().split("-");
        String year = dateSeperate[0];
        boolean yearExists = checkIfYearExistsInJSON(year);
        if(!yearExists) {
            fillCalendarNumbers(monthToFill);
            return;
        }
        boolean monthExists = checkIfYearMonthExistsInJSON(monthToFill);
        if(!monthExists) {
            fillCalendarNumbers(monthToFill);
            return;
        }
        MonthJSONFormat thisMonth = getMonthJSONFormat(monthToFill);
        List<DateJSONFormat> days = thisMonth.getDays();
        

        int yearNum = Integer.parseInt(year);
        LocalDate firstOfMonth = LocalDate.of(yearNum, monthToFill.getMonth(), 1);
        
        DayOfWeek dayOfWeek = firstOfMonth.getDayOfWeek();
        int startPos = dayOfWeek.getValue() % 7;

        int rowIndex = 1;
        int colIndex = 0;

        for(int i = 0; i < startPos; i++) {
            CalendarDate addedDate = new CalendarDate("");
            calendarGrid.add(addedDate, i, 1);
            colIndex++;
        }

        for(int i = 1; i<lengthOfMonth+1; i++) {
            final int dayNum = i;
            CalendarDate addedDate = null;
            boolean dayExist = false;
            for(DateJSONFormat day : days) {
                if(day.getDate().equals(i+"")) {
                    addedDate = new CalendarDate(day);
                    dayExist = true;
                    break;
                }
            }
            if(!dayExist) {
                addedDate = new CalendarDate(""+i );
            }
            addedDate.setOnMouseClicked(e -> {
                if(!checkIfYearMonthDayExistsInJSON(LocalDate.of(yearNum, monthToFill.getMonth().getValue(), dayNum))) {
                    DateJSONFormat date = new DateJSONFormat();
                    date.setDate(String.valueOf(dayNum));
                    date.setEvents(new ArrayList<>());
                    getMonthJSONFormat(monthToFill).getDays().add(date);
                }
                LocalDate datee = LocalDate.of(yearNum, monthToFill.getMonth().getValue(), dayNum);
                DateJSONFormat dateJSON = getDateJSONFormat(datee);
                DayScene = new Scene(new DayScreen(dateJSON, this, calendarScreen, datee, isProject),900, 600);
                this.setScene(DayScene);
            });
            calendarGrid.add(addedDate, colIndex, rowIndex);
            GridPane.setHgrow(addedDate, Priority.ALWAYS);
            GridPane.setVgrow(addedDate, Priority.ALWAYS);
            colIndex++;
            if(colIndex == 7) {
                rowIndex++;
                colIndex = 0;
            }
        }

    }

    private void fillCalendarNumbers(YearMonth monthToFill){
        int lengthOfMonth = monthToFill.lengthOfMonth();
        String[] dateSeperate = monthToFill.toString().split("-");
        String year = dateSeperate[0];
        int yearNum = Integer.parseInt(year);
        LocalDate firstOfMonth = LocalDate.of(yearNum, monthToFill.getMonth(), 1);
        
        DayOfWeek dayOfWeek = firstOfMonth.getDayOfWeek();
        int startPos = dayOfWeek.getValue() % 7;
        int rowIndex = 1;
        int colIndex = 0;

        for(int i = 0; i < startPos; i++) {
            CalendarDate addedDate = new CalendarDate("");
            calendarGrid.add(addedDate, i, 1);
            colIndex++;
        }

        for(int i = 1; i<lengthOfMonth+1; i++) {
            final int dayNum = i;
            CalendarDate addedDate = new CalendarDate(""+i);
            calendarGrid.add(addedDate, colIndex, rowIndex);
            GridPane.setHgrow(addedDate, Priority.ALWAYS);
            GridPane.setVgrow(addedDate, Priority.ALWAYS);
            addedDate.setOnMouseClicked(e -> {
                if(!checkIfYearExistsInJSON(year)){
                    YearJSONFormat yearJSON = new YearJSONFormat();
                    yearJSON.setYearNum(year);
                    yearJSON.setMonths(new ArrayList<>());
                    calendarData.getYears().add(yearJSON);
                }
                if(!checkIfYearMonthExistsInJSON(monthToFill)) {
                    MonthJSONFormat tmp = new MonthJSONFormat();
                    tmp.setMonthName(monthToFill.getMonth().toString());
                    tmp.setDays(new ArrayList<>());
                    getYearJSONFormat(year).getMonths().add(tmp);
                }
                if(!checkIfYearMonthDayExistsInJSON(LocalDate.of(yearNum, monthToFill.getMonth().getValue(), dayNum))) {
                    DateJSONFormat date = new DateJSONFormat();
                    date.setDate(String.valueOf(dayNum));
                    date.setEvents(new ArrayList<>());
                    getMonthJSONFormat(monthToFill).getDays().add(date);
                }
                LocalDate datee = LocalDate.of(yearNum, monthToFill.getMonth().getValue(), dayNum);
                DateJSONFormat dateJSON = getDateJSONFormat(datee);
                DayScene = new Scene(new DayScreen(dateJSON, this, calendarScreen, datee,isProject),900, 600);
                this.setScene(DayScene);
            });
            colIndex++;
            if(colIndex == 7) {
                rowIndex++;
                colIndex = 0;
            }
        }

    }
    @SuppressWarnings("null")
    public void addEventToUser(LocalDate date, String user, EventJSONFormat event){
        String year = date.getYear() +"";
        YearsJSONFormat userJSON = CalendarJSONConverter.convertFromMap(client.getUserCalendar(user));
        if(!checkIfYearExistsInJSON(year, userJSON)){
            YearJSONFormat yearJSON = new YearJSONFormat();
            yearJSON.setYearNum(year);
            yearJSON.setMonths(new ArrayList<>());
            userJSON.getYears().add(yearJSON);
        }
        YearJSONFormat yearJSONFormat = null;
        for(YearJSONFormat yearCheck : userJSON.getYears()) {
            if(yearCheck.getYearNum().equals(year)) {
                yearJSONFormat = yearCheck;
            }
        }
        YearMonth monthToFill = YearMonth.from(date);
        if(!checkIfYearMonthExistsInJSON(monthToFill, yearJSONFormat)) {
            MonthJSONFormat tmp = new MonthJSONFormat();
            tmp.setMonthName(monthToFill.getMonth().toString());
            tmp.setDays(new ArrayList<>());
            yearJSONFormat.getMonths().add(tmp);
        }
        MonthJSONFormat monthJSONFormat = null;
        for(MonthJSONFormat monthCheck : yearJSONFormat.getMonths()) {
            if(monthCheck.getMonthName().equals(date.getMonth().name())) {
                monthJSONFormat = monthCheck;
            }
        }
        if(!checkIfYearMonthDayExistsInJSON(date, monthJSONFormat)) {
            DateJSONFormat dateFormat = new DateJSONFormat();
            dateFormat.setDate(String.valueOf(date.getDayOfMonth()));
            dateFormat.setEvents(new ArrayList<>());
            monthJSONFormat.getDays().add(dateFormat);
        }
        DateJSONFormat dateJSON = null;
        for(DateJSONFormat dayCheck : monthJSONFormat.getDays()) {
            if(dayCheck.getDate().equals(date.getDayOfMonth()+"")) {
                dateJSON = dayCheck;
            }
        }
        dateJSON.getEvents().add(event);
        client.updateUserCalendar(user, CalendarJSONConverter.convertToString(userJSON));
    }
    @SuppressWarnings("null")
    public void addEventToProject(LocalDate date, String projectID, EventJSONFormat event){
        String year = date.getYear() +"";
        YearsJSONFormat userJSON = CalendarJSONConverter.convertFromMap(client.getProjectCalendar(projectID));
        if(!checkIfYearExistsInJSON(year, userJSON)){
            YearJSONFormat yearJSON = new YearJSONFormat();
            yearJSON.setYearNum(year);
            yearJSON.setMonths(new ArrayList<>());
            userJSON.getYears().add(yearJSON);
        }
        YearJSONFormat yearJSONFormat = null;
        for(YearJSONFormat yearCheck : userJSON.getYears()) {
            if(yearCheck.getYearNum().equals(year)) {
                yearJSONFormat = yearCheck;
            }
        }
        YearMonth monthToFill = YearMonth.from(date);
        if(!checkIfYearMonthExistsInJSON(monthToFill, yearJSONFormat)) {
            MonthJSONFormat tmp = new MonthJSONFormat();
            tmp.setMonthName(monthToFill.getMonth().toString());
            tmp.setDays(new ArrayList<>());
            yearJSONFormat.getMonths().add(tmp);
        }
        MonthJSONFormat monthJSONFormat = null;
        for(MonthJSONFormat monthCheck : yearJSONFormat.getMonths()) {
            if(monthCheck.getMonthName().equals(date.getMonth().name())) {
                monthJSONFormat = monthCheck;
            }
        }
        if(!checkIfYearMonthDayExistsInJSON(date, monthJSONFormat)) {
            DateJSONFormat dateFormat = new DateJSONFormat();
            dateFormat.setDate(String.valueOf(date.getDayOfMonth()));
            dateFormat.setEvents(new ArrayList<>());
            monthJSONFormat.getDays().add(dateFormat);
        }
        DateJSONFormat dateJSON = null;
        for(DateJSONFormat dayCheck : monthJSONFormat.getDays()) {
            if(dayCheck.getDate().equals(date.getDayOfMonth()+"")) {
                dateJSON = dayCheck;
            }
        }
        dateJSON.getEvents().add(event);
        client.updateProjectCalendar(this.getUser(), projectID, CalendarJSONConverter.convertToString(userJSON));
    }
    public String getUser(){
        return currentUser;
    }
    public String getProject(){
        return projectID;
    }
}
