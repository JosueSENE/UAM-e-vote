module UAM_evote {
    // 1. Modules requis indispensables pour JavaFX et la base de données
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires transitive javafx.graphics; // Le mot-clé 'transitive' règle le problème de Stage

    // 2. Ouvrir les packages aux mécanismes de réflexion de JavaFX (FXMLLoader)
    // opens app to javafx.graphics, javafx.fxml;
//    opens app.controller to javafx.fxml;
    opens app.model to javafx.base; // Permet aux TableView de lire les propriétés des objets

    // 3. Exporter explicitement les packages pour qu'ils soient accessibles entre eux
//    exports app;
//    exports app.controller;
    exports app.model;
//    exports app.dao;
//    exports app.service;
}