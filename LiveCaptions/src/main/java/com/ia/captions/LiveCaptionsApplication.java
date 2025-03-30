package com.ia.captions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import javafx.application.Application;
import javafx.stage.Stage;

@SpringBootApplication
public class LiveCaptionsApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(LiveCaptionsApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Live Captions App...");
		SpringApplication.run(LiveCaptionsApplication.class, args);
		logger.info("Spring App started");
		logger.info("Starting UI...");
		Application.launch(args);
		
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.show();
		logger.info("UI Started");
	}

}