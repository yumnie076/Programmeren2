import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Gui extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Homepagina");

        // Titel bovenaan de pagina
        Text title = new Text("Welkom op de Homepagina");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

        // Maak een HBox voor het opmaken van de titel
        HBox titleBox = new HBox();
        titleBox.getChildren().add(title);
        titleBox.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        titleBox.setPadding(new Insets(10, 10, 10, 10));

        // Maak een HBox voor het opmaken van de knoppen
        HBox buttonBox = new HBox(10); // 10 is de horizontale ruimte tussen knoppen
        buttonBox.setPadding(new Insets(10, 10, 10, 10));
        buttonBox.setAlignment(javafx.geometry.Pos.BOTTOM_CENTER); // Knoppen aan de onderkant en gecentreerd

        // Voeg knoppen toe aan de HBox
        Button buttonCursist = new Button("Cursist");

        // Voeg knopfunctionaliteit toe
        buttonCursist.setOnAction(e -> openCursistVenster());

        buttonBox.getChildren().addAll(buttonCursist);

        // Maak een VBox voor het opmaken van de gehele pagina
        VBox vbox = new VBox(10); // 10 is de verticale ruimte tussen titel en knoppen
        vbox.getChildren().addAll(titleBox, buttonBox);

        // Maak een Scene en toon het venster
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openCursusVenster() {
        Stage cursusStage = new Stage();
        cursusStage.setTitle("Cursuspagina");

        // Titel bovenaan de pagina
        Text title = new Text("Welkom op de Cursus pagina");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

        // Maak een HBox voor het opmaken van de titel
        HBox titleBox = new HBox();
        titleBox.getChildren().add(title);
        titleBox.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        titleBox.setPadding(new Insets(10, 10, 10, 10));

        // Maak een VBox voor het opmaken van de knoppen
        VBox vbox = new VBox(10); // 10 is de verticale ruimte tussen knoppen
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setAlignment(javafx.geometry.Pos.CENTER); // Knoppen in het midden van de pagina

        // Voeg knoppen toe aan de VBox
        Button backButton = new Button("Terug naar Homepagina");

        // Voeg knopfunctionaliteit toe
        backButton.setOnAction(e -> {
            cursusStage.close();
            openHomeVenster();
        });

        vbox.getChildren().addAll(titleBox, backButton);

        // Maak een Scene en toon het venster
        Scene scene = new Scene(vbox, 300, 200);
        cursusStage.setScene(scene);
        cursusStage.show();
    }

    private void openCursistVenster() {
        Stage Cursist = new Stage();
        Cursist.setTitle("Cursistpagina");

        // Titel bovenaan de pagina
        Text title = new Text("Welkom op de Cursist pagina");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

        // Maak een HBox voor het opmaken van de titel
        HBox titleBox = new HBox();
        titleBox.getChildren().add(title);
        titleBox.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        titleBox.setPadding(new Insets(10, 10, 10, 10));

        // Maak een HBox voor het opmaken van de knoppen
        HBox buttonBox = new HBox(10); // 10 is de horizontale ruimte tussen knoppen
        buttonBox.setPadding(new Insets(10, 10, 10, 10));
        buttonBox.setAlignment(javafx.geometry.Pos.BOTTOM_CENTER); // Knoppen aan de onderkant en gecentreerd

        // Voeg knoppen toe aan de HBox
        Button buttonCursus = new Button("Maak cursist aan");

        // Voeg knopfunctionaliteit toe
        buttonCursus.setOnAction(e -> CreateCursist());

        buttonBox.getChildren().addAll(buttonCursus);

        // Maak een VBox voor het opmaken van de gehele pagina
        VBox vbox = new VBox(10); // 10 is de verticale ruimte tussen titel en knoppen
        vbox.getChildren().addAll(titleBox, buttonBox);

        // Maak een Scene en toon het venster
        Scene scene = new Scene(vbox, 300, 200);
        Cursist.setScene(scene);
        Cursist.show();
    }

    private void CreateCursist() {
        Stage CreateCursist = new Stage();
        CreateCursist.setTitle("Cursistpagina");

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

        TextField dobField = new TextField();
        TextField addressField = new TextField();
        TextField cityField = new TextField();
        TextField countryField = new TextField();

        // Knop voor submit
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            submitCursistForm(nameField.getText(), emailField.getText(),
                    genderChoiceBox.getValue(), dobField.getText(),
                    addressField.getText(), cityField.getText(), countryField.getText());
            CreateCursist.close();
            openHomeVenster();
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
        grid.add(submitButton, 0, 7, 2, 1); // De submitButton beslaat 2 kolommen

        // Maak een Scene en toon het venster
        Scene scene = new Scene(grid, 400, 300);
        CreateCursist.setScene(scene);
        CreateCursist.show();
    }

    private void openHomeVenster() {
        Stage homeStage = new Stage();
        homeStage.setTitle("Homepagina");

        // Maak een HomePage-object en roep de start-methode aan
        Gui homePage = new Gui();
        homePage.start(homeStage);
    }

    private void submitCursistForm(String name, String email, String gender, String dob,
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
            preparedStatement.setString(4, dob);
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