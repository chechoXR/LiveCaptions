package com.ia.captions;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.stage.WindowEvent;
import javafx.util.Duration;

@SpringBootApplication
public class LiveCaptionsApplication extends Application implements NativeKeyListener {

	private static final Logger logger = LoggerFactory.getLogger(LiveCaptionsApplication.class);
	private VBox captionContainer;
	private ScrollPane scrollPane;
	private TextFlow textFlow;
	private boolean keepAutoScrolling;
	private Stage primaryStage;
	private final BlockingQueue<String> textQueue = new LinkedBlockingQueue<>();


	public static void main(String[] args) {
		logger.info("Starting Live Captions App...");
		SpringApplication.run(LiveCaptionsApplication.class, args);
		logger.info("Spring App started");
		logger.info("Starting UI...");
		Application.launch(args);

	}

	@Override
	public void start(Stage stage) {

		this.primaryStage = stage;
		keepAutoScrolling = true;

		primaryStage.initStyle(StageStyle.TRANSPARENT); // No window borders
		primaryStage.setAlwaysOnTop(true); // Always on top

		// TextFlow for proper wrapping & selection
		textFlow = new TextFlow();
		textFlow.setMaxWidth(750);
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
			//System.out.println("Scrolling detected! New value: " + newVal);
			onUserScroll(oldVal, newVal);
		});

		// Wrapper for centering
		StackPane root = new StackPane(scrollPane);
		root.setAlignment(Pos.BOTTOM_CENTER);
		root.setStyle("-fx-background-color: transparent;");

		Scene scene = new Scene(root, 750, 150);
		scene.setFill(Color.TRANSPARENT);

		primaryStage.setScene(scene);

		// Position: Centered Horizontally & Bottom-Aligned
		double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
		double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
		double windowWidth = 750;

		primaryStage.setX((screenWidth - windowWidth) / 2);
		primaryStage.setY(screenHeight - 180);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				primaryStage.close();

				// Stops listening for Global shortcuts
				try {
					GlobalScreen.unregisterNativeHook();
					logger.info("Exiting app...");
					System.exit(0);

				} catch (NativeHookException e) {
				
					e.printStackTrace();
				}
			}
		});

		logger.info("Statring Global Hotkey listener");
		setupGlobalHotkey();
		logger.info("Global Hotkey started");
		
		logger.info("Starting processQueue");
	    processQueue();
	    logger.info("ProcessQueue started");
	    
	    logger.info("Showing UI");
		primaryStage.show();
		logger.info("Now all should be up and running :D");
		
		// Test caption
		queueText(
				"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean vel feugiat dolor, mattis lacinia lorem. Nulla scelerisque, tortor eget suscipit consequat, erat metus euismod diam, at pretium sem diam eu libero. Aliquam elementum malesuada elit, ut semper ex interdum in. Duis eget arcu pharetra, aliquam sem a, interdum ante. Donec eros ipsum, pulvinar id sapien imperdiet, aliquam varius lacus. Cras bibendum neque sed purus scelerisque hendrerit. Nulla eget tortor rutrum, finibus velit sed, ullamcorper tortor. Nullam sollicitudin eget mi et tristique. Duis dignissim quis arcu a fringilla. Donec blandit rutrum nisi, in mattis ligula accumsan vel. Integer eu enim tincidunt, commodo urna in, auctor augue. Ut vestibulum eleifend fermentum. Vivamus sed nisi metus. Aenean in nulla lobortis, consequat leo vel, placerat risus. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Praesent eu luctus nunc.\n"
						+ "\n"
						+ "Aenean non dolor non erat dictum viverra. Pellentesque tincidunt, sapien et tempor maximus, enim metus consectetur mi, quis pharetra arcu orci a erat. Maecenas eget luctus sapien. Curabitur consequat ex in elit facilisis, ut ultricies mi feugiat. In quis nulla tempor, aliquam neque sed, mollis dolor. Praesent mattis consequat felis ut dignissim. Duis tempus lectus in arcu fringilla, sed vestibulum felis pulvinar.\n"
						+ "\n"
						+ "Duis tempor tincidunt augue, a dignissim ipsum volutpat placerat. Donec ornare orci et est fringilla venenatis. Aenean gravida non lectus vitae faucibus. Mauris in nunc sodales, sodales urna non, tempor elit. Suspendisse ultricies ipsum a eros iaculis convallis. Sed mattis, nunc vitae consequat dignissim, augue libero lacinia enim, id imperdiet odio velit nec est. Phasellus eget ultricies est.");
		queueText("\n Chechoooooooooooooooo");
	}

	/**
	 *  Typing effect animation with smooth autoscrolling
	 */
	@SuppressWarnings("unused")
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
	
	private void addTextWithAnimation(String text, CompletableFuture<Void> future) {
		logger.info("Writting with animation: " + text);
		for (int i = 0; i < text.length(); i++) {
	        final int index = i;
	        PauseTransition delay = new PauseTransition(Duration.millis(40 * i));
	        delay.setOnFinished(event -> {
	            Text newText = new Text(String.valueOf(text.charAt(index)));
	            newText.setFont(Font.font("Arial", 22));
	            newText.setFill(Color.WHITE);
	            
	            textFlow.getChildren().add(newText);

	            // Ensure autoscrolling
	            if (keepAutoScrolling) {
	                scrollPane.layout();
	                scrollPane.setVvalue(1.0);
	            }

	            // If it's the last character, complete the future
	            if (index == text.length() - 1) {
	                future.complete(null);
	            }
	        });
	        delay.play();
	    }
		logger.info("Finished.");
	}
	
	
	
	/**
	 *  Method to add text to queue
	 * @param text The text needed to be queued and displayed later ad the end of the scroll
	 */
	public void queueText(String text) {
	    textQueue.offer(text);
	    logger.info("Text queued: " + text);
	}

	/**
	 *  Method to process queue and display text
	 */
	private void processQueue() {
		new Thread(() -> {
	        while (true) {
	            try {
	                // Get next text from queue (waits if empty)
	                String text = textQueue.take();

	                // Use a CompletableFuture to wait for animation completion
	                CompletableFuture<Void> future = new CompletableFuture<>();

	                Platform.runLater(() -> addTextWithAnimation(text, future));

	                // Wait for animation to complete before taking the next item
	                future.get();
	            } catch (InterruptedException | ExecutionException e) {
	                e.printStackTrace();
	            }
	        }
	    }).start();
	}

	/**
	 * Method to handle scroll event
	 * 
	 * @param oldVal The old value on the scroll event
	 * @param newVal The new value on the scroll event
	 */

	private void onUserScroll(Number oldVal, Number newVal) {
		// Disable autoscrolling when user try to scroll up and re enable autoscrolling
		// when user scrolls again to the bottom of the scroll.
		logger.info("Scroll detected, Old Value: " + oldVal + "  New Value: " +  newVal);
		if (newVal.doubleValue() < 0.98) { // Less than max scroll
			this.keepAutoScrolling = false;
		} else { // If user scrolls back to the bottom
			this.keepAutoScrolling = true;
		}
	}

	private void setupGlobalHotkey() {
		try {
			// Disable JNativeHook logs
			LogManager.getLogManager().reset();
			java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);

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
		logger.info("Key pressed detected: " + e.getKeyCode());
		if (isAlt && isShift && isC) {
			// Toggle visibility of the JavaFX window
			Platform.runLater(() -> {
				if (primaryStage.isIconified()) {
					primaryStage.setIconified(false); // Restore window
					logger.info("Window restored");
				} else {
					primaryStage.setIconified(true); // Minimize window instead of hiding
					logger.info("Window minimized");
				}
			});
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {
	}

}