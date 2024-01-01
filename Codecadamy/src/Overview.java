import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Overview {
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

        Button averagePerson = new Button("Gemiddelde voortgang per module per persoon");

        Button top3 = new Button("Top 3 meest bekeken webcasts");
        top3.setOnAction(e -> topWebcast());

        Button accomplished = new Button("Aantal geslaagde cursisten");

        vbox.getChildren().addAll(titleBox, average, averagePerson, top3, accomplished);

        // Maak een Scene en toon het venster
        Scene scene = new Scene(vbox, 500, 500);
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
        topWebcast.setScene(scene);
        topWebcast.show();
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
