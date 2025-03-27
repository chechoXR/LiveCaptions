package ui;

import java.net.URL;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class EntryPoint extends Application {


	 private static final double WINDOW_WIDTH = 700;	 private static final double WINDOW_HEIGHT = 200;  

	public static void main(String[] args) {
		System.out.println("Hola");
		launch(args);
		System.out.println("Ayios");

	}

	@Override
	    public void start(Stage primaryStage) {
	     // Create a selectable text area
        TextArea captionText = new TextArea(
            "Live Captions Appear Here... áéíóú\n" +
            "This text can be selected and copied.\n" +
            "More text to test scrolling. Keep adding lines!"+
            "This text can be selected and copied.\n" +
            "More text to test scrolling. Keep adding lines!"+
            "This text can be selected and copied.\n" +
            "More text to test scrolling. Keep adding lines!"+
            "This text can be selected and copied.\n" +
            "More text to test scrolling. Keep adding lines!"
        );
        captionText.setWrapText(true); // Enables text wrapping
        captionText.setEditable(false); // Prevents user from modifying text
        captionText.setStyle(
            "-fx-font-size: 24px; " +
            "-fx-text-fill: white; " +
            "-fx-background-color: transparent; " +
            "-fx-control-inner-background: transparent; " + // Makes text area transparent
            "-fx-border-color: transparent; " // Removes borders
        );
        
     // Disable JavaFX right-click context menu
        captionText.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> event.consume());


        // ScrollPane for scrolling
        ScrollPane scrollPane = new ScrollPane(captionText);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle(
            "-fx-background: transparent; " +
            "-fx-background-color: transparent; " +
            "-fx-border-color: transparent;"
        );

        // Blurred background
        Rectangle blurBackground = new Rectangle(WINDOW_WIDTH, WINDOW_HEIGHT, Color.rgb(0, 0, 0, 0.4));
        blurBackground.setArcWidth(20);
        blurBackground.setArcHeight(20);
        blurBackground.setEffect(new BoxBlur(10, 10, 3));

        // Positioning
        VBox container = new VBox(scrollPane);
        container.setAlignment(Pos.BOTTOM_CENTER);
        container.setPadding(new Insets(0, 0, 20, 0));

        // StackPane to overlay blur + text
        StackPane root = new StackPane(blurBackground, container);
        root.setStyle("-fx-background-color: transparent;");

        // Transparent scene
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        // Configure Stage
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);

        // Position window at bottom
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        primaryStage.setX((Screen.getPrimary().getBounds().getWidth() - WINDOW_WIDTH) / 2);
        primaryStage.setY(screenHeight - WINDOW_HEIGHT - 50);

        primaryStage.show();
        
	}
	
}
