package utils;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.AppController;
import datatypes.AppRequest;
import datatypes.LogLevel;
import models.ApiKey;
import play.Logger;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import datatypes.Error;
import play.mvc.Http;

public class ValidationUtils {
    private static Map<String, ApiKey> apiKeyMap = new HashMap<>();
    protected static String API_KEY_TYPE = "TAITRA";

    public static Error validateInputParameter(JsonNode node, String... keys) {
        for (String key : keys) {
            if (!node.has(key)) {
                return Error.NOT_ENOUGH_PARAMETERS;
            } else if (node.get(key) == null) {
                return Error.NOT_ENOUGH_PARAMETERS;
            }
        }
        return null;
    }

    public static void requestLog(String apiName, LogLevel logLevel, Throwable throwable, String format, Object... parameters) {
        String message = String.format("[%s] %s", apiName, String.format(format, parameters));
        if (throwable == null) {
            switch (logLevel) {
                case TRACE:
                    Logger.trace(message);
                    break;
                case DEBUG:
                    Logger.debug(message);
                    break;
                case INFO:
                    Logger.info(message);
                    break;
                case WARN:
                    Logger.warn(message);
                    break;
                case ERROR:
                    Logger.error(message);
                    break;
            }
        } else {
            switch (logLevel) {
                case TRACE:
                    Logger.trace(message, throwable);
                    break;
                case DEBUG:
                    Logger.debug(message, throwable);
                    break;
                case INFO:
                    Logger.info(message, throwable);
                    break;
                case WARN:
                    Logger.warn(message, throwable);
                    break;
                case ERROR:
                    Logger.error(message, throwable);
                    break;
            }
        }
    }

    public static Error verifyRequest(String apiName, Http.Request request) {
        if (!request.getHeaders().contains("key") || !request.getHeaders().contains("verify")) {
            return Error.HEADER_ERROR;
        }

        String requestApiKey = request.getHeaders().get("key").get();
        String verify = request.getHeaders().get("verify").get();

        String plainText = request.body().asBytes().utf8String();

        Logger.info("request:{}", request);
        Logger.info("plainText:{}", plainText);
        try {
            ApiKey apiKey = findApiKey(requestApiKey);
            if (apiKey == null) {
                return Error.API_VERIFY_ERROR;
            }
            String calculatedSignature = Crypto.calculateHmacSha256Base64(plainText, apiKey.getApiSecret());
            if (!calculatedSignature.equals(verify)) {
                requestLog(apiName, LogLevel.INFO, "Api verify error api key=" + apiKey.getApiKey() + " client signature=" + verify + " server signature=" + calculatedSignature + "\n======Payload======\n" + plainText);
                return Error.VERIFY_ERROR;
            }
        } catch (Exception e) {
            requestLog(apiName, LogLevel.ERROR, e, "HMAC-SHA256 error key=" + requestApiKey + " content=" + plainText);
            return Error.SYSTEM_EXCEPTION;
        }
        return null;
    }

    public static String verifyRequestTest(String apiName, Http.Request request) {
        if (!request.getHeaders().contains("key") || !request.getHeaders().contains("verify")) {
            return null;
        }

        String requestApiKey = request.getHeaders().get("key").get();
        String verify = request.getHeaders().get("verify").get();

        String plainText = request.body().asBytes().utf8String();

        Logger.info("request:{}", request);
        Logger.info("plainText:{}", plainText);
        try {
            ApiKey apiKey = findApiKey(requestApiKey);
            if (apiKey == null) {
                return null;
            }
            String calculatedSignature = Crypto.calculateHmacSha256Base64(plainText, apiKey.getApiSecret());
            if (!calculatedSignature.equals(verify)) {
                requestLog(apiName, LogLevel.INFO, "Api verify error api key=" + apiKey.getApiKey() + " client signature=" + verify + " server signature=" + calculatedSignature + "\n======Payload======\n" + plainText);
                return calculatedSignature;
            }
        } catch (Exception e) {
            requestLog(apiName, LogLevel.ERROR, e, "HMAC-SHA256 error key=" + requestApiKey + " content=" + plainText);
            return null;
        }
        return null;
    }

    private static ApiKey findApiKey(String apiKey) {
        ApiKey key = apiKeyMap.get(apiKey);
        if (key == null) {
            key = ApiKey.findKey(API_KEY_TYPE, apiKey);
            if (key != null) {
                apiKeyMap.put(apiKey, key);
            }
        }
        return key;
    }

    private static void requestLog(String apiName, LogLevel logLevel, String format, Object... parameters) {
        requestLog(apiName, logLevel, null, format, parameters);
    }
}
