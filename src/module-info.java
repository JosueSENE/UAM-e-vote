module UAM_evote {
    // 1. Modules requis indispensables pour JavaFX et la base de données
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires transitive javafx.graphics;
	requires java.desktop; // Règle le problème de visibilité du Stage

    // 2. Ouvrir les packages pour la réflexion (TableView et FXMLLoader)
    opens app to javafx.graphics, javafx.fxml;
    opens app.controller to javafx.fxml, javafx.graphics;
    opens app.model to javafx.base; // Permet aux TableView de lire les propriétés des objets (User)

    // 3. Exporter explicitement les packages pour qu'ils soient accessibles par le moteur JavaFX
    exports app;
    exports app.controller;
    exports app.model;
    exports app.dao;
}