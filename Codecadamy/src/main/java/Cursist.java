package main.java;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Cursist {
    private static TableView<Cursist> cursistenTableView = new TableView<>();
    private static ObservableList<Cursist> cursisten = FXCollections.observableArrayList();
    private String naam;
    private String email;
    private String geslacht;
    private Date geboortedatum;
    private String adres;
    private String postcode;
    private String woonplaats;
    private String land;

    public Cursist(String naam, String email, String geslacht, Date cursistDatum, String adres, String postcode,
            String woonplaats,
            String land) {
        this.naam = naam;
        this.email = email;
        this.geslacht = geslacht;
        this.geboortedatum = cursistDatum;
        this.adres = adres;
        this.postcode = postcode;
        this.woonplaats = woonplaats;
        this.land = land;
    }

    public String getNaam() {
        return naam;
    }

    public String getEmail() {
        return email;
    }

    public String getGeslacht() {
        return geslacht;
    }

    public Date getGeboortedatum() {
        return geboortedatum;
    }

    public String getAdres() {
        return adres;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getWoonplaats() {
        return woonplaats;
    }

    public String getLand() {
        return land;
    }

    public static void applyStylesheet(Scene scene) {
        String css = Inschrijving.class.getResource("style.css").toExternalForm();
        scene.getStylesheets().add(css);
    }

    public static void openCursistVenster() {
        cursistenTableView.getColumns().clear();
        if (cursistenTableView.getColumns().isEmpty()) {
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
            vbox.setPadding(new Insets(10));

            // Voeg knoppen toe aan de VBox
            Button addButton = new Button("Voeg Cursist Toe");
            addButton.setOnAction(e -> CreateCursist());

            Button deleteButton = new Button("Verwijder Cursist");
            deleteButton.setOnAction(e -> deleteSelectedCursist());

            Button updateButton = new Button("Update Cursist");
            updateButton.setOnAction(e -> updateSelectedCursist());

            // Voeg lijst met cursisten toe
            TableColumn<Cursist, String> naamColumn = new TableColumn<>("Naam");
            naamColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNaam()));
            naamColumn.setMinWidth(150);

            TableColumn<Cursist, String> emailColumn = new TableColumn<>("Email");
            emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
            emailColumn.setMinWidth(150);

            TableColumn<Cursist, String> geslachtColumn = new TableColumn<>("Geslacht");
            geslachtColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGeslacht()));
            geslachtColumn.setMinWidth(100);

            TableColumn<Cursist, String> woonpTableColumn = new TableColumn<>("Woonplaats");
            woonpTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getWoonplaats()));
            woonpTableColumn.setMinWidth(150);

            cursistenTableView.getColumns()
                    .addAll(Arrays.asList(naamColumn, emailColumn, geslachtColumn, woonpTableColumn));

            // Populate data for the TableView
            cursistenTableView.refresh();
            cursisten.setAll(getCursistenFromDatabase());
            cursistenTableView.setItems(cursisten);

            vbox.getChildren().addAll(titleBox, cursistenTableView, addButton, deleteButton, updateButton);

            // Maak een Scene en toon het venster
            Scene scene = new Scene(vbox, 600, 550);
            applyStylesheet(scene);
            cursistStage.setScene(scene);
            cursistStage.show();
        }
    }

    private static List<Cursist> getCursistenFromDatabase() {
        List<Cursist> cursisten = new ArrayList<>();

        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.getGebruikersnaam();
        String wachtwoord = databaseConnect.GetPass();

        // SQL-query om gegevens op te halen
        String query = "SELECT * FROM Cursist";
        cursisten.clear();
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
                String cursistNaam = resultSet.getString("naam");
                String cursistEmail = resultSet.getString("email");
                String cursistGender = resultSet.getString("Geslacht");
                Date cursistDatum = resultSet.getDate("Geboortedatum");
                String cursistAdres = resultSet.getString("adres");
                String cursistPostcode = resultSet.getString("postcode");
                String cursistWoonplaats = resultSet.getString("woonplaats");
                String cursistLand = resultSet.getString("Land");
                Cursist cursist = new Cursist(cursistNaam, cursistEmail, cursistGender, cursistDatum, cursistAdres,
                        cursistPostcode,
                        cursistWoonplaats, cursistLand);
                cursisten.add(cursist);

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
        // Haal de geselecteerde cursist op uit de TableView
        Cursist selectedCursist = cursistenTableView.getSelectionModel().getSelectedItem();

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

                // Werk de TableView opnieuw bij
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

    private static void deleteCursistFromDatabase(Cursist selectedCursist) {
        // JDBC URL, gebruikersnaam en wachtwoord van de SQL Server-database

        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.getGebruikersnaam();
        String wachtwoord = databaseConnect.GetPass();

        // SQL-query om de cursist te verwijderen
        String query = "DELETE FROM Cursist WHERE email = ?";

        try {
            // Maak verbinding met de database
            Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Bereid de SQL-query voor
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, selectedCursist.getEmail());

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
        createCursistStage.setTitle("Create Cursist");

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
        Label postcodeLabel = new Label("Postcode:");
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
        TextField postCodeField = new TextField();
        TextField cityField = new TextField();
        TextField countryField = new TextField();

        // Knop voor submit
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            // Converteer DatePicker-waarde naar java.sql.Date
            String formattedPostCode = formatPostcode(postCodeField.getText());
            Date dobDate = null;
            if (dobPicker.getValue() != null) {
                dobDate = Date.valueOf(dobPicker.getValue());
            }
            if (isInputValid(nameField.getText(), emailField.getText(), genderChoiceBox.getValue(), dobDate)) {
                // Als de invoer geldig is, voeg de cursist toe
                submitCursistForm(nameField.getText(), emailField.getText(),
                        genderChoiceBox.getValue(), dobDate,
                        addressField.getText(), formattedPostCode, cityField.getText(), countryField.getText());

                // Sluit het venster na het toevoegen van de cursist
                createCursistStage.close();

            }

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
        grid.add(postcodeLabel, 0, 5);
        grid.add(postCodeField, 1, 5);
        grid.add(cityLabel, 0, 6);
        grid.add(cityField, 1, 6);
        grid.add(countryLabel, 0, 7);
        grid.add(countryField, 1, 7);
        grid.add(submitButton, 0, 8, 2, 1); // De submitButton beslaat 2 kolommen

        // Maak een Scene en toon het venster
        Scene scene = new Scene(grid, 500, 400);
        applyStylesheet(scene);

        createCursistStage.setScene(scene);
        createCursistStage.show();
    }

    private static boolean isInputValid(String name, String email, String gender, Date dob) {
        if (name.isEmpty() || email.isEmpty() || gender == null) {
            showErrorDialog("Vul alle verplichte velden in.");
            return false;
        }

        // Voer extra validaties uit, bijvoorbeeld voor e-mailadres en geboortedatum

        // Controleer of het e-mailadres een geldig formaat heeft
        if (!isValidEmail(email)) {
            showErrorDialog("Voer een geldig e-mailadres in.");
            return false;
        }

        // Voeg andere specifieke validaties toe indien nodig

        // Als alles geldig is, retourneer true
        return true;
    }

    private static void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validatie Fout");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9_]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$");
    }

    public static String formatPostcode(String postcode) {
        // Verwijder alle non-alphanumeric characters uit de postcode
        String alphanumericString = postcode.replaceAll("[^a-zA-Z0-9]", "");

        // Controleer of het alfanumerieke deel de verwachte lengte heeft (4 cijfers + 2
        // letters)
        if (alphanumericString.length() == 6) {
            // Formatteer als 4 cijfers gevolgd door 2 letters
            String formattedPostalCode = alphanumericString.substring(0, 4) + " " + alphanumericString.substring(4);
            return formattedPostalCode;
        } else {
            // Geef een foutmelding terug als de lengte niet overeenkomt
            throw new IllegalArgumentException("Ongeldige postcode: " + postcode);
        }
    }

    private static void updateSelectedCursist() {
        Cursist selectedCursist = cursistenTableView.getSelectionModel().getSelectedItem();

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

    private static void openUpdateCursistVenster(Cursist selectedCursist) {
        Stage updateCursistStage = new Stage();
        updateCursistStage.setTitle("Cursist bijwerken");

        Text title = new Text("Cursist bijwerken");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

        HBox titleBox = new HBox();
        titleBox.getChildren().add(title);
        titleBox.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        titleBox.setPadding(new Insets(10, 10, 10, 10));

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(javafx.geometry.Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);

        Label nameLabel = new Label("Naam:");
        Label emailLabel = new Label("Email:");
        Label genderLabel = new Label("Geslacht:");
        Label dobLabel = new Label("Geboortedatum:");
        Label addressLabel = new Label("Adres:");
        Label postcodeLabel = new Label("Postcode");
        Label cityLabel = new Label("Woonplaats:");
        Label countryLabel = new Label("Land:");

        TextField nameField = new TextField();
        TextField emailField = new TextField();
        ChoiceBox<String> genderChoiceBox = new ChoiceBox<>();
        genderChoiceBox.getItems().addAll("Man", "Vrouw", "Anders");
        DatePicker dobPicker = new DatePicker();
        TextField addressField = new TextField();
        TextField postCodeField = new TextField();
        TextField cityField = new TextField();
        TextField countryField = new TextField();

        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> {
            String formattedPostcode = formatPostcode(postCodeField.getText());
            updateCursist(selectedCursist, nameField.getText(), emailField.getText(),
                    genderChoiceBox.getValue(), dobPicker.getValue(),
                    addressField.getText(), formattedPostcode, cityField.getText(), countryField.getText());
            updateCursistStage.close();
            // Update the TableView
            cursisten.setAll(getCursistenFromDatabase());
        });

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
        grid.add(postcodeLabel, 0, 5);
        grid.add(postCodeField, 1, 5);
        grid.add(cityLabel, 0, 6);
        grid.add(cityField, 1, 6);
        grid.add(countryLabel, 0, 7);
        grid.add(countryField, 1, 7);
        grid.add(updateButton, 1, 8, 2, 1);

        populateUpdateForm(selectedCursist, nameField, emailField, genderChoiceBox, dobPicker, addressField,
                postCodeField, cityField,
                countryField);

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(titleBox, grid, updateButton);
        Scene scene = new Scene(vbox, 300, 400);
        applyStylesheet(scene);
        updateCursistStage.setScene(scene);
        updateCursistStage.show();
    }

    private static void populateUpdateForm(Cursist selectedCursist, TextField nameField, TextField emailField,
            ChoiceBox<String> genderChoiceBox, DatePicker dobPicker, TextField addressField, TextField postcode,
            TextField cityField, TextField countryField) {

        nameField.setText(selectedCursist.getNaam());
        emailField.setText(selectedCursist.getEmail());
        genderChoiceBox.setValue(selectedCursist.getGeslacht());
        // Assuming Cursist has a method getGeboortedatum() that returns LocalDate
        dobPicker.setValue(selectedCursist.getGeboortedatum().toLocalDate());
        addressField.setText(selectedCursist.getAdres());
        postcode.setText(selectedCursist.getPostcode());
        cityField.setText(selectedCursist.getWoonplaats());
        countryField.setText(selectedCursist.getLand());
    }

    private static void updateCursist(Cursist selectedCursist, String name, String email, String gender,
            LocalDate localDate,
            String address, String postcode, String city, String country) {

        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.getGebruikersnaam();
        String wachtwoord = databaseConnect.GetPass();

        // SQL-query om gegevens bij te werken
        String query = "UPDATE Cursist SET Naam = ?, Email = ?, Geslacht = ?, Geboortedatum = ?, Adres = ?, postcode = ?, Woonplaats = ?, Land = ? WHERE email = ?";

        try {
            // Maak verbinding met de database
            Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Bereid de SQL-query voor met placeholders
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, gender);
            preparedStatement.setObject(4, localDate);// Assuming localDate is a LocalDate
            preparedStatement.setString(5, address);
            preparedStatement.setString(6, postcode);
            preparedStatement.setString(7, city);
            preparedStatement.setString(8, country);
            preparedStatement.setString(9, selectedCursist.getEmail());

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
            String address, String postcode, String city, String country) {

        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.getGebruikersnaam();
        String wachtwoord = databaseConnect.GetPass();

        // SQL-query om gegevens in te voegen
        String query = "INSERT INTO Cursist (Naam, Email, Geslacht, Geboortedatum, Adres, Postcode, Woonplaats, Land) "
                +
                "VALUES (?, ?, ?, ?, ?, ? , ?, ?)";

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
            preparedStatement.setString(6, postcode);
            preparedStatement.setString(7, city);
            preparedStatement.setString(8, country);

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
