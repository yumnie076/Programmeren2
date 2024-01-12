package main.java;

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
        primaryStage.setTitle("Homepage");

        // Title at the top of the page
        Text title = new Text("Welcome to the Homepage");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 30));

        Text titleTwo = new Text("Salim Fenzar(2211259), Yumnie Taouil(2211614)");
        titleTwo.setFont(Font.font("Verdana", FontWeight.MEDIUM, 20));

        // Create an HBox for formatting the title
        HBox titleBox = new HBox();
        titleBox.getChildren().add(title);
        titleBox.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        titleBox.setPadding(new Insets(10, 10, 10, 10));

        HBox titleTwoBox = new HBox();
        titleTwoBox.getChildren().add(titleTwo);
        titleTwoBox.setAlignment(javafx.geometry.Pos.BOTTOM_CENTER);
        titleTwoBox.setPadding(new Insets(10, 10, 10, 10));

        // Create an HBox for formatting the buttons
        HBox buttonBox = new HBox(10); // 10 is the horizontal spacing between buttons
        buttonBox.setPadding(new Insets(10, 10, 10, 10));
        buttonBox.setAlignment(javafx.geometry.Pos.BOTTOM_CENTER); // Buttons at the bottom and centered

        // Add buttons to the HBox
        Button buttonCursist = new Button("Cursist");
        Button buttonOverview = new Button("Overview pages");
        Button buttonInschrijving = new Button("Inschrijving");

        // Add button functionality
        buttonCursist.setOnAction(e -> Cursist.openCursistVenster());
        buttonOverview.setOnAction(e -> Overview.openOverviewPage());
        buttonInschrijving.setOnAction(e -> Inschrijving.openInschrijvingenVenster());

        buttonBox.getChildren().addAll(buttonCursist, buttonOverview, buttonInschrijving);

        // Create a VBox for formatting the entire page
        VBox vbox = new VBox(10); // 10 is the vertical spacing between title and buttons
        vbox.getChildren().addAll(titleBox, titleTwoBox, buttonBox);

        // Create a Scene and display the window
        Scene scene = new Scene(vbox, 700, 700);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}