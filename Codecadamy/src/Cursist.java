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
import javafx.scene.control.ListView;
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
    private String woonplaats;
    private String land;

    public Cursist(String naam, String email, String geslacht, Date cursistDatum, String adres, String woonplaats,
            String land) {
        this.naam = naam;
        this.email = email;
        this.geslacht = geslacht;
        this.geboortedatum = cursistDatum;
        this.adres = adres;
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

    public String getWoonplaats() {
        return woonplaats;
    }

    public String getLand() {
        return land;
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
        TableColumn<Cursist, String> naamColumn = new TableColumn<>("Naam");
        naamColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNaam()));

        TableColumn<Cursist, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<Cursist, String> geslachtColumn = new TableColumn<>("Geslacht");
        geslachtColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGeslacht()));

        TableColumn<Cursist, String> woonpTableColumn = new TableColumn<>("woonplaats");
        woonpTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getWoonplaats()));

        cursistenTableView.getColumns()
                .addAll(Arrays.asList(naamColumn, emailColumn, geslachtColumn, woonpTableColumn));

        // Populate data for the TableView
        cursistenTableView.refresh();
        cursisten.setAll(getCursistenFromDatabase());
        cursistenTableView.setItems(cursisten);

        vbox.getChildren().addAll(titleBox, cursistenTableView, addButton, deleteButton, updateButton);

        // Maak een Scene en toon het venster
        Scene scene = new Scene(vbox, 500, 500);
        cursistStage.setScene(scene);
        cursistStage.show();
    }

    private static List<Cursist> getCursistenFromDatabase() {
        List<Cursist> cursisten = new ArrayList<>();

        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.GetPass();
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
                String cursistWoonplaats = resultSet.getString("woonplaats");
                String cursistLand = resultSet.getString("Land");
                Cursist cursist = new Cursist(cursistNaam, cursistEmail, cursistGender, cursistDatum, cursistAdres,
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
        String gebruikersnaam = databaseConnect.GetPass();
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
            if (isInputValid(nameField.getText(), emailField.getText(), genderChoiceBox.getValue(), dobDate)) {
                // Als de invoer geldig is, voeg de cursist toe
                submitCursistForm(nameField.getText(), emailField.getText(),
                        genderChoiceBox.getValue(), dobDate,
                        addressField.getText(), cityField.getText(), countryField.getText());

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

    private static boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9_]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$");
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
        Label cityLabel = new Label("Woonplaats:");
        Label countryLabel = new Label("Land:");

        TextField nameField = new TextField();
        TextField emailField = new TextField();
        ChoiceBox<String> genderChoiceBox = new ChoiceBox<>();
        genderChoiceBox.getItems().addAll("Man", "Vrouw", "Anders");
        DatePicker dobPicker = new DatePicker();
        TextField addressField = new TextField();
        TextField cityField = new TextField();
        TextField countryField = new TextField();

        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> {
            updateCursist(selectedCursist, nameField.getText(), emailField.getText(),
                    genderChoiceBox.getValue(), dobPicker.getValue(),
                    addressField.getText(), cityField.getText(), countryField.getText());
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
        grid.add(cityLabel, 0, 5);
        grid.add(cityField, 1, 5);
        grid.add(countryLabel, 0, 6);
        grid.add(countryField, 1, 6);
        grid.add(updateButton, 1, 7, 2, 1);

        populateUpdateForm(selectedCursist, nameField, emailField, genderChoiceBox, dobPicker, addressField, cityField,
                countryField);

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(titleBox, grid, updateButton);
        Scene scene = new Scene(vbox, 300, 400);
        updateCursistStage.setScene(scene);
        updateCursistStage.show();
    }

    private static void populateUpdateForm(Cursist selectedCursist, TextField nameField, TextField emailField,
            ChoiceBox<String> genderChoiceBox, DatePicker dobPicker, TextField addressField,
            TextField cityField, TextField countryField) {

        nameField.setText(selectedCursist.getNaam());
        emailField.setText(selectedCursist.getEmail());
        genderChoiceBox.setValue(selectedCursist.getGeslacht());
        // Assuming Cursist has a method getGeboortedatum() that returns LocalDate
        dobPicker.setValue(selectedCursist.getGeboortedatum().toLocalDate());
        addressField.setText(selectedCursist.getAdres());
        cityField.setText(selectedCursist.getWoonplaats());
        countryField.setText(selectedCursist.getLand());
    }

    private static void openHomeVenster() {
        Stage homeStage = new Stage();
        homeStage.setTitle("Homepagina");

        // Maak een HomePage-object en roep de start-methode aan
        Gui homePage = new Gui();
        homePage.start(homeStage);
    }

    private static void updateCursist(Cursist selectedCursist, String name, String email, String gender,
            LocalDate localDate,
            String address, String city, String country) {

        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.GetPass();
        String wachtwoord = databaseConnect.GetPass();

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
            preparedStatement.setObject(4, localDate);// Assuming localDate is a LocalDate
            preparedStatement.setString(5, address);
            preparedStatement.setString(6, city);
            preparedStatement.setString(7, country);
            preparedStatement.setString(8, selectedCursist.getEmail());

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

        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.GetPass();
        String wachtwoord = databaseConnect.GetPass();

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
