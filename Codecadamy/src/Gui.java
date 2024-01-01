
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
        Button buttonOverview = new Button("Overview pages");
        Button buttonInschrijving = new Button("Inschrijving");
        
        // Voeg knopfunctionaliteit toe
        buttonCursist.setOnAction(e -> Cursist.openCursistVenster());
        buttonOverview.setOnAction(e -> Overview.openOverviewPage());
        buttonInschrijving.setOnAction(e -> Inschrijving.openInschrijvingenVenster());

        buttonBox.getChildren().addAll(buttonCursist, buttonOverview, buttonInschrijving);

        // Maak een VBox voor het opmaken van de gehele pagina
        VBox vbox = new VBox(10); // 10 is de verticale ruimte tussen titel en knoppen
        vbox.getChildren().addAll(titleBox, buttonBox);

        // Maak een Scene en toon het venster
        Scene scene = new Scene(vbox, 700, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}