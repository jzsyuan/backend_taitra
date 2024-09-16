package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import datatypes.Error;
import datatypes.InsightType;
import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.RedisUtils;

import java.sql.Timestamp;
import java.util.*;

public class InsightTrackingController extends Controller {
    public Result getMemberList(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        /*
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }
        */
        JsonNode parameter = request.body().asJson();
        if (!parameter.has("member_id")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String memberId = parameter.get("member_id").asText();

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        try {
            //找到個人的
            MemberList memberList = MemberList.findByMemberIdAndType(memberId, InsightType.TRADE_FAVORITE_ADD_CNTY_HS6.getAction());
            ArrayNode membersArray = response.putArray("member_list");
            if (memberList != null) {
                List<InsightMemberListBean> insightMemberListBeanList = memberList.getContent();
                Collections.reverse(insightMemberListBeanList);
                for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
                    ObjectNode node = membersArray.addObject();
                    node.put("hs_code", insightMemberListBean.getHsCode6());
                    node.put("CNTY", insightMemberListBean.getCntyCode());
                }
            }

            //找到推薦的
            ArrayNode recommendArray = response.putArray("recommend_list");
            Set<String> set = new HashSet<>();
            List<MemberTrade> memberTradeList = MemberTrade.findByMemberIdAndType(memberId, "cntyhsc6");
            MemberList memberDelRecommendList = MemberList.findByMemberIdAndType(memberId, InsightType.TRADE_RECOMMEND_DEL_CNTYHSC6.getAction());
            if (memberDelRecommendList != null) {
                for (InsightMemberListBean insightMemberListBean : memberDelRecommendList.getContent()) {
                    set.add(insightMemberListBean.getHsCode6() + "_" + insightMemberListBean.getCntyCode());
                }
            }

            for (MemberTrade memberTrade : memberTradeList) {
                if (!set.contains(memberTrade.getTradeHscode() + "_" + memberTrade.getTradeCountry())) {
                    ObjectNode node = recommendArray.addObject();
                    node.put("hs_code", memberTrade.getTradeHscode());
                    node.put("CNTY", memberTrade.getTradeCountry());
                }
            }
            //Get default
            ObjectNode defaultNode = response.putObject("default");
            defaultNode.put("hs_code", "848180");
            defaultNode.put("CNTY", "USA");
        } catch (Exception e) {
            Logger.of("application").error("error:{}", e);
        }

        return ok(response);
    }

    public Result updateMemberList(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        /*
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }
        */

        JsonNode parameter = request.body().asJson();
        if (!parameter.has("member_id") ||
                !parameter.has("hs_code") ||
                !parameter.has("CNTY") ||
                !parameter.has("favor_status")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String memberId = parameter.get("member_id").asText();
        String hsCode = parameter.get("hs_code").asText();
        String regionCode = parameter.get("CNTY").asText();
        String favorStatus = parameter.get("favor_status").asText();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        try {
            MemberList memberListADD = MemberList.findByMemberIdAndType(memberId, InsightType.TRADE_FAVORITE_ADD_CNTY_HS6.getAction());
            boolean doADDUpdate = false;
            boolean doDELUpdate = false;
            if (memberListADD == null) {
                memberListADD = new MemberList();
                memberListADD.setMemberId(memberId);
                memberListADD.setType(InsightType.TRADE_FAVORITE_ADD_CNTY_HS6.getAction());
                memberListADD.setMemberListUpdateDatetime(timestamp);
            } else {
                memberListADD.setMemberListUpdateDatetime(timestamp);
                doADDUpdate = true;
            }

            MemberList memberListDEL = MemberList.findByMemberIdAndType(memberId, InsightType.TRADE_FAVORITE_DEL_CNTY_HS6.getAction());
            if (memberListDEL == null) {
                memberListDEL = new MemberList();
                memberListDEL.setMemberId(memberId);
                memberListDEL.setType(InsightType.TRADE_FAVORITE_DEL_CNTY_HS6.getAction());
                memberListDEL.setMemberListUpdateDatetime(timestamp);
            } else {
                memberListDEL.setMemberListUpdateDatetime(timestamp);
                doDELUpdate = true;
            }

            List<InsightMemberListBean> insightMemberListBeanADDList = memberListADD.getContent() == null ? new ArrayList<>() : memberListADD.getContent();
            List<InsightMemberListBean> insightMemberListBeanDELList = memberListDEL.getContent() == null ? new ArrayList<>() : memberListDEL.getContent();
            List<InsightMemberListBean> logBean = new ArrayList<>();
            String action = "";

            if (favorStatus.equals("Y")) {
                Map<String, Integer> addSet = new HashMap<>();

                for (int a = 0; a < insightMemberListBeanADDList.size(); a++) {
                    String hs6 = insightMemberListBeanADDList.get(a).getHsCode6();
                    String cnty = insightMemberListBeanADDList.get(a).getCntyCode();
                    addSet.put(hs6 + "_" + cnty, a);
                }
                if (!addSet.containsKey(hsCode + "_" + regionCode)) {
                    //put add list
                    InsightMemberListBean insightMemberListBean = new InsightMemberListBean();
                    insightMemberListBean.setHsCode6(hsCode);
                    insightMemberListBean.setCntyCode(regionCode);
                    insightMemberListBeanADDList.add(insightMemberListBean);
                    logBean.add(insightMemberListBean);
                }

                //remove del list
                Map<String, Integer> delSet = new HashMap<>();
                for (int a = 0; a < insightMemberListBeanDELList.size(); a++) {
                    String hs6 = insightMemberListBeanDELList.get(a).getHsCode6();
                    String cntyCode = insightMemberListBeanDELList.get(a).getCntyCode();
                    delSet.put(hs6 + "_" + cntyCode, a);
                }

                //Do del
                if (delSet.containsKey(hsCode + "_" + regionCode)) {
                    //Check is exist
                    insightMemberListBeanDELList.remove(delSet.get(hsCode + "_" + regionCode).intValue());
                }

                action = InsightType.TRADE_FAVORITE_ADD_CNTY_HS6.getCode();
            }
            else if (favorStatus.equals("N")) {
                Map<String, Integer> delSet = new HashMap<>();

                for (int a = 0; a < insightMemberListBeanDELList.size(); a++) {
                    String hs6 = insightMemberListBeanDELList.get(a).getHsCode6();
                    String cnty = insightMemberListBeanDELList.get(a).getCntyCode();
                    delSet.put(hs6 + "_" + cnty, a);
                }
                if (!delSet.containsKey(hsCode + "_" + regionCode)) {
                    //put del list
                    InsightMemberListBean insightMemberListBean = new InsightMemberListBean();
                    insightMemberListBean.setHsCode6(hsCode);
                    insightMemberListBean.setCntyCode(regionCode);
                    insightMemberListBeanDELList.add(insightMemberListBean);
                    logBean.add(insightMemberListBean);
                }

                //remove add list
                Map<String, Integer> addSet = new HashMap<>();
                for (int a = 0; a < insightMemberListBeanADDList.size(); a++) {
                    String hs6 = insightMemberListBeanADDList.get(a).getHsCode6();
                    String cntyCode = insightMemberListBeanADDList.get(a).getCntyCode();
                    addSet.put(hs6 + "_" + cntyCode, a);
                }

                //Do add
                if (addSet.containsKey(hsCode + "_" + regionCode)) {
                    //Check is exist
                    insightMemberListBeanADDList.remove(addSet.get(hsCode + "_" + regionCode).intValue());
                }

                action = InsightType.TRADE_FAVORITE_DEL_CNTY_HS6.getCode();
            }
            Logger.of("application").debug("ADD list:{}", insightMemberListBeanADDList);
            Logger.of("application").debug("DEL list:{}", insightMemberListBeanDELList);
            memberListADD.setContent(insightMemberListBeanADDList);
            memberListDEL.setContent(insightMemberListBeanDELList);

            //Write log
            //Push to redis
            /*
            MemberLog memberLog = new MemberLog();
            memberLog.setMemberId(memberId);
            memberLog.setType(action);
            memberLog.setMemberLogUpdateDatetime(timestamp);
            memberLog.setContent(logBean);
            memberLog.save();
            RedisUtils.savaToRedis(action, memberLog.toJson().toString());
            */
            if (doADDUpdate) {
                memberListADD.updateByMemberIdAndType();
            } else {
                memberListADD.save();
            }
            if (doDELUpdate) {
                memberListDEL.updateByMemberIdAndType();
            } else {
                memberListDEL.save();
            }
        } catch (Exception e) {
            Logger.of("application").error("error:{}", e);
        }

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        response.put("status", "ok");
        return ok(response);
    }

    public Result removeRecommend(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        /*
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }
        */

        JsonNode parameter = request.body().asJson();
        if (!parameter.has("member_id") ||
                !parameter.has("hs_code") ||
                !parameter.has("CNTY")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String memberId = parameter.get("member_id").asText();
        String hsCode = parameter.get("hs_code").asText();
        String regionCode = parameter.get("CNTY").asText();
        /*
        MemberRemoveRecommendList memberRemoveRecommendList = MemberRemoveRecommendList.findBy(memberId, "tracking", hsCode, regionCode);
        if (memberRemoveRecommendList == null) {
            memberRemoveRecommendList = new MemberRemoveRecommendList();
            memberRemoveRecommendList.setMemberId(memberId);
            memberRemoveRecommendList.setType("tracking");
            memberRemoveRecommendList.setHscode(hsCode);
            memberRemoveRecommendList.setCnty(regionCode);
            memberRemoveRecommendList.save();
        }
        */
        //New from here
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        try {
            boolean doDELUpdate = false;
            MemberList memberListDEL = MemberList.findByMemberIdAndType(memberId, InsightType.TRADE_RECOMMEND_DEL_CNTYHSC6.getAction());
            if (memberListDEL == null) {
                memberListDEL = new MemberList();
                memberListDEL.setMemberId(memberId);
                memberListDEL.setType(InsightType.TRADE_RECOMMEND_DEL_CNTYHSC6.getAction());
                memberListDEL.setMemberListUpdateDatetime(timestamp);
            } else {
                memberListDEL.setMemberListUpdateDatetime(timestamp);
                doDELUpdate = true;
            }

            List<InsightMemberListBean> insightMemberListBeanDELList = memberListDEL.getContent() == null ? new ArrayList<>() : memberListDEL.getContent();

            Map<String, Integer> delSet = new HashMap<>();

            for (int a = 0; a < insightMemberListBeanDELList.size(); a++) {
                String hs6 = insightMemberListBeanDELList.get(a).getHsCode6();
                String cnty = insightMemberListBeanDELList.get(a).getCntyCode();
                delSet.put(hs6 + "_" + cnty, a);
            }
            if (!delSet.containsKey(hsCode + "_" + regionCode)) {
                //put del list
                InsightMemberListBean insightMemberListBean = new InsightMemberListBean();
                insightMemberListBean.setHsCode6(hsCode);
                insightMemberListBean.setCntyCode(regionCode);
                insightMemberListBeanDELList.add(insightMemberListBean);
            }

            Logger.of("application").debug("DEL list:{}", insightMemberListBeanDELList);
            memberListDEL.setContent(insightMemberListBeanDELList);

            if (doDELUpdate) {
                memberListDEL.updateByMemberIdAndType();
            } else {
                memberListDEL.save();
            }
        } catch (Exception e) {
            Logger.of("application").error("error:{}", e);
        }

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        response.put("status", "ok");
        return ok(response);
    }

    public Result getDefault(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        /*
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }
        */

        JsonNode parameter = request.body().asJson();
        if (!parameter.has("hs6") ||
                !parameter.has("cntyiso3")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String hs6 = parameter.get("hs6").asText();
        String cntyiso3 = parameter.get("cntyiso3").asText();

        if (hs6 == null || hs6.isEmpty() || cntyiso3 == null || cntyiso3.isEmpty()) {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toJson()));
        }

        Date start;
        Date end;
        long duration = 0;
        start = new Date();

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ObjectNode existNode = new ObjectNode(JsonNodeFactory.instance);
        existNode.put("A-1", true);
        existNode.put("A-2", true);
        existNode.put("A-3", true);

        ObjectNode A1Node = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode a1BarArray = A1Node.putArray("bar");
        ArrayNode a1LineArray = A1Node.putArray("line");
        List<OthUnWorldYear> othUnWorldYearList = OthUnWorldYear.findListByHsCodeAndReporterIso3AndAreaId(hs6, null, null);
        Map<String, OthUnWorldYear> a1Map = new HashMap<>();
        for (OthUnWorldYear othUnWorldYear : othUnWorldYearList) {
           String key = othUnWorldYear.getHsCode6() + "_" + othUnWorldYear.getReporterIso3() + "_" + othUnWorldYear.getPartnerIso3();
           if (!a1Map.containsKey(key)) {
               a1Map.put(key, othUnWorldYear);
           } else {
               OthUnWorldYear existOthUnWorldYear = a1Map.get(key);
               int existTxnYear = Integer.valueOf(existOthUnWorldYear.getTxnYear());
               int txnYear = Integer.valueOf(othUnWorldYear.getTxnYear());
               if (existTxnYear <= txnYear) {
                   a1Map.put(key, othUnWorldYear);
               }
           }

            ObjectNode node = a1LineArray.addObject();
            node.put("TRADE_WEIGHT", othUnWorldYear.getTradeWeight());
            node.put("TRADE_AMT", othUnWorldYear.getTradeAmt());
            node.put("TRADE_AMT_DIFFERENCE", othUnWorldYear.getTradeAmtDifference());
            node.put("TRADE_AMT_GROWRATE", othUnWorldYear.getTradeAmtGrowrate());
            node.put("TRADE_AMT_PERCENT", othUnWorldYear.getTradeAmtPercent());
            node.put("WEIGHT_DIFFERENCE", othUnWorldYear.getWeightDifference());
            node.put("WEIGHT_GROWRATE", othUnWorldYear.getWeightGrowrate());
            node.put("WEIGHT_PERCENT", othUnWorldYear.getWeightPercent());
            node.put("TRADE_AMT_RANK", othUnWorldYear.getTradeAmtRank());
            node.put("TRADE_AMT_DIFFERENCE_RANK", othUnWorldYear.getTradeAmtDifferenceRank());
            node.put("TRADE_AMT_GROWRATE_RANK", othUnWorldYear.getTradeAmtGrowrateRank());
            node.put("TRADE_AMT_PERCENT_RANK", othUnWorldYear.getTradeAmtPercentRank());
            node.put("WEIGHT_RANK", othUnWorldYear.getWeightRank());
            node.put("WEIGHT_DIFFERENCE_RANK", othUnWorldYear.getWeightDifferenceRank());
            node.put("WEIGHT_GROWRATE_RANK", othUnWorldYear.getWeightGrowrateRank());
            node.put("WEIGHT_PERCENT_RANK", othUnWorldYear.getWeightPercentRank());
            node.put("PARTNER_ISO3", othUnWorldYear.getPartnerIso3());
            node.put("COUNTRY_COMM_ZH", othUnWorldYear.getCountryCommZh());
            node.put("TXN_YEAR", othUnWorldYear.getTxnYear());
        }
        for (Map.Entry<String, OthUnWorldYear> entry : a1Map.entrySet()) {
            OthUnWorldYear othUnWorldYear = entry.getValue();
            ObjectNode node = a1BarArray.addObject();
            node.put("TRADE_WEIGHT", othUnWorldYear.getTradeWeight());
            node.put("TRADE_AMT", othUnWorldYear.getTradeAmt());
            node.put("TRADE_AMT_DIFFERENCE", othUnWorldYear.getTradeAmtDifference());
            node.put("TRADE_AMT_GROWRATE", othUnWorldYear.getTradeAmtGrowrate());
            node.put("TRADE_AMT_PERCENT", othUnWorldYear.getTradeAmtPercent());
            node.put("WEIGHT_DIFFERENCE", othUnWorldYear.getWeightDifference());
            node.put("WEIGHT_GROWRATE", othUnWorldYear.getWeightGrowrate());
            node.put("WEIGHT_PERCENT", othUnWorldYear.getWeightPercent());
            node.put("TRADE_AMT_RANK", othUnWorldYear.getTradeAmtRank());
            node.put("TRADE_AMT_DIFFERENCE_RANK", othUnWorldYear.getTradeAmtDifferenceRank());
            node.put("TRADE_AMT_GROWRATE_RANK", othUnWorldYear.getTradeAmtGrowrateRank());
            node.put("WEIGHT_RANK", othUnWorldYear.getWeightRank());
            node.put("WEIGHT_DIFFERENCE_RANK", othUnWorldYear.getWeightDifferenceRank());
            node.put("WEIGHT_GROWRATE_RANK", othUnWorldYear.getWeightGrowrateRank());
            node.put("REPORTER_ISO3", othUnWorldYear.getReporterIso3());
            node.put("COUNTRY_COMM_ZH", othUnWorldYear.getCountryCommZh());
            node.put("TXN_YEAR", othUnWorldYear.getTxnYear());
        }
        if (othUnWorldYearList.size() == 0) {
            existNode.put("A-1", false);
        }
        //A-0-1
        for (Map.Entry<String, OthUnWorldYear> entry : a1Map.entrySet()) {
            OthUnWorldYear othUnWorldYear = entry.getValue();
            if (othUnWorldYear.getReporterIso3().equals(cntyiso3)) {
                response.put("A-0-1", othUnWorldYear.getTradeAmtRank());
                break;
            }
        }
        a1Map = null;
        othUnWorldYearList = null;

        ObjectNode A2Node = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode a2BarArray = A2Node.putArray("bar");
        ArrayNode a2LineArray = A2Node.putArray("line");
        List<OthUnTwnYear> othUnTwnYearList = OthUnTwnYear.findListByHsCodeAndReporterIso3AndAreaId(hs6, null, null);
        Map<String, OthUnTwnYear> a2Map = new HashMap<>();
        for (OthUnTwnYear othUnTwnYear : othUnTwnYearList) {
            String key = othUnTwnYear.getHsCode6() + "_" + othUnTwnYear.getReporterIso3() + "_" + othUnTwnYear.getPartnerIso3();
            if (!a2Map.containsKey(key)) {
                a2Map.put(key, othUnTwnYear);
            } else {
                OthUnTwnYear existOthUnTwnYear = a2Map.get(key);
                int existTxnYear = Integer.valueOf(existOthUnTwnYear.getTxnYear());
                int txnYear = Integer.valueOf(othUnTwnYear.getTxnYear());
                if (existTxnYear <= txnYear) {
                    a2Map.put(key, othUnTwnYear);
                }
            }

            ObjectNode node = a2LineArray.addObject();
            node.put("TRADE_WEIGHT", othUnTwnYear.getTradeWeight());
            node.put("TRADE_AMT", othUnTwnYear.getTradeAmt());
            node.put("TRADE_AMT_DIFFERENCE", othUnTwnYear.getTradeAmtDifference());
            node.put("TRADE_AMT_GROWRATE", othUnTwnYear.getTradeAmtGrowrate());
            node.put("TRADE_AMT_PERCENT", othUnTwnYear.getTradeAmtPercent());
            node.put("WEIGHT_DIFFERENCE", othUnTwnYear.getWeightDifference());
            node.put("WEIGHT_GROWRATE", othUnTwnYear.getWeightGrowrate());
            node.put("WEIGHT_PERCENT", othUnTwnYear.getWeightPercent());
            node.put("TRADE_AMT_RANK", othUnTwnYear.getTradeAmtRank());
            node.put("TRADE_AMT_DIFFERENCE_RANK", othUnTwnYear.getTradeAmtDifferenceRank());
            node.put("TRADE_AMT_GROWRATE_RANK", othUnTwnYear.getTradeAmtGrowrateRank());
            node.put("TRADE_AMT_PERCENT_RANK", othUnTwnYear.getTradeAmtPercentRank());
            node.put("WEIGHT_RANK", othUnTwnYear.getWeightRank());
            node.put("WEIGHT_DIFFERENCE_RANK", othUnTwnYear.getWeightDifferenceRank());
            node.put("WEIGHT_GROWRATE_RANK", othUnTwnYear.getWeightGrowrateRank());
            node.put("WEIGHT_PERCENT_RANK", othUnTwnYear.getWeightPercentRank());
            node.put("PARTNER_ISO3", othUnTwnYear.getPartnerIso3());
            node.put("COUNTRY_COMM_ZH", othUnTwnYear.getCountryCommZh());
            node.put("TXN_YEAR", othUnTwnYear.getTxnYear());
            node.put("TARIFF", othUnTwnYear.getTariff());
        }
        for (Map.Entry<String, OthUnTwnYear> entry : a2Map.entrySet()) {
            OthUnTwnYear othUnTwnYear = entry.getValue();
            ObjectNode node = a2BarArray.addObject();
            node.put("TRADE_WEIGHT", othUnTwnYear.getTradeWeight());
            node.put("TRADE_AMT", othUnTwnYear.getTradeAmt());
            node.put("TRADE_AMT_DIFFERENCE", othUnTwnYear.getTradeAmtDifference());
            node.put("TRADE_AMT_GROWRATE", othUnTwnYear.getTradeAmtGrowrate());
            node.put("TRADE_AMT_PERCENT", othUnTwnYear.getTradeAmtPercent());
            node.put("WEIGHT_DIFFERENCE", othUnTwnYear.getWeightDifference());
            node.put("WEIGHT_GROWRATE", othUnTwnYear.getWeightGrowrate());
            node.put("WEIGHT_PERCENT", othUnTwnYear.getWeightPercent());
            node.put("TRADE_AMT_RANK", othUnTwnYear.getTradeAmtRank());
            node.put("TRADE_AMT_DIFFERENCE_RANK", othUnTwnYear.getTradeAmtDifferenceRank());
            node.put("TRADE_AMT_GROWRATE_RANK", othUnTwnYear.getTradeAmtGrowrateRank());
            node.put("WEIGHT_RANK", othUnTwnYear.getWeightRank());
            node.put("WEIGHT_DIFFERENCE_RANK", othUnTwnYear.getWeightDifferenceRank());
            node.put("WEIGHT_GROWRATE_RANK", othUnTwnYear.getWeightGrowrateRank());
            node.put("REPORTER_ISO3", othUnTwnYear.getReporterIso3());
            node.put("COUNTRY_COMM_ZH", othUnTwnYear.getCountryCommZh());
            node.put("TXN_YEAR", othUnTwnYear.getTxnYear());
            node.put("TARIFF", othUnTwnYear.getTariff());
        }
        if (othUnTwnYearList.size() == 0) {
            existNode.put("A-2", false);
        }

        //A-0-2
        for (Map.Entry<String, OthUnTwnYear> entry : a2Map.entrySet()) {
            OthUnTwnYear othUnTwnYear = entry.getValue();
            if (othUnTwnYear.getReporterIso3().equals(cntyiso3)) {
                response.put("A-0-2", othUnTwnYear.getTradeAmtRank());
                break;
            }
        }
        a2Map = null;
        othUnTwnYearList = null;

        ObjectNode A3Node = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode a3BarArray = A3Node.putArray("bar");
        ArrayNode a3LineArray = A3Node.putArray("line");
        List<UnNonWorldYear> unNonWorldYearList = UnNonWorldYear.findListByHsCodeAndReporterIso3AndAreaId(hs6, cntyiso3, null);
        Map<String, UnNonWorldYear> a3Map = new HashMap<>();
        for (UnNonWorldYear unNonWorldYear : unNonWorldYearList) {
            String key = unNonWorldYear.getHsCode6() + "_" + unNonWorldYear.getReporterIso3() + "_" + unNonWorldYear.getPartnerIso3();
            if (!a3Map.containsKey(key)) {
                a3Map.put(key, unNonWorldYear);
            } else {
                UnNonWorldYear existUnNonWorldYear = a3Map.get(key);
                int existTxnYear = Integer.valueOf(existUnNonWorldYear.getTxnYear());
                int txnYear = Integer.valueOf(unNonWorldYear.getTxnYear());
                if (existTxnYear <= txnYear) {
                    a3Map.put(key, unNonWorldYear);
                }
            }

            ObjectNode node = a3LineArray.addObject();
            node.put("TRADE_WEIGHT", unNonWorldYear.getTradeWeight());
            node.put("TRADE_AMT", unNonWorldYear.getTradeAmt());
            node.put("TRADE_AMT_DIFFERENCE", unNonWorldYear.getTradeAmtDifference());
            node.put("TRADE_AMT_GROWRATE", unNonWorldYear.getTradeAmtGrowrate());
            node.put("TRADE_AMT_PERCENT", unNonWorldYear.getTradeAmtPercent());
            node.put("WEIGHT_DIFFERENCE", unNonWorldYear.getWeightDifference());
            node.put("WEIGHT_GROWRATE", unNonWorldYear.getWeightGrowrate());
            node.put("WEIGHT_PERCENT", unNonWorldYear.getWeightPercent());
            node.put("TRADE_AMT_RANK", unNonWorldYear.getTradeAmtRank());
            node.put("TRADE_AMT_DIFFERENCE_RANK", unNonWorldYear.getTradeAmtDifferenceRank());
            node.put("TRADE_AMT_GROWRATE_RANK", unNonWorldYear.getTradeAmtGrowrateRank());
            node.put("TRADE_AMT_PERCENT_RANK", unNonWorldYear.getTradeAmtPercentRank());
            node.put("WEIGHT_RANK", unNonWorldYear.getWeightRank());
            node.put("WEIGHT_DIFFERENCE_RANK", unNonWorldYear.getWeightDifferenceRank());
            node.put("WEIGHT_GROWRATE_RANK", unNonWorldYear.getWeightGrowrateRank());
            node.put("WEIGHT_PERCENT_RANK", unNonWorldYear.getWeightPercentRank());
            node.put("PARTNER_ISO3", unNonWorldYear.getPartnerIso3());
            node.put("COUNTRY_COMM_ZH", unNonWorldYear.getCountryCommZh());
            node.put("TXN_YEAR", unNonWorldYear.getTxnYear());
            node.put("TARIFF", unNonWorldYear.getTariff());
        }
        List<UnNonWorldYear> a3BarList = new ArrayList<>();
        for (Map.Entry<String, UnNonWorldYear> entry : a3Map.entrySet()) {
            UnNonWorldYear unNonWorldYear = entry.getValue();
            ObjectNode node = a3BarArray.addObject();
            node.put("TRADE_WEIGHT", unNonWorldYear.getTradeWeight());
            node.put("TRADE_AMT", unNonWorldYear.getTradeAmt());
            node.put("TRADE_AMT_DIFFERENCE", unNonWorldYear.getTradeAmtDifference());
            node.put("TRADE_AMT_GROWRATE", unNonWorldYear.getTradeAmtGrowrate());
            node.put("TRADE_AMT_PERCENT", unNonWorldYear.getTradeAmtPercent());
            node.put("WEIGHT_DIFFERENCE", unNonWorldYear.getWeightDifference());
            node.put("WEIGHT_GROWRATE", unNonWorldYear.getWeightGrowrate());
            node.put("WEIGHT_PERCENT", unNonWorldYear.getWeightPercent());
            node.put("TRADE_AMT_RANK", unNonWorldYear.getTradeAmtRank());
            node.put("TRADE_AMT_DIFFERENCE_RANK", unNonWorldYear.getTradeAmtDifferenceRank());
            node.put("TRADE_AMT_GROWRATE_RANK", unNonWorldYear.getTradeAmtGrowrateRank());
            node.put("WEIGHT_RANK", unNonWorldYear.getWeightRank());
            node.put("WEIGHT_DIFFERENCE_RANK", unNonWorldYear.getWeightDifferenceRank());
            node.put("WEIGHT_GROWRATE_RANK", unNonWorldYear.getWeightGrowrateRank());
            node.put("REPORTER_ISO3", unNonWorldYear.getReporterIso3());
            node.put("COUNTRY_COMM_ZH", unNonWorldYear.getCountryCommZh());
            node.put("TXN_YEAR", unNonWorldYear.getTxnYear());
            node.put("PARTNER_ISO3", unNonWorldYear.getPartnerIso3());
            node.put("TARIFF", unNonWorldYear.getTariff());

            a3BarList.add(unNonWorldYear);
        }
        if (unNonWorldYearList.size() == 0) {
            existNode.put("A-3", false);
        }
        //unNonWorldYearList = null;

        //A-0-3
        ArrayNode a0_3Array = response.putArray("A-0-3");
        List<String> a03List = Arrays.asList(new String[3]);
        for (UnNonWorldYear unNonWorldYear : a3BarList) {
            if (unNonWorldYear.getTradeAmtRank() != null && unNonWorldYear.getTradeAmtRank() <= 3) {
                a03List.set(unNonWorldYear.getTradeAmtRank() - 1, unNonWorldYear.getPartnerIso3());
            }
        }
        for (String s : a03List) {
            if (s != null) {
                a0_3Array.add(s);
            }
        }
        response.put("A-0-3", a0_3Array);
        //a3BarList = null;

        //A-0-4
        //A-0-5
        for (Map.Entry<String, UnNonWorldYear> entry : a3Map.entrySet()) {
            UnNonWorldYear unNonWorldYear = entry.getValue();
            if (unNonWorldYear.getPartnerIso3().equals("TWN")) {
                response.put("A-0-4", unNonWorldYear.getTradeAmtRank());
                response.put("A-0-5", unNonWorldYear.getTradeAmtPercent());
            }
        }
        a3Map = null;

        //A-3-1
        //ArrayNode a3_1Array = response.putArray("A-3-1");
        //response.put("A-3-1", a3_1Array);

        end = new Date();
        duration = end.getTime() - start.getTime();
        Logger.of("application").info("A0 duration:{}", duration);

        response.put("exist", existNode);
        response.put("A-1", A1Node);
        response.put("A-2", A2Node);
        response.put("A-3", A3Node);
        return ok(response);
    }

    public Result getA1(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        /*
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }
        */
        JsonNode parameter = request.body().asJson();
        if (!parameter.has("hs6") ||
                !parameter.has("timeunit") ||
                !parameter.has("area")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String hs6 = parameter.get("hs6").asText();
        String timeunit = parameter.get("timeunit").asText();
        //Boolean latest = parameter.get("latest").asBoolean();
        String area = parameter.get("area").isNull() ? null : parameter.get("area").asText();

        if (hs6 == null || hs6.isEmpty()) {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toJson()));
        }

        Date start;
        Date end;
        long duration = 0;
        start = new Date();

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ObjectNode A1Node = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode a1BarArray = A1Node.putArray("bar");
        ArrayNode a1LineArray = A1Node.putArray("line");

        if (timeunit.equals("month")) {
            List<OthUnWorldMonth> othUnWorldMonthList = OthUnWorldMonth.findListByHsCodeAndReporterIso3AndAreaId(hs6, null, area);
            Map<String, OthUnWorldMonth> a1Map = new HashMap<>();
            for (OthUnWorldMonth othUnWorldMonth : othUnWorldMonthList) {
                String key = othUnWorldMonth.getHsCode6() + "_" + othUnWorldMonth.getReporterIso3() + "_" + othUnWorldMonth.getPartnerIso3();
                if (!a1Map.containsKey(key)) {
                    a1Map.put(key, othUnWorldMonth);
                } else {
                    OthUnWorldMonth existOthUnWorldYear = a1Map.get(key);
                    int existTxnYtm = Integer.valueOf(existOthUnWorldYear.getTxnYTM());
                    int txnYtm = Integer.valueOf(othUnWorldMonth.getTxnYTM());
                    if (existTxnYtm <= txnYtm) {
                        a1Map.put(key, othUnWorldMonth);
                    }
                }

                ObjectNode node = a1LineArray.addObject();
                node.put("TRADE_WEIGHT", othUnWorldMonth.getTradeWeight());
                node.put("TRADE_AMT", othUnWorldMonth.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", othUnWorldMonth.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", othUnWorldMonth.getTradeAmtGrowrate());
                node.put("TRADE_AMT_PERCENT", othUnWorldMonth.getTradeAmtPercent());
                node.put("WEIGHT_DIFFERENCE", othUnWorldMonth.getWeightDifference());
                node.put("WEIGHT_GROWRATE", othUnWorldMonth.getWeightGrowrate());
                node.put("WEIGHT_PERCENT", othUnWorldMonth.getWeightPercent());
                node.put("TRADE_AMT_RANK", othUnWorldMonth.getTradeAmtRank());
                node.put("TRADE_AMT_DIFFERENCE_RANK", othUnWorldMonth.getTradeAmtDifferenceRank());
                node.put("TRADE_AMT_GROWRATE_RANK", othUnWorldMonth.getTradeAmtGrowrateRank());
                node.put("TRADE_AMT_PERCENT_RANK", othUnWorldMonth.getTradeAmtPercentRank());
                node.put("WEIGHT_RANK", othUnWorldMonth.getWeightRank());
                node.put("WEIGHT_DIFFERENCE_RANK", othUnWorldMonth.getWeightDifferenceRank());
                node.put("WEIGHT_GROWRATE_RANK", othUnWorldMonth.getWeightGrowrateRank());
                node.put("WEIGHT_PERCENT_RANK", othUnWorldMonth.getWeightPercentRank());

                node.put("TRADE_AMT_AREARANK", othUnWorldMonth.getTradeAmtArearank());
                node.put("TRADE_AMT_DIFFERENCE_AREARANK", othUnWorldMonth.getTradeAmtDifferenceArearank());
                node.put("TRADE_AMT_GROWRATE_AREARANK", othUnWorldMonth.getTradeAmtGrowrateArearank());
                node.put("WEIGHT_AREARANK", othUnWorldMonth.getWeightArearank());
                node.put("WEIGHT_DIFFERENCE_AREARANK", othUnWorldMonth.getWeightDifferenceArearank());
                node.put("WEIGHT_GROWRATE_AREARANK", othUnWorldMonth.getWeightGrowrateArearank());

                node.put("PARTNER_ISO3", othUnWorldMonth.getPartnerIso3());
                node.put("COUNTRY_COMM_ZH", othUnWorldMonth.getCountryCommZh());
                node.put("TXN_YTM", othUnWorldMonth.getTxnYTM());
                //node.put("TARIFF", 3);
            }
            for (Map.Entry<String, OthUnWorldMonth> entry : a1Map.entrySet()) {
                OthUnWorldMonth othUnWorldMonth = entry.getValue();
                ObjectNode node = a1BarArray.addObject();
                node.put("TRADE_WEIGHT", othUnWorldMonth.getTradeWeight());
                node.put("TRADE_AMT", othUnWorldMonth.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", othUnWorldMonth.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", othUnWorldMonth.getTradeAmtGrowrate());
                node.put("WEIGHT_DIFFERENCE", othUnWorldMonth.getWeightDifference());
                node.put("WEIGHT_GROWRATE", othUnWorldMonth.getWeightGrowrate());
                node.put("TRADE_AMT_RANK", othUnWorldMonth.getTradeAmtRank());
                node.put("TRADE_AMT_DIFFERENCE_RANK", othUnWorldMonth.getTradeAmtDifferenceRank());
                node.put("TRADE_AMT_GROWRATE_RANK", othUnWorldMonth.getTradeAmtGrowrateRank());
                node.put("WEIGHT_RANK", othUnWorldMonth.getWeightRank());
                node.put("WEIGHT_DIFFERENCE_RANK", othUnWorldMonth.getWeightDifferenceRank());
                node.put("WEIGHT_GROWRATE_RANK", othUnWorldMonth.getWeightGrowrateRank());

                node.put("TRADE_AMT_AREARANK", othUnWorldMonth.getTradeAmtArearank());
                node.put("TRADE_AMT_DIFFERENCE_AREARANK", othUnWorldMonth.getTradeAmtDifferenceArearank());
                node.put("TRADE_AMT_GROWRATE_AREARANK", othUnWorldMonth.getTradeAmtGrowrateArearank());
                node.put("WEIGHT_AREARANK", othUnWorldMonth.getWeightArearank());
                node.put("WEIGHT_DIFFERENCE_AREARANK", othUnWorldMonth.getWeightDifferenceArearank());
                node.put("WEIGHT_GROWRATE_AREARANK", othUnWorldMonth.getWeightGrowrateArearank());

                node.put("REPORTER_ISO3", othUnWorldMonth.getReporterIso3());
                node.put("COUNTRY_COMM_ZH", othUnWorldMonth.getCountryCommZh());
                node.put("TXN_YTM", othUnWorldMonth.getTxnYTM());
                //node.put("TARIFF", 3);
            }
        } else if (timeunit.equals("year")) {
            List<OthUnWorldYear> othUnWorldYearList = OthUnWorldYear.findListByHsCodeAndReporterIso3AndAreaId(hs6, null, area);
            Map<String, OthUnWorldYear> a1Map = new HashMap<>();
            for (OthUnWorldYear othUnWorldYear : othUnWorldYearList) {
                String key = othUnWorldYear.getHsCode6() + "_" + othUnWorldYear.getReporterIso3() + "_" + othUnWorldYear.getPartnerIso3();
                if (!a1Map.containsKey(key)) {
                    a1Map.put(key, othUnWorldYear);
                } else {
                    OthUnWorldYear existOthUnWorldYear = a1Map.get(key);
                    int existTxnYear = Integer.valueOf(existOthUnWorldYear.getTxnYear());
                    int txnYear = Integer.valueOf(othUnWorldYear.getTxnYear());
                    if (existTxnYear <= txnYear) {
                        a1Map.put(key, othUnWorldYear);
                    }
                }

                ObjectNode node = a1LineArray.addObject();
                node.put("TRADE_WEIGHT", othUnWorldYear.getTradeWeight());
                node.put("TRADE_AMT", othUnWorldYear.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", othUnWorldYear.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", othUnWorldYear.getTradeAmtGrowrate());
                node.put("TRADE_AMT_PERCENT", othUnWorldYear.getTradeAmtPercent());
                node.put("WEIGHT_DIFFERENCE", othUnWorldYear.getWeightDifference());
                node.put("WEIGHT_GROWRATE", othUnWorldYear.getWeightGrowrate());
                node.put("WEIGHT_PERCENT", othUnWorldYear.getWeightPercent());
                node.put("TRADE_AMT_RANK", othUnWorldYear.getTradeAmtRank());
                node.put("TRADE_AMT_DIFFERENCE_RANK", othUnWorldYear.getTradeAmtDifferenceRank());
                node.put("TRADE_AMT_GROWRATE_RANK", othUnWorldYear.getTradeAmtGrowrateRank());
                node.put("TRADE_AMT_PERCENT_RANK", othUnWorldYear.getTradeAmtPercentRank());
                node.put("WEIGHT_RANK", othUnWorldYear.getWeightRank());
                node.put("WEIGHT_DIFFERENCE_RANK", othUnWorldYear.getWeightDifferenceRank());
                node.put("WEIGHT_GROWRATE_RANK", othUnWorldYear.getWeightGrowrateRank());
                node.put("WEIGHT_PERCENT_RANK", othUnWorldYear.getWeightPercentRank());

                node.put("TRADE_AMT_AREARANK", othUnWorldYear.getTradeAmtArearank());
                node.put("TRADE_AMT_DIFFERENCE_AREARANK", othUnWorldYear.getTradeAmtDifferenceArearank());
                node.put("TRADE_AMT_GROWRATE_AREARANK", othUnWorldYear.getTradeAmtGrowrateArearank());
                node.put("WEIGHT_AREARANK", othUnWorldYear.getWeightArearank());
                node.put("WEIGHT_DIFFERENCE_AREARANK", othUnWorldYear.getWeightDifferenceArearank());
                node.put("WEIGHT_GROWRATE_AREARANK", othUnWorldYear.getWeightGrowrateArearank());

                node.put("PARTNER_ISO3", othUnWorldYear.getPartnerIso3());
                node.put("COUNTRY_COMM_ZH", othUnWorldYear.getCountryCommZh());
                node.put("TXN_YEAR", othUnWorldYear.getTxnYear());
                //node.put("TARIFF", 3);
            }
            for (Map.Entry<String, OthUnWorldYear> entry : a1Map.entrySet()) {
                OthUnWorldYear othUnWorldYear = entry.getValue();
                ObjectNode node = a1BarArray.addObject();
                node.put("TRADE_WEIGHT", othUnWorldYear.getTradeWeight());
                node.put("TRADE_AMT", othUnWorldYear.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", othUnWorldYear.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", othUnWorldYear.getTradeAmtGrowrate());
                node.put("WEIGHT_DIFFERENCE", othUnWorldYear.getWeightDifference());
                node.put("WEIGHT_GROWRATE", othUnWorldYear.getWeightGrowrate());
                node.put("TRADE_AMT_RANK", othUnWorldYear.getTradeAmtRank());
                node.put("TRADE_AMT_DIFFERENCE_RANK", othUnWorldYear.getTradeAmtDifferenceRank());
                node.put("TRADE_AMT_GROWRATE_RANK", othUnWorldYear.getTradeAmtGrowrateRank());
                node.put("WEIGHT_RANK", othUnWorldYear.getWeightRank());
                node.put("WEIGHT_DIFFERENCE_RANK", othUnWorldYear.getWeightDifferenceRank());
                node.put("WEIGHT_GROWRATE_RANK", othUnWorldYear.getWeightGrowrateRank());

                node.put("TRADE_AMT_AREARANK", othUnWorldYear.getTradeAmtArearank());
                node.put("TRADE_AMT_DIFFERENCE_AREARANK", othUnWorldYear.getTradeAmtDifferenceArearank());
                node.put("TRADE_AMT_GROWRATE_AREARANK", othUnWorldYear.getTradeAmtGrowrateArearank());
                node.put("WEIGHT_AREARANK", othUnWorldYear.getWeightArearank());
                node.put("WEIGHT_DIFFERENCE_AREARANK", othUnWorldYear.getWeightDifferenceArearank());
                node.put("WEIGHT_GROWRATE_AREARANK", othUnWorldYear.getWeightGrowrateArearank());

                node.put("REPORTER_ISO3", othUnWorldYear.getReporterIso3());
                node.put("COUNTRY_COMM_ZH", othUnWorldYear.getCountryCommZh());
                node.put("TXN_YEAR", othUnWorldYear.getTxnYear());
                //node.put("TARIFF", 3);
            }
        } else {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }

        end = new Date();
        duration = end.getTime() - start.getTime();
        Logger.of("application").info("A1 duration:{}", duration);

        response.put("A-1", A1Node);
        return ok(response);
    }

    public Result getA2(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        /*
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }
        */
        JsonNode parameter = request.body().asJson();
        if (!parameter.has("hs6") ||
                !parameter.has("timeunit") ||
                !parameter.has("area")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String hs6 = parameter.get("hs6").asText();
        String timeunit = parameter.get("timeunit").asText();
        String area = parameter.get("area").isNull() ? null : parameter.get("area").asText();

        if (hs6 == null || hs6.isEmpty()) {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toJson()));
        }

        Date start;
        Date end;
        long duration = 0;
        start = new Date();

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ObjectNode A2Node = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode a2BarArray = A2Node.putArray("bar");
        ArrayNode a2LineArray = A2Node.putArray("line");

        if (timeunit.equals("month")) {
            List<OthUnTwnMonth> othUnTwnMonthList = OthUnTwnMonth.findListByHsCodeAndReporterIso3AndAreaId(hs6, null, area);
            Map<String, OthUnTwnMonth> a2Map = new HashMap<>();
            for (OthUnTwnMonth othUnTwnMonth : othUnTwnMonthList) {
                String key = othUnTwnMonth.getHsCode6() + "_" + othUnTwnMonth.getReporterIso3() + "_" + othUnTwnMonth.getPartnerIso3();
                if (!a2Map.containsKey(key)) {
                    a2Map.put(key, othUnTwnMonth);
                } else {
                    OthUnTwnMonth existOthUnTwnMonth = a2Map.get(key);
                    int existTxnMonth = Integer.valueOf(existOthUnTwnMonth.getTxnYTM());
                    int txnMonth = Integer.valueOf(othUnTwnMonth.getTxnYTM());
                    if (existTxnMonth <= txnMonth) {
                        a2Map.put(key, othUnTwnMonth);
                    }
                }

                ObjectNode node = a2LineArray.addObject();
                node.put("TRADE_WEIGHT", othUnTwnMonth.getTradeWeight());
                node.put("TRADE_AMT", othUnTwnMonth.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", othUnTwnMonth.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", othUnTwnMonth.getTradeAmtGrowrate());
                node.put("TRADE_AMT_PERCENT", othUnTwnMonth.getTradeAmtPercent());
                node.put("WEIGHT_DIFFERENCE", othUnTwnMonth.getWeightDifference());
                node.put("WEIGHT_GROWRATE", othUnTwnMonth.getWeightGrowrate());
                node.put("WEIGHT_PERCENT", othUnTwnMonth.getWeightPercent());
                node.put("TRADE_AMT_RANK", othUnTwnMonth.getTradeAmtRank());
                node.put("TRADE_AMT_DIFFERENCE_RANK", othUnTwnMonth.getTradeAmtDifferenceRank());
                node.put("TRADE_AMT_GROWRATE_RANK", othUnTwnMonth.getTradeAmtGrowrateRank());
                node.put("TRADE_AMT_PERCENT_RANK", othUnTwnMonth.getTradeAmtPercentRank());
                node.put("WEIGHT_RANK", othUnTwnMonth.getWeightRank());
                node.put("WEIGHT_DIFFERENCE_RANK", othUnTwnMonth.getWeightDifferenceRank());
                node.put("WEIGHT_GROWRATE_RANK", othUnTwnMonth.getWeightGrowrateRank());
                node.put("WEIGHT_PERCENT_RANK", othUnTwnMonth.getWeightPercentRank());

                node.put("TRADE_AMT_AREARANK", othUnTwnMonth.getTradeAmtArearank());
                node.put("TRADE_AMT_DIFFERENCE_AREARANK", othUnTwnMonth.getTradeAmtDifferenceArearank());
                node.put("TRADE_AMT_GROWRATE_AREARANK", othUnTwnMonth.getTradeAmtGrowrateArearank());
                node.put("WEIGHT_AREARANK", othUnTwnMonth.getWeightArearank());
                node.put("WEIGHT_DIFFERENCE_AREARANK", othUnTwnMonth.getWeightDifferenceArearank());
                node.put("WEIGHT_GROWRATE_AREARANK", othUnTwnMonth.getWeightGrowrateArearank());

                node.put("PARTNER_ISO3", othUnTwnMonth.getPartnerIso3());
                node.put("COUNTRY_COMM_ZH", othUnTwnMonth.getCountryCommZh());
                node.put("TXN_YTM", othUnTwnMonth.getTxnYTM());
                node.put("TARIFF", othUnTwnMonth.getTariff());
            }
            for (Map.Entry<String, OthUnTwnMonth> entry : a2Map.entrySet()) {
                OthUnTwnMonth othUnTwnMonth = entry.getValue();
                ObjectNode node = a2BarArray.addObject();
                node.put("TRADE_WEIGHT", othUnTwnMonth.getTradeWeight());
                node.put("TRADE_AMT", othUnTwnMonth.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", othUnTwnMonth.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", othUnTwnMonth.getTradeAmtGrowrate());
                node.put("TRADE_AMT_PERCENT", othUnTwnMonth.getTradeAmtPercent());
                node.put("WEIGHT_DIFFERENCE", othUnTwnMonth.getWeightDifference());
                node.put("WEIGHT_GROWRATE", othUnTwnMonth.getWeightGrowrate());
                node.put("WEIGHT_PERCENT", othUnTwnMonth.getWeightPercent());
                node.put("TRADE_AMT_RANK", othUnTwnMonth.getTradeAmtRank());
                node.put("TRADE_AMT_DIFFERENCE_RANK", othUnTwnMonth.getTradeAmtDifferenceRank());
                node.put("TRADE_AMT_GROWRATE_RANK", othUnTwnMonth.getTradeAmtGrowrateRank());
                node.put("WEIGHT_RANK", othUnTwnMonth.getWeightRank());
                node.put("WEIGHT_DIFFERENCE_RANK", othUnTwnMonth.getWeightDifferenceRank());
                node.put("WEIGHT_GROWRATE_RANK", othUnTwnMonth.getWeightGrowrateRank());

                node.put("TRADE_AMT_AREARANK", othUnTwnMonth.getTradeAmtArearank());
                node.put("TRADE_AMT_DIFFERENCE_AREARANK", othUnTwnMonth.getTradeAmtDifferenceArearank());
                node.put("TRADE_AMT_GROWRATE_AREARANK", othUnTwnMonth.getTradeAmtGrowrateArearank());
                node.put("WEIGHT_AREARANK", othUnTwnMonth.getWeightArearank());
                node.put("WEIGHT_DIFFERENCE_AREARANK", othUnTwnMonth.getWeightDifferenceArearank());
                node.put("WEIGHT_GROWRATE_AREARANK", othUnTwnMonth.getWeightGrowrateArearank());

                node.put("REPORTER_ISO3", othUnTwnMonth.getReporterIso3());
                node.put("COUNTRY_COMM_ZH", othUnTwnMonth.getCountryCommZh());
                node.put("TXN_YTM", othUnTwnMonth.getTxnYTM());
                node.put("TARIFF", othUnTwnMonth.getTariff());
            }
        } else if (timeunit.equals("year")) {
            List<OthUnTwnYear> othUnTwnYearList = OthUnTwnYear.findListByHsCodeAndReporterIso3AndAreaId(hs6, null, area);
            Map<String, OthUnTwnYear> a2Map = new HashMap<>();
            for (OthUnTwnYear othUnTwnYear : othUnTwnYearList) {
                String key = othUnTwnYear.getHsCode6() + "_" + othUnTwnYear.getReporterIso3() + "_" + othUnTwnYear.getPartnerIso3();
                if (!a2Map.containsKey(key)) {
                    a2Map.put(key, othUnTwnYear);
                } else {
                    OthUnTwnYear existOthUnTwnYear = a2Map.get(key);
                    int existTxnYear = Integer.valueOf(existOthUnTwnYear.getTxnYear());
                    int txnYear = Integer.valueOf(othUnTwnYear.getTxnYear());
                    if (existTxnYear <= txnYear) {
                        a2Map.put(key, othUnTwnYear);
                    }
                }

                ObjectNode node = a2LineArray.addObject();
                node.put("TRADE_WEIGHT", othUnTwnYear.getTradeWeight());
                node.put("TRADE_AMT", othUnTwnYear.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", othUnTwnYear.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", othUnTwnYear.getTradeAmtGrowrate());
                node.put("TRADE_AMT_PERCENT", othUnTwnYear.getTradeAmtPercent());
                node.put("WEIGHT_DIFFERENCE", othUnTwnYear.getWeightDifference());
                node.put("WEIGHT_GROWRATE", othUnTwnYear.getWeightGrowrate());
                node.put("WEIGHT_PERCENT", othUnTwnYear.getWeightPercent());
                node.put("TRADE_AMT_RANK", othUnTwnYear.getTradeAmtRank());
                node.put("TRADE_AMT_DIFFERENCE_RANK", othUnTwnYear.getTradeAmtDifferenceRank());
                node.put("TRADE_AMT_GROWRATE_RANK", othUnTwnYear.getTradeAmtGrowrateRank());
                node.put("TRADE_AMT_PERCENT_RANK", othUnTwnYear.getTradeAmtPercentRank());
                node.put("WEIGHT_RANK", othUnTwnYear.getWeightRank());
                node.put("WEIGHT_DIFFERENCE_RANK", othUnTwnYear.getWeightDifferenceRank());
                node.put("WEIGHT_GROWRATE_RANK", othUnTwnYear.getWeightGrowrateRank());
                node.put("WEIGHT_PERCENT_RANK", othUnTwnYear.getWeightPercentRank());

                node.put("TRADE_AMT_AREARANK", othUnTwnYear.getTradeAmtArearank());
                node.put("TRADE_AMT_DIFFERENCE_AREARANK", othUnTwnYear.getTradeAmtDifferenceArearank());
                node.put("TRADE_AMT_GROWRATE_AREARANK", othUnTwnYear.getTradeAmtGrowrateArearank());
                node.put("WEIGHT_AREARANK", othUnTwnYear.getWeightArearank());
                node.put("WEIGHT_DIFFERENCE_AREARANK", othUnTwnYear.getWeightDifferenceArearank());
                node.put("WEIGHT_GROWRATE_AREARANK", othUnTwnYear.getWeightGrowrateArearank());

                node.put("PARTNER_ISO3", othUnTwnYear.getPartnerIso3());
                node.put("COUNTRY_COMM_ZH", othUnTwnYear.getCountryCommZh());
                node.put("TXN_YEAR", othUnTwnYear.getTxnYear());
                node.put("TARIFF", othUnTwnYear.getTariff());
            }
            for (Map.Entry<String, OthUnTwnYear> entry : a2Map.entrySet()) {
                OthUnTwnYear othUnTwnYear = entry.getValue();
                ObjectNode node = a2BarArray.addObject();
                node.put("TRADE_WEIGHT", othUnTwnYear.getTradeWeight());
                node.put("TRADE_AMT", othUnTwnYear.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", othUnTwnYear.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", othUnTwnYear.getTradeAmtGrowrate());
                node.put("TRADE_AMT_PERCENT", othUnTwnYear.getTradeAmtPercent());
                node.put("WEIGHT_DIFFERENCE", othUnTwnYear.getWeightDifference());
                node.put("WEIGHT_GROWRATE", othUnTwnYear.getWeightGrowrate());
                node.put("WEIGHT_PERCENT", othUnTwnYear.getWeightPercent());
                node.put("TRADE_AMT_RANK", othUnTwnYear.getTradeAmtRank());
                node.put("TRADE_AMT_DIFFERENCE_RANK", othUnTwnYear.getTradeAmtDifferenceRank());
                node.put("TRADE_AMT_GROWRATE_RANK", othUnTwnYear.getTradeAmtGrowrateRank());
                node.put("WEIGHT_RANK", othUnTwnYear.getWeightRank());
                node.put("WEIGHT_DIFFERENCE_RANK", othUnTwnYear.getWeightDifferenceRank());
                node.put("WEIGHT_GROWRATE_RANK", othUnTwnYear.getWeightGrowrateRank());

                node.put("TRADE_AMT_AREARANK", othUnTwnYear.getTradeAmtArearank());
                node.put("TRADE_AMT_DIFFERENCE_AREARANK", othUnTwnYear.getTradeAmtDifferenceArearank());
                node.put("TRADE_AMT_GROWRATE_AREARANK", othUnTwnYear.getTradeAmtGrowrateArearank());
                node.put("WEIGHT_AREARANK", othUnTwnYear.getWeightArearank());
                node.put("WEIGHT_DIFFERENCE_AREARANK", othUnTwnYear.getWeightDifferenceArearank());
                node.put("WEIGHT_GROWRATE_AREARANK", othUnTwnYear.getWeightGrowrateArearank());

                node.put("REPORTER_ISO3", othUnTwnYear.getReporterIso3());
                node.put("COUNTRY_COMM_ZH", othUnTwnYear.getCountryCommZh());
                node.put("TXN_YEAR", othUnTwnYear.getTxnYear());
                node.put("TARIFF", othUnTwnYear.getTariff());
            }
        } else {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }

        end = new Date();
        duration = end.getTime() - start.getTime();
        Logger.of("application").info("A2 duration:{}", duration);

        response.put("A-2", A2Node);
        return ok(response);
    }

    public Result getA3(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        /*
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }
        */
        JsonNode parameter = request.body().asJson();
        if (!parameter.has("hs6") ||
                !parameter.has("cntyiso3") ||
                !parameter.has("timeunit") ||
                !parameter.has("area")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String hs6 = parameter.get("hs6").asText();
        String cntyiso3 = parameter.get("cntyiso3").asText();
        String timeunit = parameter.get("timeunit").asText();
        String area = parameter.get("area").isNull() ? null : parameter.get("area").asText();

        if (hs6 == null || hs6.isEmpty()) {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toJson()));
        }

        Date start;
        Date end;
        long duration = 0;
        start = new Date();

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ObjectNode A3Node = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode a3BarArray = A3Node.putArray("bar");
        ArrayNode a3LineArray = A3Node.putArray("line");

        if (timeunit.equals("month")) {
            List<UnNonWorldMonth> unNonWorldMonthList = UnNonWorldMonth.findListByHsCodeAndReporterIso3AndAreaId(hs6, cntyiso3, area);
            Map<String, UnNonWorldMonth> a3Map = new HashMap<>();
            for (UnNonWorldMonth unNonWorldMonth : unNonWorldMonthList) {
                String key = unNonWorldMonth.getHsCode6() + "_" + unNonWorldMonth.getReporterIso3() + "_" + unNonWorldMonth.getPartnerIso3();
                if (!a3Map.containsKey(key)) {
                    a3Map.put(key, unNonWorldMonth);
                } else {
                    UnNonWorldMonth existUnNonWorldMonth = a3Map.get(key);
                    int existTxnMonth = Integer.valueOf(existUnNonWorldMonth.getTxnYTM());
                    int txnMonth = Integer.valueOf(unNonWorldMonth.getTxnYTM());
                    if (existTxnMonth <= txnMonth) {
                        a3Map.put(key, unNonWorldMonth);
                    }
                }

                ObjectNode node = a3LineArray.addObject();
                node.put("TRADE_WEIGHT", unNonWorldMonth.getTradeWeight());
                node.put("TRADE_AMT", unNonWorldMonth.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", unNonWorldMonth.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", unNonWorldMonth.getTradeAmtGrowrate());
                node.put("TRADE_AMT_PERCENT", unNonWorldMonth.getTradeAmtPercent());
                node.put("WEIGHT_DIFFERENCE", unNonWorldMonth.getWeightDifference());
                node.put("WEIGHT_GROWRATE", unNonWorldMonth.getWeightGrowrate());
                node.put("WEIGHT_PERCENT", unNonWorldMonth.getWeightPercent());
                node.put("TRADE_AMT_RANK", unNonWorldMonth.getTradeAmtRank());
                node.put("TRADE_AMT_DIFFERENCE_RANK", unNonWorldMonth.getTradeAmtDifferenceRank());
                node.put("TRADE_AMT_GROWRATE_RANK", unNonWorldMonth.getTradeAmtGrowrateRank());
                node.put("TRADE_AMT_PERCENT_RANK", unNonWorldMonth.getTradeAmtPercentRank());
                node.put("WEIGHT_RANK", unNonWorldMonth.getWeightRank());
                node.put("WEIGHT_DIFFERENCE_RANK", unNonWorldMonth.getWeightDifferenceRank());
                node.put("WEIGHT_GROWRATE_RANK", unNonWorldMonth.getWeightGrowrateRank());
                node.put("WEIGHT_PERCENT_RANK", unNonWorldMonth.getWeightPercentRank());

                node.put("TRADE_AMT_AREARANK", unNonWorldMonth.getTradeAmtArearank());
                node.put("TRADE_AMT_DIFFERENCE_AREARANK", unNonWorldMonth.getTradeAmtDifferenceArearank());
                node.put("TRADE_AMT_GROWRATE_AREARANK", unNonWorldMonth.getTradeAmtGrowrateArearank());
                node.put("WEIGHT_AREARANK", unNonWorldMonth.getWeightArearank());
                node.put("WEIGHT_DIFFERENCE_AREARANK", unNonWorldMonth.getWeightDifferenceArearank());
                node.put("WEIGHT_GROWRATE_AREARANK", unNonWorldMonth.getWeightGrowrateArearank());

                node.put("PARTNER_ISO3", unNonWorldMonth.getPartnerIso3());
                node.put("COUNTRY_COMM_ZH", unNonWorldMonth.getCountryCommZh());
                node.put("TXN_YTM", unNonWorldMonth.getTxnYTM());
                node.put("TARIFF", unNonWorldMonth.getTariff());
            }
            for (Map.Entry<String, UnNonWorldMonth> entry : a3Map.entrySet()) {
                UnNonWorldMonth unNonWorldMonth = entry.getValue();
                ObjectNode node = a3BarArray.addObject();
                node.put("TRADE_WEIGHT", unNonWorldMonth.getTradeWeight());
                node.put("TRADE_AMT", unNonWorldMonth.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", unNonWorldMonth.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", unNonWorldMonth.getTradeAmtGrowrate());
                node.put("TRADE_AMT_PERCENT", unNonWorldMonth.getTradeAmtPercent());
                node.put("WEIGHT_DIFFERENCE", unNonWorldMonth.getWeightDifference());
                node.put("WEIGHT_GROWRATE", unNonWorldMonth.getWeightGrowrate());
                node.put("WEIGHT_PERCENT", unNonWorldMonth.getWeightPercent());
                node.put("TRADE_AMT_RANK", unNonWorldMonth.getTradeAmtRank());
                node.put("TRADE_AMT_DIFFERENCE_RANK", unNonWorldMonth.getTradeAmtDifferenceRank());
                node.put("TRADE_AMT_GROWRATE_RANK", unNonWorldMonth.getTradeAmtGrowrateRank());
                node.put("WEIGHT_RANK", unNonWorldMonth.getWeightRank());
                node.put("WEIGHT_DIFFERENCE_RANK", unNonWorldMonth.getWeightDifferenceRank());
                node.put("WEIGHT_GROWRATE_RANK", unNonWorldMonth.getWeightGrowrateRank());

                node.put("TRADE_AMT_AREARANK", unNonWorldMonth.getTradeAmtArearank());
                node.put("TRADE_AMT_DIFFERENCE_AREARANK", unNonWorldMonth.getTradeAmtDifferenceArearank());
                node.put("TRADE_AMT_GROWRATE_AREARANK", unNonWorldMonth.getTradeAmtGrowrateArearank());
                node.put("WEIGHT_AREARANK", unNonWorldMonth.getWeightArearank());
                node.put("WEIGHT_DIFFERENCE_AREARANK", unNonWorldMonth.getWeightDifferenceArearank());
                node.put("WEIGHT_GROWRATE_AREARANK", unNonWorldMonth.getWeightGrowrateArearank());

                node.put("REPORTER_ISO3", unNonWorldMonth.getReporterIso3());
                node.put("COUNTRY_COMM_ZH", unNonWorldMonth.getCountryCommZh());
                node.put("TXN_YTM", unNonWorldMonth.getTxnYTM());
                node.put("PARTNER_ISO3", unNonWorldMonth.getPartnerIso3());
                node.put("TARIFF", unNonWorldMonth.getTariff());
            }
        } else if (timeunit.equals("year")) {
            List<UnNonWorldYear> unNonWorldYearList = UnNonWorldYear.findListByHsCodeAndReporterIso3AndAreaId(hs6, cntyiso3, area);
            Map<String, UnNonWorldYear> a3Map = new HashMap<>();
            for (UnNonWorldYear unNonWorldYear : unNonWorldYearList) {
                String key = unNonWorldYear.getHsCode6() + "_" + unNonWorldYear.getReporterIso3() + "_" + unNonWorldYear.getPartnerIso3();
                if (!a3Map.containsKey(key)) {
                    a3Map.put(key, unNonWorldYear);
                } else {
                    UnNonWorldYear existUnNonWorldYear = a3Map.get(key);
                    int existTxnYear = Integer.valueOf(existUnNonWorldYear.getTxnYear());
                    int txnYear = Integer.valueOf(unNonWorldYear.getTxnYear());
                    if (existTxnYear <= txnYear) {
                        a3Map.put(key, unNonWorldYear);
                    }
                }

                ObjectNode node = a3LineArray.addObject();
                node.put("TRADE_WEIGHT", unNonWorldYear.getTradeWeight());
                node.put("TRADE_AMT", unNonWorldYear.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", unNonWorldYear.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", unNonWorldYear.getTradeAmtGrowrate());
                node.put("TRADE_AMT_PERCENT", unNonWorldYear.getTradeAmtPercent());
                node.put("WEIGHT_DIFFERENCE", unNonWorldYear.getWeightDifference());
                node.put("WEIGHT_GROWRATE", unNonWorldYear.getWeightGrowrate());
                node.put("WEIGHT_PERCENT", unNonWorldYear.getWeightPercent());
                node.put("TRADE_AMT_RANK", unNonWorldYear.getTradeAmtRank());
                node.put("TRADE_AMT_DIFFERENCE_RANK", unNonWorldYear.getTradeAmtDifferenceRank());
                node.put("TRADE_AMT_GROWRATE_RANK", unNonWorldYear.getTradeAmtGrowrateRank());
                node.put("TRADE_AMT_PERCENT_RANK", unNonWorldYear.getTradeAmtPercentRank());
                node.put("WEIGHT_RANK", unNonWorldYear.getWeightRank());
                node.put("WEIGHT_DIFFERENCE_RANK", unNonWorldYear.getWeightDifferenceRank());
                node.put("WEIGHT_GROWRATE_RANK", unNonWorldYear.getWeightGrowrateRank());
                node.put("WEIGHT_PERCENT_RANK", unNonWorldYear.getWeightPercentRank());

                node.put("TRADE_AMT_AREARANK", unNonWorldYear.getTradeAmtArearank());
                node.put("TRADE_AMT_DIFFERENCE_AREARANK", unNonWorldYear.getTradeAmtDifferenceArearank());
                node.put("TRADE_AMT_GROWRATE_AREARANK", unNonWorldYear.getTradeAmtGrowrateArearank());
                node.put("WEIGHT_AREARANK", unNonWorldYear.getWeightArearank());
                node.put("WEIGHT_DIFFERENCE_AREARANK", unNonWorldYear.getWeightDifferenceArearank());
                node.put("WEIGHT_GROWRATE_AREARANK", unNonWorldYear.getWeightGrowrateArearank());

                node.put("PARTNER_ISO3", unNonWorldYear.getPartnerIso3());
                node.put("COUNTRY_COMM_ZH", unNonWorldYear.getCountryCommZh());
                node.put("TXN_YEAR", unNonWorldYear.getTxnYear());
                node.put("TARIFF", unNonWorldYear.getTariff());
            }
            for (Map.Entry<String, UnNonWorldYear> entry : a3Map.entrySet()) {
                UnNonWorldYear unNonWorldYear = entry.getValue();
                ObjectNode node = a3BarArray.addObject();
                node.put("TRADE_WEIGHT", unNonWorldYear.getTradeWeight());
                node.put("TRADE_AMT", unNonWorldYear.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", unNonWorldYear.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", unNonWorldYear.getTradeAmtGrowrate());
                node.put("TRADE_AMT_PERCENT", unNonWorldYear.getTradeAmtPercent());
                node.put("WEIGHT_DIFFERENCE", unNonWorldYear.getWeightDifference());
                node.put("WEIGHT_GROWRATE", unNonWorldYear.getWeightGrowrate());
                node.put("WEIGHT_PERCENT", unNonWorldYear.getWeightPercent());
                node.put("TRADE_AMT_RANK", unNonWorldYear.getTradeAmtRank());
                node.put("TRADE_AMT_DIFFERENCE_RANK", unNonWorldYear.getTradeAmtDifferenceRank());
                node.put("TRADE_AMT_GROWRATE_RANK", unNonWorldYear.getTradeAmtGrowrateRank());
                node.put("WEIGHT_RANK", unNonWorldYear.getWeightRank());
                node.put("WEIGHT_DIFFERENCE_RANK", unNonWorldYear.getWeightDifferenceRank());
                node.put("WEIGHT_GROWRATE_RANK", unNonWorldYear.getWeightGrowrateRank());

                node.put("TRADE_AMT_AREARANK", unNonWorldYear.getTradeAmtArearank());
                node.put("TRADE_AMT_DIFFERENCE_AREARANK", unNonWorldYear.getTradeAmtDifferenceArearank());
                node.put("TRADE_AMT_GROWRATE_AREARANK", unNonWorldYear.getTradeAmtGrowrateArearank());
                node.put("WEIGHT_AREARANK", unNonWorldYear.getWeightArearank());
                node.put("WEIGHT_DIFFERENCE_AREARANK", unNonWorldYear.getWeightDifferenceArearank());
                node.put("WEIGHT_GROWRATE_AREARANK", unNonWorldYear.getWeightGrowrateArearank());

                node.put("REPORTER_ISO3", unNonWorldYear.getReporterIso3());
                node.put("COUNTRY_COMM_ZH", unNonWorldYear.getCountryCommZh());
                node.put("TXN_YEAR", unNonWorldYear.getTxnYear());
                node.put("PARTNER_ISO3", unNonWorldYear.getPartnerIso3());
                node.put("TARIFF", unNonWorldYear.getTariff());
            }
        } else {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }

        end = new Date();
        duration = end.getTime() - start.getTime();
        Logger.of("application").info("A3 duration:{}", duration);

        response.put("A-3", A3Node);
        return ok(response);
    }
}
