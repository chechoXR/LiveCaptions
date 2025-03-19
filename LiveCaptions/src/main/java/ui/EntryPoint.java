package ui;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class EntryPoint extends Application  implements NativeKeyListener {


	 private static final double WINDOW_WIDTH = 700;	 private static final double WINDOW_HEIGHT = 200;
     private Stage primaryStage;


	public static void main(String[] args) {
		System.out.println("Hola");
		launch(args);
		System.out.println("Ayios");

	}

	@Override
	    public void start(Stage stage) {
		this.primaryStage=stage;
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
        
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setAlwaysOnTop(true);

        // Position window at bottom
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        primaryStage.setX((Screen.getPrimary().getBounds().getWidth() - WINDOW_WIDTH) / 2);
        primaryStage.setY(screenHeight - WINDOW_HEIGHT - 50);

        setupGlobalHotkey();
        primaryStage.show();
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				primaryStage.close();
				
				//Stops listening for Global shortcuts
				try {
					GlobalScreen.unregisterNativeHook();
				} catch (NativeHookException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        
        
        
	}
	
	private void setupGlobalHotkey() {
        try {
            // Disable JNativeHook logs
            LogManager.getLogManager().reset();
            Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);

            // Register global key listener
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
    	 // Detect CTRL + SHIFT (left) + C
        boolean isAlt = (e.getModifiers() & NativeKeyEvent.ALT_MASK) != 0;
        boolean isShift = (e.getModifiers() & NativeKeyEvent.SHIFT_L_MASK) != 0; // Left Shift
        boolean isC = (e.getKeyCode() == NativeKeyEvent.VC_C);

        if (isAlt && isShift && isC) {
            // Toggle visibility of the JavaFX window
        	Platform.runLater(() -> {
        	    if (primaryStage.isIconified()) {
        	        primaryStage.setIconified(false); // Restore window
        	    } else {
        	        primaryStage.setIconified(true); // Minimize window instead of hiding
        	    }
        	});
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {}

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {}

	
}
