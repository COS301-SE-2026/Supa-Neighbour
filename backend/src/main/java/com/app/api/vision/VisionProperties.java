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

    public Mode getMode(){
        return mode;
    }

    public void setMode(Mode mode){
        this.mode = mode;
    }

    public AzureOpenai getAzureOpenai(){
        return azureOpenai;
    }

    public Stub getStub(){
        return stub;
    }

    public static class Stub{
        public enum Outcome { COMPLETE, INCOMPLETE, CONTENT_FILTERED, RATE_LIMITED, ERROR }

        private Outcome outcome = Outcome.COMPLETE;

        public Outcome getOutcome(){
            return outcome;
        }
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
 
        public String getEndpoint() { 
            return endpoint; 
        }
        public void setEndpoint(String endpoint) { 
            this.endpoint = endpoint; 
        }
        public String getKey() { 
            return key; 
        }
        public void setKey(String key) { 
            this.key = key; 
        }
        public String getDeployment() { 
            return deployment; 
        }
        public void setDeployment(String deployment) { 
            this.deployment = deployment;
        }
        public String getApiVersion() { 
            return apiVersion; 
        }
        public void setApiVersion(String apiVersion) { 
            this.apiVersion = apiVersion; 
        }
        public int getMaxImageEdgePx() { 
            return maxImageEdgePx; 
        }
        public void setMaxImageEdgePx(int maxImageEdgePx) {
            this.maxImageEdgePx = maxImageEdgePx; 
        }
        public int getTimeoutSeconds() {
             return timeoutSeconds; 
        }
        public void setTimeoutSeconds(int timeoutSeconds) {
             this.timeoutSeconds = timeoutSeconds; 
        }
    }
    
}
