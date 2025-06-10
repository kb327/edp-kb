module edp.wat.edu.pl.projectkb {
    requires org.hibernate.orm.core;
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires static lombok;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires jakarta.persistence;
    requires jbcrypt;

    opens edp.wat.edu.pl.projectkb to javafx.fxml;
    exports edp.wat.edu.pl.projectkb;
    exports edp.wat.edu.pl.projectkb.controller to javafx.fxml;
    exports edp.wat.edu.pl.projectkb.model to com.fasterxml.jackson.databind;
    opens edp.wat.edu.pl.projectkb.controller to javafx.fxml;
    exports edp.wat.edu.pl.projectkb.model.weatherApiModel to com.fasterxml.jackson.databind;
    opens edp.wat.edu.pl.projectkb.service;
    opens edp.wat.edu.pl.projectkb.model to org.hibernate.orm.core, javafx.fxml;

}