import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
    private static ComboBox<String> cursusComboBox = new ComboBox<>();
    private static Map<String, Integer> cursusIdMap = new HashMap<>();
    private static TableView<InschrijvingDetail> inschrijvingenTable;
    private static ObservableList<InschrijvingDetail> data = FXCollections.observableArrayList();

    public static void openInschrijvingenVenster() {
        Stage overzichtStage = new Stage();
        overzichtStage.setTitle("Inschrijvingen Overzicht");

        // Tabel voor inschrijvingen
        inschrijvingenTable = new TableView<>();

        // Kolommen voor tabel
        TableColumn<InschrijvingDetail, String> cursistCol = new TableColumn<>("Cursist Email");
        cursistCol.setCellValueFactory(new PropertyValueFactory<>("cursistEmail"));
        TableColumn<InschrijvingDetail, String> cursusCol = new TableColumn<>("Cursus ID");
        cursusCol.setCellValueFactory(new PropertyValueFactory<>("cursusId"));
        TableColumn<InschrijvingDetail, String> datumCol = new TableColumn<>("Inschrijf Datum");
        datumCol.setCellValueFactory(new PropertyValueFactory<>("inschrijfDatum"));

        inschrijvingenTable.getColumns()
                .addAll(Arrays.asList(cursistCol, cursusCol, datumCol));
        inschrijvingenTable.refresh();
        data.setAll(haalInschrijvingenOp());
        inschrijvingenTable.setItems(data);

        Button addButton = new Button("Maak inschrijving aan");
        addButton.setOnAction(e -> openInschrijvingsFormulier());

        Button verwijderKnop = new Button("Verwijder Inschrijving");
        verwijderKnop.setOnAction(e -> verwijderGeselecteerdeInschrijving(inschrijvingenTable));
         // Add the update button to your VBox layout
        Button updateButton = new Button("Update Inschrijving");
        updateButton.setOnAction(e -> {
            InschrijvingDetail selected = inschrijvingenTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                openInschrijvingsUpdateForm(selected);
            } });
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(inschrijvingenTable, addButton, verwijderKnop,updateButton);
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
            String geselecteerdeCursusId = cursusComboBox.getValue();
            LocalDate inschrijfDatum = datePicker.getValue();
            if (geselecteerdeCursistEmail != null && geselecteerdeCursusId != null && inschrijfDatum != null) {
                inschrijvenCursist(geselecteerdeCursistEmail, inschrijfDatum);

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

        String query = "SELECT CursusID, CursusNaam FROM Cursus";

        try (Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            ObservableList<String> cursussen = FXCollections.observableArrayList();
            while (resultSet.next()) {
                int cursusId = resultSet.getInt("CursusID");
                String cursusNaam = resultSet.getString("CursusNaam");
                cursussen.add(cursusNaam);

                // Map cursusNaam to cursusId
                cursusIdMap.put(cursusNaam, cursusId);
            }
            cursusComboBox.setItems(cursussen);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void inschrijvenCursist(String cursistEmail, LocalDate inschrijfDatum) {
        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.getGebruikersnaam();
        String wachtwoord = databaseConnect.GetPass();

        try (Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord)) {

            // Haal het cursusId op van de geselecteerde cursusNaam in de ComboBox
            int geselecteerdeCursusId = cursusIdMap.get(cursusComboBox.getValue());

            String query = "INSERT INTO Inschrijving (CursistEmail, CursusID, InschrijfDatum) VALUES (?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, cursistEmail);
                preparedStatement.setInt(2, geselecteerdeCursusId);
                preparedStatement.setDate(3, java.sql.Date.valueOf(inschrijfDatum));

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

                // InschrijvingDetail constructor 
                
                InschrijvingDetail inschrijving = new InschrijvingDetail(cursistId, cursusId, inschrijfDatum);
                inschrijvingen.add(inschrijving);
                inschrijvingenTable.refresh();
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

   

// Method to open the update form
public static void openInschrijvingsUpdateForm(InschrijvingDetail detail) {
    Stage updateStage = new Stage();
    updateStage.setTitle("Update Inschrijving");

    GridPane grid = new GridPane();
    grid.setPadding(new Insets(10, 10, 10, 10));
    grid.setAlignment(Pos.CENTER);
    grid.setVgap(10);
    grid.setHgap(10);

    Label cursistLabel = new Label("Cursist:");
    cursistComboBox.setPromptText("Selecteer een cursist");
    cursistComboBox.setValue(detail.getCursistEmail()); // Pre-fill 

    Label cursusLabel = new Label("Cursus:");
    cursusComboBox.setPromptText("Selecteer een cursus");
    cursusComboBox.setValue(getCursusNaamFromId(detail.getCursusId())); // Pre-fill 

    Label datumLabel = new Label("Datum:");
    DatePicker datePicker = new DatePicker();
    datePicker.setValue(detail.getInschrijfDatum()); // Pre-fill 

    Button updateButton = new Button("Update");
    updateButton.setOnAction(e -> {
        String selectedCursistEmail = cursistComboBox.getValue();
        String selectedCursusNaam = cursusComboBox.getValue();
        LocalDate inschrijfDatum = datePicker.getValue();
        if (selectedCursistEmail != null && selectedCursusNaam != null && inschrijfDatum != null) {
            int selectedCursusId = cursusIdMap.get(selectedCursusNaam);
            updateInschrijving(selectedCursistEmail, selectedCursusId, inschrijfDatum);
            updateStage.close();
        }
    });

    grid.add(cursistLabel, 0, 0);
    grid.add(cursistComboBox, 1, 0);
    grid.add(cursusLabel, 0, 1);
    grid.add(cursusComboBox, 1, 1);
    grid.add(datumLabel, 0, 2);
    grid.add(datePicker, 1, 2);
    grid.add(updateButton, 1, 3);

    Scene scene = new Scene(grid, 300, 275);
    updateStage.setScene(scene);
    updateStage.show();
}

private static String getCursusNaamFromId(int cursusId) {
    for (Map.Entry<String, Integer> entry : cursusIdMap.entrySet()) {
        if (entry.getValue().equals(cursusId)) {
            return entry.getKey();
        }
    }
    return null; 
}

    public static void updateInschrijving(String cursistEmail, int cursusId, LocalDate inschrijfDatum) {
        String url = databaseConnect.getUrl();
        String gebruikersnaam = databaseConnect.getGebruikersnaam();
        String wachtwoord = databaseConnect.GetPass();
    
        try (Connection connection = DriverManager.getConnection(url, gebruikersnaam, wachtwoord)) {
            String query = "UPDATE Inschrijving SET CursusID = ?, InschrijfDatum = ? WHERE CursistEmail = ?";
    
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, cursusId);
                preparedStatement.setDate(2, java.sql.Date.valueOf(inschrijfDatum));
                preparedStatement.setString(3, cursistEmail);
    
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Inschrijving is succesvol bijgewerkt voor cursist: " + cursistEmail);
                } else {
                    System.out.println("Er is een probleem opgetreden bij het bijwerken van de inschrijving.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Databasefout: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

}