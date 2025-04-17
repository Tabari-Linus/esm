module lii.employeemanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens lii.employeemanagementsystem to javafx.fxml;
    exports lii.employeemanagementsystem;
    exports lii.employeemanagementsystem.ui;
    opens lii.employeemanagementsystem.ui to javafx.fxml;
}