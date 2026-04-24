module sistema {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
	requires java.sql;	

    opens application.controller to javafx.fxml;
    opens application.model to javafx.base;
    opens application to javafx.graphics;

    exports application;
    exports application.controller;
    exports application.view;
}
