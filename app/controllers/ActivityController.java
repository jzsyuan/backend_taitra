package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import datatypes.CategoryType;
import datatypes.Error;
import datatypes.InsightType;
import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.DateUtils;
import utils.RedisUtils;
import utils.Utils;
import utils.ValidationUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.*;

public class ActivityController extends Controller {

    public Result getRecommendedActivities(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        /*
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }
        */
        JsonNode parameter = request.body().asJson();
        if (!parameter.has("keyword") ||
                !parameter.has("type") ||
                !parameter.has("industry") ||
                !parameter.has("location") ||
                !parameter.has("start_time") ||
                !parameter.has("end_time") ||
                !parameter.has("taitra") || //null、false、true
                !parameter.has("show_all") || //null、false、true
                !parameter.has("open_status") ||
                !parameter.has("page_index") ||
                !parameter.has("page_size")) {
            return internalServerError(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String memberId = parameter.get("member_id").isNull() ? "" : parameter.get("member_id").asText();
        String keyword = parameter.has("keyword") ? parameter.get("keyword").asText() : "";
        String type = parameter.has("type") ? parameter.get("type").asText() : "";
        String industry = parameter.has("industry") ? parameter.get("industry").asText() : "";
        String location = parameter.has("location") ? parameter.get("location").asText() : "";
        String startTimeStr = parameter.has("start_time") ? parameter.get("start_time").asText() : "";
        String endTimeStr = parameter.has("end_time") ? parameter.get("end_time").asText() : "";
        boolean isCollect = parameter.has("is_collect") ? parameter.get("is_collect").asBoolean() : false;
        Boolean taitra = parameter.has("taitra") ? (parameter.get("taitra").isNull() ? null : parameter.get("taitra").asBoolean()) : null;
        Boolean showAll = parameter.has("show_all") ? (parameter.get("show_all").isNull() ? null : parameter.get("show_all").asBoolean()) : null;
        String openStatus = parameter.has("open_status") ? parameter.get("open_status").asText() : null;
        int pageIndex = parameter.has("page_index") ? parameter.get("page_index").asInt() : 1;
        int pageSize = parameter.has("page_size") ? parameter.get("page_size").asInt() : 6;

        if (pageIndex < 0) {
            return internalServerError(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }
        if (pageSize > 0 && pageIndex > 0) {
            pageIndex = (pageIndex - 1) * pageSize;
        }

        List<String> activityIds = new ArrayList<>();
        List<String> blockIds = new ArrayList<>();
        Set<String> memberFavorActivityIds = new HashSet<>();
        //Set<String> memberRecommendActivityIds = new HashSet<>();
        Map<String, Integer> recommendRankMap = new HashMap<>();
        MemberList blockMemberList = MemberList.findByMemberIdAndType("taitra-backend", InsightType.ACTIVITY_BLOCK_ID.getAction());
        if (blockMemberList != null) {
            List<InsightMemberListBean> insightMemberListBeanList = blockMemberList.getContent();
            for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
                blockIds.add(insightMemberListBean.getActivityId());
            }
        } else {
            blockIds = null;
        }
        if (!memberId.isEmpty()) {
            MemberList memberList = MemberList.findByMemberIdAndType(memberId, InsightType.ACTIVITY_FAVORITE_ADD_ID.getAction());
            if (memberList != null) {
                List<InsightMemberListBean> insightMemberListBeanList = memberList.getContent();
                for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
                    memberFavorActivityIds.add(insightMemberListBean.getActivityId());
                    activityIds.add(insightMemberListBean.getActivityId());
                }
            }
        }

        CategoryType categoryType = CategoryType.fromOrdinal(type);
        if (categoryType == null) {
            type = "";
        } else {
            type = categoryType.getDescription();
        }
        if (industry.equals("全部")) {
            industry = "";
        }
        if (location.equals("全部")) {
            location = "";
        }

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);

        Date today = new Date();
        Timestamp now = new Timestamp(today.getTime());
        Date startDate = startTimeStr.isEmpty() ? null : DateUtils.stringToDate(startTimeStr, "yyyy-MM-dd");
        Date endDate = endTimeStr.isEmpty() ? null : DateUtils.stringToDate(endTimeStr, "yyyy-MM-dd");

        //Favor only
        if (isCollect) {
            List<ActivityList> activityLists = ActivityList.findActivityList(keyword, type, industry, location, startDate, endDate, taitra, showAll, openStatus, null, activityIds, blockIds,true,  pageIndex, pageSize);
            int count = ActivityList.findActivityCount(keyword, type, industry, location, startDate, endDate, taitra, showAll, openStatus, null, activityIds, blockIds, true);

            ArrayNode activityListArray = response.putArray("activity_list");
            for (ActivityList activityList : activityLists) {
                ObjectNode activityNode = activityListArray.addObject();
                activityNode.put("activity_id", activityList.getActivityId());
                activityNode.put("locate", activityList.getActivityLocation());
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

                activityNode.put("is_collect", memberFavorActivityIds.contains(activityList.getActivityId()) ? true : false);
                activityNode.put("is_favor", recommendRankMap.containsKey(activityList.getActivityId()));
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
            }

            ObjectNode pagination = response.putObject("pagination");
            int totalPages = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
            pagination.put("total_pages", totalPages);
            pagination.put("total_count", count);
        } else {
            //(取得推薦 -> 排除收藏取10個 -> ActivityId查詢) -> 查詢非推薦未來活動 -> 查詢非推薦過去活動
            Set<String> memberRecommendDELActivityIds = new HashSet<>();
            MemberList memberRecommendDELList = MemberList.findByMemberIdAndType(memberId, InsightType.ACTIVITY_RECOMMEND_DEL_ID.getAction());
            if (memberRecommendDELList != null) {
                List<InsightMemberListBean> insightMemberListBeanList = memberRecommendDELList.getContent();
                for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
                    memberRecommendDELActivityIds.add(insightMemberListBean.getActivityId());
                }
            }

            List<String> recommendActivityIds = new ArrayList<>();
            List<MemberActivity> memberActivityList = MemberActivity.findByMemberId(memberId);
            int recommendMaxCount = 0;
            for (MemberActivity memberActivity : memberActivityList) {
                if (recommendMaxCount >= 10) {
                    break;
                }
                if (memberRecommendDELActivityIds.contains(memberActivity.getActivityId())) {
                    continue;
                }
                if (!memberFavorActivityIds.contains(memberActivity.getActivityId())) {
                    //memberRecommendActivityIds.add(memberActivity.getActivityId());
                    recommendActivityIds.add(memberActivity.getActivityId());
                    recommendRankMap.put(memberActivity.getActivityId(), memberActivity.getRank());
                    recommendMaxCount++;
                }
            }

            List<ActivityList> activityLists = new ArrayList<>();
            List<ActivityList> recommendList = new ArrayList<>();
            List<ActivityList> futureList = new ArrayList<>();
            List<ActivityList> pastList = new ArrayList<>();
            int queryPageIndex = pageIndex;
            int queryPageSize = pageSize;
            int count = 0;

            Logger.of("application").info("query activity recommend pageIndex={}, pageSize={}", queryPageIndex, queryPageSize);
            int recommendCount = 0;
            if (!memberId.isEmpty() && recommendActivityIds.size() > 0) {
                recommendList = ActivityList.findActivityList(keyword, type, industry, location, startDate, endDate, taitra, showAll, openStatus, null, recommendActivityIds, blockIds, true, 0, 10);
                //recommendCount = ActivityList.findActivityCount(keyword, type, industry, location, startDate, endDate, taitra, showAll, openStatus, null, recommendActivityIds, blockIds, true);
                recommendCount = recommendList.size();
            }
            //int recommendCount = recommendList.size();
            Logger.of("application").info("query activity recommend pageIndex={}, pageSize={}, list size={}", queryPageIndex, queryPageSize, recommendList.size());
            //Rank recommend
            for (ActivityList activityList : recommendList) {
                if (recommendRankMap.containsKey(activityList.getActivityId())) {
                    activityList.setRank(recommendRankMap.get(activityList.getActivityId()));
                }
            }
            Collections.sort(recommendList, new Comparator<ActivityList>() {
                @Override
                public int compare(ActivityList p1, ActivityList p2) {
                    return p1.getRank().compareTo(p2.getRank()); //ascending
                }
            });
            for (int a = queryPageIndex; a < recommendList.size(); a++) {
                if (activityLists.size() >= pageSize) {
                    break;
                }
                activityLists.add(recommendList.get(a));
            }

            if (blockIds != null) {
                recommendActivityIds.addAll(blockIds);
            }
            queryPageIndex = queryPageIndex - recommendCount < 0 ? 0 : queryPageIndex - recommendCount;
            queryPageSize = queryPageSize - activityLists.size();
            if (queryPageIndex >= 0 && queryPageSize > 0) {
                futureList = ActivityList.findActivityList(keyword, type, industry, location, startDate, endDate, taitra, showAll, openStatus, today, null, recommendActivityIds.size() == 0 ? null : recommendActivityIds, false, queryPageIndex, queryPageSize);
            }
            int futureCount = ActivityList.findActivityCount(keyword, type, industry, location, startDate, endDate, taitra, showAll, openStatus, today, null, recommendActivityIds.size() == 0 ? null : recommendActivityIds, false);
            Logger.of("application").info("query activity future pageIndex={}, pageSize={}, list size={}", queryPageIndex, queryPageSize, futureList.size());
            activityLists.addAll(futureList);

            queryPageIndex = queryPageIndex - futureCount < 0 ? 0 : queryPageIndex - futureCount;
            queryPageSize = queryPageSize - activityLists.size();
            if (queryPageIndex >= 0 && queryPageSize > 0) {
                pastList = ActivityList.findActivityList(keyword, type, industry, location, startDate, endDate, taitra, showAll, openStatus, today, null, recommendActivityIds.size() == 0 ? null : recommendActivityIds, true, queryPageIndex, queryPageSize);
            }
            int pastCount = ActivityList.findActivityCount(keyword, type, industry, location, startDate, endDate, taitra, showAll, openStatus, today, null, recommendActivityIds.size() == 0 ? null : recommendActivityIds, true);
            Logger.of("application").info("query activity past pageIndex={}, pageSize={}, list size={}", queryPageIndex, queryPageSize, pastList.size());

            Logger.of("application").info("recommendCount={}, futureCount={}, pastCount={}", recommendCount, futureCount, pastCount);
            count = recommendCount + futureCount + pastCount;
            //activityLists.addAll(recommendList);
            activityLists.addAll(pastList);

            ArrayNode activityListArray = response.putArray("activity_list");
            for (ActivityList activityList : activityLists) {
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

                activityNode.put("is_collect", memberFavorActivityIds.contains(activityList.getActivityId()) ? true : false);
                activityNode.put("is_favor", recommendRankMap.containsKey(activityList.getActivityId()));
                if (activityList.getActivityEnrollStart() != null && activityList.getActivityEnrollEnd() != null) {
                    if (now.before(activityList.getActivityEnrollStart()))
                        activityNode.put("open_status", 2);
                    else if (now.after(activityList.getActivityEnrollStart()) && now.before(activityList.getActivityEnrollEnd()))
                        activityNode.put("open_status", 0);
                    else
                        activityNode.put("open_status", 1);
                } else {
                    activityNode.put("open_status", 1);
                }
            }

            ObjectNode pagination = response.putObject("pagination");
            int totalPages = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
            pagination.put("total_pages", totalPages);
            pagination.put("total_count", count);
        }

        return ok(response);
    }

    public Result getActivityDetail(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        /*
        datatypes.Error error = ValidationUtils.verifyRequest(request.toString(), request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }
        */
        JsonNode parameter = request.body().asJson();
        if (!parameter.has("member_id") || !parameter.has("activity_id")) {
            return internalServerError(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String memberId = parameter.get("member_id").isNull() ? "" : parameter.get("member_id").asText();
        String activityId = parameter.get("activity_id").isNull() ? "" : parameter.get("activity_id").asText();
        ActivityList activityList = ActivityList.findActivityListById(activityId);
        if (activityList == null) {
            return internalServerError(Json.toJson(Error.ACTIVITY_NOT_FOUND.toErrorMap()));
        }

        //MemberActivity memberActivity = MemberActivity.findByActivityIdAndMemberId(activityId, memberId);
        MemberList memberList = MemberList.findByMemberIdAndType(memberId, InsightType.ACTIVITY_FAVORITE_ADD_ID.getAction());
        List<InsightMemberListBean> insightMemberListBeanList = memberList == null ? new ArrayList<>() : memberList.getContent();
        Set<String> favorActivityIds = new HashSet<>();
        for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
            favorActivityIds.add(insightMemberListBean.getActivityId());
        }

        Timestamp now = new Timestamp(new Date().getTime());

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        response.put("title", activityList.getActivityTitleTc() == null ? "" : activityList.getActivityTitleTc());
        response.put("date_start", DateUtils.dateToString(activityList.getActivityEventStart(), "yyyy-MM-dd"));
        response.put("date_end", DateUtils.dateToString(activityList.getActivityEventEnd(), "yyyy-MM-dd"));
        response.put("cost", activityList.getActivityFee() == null ? "" : activityList.getActivityFee());
        response.put("unit", activityList.getActivityOrganizer() == null ? "" : activityList.getActivityOrganizer());
        response.put("intro", activityList.getActivityContent() == null ? "" : activityList.getActivityContent());
        response.put("contact", activityList.getActivityContactPerson() + " " + activityList.getActivityContactTel());
        response.put("source_url", activityList.getActivityUrl() == null ? "" : activityList.getActivityUrl());
        response.put("banner_img", activityList.getActivityCover() == null ? Utils.getRandomActivityImageLink() : activityList.getActivityCover());
        if (favorActivityIds.contains(activityList.getActivityId())) {
            response.put("is_collect", true);
        } else {
            response.put("is_collect", false);
        }

        ArrayNode tagsArray = response.putArray("tags");
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
            response.put("locate", sb.toString());
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

        if (activityList.getActivityEnrollStart() != null) {
            if (now.before(activityList.getActivityEnrollStart()))
                response.put("open_status", 2);
            else if (now.after(activityList.getActivityEnrollStart()) && now.before(activityList.getActivityEnrollEnd()))
                response.put("open_status", 0);
            else
                response.put("open_status", 1);
        } else {
            response.put("open_status", 1);
        }

        ArrayNode addOnUrls = response.putArray("add_on_urls");
        ObjectNode addOn1 = addOnUrls.addObject();
        addOn1.put("url", "https://xxx/Interview_Techniques.pdf");
        addOn1.put("name", "Interview_Techniques.pdf");
        ObjectNode addOn2 = addOnUrls.addObject();
        addOn2.put("url", "https://xxx/Supervisor_Skills.pdf");
        addOn2.put("name", "Supervisor_Skills.pdf");

        return ok(response);
    }

    public Result getMenu(Http.Request request) {

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);

        ArrayNode categoryArray = response.putArray("category");
        List<String> categoryList = Arrays.asList("國內展覽", "國外展覽", "採購洽談", "研討會/說明會", "貿訪/拓銷團", "培訓課程", "其他");
        int categoryIndex = 1;
        for (String s : categoryList) {
            ObjectNode category = categoryArray.addObject();
            category.put("text", s);
            category.put("id", "category" + categoryIndex);
            categoryIndex++;
        }

        ArrayNode industryArray = response.putArray("industry");
        //"消費性電子","禮品及藝品","皮箱、手提包及袋類","機械零件","電力、電機設備","電機及電子","建材及衛浴設備","醫療器材","台灣國際木工機械展","自行車及零配件","量測及分析儀器","中、西藥品","五金","航太","汽機車","機械","工具","包裝、印刷及廣告","生物科技","電腦軟、硬體及週邊設備","電信","服飾及配件","手工具","自行車","電腦","鐘錶、珠寶及眼鏡","美容及個人用品","家具","通訊產品","醫療及保健","樂器","包裝材料及容器","消費性電子及電器產品","電子零組件","家庭用電器","金銀及珠寶首飾","金屬材料及五金製品","紡織及皮革","農產及食品","化學品","辦公室及學校用品","農產品及食品","運動用品","礦產及金屬材料","燈光及照明","儀器","家庭用品","服務業","箱包","船舶及漁業","玩具、嬰幼兒及寵物用品","化妝及盥洗用品","汽、機車及零配件","安全",
        //List<String> industryList = Arrays.asList("消費性電子","禮品及藝品","皮箱、手提包及袋類","機械零件","電力、電機設備","電機及電子","建材及衛浴設備","醫療器材","台灣國際木工機械展","自行車及零配件","量測及分析儀器","中、西藥品","五金","航太","汽機車","機械","工具","包裝、印刷及廣告","生物科技","電腦軟、硬體及週邊設備","電信","服飾及配件","手工具","自行車","電腦","鐘錶、珠寶及眼鏡","美容及個人用品","家具","通訊產品","醫療及保健","樂器","包裝材料及容器","消費性電子及電器產品","電子零組件","家庭用電器","金銀及珠寶首飾","金屬材料及五金製品","紡織及皮革","農產及食品","化學品","辦公室及學校用品","農產品及食品","運動用品","礦產及金屬材料","燈光及照明","儀器","家庭用品","服務業","箱包","船舶及漁業","玩具、嬰幼兒及寵物用品","化妝及盥洗用品","汽、機車及零配件","安全");
        List<String> industryList = Arrays.asList("航太", "嬰幼兒用品", "建材", "設計業", "能源", "食品", "綠能環保", "物聯網", "機械及機械零件", "辦公室及學校用品", "智慧機械", "智慧建築", "安控器材", "服飾及配件", "VR", "農業", "竹炭", "電腦", "電子商務", "紡織及皮革", "食品加工", "五金工具", "LED照明", "製造業", "寵物用品", "橡塑膠", "太陽能", "科技", "隔熱鋁箔", "鐘錶珠寶及眼鏡", "樂器", "美容及個人用品", "消費性電子", "教育", "扣件", "連鎖加盟", "醫療及保健", "燈光及照明", "礦產及金屬材料", "公共工程", "安全", "運動休閒", "電子資訊", "玩具", "汽機車", "自行車", "美妝", "電動車", "金融", "禮品及藝術品", "飯店", "長照", "電影", "大眾運輸", "服務業", "新創", "量測及分析儀器", "運輸", "航空業", "生技醫療", "文化創意", "電機及電子", "漁業", "政府採購", "家庭用電器", "箱包", "清真", "軌道產業", "船舶", "文具家用品", "紡織", "觀光旅遊");
        //List<String> industryList = Arrays.asList("全部", "電子 / 通訊及周邊設備", "機械", "汽機車 / 零配件 / 交通運輸", "農漁產品 / 食品", "醫療 / 保健 / 藥品 / 美容生技", "五金手工具 / 建材 / 家具 / 安全器材", "化學 / 橡塑膠 / 儀器 / 光學 / 紙 / 包裝", "禮品 / 文具 / 文創 / 家用品 / 百貨", "醫療服務 / 文創 / 連鎖 / 資訊服務", "紡織 / 服飾配件 / 鞋類 / 袋類 / 珠寶", "自行車 / 運動用品", "親子婦幼及銀髮用品");
        int industryIndex = 0;
        for (String s : industryList) {
            ObjectNode industry = industryArray.addObject();
            industry.put("text", s);
            industry.put("id", s);
            //industry.put("id", "industry" + industryIndex);
            industryIndex++;
        }

        ArrayNode locationArray = response.putArray("location");
        //"羅馬尼亞","坦尚尼亞","澳大利亞","巴林","馬達加斯加","衣索比亞","亞塞拜然共和國","新加坡","阿爾及利亞","安道爾","迦納","紐西蘭","南非","線上","瑞典","黎巴嫩","南韓","尼泊爾","蘇丹","其他非洲國家","喬治亞","阿拉伯聯合大公國","智利","汶萊","印度","莫三比克","厄瓜多爾","台灣","馬其頓","其它","巴拉圭","哈薩克","剛果民主共和國","越南","幾內亞","丹麥","台中","義大利","法國","沙烏地阿拉伯","捷克","荷蘭","其他歐洲國家","高雄","加拿大","突尼西亞","香港","帛琉","約旦","烏茲別克斯坦","愛爾蘭","盧安達","比利時","柬埔寨","瓜地馬拉","塞內加爾","模里西斯","瑞士","菲律賓群島","拉脫維亞","以色列","蒙古","巴基斯坦","聖克里斯多福及尼維斯","吉爾吉斯共和國","阿爾巴尼亞","奈及利亞","孟加拉","阿富汗","蓋亞那","美屬薩摩亞","印尼","保加利亞","英國","日本","烏克蘭","肯亞","匈牙利","澳門","斯里蘭卡","象牙海岸","*法國，大都市的","克羅埃西亞","奧地利","科威特","西班牙","馬來西亞","希臘","土耳其","烏干達","中國","摩洛哥","台南","埃及","德國","墨西哥","新竹","葡萄牙","納米比亞","桃園","波蘭","巴拿馬","阿曼","貝里斯","斯洛伐克共和國","緬甸","俄羅斯聯邦","斯洛法尼亞","台北","聖露西亞","阿根廷","塔吉克共和國","亞美尼亞","泰國","卡達","秘魯","巴西","喀麥隆","伊拉克","塞爾維亞","史瓦帝尼王國","哥倫比亞","波士尼亞-赫塞哥維那","美國","立陶宛",
        List<String> locationList = Arrays.asList("羅馬尼亞","坦尚尼亞","澳大利亞","巴林","馬達加斯加","衣索比亞","亞塞拜然共和國","新加坡","阿爾及利亞","安道爾","迦納","紐西蘭","南非","線上","瑞典","黎巴嫩","南韓","尼泊爾","蘇丹","其他非洲國家","喬治亞","阿拉伯聯合大公國","智利","汶萊","印度","莫三比克","厄瓜多爾","台灣","馬其頓","其它","巴拉圭","哈薩克","剛果民主共和國","越南","幾內亞","丹麥","台中","義大利","法國","沙烏地阿拉伯","捷克","荷蘭","其他歐洲國家","高雄","加拿大","突尼西亞","香港","帛琉","約旦","烏茲別克","愛爾蘭","盧安達","比利時","柬埔寨","瓜地馬拉","塞內加爾","模里西斯","瑞士","菲律賓群島","拉脫維亞","以色列","蒙古","巴基斯坦","聖克里斯多福及尼維斯","吉爾吉斯共和國","阿爾巴尼亞","奈及利亞","孟加拉","阿富汗","蓋亞那","美屬薩摩亞","印尼","保加利亞","英國","日本","烏克蘭","肯亞","匈牙利","澳門","斯里蘭卡","象牙海岸","*法國，大都市的","克羅埃西亞","奧地利","科威特","西班牙","馬來西亞","希臘","土耳其","烏干達","中國","摩洛哥","台南","埃及","德國","墨西哥","新竹","葡萄牙","納米比亞","桃園","波蘭","巴拿馬","阿曼","貝里斯","斯洛伐克共和國","緬甸","俄羅斯聯邦","斯洛維尼亞","台北","聖露西亞","阿根廷","塔吉克共和國","亞美尼亞","泰國","卡達","秘魯","巴西","喀麥隆","伊拉克","塞爾維亞","史瓦帝尼王國","哥倫比亞","波士尼亞-赫塞哥維那","美國","立陶宛");
        //List<String> locationList = Arrays.asList("全部", "台北", "桃園", "新竹", "台中", "台南", "高雄", "台灣", "亞洲", "中東", "歐洲", "非洲", "北美洲", "中美洲", "南美洲", "大洋洲");
        int locationIndex = 0;
        for (String s : locationList) {
            ObjectNode location = locationArray.addObject();
            location.put("text", s);
            location.put("id", s);
            //location.put("id", "location" + locationIndex);
            locationIndex++;
        }

        return ok(response);
    }

    public Result updateFavoriteActivities(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());

        datatypes.Error error = ValidationUtils.verifyRequest(request.toString(), request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }

        JsonNode parameter = request.body().asJson();
        if (!parameter.has("member_id")
                || !parameter.has("activity_id")
                || !parameter.has("favor_status")) {
            return internalServerError(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String memberId = parameter.get("member_id").asText();
        String activityId = parameter.get("activity_id").asText();
        String favorStatus = parameter.get("favor_status").asText();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        try {
            MemberList memberListADD = MemberList.findByMemberIdAndType(memberId, InsightType.ACTIVITY_FAVORITE_ADD_ID.getAction());
            boolean doADDUpdate = false;
            boolean doDELUpdate = false;
            if (memberListADD == null) {
                memberListADD = new MemberList();
                memberListADD.setMemberId(memberId);
                memberListADD.setType(InsightType.ACTIVITY_FAVORITE_ADD_ID.getAction());
                memberListADD.setMemberListUpdateDatetime(timestamp);
            } else {
                memberListADD.setMemberListUpdateDatetime(timestamp);
                doADDUpdate = true;
            }

            MemberList memberListDEL = MemberList.findByMemberIdAndType(memberId, InsightType.ACTIVITY_FAVORITE_DEL_ID.getAction());
            if (memberListDEL == null) {
                memberListDEL = new MemberList();
                memberListDEL.setMemberId(memberId);
                memberListDEL.setType(InsightType.ACTIVITY_FAVORITE_DEL_ID.getAction());
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
                    String actId = insightMemberListBeanADDList.get(a).getActivityId();
                    addSet.put(actId, a);
                }
                if (!addSet.containsKey(activityId)) {
                    //put add list
                    InsightMemberListBean insightMemberListBean = new InsightMemberListBean();
                    insightMemberListBean.setActivityId(activityId);
                    insightMemberListBeanADDList.add(insightMemberListBean);
                    logBean.add(insightMemberListBean);
                }

                //remove del list
                Map<String, Integer> delSet = new HashMap<>();
                for (int a = 0; a < insightMemberListBeanDELList.size(); a++) {
                    String exhId = insightMemberListBeanDELList.get(a).getActivityId();
                    delSet.put(exhId, a);
                }

                //Do del
                if (delSet.containsKey(activityId)) {
                    //Check is exist
                    insightMemberListBeanDELList.remove(delSet.get(activityId).intValue());
                }

                action = InsightType.ACTIVITY_FAVORITE_ADD_ID.getCode();
            }
            else if (favorStatus.equals("N")) {
                Map<String, Integer> delSet = new HashMap<>();

                for (int a = 0; a < insightMemberListBeanDELList.size(); a++) {
                    String actId = insightMemberListBeanDELList.get(a).getActivityId();
                    delSet.put(actId, a);
                }
                if (!delSet.containsKey(activityId)) {
                    //put del list
                    InsightMemberListBean insightMemberListBean = new InsightMemberListBean();
                    insightMemberListBean.setActivityId(activityId);
                    insightMemberListBeanDELList.add(insightMemberListBean);
                    logBean.add(insightMemberListBean);
                }

                //remove add list
                Map<String, Integer> addSet = new HashMap<>();
                for (int a = 0; a < insightMemberListBeanADDList.size(); a++) {
                    String actId = insightMemberListBeanADDList.get(a).getActivityId();
                    addSet.put(actId, a);
                }

                //Do add
                if (addSet.containsKey(activityId)) {
                    //Check is exist
                    insightMemberListBeanADDList.remove(addSet.get(activityId).intValue());
                }

                action = InsightType.ACTIVITY_FAVORITE_DEL_ID.getCode();
            }
            Logger.of("application").debug("ADD list:{}", insightMemberListBeanADDList);
            Logger.of("application").debug("DEL list:{}", insightMemberListBeanDELList);
            memberListADD.setContent(insightMemberListBeanADDList);
            memberListDEL.setContent(insightMemberListBeanDELList);

            //Write log
            //Push to redis
            MemberLog memberLog = new MemberLog();
            memberLog.setMemberId(memberId);
            memberLog.setType(action);
            memberLog.setMemberLogUpdateDatetime(timestamp);
            memberLog.setContent(logBean);
            //memberLog.insert();
            RedisUtils.savaToRedis(action, memberLog.toJson().toString());

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

        datatypes.Error error = ValidationUtils.verifyRequest(request.toString(), request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }

        JsonNode parameter = request.body().asJson();
        if (!parameter.has("member_id") ||
                !parameter.has("activity_id")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String memberId = parameter.get("member_id").asText();
        String activityId = parameter.get("activity_id").asText();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        try {
            boolean doDELUpdate = false;
            MemberList memberListDEL = MemberList.findByMemberIdAndType(memberId, InsightType.ACTIVITY_RECOMMEND_DEL_ID.getAction());
            if (memberListDEL == null) {
                memberListDEL = new MemberList();
                memberListDEL.setMemberId(memberId);
                memberListDEL.setType(InsightType.ACTIVITY_RECOMMEND_DEL_ID.getAction());
                memberListDEL.setMemberListUpdateDatetime(timestamp);
            } else {
                memberListDEL.setMemberListUpdateDatetime(timestamp);
                doDELUpdate = true;
            }

            List<InsightMemberListBean> insightMemberListBeanDELList = memberListDEL.getContent() == null ? new ArrayList<>() : memberListDEL.getContent();

            Map<String, Integer> delSet = new HashMap<>();

            for (int a = 0; a < insightMemberListBeanDELList.size(); a++) {
                String actId = insightMemberListBeanDELList.get(a).getActivityId();
                delSet.put(actId, a);
            }
            if (!delSet.containsKey(activityId)) {
                //put del list
                InsightMemberListBean insightMemberListBean = new InsightMemberListBean();
                insightMemberListBean.setActivityId(activityId);
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
}
