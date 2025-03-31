package com.ia.captions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

@SpringBootApplication
public class LiveCaptionsApplication extends Application {

	private static final Logger logger = LoggerFactory.getLogger(LiveCaptionsApplication.class);
	private VBox captionContainer;
	private ScrollPane scrollPane;
	private TextFlow textFlow;
	private boolean keepAutoScrolling;

	public static void main(String[] args) {
		logger.info("Starting Live Captions App...");
		SpringApplication.run(LiveCaptionsApplication.class, args);
		logger.info("Spring App started");
		logger.info("Starting UI...");
		Application.launch(args);

	}

	@Override
	public void start(Stage primaryStage) {

		keepAutoScrolling = true;

		primaryStage.initStyle(StageStyle.TRANSPARENT); // No window borders
		primaryStage.setAlwaysOnTop(true); // Always on top

		// TextFlow for proper wrapping & selection
		textFlow = new TextFlow();
		textFlow.setMaxWidth(550);
		textFlow.setLineSpacing(5);
		textFlow.setStyle("-fx-background-color: transparent;");

		// VBox to contain the textFlow
		captionContainer = new VBox(textFlow);
		captionContainer.setAlignment(Pos.BOTTOM_CENTER);
		captionContainer.setStyle(
				"-fx-background-color: rgba(0, 0, 0, 0.3); " + "-fx-padding: 15px; " + "-fx-background-radius: 15px;");

		// ScrollPane (Transparent)
		scrollPane = new ScrollPane(captionContainer);
		scrollPane.setFitToWidth(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
		scrollPane.setPannable(true);
		scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
			System.out.println("Scrolling detected! New value: " + newVal);
			onUserScroll(oldVal, newVal);
		});

		// Wrapper for centering
		StackPane root = new StackPane(scrollPane);
		root.setAlignment(Pos.BOTTOM_CENTER);
		root.setStyle("-fx-background-color: transparent;");

		Scene scene = new Scene(root, 600, 150);
		scene.setFill(Color.TRANSPARENT);

		primaryStage.setScene(scene);

		// Position: Centered Horizontally & Bottom-Aligned
		double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
		double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
		double windowWidth = 600;

		primaryStage.setX((screenWidth - windowWidth) / 2);
		primaryStage.setY(screenHeight - 180);

		primaryStage.show();

		// Test caption
		addTextWithAnimation(
				"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean vel feugiat dolor, mattis lacinia lorem. Nulla scelerisque, tortor eget suscipit consequat, erat metus euismod diam, at pretium sem diam eu libero. Aliquam elementum malesuada elit, ut semper ex interdum in. Duis eget arcu pharetra, aliquam sem a, interdum ante. Donec eros ipsum, pulvinar id sapien imperdiet, aliquam varius lacus. Cras bibendum neque sed purus scelerisque hendrerit. Nulla eget tortor rutrum, finibus velit sed, ullamcorper tortor. Nullam sollicitudin eget mi et tristique. Duis dignissim quis arcu a fringilla. Donec blandit rutrum nisi, in mattis ligula accumsan vel. Integer eu enim tincidunt, commodo urna in, auctor augue. Ut vestibulum eleifend fermentum. Vivamus sed nisi metus. Aenean in nulla lobortis, consequat leo vel, placerat risus. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Praesent eu luctus nunc.\n"
						+ "\n"
						+ "Aenean non dolor non erat dictum viverra. Pellentesque tincidunt, sapien et tempor maximus, enim metus consectetur mi, quis pharetra arcu orci a erat. Maecenas eget luctus sapien. Curabitur consequat ex in elit facilisis, ut ultricies mi feugiat. In quis nulla tempor, aliquam neque sed, mollis dolor. Praesent mattis consequat felis ut dignissim. Duis tempus lectus in arcu fringilla, sed vestibulum felis pulvinar.\n"
						+ "\n"
						+ "Duis tempor tincidunt augue, a dignissim ipsum volutpat placerat. Donec ornare orci et est fringilla venenatis. Aenean gravida non lectus vitae faucibus. Mauris in nunc sodales, sodales urna non, tempor elit. Suspendisse ultricies ipsum a eros iaculis convallis. Sed mattis, nunc vitae consequat dignissim, augue libero lacinia enim, id imperdiet odio velit nec est. Phasellus eget ultricies est.");
	}

	// Typing effect animation with smooth autoscrolling
	private void addTextWithAnimation(String text) {
		for (int i = 0; i < text.length(); i++) {
			final int index = i;
			PauseTransition delay = new PauseTransition(Duration.millis(30 * i));
			delay.setOnFinished(event -> {
				Text newText = new Text(String.valueOf(text.charAt(index)));
				newText.setFont(Font.font("Arial", 22));
				newText.setFill(Color.WHITE);

				textFlow.getChildren().add(newText);

				// Ensure scrolling is updated properly
				if (keepAutoScrolling) {
					scrollPane.layout();
					scrollPane.setVvalue(1.0); // Forces autoscrolling

				}
			});
			delay.play();
		}
	}

	// Method to handle scroll event
	private void onUserScroll(Number oldVal, Number newVal) {
		// Disable autoscrolling when user try to scroll up and re enable autoscrolling
		// when user scrolls again to the bottom of the scroll.

		if (newVal.doubleValue() < 0.98) { // Less than max scroll
			this.keepAutoScrolling = false;
		} else { // If user scrolls back to the bottom
			this.keepAutoScrolling = true;
		}
	}

}