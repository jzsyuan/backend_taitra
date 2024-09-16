package controllers;

import play.mvc.Controller;

public class InsightController extends Controller {


    /*
    public Result getRecommendMarket(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());

        JsonNode parameter = request.body().asJson();
        if (!parameter.has("type") ||
                !parameter.has("nation_id") ||
                !parameter.has("industry_id")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode dataArray = response.putArray("data");
        ObjectNode node = dataArray.addObject();
        node.put("data_name", "日本");
        node.put("data_value", "4931");
        node.put("other_val", "4431萬美元");
        node.put("other_val2", "9.2%");
        node.put("other_val3", "17.96兆美元");

        return ok(response);
    }

    public Result getRecommendMarketHistory(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());

        JsonNode parameter = request.body().asJson();
        if (!parameter.has("type") ||
                !parameter.has("period") ||
                !parameter.has("nation_ids") ||
                !parameter.has("industry_ids")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        for (JsonNode nationIds : parameter.get("nation_ids")) {
            int nationId = nationIds.asInt();
        }

        for (JsonNode industryIds : parameter.get("industry_ids")) {
            int industryId = industryIds.asInt();
        }

        String type = parameter.get("type").asText();
        String period = parameter.get("period").asText();

        if (!type.equals("I") && !type.equals("O")) {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }

        if (!period.equals("1") && !period.equals("3")) {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode dataArray = response.putArray("data");
        ObjectNode node = dataArray.addObject();
        node.put("data_name", "2024");
        node.put("data_value", "3000");

        ObjectNode node2 = dataArray.addObject();
        node2.put("data_name", "2023");
        node2.put("data_value", "2000");

        ObjectNode node3 = dataArray.addObject();
        node3.put("data_name", "2022");
        node3.put("data_value", "1000");

        return ok(response);
    }

    public Result getFreeMarket(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());

        JsonNode parameter = request.body().asJson();
        if (!parameter.has("type") ||
                !parameter.has("period") ||
                !parameter.has("nation_id") ||
                !parameter.has("industry_id") ||
                !parameter.has("hs_code")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String type = parameter.get("type").asText();
        String period = parameter.get("period").asText();

        if (!type.equals("I") && !type.equals("O")) {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }

        if (!period.equals("1") && !period.equals("3")) {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode dataArray = response.putArray("data");
        ObjectNode node = dataArray.addObject();
        node.put("data_name", "紡織");
        node.put("data_value", "3456");
        node.put("other_val", "3431萬美元");
        node.put("other_val2", "4.4%");
        node.put("other_val3", "10.16兆美元");

        return ok(response);
    }

    public Result getFreeMarketHistory(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());

        JsonNode parameter = request.body().asJson();
        if (!parameter.has("type") ||
                !parameter.has("period") ||
                !parameter.has("nation_id") ||
                !parameter.has("industry_id") ||
                !parameter.has("hs_code")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String type = parameter.get("type").asText();
        String period = parameter.get("period").asText();

        if (!type.equals("I") && !type.equals("O")) {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }

        if (!period.equals("1") && !period.equals("3")) {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode dataArray = response.putArray("data");
        ObjectNode node = dataArray.addObject();
        node.put("data_name", "2024");
        node.put("data_value", "2200");

        ObjectNode node2 = dataArray.addObject();
        node2.put("data_name", "2023");
        node2.put("data_value", "1200");

        ObjectNode node3 = dataArray.addObject();
        node3.put("data_name", "2022");
        node3.put("data_value", "500");

        return ok(response);
    }

    public Result getRegionalMarket(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());

        JsonNode parameter = request.body().asJson();

        int memberId = parameter.has("member_id") ? parameter.get("member_id").asInt() : 0;

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode dataArray = response.putArray("data");
        ObjectNode node = dataArray.addObject();
        node.put("nation_id", "1");
        node.put("nation_name", "日本");

        return ok(response);
    }

    public Result getIndustry(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());

        JsonNode parameter = request.body().asJson();
        int memberId = parameter.has("member_id") ? parameter.get("member_id").asInt() : 0;

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode dataArray = response.putArray("data");
        ObjectNode node = dataArray.addObject();
        node.put("industry_id", "2");
        node.put("industry_name", "紡織");

        return ok(response);
    }
    */
}
