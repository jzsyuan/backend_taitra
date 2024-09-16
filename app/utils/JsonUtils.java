package utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import play.Logger;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by ilss0902 on 2017/6/14.
 */
public class JsonUtils {

    static ObjectMapper mapper = new ObjectMapper();

    public static ObjectNode createObjectNode() {
        return mapper.createObjectNode();
    }

    public static JsonNode parse(String string) {
        try {
           return mapper.readTree(string);
        } catch (IOException e) {
            //e.printStackTrace();
            Logger.error("parse json error,data={}", string );
        }
        catch (Exception e) {
            //e.printStackTrace();
            Logger.error("parse json error,data={}", string );
        }
        return  null;
    }

    public static <A> A fromJson(JsonNode json, Class<A> clazz) {
        try {
            return mapper.treeToValue(json, clazz);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <A> A stringToObject(String jsonString, Class<A> clazz) {
        try {
            return mapper.readValue(jsonString, clazz);

        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String objectToJsonString(Object obj) {

        return generateJson(obj,false,false);

    }


    public static String stringify(JsonNode json) {
        return generateJson(json, false, false);
    }

    /**
     * Converts a JsonNode to its string representation, escaping non-ascii characters.
     * @param json    the JSON node to convert.
     * @return the string representation with escaped non-ascii characters.
     */
    public static String asciiStringify(JsonNode json) {
        return generateJson(json, false, true);
    }

    /**
     * Converts a JsonNode to its string representation.
     * @param json    the JSON node to convert.
     * @return the string representation, pretty printed.
     */
    public static String prettyPrint(JsonNode json) {
        return generateJson(json, true, false);
    }


    private static String generateJson(Object o, boolean prettyPrint, boolean escapeNonASCII) {
        try {
            ObjectWriter writer = mapper.writer();
            if (prettyPrint) {
                writer = writer.with(SerializationFeature.INDENT_OUTPUT);
            }
            if (escapeNonASCII) {
                writer = writer.with(JsonGenerator.Feature.ESCAPE_NON_ASCII);
            }
            return writer.writeValueAsString(o);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static JsonNode objectToJson(Object obj) {

        try {

            JsonNode node = mapper.valueToTree(obj);

            return node;

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JsonNode objectToJson(Object obj, PropertyNamingStrategy propertyNamingStrategy) {

        ObjectMapper mapper2 = new ObjectMapper().setPropertyNamingStrategy(
                propertyNamingStrategy);

        try {

            JsonNode node = mapper2.valueToTree(obj);

            return node;

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getNodeValue(JsonNode node, String key, String defaultValaue) {
        if ( node.has(key) ) {
            return node.get(key).asText();
        }
        return defaultValaue;
    }

    public static String[] getArrayNodeStringArray(JsonNode node) {
        return getArrayNodeStringArray(node, false);
    }

    public static String[] getArrayNodeStringArray(JsonNode node , boolean ignoreEmptyValue) {
        if ( node == null ) {
            return null;
        }

        String[] s = new String[node.size()];

        if ( node instanceof ArrayNode ) {
            s = new String[node.size()];
            for(int i=0;i<s.length;i++) {
                s[i] = node.get(i).asText();
            }
        }
        else {
            s = new String[1];
            s[0] = node.asText();
        }


        if ( ignoreEmptyValue == true ) { //把空字串過率
            List<String> list = new ArrayList<String>();
            for(String foo : s) {
                if ( foo.isEmpty() ) {
                    continue;
                }
                list.add(foo);
            }
            s = list.toArray(new String[0]);
        }

        return s;
    }


}
