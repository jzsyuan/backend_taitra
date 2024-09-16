package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import datatypes.Error;
import com.fasterxml.jackson.databind.JsonNode;
import datatypes.InsightType;
import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.DateUtils;
import utils.MyCache;
import utils.Utils;
import utils.ValidationUtils;

import java.sql.Timestamp;
import java.util.*;

public class AIInfoController extends Controller {

    private final MyCache myCache = MyCache.getInstance();

    public Result getDashboard(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }

        JsonNode parameter = request.body().asJson();

        if (!parameter.has("tax_id") ||
                !parameter.has("member_id")) {
            return internalServerError(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String taxId = parameter.get("tax_id").isNull() ? "" : parameter.get("tax_id").asText();
        String memberId = parameter.get("member_id").isNull() ? "" : parameter.get("member_id").asText();

        if (taxId.isEmpty() && memberId.isEmpty()) {
            return internalServerError(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }

        Timestamp now = new Timestamp(new Date().getTime());

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);

        List<String> exhibitionIds = new ArrayList<>();
        List<String> blockExhibitionIds = new ArrayList<>();
        Set<String> memberFavExhibitionIds = new HashSet<>();
        Set<String> memberRecExhibitionIds = new HashSet<>();
        Map<String, Integer> exbRecommendRankMap = new HashMap<>();
        MemberList memberBlkExhibitionList = MemberList.findByMemberIdAndType("taitra-backend", InsightType.EXHIBITION_BLOCK_ID.getAction());
        if (memberBlkExhibitionList != null) {
            List<InsightMemberListBean> insightMemberListBeanList = memberBlkExhibitionList.getContent();
            for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
                blockExhibitionIds.add(insightMemberListBean.getExhibitionId());
            }
        }
        if (!memberId.isEmpty()) {
            MemberList memberFavExhibitionList = MemberList.findByMemberIdAndType(memberId, InsightType.EXHIBITION_FAVORITE_ADD_ID.getAction());
            //MemberList memberDelExhibitionList = MemberList.findByMemberIdAndType(memberId, InsightType.EXHIBITION_FAVORITE_DEL_ID.getAction());
            MemberList memberDelRecExhibitionList = MemberList.findByMemberIdAndType(memberId, InsightType.EXHIBITION_RECOMMEND_DEL_ID.getAction());
            if (memberFavExhibitionList != null) {
                List<InsightMemberListBean> insightMemberListBeanList = memberFavExhibitionList.getContent();
                for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
                    memberFavExhibitionIds.add(insightMemberListBean.getExhibitionId());
                    blockExhibitionIds.add(insightMemberListBean.getExhibitionId());
                }
            }
            /*
            if (memberDelExhibitionList != null) {
                List<InsightMemberListBean> insightMemberListBeanList = memberDelExhibitionList.getContent();
                for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
                    blockExhibitionIds.add(insightMemberListBean.getExhibitionId());
                }
            }
            */
            if (memberDelRecExhibitionList != null) {
                List<InsightMemberListBean> insightMemberListBeanList = memberDelRecExhibitionList.getContent();
                for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
                    blockExhibitionIds.add(insightMemberListBean.getExhibitionId());
                }
            }

            List<MemberExhibition> memberExhibitions = MemberExhibition.findByMemberId(memberId);
            for (MemberExhibition memberExhibition : memberExhibitions) {
                memberRecExhibitionIds.add(memberExhibition.getExhibitionId());
                exhibitionIds.add(memberExhibition.getExhibitionId());
                exbRecommendRankMap.put(memberExhibition.getExhibitionId(), memberExhibition.getRank());
            }
        }
        else if (memberId.isEmpty() && !taxId.isEmpty()) {
            List<CompanyExhibition> companyExhibitionList = CompanyExhibition.findByTaxId(taxId);
            for (CompanyExhibition companyExhibition : companyExhibitionList) {
                memberRecExhibitionIds.add(companyExhibition.getExhibitionId());
                exhibitionIds.add(companyExhibition.getExhibitionId());
                exbRecommendRankMap.put(companyExhibition.getExhibitionId(), companyExhibition.getRank());
            }
        }

        List<String> activityIds = new ArrayList<>();
        List<String> blockActivityIds = new ArrayList<>();
        Set<String> memberFavActivityIds = new HashSet<>();
        Set<String> memberRecActivityIds = new HashSet<>();
        Map<String, Integer> actRecommendRankMap = new HashMap<>();
        MemberList memberBlkActivityList = MemberList.findByMemberIdAndType("taitra-backend", InsightType.ACTIVITY_BLOCK_ID.getAction());
        if (memberBlkActivityList != null) {
            List<InsightMemberListBean> insightMemberListBeanList = memberBlkActivityList.getContent();
            for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
                blockActivityIds.add(insightMemberListBean.getActivityId());
            }
        }
        if (!memberId.isEmpty()) {
            MemberList memberFavActivityList = MemberList.findByMemberIdAndType(memberId, InsightType.ACTIVITY_FAVORITE_ADD_ID.getAction());
            //MemberList memberDelActivityList = MemberList.findByMemberIdAndType(memberId, InsightType.ACTIVITY_FAVORITE_DEL_ID.getAction());
            MemberList memberDelRecActivityList = MemberList.findByMemberIdAndType(memberId, InsightType.ACTIVITY_RECOMMEND_DEL_ID.getAction());
            if (memberFavActivityList != null) {
                List<InsightMemberListBean> insightMemberListBeanList = memberFavActivityList.getContent();
                for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
                    memberFavActivityIds.add(insightMemberListBean.getActivityId());
                    blockActivityIds.add(insightMemberListBean.getActivityId());
                }
            }
            /*
            if (memberDelActivityList != null) {
                List<InsightMemberListBean> insightMemberListBeanList = memberDelActivityList.getContent();
                for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
                    blockActivityIds.add(insightMemberListBean.getActivityId());
                }
            }
            */
            if (memberDelRecActivityList != null) {
                List<InsightMemberListBean> insightMemberListBeanList = memberDelRecActivityList.getContent();
                for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
                    blockActivityIds.add(insightMemberListBean.getActivityId());
                }
            }

            List<MemberActivity> memberActivities = MemberActivity.findByMemberId(memberId);
            for (MemberActivity memberActivity : memberActivities) {
                memberRecActivityIds.add(memberActivity.getActivityId());
                activityIds.add(memberActivity.getActivityId());
                actRecommendRankMap.put(memberActivity.getActivityId(), memberActivity.getRank());
            }
        }
        else if (memberId.isEmpty() && !taxId.isEmpty()) {
            List<CompanyActivity> companyActivityList = CompanyActivity.findByTaxId(taxId);
            for (CompanyActivity companyActivity : companyActivityList) {
                memberRecActivityIds.add(companyActivity.getActivityId());
                activityIds.add(companyActivity.getActivityId());
                actRecommendRankMap.put(companyActivity.getActivityId(), companyActivity.getRank());
            }
        }

        int exhibitionLength = 6;
        List<ExhibitionListGroup> exhibitionLists = ExhibitionListGroup.findExhibitionGroup(null, 0, null, null, exhibitionIds, blockExhibitionIds, true, 0, exhibitionIds.size());
        //List<ExhibitionListGroup> exhibitionLists = ExhibitionListGroup.findExhibitionGroup(null, 0, null, now, null, null, true, 0, exhibitionLength);
        //Rank recommend
        for (ExhibitionListGroup exhibitionListGroups : exhibitionLists) {
            if (exbRecommendRankMap.containsKey(exhibitionListGroups.getExhibitionId())) {
                exhibitionListGroups.setRank(exbRecommendRankMap.get(exhibitionListGroups.getExhibitionId()));
            }
        }
        Logger.of("application").info("recommend exhibition lists size:" + exhibitionLists.size());
        Collections.sort(exhibitionLists, new Comparator<ExhibitionListGroup>() {
            @Override
            public int compare(ExhibitionListGroup p1, ExhibitionListGroup p2) {
                return p1.getRank().compareTo(p2.getRank()); //ascending
            }
        });
        /*
        if (exhibitionLists.size() < exhibitionLength) {
            List<ExhibitionListGroup> addExhibitionLists = ExhibitionListGroup.findExhibitionGroup(null, 0, null, now, null, blockExhibitionIds, true, 0, exhibitionLength - exhibitionLists.size());
            exhibitionLists.addAll(addExhibitionLists);
        }
        */
        int exhibitionCount = 0;
        ArrayNode exhibitionListGroupArray = response.putArray("exhibition_list");   // max = 6
        for (ExhibitionListGroup exhibitionListGroup : exhibitionLists) {
            if (exhibitionCount >= exhibitionLength) {
                break;
            }
            ObjectNode exhibition = exhibitionListGroupArray.addObject();
            exhibition.put("exhibition_id", exhibitionListGroup.getExhibitionId());
            exhibition.put("title", exhibitionListGroup.getExhibitionTitle() == null ? "" : exhibitionListGroup.getExhibitionTitle());
            exhibition.put("date_start", exhibitionListGroup.getExhibitionSdate() == null ? "" : DateUtils.dateToString(exhibitionListGroup.getExhibitionSdate(), "yyyy-MM-dd"));
            exhibition.put("date_end", exhibitionListGroup.getExhibitionEdate() == null ? "" : DateUtils.dateToString(exhibitionListGroup.getExhibitionEdate(), "yyyy-MM-dd"));
            exhibition.put("locate", exhibitionListGroup.getExhibitionCountry() == null ? "" : exhibitionListGroup.getExhibitionCountry());

            ArrayNode tagsArray = exhibition.putArray("tags");
            try {
                if (exhibitionListGroup.getExhibitionTags() != null) {
                    int index = 0;
                    JsonNode jsonNode = (JsonNode) new ObjectMapper().readTree(exhibitionListGroup.getExhibitionTags());
                    for (JsonNode node : jsonNode) {
                        if (index == 4)
                            break;
                        ObjectNode tag = tagsArray.addObject();
                        tag.put("tag_id", index);
                        tag.put("tag_name", node.asText());
                        index++;
                    }
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            if (exhibitionListGroup.getExhibitionSdate() != null) {
                if (now.before(exhibitionListGroup.getExhibitionSdate()))
                    exhibition.put("open_status", 2);
                else if (now.after(exhibitionListGroup.getExhibitionSdate()) && now.before(exhibitionListGroup.getExhibitionEdate()))
                    exhibition.put("open_status", 0);
                else
                    exhibition.put("open_status", 1);
            } else {
                exhibition.put("open_status", 1);
            }

            exhibition.put("img_url", exhibitionListGroup.getExhibitionCover() == null ? Utils.getRandomExhibitionImageLink() : exhibitionListGroup.getExhibitionCover());
            exhibition.put("is_favor", memberRecExhibitionIds.contains(exhibitionListGroup.getExhibitionId()));
            exhibition.put("is_collect", memberFavActivityIds.contains(exhibitionListGroup.getExhibitionId()));
            exhibition.put("relate_activity_id", "");

            exhibitionCount++;
        }

        int activityLength = 3;
        Logger.of("application").info("activity list:" + Arrays.toString(activityIds.toArray()));
        Logger.of("application").info("activity block list:" + Arrays.toString(blockActivityIds.toArray()));
        List<ActivityList> activityLists = ActivityList.findActivityList("", "", "", "", null, null, null, null, null, null, activityIds, blockActivityIds, true, 0, activityIds.size());
        //List<ActivityList> activityLists = ActivityList.findActivityList("", "", "", "", null, null, null, null, null, now, null, null, true, 0, activityLength);
        //Rank recommend
        for (ActivityList activityList : activityLists) {
            if (actRecommendRankMap.containsKey(activityList.getActivityId())) {
                activityList.setRank(actRecommendRankMap.get(activityList.getActivityId()));
            }
        }

        Collections.sort(activityLists, new Comparator<ActivityList>() {
            @Override
            public int compare(ActivityList p1, ActivityList p2) {
                return p1.getRank().compareTo(p2.getRank()); //ascending
            }
        });
        /*
        if (activityLists.size() < activityLength) {
            List<ActivityList> addActivityLists = ActivityList.findActivityList("", "", "", "", null, null, null, null, null, now, null, blockActivityIds, true, 0, activityLength - activityLists.size());
            activityLists.addAll(addActivityLists);
        }
        */
        int activityCount = 0;
        ArrayNode activityListArray = response.putArray("activity_list");
        for (ActivityList activityList : activityLists) {
            if (activityCount >= activityLength) {
                break;
            }
            ObjectNode activityNode = activityListArray.addObject();
            activityNode.put("activity_id", activityList.getActivityId());
            activityNode.put("title", activityList.getActivityTitleTc());
            activityNode.put("date_start", DateUtils.dateToString(activityList.getActivityEventStart(), "yyyy-MM-dd"));
            activityNode.put("date_end", DateUtils.dateToString(activityList.getActivityEventEnd(), "yyyy-MM-dd"));
            activityNode.put("source_url", activityList.getActivityUrl() == null ? "" : activityList.getActivityUrl());

            ArrayNode tagsArray = activityNode.putArray("tags");
            try {
                StringBuilder sb = new StringBuilder();
                if (activityList.getActivityCountry() != null) {
                    JsonNode jsonNode = (JsonNode) new ObjectMapper().readTree(activityList.getActivityCountry());
                    for (int a = 0; a < jsonNode.size(); a++) {
                        sb.append(jsonNode.get(a).asText());
                        if (a + 1 < jsonNode.size()) {
                            sb.append(",");
                        }
                    }
                }
                activityNode.put("locate", sb.toString());

                if (activityList.getActivityTags() != null) {
                    int index = 0;
                    JsonNode jsonNode = (JsonNode) new ObjectMapper().readTree(activityList.getActivityTags());
                    for (JsonNode node : jsonNode) {
                        if (index == 4)
                            break;
                        ObjectNode tag = tagsArray.addObject();
                        tag.put("tag_id", index);
                        tag.put("tag_name", node.asText());
                        index++;
                    }
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            activityNode.put("is_collect", memberFavActivityIds.contains(activityList.getActivityId()));
            activityNode.put("is_favor", memberRecActivityIds.contains(activityList.getActivityId()));
            if (activityList.getActivityEnrollStart() != null) {
                if (now.before(activityList.getActivityEnrollStart()))
                    activityNode.put("open_status", 2);
                else if (now.after(activityList.getActivityEnrollStart()) && now.before(activityList.getActivityEnrollEnd()))
                    activityNode.put("open_status", 0);
                else
                    activityNode.put("open_status", 1);
            } else {
                activityNode.put("open_status", 1);
            }
            activityCount++;
        }

        Date today = new Date();
        Date past7Days = DateUtils.getShiftDate(today, Calendar.DATE, -7);
        List<NewsList> newsListList = NewsList.findBy(null, past7Days, today, 0, 4);
        ArrayNode newsListArray = response.putArray("news_list");
        for (NewsList newsList : newsListList) {
            ObjectNode node = newsListArray.addObject();
            node.put("news_id", newsList.getNewsId());
            node.put("thumbnail", newsList.getNewsCover() == null ? "https://ezchoice-staging.s3-ap-southeast-1.amazonaws.com/uploads/taitra/news/1.png" : newsList.getNewsCover());
            node.put("title", newsList.getNewsTitle());
            node.put("date", DateUtils.dateToString(new Date(newsList.getNewsDatetime().getTime()), "YYYY-MM-dd"));
            node.put("source_org", newsList.getNewsSource());
            node.put("source_url", newsList.getNewsUrl());
        }

        String imageAhs6 = "";
        String imageAcnty = "";
        String imageChs6 = "";

        if (!memberId.isEmpty()) {
            MemberList memberListA = MemberList.findByMemberIdAndType(memberId, InsightType.TRADE_FAVORITE_ADD_CNTY_HS6.getAction());
            MemberList memberListC = MemberList.findByMemberIdAndType(memberId, InsightType.TRADE_FAVORITE_ADD_HS6.getAction());
            //有收藏取收藏
            if (memberListA != null) {
                List<InsightMemberListBean> insightMemberListBeanList = memberListA.getContent();
                Collections.reverse(insightMemberListBeanList);
                for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
                    //取第一個
                    imageAhs6 = insightMemberListBean.getHsCode6();
                    imageAcnty = insightMemberListBean.getCntyCode();
                    break;
                }
            }
            //沒收藏取推薦
            else {
                List<MemberTrade> memberTradeList = MemberTrade.findByMemberIdAndType(memberId, "cntyhsc6");
                MemberList memberDelRecommendList = MemberList.findByMemberIdAndType(memberId, InsightType.TRADE_RECOMMEND_DEL_CNTYHSC6.getAction());
                Set<String> set = new HashSet<>();
                if (memberDelRecommendList != null) {
                    for (InsightMemberListBean insightMemberListBean : memberDelRecommendList.getContent()) {
                        set.add(insightMemberListBean.getHsCode6() + "_" + insightMemberListBean.getCntyCode());
                    }
                }
                for (MemberTrade memberTrade : memberTradeList) {
                    if (!set.contains(memberTrade.getTradeHscode() + "_" + memberTrade.getTradeCountry())) {
                        //取第一個
                        imageAhs6 = memberTrade.getTradeHscode();
                        imageAcnty = memberTrade.getTradeCountry();
                        break;
                    }
                }
            }

            //有推薦取推薦
            List<MemberTrade> memberTradeList = MemberTrade.findByMemberIdAndType(memberId, "hsc6");
            MemberList memberDelRecommendList = MemberList.findByMemberIdAndType(memberId, InsightType.TRADE_RECOMMEND_DEL_HSC6.getAction());
            if (!memberTradeList.isEmpty()) {
                Set<String> set = new HashSet<>();
                if (memberDelRecommendList != null) {
                    for (InsightMemberListBean insightMemberListBean : memberDelRecommendList.getContent()) {
                        set.add(insightMemberListBean.getHsCode6() + "_" + insightMemberListBean.getCntyCode());
                    }
                }
                for (MemberTrade memberTrade : memberTradeList) {
                    if (!set.contains(memberTrade.getTradeHscode() + "_" + memberTrade.getTradeCountry())) {
                        //取第一個
                        imageChs6 = memberTrade.getTradeHscode();
                        break;
                    }
                }
            }
            //沒推薦取收藏
            if (imageChs6.isEmpty() && memberListC != null) {
                List<InsightMemberListBean> insightMemberListBeanList = memberListC.getContent();
                Collections.reverse(insightMemberListBeanList);
                for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
                    //取第一個
                    imageChs6 = insightMemberListBean.getHsCode6();
                    break;
                }
            }
        }
        else if (memberId.isEmpty() && !taxId.isEmpty()) {
            List<CompanyTrade> companyTradeA = CompanyTrade.findByCompanyBanAndType(taxId, "cntyhsc6");
            List<CompanyTrade> companyTradeC = CompanyTrade.findByCompanyBanAndType(taxId, "hsc6");
            for (CompanyTrade companyTrade : companyTradeA) {
                imageAhs6 = companyTrade.getTradeHscode();
                imageAcnty = companyTrade.getTradeCountry();
                break;
            }
            for (CompanyTrade companyTrade : companyTradeC) {
                imageChs6 = companyTrade.getTradeHscode();
                break;
            }
        }

        if (imageAhs6.isEmpty() && imageAcnty.isEmpty()) {
            imageAhs6 = "848180";
            imageAcnty = "USA";
        }
        if (imageChs6.isEmpty()) {
            imageChs6 = "854231";
        }

        ObjectNode A1Node = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode a1BarArray = A1Node.putArray("bar");
        response.putNull("A-0-1");
        List<OthUnWorldYear> othUnWorldYearList = new ArrayList<>();
        if (!imageAhs6.isEmpty()) {
            othUnWorldYearList = myCache.getOthUnWorldYearCache(imageAhs6);
            if (othUnWorldYearList == null) {
                othUnWorldYearList = OthUnWorldYear.findListByHsCodeAndReporterIso3AndAreaId(imageAhs6, null, null);
                myCache.putOthUnWorldYearCache(imageAhs6, othUnWorldYearList);
            }
        }

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

            if (othUnWorldYear.getReporterIso3().equals(imageAcnty)) {
                response.put("A-0-1", othUnWorldYear.getTradeAmtRank());
            }
        }

        othUnWorldYearList = new ArrayList<>();
        if (!imageChs6.isEmpty()) {
            othUnWorldYearList = myCache.getOthUnWorldYearCache(imageChs6);
            if (othUnWorldYearList == null && !imageChs6.isEmpty()) {
                othUnWorldYearList = OthUnWorldYear.findListByHsCodeAndReporterIso3AndAreaId(imageChs6, null, null);
                myCache.putOthUnWorldYearCache(imageChs6, othUnWorldYearList);
            }
        }

        ObjectNode C1Node = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode c1LineArray = C1Node.putArray("line");
        ArrayNode c1_1Array = response.putArray("C-1-1");
        List<String> c11List = Arrays.asList(new String[3]);
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

            if (othUnWorldYear.getTradeAmtRank() != null && othUnWorldYear.getTradeAmtRank() <= 3) {
                c11List.set(othUnWorldYear.getTradeAmtRank() - 1, othUnWorldYear.getReporterIso3());
            }
        }
        for (String s : c11List) {
            if (s != null) {
                c1_1Array.add(s);
            }
        }

        A1Node.put("hscode", imageAhs6);
        A1Node.put("CNTY", imageAcnty);
        C1Node.put("hscode", imageChs6);
        response.put("A-1", A1Node);
        response.put("C-1", C1Node);

        return ok(response);
    }

    public Result getNews(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }

        JsonNode parameter = request.body().asJson();
        if (!parameter.has("page_index") ||
                !parameter.has("page_size")) {
            return internalServerError(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        //String startDate = parameter.has("start_date") ? parameter.get("start_date").asText() : "";
        //String endDate = parameter.has("end_date") ? parameter.get("end_date").asText() : "";
        String keyword = parameter.has("keyword") ? (parameter.get("keyword").isNull() ? null : parameter.get("keyword").asText()) : null;
        int pageIndex = parameter.has("page_index") ? parameter.get("page_index").asInt() : 1;
        int pageSize = parameter.has("page_size") ? parameter.get("page_size").asInt() : 6;
        if (pageIndex < 0) {
            return internalServerError(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }
        if (pageSize > 0) {
            pageIndex = (pageIndex - 1) * pageSize;
        }

        Date today = new Date();
        Date past7Days = DateUtils.getShiftDate(today, Calendar.DATE, -7);
        today = DateUtils.getEndOfTheDay();
        List<NewsList> newsListList = NewsList.findBy(keyword, past7Days, today, pageIndex, pageSize);
        int count = NewsList.findCount(keyword, past7Days, today);

        //List<NewsList> newsListList = NewsList.findBy(null, null, pageIndex, pageSize);
        //int count = NewsList.findCount(null, null);

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode newsListArray = response.putArray("news_list");
        for (NewsList newsList : newsListList) {
            ObjectNode node = newsListArray.addObject();
            node.put("news_id", newsList.getNewsId());
            node.put("thumbnail", newsList.getNewsCover() == null ? "https://ezchoice-staging.s3-ap-southeast-1.amazonaws.com/uploads/taitra/news/1.png" : newsList.getNewsCover());
            node.put("title", newsList.getNewsTitle());
            node.put("date", DateUtils.dateToString(new Date(newsList.getNewsDatetime().getTime()), "YYYY-MM-dd"));
            node.put("source_org", newsList.getNewsSource());
            node.put("source_url", newsList.getNewsUrl());
        }

        ObjectNode pagination = response.putObject("pagination");
        int totalPages = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
        pagination.put("total_pages", totalPages);
        pagination.put("total_count", count);

        return ok(response);
    }
}
