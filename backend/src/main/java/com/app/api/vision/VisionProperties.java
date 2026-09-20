package com.app.api.vision;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component 
@ConfigurationProperties(prefix = "supaneighbour.vision")
public class VisionProperties {

    public enum Mode {STUB, AZURE_OPENAI}

    private Mode mode;
    private final AzureOpenai azureOpenai = new AzureOpenai();
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
    public AzureOpenai getAzureOpenai(){
        return azureOpenai;
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
        public void setOutcom(Outcome outcome){
            this.outcome = outcome;
        }
    }

    public static class AzureOpenai {
        /** https://&lt;resource-name&gt;.openai.azure.com */
        private String endpoint;
        /** Secret - keep in Azure Application Settings / env vars, never commit, never log. */
        private String key;
        /** The DEPLOYMENT name Gendac chose, not the model name. */
        private String deployment;
        private String apiVersion = "2024-10-21";
        private int maxImageEdgePx = 1600;
        private int timeoutSeconds = 30;
 
        /**
         * Gets the endpoint URL.
         *
         * @return the endpoint URL
         */
        public String getEndpoint() { 
            return endpoint; 
        }
        /**
         * Sets the endpoint URL.
         *
         * @param endpoint the endpoint URL
         */
        public void setEndpoint(String endpoint) { 
            this.endpoint = endpoint; 
        }
        /**
         * Gets the API key.
         *
         * @return the API key
         */
        public String getKey() { 
            return key; 
        }
        /**
         * Sets the API key.
         *
         * @param key the API key
         */
        public void setKey(String key) { 
            this.key = key; 
        }
        /**
         * Gets the deployment name.
         *
         * @return the deployment name
         */
        public String getDeployment() { 
            return deployment; 
        }
        /**
         * Sets the deployment name.
         *
         * @param deployment the deployment name
         */
        public void setDeployment(String deployment) { 
            this.deployment = deployment;
        }
        /**
         * Gets the API version.
         *
         * @return the API version
         */
        public String getApiVersion() { 
            return apiVersion; 
        }
        /**
         * Sets the API version.
         *
         * @param apiVersion the API version
         */
        public void setApiVersion(String apiVersion) { 
            this.apiVersion = apiVersion; 
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
