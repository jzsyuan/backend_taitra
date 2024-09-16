package datatypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

/**
 * Created by ethanlin on 25/07/2017.
 */
public class AppResponse {
    private Error error;
    private String additionalErrorMessage;
    private JsonNode response;

    public JsonNode getMixedResponse() {
        ObjectNode mixedResponse = new ObjectNode(JsonNodeFactory.instance);
        if (response!=null) {
            mixedResponse.setAll((ObjectNode) response);
        }
        if (error!=null) {
            mixedResponse.setAll((ObjectNode) Json.toJson(error.toErrorMap(additionalErrorMessage)));
        }
        return mixedResponse;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public void setError(Error error, String additionalErrorMessage) {
        this.error = error;
        this.additionalErrorMessage = additionalErrorMessage;
    }

    public String getAdditionalErrorMessage() {
        return additionalErrorMessage;
    }

    public void setAdditionalErrorMessage(String additionalErrorMessage) {
        this.additionalErrorMessage = additionalErrorMessage;
    }

    public JsonNode getResponse() {
        return response;
    }

    public void setResponse(JsonNode response) {
        this.response = response;
    }
}
