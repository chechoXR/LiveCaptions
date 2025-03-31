package com.ia.captions;

import java.util.concurrent.BlockingQueue;


/**
 * TODO: Here is needed to implement the necessary code to record audio system and generate live captions.
 * After generate the captions use the generateCaption to return the value of the text that will be displayed.
 * 
 *  - Use the variables to get the secrets and sensitive data from the application.properties file, so no secrets will be persisted in code.
 *  
 */

public class CaptionGenerator {
	private BlockingQueue<String> textQueue;
	
	private String endpoint;
	private String secret;

	
	/**
	 * TODO: Initialize variables with the proper value. 
	 * @param textQueue2
	 */
    public CaptionGenerator(BlockingQueue<String> textQueue2) {
        this.textQueue = textQueue2; // This links it to the main app
        
        
    }

    
    /**
     * This will keep "listening" for new text to be added into the queue so the text will be shown as is generated.
     */
    public void startListening() {
        new Thread(() -> {
            try {
                while (true) {
                    String newCaption = generateCaption(); // Replace with real caption source
                    if (newCaption != null && !newCaption.isEmpty()) {
                        textQueue.offer(newCaption); // Adds text to the shared queue
                    }
                    
                    //Check if this sleep works with the Generated AI caption 
                    Thread.sleep(200); 
                    
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    
    /**
     * Todo: Here should be the main code that makes the magic of generating the captions  
     * @return
     */
    private String generateCaption() {
        String[] sampleCaptions = {
            "¡Hola! ¿Cómo estás?\n",
            "El clima está increíble hoy.\n",
            "La vida es un hermoso viaje.\n",
            "¿Te gustaría un café?\n",
            "Árboles, montañas y ríos, qué hermoso paisaje.\n"
        };
        return sampleCaptions[(int) (Math.random() * sampleCaptions.length)];
    }
}
