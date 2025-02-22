module com.ums {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires java.sql;


    opens com.ums to javafx.fxml;
    opens com.ums.controller to javafx.fxml;
    opens com.ums.data to javafx.fxml;
    exports com.ums;
    exports com.ums.controller;
    exports com.ums.data;


}