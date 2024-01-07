package main.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Overview {
    public static void applyStylesheet(Scene scene) {
        String css = Inschrijving.class.getResource("style.css").toExternalForm();
        scene.getStylesheets().add(css);
    }

    public static void openOverviewPage() {
        Stage overviewStage = new Stage();
        overviewStage.setTitle("Overview pagina");

        // Titel bovenaan de pagina
        Text title = new Text("Overzicht van de query's");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 18));

        // Maak een HBox voor het opmaken van de titel
        HBox titleBox = new HBox();
        titleBox.getChildren().add(title);
        titleBox.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        titleBox.setPadding(new Insets(10, 10, 10, 10));

        VBox vbox = new VBox(10); // 10 is de verticale ruimte tussen knoppen en lijst
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setAlignment(javafx.geometry.Pos.CENTER); // Knoppen en lijst in het midden van de pagina

        Button average = new Button("Gemiddelde voortgang per module");
        average.setOnAction(e -> average());

        Button averagePerson = new Button("Gemiddelde voortgang per module per persoon");
        averagePerson.setOnAction(e -> averagePerson());

        Button top3 = new Button("Top 3 meest bekeken webcasts");
        top3.setOnAction(e -> topWebcast());

        Button accomplished = new Button("Aantal geslaagde cursisten");
        accomplished.setOnAction(e -> accomplishedCourses());

        vbox.getChildren().addAll(titleBox, average, averagePerson, top3, accomplished);

        // Maak een Scene en toon het venster
        Scene scene = new Scene(vbox, 500, 500);
        applyStylesheet(scene);
        overviewStage.setScene(scene);
        overviewStage.show();
    }

    static void topWebcast() {
        Stage topWebcast = new Stage();
        topWebcast.setTitle("Top 3");

        Text title = new Text("Top 3 meest bekeken webcasts");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 18));

        HBox titleBox = new HBox();
        titleBox.getChildren().add(title);
        titleBox.setAlignment(Pos.TOP_CENTER);
        titleBox.setPadding(new Insets(10, 10, 10, 10));

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(titleBox);
        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.getGebruikersnaam();
        String wachtwoord = databaseConnect.GetPass();

        String query = "SELECT TOP 3 W.Titel, V.CursistEmail, V.BekekenPercentage " +
                "FROM VoortgangWebcast V " +
                "JOIN Webcast W ON V.WebcastID = W.WebcastID " +
                "ORDER BY V.BekekenPercentage DESC";
        ;

        try {
            Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord);
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            // Bereid de SQL-query voor
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String webcastTitel = resultSet.getString("Titel");
                int bekekenPercentage = resultSet.getInt("BekekenPercentage");

                Text webcastInfo = new Text(webcastTitel + ": " + bekekenPercentage + "% bekeken");
                vbox.getChildren().add(webcastInfo);
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Databasefout", "Er is een fout opgetreden bij het ophalen van de gegevens uit de database.");
        }

        Scene scene = new Scene(vbox, 500, 500);
        applyStylesheet(scene);
        topWebcast.setScene(scene);
        topWebcast.show();
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    static void average() {
        Stage average = new Stage();
        average.setTitle("Gemiddelde voortgang per module");

        Text title = new Text("Gemiddelde voortgang per module");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 18));

        HBox titleBox = new HBox();
        titleBox.getChildren().add(title);
        titleBox.setAlignment(Pos.TOP_CENTER);
        titleBox.setPadding(new Insets(10, 10, 10, 10));

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(titleBox);

        // Keuzelijst voor cursussen
        ComboBox<String> cursusComboBox = new ComboBox<>(
                FXCollections.observableArrayList(getCursusNamenFromDatabase()));
        cursusComboBox.setPromptText("Selecteer een cursus");
        Button calculateButton = new Button("Bereken Gemiddelde Voortgang");
        calculateButton.setOnAction(e -> toonGemiddeldeVoortgang(cursusComboBox.getValue(), vbox));

        HBox selectieBox = new HBox(10);
        selectieBox.getChildren().addAll(new Label("Selecteer een cursus: "), cursusComboBox, calculateButton);
        selectieBox.setAlignment(Pos.CENTER);

        vbox.getChildren().add(selectieBox);

        Scene scene = new Scene(vbox, 500, 500);
        applyStylesheet(scene);
        average.setScene(scene);
        average.show();
    }

    static void averagePerson() {
        Stage averagePerson = new Stage();
        averagePerson.setTitle("Gemiddelde voortgang per module per persoon");

        Text title = new Text("Gemiddelde voortgang per module per persoon");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 18));

        HBox titleBox = new HBox();
        titleBox.getChildren().add(title);
        titleBox.setAlignment(Pos.TOP_CENTER);
        titleBox.setPadding(new Insets(10, 10, 10, 10));

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(titleBox);

        // Choicebox for cursist
        ComboBox<String> cursistComboBox = new ComboBox<>(
                FXCollections.observableArrayList(getCursistNamenFromDB()));
        cursistComboBox.setPromptText("Selecteer Cursist");

        // Keuzelijst voor cursussen
        ComboBox<String> cursusComboBox = new ComboBox<>(
                FXCollections.observableArrayList(getCursusNamenFromDatabase()));
        cursusComboBox.setPromptText("Selecteer Cursus");

        Button calculateButton = new Button("Bereken Gemiddelde Voortgang");
        calculateButton
                .setOnAction(e -> showAveragePercentage(cursusComboBox.getValue(), cursistComboBox.getValue(), vbox));

        HBox selectieBox = new HBox(10);
        selectieBox.getChildren().addAll(new Label("Selecteer een cursus: "), cursistComboBox, cursusComboBox,
                calculateButton);
        selectieBox.setAlignment(Pos.CENTER);

        vbox.getChildren().add(selectieBox);

        Scene scene = new Scene(vbox, 800, 800);
        applyStylesheet(scene);
        averagePerson.setScene(scene);
        averagePerson.show();
    }

    static void accomplishedCourses() {
        Stage accomplishedCourses = new Stage();
        accomplishedCourses.setTitle("Hoeveel cursisten hebben het behaald");

        Text title = new Text("Hoeveel cursisten hebben het behaald");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 18));

        HBox titleBox = new HBox();
        titleBox.getChildren().add(title);
        titleBox.setAlignment(Pos.TOP_CENTER);
        titleBox.setPadding(new Insets(10, 10, 10, 10));

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(titleBox);

        // Keuzelijst voor cursussen
        ComboBox<String> cursusComboBox = new ComboBox<>(
                FXCollections.observableArrayList(getCursusNamenFromDatabase()));
        cursusComboBox.setPromptText("Selecteer een cursus");
        Button calculateButton = new Button("Bereken");
        calculateButton.setOnAction(e -> showAccomplishedCursist(cursusComboBox.getValue(), vbox));

        HBox selectieBox = new HBox(10);
        selectieBox.getChildren().addAll(new Label("Selecteer een cursus: "), cursusComboBox, calculateButton);
        selectieBox.setAlignment(Pos.CENTER);

        vbox.getChildren().add(selectieBox);

        Scene scene = new Scene(vbox, 500, 500);
        applyStylesheet(scene);
        accomplishedCourses.setScene(scene);
        accomplishedCourses.show();
    }

    private static void toonGemiddeldeVoortgang(String geselecteerdeCursus, VBox vbox) {
        int geselecteerdeCursusID = getCursusIDFromDatabase(geselecteerdeCursus);
        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.getGebruikersnaam();
        String wachtwoord = databaseConnect.GetPass();
        Label moduleLabel = new Label();
        try {
            Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord);
            String query = "SELECT m.ModuleID, m.Titel AS ModuleNaam, AVG(COALESCE(vm.VoortgangPercentage, 0)) AS GemiddeldeVoortgangPercentage "
                    + "FROM Module m "
                    + "JOIN Cursus c ON m.CursusID = c.CursusID "
                    + "LEFT JOIN VoortgangModule vm ON m.ModuleID = vm.ModuleID "
                    + "WHERE c.CursusID = ? "
                    + "GROUP BY m.ModuleID, m.Titel";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, geselecteerdeCursusID);
                vbox.getChildren().clear();
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Voeg gemiddelde voortgang per module toe aan de JavaFX GUI
                    moduleLabel.setText("");
                    boolean rowsFound = false;
                    while (resultSet.next()) {
                        rowsFound = true;
                        int moduleID = resultSet.getInt("ModuleID");
                        String moduleNaam = resultSet.getString("ModuleNaam");
                        double gemiddeldeVoortgang = resultSet.getDouble("GemiddeldeVoortgangPercentage");

                        moduleLabel.setText(
                                moduleNaam + " (Module ID: " + moduleID + "): " + gemiddeldeVoortgang + "%");
                        vbox.getChildren().add(moduleLabel);
                    }

                    if (!rowsFound) {
                        Label noDataLabel = new Label("Geen gegevens gevonden voor de geselecteerde cursus.");
                        vbox.getChildren().add(noDataLabel);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Voeg een foutmelding toe aan de GUI als er een SQL-fout optreedt
            Label errorLabel = new Label("Fout bij het ophalen van gegevens: " + e.getMessage());
            vbox.getChildren().add(errorLabel);
        }
    }

    private static void showAccomplishedCursist(String geselecteerdeCursus, VBox vbox) {
        int geselecteerdeCursusID = getCursusIDFromDatabase(geselecteerdeCursus);
        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.getGebruikersnaam();
        String wachtwoord = databaseConnect.GetPass();
        Label moduleLabel = new Label();
        String hebbenOfHeeft = "";
        try {
            Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord);
            String query = "SELECT c.CursusNaam, COUNT(DISTINCT i.CursistEmail) AS AantalBehaaldeCursisten "
                    + "FROM Cursus c "
                    + "JOIN Inschrijving i ON c.CursusID = i.CursusID "
                    + "LEFT JOIN VoortgangModule vm ON i.CursistEmail = vm.CursistEmail "
                    + "AND vm.ModuleID IN (SELECT ModuleID FROM Module WHERE CursusID = c.CursusID) "
                    + "WHERE c.CursusID = ? AND COALESCE(vm.VoortgangPercentage, 0) = 100 "
                    + "GROUP BY c.CursusNaam";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, geselecteerdeCursusID);
                vbox.getChildren().clear();
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Voeg gemiddelde voortgang per module toe aan de JavaFX GUI
                    moduleLabel.setText("");
                    boolean rowsFound = false;
                    while (resultSet.next()) {
                        rowsFound = true;
                        String cursusNaam = resultSet.getString("CursusNaam");
                        int AantalBehaaldeCursisten = resultSet.getInt("AantalBehaaldeCursisten");
                        if (AantalBehaaldeCursisten == 1) {
                            hebbenOfHeeft = "cursist heeft";
                        } else {
                            hebbenOfHeeft = "cursisten hebben";
                        }
                        moduleLabel.setText(
                                AantalBehaaldeCursisten + " " + hebbenOfHeeft + " de " + cursusNaam + " behaald");
                        vbox.getChildren().add(moduleLabel);
                    }

                    if (!rowsFound) {
                        Label noDataLabel = new Label("Nog niemand is geslaagd voor deze cursus!.");
                        vbox.getChildren().add(noDataLabel);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Voeg een foutmelding toe aan de GUI als er een SQL-fout optreedt
            Label errorLabel = new Label("Fout bij het ophalen van gegevens: " + e.getMessage());
            vbox.getChildren().add(errorLabel);
        }
    }

    private static void showAveragePercentage(String geselecteerdeCursus, String geselecteerdeCursist, VBox vbox) {
        int geselecteerdeCursusID = getCursusIDFromDatabase(geselecteerdeCursus);
        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.getGebruikersnaam();
        String wachtwoord = databaseConnect.GetPass();
        Label moduleLabel = new Label();
        try {
            Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord);
            String query = "SELECT m.ModuleID, m.Titel AS ModuleNaam, COALESCE(MAX(vm.VoortgangPercentage), 0)  AS GemiddeldeVoortgangPercentage "
                    + "FROM Module m "
                    + "JOIN Cursus c ON m.CursusID = c.CursusID "
                    + "LEFT JOIN VoortgangModule vm ON m.ModuleID = vm.ModuleID "
                    + "AND vm.CursistEmail = ?"
                    + "WHERE c.CursusID = ? "
                    + "GROUP BY m.ModuleID, m.Titel";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, geselecteerdeCursist);
                statement.setInt(2, geselecteerdeCursusID);
                vbox.getChildren().clear();
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Voeg gemiddelde voortgang per module toe aan de JavaFX GUI
                    moduleLabel.setText("");
                    boolean rowsFound = false;
                    while (resultSet.next()) {
                        rowsFound = true;
                        int moduleID = resultSet.getInt("ModuleID");
                        String moduleNaam = resultSet.getString("ModuleNaam");
                        double gemiddeldeVoortgang = resultSet.getDouble("GemiddeldeVoortgangPercentage");

                        moduleLabel.setText(
                                moduleNaam + " (Module ID: " + moduleID + "): " + gemiddeldeVoortgang + "%");
                        vbox.getChildren().add(moduleLabel);
                    }

                    if (!rowsFound) {
                        Label noDataLabel = new Label("Geen gegevens gevonden voor de geselecteerde cursus.");
                        vbox.getChildren().add(noDataLabel);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Voeg een foutmelding toe aan de GUI als er een SQL-fout optreedt
            Label errorLabel = new Label("Fout bij het ophalen van gegevens: " + e.getMessage());
            vbox.getChildren().add(errorLabel);
        }
    }

    private static String[] getCursusNamenFromDatabase() {
        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.getGebruikersnaam();
        String wachtwoord = databaseConnect.GetPass();

        try {
            Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord);
            String query = "SELECT CursusNaam FROM Cursus";
            try (PreparedStatement statement = connection.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery()) {

                // Voeg cursusnamen toe aan de keuzelijst
                List<String> cursusNamenList = new ArrayList<>();
                while (resultSet.next()) {
                    cursusNamenList.add(resultSet.getString("CursusNaam"));
                }

                return cursusNamenList.toArray(new String[0]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    private static String[] getCursistNamenFromDB() {
        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.getGebruikersnaam();
        String wachtwoord = databaseConnect.GetPass();

        try {
            Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord);
            String query = "SELECT Email FROM Cursist";
            try (PreparedStatement statement = connection.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery()) {

                // Voeg cursusnamen toe aan de keuzelijst
                List<String> cursistNamenList = new ArrayList<>();
                while (resultSet.next()) {
                    cursistNamenList.add(resultSet.getString("Email"));
                }

                return cursistNamenList.toArray(new String[0]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    private static int getCursusIDFromDatabase(String cursusNaam) {
        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.getGebruikersnaam();
        String wachtwoord = databaseConnect.GetPass();

        try (Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord)) {
            String query = "SELECT CursusID FROM Cursus WHERE CursusNaam = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, cursusNaam);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("CursusID");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Ongeldige cursus
    }
}
