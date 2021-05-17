module org.openjfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires postgresql;
    requires java.desktop;

    opens presentation to javafx.fxml;
    exports presentation;
}