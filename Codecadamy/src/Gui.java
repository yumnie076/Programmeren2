
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 30));

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
        buttonCursist.setOnAction(e -> Cursist.openCursistVenster());

        buttonBox.getChildren().addAll(buttonCursist);

        // Maak een VBox voor het opmaken van de gehele pagina
        VBox vbox = new VBox(10); // 10 is de verticale ruimte tussen titel en knoppen
        vbox.getChildren().addAll(titleBox, buttonBox);

        // Maak een Scene en toon het venster
        Scene scene = new Scene(vbox, 700, 700);
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
        HBox HBox = new HBox(10); // 10 is de verticale ruimte tussen knoppen
        HBox.setPadding(new Insets(10, 10, 10, 10));
        HBox.setAlignment(javafx.geometry.Pos.CENTER); // Knoppen in het midden van de pagina

        // Voeg knoppen toe aan de VBox
        Button backButton = new Button("Terug naar Homepagina");

        // Voeg knopfunctionaliteit toe
        backButton.setOnAction(e -> {
            cursusStage.close();
            openHomeVenster();
        });

        HBox.getChildren().addAll(titleBox, backButton);

        // Maak een Scene en toon het venster
        Scene scene = new Scene(HBox, 300, 200);
        cursusStage.setScene(scene);
        cursusStage.show();
    }

    private static void openHomeVenster() {
        Stage homeStage = new Stage();
        homeStage.setTitle("Homepagina");

        // Maak een HomePage-object en roep de start-methode aan
        Gui homePage = new Gui();
        homePage.start(homeStage);
    }
}