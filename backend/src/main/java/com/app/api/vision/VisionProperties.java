package com.app.api.vision;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component 
@ConfigurationProperties(prefix = "supaneighbour.vision")
public class VisionProperties {

    public enum Mode {STUB, GEMINI}

    private Mode mode;
    private final Gemini gemini = new Gemini();
    private final Stub stub = new Stub();

    /**
     * Gets the active client mode.
     *
     * @return the mode, or {@code null} if not configured
     */
    public Mode getMode(){
        return mode;
    }

    /**
     * Sets the active client mode.
     *
     * @param mode the mode
     */
    public void setMode(Mode mode){
        this.mode = mode;
    }

    /**
     * Gets the Azure OpenAI settings.
     *
     * @return the Azure OpenAI settings; never {@code null}
     */
    public Gemini getGemini(){
        return gemini;
    }

    /**
     * Gets the stub-client settings.
     *
     * @return the stub settings; never {@code null}
     */
    public Stub getStub(){
        return stub;
    }

    /**
     * Settings for {@code StubVisionClient}, controlling which canned outcome
     * the stub returns.
     */
    public static class Stub{
        public enum Outcome { COMPLETE, INCOMPLETE, CONTENT_FILTERED, RATE_LIMITED, ERROR }

        private Outcome outcome = Outcome.COMPLETE;

        /**
         * Gets the configured stub outcome.
         *
         * @return the outcome
         */
        public Outcome getOutcome(){
            return outcome;
        }
        /**
         * Sets the stub outcome.
         *
         * @param outcome the outcome
         */
        public void setOutcome(Outcome outcome){
            this.outcome = outcome;
        }
    }

    public static class Gemini {
        /** Secret - keep in Azure Application Settings / env vars, never commit, never log. */
        private String apiKey;
        /** The DEPLOYMENT name Gendac chose, not the model name. */
        private String model = "gemini-2.0-flash";
        private int maxImageEdgePx = 1600;
        private int timeoutSeconds = 30;
 
        
        /**
         * Gets the API key.
         *
         * @return the API key
         */
        public String getApiKey() { 
            return apiKey; 
        }
        /**
         * Sets the API key.
         *
         * @param key the API key
         */
        public void setApiKey(String key) { 
            this.apiKey = key; 
        }
        /**
         * Gets the model name.
         *
         * @return the model name
         */
        public String getModel() { 
            return model; 
        }
        /**
         * Sets the model name.
         *
         * @param model the model name
         */
        public void setModel(String model) { 
            this.model = model;
        }
        /**
         * Gets the maximum image edge length.
         *
         * @return the maximum edge length in pixels
         */
        public int getMaxImageEdgePx() { 
            return maxImageEdgePx; 
        }
        /**
         * Sets the maximum image edge length.
         *
         * @param maxImageEdgePx the maximum edge length in pixels
         */
        public void setMaxImageEdgePx(int maxImageEdgePx) {
            this.maxImageEdgePx = maxImageEdgePx; 
        }
        /**
         * Gets the HTTP request timeout.
         *
         * @return the timeout in seconds
         */
        public int getTimeoutSeconds() {
             return timeoutSeconds; 
        }
        /**
         * Sets the HTTP request timeout.
         *
         * @param timeoutSeconds the timeout in seconds
         */
        public void setTimeoutSeconds(int timeoutSeconds) {
             this.timeoutSeconds = timeoutSeconds; 
        }
    }
    
}
