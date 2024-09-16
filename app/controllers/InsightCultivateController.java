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
import utils.MyCache;
import utils.RedisUtils;

import java.sql.Timestamp;
import java.util.*;

public class InsightCultivateController extends Controller {
    private final MyCache myCache = MyCache.getInstance();

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
            MemberList memberList = MemberList.findByMemberIdAndType(memberId, InsightType.TRADE_FAVORITE_ADD_CNTY_HS4.getAction());
            ArrayNode membersArray = response.putArray("member_list");
            if (memberList != null) {
                List<InsightMemberListBean> insightMemberListBeanList = memberList.getContent();
                Collections.reverse(insightMemberListBeanList);
                for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
                    ObjectNode node = membersArray.addObject();
                    node.put("hs_code", insightMemberListBean.getHsCode4());
                    node.put("CNTY", insightMemberListBean.getCntyCode());
                }
            }

            //找到推薦的
            ArrayNode recommendArray = response.putArray("recommend_list");
            //List<MemberRemoveRecommendList> memberRemoveRecommendList = MemberRemoveRecommendList.findByMemberIdAndType(memberId, "cultivate");
            List<MemberTrade> memberTradeList = MemberTrade.findByMemberIdAndType(memberId, "cntyhsc4");

            Set<String> set = new HashSet<>();
            MemberList memberDelRecommendList = MemberList.findByMemberIdAndType(memberId, InsightType.TRADE_RECOMMEND_DEL_CNTYHSC4.getAction());
            if (memberDelRecommendList != null) {
                for (InsightMemberListBean insightMemberListBean : memberDelRecommendList.getContent()) {
                    set.add(insightMemberListBean.getHsCode4() + "_" + insightMemberListBean.getCntyCode());
                }
            }

            for (MemberTrade memberTrade : memberTradeList) {
                if (!set.contains(memberTrade.getTradeHscode() + "_" + memberTrade.getTradeCountry())) {
                    ObjectNode node = recommendArray.addObject();
                    node.put("hs_code", memberTrade.getTradeHscode());
                    node.put("CNTY", memberTrade.getTradeCountry());
                }
            }

            ObjectNode defaultNode = response.putObject("default");
            defaultNode.put("hs_code", "8480");
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
            MemberList memberListADD = MemberList.findByMemberIdAndType(memberId, InsightType.TRADE_FAVORITE_ADD_CNTY_HS4.getAction());
            boolean doADDUpdate = false;
            boolean doDELUpdate = false;
            if (memberListADD == null) {
                memberListADD = new MemberList();
                memberListADD.setMemberId(memberId);
                memberListADD.setType(InsightType.TRADE_FAVORITE_ADD_CNTY_HS4.getAction());
                memberListADD.setMemberListUpdateDatetime(timestamp);
            } else {
                memberListADD.setMemberListUpdateDatetime(timestamp);
                doADDUpdate = true;
            }

            MemberList memberListDEL = MemberList.findByMemberIdAndType(memberId, InsightType.TRADE_FAVORITE_DEL_CNTY_HS4.getAction());
            if (memberListDEL == null) {
                memberListDEL = new MemberList();
                memberListDEL.setMemberId(memberId);
                memberListDEL.setType(InsightType.TRADE_FAVORITE_DEL_CNTY_HS4.getAction());
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
                    String hs4 = insightMemberListBeanADDList.get(a).getHsCode4();
                    String cnty = insightMemberListBeanADDList.get(a).getCntyCode();
                    addSet.put(hs4 + "_" + cnty, a);
                }
                if (!addSet.containsKey(hsCode + "_" + regionCode)) {
                    //put add list
                    InsightMemberListBean insightMemberListBean = new InsightMemberListBean();
                    insightMemberListBean.setHsCode4(hsCode);
                    insightMemberListBean.setCntyCode(regionCode);
                    insightMemberListBeanADDList.add(insightMemberListBean);
                }

                //remove del list
                Map<String, Integer> delSet = new HashMap<>();
                for (int a = 0; a < insightMemberListBeanDELList.size(); a++) {
                    String hs4 = insightMemberListBeanDELList.get(a).getHsCode4();
                    String cntyCode = insightMemberListBeanDELList.get(a).getCntyCode();
                    delSet.put(hs4 + "_" + cntyCode, a);
                }

                //Do del
                if (delSet.containsKey(hsCode + "_" + regionCode)) {
                    //Check is exist
                    insightMemberListBeanDELList.remove(delSet.get(hsCode + "_" + regionCode).intValue());
                }

                action = InsightType.TRADE_FAVORITE_ADD_CNTY_HS4.getCode();
            }
            else if (favorStatus.equals("N")) {
                Map<String, Integer> delSet = new HashMap<>();

                for (int a = 0; a < insightMemberListBeanDELList.size(); a++) {
                    String hs4 = insightMemberListBeanDELList.get(a).getHsCode4();
                    String cnty = insightMemberListBeanDELList.get(a).getCntyCode();
                    delSet.put(hs4 + "_" + cnty, a);
                }
                if (!delSet.containsKey(hsCode + "_" + regionCode)) {
                    //put del list
                    InsightMemberListBean insightMemberListBean = new InsightMemberListBean();
                    insightMemberListBean.setHsCode4(hsCode);
                    insightMemberListBean.setCntyCode(regionCode);
                    insightMemberListBeanDELList.add(insightMemberListBean);
                }

                //remove add list
                Map<String, Integer> addSet = new HashMap<>();
                for (int a = 0; a < insightMemberListBeanADDList.size(); a++) {
                    String hs4 = insightMemberListBeanADDList.get(a).getHsCode4();
                    String cntyCode = insightMemberListBeanADDList.get(a).getCntyCode();
                    addSet.put(hs4 + "_" + cntyCode, a);
                }

                //Do add
                if (addSet.containsKey(hsCode + "_" + regionCode)) {
                    //Check is exist
                    insightMemberListBeanADDList.remove(addSet.get(hsCode + "_" + regionCode).intValue());
                }

                action = InsightType.TRADE_FAVORITE_DEL_CNTY_HS4.getCode();
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
        MemberRemoveRecommendList memberRemoveRecommendList = MemberRemoveRecommendList.findBy(memberId, "cultivate", hsCode, regionCode);
        if (memberRemoveRecommendList == null) {
            memberRemoveRecommendList = new MemberRemoveRecommendList();
            memberRemoveRecommendList.setMemberId(memberId);
            memberRemoveRecommendList.setType("cultivate");
            memberRemoveRecommendList.setHscode(hsCode);
            memberRemoveRecommendList.setCnty(regionCode);
            memberRemoveRecommendList.save();
        }
        */
        //New from here
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        try {
            boolean doDELUpdate = false;
            MemberList memberListDEL = MemberList.findByMemberIdAndType(memberId, InsightType.TRADE_RECOMMEND_DEL_CNTYHSC4.getAction());
            if (memberListDEL == null) {
                memberListDEL = new MemberList();
                memberListDEL.setMemberId(memberId);
                memberListDEL.setType(InsightType.TRADE_RECOMMEND_DEL_CNTYHSC4.getAction());
                memberListDEL.setMemberListUpdateDatetime(timestamp);
            } else {
                memberListDEL.setMemberListUpdateDatetime(timestamp);
                doDELUpdate = true;
            }

            List<InsightMemberListBean> insightMemberListBeanDELList = memberListDEL.getContent() == null ? new ArrayList<>() : memberListDEL.getContent();

            Map<String, Integer> delSet = new HashMap<>();

            for (int a = 0; a < insightMemberListBeanDELList.size(); a++) {
                String hs4 = insightMemberListBeanDELList.get(a).getHsCode4();
                String cnty = insightMemberListBeanDELList.get(a).getCntyCode();
                delSet.put(hs4 + "_" + cnty, a);
            }
            if (!delSet.containsKey(hsCode + "_" + regionCode)) {
                //put del list
                InsightMemberListBean insightMemberListBean = new InsightMemberListBean();
                insightMemberListBean.setHsCode4(hsCode);
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
        if (!parameter.has("hs4") ||
                !parameter.has("cntyiso3")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String hs4 = parameter.get("hs4").asText();
        String cntyiso3 = parameter.get("cntyiso3").asText();
        Date start;
        Date end;
        long duration = 0;
        start = new Date();

        if (hs4 == null || hs4.isEmpty() || cntyiso3 == null || cntyiso3.isEmpty()) {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toJson()));
        }

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ObjectNode existNode = new ObjectNode(JsonNodeFactory.instance);
        existNode.put("B-1", true);
        existNode.put("B-2", true);
        existNode.put("B-3", true);

        ArrayNode b0_1Array = response.putArray("B-0-1");
        ArrayNode b1_1Array = response.putArray("B-1-1");
        ArrayNode b2_1Array = response.putArray("B-2-1");
        ArrayNode b3_4Array = response.putArray("B-3-4");

        ObjectNode B1Node = new ObjectNode(JsonNodeFactory.instance);
        List<String> b11List = Arrays.asList(new String[3]);
        ArrayNode b1BarArray = B1Node.putArray("bar");
        List<TxnUnYearHscode4> txnUnYearHscode4List = TxnUnYearHscode4.findByHsCode4AndReporterIso3AndPartnerIso3(hs4, cntyiso3, "WLD");
        for (TxnUnYearHscode4 txnUnYearHscode4 : txnUnYearHscode4List) {
            ObjectNode node = b1BarArray.addObject();
            node.put("TXN_YEAR", txnUnYearHscode4.getTxnYear());
            node.put("HSCODE_4", txnUnYearHscode4.getHsCode4());
            node.put("TRADE_WEIGHT", txnUnYearHscode4.getTradeWeight());
            node.put("TRADE_AMT", txnUnYearHscode4.getTradeAmt());
            node.put("TRADE_AMT_DIFFERENCE", txnUnYearHscode4.getTradeAmtDifference());
            node.put("TRADE_AMT_GROWRATE", txnUnYearHscode4.getTradeAmtGrowrate());
            node.put("WEIGHT_DIFFERENCE", txnUnYearHscode4.getWeightDifference());
            node.put("WEIGHT_GROWRATE", txnUnYearHscode4.getWeightGrowrate());
        }
        txnUnYearHscode4List.clear();

        ArrayNode b1LineArray = B1Node.putArray("line");
        List<TxnUnYearHscode6> txnUnYearHscode6List = TxnUnYearHscode6.findByHsCode4AndReporterIso3AndPartnerIso3(hs4, cntyiso3, List.of("WLD"));
        //List<TxnUnYearHscode6> txnUnYearHscode6List = TxnUnYearHscode6.findByHsCode4AndReporterIso3AndPartnerIso3(hs4, cntyiso3, List.of("WLD", "TWN"));
        for (TxnUnYearHscode6 txnUnYearHscode6 : txnUnYearHscode6List) {
            //ISO3 == WLD
            if (txnUnYearHscode6.getPartnerIso3().equals("WLD")) {
                ObjectNode node = b1LineArray.addObject();
                node.put("TXN_YEAR", txnUnYearHscode6.getTxnYear());
                node.put("HSCODE_6", txnUnYearHscode6.getHsCode6());
                node.put("TRADE_WEIGHT", txnUnYearHscode6.getTradeWeight());
                node.put("TRADE_AMT", txnUnYearHscode6.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", txnUnYearHscode6.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", txnUnYearHscode6.getTradeAmtGrowrate());
                node.put("WEIGHT_DIFFERENCE", txnUnYearHscode6.getWeightDifference());
                node.put("WEIGHT_GROWRATE", txnUnYearHscode6.getWeightGrowrate());
                node.put("TRADE_AMT_RANK_HSCODE", txnUnYearHscode6.getTradeAmtRankHscode());
                node.put("TRADE_AMT_DIFFERENCE_RANK_HSCODE", txnUnYearHscode6.getTradeAmtDifferenceRankHscode());
                node.put("TRADE_AMT_GROWRATE_RANK_HSCODE", txnUnYearHscode6.getTradeAmtGrowrateRankHscode());
                node.put("WEIGHT_RANK_HSCODE", txnUnYearHscode6.getWeightRankHscode());
                node.put("WEIGHT_DIFFERENCE_RANK_HSCODE", txnUnYearHscode6.getWeightDifferenceRankHscode());
                node.put("WEIGHT_GROWRATE_RANK_HSCODE", txnUnYearHscode6.getWeightGrowrateRankHscode());
            }
            //B-1-1 Top3
            if (txnUnYearHscode6.getTradeAmtRankHscode() != null && txnUnYearHscode6.getTradeAmtRankHscode() <= 3) {
                b11List.set(txnUnYearHscode6.getTradeAmtRankHscode() - 1, txnUnYearHscode6.getHsCode6());
            }
        }
        if (txnUnYearHscode4List.size() == 0 && txnUnYearHscode6List.size() == 0) {
            existNode.put("B-1", false);
        }

        //B-1-1 Top3
        /*
        List<String> b11List = Arrays.asList(new String[3]);
        for (TxnUnYearHscode6 txnUnYearHscode6 : txnUnYearHscode6List) {
            if (txnUnYearHscode6.getTradeAmtRankHscode() != null && txnUnYearHscode6.getTradeAmtRankHscode() <= 3) {
                b11List.set(txnUnYearHscode6.getTradeAmtRankHscode() - 1, txnUnYearHscode6.getHsCode6());
            }
        }
        */
        for (String s : b11List) {
            if (s != null) {
                b1_1Array.add(s);
            }
        }

        ObjectNode B2Node = new ObjectNode(JsonNodeFactory.instance);
        List<String> b21List = Arrays.asList(new String[3]);
        List<TnxMofYearHscode4> tnxMofYearHscode4List = TnxMofYearHscode4.findByHsCode4AndPartnerIso3(hs4, cntyiso3);
        ArrayNode b2BarArray = B2Node.putArray("bar");
        for (TnxMofYearHscode4 tnxMofYearHscode4 : tnxMofYearHscode4List) {
            ObjectNode node = b2BarArray.addObject();
            node.put("TXN_YEAR", tnxMofYearHscode4.getTxnYear());
            node.put("HSCODE_4", tnxMofYearHscode4.getHsCode4());
            node.put("TRADE_WEIGHT", tnxMofYearHscode4.getTradeWeight());
            node.put("TRADE_AMT", tnxMofYearHscode4.getTradeAmt());
            node.put("TRADE_AMT_DIFFERENCE", tnxMofYearHscode4.getTradeAmtDifference());
            node.put("TRADE_AMT_GROWRATE", tnxMofYearHscode4.getTradeAmtGrowrate());
            node.put("TRADE_AMT_PERCENT", tnxMofYearHscode4.getTradeAmtPercent());
            node.put("WEIGHT_DIFFERENCE", tnxMofYearHscode4.getWeightDifference());
            node.put("WEIGHT_GROWRATE", tnxMofYearHscode4.getWeightGrowrate());
            node.put("WEIGHT_PERCENT", tnxMofYearHscode4.getWeightPercent());
        }

        List<TxnMofYearHscode6Raw> txnMofYearHscode6RawList = TxnMofYearHscode6Raw.findByHscodeAndCNTY(hs4, cntyiso3);
        ArrayNode b2LineArray = B2Node.putArray("line");
        for (TxnMofYearHscode6Raw txnMofYearHscode6Raw : txnMofYearHscode6RawList) {
            ObjectNode node = b2LineArray.addObject();
            node.put("TXN_YEAR", txnMofYearHscode6Raw.getTxnYear());
            node.put("HSCODE_6", txnMofYearHscode6Raw.getHscode6());
            node.put("TRADE_WEIGHT", txnMofYearHscode6Raw.getTradeWeight());
            node.put("TRADE_AMT", txnMofYearHscode6Raw.getTradeAmt());
            node.put("TRADE_AMT_DIFFERENCE", txnMofYearHscode6Raw.getTradeAmtDifference());
            node.put("TRADE_AMT_GROWRATE", txnMofYearHscode6Raw.getTradeAmtGrowrate());
            node.put("TRADE_AMT_PERCENT", txnMofYearHscode6Raw.getTradeAmtPercent());
            node.put("WEIGHT_DIFFERENCE", txnMofYearHscode6Raw.getWeightDifference());
            node.put("WEIGHT_GROWRATE", txnMofYearHscode6Raw.getWeightGrowrate());
            node.put("WEIGHT_PERCENT", txnMofYearHscode6Raw.getWeightPercent());

            node.put("TRADE_AMT_RANK_HSCODE", txnMofYearHscode6Raw.getTradeAmtRankHscode());
            node.put("TRADE_AMT_DIFFERENCE_RANK_HSCODE", txnMofYearHscode6Raw.getTradeAmtPercentRankHscode());
            node.put("TRADE_AMT_GROWRATE_RANK_HSCODE", txnMofYearHscode6Raw.getTradeAmtGrowrateRankHscode());
            node.put("TRADE_AMT_PERCENT_RANK_HSCODE", txnMofYearHscode6Raw.getTradeAmtPercentRankHscode());
            node.put("WEIGHT_RANK_HSCODE", txnMofYearHscode6Raw.getWeightRankHscode());
            node.put("WEIGHT_DIFFERENCE_RANK_HSCODE", txnMofYearHscode6Raw.getWeightDifferenceRankHscode());
            node.put("WEIGHT_GROWRATE_RANK_HSCODE", txnMofYearHscode6Raw.getWeightGrowrateRankHscode());
            node.put("WEIGHT_PERCENT_RANK_HSCODE", txnMofYearHscode6Raw.getWeightPercentRankHscode());
            //B-2-1
            if (txnMofYearHscode6Raw.getTradeAmtRankHscode() != null && txnMofYearHscode6Raw.getTradeAmtRankHscode() <= 3) {
                b21List.set(txnMofYearHscode6Raw.getTradeAmtRankHscode() - 1, txnMofYearHscode6Raw.getHscode6());
            }
        }
        if (tnxMofYearHscode4List.size() == 0 && txnMofYearHscode6RawList.size() == 0) {
            existNode.put("B-2", false);
        }
        //B-2-1
        /*
        List<String> b21List = Arrays.asList(new String[3]);
        for (TxnMofYearHscode6Raw txnMofYearHscode6Raw : txnMofYearHscode6RawList) {
            if (txnMofYearHscode6Raw.getTradeAmtRankHscode() != null && txnMofYearHscode6Raw.getTradeAmtRankHscode() <= 3) {
                b21List.set(txnMofYearHscode6Raw.getTradeAmtRankHscode() - 1, txnMofYearHscode6Raw.getHscode6());
            }
        }
        */
        for (String s : b21List) {
            if (s != null) {
                b2_1Array.add(s);
            }
        }

        ObjectNode B3Node = new ObjectNode(JsonNodeFactory.instance);
        List<B3TableRaw> b3TableRawList = new ArrayList<>();
        b3TableRawList = myCache.getB3TableCache(hs4 + "_" + cntyiso3);
        if (b3TableRawList == null) {
            b3TableRawList = B3TableRaw.findByHscodeAndCNTY(hs4, cntyiso3);
            myCache.putB3TableCache(hs4 + "_" + cntyiso3, b3TableRawList);
        }

        ArrayNode b3TableArray = B3Node.putArray("table");
        B3TableRaw b3TableRawFirst = b3TableRawList.isEmpty() ? null : b3TableRawList.get(0);
        boolean twnGroupAbove0 = false;
        boolean hasTwnAmt = false;
        for (B3TableRaw b3TableRaw : b3TableRawList) {
            ObjectNode node = b3TableArray.addObject();
            node.put("TXN_YEAR", b3TableRaw.getTxnYear());
            node.put("HSCODE_6", b3TableRaw.getHscode6());
            node.put("TRADE_AMT", b3TableRaw.getTradeAmt());
            node.put("TRADE_AMT_GROWRATE", b3TableRaw.getTradeAmtGrowrate());
            node.put("TWN_AMT", b3TableRaw.getTwnAmt());
            node.put("TWN_GROWRATE", b3TableRaw.getTwnGrowrate());
            node.put("TARIFF", b3TableRaw.getTariff());

            if (b3TableRaw.getTwnGrowrate() != null && Double.valueOf(b3TableRaw.getTwnGrowrate()) > 0) {
                twnGroupAbove0 = true;
            }
            if (b3TableRaw.getTwnAmt() != null) {
                hasTwnAmt = true;
            }
        }
        if (b3TableRawList.size() == 0) {
            existNode.put("B-3", false);
        }

        //B-3-2 Export from TWN and amount >= 0
        if (!twnGroupAbove0 || !hasTwnAmt) {
            response.put("B-3-2", false);
        } else {
            response.put("B-3-2", true);
        }
        //B-3-3 From B-3 Table
        if (b3TableRawFirst != null) {
            response.put("B-3-3", b3TableRawFirst.getHscode6());
        }

        int count = 0;
        for (B3TableRaw b3TableRaw : b3TableRawList) {
            if (count == 3) {
                break;
            }
            b0_1Array.add(b3TableRaw.getHscode6());
            count++;
        }

        List<String> b34List = Arrays.asList(new String[3]);
        ArrayNode b3BarArray = B3Node.putArray("bar");
        ArrayNode b3LineArray = B3Node.putArray("line");
        if (b3TableRawFirst != null) {
            List<UnWorldYear> unWorldYearList = UnWorldYear.findListByHsCodeAndReporterIso3(b3TableRawFirst.getHscode6(), cntyiso3);
            for (UnWorldYear unWorldYear : unWorldYearList) {
                ObjectNode nodeBar = b3BarArray.addObject();
                nodeBar.put("TXN_YEAR", unWorldYear.getTxnYear());
                nodeBar.put("TRADE_WEIGHT", unWorldYear.getTradeWeight());
                nodeBar.put("TRADE_AMT", unWorldYear.getTradeAmt());
                nodeBar.put("TRADE_AMT_DIFFERENCE", unWorldYear.getTradeAmtDifference());
                nodeBar.put("TRADE_AMT_GROWRATE", unWorldYear.getTradeAmtGrowrate());
                nodeBar.put("TRADE_AMT_PERCENT", unWorldYear.getTradeAmtPercent());
                nodeBar.put("WEIGHT_DIFFERENCE", unWorldYear.getWeightDifference());
                nodeBar.put("WEIGHT_GROWRATE", unWorldYear.getWeightGrowrate());
                nodeBar.put("WEIGHT_PERCENT", unWorldYear.getWeightPercent());
            }

            List<UnNonWorldYear> unNonWorldYearList = UnNonWorldYear.findListByHsCodeAndReporterIso3AndAreaId(b3TableRawFirst.getHscode6(), cntyiso3, null);
            for (UnNonWorldYear unNonWorldYear : unNonWorldYearList) {
                ObjectNode nodeLine = b3LineArray.addObject();
                nodeLine.put("TXN_YEAR", unNonWorldYear.getTxnYear());
                nodeLine.put("PARTNER_ISO3", unNonWorldYear.getPartnerIso3());
                nodeLine.put("TRADE_WEIGHT", unNonWorldYear.getTradeWeight());
                nodeLine.put("TRADE_AMT", unNonWorldYear.getTradeAmt());
                nodeLine.put("TRADE_AMT_DIFFERENCE", unNonWorldYear.getTradeAmtDifference());
                nodeLine.put("TRADE_AMT_GROWRATE", unNonWorldYear.getTradeAmtGrowrate());
                nodeLine.put("TRADE_AMT_PERCENT", unNonWorldYear.getTradeAmtPercent());
                nodeLine.put("WEIGHT_DIFFERENCE", unNonWorldYear.getWeightDifference());
                nodeLine.put("WEIGHT_GROWRATE", unNonWorldYear.getWeightGrowrate());
                nodeLine.put("WEIGHT_PERCENT", unNonWorldYear.getWeightPercent());
                nodeLine.put("TRADE_AMT_RANK", unNonWorldYear.getTradeAmtRank());
                nodeLine.put("TRADE_AMT_DIFFERENCE_RANK", unNonWorldYear.getTradeAmtDifferenceRank());
                nodeLine.put("TRADE_AMT_GROWRATE_RANK", unNonWorldYear.getTradeAmtGrowrateRank());
                nodeLine.put("TRADE_AMT_PERCENT_RANK", unNonWorldYear.getTradeAmtPercentRank());
                nodeLine.put("WEIGHT_RANK", unNonWorldYear.getWeightRank());
                nodeLine.put("WEIGHT_DIFFERENCE_RANK", unNonWorldYear.getWeightDifferenceRank());
                nodeLine.put("WEIGHT_GROWRATE_RANK", unNonWorldYear.getWeightGrowrateRank());
                nodeLine.put("WEIGHT_PERCENT_RANK", unNonWorldYear.getWeightPercentRank());
                nodeLine.put("COUNTRY_COMM_ZH", unNonWorldYear.getCountryCommZh());
                nodeLine.put("TARIFF", unNonWorldYear.getTariff());
                //B-3-4
                if (unNonWorldYear.getTradeAmtRank() != null && unNonWorldYear.getTradeAmtRank() <= 3) {
                    b34List.set(unNonWorldYear.getTradeAmtRank() - 1, unNonWorldYear.getPartnerIso3());
                }
            }
            //B-3-4
            /*
            List<String> b34List = Arrays.asList(new String[3]);
            for (UnNonWorldYear unNonWorldYear : unNonWorldYearList) {
                if (unNonWorldYear.getTradeAmtRank() != null && unNonWorldYear.getTradeAmtRank() <= 3) {
                    b34List.set(unNonWorldYear.getTradeAmtRank() - 1, unNonWorldYear.getPartnerIso3());
                }
            }
            */
            for (String s : b34List) {
                if (s != null) {
                    b3_4Array.add(s);
                }
            }
        }

        end = new Date();
        duration = end.getTime() - start.getTime();
        Logger.of("application").info("B0 duration:{}", duration);

        response.put("exist", existNode);
        response.put("B-1", B1Node);
        response.put("B-2", B2Node);
        response.put("B-3", B3Node);
        return ok(response);
    }

    public Result getB1(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        /*
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }
        */
        JsonNode parameter = request.body().asJson();
        if (!parameter.has("hs4") ||
                !parameter.has("cntyiso3") ||
                !parameter.has("timeunit")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String hs4 = parameter.get("hs4").asText();
        String cntyiso3 = parameter.get("cntyiso3").asText();
        String timeunit = parameter.get("timeunit").asText();

        if (hs4 == null || hs4.isEmpty() || cntyiso3 == null || cntyiso3.isEmpty()) {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toJson()));
        }

        Date start;
        Date end;
        long duration = 0;
        start = new Date();

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ObjectNode B1Node = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode b1BarArray = B1Node.putArray("bar");
        ArrayNode b1LineArray = B1Node.putArray("line");

        if (timeunit.equals("month")) {
            List<TxnUnMonthHscode4> txnUnMonthHscode4List = TxnUnMonthHscode4.findByHsCode4AndReporterIso3AndPartnerIso3(hs4, cntyiso3, "WLD");
            for (TxnUnMonthHscode4 txnUnMonthHscode4 : txnUnMonthHscode4List) {
                ObjectNode node = b1BarArray.addObject();
                node.put("TXN_YTM", txnUnMonthHscode4.getTxnYTM());
                node.put("HSCODE_4", txnUnMonthHscode4.getHsCode4());
                node.put("TRADE_WEIGHT", txnUnMonthHscode4.getTradeWeight());
                node.put("TRADE_AMT", txnUnMonthHscode4.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", txnUnMonthHscode4.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", txnUnMonthHscode4.getTradeAmtGrowrate());
                node.put("WEIGHT_DIFFERENCE", txnUnMonthHscode4.getWeightDifference());
                node.put("WEIGHT_GROWRATE", txnUnMonthHscode4.getWeightGrowrate());
            }
            List<TxnUnMonthHscode6> txnUnMonthHscode6List = TxnUnMonthHscode6.findByHsCode4AndReporterIso3AndPartnerIso3(hs4, cntyiso3, List.of("WLD"));
            for (TxnUnMonthHscode6 txnUnMonthHscode6 : txnUnMonthHscode6List) {
                ObjectNode node = b1LineArray.addObject();
                node.put("TXN_YTM", txnUnMonthHscode6.getTxnYtm());
                node.put("HSCODE_6", txnUnMonthHscode6.getHsCode6());
                node.put("TRADE_WEIGHT", txnUnMonthHscode6.getTradeWeight());
                node.put("TRADE_AMT", txnUnMonthHscode6.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", txnUnMonthHscode6.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", txnUnMonthHscode6.getTradeAmtGrowrate());
                node.put("WEIGHT_DIFFERENCE", txnUnMonthHscode6.getWeightDifference());
                node.put("WEIGHT_GROWRATE", txnUnMonthHscode6.getWeightGrowrate());
                node.put("TRADE_AMT_RANK_HSCODE", txnUnMonthHscode6.getTradeAmtRankHscode());
                node.put("TRADE_AMT_DIFFERENCE_RANK_HSCODE", txnUnMonthHscode6.getTradeAmtDifferenceRankHscode());
                node.put("TRADE_AMT_GROWRATE_RANK_HSCODE", txnUnMonthHscode6.getTradeAmtGrowrateRankHscode());
                node.put("WEIGHT_RANK_HSCODE", txnUnMonthHscode6.getWeightRankHscode());
                node.put("WEIGHT_DIFFERENCE_RANK_HSCODE", txnUnMonthHscode6.getWeightDifferenceRankHscode());
                node.put("WEIGHT_GROWRATE_RANK_HSCODE", txnUnMonthHscode6.getWeightGrowrateRankHscode());
            }
        } else if (timeunit.equals("year")) {
            List<TxnUnYearHscode4> txnUnYearHscode4List = TxnUnYearHscode4.findByHsCode4AndReporterIso3AndPartnerIso3(hs4, cntyiso3, "WLD");
            for (TxnUnYearHscode4 txnUnYearHscode4 : txnUnYearHscode4List) {
                ObjectNode node = b1BarArray.addObject();
                node.put("TXN_YEAR", txnUnYearHscode4.getTxnYear());
                node.put("HSCODE_4", txnUnYearHscode4.getHsCode4());
                node.put("TRADE_WEIGHT", txnUnYearHscode4.getTradeWeight());
                node.put("TRADE_AMT", txnUnYearHscode4.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", txnUnYearHscode4.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", txnUnYearHscode4.getTradeAmtGrowrate());
                node.put("WEIGHT_DIFFERENCE", txnUnYearHscode4.getWeightDifference());
                node.put("WEIGHT_GROWRATE", txnUnYearHscode4.getWeightGrowrate());
            }

            List<TxnUnYearHscode6> txnUnYearHscode6List = TxnUnYearHscode6.findByHsCode4AndReporterIso3AndPartnerIso3(hs4, cntyiso3, List.of("WLD"));
            for (TxnUnYearHscode6 txnUnYearHscode6 : txnUnYearHscode6List) {
                ObjectNode node = b1LineArray.addObject();
                node.put("TXN_YEAR", txnUnYearHscode6.getTxnYear());
                node.put("HSCODE_6", txnUnYearHscode6.getHsCode6());
                node.put("TRADE_WEIGHT", txnUnYearHscode6.getTradeWeight());
                node.put("TRADE_AMT", txnUnYearHscode6.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", txnUnYearHscode6.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", txnUnYearHscode6.getTradeAmtGrowrate());
                node.put("WEIGHT_DIFFERENCE", txnUnYearHscode6.getWeightDifference());
                node.put("WEIGHT_GROWRATE", txnUnYearHscode6.getWeightGrowrate());
                node.put("TRADE_AMT_RANK_HSCODE", txnUnYearHscode6.getTradeAmtRankHscode());
                node.put("TRADE_AMT_DIFFERENCE_RANK_HSCODE", txnUnYearHscode6.getTradeAmtDifferenceRankHscode());
                node.put("TRADE_AMT_GROWRATE_RANK_HSCODE", txnUnYearHscode6.getTradeAmtGrowrateRankHscode());
                node.put("WEIGHT_RANK_HSCODE", txnUnYearHscode6.getWeightRankHscode());
                node.put("WEIGHT_DIFFERENCE_RANK_HSCODE", txnUnYearHscode6.getWeightDifferenceRankHscode());
                node.put("WEIGHT_GROWRATE_RANK_HSCODE", txnUnYearHscode6.getWeightGrowrateRankHscode());
            }
        } else {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }

        end = new Date();
        duration = end.getTime() - start.getTime();
        Logger.of("application").info("B1 duration:{}", duration);

        response.put("B-1", B1Node);
        return ok(response);
    }

    public Result getB2(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        /*
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }
        */
        JsonNode parameter = request.body().asJson();
        if (!parameter.has("hs4") ||
                !parameter.has("cntyiso3") ||
                !parameter.has("timeunit")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String hs4 = parameter.get("hs4").asText();
        String cntyiso3 = parameter.get("cntyiso3").asText();
        String timeunit = parameter.get("timeunit").asText();

        if (hs4 == null || hs4.isEmpty() || cntyiso3 == null || cntyiso3.isEmpty()) {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toJson()));
        }

        Date start;
        Date end;
        long duration = 0;
        start = new Date();

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ObjectNode B2Node = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode b2BarArray = B2Node.putArray("bar");
        ArrayNode b2LineArray = B2Node.putArray("line");

        if (timeunit.equals("month")) {
            List<TnxMofMonthHscode4> txnMofMonthHscode4List = TnxMofMonthHscode4.findByHsCode4AndPartnerIso3(hs4, cntyiso3);
            for (TnxMofMonthHscode4 tnxMofMonthHscode4 : txnMofMonthHscode4List) {
                ObjectNode node = b2BarArray.addObject();
                node.put("TXN_YTM", tnxMofMonthHscode4.getTxnYtm());
                node.put("HSCODE_4", tnxMofMonthHscode4.getHsCode4());
                node.put("TRADE_WEIGHT", tnxMofMonthHscode4.getTradeWeight());
                node.put("TRADE_AMT", tnxMofMonthHscode4.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", tnxMofMonthHscode4.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", tnxMofMonthHscode4.getTradeAmtGrowrate());
                node.put("TRADE_AMT_PERCENT", tnxMofMonthHscode4.getTradeAmtPercent());
                node.put("WEIGHT_DIFFERENCE", tnxMofMonthHscode4.getWeightDifference());
                node.put("WEIGHT_GROWRATE", tnxMofMonthHscode4.getWeightGrowrate());
                node.put("WEIGHT_PERCENT", tnxMofMonthHscode4.getWeightPercent());
            }

            List<TxnMofMonthHscode6Raw> txnMofMonthHscode6RawList = TxnMofMonthHscode6Raw.findByHscodeAndCNTY(hs4, cntyiso3);
            for (TxnMofMonthHscode6Raw txnMofMonthHscode6Raw : txnMofMonthHscode6RawList) {
                ObjectNode node = b2LineArray.addObject();
                node.put("TXN_YTM", txnMofMonthHscode6Raw.getTxnYtm());
                node.put("HSCODE_6", txnMofMonthHscode6Raw.getHscode6());
                node.put("TRADE_WEIGHT", txnMofMonthHscode6Raw.getTradeWeight());
                node.put("TRADE_AMT", txnMofMonthHscode6Raw.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", txnMofMonthHscode6Raw.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", txnMofMonthHscode6Raw.getTradeAmtGrowrate());
                node.put("TRADE_AMT_PERCENT", txnMofMonthHscode6Raw.getTradeAmtPercent());
                node.put("WEIGHT_DIFFERENCE", txnMofMonthHscode6Raw.getWeightDifference());
                node.put("WEIGHT_GROWRATE", txnMofMonthHscode6Raw.getWeightGrowrate());
                node.put("WEIGHT_PERCENT", txnMofMonthHscode6Raw.getWeightPercent());

                node.put("TRADE_AMT_RANK_HSCODE", txnMofMonthHscode6Raw.getTradeAmtRankHscode());
                node.put("TRADE_AMT_DIFFERENCE_RANK_HSCODE", txnMofMonthHscode6Raw.getTradeAmtDifferenceRankHscode());
                node.put("TRADE_AMT_GROWRATE_RANK_HSCODE", txnMofMonthHscode6Raw.getTradeAmtGrowrateRankHscode());
                node.put("TRADE_AMT_PERCENT_RANK_HSCODE", txnMofMonthHscode6Raw.getTradeAmtPercentRankHscode());
                node.put("WEIGHT_RANK_HSCODE", txnMofMonthHscode6Raw.getWeightRankHscode());
                node.put("WEIGHT_DIFFERENCE_RANK_HSCODE", txnMofMonthHscode6Raw.getWeightDifferenceRankHscode());
                node.put("WEIGHT_GROWRATE_RANK_HSCODE", txnMofMonthHscode6Raw.getWeightGrowrateRankHscode());
                node.put("WEIGHT_PERCENT_RANK_HSCODE", txnMofMonthHscode6Raw.getWeightPercentRankHscode());

                node.put("TARIFF", txnMofMonthHscode6Raw.getTariff());
            }
        } else if (timeunit.equals("year")) {
            List<TnxMofYearHscode4> txnMofYearHscode4List = TnxMofYearHscode4.findByHsCode4AndPartnerIso3(hs4, cntyiso3);
            for (TnxMofYearHscode4 tnxMofYearHscode4 : txnMofYearHscode4List) {
                ObjectNode node = b2BarArray.addObject();
                node.put("TXN_YEAR", tnxMofYearHscode4.getTxnYear());
                node.put("HSCODE_4", tnxMofYearHscode4.getHsCode4());
                node.put("TRADE_WEIGHT", tnxMofYearHscode4.getTradeWeight());
                node.put("TRADE_AMT", tnxMofYearHscode4.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", tnxMofYearHscode4.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", tnxMofYearHscode4.getTradeAmtGrowrate());
                node.put("TRADE_AMT_PERCENT", tnxMofYearHscode4.getTradeAmtPercent());
                node.put("WEIGHT_DIFFERENCE", tnxMofYearHscode4.getWeightDifference());
                node.put("WEIGHT_GROWRATE", tnxMofYearHscode4.getWeightGrowrate());
                node.put("WEIGHT_PERCENT", tnxMofYearHscode4.getWeightPercent());
            }

            List<TxnMofYearHscode6Raw> txnMofYearHscode6RawList = TxnMofYearHscode6Raw.findByHscodeAndCNTY(hs4, cntyiso3);
            for (TxnMofYearHscode6Raw txnMofYearHscode6Raw : txnMofYearHscode6RawList) {
                ObjectNode node = b2LineArray.addObject();
                node.put("TXN_YEAR", txnMofYearHscode6Raw.getTxnYear());
                node.put("HSCODE_6", txnMofYearHscode6Raw.getHscode6());
                node.put("TRADE_WEIGHT", txnMofYearHscode6Raw.getTradeWeight());
                node.put("TRADE_AMT", txnMofYearHscode6Raw.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", txnMofYearHscode6Raw.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", txnMofYearHscode6Raw.getTradeAmtGrowrate());
                node.put("TRADE_AMT_PERCENT", txnMofYearHscode6Raw.getTradeAmtPercent());
                node.put("WEIGHT_DIFFERENCE", txnMofYearHscode6Raw.getWeightDifference());
                node.put("WEIGHT_GROWRATE", txnMofYearHscode6Raw.getWeightGrowrate());
                node.put("WEIGHT_PERCENT", txnMofYearHscode6Raw.getWeightPercent());

                node.put("TRADE_AMT_RANK_HSCODE", txnMofYearHscode6Raw.getTradeAmtRankHscode());
                node.put("TRADE_AMT_DIFFERENCE_RANK_HSCODE", txnMofYearHscode6Raw.getTradeAmtDifferenceRankHscode());
                node.put("TRADE_AMT_GROWRATE_RANK_HSCODE", txnMofYearHscode6Raw.getTradeAmtGrowrateRankHscode());
                node.put("TRADE_AMT_PERCENT_RANK_HSCODE", txnMofYearHscode6Raw.getTradeAmtPercentRankHscode());
                node.put("WEIGHT_RANK_HSCODE", txnMofYearHscode6Raw.getWeightRankHscode());
                node.put("WEIGHT_DIFFERENCE_RANK_HSCODE", txnMofYearHscode6Raw.getWeightDifferenceRankHscode());
                node.put("WEIGHT_GROWRATE_RANK_HSCODE", txnMofYearHscode6Raw.getWeightGrowrateRankHscode());
                node.put("WEIGHT_PERCENT_RANK_HSCODE", txnMofYearHscode6Raw.getWeightPercentRankHscode());

                node.put("TARIFF", txnMofYearHscode6Raw.getTariff());
            }
        } else {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }

        end = new Date();
        duration = end.getTime() - start.getTime();
        Logger.of("application").info("B2 duration:{}", duration);

        response.put("B-2", B2Node);
        return ok(response);
    }

    public Result getB3(Http.Request request) {
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

        if (hs6 == null || hs6.isEmpty() || cntyiso3 == null || cntyiso3.isEmpty()) {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toJson()));
        }

        Date start;
        Date end;
        long duration = 0;
        start = new Date();

        if (area != null && area.equals("WLD")) {
            area = null;
        }

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ObjectNode B3Node = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode b3BarArray = B3Node.putArray("bar");
        ArrayNode b3LineArray = B3Node.putArray("line");

        if (timeunit.equals("month")) {
            List<UnWorldMonth> unWorldMonthList = UnWorldMonth.findListByHsCodeAndReporterIso3(hs6, cntyiso3);
            for (UnWorldMonth unWorldMonth : unWorldMonthList) {
                ObjectNode nodeBar = b3BarArray.addObject();
                nodeBar.put("TXN_YTM", unWorldMonth.getTxnYTM());
                nodeBar.put("TRADE_WEIGHT", unWorldMonth.getTradeWeight());
                nodeBar.put("TRADE_AMT", unWorldMonth.getTradeAmt());
                nodeBar.put("TRADE_AMT_DIFFERENCE", unWorldMonth.getTradeAmtDifference());
                nodeBar.put("TRADE_AMT_GROWRATE", unWorldMonth.getTradeAmtGrowrate());
                nodeBar.put("TRADE_AMT_PERCENT", unWorldMonth.getTradeAmtPercent());
                nodeBar.put("WEIGHT_DIFFERENCE", unWorldMonth.getWeightDifference());
                nodeBar.put("WEIGHT_GROWRATE", unWorldMonth.getWeightGrowrate());
                nodeBar.put("WEIGHT_PERCENT", unWorldMonth.getWeightPercent());
            }

            List<UnNonWorldMonth> unNonWorldMonthList = UnNonWorldMonth.findListByHsCodeAndReporterIso3AndAreaId(hs6, cntyiso3, area);
            for (UnNonWorldMonth unNonWorldMonth : unNonWorldMonthList) {
                ObjectNode nodeLine = b3LineArray.addObject();
                nodeLine.put("TXN_YTM", unNonWorldMonth.getTxnYTM());
                nodeLine.put("PARTNER_ISO3", unNonWorldMonth.getPartnerIso3());
                nodeLine.put("TRADE_WEIGHT", unNonWorldMonth.getTradeWeight());
                nodeLine.put("TRADE_AMT", unNonWorldMonth.getTradeAmt());
                nodeLine.put("TRADE_AMT_DIFFERENCE", unNonWorldMonth.getTradeAmtDifference());
                nodeLine.put("TRADE_AMT_GROWRATE", unNonWorldMonth.getTradeAmtGrowrate());
                nodeLine.put("TRADE_AMT_PERCENT", unNonWorldMonth.getTradeAmtPercent());
                nodeLine.put("WEIGHT_DIFFERENCE", unNonWorldMonth.getWeightDifference());
                nodeLine.put("WEIGHT_GROWRATE", unNonWorldMonth.getWeightGrowrate());
                nodeLine.put("WEIGHT_PERCENT", unNonWorldMonth.getWeightPercent());
                nodeLine.put("TRADE_AMT_RANK", unNonWorldMonth.getTradeAmtRank());
                nodeLine.put("TRADE_AMT_DIFFERENCE_RANK", unNonWorldMonth.getTradeAmtDifferenceRank());
                nodeLine.put("TRADE_AMT_GROWRATE_RANK", unNonWorldMonth.getTradeAmtGrowrateRank());
                nodeLine.put("TRADE_AMT_PERCENT_RANK", unNonWorldMonth.getTradeAmtPercentRank());
                nodeLine.put("WEIGHT_RANK", unNonWorldMonth.getWeightRank());
                nodeLine.put("WEIGHT_DIFFERENCE_RANK", unNonWorldMonth.getWeightDifferenceRank());
                nodeLine.put("WEIGHT_GROWRATE_RANK", unNonWorldMonth.getWeightGrowrateRank());
                nodeLine.put("WEIGHT_PERCENT_RANK", unNonWorldMonth.getWeightPercentRank());
                nodeLine.put("COUNTRY_COMM_ZH", unNonWorldMonth.getCountryCommZh());
                nodeLine.put("TARIFF", unNonWorldMonth.getTariff());
                //nodeLine.put("COUNTRY_COMM_EN", unNonWorldYear.getc());
            }

            ArrayNode b3_4Array = response.putArray("B-3-4");
            List<String> b34List = Arrays.asList(new String[3]);
            for (UnNonWorldMonth unNonWorldMonth : unNonWorldMonthList) {
                if (unNonWorldMonth.getTradeAmtRank() != null && unNonWorldMonth.getTradeAmtRank() <= 3) {
                    b34List.set(unNonWorldMonth.getTradeAmtRank() - 1, unNonWorldMonth.getPartnerIso3());
                }
            }
            for (String s : b34List) {
                if (s != null) {
                    b3_4Array.add(s);
                }
            }
        } else if (timeunit.equals("year")) {
            List<UnWorldYear> unWorldYearList = UnWorldYear.findListByHsCodeAndReporterIso3(hs6, cntyiso3);
            for (UnWorldYear unWorldYear : unWorldYearList) {
                ObjectNode nodeBar = b3BarArray.addObject();
                nodeBar.put("TXN_YEAR", unWorldYear.getTxnYear());
                nodeBar.put("TRADE_WEIGHT", unWorldYear.getTradeWeight());
                nodeBar.put("TRADE_AMT", unWorldYear.getTradeAmt());
                nodeBar.put("TRADE_AMT_DIFFERENCE", unWorldYear.getTradeAmtDifference());
                nodeBar.put("TRADE_AMT_GROWRATE", unWorldYear.getTradeAmtGrowrate());
                nodeBar.put("TRADE_AMT_PERCENT", unWorldYear.getTradeAmtPercent());
                nodeBar.put("WEIGHT_DIFFERENCE", unWorldYear.getWeightDifference());
                nodeBar.put("WEIGHT_GROWRATE", unWorldYear.getWeightGrowrate());
                nodeBar.put("WEIGHT_PERCENT", unWorldYear.getWeightPercent());
            }

            List<UnNonWorldYear> unNonWorldYearList = UnNonWorldYear.findListByHsCodeAndReporterIso3AndAreaId(hs6, cntyiso3, area);
            for (UnNonWorldYear unNonWorldYear : unNonWorldYearList) {
                ObjectNode nodeLine = b3LineArray.addObject();
                nodeLine.put("TXN_YEAR", unNonWorldYear.getTxnYear());
                nodeLine.put("PARTNER_ISO3", unNonWorldYear.getPartnerIso3());
                nodeLine.put("TRADE_WEIGHT", unNonWorldYear.getTradeWeight());
                nodeLine.put("TRADE_AMT", unNonWorldYear.getTradeAmt());
                nodeLine.put("TRADE_AMT_DIFFERENCE", unNonWorldYear.getTradeAmtDifference());
                nodeLine.put("TRADE_AMT_GROWRATE", unNonWorldYear.getTradeAmtGrowrate());
                nodeLine.put("TRADE_AMT_PERCENT", unNonWorldYear.getTradeAmtPercent());
                nodeLine.put("WEIGHT_DIFFERENCE", unNonWorldYear.getWeightDifference());
                nodeLine.put("WEIGHT_GROWRATE", unNonWorldYear.getWeightGrowrate());
                nodeLine.put("WEIGHT_PERCENT", unNonWorldYear.getWeightPercent());
                nodeLine.put("TRADE_AMT_RANK", unNonWorldYear.getTradeAmtRank());
                nodeLine.put("TRADE_AMT_DIFFERENCE_RANK", unNonWorldYear.getTradeAmtDifferenceRank());
                nodeLine.put("TRADE_AMT_GROWRATE_RANK", unNonWorldYear.getTradeAmtGrowrateRank());
                nodeLine.put("TRADE_AMT_PERCENT_RANK", unNonWorldYear.getTradeAmtPercentRank());
                nodeLine.put("WEIGHT_RANK", unNonWorldYear.getWeightRank());
                nodeLine.put("WEIGHT_DIFFERENCE_RANK", unNonWorldYear.getWeightDifferenceRank());
                nodeLine.put("WEIGHT_GROWRATE_RANK", unNonWorldYear.getWeightGrowrateRank());
                nodeLine.put("WEIGHT_PERCENT_RANK", unNonWorldYear.getWeightPercentRank());
                nodeLine.put("COUNTRY_COMM_ZH", unNonWorldYear.getCountryCommZh());
                nodeLine.put("TARIFF", unNonWorldYear.getTariff());
                //nodeLine.put("COUNTRY_COMM_EN", unNonWorldYear.getc());
            }

            ArrayNode b3_4Array = response.putArray("B-3-4");
            List<String> b34List = Arrays.asList(new String[3]);
            for (UnNonWorldYear unNonWorldYear : unNonWorldYearList) {
                if (unNonWorldYear.getTradeAmtRank() != null && unNonWorldYear.getTradeAmtRank() <= 3) {
                    b34List.set(unNonWorldYear.getTradeAmtRank() - 1, unNonWorldYear.getPartnerIso3());
                }
            }
            for (String s : b34List) {
                if (s != null) {
                    b3_4Array.add(s);
                }
            }

        } else {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }

        end = new Date();
        duration = end.getTime() - start.getTime();
        Logger.of("application").info("B3 duration:{}", duration);

        response.put("B-3", B3Node);
        return ok(response);
    }
}
