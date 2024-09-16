package datatypes;

import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.Http;

/**
 * Created by ethanlin on 25/07/2017.
 */
public class AppRequest {
    private String requestUuid;
    private String apiKey;
    private String verify;
    private String timestamp;
    private String processCode;
    private Http.Request httpRequest;
    private JsonNode parameters;

    public AppRequest(String requestUuid, String apiKey, String verify, String timestamp, String processCode, Http.Request httpRequest, JsonNode parameters) {
        this.requestUuid = requestUuid;
        this.apiKey = apiKey;
        this.verify = verify;
        this.timestamp = timestamp;
        this.httpRequest = httpRequest;
        this.processCode = processCode;
        this.parameters = parameters;
    }

    public JsonNode getParameters() {
        return parameters;
    }

    public void setParameters(JsonNode parameters) {
        this.parameters = parameters;
    }

    public String getProcessCode() {
        return processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    public Http.Request getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(Http.Request httpRequest) {
        this.httpRequest = httpRequest;
    }

    public String getRequestUuid() {
        return requestUuid;
    }

    public void setRequestUuid(String requestUuid) {
        this.requestUuid = requestUuid;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getVerify() {
        return verify;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
