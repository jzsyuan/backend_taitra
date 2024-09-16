package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import datatypes.Error;
import datatypes.InsightType;
import datatypes.MemberLogType;
import models.InsightMemberListBean;
import models.MemberList;
import models.MemberLog;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.MyCache;
import utils.RedisUtils;
import utils.ValidationUtils;

import java.sql.Timestamp;
import java.util.*;


public class AppController extends Controller {
    private final MyCache myCache = MyCache.getInstance();

    public Result heartbeat(Http.Request request) {
        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        response.put("current_time", System.currentTimeMillis());
        response.put("name", "taitra_api_server_1.0");
        response.put("status", "ok");

        return ok(response);
    }


    public Result verify(Http.Request request) {
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            if (error == Error.VERIFY_ERROR) {
                ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
                response.put("status", "fail");
                response.put("verify", ValidationUtils.verifyRequestTest("Test", request) == null ? "" : ValidationUtils.verifyRequestTest("Test", request));
                return internalServerError(response);
            }
            return internalServerError(Json.toJson(error.toErrorMap()));
        }

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        response.put("status", "ok");

        return ok(response);
    }

    public Result updateLog(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        /*
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }
        */
        JsonNode parameter = request.body().asJson();
        Logger.of("application").info("Log : {}", parameter.toString());

        if (!parameter.has("member_id") ||
                !parameter.has("type") ||
                !parameter.has("data")) {
            return internalServerError(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String memberId = parameter.get("member_id").asText();
        String type = parameter.get("type").isNull() ? "" : parameter.get("type").asText();
        JsonNode data = parameter.get("data");

        if (type.isEmpty()) {
            return internalServerError(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }

        MemberLogType memberLogType = MemberLogType.fromOrdinal(type);
        if (memberLogType == memberLogType.NONE) {
            //return internalServerError(Json.toJson(Error.LOG_TYPE_NOT_FOUND.toErrorMap()));
            Logger.of("application").info("Invalid log type : {}", type);
        }

        if (type.endsWith("_id") && data.isArray()) {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (int i = 0; i < data.size(); i++) {
                if (!data.get(i).isObject()) {
                    if (data.get(i).isNull()) {
                        Logger.of("application").info("Invalid log value");
                        return internalServerError(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
                    }
                    String s = data.get(i).asText();
                    array.add(s);
                }
            }
            data = array;
        }

        MemberLog memberLog = new MemberLog();
        memberLog.setMemberId(memberId == null ? "" : memberId);
        memberLog.setType(type);
        memberLog.setMemberLogUpdateDatetime(new Timestamp(System.currentTimeMillis()));
        memberLog.insert(data.toString());

        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.put("member_id", memberId);
        node.put("type", type);
        node.put("ts", System.currentTimeMillis());
        node.put("data", data);
        RedisUtils.savaToRedis(type, node.toString());

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        response.put("status", "ok");
        return ok(response);
    }

    public Result blockId(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        /*
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }
        */
        JsonNode parameter = request.body().asJson();
        if (!parameter.has("id") ||
                !parameter.has("type") ||
                !parameter.has("block")) {
            return internalServerError(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String id = parameter.get("id").asText();
        String type = parameter.get("type").asText();   //exhibition, activity
        String block = parameter.get("block").asText(); //Y/N

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        //Check type
        InsightType insightType = null;
        if (type.equals("exhibition")) {
            insightType = InsightType.EXHIBITION_BLOCK_ID;
        } else if (type.equals("activity")) {
            insightType = InsightType.ACTIVITY_BLOCK_ID;
        } else {
            return internalServerError(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }

        //Put into MEMBER_LIST
        boolean doUpdate = false;
        MemberList memberList = MemberList.findByMemberIdAndType("taitra-backend", insightType.getAction());
        if (memberList == null) {
            memberList = new MemberList();
            memberList.setMemberId("taitra-backend");
            memberList.setType(insightType.getAction());
            memberList.setMemberListUpdateDatetime(timestamp);
        } else {
            memberList.setMemberListUpdateDatetime(timestamp);
            doUpdate = true;
        }

        List<InsightMemberListBean> insightMemberListBeanList = memberList.getContent() == null ? new ArrayList<>() : memberList.getContent();
        if (block.equals("Y")) {
            //ADD id
            Map<String, Integer> addSet = new HashMap<>();
            for (int a = 0; a < insightMemberListBeanList.size(); a++) {
                String getId = null;
                if (type.equals("exhibition")) {
                    getId = insightMemberListBeanList.get(a).getExhibitionId();
                }
                if (type.equals("activity")) {
                    getId = insightMemberListBeanList.get(a).getActivityId();
                }
                addSet.put(getId, a);
            }
            if (!addSet.containsKey(id)) {
                //put add list
                InsightMemberListBean insightMemberListBean = new InsightMemberListBean();
                if (type.equals("exhibition")) {
                    insightMemberListBean.setExhibitionId(id);
                }
                if (type.equals("activity")) {
                    insightMemberListBean.setActivityId(id);
                }
                insightMemberListBeanList.add(insightMemberListBean);
            }
        } else if (block.equals("N")) {
            //DEL id
            Map<String, Integer> delSet = new HashMap<>();
            for (int a = 0; a < insightMemberListBeanList.size(); a++) {
                String getId = null;
                if (type.equals("exhibition")) {
                    getId = insightMemberListBeanList.get(a).getExhibitionId();
                }
                if (type.equals("activity")) {
                    getId = insightMemberListBeanList.get(a).getActivityId();
                }
                delSet.put(getId, a);
            }
            //Do del
            if (delSet.containsKey(id)) {
                //Check is exist
                insightMemberListBeanList.remove(delSet.get(id).intValue());
            }
        } else {
            return internalServerError(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }
        Logger.of("application").debug("Block list:{}", insightMemberListBeanList);
        memberList.setContent(insightMemberListBeanList);

        if (doUpdate) {
            memberList.updateByMemberIdAndType();
        } else {
            memberList.save();
        }

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        response.put("status", "ok");
        return ok(response);
    }

    public Result cleanCache(Http.Request request) {
        //myCache.setRenewFlag(true);
        myCache.clearAllCache();

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        response.put("status", "ok");
        return ok(response);
    }
}
