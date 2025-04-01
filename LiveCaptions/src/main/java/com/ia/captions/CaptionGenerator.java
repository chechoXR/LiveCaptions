package com.ia.captions;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.core.env.Environment;

import com.microsoft.cognitiveservices.speech.CancellationDetails;
import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.PropertyId;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.translation.SpeechTranslationConfig;
import com.microsoft.cognitiveservices.speech.translation.TranslationRecognitionResult;
import com.microsoft.cognitiveservices.speech.translation.TranslationRecognizer;

/**
 * TODO: Here is needed to implement the necessary code to record audio system
 * and generate live captions. After generate the captions use the
 * generateCaption to return the value of the text that will be displayed.
 * 
 * - Use the variables to get the secrets and sensitive data from the
 * application.properties file, so no secrets will be persisted in code.
 * 
 */

public class CaptionGenerator {
	private BlockingQueue<String> textQueue;

	private String endpoint;
	private String region;
	private String key;

	private AudioConfig audioConfig;
	private TranslationRecognizer translationRecognizer;
	
	private static final String RecognitionLanguage="en-US";
	private static final String TranslationLanguage="es";

	/**
	 * TODO: Initialize variables with the proper value.
	 * 
	 * @param textQueue2
	 */
	public CaptionGenerator(BlockingQueue<String> textQueue2, Environment environment) {
		this.textQueue = textQueue2; // This links it to the main app

		this.endpoint = environment.getProperty("speech.endpoint");
		this.region = environment.getProperty("speech.region");
		this.key = environment.getProperty("speech.key");

		System.out.println("ENDPOINT: " + environment.getProperty("speech.endpoint"));

		if (this.endpoint == null || this.region == null || this.key == null) {
			System.err.println("No se pudieron encontrar los parametros para el reconocimiento de voz.");
			System.exit(0);
		}

		// Speech config:

		SpeechTranslationConfig speechTranslationConfig = SpeechTranslationConfig.fromSubscription(this.key,
				this.region);
		speechTranslationConfig.setSpeechRecognitionLanguage(RecognitionLanguage);
		System.out.println(speechTranslationConfig.getProperty("SPEECH-Endpoint-SilenceTimeoutMs").toString());
		System.out.println(speechTranslationConfig.getProperty("SPEECH-Endpoint-SilenceTimeoutMs").toString());
		//SPEECH-Endpoint-SilenceTimeoutMs
		speechTranslationConfig.setProperty(PropertyId.Speech_SegmentationSilenceTimeoutMs, "100");
		System.out.println(speechTranslationConfig.getProperty("SPEECH-Endpoint-SilenceTimeoutMs").toString());

		String[] toLanguages = { TranslationLanguage };
		for (String language : toLanguages) {
			speechTranslationConfig.addTargetLanguage(language);
		}
		this.audioConfig = AudioConfig.fromDefaultMicrophoneInput();
		this.translationRecognizer = new TranslationRecognizer(speechTranslationConfig, audioConfig);

	}

	/**
	 * This will keep "listening" for new text to be added into the queue so the
	 * text will be shown as is generated.
	 */
	public void startListening() throws InterruptedException, ExecutionException {
		generateCaption();
	}

	/**
	 * Todo: Here should be the main code that makes the magic of generating the
	 * captions
	 * 
	 * @return
	 */
	private String generateCaption() throws InterruptedException, ExecutionException {

		System.out.println("Speak into your microphone.");

		translationRecognizer.recognized.addEventListener((s, e) -> {
			if (e.getResult().getReason() == ResultReason.TranslatedSpeech) {
				String result = e.getResult().getTranslations().get(TranslationLanguage);
				System.out.println(result);
				System.out.println(result);
				System.out.println(result);
				if (result != null && !result.isEmpty()) {
					textQueue.offer(result + "\n");
				}
			} else
				System.out.println("Other reason: " + e.getResult().getReason());
		});
		translationRecognizer.startContinuousRecognitionAsync();

		return "........\n";

	}
}
