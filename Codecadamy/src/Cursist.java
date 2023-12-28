import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Cursist {
    private static ListView<String> cursistenListView = new ListView<>();
    private static ObservableList<String> cursisten = FXCollections.observableArrayList();
    private String naam;
    private String email;
    private String geslacht;

    public Cursist(String naam, String email, String geslacht) {
        this.naam = naam;
        this.email = email;
        this.geslacht = geslacht;
    }

    @Override
    public String toString() {
        return naam + " (" + email + ") - Geslacht: " + geslacht;
    }

    public static void openCursistVenster() {
        Stage cursistStage = new Stage();
        cursistStage.setTitle("Cursistpagina");

        // Titel bovenaan de pagina
        Text title = new Text("Cursisten Overzicht");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

        // Maak een HBox voor het opmaken van de titel
        HBox titleBox = new HBox();
        titleBox.getChildren().add(title);
        titleBox.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        titleBox.setPadding(new Insets(10, 10, 10, 10));

        // Maak een VBox voor het opmaken van de knoppen en de lijst met cursisten
        VBox vbox = new VBox(10); // 10 is de verticale ruimte tussen knoppen en lijst
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setAlignment(javafx.geometry.Pos.CENTER); // Knoppen en lijst in het midden van de pagina

        // Voeg knoppen toe aan de VBox
        Button addButton = new Button("Voeg Cursist Toe");
        addButton.setOnAction(e -> CreateCursist());

        Button deleteButton = new Button("Verwijder Geselecteerde Cursist");
        deleteButton.setOnAction(e -> deleteSelectedCursist());

        Button updateButton = new Button("Update Geselecteerde Cursist");
        updateButton.setOnAction(e -> updateSelectedCursist());

        // Voeg lijst met cursisten toe
        cursisten = FXCollections.observableArrayList(getCursistenFromDatabase());
        cursistenListView.setItems(cursisten);
        vbox.getChildren().addAll(titleBox, cursistenListView, addButton, deleteButton, updateButton);

        // Maak een Scene en toon het venster
        Scene scene = new Scene(vbox, 500, 500);
        cursistStage.setScene(scene);
        cursistStage.show();
    }

    private static List<String> getCursistenFromDatabase() {
        List<String> cursisten = new ArrayList<>();

        // JDBC URL, gebruikersnaam en wachtwoord van de SQL Server-database
        String url = "jdbc:sqlserver://localhost;databaseName=CodeAcadamyGroup;encrypt=true;trustServerCertificate=true;";
        String gebruikersnaam = "salim";
        String wachtwoord = "salim";

        // SQL-query om gegevens op te halen
        String query = "SELECT naam,Email FROM Cursist";

        try {
            // Maak verbinding met de database
            Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Bereid de SQL-query voor
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            // Voer de query uit en ontvang het resultaat
            ResultSet resultSet = preparedStatement.executeQuery();

            // Verwerk het resultaat en voeg het toe aan de lijst
            while (resultSet.next()) {
                String cursistNaam = resultSet.getString("email");
                cursisten.add(cursistNaam);
            }

            // Sluit de verbinding
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cursisten;
    }

    private static void deleteSelectedCursist() {
        // Haal de geselecteerde cursist op uit de lijst
        String selectedCursist = cursistenListView.getSelectionModel().getSelectedItem();

        if (selectedCursist != null) {
            // Bevestig of de gebruiker de cursist echt wil verwijderen
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Bevestig Verwijdering");
            alert.setHeaderText("Weet je zeker dat je de cursist wilt verwijderen?");
            alert.setContentText("Geselecteerde cursist: " + selectedCursist);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Voer de delete-operatie uit
                deleteCursistFromDatabase(selectedCursist);

                // Werk de lijst met cursisten opnieuw bij
                cursisten.setAll(getCursistenFromDatabase());
            }
        } else {
            // Geen cursist geselecteerd, toon een waarschuwing
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Geen Cursist Geselecteerd");
            alert.setHeaderText(null);
            alert.setContentText("Selecteer eerst een cursist om te verwijderen.");
            alert.showAndWait();
        }
    }

    private static void deleteCursistFromDatabase(String cursistEmail) {
        // JDBC URL, gebruikersnaam en wachtwoord van de SQL Server-database
        String url = "jdbc:sqlserver://localhost;databaseName=CodeAcadamyGroup;encrypt=true;trustServerCertificate=true;";
        String gebruikersnaam = "salim";
        String wachtwoord = "salim";

        // SQL-query om de cursist te verwijderen
        String query = "DELETE FROM Cursist WHERE email = ?";

        try {
            // Maak verbinding met de database
            Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Bereid de SQL-query voor
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cursistEmail);

            // Voer de update uit
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cursist succesvol verwijderd uit de database.");
            } else {
                System.out.println("Verwijderen van cursist uit de database mislukt.");
            }

            // Sluit de verbinding
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void CreateCursist() {
        Stage createCursistStage = new Stage();
        createCursistStage.setTitle("Cursistpagina");

        // Maak een GridPane voor het opmaken van het formulier
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(javafx.geometry.Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10); // Voeg horizontale ruimte tussen elementen toe

        // Labels
        Label nameLabel = new Label("Naam:");
        Label emailLabel = new Label("Email:");
        Label genderLabel = new Label("Geslacht:");
        Label dobLabel = new Label("Geboortedatum:");
        Label addressLabel = new Label("Adres:");
        Label cityLabel = new Label("Woonplaats:");
        Label countryLabel = new Label("Land:");

        // Invoervelden
        TextField nameField = new TextField();
        TextField emailField = new TextField();

        // ChoiceBox voor geslacht
        ChoiceBox<String> genderChoiceBox = new ChoiceBox<>();
        genderChoiceBox.getItems().addAll("Man", "Vrouw", "Anders");

        // Datumveld voor geboortedatum
        DatePicker dobPicker = new DatePicker();

        TextField addressField = new TextField();
        TextField cityField = new TextField();
        TextField countryField = new TextField();

        // Knop voor submit
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            // Converteer DatePicker-waarde naar java.sql.Date
            Date dobDate = null;
            if (dobPicker.getValue() != null) {
                dobDate = Date.valueOf(dobPicker.getValue());
            }

            submitCursistForm(nameField.getText(), emailField.getText(),
                    genderChoiceBox.getValue(), dobDate,
                    addressField.getText(), cityField.getText(), countryField.getText());
            createCursistStage.close();
            openHomeVenster();
        });

        // Voeg labels, invoervelden, ChoiceBox, DatePicker en knop toe aan de GridPane
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(genderLabel, 0, 2);
        grid.add(genderChoiceBox, 1, 2);
        grid.add(dobLabel, 0, 3);
        grid.add(dobPicker, 1, 3);
        grid.add(addressLabel, 0, 4);
        grid.add(addressField, 1, 4);
        grid.add(cityLabel, 0, 5);
        grid.add(cityField, 1, 5);
        grid.add(countryLabel, 0, 6);
        grid.add(countryField, 1, 6);
        grid.add(submitButton, 0, 7, 2, 1); // De submitButton beslaat 2 kolommen

        // Maak een Scene en toon het venster
        Scene scene = new Scene(grid, 400, 300);
        createCursistStage.setScene(scene);
        createCursistStage.show();
    }

    private static void updateSelectedCursist() {
        String selectedCursist = cursistenListView.getSelectionModel().getSelectedItem();

        if (selectedCursist != null) {
            openUpdateCursistVenster(selectedCursist);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Geen Cursist Geselecteerd");
            alert.setHeaderText(null);
            alert.setContentText("Selecteer eerst een cursist om te updaten.");
            alert.showAndWait();
        }
    }

    private static void openUpdateCursistVenster(String selectedCursist) {
        Stage updateCursistStage = new Stage();
        updateCursistStage.setTitle("Cursist bijwerken");

        // Titel bovenaan de pagina
        Text title = new Text("Cursist bijwerken");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

        // Maak een HBox voor het opmaken van de titel
        HBox titleBox = new HBox();
        titleBox.getChildren().add(title);
        titleBox.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        titleBox.setPadding(new Insets(10, 10, 10, 10));

        // Maak een GridPane voor het opmaken van het formulier
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(javafx.geometry.Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10); // Voeg horizontale ruimte tussen elementen toe

        // Labels
        Label nameLabel = new Label("Naam:");
        Label emailLabel = new Label("Email:");
        Label genderLabel = new Label("Geslacht:");
        Label dobLabel = new Label("Geboortedatum:");
        Label addressLabel = new Label("Adres:");
        Label cityLabel = new Label("Woonplaats:");
        Label countryLabel = new Label("Land:");

        // Invoervelden
        TextField nameField = new TextField();
        TextField emailField = new TextField();
        ChoiceBox<String> genderChoiceBox = new ChoiceBox<>();
        genderChoiceBox.getItems().addAll("Man", "Vrouw", "Anders");
        TextField dobField = new TextField();
        TextField addressField = new TextField();
        TextField cityField = new TextField();
        TextField countryField = new TextField();

        // Knop voor update
        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> {
            updateCursist(selectedCursist, nameField.getText(), emailField.getText(),
                    genderChoiceBox.getValue(), dobField.getText(),
                    addressField.getText(), cityField.getText(), countryField.getText());
            updateCursistStage.close();
            openCursistVenster();
        });

        // Voeg labels, invoervelden, ChoiceBox en knop toe aan de GridPane
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(genderLabel, 0, 2);
        grid.add(genderChoiceBox, 1, 2);
        grid.add(dobLabel, 0, 3);
        grid.add(dobField, 1, 3);
        grid.add(addressLabel, 0, 4);
        grid.add(addressField, 1, 4);
        grid.add(cityLabel, 0, 5);
        grid.add(cityField, 1, 5);
        grid.add(countryLabel, 0, 6);
        grid.add(countryField, 1, 6);
        grid.add(updateButton, 1, 7, 2, 1); // De updateButton beslaat 2 kolommen

        // Haal de gegevens van de geselecteerde cursist op en vul de velden in
        populateUpdateForm(selectedCursist, nameField, emailField, genderChoiceBox, dobField, addressField, cityField,
                countryField);

        // Maak een Scene en toon het venster
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(titleBox, grid, updateButton);
        Scene scene = new Scene(vbox, 300, 400);
        updateCursistStage.setScene(scene);
        updateCursistStage.show();
    }

    private static void populateUpdateForm(String selectedCursist, TextField nameField, TextField emailField,
            ChoiceBox<String> genderChoiceBox, TextField dobField, TextField addressField,
            TextField cityField, TextField countryField) {
        // JDBC URL, gebruikersnaam en wachtwoord van de SQL Server-database
        String url = "jdbc:sqlserver://localhost;databaseName=CodeAcadamyGroup;encrypt=true;trustServerCertificate=true;";
        String gebruikersnaam = "salim";
        String wachtwoord = "salim";

        // SQL-query om gegevens op te halen voor de geselecteerde cursist
        String query = "SELECT * FROM Cursist WHERE email = ?";

        try {
            // Maak verbinding met de database
            Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Bereid de SQL-query voor
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, selectedCursist);

            // Voer de query uit en ontvang het resultaat
            ResultSet resultSet = preparedStatement.executeQuery();

            // Vul de velden in met de gegevens van de geselecteerde cursist
            if (resultSet.next()) {
                nameField.setText(resultSet.getString("Naam"));
                emailField.setText(resultSet.getString("Email"));
                genderChoiceBox.setValue(resultSet.getString("Geslacht"));
                dobField.setText(resultSet.getString("Geboortedatum"));
                addressField.setText(resultSet.getString("Adres"));
                cityField.setText(resultSet.getString("Woonplaats"));
                countryField.setText(resultSet.getString("Land"));
            }

            // Sluit de verbinding
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void openHomeVenster() {
        Stage homeStage = new Stage();
        homeStage.setTitle("Homepagina");

        // Maak een HomePage-object en roep de start-methode aan
        Gui homePage = new Gui();
        homePage.start(homeStage);
    }

    private static void updateCursist(String selectedCursist, String name, String email, String gender, String dob,
            String address, String city, String country) {
        // JDBC URL, gebruikersnaam en wachtwoord van de SQL Server-database
        String url = "jdbc:sqlserver://localhost;databaseName=CodeAcadamyGroup;encrypt=true;trustServerCertificate=true;";
        String gebruikersnaam = "salim";
        String wachtwoord = "salim";

        // SQL-query om gegevens bij te werken
        String query = "UPDATE Cursist SET Naam = ?, Email = ?, Geslacht = ?, Geboortedatum = ?, Adres = ?, Woonplaats = ?, Land = ? WHERE email = ?";

        try {
            // Maak verbinding met de database
            Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Bereid de SQL-query voor met placeholders
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, gender);
            preparedStatement.setString(4, dob);
            preparedStatement.setString(5, address);
            preparedStatement.setString(6, city);
            preparedStatement.setString(7, country);
            preparedStatement.setString(8, selectedCursist);

            // Voer de update uit
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cursist succesvol bijgewerkt in de database.");
            } else {
                System.out.println("Bijwerken van cursist in de database mislukt.");
            }

            // Sluit de verbinding
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void submitCursistForm(String name, String email, String gender, Date dob,
            String address, String city, String country) {
        // JDBC URL, gebruikersnaam en wachtwoord van de SQL Server-database
        String url = "jdbc:sqlserver://localhost;databaseName=CodeAcadamyGroup; encrypt=true;trustServerCertificate=true;";
        String gebruikersnaam = "salim";
        String wachtwoord = "salim";

        // SQL-query om gegevens in te voegen
        String query = "INSERT INTO Cursist (Naam, Email, Geslacht, Geboortedatum, Adres, Woonplaats, Land) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            // Maak verbinding met de database
            Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // Bereid de SQL-query voor met placeholders
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, gender);
            preparedStatement.setDate(4, dob);
            preparedStatement.setString(5, address);
            preparedStatement.setString(6, city);
            preparedStatement.setString(7, country);

            // Voer de update uit
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Gegevens succesvol toegevoegd aan de database.");
            } else {
                System.out.println("Toevoegen van gegevens aan de database mislukt.");
            }

            // Sluit de verbinding
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
