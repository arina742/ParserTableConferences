module com.example.ParserTableConferences {
    requires javafx.controls;
    requires javafx.fxml;

//    requires com.dlsc.formsfx;
//    requires org.kordamp.bootstrapfx.core;
    requires org.jsoup;

    opens com.example.parsertableconferences to javafx.fxml;
    exports com.example.parsertableconferences;
}