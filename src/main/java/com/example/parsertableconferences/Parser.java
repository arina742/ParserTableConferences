package com.example.parsertableconferences;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    @FXML
    private Label welcomeText;
    @FXML
    private TextArea maintxt;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("underidoderidoderiododeriodoo - Winston Churchill");
    }

    //check if file is empty
    public static boolean isFileEmpty(File file) {
        return file.length() == 0;
    }

    public static Document fileCheck(String filename, String url) throws IOException {
        File file = new File(filename + ".html");
        FileWriter fileWriter = new FileWriter(file);
        Document doc = null;

        if (file.exists()) {
            if (isFileEmpty(file)) {
                doc = Jsoup.connect(url).get();
                fileWriter.write(String.valueOf(doc));
            }
        }
        doc = Jsoup.parse(file, "UTF-8", url);
        return doc;
    }

    public static class Event {

        private String num;
        private String name;
        private String date;
        private String link;
        private Boolean fav;

        public Event(String name, String date, String link) {
            this.name = name;
            this.date = date;
            this.link = link;
            this.fav = false;
        }

        public String getNum() {
            return num;
        }
        public String getName() {
            return name;
        }
        public void setName(String namee) {
            this.name = namee;
        }
        public String getDate() {
            return date;
        }
        public void setDate(String datee) {
            this.date = datee;
        }
        public String getLink() {
            return link;
        }
        public void setLink(String linkk) {
            this.link = linkk;
        }
        public Boolean getFav() {
            return fav;
        }
        public void setFav(Boolean fav) {
            this.fav = fav;
        }
    }

    //to change the date output format
    public String replaceMonth(String str) {
        str = str.replace("янв", "01");
        str = str.replace("фев", "02");
        str = str.replace("мар", "03");
        str = str.replace("апр", "04");
        str = str.replace("май", "05");
        str = str.replace("мая", "05");
        str = str.replace("июн", "06");
        str = str.replace("июл", "07");
        str = str.replace("авг", "08");
        str = str.replace("сен", "09");
        str = str.replace("окт", "10");
        str = str.replace("ноя", "11");
        str = str.replace("дек", "12");
        str = str.replace(" ", ".");
        return str;
    }

    ObservableList<Event> listEvents = FXCollections.observableArrayList();

    //

    @FXML
    private TableView mainTab = new TableView<>(listEvents);
    @FXML
    private TableColumn<Event, String> numColumn, nameColumn, dateColumn, linkColumn;
    @FXML
    private TableColumn<Event, Boolean> favColumn;

    /**
     * getting data from the gorodzovet.ru website
     * @throws IOException
     */
    public void getEventsGor() throws IOException {
        String url = "https://gorodzovet.ru/spb/it/";
        Document doc = fileCheck("gorodzovet", url);

        Elements events = doc.getElementsByClass("event-block");
        for (int i = 0; i < events.size(); i++) {
            String name = events.get(i).getElementsByTag("h3").text();
            String day = events.get(i).getElementsByClass("event-day").text();
            String date;
            if (day.length() == 0) {
                date = events.get(i).getElementsByClass("event-date").text();
            } else {
                String month = events.get(i).getElementsByClass("event-month").text().toLowerCase();
                month = replaceMonth(month);
                date = day + "." + month;
            }
            String link = events.get(i).getElementsByClass("event-link save-click").attr("href");
            listEvents.add(new Event(name, date, "https://gorodzovet.ru" + link));
        }
    }

    //getting data from the all-events.ru website
    public void getEventsAll() throws IOException {
        String url = "https://all-events.ru/events/calendar/type-is-conferencia/theme-is-informatsionnye_tekhnologii/";
        Document doc = fileCheck("all-events", url);

        Elements events = doc.getElementsByClass("event");
        for (int i = 0; i < events.size(); i++) {
            String name = events.get(i).getElementsByClass("event-name").text();
            String date = events.get(i).getElementsByClass("event-date").text();
            if (date.length() > 10) {
                date = date.substring(0, 11);
                String month = date.substring(8, 11);
                month = replaceMonth(month);
                date = date.replace(" - ", "." + month + "-");
                date = replaceMonth(date);
            } else if (date.length() < 10) {
                date = date.substring(0, 6);
                date = replaceMonth(date);
            }
            String link = events.get(i).getElementsByClass("link-button").attr("href");
            listEvents.add(new Event(name, date, "https://all-events.ru" + link));
        }
    }

    //getting data from the expomap.ru website
    public void getEventsExp() throws IOException {
        String url = "https://expomap.ru/conference/theme/it-kommunikatsii-svyaz/";
        Document doc = fileCheck("expomap", url);

        Elements events = doc.getElementsByClass("cl-item");
        for (int i = 0; i < events.size(); i++) {
            String name = events.get(i).getElementsByClass("cli-title").text();
            String date = events.get(i).getElementsByClass("cli-date").text();
            String month_1 = null;
            String month_2;
            int sp = 0, fsp = 0, lsp = 0, msp = 0;

            for (int j = 0; j < date.length(); j++) {
                if (date.charAt(j) == ' ') {
                    sp++;
                    if (sp == 1) {
                        fsp = j;
                    }
                    if (sp == 2) {
                        msp = j;
                    }
                    if (sp >= 4) {
                        lsp = j;
                    }
                }
            }

            if (sp == 2) {
                date = date.substring(0, fsp + 4);
            } else if (sp == 4) {
                date = date.substring(0, lsp + 4);
            } else if (sp == 5) {
                month_1 = date.substring(msp, msp + 4);
                date = date.substring(0, lsp + 4);
            }

            date = replaceMonth(date);

            month_2 = date.substring(date.length() - 3, date.length());
            date = date.replace("с.", "");
            if (month_1 == null) {
                date = date.replace(".по.", month_2 + "-");
            } else {
                date = date.replace(month_1, "").replace(".по.", "-");
            }

            if (date.contains("-")) {
                String[] dayAndMonth;
                dayAndMonth = date.split("-");
                String[] day_1, day_2;
                day_1 = dayAndMonth[0].split("\\.");
                day_2 = dayAndMonth[1].split("\\.");
                DecimalFormat dF = new DecimalFormat("00");
                day_1[0] = dF.format(Integer.parseInt(day_1[0]));
                day_2[0] = dF.format(Integer.parseInt(day_2[0]));
                date = day_1[0] + "." + day_1[1] + "-" + day_2[0] + "." + day_2[1];
            } else {
                String[] day;
                day = date.split("\\.");
                DecimalFormat dF = new DecimalFormat("00");
                day[0] = dF.format(Integer.parseInt(day[0]));
                date = day[0] + "." + day[1];
            }

            String link = events.get(i).getElementsByClass("button icon-sm").attr("href");
            listEvents.add(new Event(name, date, "https://expomap.ru" + link));
        }
    }

    public void printEvents(ObservableList<Event> listEvents) {
        for (int i = 0; i < listEvents.size(); i++) {
            System.out.println(listEvents.get(i).name + "\n" + listEvents.get(i).date + "\n" + listEvents.get(i).link + "\n\n");
        }
    }

    public int strToData(String data, String dayOrMonth) {

        if (dayOrMonth.equals("day")) {
            return Integer.parseInt(data.substring(0, 2));
        } else {

            return Integer.parseInt(data.substring(3, 5));
        }
    }

    //swaps two elements in the array
    public void swap(ObservableList<Event> listEvents, int i, int j) {
        Event temp = listEvents.get(i);
        listEvents.set(i, listEvents.get(j));
        listEvents.set(j, temp);
    }

    //bubble sort
    public void sortEvents(ObservableList<Event> listEvents) {
        for (int i = listEvents.size() - 1; i >= 1; i--) {
            for (int j = 0; j < i; j++) {

                int day_1 = strToData(listEvents.get(j).date, "day");
                int day_2 = strToData(listEvents.get(j + 1).date, "day");
                int month_1 = strToData(listEvents.get(j).date, "month");
                int month_2 = strToData(listEvents.get(j + 1).date, "month");
                if (((day_1 > day_2) && (month_1 >= month_2)) || (month_1 > month_2) && (day_1 <= day_2)) {
                    swap(listEvents, j, j + 1);
                }
            }
        }
    }

    public void addNumber(ObservableList<Event> listEvents)
    {
        int i, count;
        for(i = 0, count = 1; i < listEvents.size(); i++, count++)
        {
            listEvents.get(i).num = String.valueOf(count);
        }
    }

    private static final int N = 365;
    @FXML
    protected void Refresh() throws IOException{
        StartParcing();

//        for (int i = 0; i < listEvents.size(); i++) {
//            maintxt.appendText(listEvents.get(i).name + "\n" + listEvents.get(i).date + "\n" + listEvents.get(i).link + "\n\n");
//        }

        numColumn.setCellValueFactory(new PropertyValueFactory<Event, String> ("num"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Event, String> ("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<Event, String> ("date"));
        linkColumn.setCellValueFactory(new PropertyValueFactory<Event, String> ("link"));
        favColumn.setCellValueFactory(new PropertyValueFactory<>("fav"));
        favColumn.setCellFactory(tc -> new CheckBoxTableCell<>());
        mainTab.setItems(listEvents);
    }

    public void StartParcing() throws IOException {
        //getEventsGor();
        getEventsAll();
        getEventsExp();
        sortEvents(listEvents);
        addNumber(listEvents);
    }
}