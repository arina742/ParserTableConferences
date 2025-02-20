import org.example.Main;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private Main main;

    @BeforeEach
    void setUp() {
        main = new Main();
    }

    @Test
    void testIsFileEmpty() {
        File file = new File("testFile.txt");
        try {
            file.createNewFile();
            assertTrue(main.isFileEmpty(file));

            FileWriter writer = new FileWriter(file);
            writer.write("Some content");
            writer.close();
            assertFalse(main.isFileEmpty(file));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            file.delete();
        }
    }

    @Test
    void testFileCheck() throws IOException {
        String filename = "testFile";
        String url = "https://example.com";
        File file = new File(filename + ".html");

        Document doc = main.fileCheck(filename, url);
        assertNotNull(doc);
        assertTrue(file.exists());
        assertFalse(main.isFileEmpty(file));

        file.delete();
    }

    @Test
    void testReplaceMonth() {
        String input = "15 янв 2023";
        String expected = "15.01.2023";
        assertEquals(expected, main.replaceMonth(input));
    }

    @Test
    void testGetEventsGor() throws IOException {
        main.getEventsGor();
        assertFalse(main.listEvents.isEmpty());
    }

    @Test
    void testGetEventsAll() throws IOException {
        main.getEventsAll();
        assertFalse(main.listEvents.isEmpty());
    }

    @Test
    void testGetEventsExp() throws IOException {
        main.getEventsExp();
        assertFalse(main.listEvents.isEmpty());
    }

    @Test
    void testSortEvents() throws IOException {
        main.getEventsGor();
        main.getEventsAll();
        main.getEventsExp();

        ArrayList<Main.Event> events = main.listEvents;
        main.sortEvents(events);

        for (int i = 0; i < events.size() - 1; i++) {
            int day1 = main.strToData(events.get(i).date, "day");
            int day2 = main.strToData(events.get(i + 1).date, "day");
            int month1 = main.strToData(events.get(i).date, "month");
            int month2 = main.strToData(events.get(i + 1).date, "month");

            assertTrue(month1 <= month2);
            if (month1 == month2) {
                assertTrue(day1 <= day2);
            }
        }
    }

    @Test
    void testStrToData() {
        String date = "15.01.2023";
        assertEquals(15, main.strToData(date, "day"));
        assertEquals(1, main.strToData(date, "month"));
    }

    @Test
    void testSwap() {
        ArrayList<Main.Event> events = new ArrayList<>();
        events.add(new Main.Event("Event1", "01.01.2023", "link1"));
        events.add(new Main.Event("Event2", "02.01.2023", "link2"));

        main.swap(events, 0, 1);
        assertEquals("Event2", events.get(0).name);
        assertEquals("Event1", events.get(1).name);
    }
}
