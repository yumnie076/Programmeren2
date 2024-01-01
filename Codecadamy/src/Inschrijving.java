import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

public class Inschrijving {

    private static ComboBox<String> cursistComboBox = new ComboBox<>();
    private static ComboBox<Integer> cursusComboBox = new ComboBox<>();

    public static void openInschrijvingenVenster() {
        Stage overzichtStage = new Stage();
        overzichtStage.setTitle("Inschrijvingen Overzicht");

        // Tabel voor inschrijvingen
        TableView<InschrijvingDetail> table = new TableView<>();
        ObservableList<InschrijvingDetail> data = haalInschrijvingenOp();

        // Kolommen voor tabel
        TableColumn<InschrijvingDetail, String> cursistCol = new TableColumn<>("Cursist Email");
        cursistCol.setCellValueFactory(new PropertyValueFactory<>("cursistEmail"));
        TableColumn<InschrijvingDetail, String> cursusCol = new TableColumn<>("Cursus ID");
        cursusCol.setCellValueFactory(new PropertyValueFactory<>("cursusId"));
        TableColumn<InschrijvingDetail, String> datumCol = new TableColumn<>("Inschrijf Datum");
        datumCol.setCellValueFactory(new PropertyValueFactory<>("inschrijfDatum"));

        table.getColumns()
                .addAll(Arrays.asList(cursistCol, cursusCol, datumCol));
        table.setItems(data);

        Button addButton = new Button("Maak inschrijving aan");
        addButton.setOnAction(e -> openInschrijvingsFormulier());

        Button verwijderKnop = new Button("Verwijder Inschrijving");
        verwijderKnop.setOnAction(e -> verwijderGeselecteerdeInschrijving(table));

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(table, addButton, verwijderKnop);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 500, 500);
        overzichtStage.setScene(scene);
        overzichtStage.show();
    }

    public static void openInschrijvingsFormulier() {
        Stage inschrijvingsStage = new Stage();
        inschrijvingsStage.setTitle("Inschrijving");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(javafx.geometry.Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);

        Label cursistLabel = new Label("Cursist:");
        cursistComboBox.setPromptText("Selecteer een cursist");

        Label cursusLabel = new Label("Cursus:");
        cursusComboBox.setPromptText("Selecteer een cursus");

        Label datumLabel = new Label("Datum:");
        DatePicker datePicker = new DatePicker();

        Button inschrijfButton = new Button("Inschrijven");
        inschrijfButton.setOnAction(e -> {
            String geselecteerdeCursistEmail = cursistComboBox.getValue();
            Integer geselecteerdeCursusId = cursusComboBox.getValue();
            LocalDate inschrijfDatum = datePicker.getValue();
            if (geselecteerdeCursistEmail != null && geselecteerdeCursusId != null && inschrijfDatum != null) {
                inschrijvenCursist(geselecteerdeCursistEmail, geselecteerdeCursusId, inschrijfDatum);

                inschrijvingsStage.close();
            }
        });

        grid.add(cursistLabel, 0, 0);
        grid.add(cursistComboBox, 1, 0);
        grid.add(cursusLabel, 0, 1);
        grid.add(cursusComboBox, 1, 1);
        grid.add(datumLabel, 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(inschrijfButton, 1, 3);

        vulCursistenComboBox();
        vulCursusComboBox();

        Scene scene = new Scene(grid, 300, 275);
        inschrijvingsStage.setScene(scene);
        inschrijvingsStage.show();
    }

    private static void vulCursistenComboBox() {
        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.getGebruikersnaam();
        String wachtwoord = databaseConnect.GetPass();

        String query = "SELECT email FROM Cursist";

        try (Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            ObservableList<String> cursisten = FXCollections.observableArrayList();
            while (resultSet.next()) {
                cursisten.add(resultSet.getString("email"));
            }
            cursistComboBox.setItems(cursisten);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void vulCursusComboBox() {
        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.getGebruikersnaam();
        String wachtwoord = databaseConnect.GetPass();

        String query = "SELECT CursusID FROM Cursus";

        try (Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            ObservableList<Integer> cursussen = FXCollections.observableArrayList();
            while (resultSet.next()) {
                cursussen.add(resultSet.getInt("CursusID"));
            }
            cursusComboBox.setItems(cursussen);
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    public static void inschrijvenCursist(String cursistEmail, int cursusId, LocalDate inschrijfDatum) {
        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.getGebruikersnaam();
        String wachtwoord = databaseConnect.GetPass();

        try (Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord)) {

            String query = "INSERT INTO Inschrijving (CursistEmail, CursusID, InschrijfDatum) VALUES (?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) { // De nieuwe InschrijvingId
                preparedStatement.setString(1, cursistEmail); // E-mailadres van de cursist
                preparedStatement.setInt(2, cursusId); // ID van de cursus
                preparedStatement.setDate(3, java.sql.Date.valueOf(inschrijfDatum)); // De inschrijfdatum

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Cursist is succesvol ingeschreven met e-mail: " + cursistEmail);
                } else {
                    System.out.println("Er is een probleem opgetreden bij het inschrijven van de cursist.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Databasefout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Een klasse om de details van een inschrijving te houden
    public static class InschrijvingDetail {
        private final SimpleStringProperty cursistEmail;
        private final SimpleIntegerProperty cursusId;
        private final SimpleObjectProperty<LocalDate> inschrijfDatum;

        public InschrijvingDetail(String cursistEmail, int cursusId, LocalDate inschrijfDatum) {
            this.cursistEmail = new SimpleStringProperty(cursistEmail);
            this.cursusId = new SimpleIntegerProperty(cursusId);
            this.inschrijfDatum = new SimpleObjectProperty<>(inschrijfDatum);
        }

        // Getters

        public String getCursistEmail() {
            return cursistEmail.get();
        }

        public int getCursusId() {
            return cursusId.get();
        }

        public LocalDate getInschrijfDatum() {
            return inschrijfDatum.get();
        }
    }

    private static ObservableList<InschrijvingDetail> haalInschrijvingenOp() {
        ObservableList<InschrijvingDetail> inschrijvingen = FXCollections.observableArrayList();

        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.getGebruikersnaam();
        String wachtwoord = databaseConnect.GetPass();

        String query = "SELECT InschrijvingId, CursistEmail, CursusID, InschrijfDatum FROM Inschrijving";

        try (Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String cursistId = resultSet.getString("CursistEmail");
                int cursusId = resultSet.getInt("CursusID");
                LocalDate inschrijfDatum = resultSet.getDate("InschrijfDatum").toLocalDate();

                // Aannemende dat de InschrijvingDetail constructor alle benodigde velden als
                // parameters neemt.
                InschrijvingDetail inschrijving = new InschrijvingDetail(cursistId, cursusId, inschrijfDatum);
                inschrijvingen.add(inschrijving);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }

        return inschrijvingen;
    }

    private static void verwijderGeselecteerdeInschrijving(TableView<InschrijvingDetail> table) {
        InschrijvingDetail geselecteerd = table.getSelectionModel().getSelectedItem();
        if (geselecteerd != null) {

            table.getItems().remove(geselecteerd);
        }
    }

}