package org.example;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

public class Main {
    //check if file is empty
    public static boolean isFileEmpty(File file) {
        return file.length() == 0;
    }

    //get information about events from "all-events.ru"
    public static void getInfo1() throws IOException {
        File file = new File("all-events.html");
        FileWriter fileWriter = new FileWriter(file);
        Document doc = null;

        if (file.exists()) {
            if (isFileEmpty(file)) {
                doc = Jsoup.connect("https://all-events.ru/events/calendar/type-is-conferencia/theme-is-informatsionnye_tekhnologii/").get();
                fileWriter.write(String.valueOf(doc));
            }
        }
        doc = Jsoup.parse(file, "UTF-8", "https://all-events.ru/events/calendar/type-is-conferencia/theme-is-informatsionnye_tekhnologii/");


        //collected name of events from site
        Elements eventsName = doc.getElementsByClass("event-name");
        ArrayList<String> listNameEvents = new ArrayList<>();
        eventsName.forEach(element -> listNameEvents.add(element.text()));

        //collected dates of events from site
        Elements eventsDate = doc.getElementsByClass("event-date");
        ArrayList<String> listDateEvents = new ArrayList<>();
        eventsDate.forEach(element -> listDateEvents.add(element.text()));

        //todo: will be good, if someone figure out how don't collect extra dates in previous step

        //deleted extra dates
        for (int i = 0; i < listDateEvents.size(); i++) {
            if (listDateEvents.get(i).contains("января") ||
                    listDateEvents.get(i).contains("февраля") ||
                    listDateEvents.get(i).contains("марта") ||
                    listDateEvents.get(i).contains("апреля") ||
                    listDateEvents.get(i).contains("мая") ||
                    listDateEvents.get(i).contains("июня") ||
                    listDateEvents.get(i).contains("июля") ||
                    listDateEvents.get(i).contains("августа") ||
                    listDateEvents.get(i).contains("сентября") ||
                    listDateEvents.get(i).contains("октября") ||
                    listDateEvents.get(i).contains("ноября") ||
                    listDateEvents.get(i).contains("декабря")) {
                listDateEvents.remove(i);
                i--;
            }
        }

        //collected links of events from site
        Elements eventsLink = doc.getElementsByClass("link-button");
        ArrayList<String> listLinkEvents = new ArrayList<>();
        eventsLink.forEach(element -> listLinkEvents.add(element.attr("href")));


        //collected locations of events from site
        Elements eventsPlace = doc.getElementsByClass("event-place");
        ArrayList<String> listPlaceEvents = new ArrayList<>();
        eventsPlace.forEach(element -> listPlaceEvents.add(element.text()));

        //print information about events
        for (int i = 0; i < listNameEvents.size(); i++) {
            System.out.println(listNameEvents.get(i) + "\n" + listPlaceEvents.get(i) + "\n" + listDateEvents.get(i) + "\n" + "https://all-events.ru" + listLinkEvents.get(i) + "\n\n");
        }
        fileWriter.flush();
    }

    public static void main(String[] args) throws IOException {
        getInfo1();
    }
}