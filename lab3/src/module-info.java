module lab3db {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.logging;
    requires java.sql;
    //noinspection Java9RedundantRequiresStatement
    requires mysql.connector.java;

    opens pogrebenko.lab3db.controller;
    opens pogrebenko.lab3db.model.medicine;
}