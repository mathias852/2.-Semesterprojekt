module org.openjfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires postgresql;

    opens presentation to javafx.fxml;
    exports presentation;
}