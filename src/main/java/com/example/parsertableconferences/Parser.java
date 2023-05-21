package com.example.parsertableconferences;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;

public class Parser {
    @FXML
    private Label welcomeText;
    @FXML
    private TextArea maintxt;
    @FXML
    private TextField filterField;

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
        public static String favoriteEvents;
        public static String[] favoriteEventsLinks;
        private static String num;
        private final SimpleStringProperty name;
        private final SimpleStringProperty date;
        private final SimpleStringProperty link;
        private CheckBox select;

        public Event(String name, String date, String link) {
            this.name = new SimpleStringProperty(name);
            this.date = new SimpleStringProperty(date);
            this.link = new SimpleStringProperty(link);
            this.select = new CheckBox();

            favoriteEvents = "";
            favoriteEventsLinks = null;
            favoriteEvents = getFavoriteEvents();
            favoriteEventsLinks = favoriteEvents.split(",");

            for(String evlink: favoriteEventsLinks)
                if(evlink.equals(getLink()))
                    select.setSelected(true);

            select.selectedProperty().addListener(new ChangeListener<Boolean>() {
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                    if (select.isSelected()) {
                        favoriteEvents += (getLink() + ",");
                        saveFavoriteEvents(favoriteEvents);
                    } else {
                        favoriteEvents = favoriteEvents.replaceAll((getLink() + ","), "");
                        saveFavoriteEvents(favoriteEvents);
                    }

                }
            });
        }

        public static String getFavoriteEvents() {
            String str = new String();
            try (DataInputStream dis = new DataInputStream(new FileInputStream("favs.dat"))) {
                str = dis.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(str != null) return str;
            else return "";
        }

        public static void saveFavoriteEvents(String favEvents) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("favs.dat")))) {
                writer.write(favEvents); // записываем строку в файл
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getNum() {
            return num;
        }

        public String getName() {
            return name.get();
        }

        public void setName(String namee) {
            name.set(namee);
        }

        public String getDate() {
            return date.get();
        }

        public void setDate(String datee) {
            date.set(datee);
        }

        public String getLink() {
            return link.get();
        }

        public void setLink(String linkk) {
            link.set(linkk);
        }

        public CheckBox getSelect() {
            return select;
        }

        public void setSelect(CheckBox select) {
            this.select = select;
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
    ObservableList<Event> favEvents = FXCollections.observableArrayList();
    FilteredList<Event> filteredEvents;

    @FXML
    public TableView mainTab = new TableView<>(listEvents);
    @FXML
    public TableColumn<Event, String> numColumn, nameColumn, dateColumn;

    @FXML
    public TableColumn<Event, Void> linkColumn;
    @FXML
    public TableColumn<Event, Boolean> favColumn;

    /**
     * getting data from the gorodzovet.ru website
     *
     * @throws IOException
     */
    public void getEventsGor() throws IOException {
        String url = "https://gorodzovet.ru/spb/it/";
        Document doc = fileCheck("gorodzovet", url);

        Elements events = doc.getElementsByClass("col-lg-4 col-md-6 col-sm-12 mb-4");

        for (int i = 0; i < events.size(); i++) {
            String name = events.get(i).getElementsByTag("h3").text();
            String day = events.get(i).getElementsByClass("event-day").text();
            String date;
            if (day.length() == 0) {
                date = events.get(i).getElementsByClass("event-date").text();

                String month1 = replaceMonth(date.substring(0, 3).toLowerCase());
                String month2 = month1;

                if (date.length() > 10) {
                    month2 = replaceMonth(date.substring(4, 7).toLowerCase());
                }

                String day1 = date.substring(date.length() - 5, date.length() - 3);
                String day2 = date.substring(date.length() - 2, date.length());

                date = day1 + "." + month1 + "-" + day2 + "." + month2;
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

                int day_1 = strToData(listEvents.get(j).date.get(), "day");
                int day_2 = strToData(listEvents.get(j + 1).date.get(), "day");
                int month_1 = strToData(listEvents.get(j).date.get(), "month");
                int month_2 = strToData(listEvents.get(j + 1).date.get(), "month");
                if (((day_1 > day_2) && (month_1 >= month_2)) || (month_1 > month_2) && (day_1 <= day_2)) {
                    swap(listEvents, j, j + 1);
                }
            }
        }
    }

    public void addNumber(ObservableList<Event> listEvents) {
        int i, count;
        for (i = 0, count = 1; i < listEvents.size(); i++, count++) {
            listEvents.get(i).num = String.valueOf(count);
        }
    }

    public void GetFavorites() {
        favEvents.clear();
        for (Event listEvent : listEvents) {
            if (listEvent.select.isSelected()) favEvents.add(listEvent);
        }

        mainTab.setItems(favEvents);
    }

    public void GetAllEvents() {
        mainTab.setItems(filteredEvents);
    }

    public boolean DateBetween(Event e, String filter) {
        if (e.getDate().length() != 11 || !filter.matches("[-+]?\\d+")) return false;
        Integer filterDate = Integer.parseInt(filter);
        Integer dayStart = Integer.parseInt(String.valueOf(e.getDate().charAt(0)) + String.valueOf(e.getDate().charAt(1)));
        Integer dayEnd = Integer.parseInt(String.valueOf(e.getDate().charAt(6)) + String.valueOf(e.getDate().charAt(7)));
        Integer monthStart = Integer.parseInt(String.valueOf(e.getDate().charAt(3)) + String.valueOf(e.getDate().charAt(4)));
        Integer monthEnd = Integer.parseInt(String.valueOf(e.getDate().charAt(9)) + String.valueOf(e.getDate().charAt(10)));
        if (dayStart < filterDate && filterDate < dayEnd && monthStart == monthEnd) return true;
        else if (monthStart != monthEnd && dayStart < filterDate && filterDate < dayEnd + (monthEnd - monthStart) * 30)
            return true;
        else return false;
    }

    public void Search(FilteredList<Event> list) {
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            list.setPredicate((event) -> {
                if (newValue != null && !newValue.isEmpty()) {
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (event.getName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                        return true;
                    } else if (event.getDate().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                        return true;
                    } else {
                        return this.DateBetween(event, lowerCaseFilter);
                    }
                } else {
                    return true;
                }
            });
        });
    }

    @FXML
    protected void Refresh() throws IOException {
        listEvents.clear();
        StartParcing();

        numColumn.setCellValueFactory(new PropertyValueFactory<Event, String>("num"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Event, String>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<Event, String>("date"));
        Callback<TableColumn<Event, Void>, TableCell<Event, Void>> linkCellFactory = new Callback<TableColumn<Event, Void>, TableCell<Event, Void>>()
        {
            public TableCell<Event, Void> call(TableColumn<Event, Void> param) {
                final TableCell<Event, Void> cell = new TableCell<Event, Void>() {
                    private final Button button = new Button("Button");
                    {
                        button.setId("linkButton");
                        //button.setText(getTableView().getItems().get(getIndex()).getLink());
                        button.setText("Подробнее");


                        // обработчик события нажатия на кнопку
                        button.setOnAction((event) -> {
                            // получаем объект, соответствующий строке таблицы
                            Event obj = getTableView().getItems().get(getIndex());
                            // выполняем действия при нажатии на кнопку
                            try {
                                java.awt.Desktop.getDesktop().browse(new URI(obj.getLink()));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (URISyntaxException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                };
                return cell;
            }
        };
        linkColumn.setCellFactory(linkCellFactory);

        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String somtxt = mouseEvent.toString();
                maintxt.appendText(somtxt);
                if (mouseEvent.getClickCount() == 2) maintxt.appendText(somtxt);
            }
        };
        linkColumn.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);

        favColumn.setCellValueFactory(new PropertyValueFactory<>("select"));

        filteredEvents = new FilteredList<>(listEvents, b -> true);
        Search(filteredEvents);
        SortedList<Event> sortedEvents = new SortedList<>(filteredEvents);
        sortedEvents.comparatorProperty().bind(mainTab.comparatorProperty());

        mainTab.setRowFactory(mainTab -> {
            TableRow<Event> row = new TableRow<>();
            row.pseudoClassStateChanged(PseudoClass.getPseudoClass("odd"),
                    (Integer.parseInt(Event.num)) % 2 > 0);
            return row;
        });

        mainTab.setItems(sortedEvents);
        mainTab.setEditable(true);
    }

    public void StartParcing() throws IOException {
        getEventsGor();
        getEventsAll();
        //if(listEvents == null) "нет подключения к тыренту";
        getEventsExp();
        sortEvents(listEvents);
        addNumber(listEvents);
    }

    public Parser() {

    }
}