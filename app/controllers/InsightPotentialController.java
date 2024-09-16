package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import datatypes.Error;
import datatypes.InsightType;
import models.*;
import play.Logger;
import play.api.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.MyCache;
import utils.RedisUtils;

import java.sql.Timestamp;
import java.util.*;

public class InsightPotentialController extends Controller {
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
            MemberList memberList = MemberList.findByMemberIdAndType(memberId, InsightType.TRADE_FAVORITE_ADD_HS6.getAction());
            ArrayNode membersArray = response.putArray("member_list");
            if (memberList != null) {
                List<InsightMemberListBean> insightMemberListBeanList = memberList.getContent();
                Collections.reverse(insightMemberListBeanList);
                for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
                    ObjectNode node = membersArray.addObject();
                    node.put("hs_code", insightMemberListBean.getHsCode6());
                }
            }

            //找到推薦的
            ArrayNode recommendArray = response.putArray("recommend_list");
            //List<MemberRemoveRecommendList> memberRemoveRecommendList = MemberRemoveRecommendList.findByMemberIdAndType(memberId, "potential");
            List<MemberTrade> memberTradeList = MemberTrade.findByMemberIdAndType(memberId, "hsc6");

            Set<String> set = new HashSet<>();
            MemberList memberDelRecommendList = MemberList.findByMemberIdAndType(memberId, InsightType.TRADE_RECOMMEND_DEL_HSC6.getAction());
            if (memberDelRecommendList != null) {
                for (InsightMemberListBean insightMemberListBean : memberDelRecommendList.getContent()) {
                    set.add(insightMemberListBean.getHsCode6());
                }
            }

            for (MemberTrade memberTrade : memberTradeList) {
                if (!set.contains(memberTrade.getTradeHscode())) {
                    ObjectNode node = recommendArray.addObject();
                    node.put("hs_code", memberTrade.getTradeHscode());
                }
            }

            ObjectNode defaultNode = response.putObject("default");
            defaultNode.put("hs_code", "854231");
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
                !parameter.has("favor_status")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String memberId = parameter.get("member_id").asText();
        String hsCode = parameter.get("hs_code").asText();
        String favorStatus = parameter.get("favor_status").asText();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        try {
            MemberList memberListADD = MemberList.findByMemberIdAndType(memberId, InsightType.TRADE_FAVORITE_ADD_HS6.getAction());
            boolean doADDUpdate = false;
            boolean doDELUpdate = false;
            if (memberListADD == null) {
                memberListADD = new MemberList();
                memberListADD.setMemberId(memberId);
                memberListADD.setType(InsightType.TRADE_FAVORITE_ADD_HS6.getAction());
                memberListADD.setMemberListUpdateDatetime(timestamp);
            } else {
                memberListADD.setMemberListUpdateDatetime(timestamp);
                doADDUpdate = true;
            }

            MemberList memberListDEL = MemberList.findByMemberIdAndType(memberId, InsightType.TRADE_FAVORITE_DEL_HS6.getAction());
            if (memberListDEL == null) {
                memberListDEL = new MemberList();
                memberListDEL.setMemberId(memberId);
                memberListDEL.setType(InsightType.TRADE_FAVORITE_DEL_HS6.getAction());
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
                    addSet.put(hs6, a);
                }
                if (!addSet.containsKey(hsCode)) {
                    //put add list
                    InsightMemberListBean insightMemberListBean = new InsightMemberListBean();
                    insightMemberListBean.setHsCode6(hsCode);
                    insightMemberListBeanADDList.add(insightMemberListBean);
                    logBean.add(insightMemberListBean);
                }

                //remove del list
                Map<String, Integer> delSet = new HashMap<>();
                for (int a = 0; a < insightMemberListBeanDELList.size(); a++) {
                    String hs6 = insightMemberListBeanDELList.get(a).getHsCode6();
                    delSet.put(hs6, a);
                }

                //Do del
                if (delSet.containsKey(hsCode)) {
                    //Check is exist
                    insightMemberListBeanDELList.remove(delSet.get(hsCode).intValue());
                }

                action = InsightType.TRADE_FAVORITE_ADD_HS6.getCode();
            }
            else if (favorStatus.equals("N")) {
                Map<String, Integer> delSet = new HashMap<>();

                for (int a = 0; a < insightMemberListBeanDELList.size(); a++) {
                    String hs6 = insightMemberListBeanDELList.get(a).getHsCode6();
                    delSet.put(hs6, a);
                }
                if (!delSet.containsKey(hsCode)) {
                    //put del list
                    InsightMemberListBean insightMemberListBean = new InsightMemberListBean();
                    insightMemberListBean.setHsCode6(hsCode);
                    insightMemberListBeanDELList.add(insightMemberListBean);
                    logBean.add(insightMemberListBean);
                }

                //remove add list
                Map<String, Integer> addSet = new HashMap<>();
                for (int a = 0; a < insightMemberListBeanADDList.size(); a++) {
                    String hs6 = insightMemberListBeanADDList.get(a).getHsCode6();
                    addSet.put(hs6, a);
                }

                //Do add
                if (addSet.containsKey(hsCode)) {
                    //Check is exist
                    insightMemberListBeanADDList.remove(addSet.get(hsCode).intValue());
                }

                action = InsightType.TRADE_FAVORITE_DEL_HS6.getCode();
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
                !parameter.has("hs_code")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String memberId = parameter.get("member_id").asText();
        String hsCode = parameter.get("hs_code").asText();
        /*
        MemberRemoveRecommendList memberRemoveRecommendList = MemberRemoveRecommendList.findBy(memberId, "potential", hsCode, null);
        if (memberRemoveRecommendList == null) {
            memberRemoveRecommendList = new MemberRemoveRecommendList();
            memberRemoveRecommendList.setMemberId(memberId);
            memberRemoveRecommendList.setType("potential");
            memberRemoveRecommendList.setHscode(hsCode);
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
                String hs6 = insightMemberListBeanDELList.get(a).getHsCode6();
                delSet.put(hs6, a);
            }
            if (!delSet.containsKey(hsCode)) {
                //put del list
                InsightMemberListBean insightMemberListBean = new InsightMemberListBean();
                insightMemberListBean.setHsCode6(hsCode);
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
        if (!parameter.has("hs6")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String hs6 = parameter.get("hs6").asText();
        if (hs6 == null || hs6.isEmpty()) {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toJson()));
        }

        Date start;
        Date end;
        long duration = 0;
        start = new Date();

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ObjectNode existNode = new ObjectNode(JsonNodeFactory.instance);
        existNode.put("C-1", true);
        existNode.put("C-2", true);
        existNode.put("C-3", true);

        ArrayNode c0_1Array = response.putArray("C-0-1");
        ArrayNode c1_1Array = response.putArray("C-1-1");
        ArrayNode c2_1Array = response.putArray("C-2-1");
        ArrayNode c3_4Array = response.putArray("C-3-4");

        //C-1 LINE
        List<OthUnWorldYear> othUnWorldYearList = OthUnWorldYear.findListByHsCodeAndReporterIso3AndAreaId(hs6, null, null);
        ObjectNode C1Node = new ObjectNode(JsonNodeFactory.instance);
        List<String> c11List = Arrays.asList(new String[3]);
        ArrayNode c1LineArray = C1Node.putArray("line");
        for (OthUnWorldYear othUnWorldYear : othUnWorldYearList) {
            ObjectNode node = c1LineArray.addObject();
            node.put("REPORTER_ISO3", othUnWorldYear.getReporterIso3());
            node.put("TRADE_AMT", othUnWorldYear.getTradeAmt());
            node.put("TRADE_AMT_DIFFERENCE", othUnWorldYear.getTradeAmtDifference());
            node.put("TRADE_AMT_GROWRATE", othUnWorldYear.getTradeAmtGrowrate());
            node.put("TRADE_WEIGHT", othUnWorldYear.getTradeWeight());
            node.put("WEIGHT_DIFFERENCE", othUnWorldYear.getWeightDifference());
            node.put("WEIGHT_GROWRATE", othUnWorldYear.getWeightGrowrate());
            node.put("TRADE_AMT_RANK", othUnWorldYear.getTradeAmtRank());
            node.put("TRADE_AMT_DIFFERENCE_RANK", othUnWorldYear.getTradeAmtDifferenceRank());
            node.put("TRADE_AMT_GROWRATE_RANK", othUnWorldYear.getTradeAmtGrowrateRank());
            node.put("TRADE_AMT_PERCENT_RANK", othUnWorldYear.getTradeAmtPercentRank());
            node.put("WEIGHT_RANK", othUnWorldYear.getWeightRank());
            node.put("WEIGHT_DIFFERENCE_RANK", othUnWorldYear.getWeightDifferenceRank());
            node.put("WEIGHT_GROWRATE_RANK", othUnWorldYear.getWeightGrowrateRank());
            node.put("WEIGHT_PERCENT_RANK", othUnWorldYear.getWeightPercentRank());
            node.put("COUNTRY_COMM_ZH", othUnWorldYear.getCountryCommZh());
            node.put("TXN_YEAR", othUnWorldYear.getTxnYear());
            //C-1-1 Top3
            if (othUnWorldYear.getTradeAmtRank() != null && othUnWorldYear.getTradeAmtRank() <= 3) {
                c11List.set(othUnWorldYear.getTradeAmtRank() - 1, othUnWorldYear.getReporterIso3());
            }
        }
        if (othUnWorldYearList.size() == 0) {
            existNode.put("C-1", false);
        }
        //C-1-1 Top3
        /*
        List<String> c11List = Arrays.asList(new String[3]);
        for (OthUnWorldYear othUnWorldYear : othUnWorldYearList) {
            if (othUnWorldYear.getTradeAmtRank() != null && othUnWorldYear.getTradeAmtRank() <= 3) {
                c11List.set(othUnWorldYear.getTradeAmtRank() - 1, othUnWorldYear.getReporterIso3());
            }
        }
        */
        for (String s : c11List) {
            if (s != null) {
                c1_1Array.add(s);
            }
        }
        othUnWorldYearList = null;

        //C-2 BAR LINE
        ObjectNode C2Node = new ObjectNode(JsonNodeFactory.instance);
        List<String> c21List = Arrays.asList(new String[3]);
        ArrayNode c2BarArray = C2Node.putArray("bar");
        List<MofWorldYear> mofWorldYearList = MofWorldYear.findListByHsCode(hs6);
        for (MofWorldYear mofWorldYear : mofWorldYearList) {
            ObjectNode node = c2BarArray.addObject();
            node.put("PARTNER_ISO3", mofWorldYear.getPartnerIso3());
            node.put("TRADE_AMT", mofWorldYear.getTradeAmt());
            node.put("TRADE_AMT_DIFFERENCE", mofWorldYear.getTradeAmtDifference());
            node.put("TRADE_AMT_GROWRATE", mofWorldYear.getTradeAmtGrowrate());
            node.put("TRADE_WEIGHT", mofWorldYear.getTradeWeight());
            node.put("WEIGHT_DIFFERENCE", mofWorldYear.getWeightDifference());
            node.put("WEIGHT_GROWRATE", mofWorldYear.getWeightGrowrate());
            node.put("TRADE_AMT_PERCENT", mofWorldYear.getTradeAmtPercent());
            node.put("WEIGHT_PERCENT", mofWorldYear.getWeightPercent());
            node.put("TXN_YEAR", mofWorldYear.getTxnYear());
        }

        ArrayNode c2LineArray = C2Node.putArray("line");
        List<MofNonWorldYear> mofNonWorldYearList = MofNonWorldYear.findListByHsCodeAndAreaId(hs6, null);
        for (MofNonWorldYear mofNonWorldYear : mofNonWorldYearList) {
            ObjectNode node = c2LineArray.addObject();
            node.put("PARTNER_ISO3", mofNonWorldYear.getPartnerIso3());
            node.put("TRADE_AMT", mofNonWorldYear.getTradeAmt());
            node.put("TRADE_AMT_DIFFERENCE", mofNonWorldYear.getTradeAmtDifference());
            node.put("TRADE_AMT_GROWRATE", mofNonWorldYear.getTradeAmtGrowrate());
            node.put("TRADE_WEIGHT", mofNonWorldYear.getTradeWeight());
            node.put("WEIGHT_DIFFERENCE", mofNonWorldYear.getWeightDifference());
            node.put("WEIGHT_GROWRATE", mofNonWorldYear.getWeightGrowrate());
            node.put("TRADE_AMT_PERCENT", mofNonWorldYear.getTradeAmtPercent());
            node.put("WEIGHT_PERCENT", mofNonWorldYear.getWeightPercent());
            node.put("TRADE_AMT_RANK", mofNonWorldYear.getTradeAmtRank());
            node.put("TRADE_AMT_DIFFERENCE_RANK", mofNonWorldYear.getTradeAmtDifferenceRank());
            node.put("TRADE_AMT_GROWRATE_RANK", mofNonWorldYear.getTradeAmtGrowrateRank());
            node.put("TRADE_AMT_PERCENT_RANK", mofNonWorldYear.getTradeAmtPercentRank());
            node.put("WEIGHT_RANK", mofNonWorldYear.getWeightRank());
            node.put("WEIGHT_DIFFERENCE_RANK", mofNonWorldYear.getWeightDifferenceRank());
            node.put("WEIGHT_GROWRATE_RANK", mofNonWorldYear.getWeightGrowrateRank());
            node.put("WEIGHT_PERCENT_RANK", mofNonWorldYear.getWeightPercentRank());
            node.put("COUNTRY_COMM_ZH", mofNonWorldYear.getCountryCommZh());
            node.put("TXN_YEAR", mofNonWorldYear.getTxnYear());
            //C-2-1 Top3
            if (mofNonWorldYear.getTradeAmtRank() != null && mofNonWorldYear.getTradeAmtRank() <= 3) {
                c21List.set(mofNonWorldYear.getTradeAmtRank() - 1, mofNonWorldYear.getPartnerIso3());
            }
        }
        if (mofWorldYearList.size() == 0 && mofNonWorldYearList.size() == 0) {
            existNode.put("C-2", false);
        }

        //C-2-1 Top3
        /*
        List<String> c21List = Arrays.asList(new String[3]);
        for (MofNonWorldYear mofNonWorldYear : mofNonWorldYearList) {
            if (mofNonWorldYear.getTradeAmtRank() != null && mofNonWorldYear.getTradeAmtRank() <= 3) {
                c21List.set(mofNonWorldYear.getTradeAmtRank() - 1, mofNonWorldYear.getPartnerIso3());
            }
        }
        */
        for (String s : c21List) {
            if (s != null) {
                c2_1Array.add(s);
            }
        }

        //C-3-1 Top 10 Order by year then order by amount of export from TWN
        ObjectNode C3Node = new ObjectNode(JsonNodeFactory.instance);
        List<C3TableRaw> c3TableRawList = new ArrayList<>();
        c3TableRawList = myCache.getC3TableCache(hs6);
        if (c3TableRawList == null) {
            c3TableRawList = C3TableRaw.findByHscode(hs6);
            myCache.putC3TableCache(hs6, c3TableRawList);
        }
        ArrayNode c3TableArray = C3Node.putArray("table");
        //List<C3TableRaw> c3TableRawList = C3TableRaw.findByHscode(hs6);
        C3TableRaw c3TableRawFirst = c3TableRawList.isEmpty() ? null : c3TableRawList.get(0);
        boolean twnGroupAbove0 = false;
        boolean hasTwnAmt = false;
        for (C3TableRaw c3TableRaw : c3TableRawList) {
            ObjectNode node = c3TableArray.addObject();
            node.put("REPORTER_ISO3", c3TableRaw.getReporterIso3());
            node.put("TXN_YEAR", c3TableRaw.getTxnYear());
            node.put("TRADE_AMT", c3TableRaw.getTradeAmt());
            node.put("TRADE_AMT_GROWRATE", c3TableRaw.getTradeAmtGrowrate());
            node.put("TWN_AMT", c3TableRaw.getTwnAmt());
            node.put("TWV_GROWRATE", c3TableRaw.getTwvGrowrate());
            node.put("TARIFF", c3TableRaw.getTariff());
            node.put("COUNTRY_COMM_ZH", c3TableRaw.getCountryCommZh());

            if (c3TableRaw.getTwvGrowrate() != null && Double.valueOf(c3TableRaw.getTwvGrowrate()) > 0) {
                twnGroupAbove0 = true;
            }
            if (c3TableRaw.getTwnAmt() != null) {
                hasTwnAmt = true;
            }
        }
        if (c3TableRawList.size() == 0) {
            existNode.put("C-3", false);
        }

        //C-0-1 Top5
        int count = 0;
        for (C3TableRaw c3TableRaw : c3TableRawList) {
            if (count >= 5) {
                break;
            }
            c0_1Array.add(c3TableRaw.getReporterIso3());
            count++;
        }

        //C-3-2 Export from TWN and amount >= 0
        if (!twnGroupAbove0 || !hasTwnAmt) {
            response.put("C-3-2", false);
        } else {
            response.put("C-3-2", true);
        }

        //C-3-3 From C-3-1
        if (c3TableRawFirst != null) {
            response.put("C-3-3", c3TableRawFirst.getReporterIso3());
        }

        //C-3 BAR LINE From C-3-1 First row
        ArrayNode c3BarArray = C3Node.putArray("bar");
        if (c3TableRawFirst != null) {
            List<UnWorldYear> unWorldYearList = UnWorldYear.findListByHsCodeAndReporterIso3(hs6, c3TableRawFirst.getReporterIso3());
            for (UnWorldYear unWorldYear : unWorldYearList) {
                ObjectNode node = c3BarArray.addObject();
                node.put("PARTNER_ISO3", unWorldYear.getPartnerIso3());
                node.put("REPORTER_ISO3", unWorldYear.getReporterIso3());
                node.put("TRADE_AMT", unWorldYear.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", unWorldYear.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", unWorldYear.getTradeAmtGrowrate());
                node.put("TRADE_AMT_PERCENT", unWorldYear.getTradeAmtPercent());
                node.put("TRADE_WEIGHT", unWorldYear.getTradeWeight());
                node.put("WEIGHT_DIFFERENCE", unWorldYear.getWeightDifference());
                node.put("WEIGHT_GROWRATE", unWorldYear.getWeightGrowrate());
                node.put("WEIGHT_PERCENT", unWorldYear.getWeightPercent());
                node.put("TXN_YEAR", unWorldYear.getTxnYear());
            }
        }

        List<String> c34List = Arrays.asList(new String[3]);
        ArrayNode c3LineArray = C3Node.putArray("line");
        if (c3TableRawFirst != null) {
            List<UnNonWorldYear> unNonWorldYearList = UnNonWorldYear.findListByHsCodeAndReporterIso3AndAreaId(hs6, c3TableRawFirst.getReporterIso3(), null);
            for (UnNonWorldYear unNonWorldYear : unNonWorldYearList) {
                ObjectNode node = c3LineArray.addObject();
                node.put("PARTNER_ISO3", unNonWorldYear.getPartnerIso3());
                node.put("REPORTER_ISO3", unNonWorldYear.getReporterIso3());
                node.put("TRADE_AMT", unNonWorldYear.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", unNonWorldYear.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", unNonWorldYear.getTradeAmtGrowrate());
                node.put("TRADE_WEIGHT", unNonWorldYear.getTradeWeight());
                node.put("WEIGHT_DIFFERENCE", unNonWorldYear.getWeightDifference());
                node.put("WEIGHT_GROWRATE", unNonWorldYear.getWeightGrowrate());
                node.put("TRADE_AMT_PERCENT", unNonWorldYear.getTradeAmtPercent());
                node.put("WEIGHT_PERCENT", unNonWorldYear.getWeightPercent());
                node.put("TRADE_AMT_RANK", unNonWorldYear.getTradeAmtRank());
                node.put("TRADE_AMT_DIFFERENCE_RANK", unNonWorldYear.getTradeAmtDifferenceRank());
                node.put("TRADE_AMT_GROWRATE_RANK", unNonWorldYear.getTradeAmtGrowrateRank());
                node.put("TRADE_AMT_PERCENT_RANK", unNonWorldYear.getTradeAmtPercentRank());
                node.put("WEIGHT_RANK", unNonWorldYear.getWeightRank());
                node.put("WEIGHT_DIFFERENCE_RANK", unNonWorldYear.getWeightDifferenceRank());
                node.put("WEIGHT_GROWRATE_RANK", unNonWorldYear.getWeightGrowrateRank());
                node.put("WEIGHT_PERCENT_RANK", unNonWorldYear.getWeightPercentRank());
                node.put("COUNTRY_COMM_ZH", unNonWorldYear.getCountryCommZh());
                node.put("TXN_YEAR", unNonWorldYear.getTxnYear());
                node.put("TARIFF", unNonWorldYear.getTariff());
                //C-3-4 Top3
                if (unNonWorldYear.getTradeAmtRank() != null && unNonWorldYear.getTradeAmtRank() <= 3) {
                    c34List.set(unNonWorldYear.getTradeAmtRank() - 1, unNonWorldYear.getPartnerIso3());
                }
            }
            //C-3-4 Top3
            /*
            List<String> c34List = Arrays.asList(new String[3]);
            for (UnNonWorldYear unNonWorldYear : unNonWorldYearList) {
                if (unNonWorldYear.getTradeAmtRank() != null && unNonWorldYear.getTradeAmtRank() <= 3) {
                    c34List.set(unNonWorldYear.getTradeAmtRank() - 1, unNonWorldYear.getPartnerIso3());
                }
            }
            */
            for (String s : c34List) {
                if (s != null) {
                    c3_4Array.add(s);
                }
            }
        }

        end = new Date();
        duration = end.getTime() - start.getTime();
        Logger.of("application").info("C0 duration:{}", duration);

        response.put("exist", existNode);
        response.put("C-1", C1Node);
        response.put("C-2", C2Node);
        response.put("C-3", C3Node);
        return ok(response);
    }

    public Result getC1(Http.Request request) {
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
        ArrayNode c1Array = response.putArray("C-1");
        if (timeunit.equals("month")) {
            List<OthUnWorldMonth> othUnWorldMonthList = OthUnWorldMonth.findListByHsCodeAndReporterIso3AndAreaId(hs6, null, area);
            for (OthUnWorldMonth othUnWorldMonth : othUnWorldMonthList) {
                ObjectNode node = c1Array.addObject();
                node.put("REPORTER_ISO3", othUnWorldMonth.getReporterIso3());
                node.put("TRADE_AMT", othUnWorldMonth.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", othUnWorldMonth.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", othUnWorldMonth.getTradeAmtGrowrate());
                node.put("TRADE_WEIGHT", othUnWorldMonth.getTradeWeight());
                node.put("WEIGHT_DIFFERENCE", othUnWorldMonth.getWeightDifference());
                node.put("WEIGHT_GROWRATE", othUnWorldMonth.getWeightGrowrate());
                node.put("TRADE_AMT_RANK", othUnWorldMonth.getTradeAmtRank());
                node.put("TRADE_AMT_DIFFERENCE_RANK", othUnWorldMonth.getTradeAmtDifferenceRank());
                node.put("TRADE_AMT_GROWRATE_RANK", othUnWorldMonth.getTradeAmtGrowrateRank());
                node.put("TRADE_AMT_PERCENT_RANK", othUnWorldMonth.getTradeAmtPercentRank());
                node.put("WEIGHT_RANK", othUnWorldMonth.getWeightRank());
                node.put("WEIGHT_DIFFERENCE_RANK", othUnWorldMonth.getWeightDifferenceRank());
                node.put("WEIGHT_GROWRATE_RANK", othUnWorldMonth.getWeightGrowrateRank());
                node.put("WEIGHT_PERCENT_RANK", othUnWorldMonth.getWeightPercentRank());
                node.put("COUNTRY_COMM_ZH", othUnWorldMonth.getCountryCommZh());
                node.put("TXN_YTM", othUnWorldMonth.getTxnYTM());
            }
        } else if (timeunit.equals("year")) {
            List<OthUnWorldYear> othUnWorldYearList = OthUnWorldYear.findListByHsCodeAndReporterIso3AndAreaId(hs6, null, area);
            for (OthUnWorldYear othUnWorldYear : othUnWorldYearList) {
                ObjectNode node = c1Array.addObject();
                node.put("REPORTER_ISO3", othUnWorldYear.getReporterIso3());
                node.put("TRADE_AMT", othUnWorldYear.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", othUnWorldYear.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", othUnWorldYear.getTradeAmtGrowrate());
                node.put("TRADE_WEIGHT", othUnWorldYear.getTradeWeight());
                node.put("WEIGHT_DIFFERENCE", othUnWorldYear.getWeightDifference());
                node.put("WEIGHT_GROWRATE", othUnWorldYear.getWeightGrowrate());
                node.put("TRADE_AMT_RANK", othUnWorldYear.getTradeAmtRank());
                node.put("TRADE_AMT_DIFFERENCE_RANK", othUnWorldYear.getTradeAmtDifferenceRank());
                node.put("TRADE_AMT_GROWRATE_RANK", othUnWorldYear.getTradeAmtGrowrateRank());
                node.put("TRADE_AMT_PERCENT_RANK", othUnWorldYear.getTradeAmtPercentRank());
                node.put("WEIGHT_RANK", othUnWorldYear.getWeightRank());
                node.put("WEIGHT_DIFFERENCE_RANK", othUnWorldYear.getWeightDifferenceRank());
                node.put("WEIGHT_GROWRATE_RANK", othUnWorldYear.getWeightGrowrateRank());
                node.put("WEIGHT_PERCENT_RANK", othUnWorldYear.getWeightPercentRank());
                node.put("COUNTRY_COMM_ZH", othUnWorldYear.getCountryCommZh());
                node.put("TXN_YEAR", othUnWorldYear.getTxnYear());
            }
        } else {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }

        end = new Date();
        duration = end.getTime() - start.getTime();
        Logger.of("application").info("C1 duration:{}", duration);

        return ok(response);
    }

    public Result getC2(Http.Request request) {
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
        ObjectNode C2Node = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode c2BarArray = C2Node.putArray("bar");
        ArrayNode c2LineArray = C2Node.putArray("line");

        if (timeunit.equals("month")) {
            List<MofWorldMonth> mofWorldMonthList = MofWorldMonth.findListByHsCode(hs6);
            for (MofWorldMonth mofWorldMonth : mofWorldMonthList) {
                ObjectNode node = c2BarArray.addObject();
                node.put("PARTNER_ISO3", mofWorldMonth.getPartnerIso3());
                node.put("TRADE_AMT", mofWorldMonth.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", mofWorldMonth.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", mofWorldMonth.getTradeAmtGrowrate());
                node.put("TRADE_WEIGHT", mofWorldMonth.getTradeWeight());
                node.put("WEIGHT_DIFFERENCE", mofWorldMonth.getWeightDifference());
                node.put("WEIGHT_GROWRATE", mofWorldMonth.getWeightGrowrate());
                node.put("TRADE_AMT_PERCENT", mofWorldMonth.getTradeAmtPercent());
                node.put("WEIGHT_PERCENT", mofWorldMonth.getWeightPercent());
                node.put("TXN_YTM", mofWorldMonth.getTxnYTM());
            }

            List<MofNonWorldMonth> mofNonWorldMonths = MofNonWorldMonth.findListByHsCodeAndAreaId(hs6, area);
            for (MofNonWorldMonth mofNonWorldMonth : mofNonWorldMonths) {
                ObjectNode node = c2LineArray.addObject();
                node.put("PARTNER_ISO3", mofNonWorldMonth.getPartnerIso3());
                node.put("TRADE_AMT", mofNonWorldMonth.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", mofNonWorldMonth.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", mofNonWorldMonth.getTradeAmtGrowrate());
                node.put("TRADE_WEIGHT", mofNonWorldMonth.getTradeWeight());
                node.put("WEIGHT_DIFFERENCE", mofNonWorldMonth.getWeightDifference());
                node.put("WEIGHT_GROWRATE", mofNonWorldMonth.getWeightGrowrate());
                node.put("TRADE_AMT_PERCENT", mofNonWorldMonth.getTradeAmtPercent());
                node.put("WEIGHT_PERCENT", mofNonWorldMonth.getWeightPercent());
                node.put("TRADE_AMT_RANK", mofNonWorldMonth.getTradeAmtRank());
                node.put("TRADE_AMT_DIFFERENCE_RANK", mofNonWorldMonth.getTradeAmtDifferenceRank());
                node.put("TRADE_AMT_GROWRATE_RANK", mofNonWorldMonth.getTradeAmtGrowrateRank());
                node.put("TRADE_AMT_PERCENT_RANK", mofNonWorldMonth.getTradeAmtPercentRank());
                node.put("WEIGHT_RANK", mofNonWorldMonth.getWeightRank());
                node.put("WEIGHT_DIFFERENCE_RANK", mofNonWorldMonth.getWeightDifferenceRank());
                node.put("WEIGHT_GROWRATE_RANK", mofNonWorldMonth.getWeightGrowrateRank());
                node.put("WEIGHT_PERCENT_RANK", mofNonWorldMonth.getWeightPercentRank());
                node.put("COUNTRY_COMM_ZH", mofNonWorldMonth.getCountryCommZh());
                node.put("TXN_YTM", mofNonWorldMonth.getTxnYTM());
            }

        } else if (timeunit.equals("year")) {
            List<MofWorldYear> mofWorldYearList = MofWorldYear.findListByHsCode(hs6);
            for (MofWorldYear mofWorldYear : mofWorldYearList) {
                ObjectNode node = c2BarArray.addObject();
                node.put("PARTNER_ISO3", mofWorldYear.getPartnerIso3());
                node.put("TRADE_AMT", mofWorldYear.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", mofWorldYear.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", mofWorldYear.getTradeAmtGrowrate());
                node.put("TRADE_WEIGHT", mofWorldYear.getTradeWeight());
                node.put("WEIGHT_DIFFERENCE", mofWorldYear.getWeightDifference());
                node.put("WEIGHT_GROWRATE", mofWorldYear.getWeightGrowrate());
                node.put("TRADE_AMT_PERCENT", mofWorldYear.getTradeAmtPercent());
                node.put("WEIGHT_PERCENT", mofWorldYear.getWeightPercent());
                node.put("TXN_YEAR", mofWorldYear.getTxnYear());
            }

            List<MofNonWorldYear> mofNonWorldYearList = MofNonWorldYear.findListByHsCodeAndAreaId(hs6, area);
            for (MofNonWorldYear mofNonWorldYear : mofNonWorldYearList) {
                ObjectNode node = c2LineArray.addObject();
                node.put("PARTNER_ISO3", mofNonWorldYear.getPartnerIso3());
                node.put("TRADE_AMT", mofNonWorldYear.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", mofNonWorldYear.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", mofNonWorldYear.getTradeAmtGrowrate());
                node.put("TRADE_WEIGHT", mofNonWorldYear.getTradeWeight());
                node.put("WEIGHT_DIFFERENCE", mofNonWorldYear.getWeightDifference());
                node.put("WEIGHT_GROWRATE", mofNonWorldYear.getWeightGrowrate());
                node.put("TRADE_AMT_PERCENT", mofNonWorldYear.getTradeAmtPercent());
                node.put("WEIGHT_PERCENT", mofNonWorldYear.getWeightPercent());
                node.put("TRADE_AMT_RANK", mofNonWorldYear.getTradeAmtRank());
                node.put("TRADE_AMT_DIFFERENCE_RANK", mofNonWorldYear.getTradeAmtDifferenceRank());
                node.put("TRADE_AMT_GROWRATE_RANK", mofNonWorldYear.getTradeAmtGrowrateRank());
                node.put("TRADE_AMT_PERCENT_RANK", mofNonWorldYear.getTradeAmtPercentRank());
                node.put("WEIGHT_RANK", mofNonWorldYear.getWeightRank());
                node.put("WEIGHT_DIFFERENCE_RANK", mofNonWorldYear.getWeightDifferenceRank());
                node.put("WEIGHT_GROWRATE_RANK", mofNonWorldYear.getWeightGrowrateRank());
                node.put("WEIGHT_PERCENT_RANK", mofNonWorldYear.getWeightPercentRank());
                node.put("COUNTRY_COMM_ZH", mofNonWorldYear.getCountryCommZh());
                node.put("TXN_YEAR", mofNonWorldYear.getTxnYear());
            }

        } else {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }

        end = new Date();
        duration = end.getTime() - start.getTime();
        Logger.of("application").info("C2 duration:{}", duration);

        response.put("C-2", C2Node);
        return ok(response);
    }

    public Result getC3P1(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        /*
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }
        */
        JsonNode parameter = request.body().asJson();
        if (!parameter.has("hs6")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String hs6 = parameter.get("hs6").asText();

        if (hs6 == null || hs6.isEmpty()) {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toJson()));
        }

        Date start;
        Date end;
        long duration = 0;
        start = new Date();

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode c3_1Array = response.putArray("C-3-1");

        List<C3TableRaw> c3TableRawList = C3TableRaw.findByHscode(hs6);
        C3TableRaw c3TableRawFirst = c3TableRawList.isEmpty() ? null : c3TableRawList.get(0);
        boolean twnGroupAbove0 = false;
        boolean hasTwnAmt = false;
        for (C3TableRaw c3TableRaw : c3TableRawList) {
            ObjectNode node = c3_1Array.addObject();
            node.put("REPORTER_ISO3", c3TableRaw.getReporterIso3());
            node.put("TXN_YEAR", c3TableRaw.getTxnYear());
            node.put("TRADE_AMT", c3TableRaw.getTradeAmt());
            node.put("TRADE_AMT_GROWRATE", c3TableRaw.getTradeAmtGrowrate());
            node.put("TWN_AMT", c3TableRaw.getTwnAmt());
            node.put("TWV_GROWRATE", c3TableRaw.getTwvGrowrate());
            node.put("TARIFF", c3TableRaw.getTariff());
            node.put("COUNTRY_COMM_ZH", c3TableRaw.getCountryCommZh());

            if (c3TableRaw.getTwvGrowrate() != null && Double.valueOf(c3TableRaw.getTwvGrowrate()) > 0) {
                twnGroupAbove0 = true;
            }
            if (c3TableRaw.getTwnAmt() != null) {
                hasTwnAmt = true;
            }
        }

        //C-3-2 Export from TWN and amount >= 0
        if (!twnGroupAbove0 || !hasTwnAmt) {
            response.put("C-3-2", false);
        } else {
            response.put("C-3-2", true);
        }

        //C-3-3 From C-3-1
        if (c3TableRawFirst != null) {
            response.put("C-3-3", c3TableRawFirst.getReporterIso3());
        }

        end = new Date();
        duration = end.getTime() - start.getTime();
        Logger.of("application").info("C3P1 duration:{}", duration);

        return ok(response);
    }

    public Result getC3P2(Http.Request request) {
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
                !parameter.has("cntyiso3") ||
                !parameter.has("area")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String hs6 = parameter.get("hs6").asText();
        String timeunit = parameter.get("timeunit").asText();
        String cntyiso3 = parameter.get("cntyiso3").asText();
        String area = parameter.get("area").isNull() ? null : parameter.get("area").asText();

        if (hs6 == null || hs6.isEmpty()) {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toJson()));
        }

        Date start;
        Date end;
        long duration = 0;
        start = new Date();

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ObjectNode C3Node = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode c3BarArray = C3Node.putArray("bar");
        ArrayNode c3LineArray = C3Node.putArray("line");

        if (timeunit.equals("month")) {
            List<UnWorldMonth> unWorldMonthList = UnWorldMonth.findListByHsCodeAndReporterIso3(hs6, cntyiso3);
            for (UnWorldMonth unWorldMonth : unWorldMonthList) {
                ObjectNode node = c3BarArray.addObject();
                node.put("PARTNER_ISO3", unWorldMonth.getPartnerIso3());
                node.put("REPORTER_ISO3", unWorldMonth.getReporterIso3());
                node.put("TRADE_AMT", unWorldMonth.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", unWorldMonth.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", unWorldMonth.getTradeAmtGrowrate());
                node.put("TRADE_AMT_PERCENT", unWorldMonth.getTradeAmtPercent());
                node.put("TRADE_WEIGHT", unWorldMonth.getTradeWeight());
                node.put("WEIGHT_DIFFERENCE", unWorldMonth.getWeightDifference());
                node.put("WEIGHT_GROWRATE", unWorldMonth.getWeightGrowrate());
                node.put("WEIGHT_PERCENT", unWorldMonth.getWeightPercent());
                node.put("TXN_YTM", unWorldMonth.getTxnYTM());
            }

            List<UnNonWorldMonth> unNonWorldMonthList = UnNonWorldMonth.findListByHsCodeAndReporterIso3AndAreaId(hs6, cntyiso3, area);
            for (UnNonWorldMonth unNonWorldMonth : unNonWorldMonthList) {
                ObjectNode node = c3LineArray.addObject();
                node.put("PARTNER_ISO3", unNonWorldMonth.getPartnerIso3());
                node.put("REPORTER_ISO3", unNonWorldMonth.getReporterIso3());
                node.put("TRADE_AMT", unNonWorldMonth.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", unNonWorldMonth.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", unNonWorldMonth.getTradeAmtGrowrate());
                node.put("TRADE_WEIGHT", unNonWorldMonth.getTradeWeight());
                node.put("WEIGHT_DIFFERENCE", unNonWorldMonth.getWeightDifference());
                node.put("WEIGHT_GROWRATE", unNonWorldMonth.getWeightGrowrate());
                node.put("TRADE_AMT_PERCENT", unNonWorldMonth.getTradeAmtPercent());
                node.put("WEIGHT_PERCENT", unNonWorldMonth.getWeightPercent());
                node.put("TRADE_AMT_RANK", unNonWorldMonth.getTradeAmtRank());
                node.put("TRADE_AMT_DIFFERENCE_RANK", unNonWorldMonth.getTradeAmtDifferenceRank());
                node.put("TRADE_AMT_GROWRATE_RANK", unNonWorldMonth.getTradeAmtGrowrateRank());
                node.put("TRADE_AMT_PERCENT_RANK", unNonWorldMonth.getTradeAmtPercentRank());
                node.put("WEIGHT_RANK", unNonWorldMonth.getWeightRank());
                node.put("WEIGHT_DIFFERENCE_RANK", unNonWorldMonth.getWeightDifferenceRank());
                node.put("WEIGHT_GROWRATE_RANK", unNonWorldMonth.getWeightGrowrateRank());
                node.put("WEIGHT_PERCENT_RANK", unNonWorldMonth.getWeightPercentRank());
                node.put("COUNTRY_COMM_ZH", unNonWorldMonth.getCountryCommZh());
                node.put("TXN_YTM", unNonWorldMonth.getTxnYTM());
                node.put("TARIFF", unNonWorldMonth.getTariff());
            }

            ArrayNode c3_4Array = response.putArray("B-3-4");
            List<String> c34List = Arrays.asList(new String[3]);
            for (UnNonWorldMonth unNonWorldMonth : unNonWorldMonthList) {
                if (unNonWorldMonth.getTradeAmtRank() != null && unNonWorldMonth.getTradeAmtRank() <= 3) {
                    c34List.set(unNonWorldMonth.getTradeAmtRank() - 1, unNonWorldMonth.getPartnerIso3());
                }
            }
            for (String s : c34List) {
                if (s != null) {
                    c3_4Array.add(s);
                }
            }
        } else if (timeunit.equals("year")) {
            List<UnWorldYear> unWorldYearList = UnWorldYear.findListByHsCodeAndReporterIso3(hs6, cntyiso3);
            for (UnWorldYear unWorldYear : unWorldYearList) {
                ObjectNode node = c3BarArray.addObject();
                node.put("PARTNER_ISO3", unWorldYear.getPartnerIso3());
                node.put("REPORTER_ISO3", unWorldYear.getReporterIso3());
                node.put("TRADE_AMT", unWorldYear.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", unWorldYear.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", unWorldYear.getTradeAmtGrowrate());
                node.put("TRADE_AMT_PERCENT", unWorldYear.getTradeAmtPercent());
                node.put("TRADE_WEIGHT", unWorldYear.getTradeWeight());
                node.put("WEIGHT_DIFFERENCE", unWorldYear.getWeightDifference());
                node.put("WEIGHT_GROWRATE", unWorldYear.getWeightGrowrate());
                node.put("WEIGHT_PERCENT", unWorldYear.getWeightPercent());
                node.put("TXN_YEAR", unWorldYear.getTxnYear());
            }

            List<UnNonWorldYear> unNonWorldYearList = UnNonWorldYear.findListByHsCodeAndReporterIso3AndAreaId(hs6, cntyiso3, area);
            for (UnNonWorldYear unNonWorldYear : unNonWorldYearList) {
                ObjectNode node = c3LineArray.addObject();
                node.put("PARTNER_ISO3", unNonWorldYear.getPartnerIso3());
                node.put("REPORTER_ISO3", unNonWorldYear.getReporterIso3());
                node.put("TRADE_AMT", unNonWorldYear.getTradeAmt());
                node.put("TRADE_AMT_DIFFERENCE", unNonWorldYear.getTradeAmtDifference());
                node.put("TRADE_AMT_GROWRATE", unNonWorldYear.getTradeAmtGrowrate());
                node.put("TRADE_WEIGHT", unNonWorldYear.getTradeWeight());
                node.put("WEIGHT_DIFFERENCE", unNonWorldYear.getWeightDifference());
                node.put("WEIGHT_GROWRATE", unNonWorldYear.getWeightGrowrate());
                node.put("TRADE_AMT_PERCENT", unNonWorldYear.getTradeAmtPercent());
                node.put("WEIGHT_PERCENT", unNonWorldYear.getWeightPercent());
                node.put("TRADE_AMT_RANK", unNonWorldYear.getTradeAmtRank());
                node.put("TRADE_AMT_DIFFERENCE_RANK", unNonWorldYear.getTradeAmtDifferenceRank());
                node.put("TRADE_AMT_GROWRATE_RANK", unNonWorldYear.getTradeAmtGrowrateRank());
                node.put("TRADE_AMT_PERCENT_RANK", unNonWorldYear.getTradeAmtPercentRank());
                node.put("WEIGHT_RANK", unNonWorldYear.getWeightRank());
                node.put("WEIGHT_DIFFERENCE_RANK", unNonWorldYear.getWeightDifferenceRank());
                node.put("WEIGHT_GROWRATE_RANK", unNonWorldYear.getWeightGrowrateRank());
                node.put("WEIGHT_PERCENT_RANK", unNonWorldYear.getWeightPercentRank());
                node.put("COUNTRY_COMM_ZH", unNonWorldYear.getCountryCommZh());
                node.put("TXN_YEAR", unNonWorldYear.getTxnYear());
                node.put("TARIFF", unNonWorldYear.getTariff());
            }
            ArrayNode c3_4Array = response.putArray("B-3-4");
            List<String> c34List = Arrays.asList(new String[3]);
            for (UnNonWorldYear unNonWorldYear : unNonWorldYearList) {
                if (unNonWorldYear.getTradeAmtRank() != null && unNonWorldYear.getTradeAmtRank() <= 3) {
                    c34List.set(unNonWorldYear.getTradeAmtRank() - 1, unNonWorldYear.getPartnerIso3());
                }
            }
            for (String s : c34List) {
                if (s != null) {
                    c3_4Array.add(s);
                }
            }
        } else {
            return ok(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }

        end = new Date();
        duration = end.getTime() - start.getTime();
        Logger.of("application").info("C3P2 duration:{}", duration);

        response.put("C-3", C3Node);
        return ok(response);
    }
}
