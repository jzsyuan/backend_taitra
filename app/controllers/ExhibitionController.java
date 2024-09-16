package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import utils.DateUtils;
import utils.RedisUtils;
import utils.Utils;
import utils.ValidationUtils;

import java.sql.Timestamp;
import java.util.*;

public class ExhibitionController extends Controller {
    public Result getRecommendedExhibitions(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        /*
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }
        */
        JsonNode parameter = request.body().asJson();
        if (!parameter.has("keyword") ||
                !parameter.has("nation") ||
                !parameter.has("year") ||
                !parameter.has("is_collect") ||
                !parameter.has("page_index") ||
                !parameter.has("page_size")) {
            return internalServerError(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String memberId = parameter.get("member_id").isNull() ? "" : parameter.get("member_id").asText();
        String keyword = parameter.has("keyword") ? parameter.get("keyword").asText() : "";
        String yearStr = parameter.has("year") ? parameter.get("year").asText() : "";
        String nation = parameter.has("nation") ? parameter.get("nation").asText() : "";
        boolean isCollect = parameter.has("is_collect") ? parameter.get("is_collect").asBoolean() : false;
        int pageIndex = parameter.has("page_index") ? parameter.get("page_index").asInt() : 1;
        int pageSize = parameter.has("page_size") ? parameter.get("page_size").asInt() : 6;

        int year = yearStr.isEmpty() ? 0 : Integer.parseInt(yearStr);

        if (pageIndex < 0) {
            return internalServerError(Json.toJson(Error.INVALID_PARAMETER.toErrorMap()));
        }
        if (pageSize > 0 && pageIndex > 0) {
            pageIndex = (pageIndex - 1) * pageSize;
        }

        List<String> exhibitionIds = new ArrayList<>();
        List<String> blockIds = new ArrayList<>();
        Set<String> memberFavorExhibitionIds = new HashSet<>();
        //Set<String> memberRecommendExhibitionIds = new HashSet<>();
        Map<String, Integer> recommendRankMap = new HashMap<>();
        MemberList blockMemberList = MemberList.findByMemberIdAndType("taitra-backend", InsightType.EXHIBITION_BLOCK_ID.getAction());
        if (blockMemberList != null) {
            List<InsightMemberListBean> insightMemberListBeanList = blockMemberList.getContent();
            for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
                blockIds.add(insightMemberListBean.getExhibitionId());
            }
        } else {
            blockIds = null;
        }
        if (!memberId.isEmpty()) {
            MemberList memberList = MemberList.findByMemberIdAndType(memberId, InsightType.EXHIBITION_FAVORITE_ADD_ID.getAction());
            if (memberList != null) {
                List<InsightMemberListBean> insightMemberListBeanList = memberList.getContent();
                for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
                    memberFavorExhibitionIds.add(insightMemberListBean.getExhibitionId());
                    exhibitionIds.add(insightMemberListBean.getExhibitionId());
                }
            }
        }

        Date today = new Date();
        Timestamp now = new Timestamp(today.getTime());

        //Favor only
        if (isCollect) {
            List<ExhibitionListGroup> exhibitionListGroups = ExhibitionListGroup.findExhibitionGroup(keyword, year, nation, null, exhibitionIds, blockIds, true, pageIndex, pageSize);
            int count = ExhibitionListGroup.findExhibitionGroupCount(keyword, year, nation, null, exhibitionIds, blockIds, true);

            ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
            ArrayNode exhibitionListGroupArray = response.putArray("exhibition_list");
            for (ExhibitionListGroup exhibitionListGroup : exhibitionListGroups) {
                ObjectNode exhibition = exhibitionListGroupArray.addObject();
                exhibition.put("exhibition_id", exhibitionListGroup.getExhibitionId());
                exhibition.put("title", exhibitionListGroup.getExhibitionTitle() == null ? "" : exhibitionListGroup.getExhibitionTitle());
                exhibition.put("date_start", exhibitionListGroup.getExhibitionSdate() == null ? "" : DateUtils.dateToString(exhibitionListGroup.getExhibitionSdate(), "yyyy-MM-dd"));
                exhibition.put("date_end", exhibitionListGroup.getExhibitionEdate() == null ? "" : DateUtils.dateToString(exhibitionListGroup.getExhibitionEdate(), "yyyy-MM-dd"));
                exhibition.put("locate",exhibitionListGroup.getExhibitionCountry() == null ? "" : exhibitionListGroup.getExhibitionCountry());

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

                exhibition.put("is_collect", memberFavorExhibitionIds.contains(exhibitionListGroup.getExhibitionId()) ? true : false);
                if (exhibitionListGroup.getExhibitionId().equals("88b5b086cf3bc68d64773ee1c0a0395334b17d8e805157aa29d59110c5986a9f")) {
                    exhibition.put("relate_activity_id", "E2FDBD9D-AF7B-4901-AC3D-CFC6B2AB550D");
                } else {
                    exhibition.put("relate_activity_id", "");
                }
                exhibition.put("is_favor", recommendRankMap.containsKey(exhibitionListGroup.getExhibitionId()));
            }

            ObjectNode pagination = response.putObject("pagination");
            int totalPages = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
            pagination.put("total_pages", totalPages);
            pagination.put("total_count", count);

            return ok(response);
        } else {
            if (nation != null && nation.equals("all")) {
                nation = null;
            }
            //排序 = 推薦 -> 尚未開始且距離當下由近到遠(遞增) -> 其餘由近到遠(遞減)
            Set<String> memberRecommendDELExhibitionIds = new HashSet<>();
            MemberList memberRecommendDELList = MemberList.findByMemberIdAndType(memberId, InsightType.EXHIBITION_RECOMMEND_DEL_ID.getAction());
            if (memberRecommendDELList != null) {
                List<InsightMemberListBean> insightMemberListBeanList = memberRecommendDELList.getContent();
                for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
                    memberRecommendDELExhibitionIds.add(insightMemberListBean.getExhibitionId());
                }
            }

            List<String> recommendExhibitionIds = new ArrayList<>();
            List<MemberExhibition> memberExhibitionList = MemberExhibition.findByMemberId(memberId);
            int recommendMaxCount = 0;
            for (MemberExhibition memberExhibition : memberExhibitionList) {
                if (recommendMaxCount >= 10) {
                    break;
                }
                if (memberRecommendDELExhibitionIds.contains(memberExhibition.getExhibitionId())) {
                    continue;
                }
                if (!memberFavorExhibitionIds.contains(memberExhibition.getExhibitionId())) {
                    recommendExhibitionIds.add(memberExhibition.getExhibitionId());
                    recommendRankMap.put(memberExhibition.getExhibitionId(), memberExhibition.getRank());
                    recommendMaxCount++;
                }
            }
            Logger.of("application").info("Recommend list:" + Arrays.toString(recommendExhibitionIds.toArray()));

            List<ExhibitionListGroup> exhibitionLists = new ArrayList<>();
            List<ExhibitionListGroup> recommendList = new ArrayList<>();
            List<ExhibitionListGroup> futureList = new ArrayList<>();
            List<ExhibitionListGroup> pastList = new ArrayList<>();
            int queryPageIndex = pageIndex;
            int queryPageSize = pageSize;
            int count = 0;

            Logger.of("application").info("query recommend pageIndex={}, pageSize={}", queryPageIndex, queryPageSize);
            int recommendCount = 0;
            if (!memberId.isEmpty() && recommendExhibitionIds.size() > 0) {
                recommendList = ExhibitionListGroup.findExhibitionGroup(keyword, year, nation, null, recommendExhibitionIds, blockIds, true, 0, 10);
                //recommendCount = ExhibitionListGroup.findExhibitionGroupCount(keyword, year, nation, null, recommendExhibitionIds, blockIds, true);
                recommendCount = recommendList.size();
            }
            //int recommendCount = recommendList.size();
            Logger.of("application").info("query recommend pageIndex={}, pageSize={}, list size={}", queryPageIndex, queryPageSize, recommendList.size());
            //Rank recommend
            for (ExhibitionListGroup exhibitionListGroups : recommendList) {
                if (recommendRankMap.containsKey(exhibitionListGroups.getExhibitionId())) {
                    exhibitionListGroups.setRank(recommendRankMap.get(exhibitionListGroups.getExhibitionId()));
                }
            }
            Collections.sort(recommendList, new Comparator<ExhibitionListGroup>() {
                @Override
                public int compare(ExhibitionListGroup p1, ExhibitionListGroup p2) {
                    return p1.getRank().compareTo(p2.getRank()); //ascending
                }
            });
            for (int a = queryPageIndex; a < recommendList.size(); a++) {
                if (exhibitionLists.size() >= pageSize) {
                    break;
                }
                exhibitionLists.add(recommendList.get(a));
            }

            if (blockIds != null) {
                recommendExhibitionIds.addAll(blockIds);
            }
            queryPageIndex = queryPageIndex - recommendCount < 0 ? 0 : queryPageIndex - recommendCount;
            queryPageSize = queryPageSize - exhibitionLists.size();
            if (queryPageIndex >= 0 && queryPageSize > 0) {
                futureList = ExhibitionListGroup.findExhibitionGroup(keyword, year, nation, today, null, recommendExhibitionIds.size() == 0 ? null : recommendExhibitionIds, false, queryPageIndex, queryPageSize);
            }
            int futureCount = ExhibitionListGroup.findExhibitionGroupCount(keyword, year, nation, today, null, recommendExhibitionIds.size() == 0 ? null : recommendExhibitionIds, false);
            Logger.of("application").info("query future pageIndex={}, pageSize={}, list size={}", queryPageIndex, queryPageSize, futureList.size());
            exhibitionLists.addAll(futureList);

            queryPageIndex = queryPageIndex - futureCount < 0 ? 0 : queryPageIndex - futureCount;
            queryPageSize = queryPageSize - exhibitionLists.size();
            if (queryPageIndex >= 0 && queryPageSize > 0) {
                pastList = ExhibitionListGroup.findExhibitionGroup(keyword, year, nation, today, null, recommendExhibitionIds.size() == 0 ? null : recommendExhibitionIds, true, queryPageIndex, queryPageSize);
            }
            int pastCount = ExhibitionListGroup.findExhibitionGroupCount(keyword, year, nation, today, null, recommendExhibitionIds.size() == 0 ? null : recommendExhibitionIds, true);
            Logger.of("application").info("query past pageIndex={}, pageSize={}, list size={}", queryPageIndex, queryPageSize, pastList.size());

            Logger.of("application").info("recommendCount={}, futureCount={}, pastCount={}", recommendCount, futureCount, pastCount);
            count = recommendCount + futureCount + pastCount;
            //exhibitionLists.addAll(recommendList);
            exhibitionLists.addAll(pastList);

            ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
            ArrayNode exhibitionListGroupArray = response.putArray("exhibition_list");
            for (ExhibitionListGroup exhibitionListGroup : exhibitionLists) {
                ObjectNode exhibition = exhibitionListGroupArray.addObject();
                exhibition.put("exhibition_id", exhibitionListGroup.getExhibitionId());
                exhibition.put("title", exhibitionListGroup.getExhibitionTitle() == null ? "" : exhibitionListGroup.getExhibitionTitle());
                exhibition.put("date_start", exhibitionListGroup.getExhibitionSdate() == null ? "" : DateUtils.dateToString(exhibitionListGroup.getExhibitionSdate(), "yyyy-MM-dd"));
                exhibition.put("date_end", exhibitionListGroup.getExhibitionEdate() == null ? "" : DateUtils.dateToString(exhibitionListGroup.getExhibitionEdate(), "yyyy-MM-dd"));
                exhibition.put("locate",exhibitionListGroup.getExhibitionCountry() == null ? "" : exhibitionListGroup.getExhibitionCountry());

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

                exhibition.put("is_collect", memberFavorExhibitionIds.contains(exhibitionListGroup.getExhibitionId()) ? true : false);
                if (exhibitionListGroup.getExhibitionId().equals("88b5b086cf3bc68d64773ee1c0a0395334b17d8e805157aa29d59110c5986a9f")) {
                    exhibition.put("relate_activity_id", "E2FDBD9D-AF7B-4901-AC3D-CFC6B2AB550D");
                } else {
                    exhibition.put("relate_activity_id", "");
                }

                if (recommendRankMap.containsKey(exhibitionListGroup.getExhibitionId())) {
                    exhibition.put("is_favor", true);
                } else {
                    exhibition.put("is_favor", false);
                }
            }

            ObjectNode pagination = response.putObject("pagination");
            int totalPages = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
            pagination.put("total_pages", totalPages);
            pagination.put("total_count", count);

            return ok(response);
        }
    }

    public Result getExhibitionDetail(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        /*
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }
        */
        JsonNode parameter = request.body().asJson();
        if (!parameter.has("member_id") ||!parameter.has("exhibition_id")) {
            return internalServerError(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String memberId = parameter.get("member_id").isNull() ? "" : parameter.get("member_id").asText();
        String exhibitionId = parameter.get("exhibition_id").asText();
        ExhibitionList exhibitionList = ExhibitionList.findExhibitionListById(exhibitionId);

        if (exhibitionList == null) {
            return internalServerError(Json.toJson(Error.EXHIBITION_NOT_FOUND.toErrorMap()));
        }

        //MemberExhibition memberExhibition = MemberExhibition.findByExhibitionIdAndMemberId(exhibitionId, memberId);
        MemberList memberList = MemberList.findByMemberIdAndType(memberId, InsightType.EXHIBITION_FAVORITE_ADD_ID.getAction());
        List<InsightMemberListBean> insightMemberListBeanList = memberList == null ? new ArrayList<>() : memberList.getContent();
        Set<String> favorExhibitionIds = new HashSet<>();
        for (InsightMemberListBean insightMemberListBean : insightMemberListBeanList) {
            favorExhibitionIds.add(insightMemberListBean.getExhibitionId());
        }

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        response.put("title", exhibitionList.getExhibitionTitle() == null ? "" : exhibitionList.getExhibitionTitle());
        response.put("date_start", exhibitionList.getExhibitionSdate() == null ? "" : DateUtils.dateToString(exhibitionList.getExhibitionSdate(), "yyyy-MM-dd"));

        response.put("date_end", exhibitionList.getExhibitionEdate() == null ? "" : DateUtils.dateToString(exhibitionList.getExhibitionEdate(), "yyyy-MM-dd"));
        response.put("locate", exhibitionList.getExhibitionCity() == null ? "" : exhibitionList.getExhibitionCity());
        response.put("exhibition_hall", exhibitionList.getExhibitionVenue() == null ? "" : exhibitionList.getExhibitionVenue());

        response.put("exhibition_intro", exhibitionList.getExhibitionSummary() == null ? "" : exhibitionList.getExhibitionSummary());
        response.put("frequency", exhibitionList.getExhibitionFrequency() == null ? "" : exhibitionList.getExhibitionFrequency());
        response.put("exhibition_host", exhibitionList.getExhibitionOrganizer() == null ? "" : exhibitionList.getExhibitionOrganizer());
        response.put("target_audience", exhibitionList.getExhibitionEligibility() == null ? "" : exhibitionList.getExhibitionEligibility());
        response.put("source_url", exhibitionList.getExhibitionWebsite() == null ? "" : exhibitionList.getExhibitionWebsite());
        if (favorExhibitionIds.contains(exhibitionList.getExhibitionId())) {
            response.put("is_collect", true);
        } else {
            response.put("is_collect", false);
        }

        ArrayNode tagsArray = response.putArray("tags");
        try {
            StringBuilder industrySb = new StringBuilder();
            if (exhibitionList.getExhibitionIndustries() != null) {
                JsonNode jsonNode = (JsonNode) new ObjectMapper().readTree(exhibitionList.getExhibitionIndustries());
                for (int a = 0; a < jsonNode.size(); a++) {
                    industrySb.append(jsonNode.get(a).asText());
                    if (a + 1 < jsonNode.size()) {
                        industrySb.append(",");
                    }
                }
            }
            response.put("industry", industrySb.toString());

            StringBuilder productSb = new StringBuilder();
            if (exhibitionList.getExhibitionProducts() != null) {
                JsonNode jsonNode = (JsonNode) new ObjectMapper().readTree(exhibitionList.getExhibitionProducts());
                for (int a = 0; a < jsonNode.size(); a++) {
                    productSb.append(jsonNode.get(a).asText());
                    if (a + 1 < jsonNode.size()) {
                        productSb.append(",");
                    }
                }
            }
            response.put("exhibition_cat", productSb.toString());

            if (exhibitionList.getExhibitionTags() != null) {
                int index = 0;
                JsonNode jsonNode = (JsonNode) new ObjectMapper().readTree(exhibitionList.getExhibitionTags());
                for (JsonNode node : jsonNode) {
                    if (index == 4)
                        break;
                    ObjectNode tag = tagsArray.addObject();
                    tag.put("tag_id", index);
                    tag.put("tag_name", node.asText());
                    index++;
                }
            }

            List<ExhibitionList> historyExhibitionList = ExhibitionList.findByGroup(exhibitionList.getExhibitionGroup());
            ArrayNode historyViewList = response.putArray("history_view");
            for (ExhibitionList historyExhibition : historyExhibitionList) {
                ObjectNode historyView = historyViewList.addObject();
                historyView.put("year", historyExhibition.getExhibitionYear() == null ? "-" : historyExhibition.getExhibitionYear() + "");
                historyView.put("exhibitor",  historyExhibition.getExhibitionExhibitors() == null ? "-" : historyExhibition.getExhibitionExhibitors() + "");
                historyView.put("visitor", historyExhibition.getExhibitionVisitors() == null ? "-" : historyExhibition.getExhibitionVisitors() + "");
                historyView.put("area", historyExhibition.getExhibitionSpace() == null ? "-" : historyExhibition.getExhibitionSpace() + "");
            }

        } catch (JsonProcessingException e) {
            Logger.of("application").error("error:{}", e);
        }
        response.put("banner_img", exhibitionList.getExhibitionCover() == null ? Utils.getRandomExhibitionImageLink() : exhibitionList.getExhibitionCover());
        if (exhibitionList.getExhibitionId().equals("88b5b086cf3bc68d64773ee1c0a0395334b17d8e805157aa29d59110c5986a9f")) {
            response.put("relate_activity_id", "E2FDBD9D-AF7B-4901-AC3D-CFC6B2AB550D");
        } else {
            response.put("relate_activity_id", "");
        }

        return ok(response);
    }

    public Result getMenu(Http.Request request) {
        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);

        ArrayNode locationArray = response.putArray("nation");
        //ISO3
        List<String> iso3List = Arrays.asList("ABW", "AFG", "AGO", "AIA", "ALB", "AND", "ARE", "ARG", "ARM", "ASM", "ATG", "AUS", "AUT", "AZE", "BDI", "BEL", "BEN", "BES", "BFA", "BGD", "BGR", "BHR", "BHS", "BIH", "BLM", "BLR", "BLZ", "BMU", "BOL", "BRA", "BRB", "BRN", "BTN", "BWA", "CAF", "CAN", "CHE", "CHL", "CHN", "CIV", "CMR", "COD", "COG", "COK", "COL", "COM", "CPV", "CRI", "CUB", "CUW", "CXR", "CYM", "CYP", "CZE", "DEU", "DJI", "DMA", "DNK", "DOM", "DZA", "ECU", "EGY", "ERI", "ESP", "EST", "ETH", "FIN", "FJI", "FLK", "FRA", "FRO", "FSM", "GAB", "GBR", "GEO", "GGY", "GHA", "GIB", "GIN", "GLP", "GMB", "GNB", "GNQ", "GRC", "GRD", "GRL", "GTM", "GUF", "GUM", "GUY", "HKG", "HND", "HRV", "HTI", "HUN", "IDN", "IMN", "IND", "IRL", "IRN", "IRQ", "ISL", "ISR", "ITA", "JAM", "JEY", "JOR", "JPN", "KAZ", "KEN", "KGZ", "KHM", "KIR", "KNA", "KOR", "KWT", "LAO", "LBN", "LBR", "LBY", "LCA", "LIE", "LKA", "LSO", "LTU", "LUX", "LVA", "MAC", "MAF", "MAR", "MCO", "MDA", "MDG", "MDV", "MEX", "MHL", "MKD", "MLI", "MLT", "MMR", "MNE", "MNG", "MNP", "MOZ", "MRT", "MSR", "MTQ", "MUS", "MWI", "MYS", "MYT", "NAM", "NCL", "NER", "NFK", "NGA", "NIC", "NIU", "NLD", "NOR", "NPL", "NRU", "NZL", "OMN", "PAK", "PAN", "PCN", "PER", "PHL", "PLW", "PNG", "POL", "PRI", "PRK", "PRT", "PRY", "PYF", "QAT", "REU", "ROU", "RUS", "RWA", "SAU", "SDN", "SEN", "SGP", "SGS", "SHN", "SLB", "SLE", "SLV", "SMR", "SOM", "SPM", "SRB", "SSD", "STP", "SUR", "SVK", "SVN", "SWE", "SWZ", "SXM", "SYC", "SYR", "TCA", "TCD", "TGO", "THA", "TJK", "TKM", "TLS", "TON", "TTO", "TUN", "TUR", "TUV", "TWN", "TZA", "UGA", "UKR", "URY", "USA", "UZB", "VAT", "VCT", "VEN", "VGB", "VIR", "VNM", "VUT", "WLF", "WSM", "XKS", "YEM", "ZAF", "ZMB", "ZWE");
        //CHT
        List<String> locationList = Arrays.asList("阿魯巴", "阿富汗", "安哥拉", "安圭拉", "阿爾巴尼亞", "安道爾", "阿拉伯聯合大公國", "阿根廷", "亞美尼亞", "美屬薩摩亞", "安地卡及巴布達", "澳大利亞", "奧地利", "亞塞拜然", "蒲隆地", "比利時", "貝南", "波奈及聖佑達修斯及沙巴", "布吉納法索", "孟加拉", "保加利亞", "巴林", "巴哈馬", "波士尼亞及赫塞哥維納", "聖巴瑟米", "白俄羅斯", "貝里斯", "百慕達", "玻利維亞", "巴西", "巴貝多", "汶萊", "不丹", "波札那", "中非", "加拿大", "瑞士", "智利", "中國大陸", "象牙海岸", "喀麥隆", "剛果民主共和國", "剛果", "庫克群島", "哥倫比亞", "葛摩聯盟", "維德角", "哥斯大黎加", "古巴", "古拉索", "聖誕島", "開曼群島", "賽普勒斯", "捷克", "德國", "吉布地", "多米尼克", "丹麥", "多明尼加", "阿爾及利亞", "厄瓜多", "埃及", "厄利垂亞", "西班牙", "愛沙尼亞", "衣索比亞", "芬蘭", "斐濟", "福克蘭群島", "法國", "法羅群島", "密克羅尼西亞", "加彭", "英國", "喬治亞", "格恩西", "迦納", "直布羅陀", "幾內亞", "瓜德魯普", "甘比亞", "幾內亞比索", "赤道幾內亞", "希臘", "格瑞那達", "格陵蘭", "瓜地馬拉", "法屬圭亞那", "關島", "蓋亞那", "香港", "宏都拉斯", "克羅埃西亞", "海地", "匈牙利", "印尼", "曼島", "印度", "愛爾蘭", "伊朗", "伊拉克", "冰島", "以色列", "義大利", "牙買加", "澤西島", "約旦", "日本", "哈薩克", "肯亞", "吉爾吉斯", "柬埔寨", "吉里巴斯", "聖克里斯多福", "南韓", "科威特", "寮國", "黎巴嫩", "賴比瑞亞", "利比亞", "聖露西亞", "列支敦斯登", "斯里蘭卡", "賴索托", "立陶宛", "盧森堡", "拉脫維亞", "澳門", "法屬聖馬丁", "摩洛哥", "摩納哥", "摩爾多瓦", "馬達加斯加", "馬爾地夫", "墨西哥", "馬紹爾群島共和國", "北馬其頓", "馬利", "馬爾他", "緬甸", "蒙特內哥羅", "蒙古", "北馬里亞納群島", "莫三比克", "茅利塔尼亞伊斯蘭共和國", "蒙瑟拉特", "馬丁尼克", "模里西斯", "馬拉威", "馬來西亞", "馬約特", "納米比亞", "紐喀里多尼亞", "尼日", "諾福克島", "奈及利亞", "尼加拉瓜", "紐威", "荷蘭", "挪威", "尼泊爾", "諾魯", "紐西蘭", "阿曼", "巴基斯坦", "巴拿馬", "皮特康", "秘魯", "菲律賓", "帛琉", "巴布亞紐幾內亞", "波蘭", "波多黎各", "北韓", "葡萄牙", "巴拉圭", "法屬玻里尼西亞", "卡達", "留尼旺", "羅馬尼亞", "俄羅斯", "盧安達", "沙烏地阿拉伯", "蘇丹", "塞內加爾", "新加坡", "南喬治亞及南三明治群島", "聖赫勒拿", "索羅門群島", "獅子山", "薩爾瓦多", "聖馬利諾", "索馬利亞", "聖匹及密啟倫群島", "塞爾維亞", "南蘇丹", "聖多美普林西比", "蘇利南", "斯洛伐克", "斯洛維尼亞", "瑞典", "史瓦帝尼", "荷屬聖馬丁", "塞席爾", "敘利亞", "土克斯及開科斯群島", "查德", "多哥", "泰國", "塔吉克", "土庫曼", "東帝汶", "東加", "千里達及托巴哥", "突尼西亞", "土耳其", "吐瓦魯", "臺灣", "坦尚尼亞", "烏干達", "烏克蘭", "烏拉圭", "美國", "烏茲別克", "教廷", "聖文森及格瑞納丁", "委內瑞拉", "英屬維爾京群島", "美屬維京群島", "越南", "萬那杜", "渥里斯及伏塔那", "薩摩亞", "科索沃共和國", "葉門", "南非", "尚比亞", "辛巴威");
        //EN
        List<String> locationListEN = Arrays.asList("Aruba", "Afghanistan", "Angola", "Anguilla", "Albania", "Andorra", "United Arab Emirates", "Argentina", "Armenia", "American Samoa", "Antigua and Barbuda", "Australia", "Austria", "Azerbaijan", "Burundi", "Belgium", "Benin", "Bonaire, Sint Eustatius, and Saba", "Burkina Faso", "Bangladesh", "Bulgaria", "Bahrain", "Bahamas, The", "Bosnia and Herzegovina", "Saint Barthelemy", "Belarus", "Belize", "Bermuda", "Bolivia", "Brazil", "Barbados", "Brunei", "Bhutan", "Botswana", "Central African Republic", "Canada", "Switzerland", "Chile", "China", "Côte d’Ivoire", "Cameroon", "Congo (Kinshasa)", "Congo (Brazzaville)", "Cook Islands", "Colombia", "Comoros", "Cabo Verde", "Costa Rica", "Cuba", "Curaçao", "Christmas Island", "Cayman Islands", "Cyprus", "Czechia", "Germany", "Djibouti", "Dominica", "Denmark", "Dominican Republic", "Algeria", "Ecuador", "Egypt", "Eritrea", "Spain", "Estonia", "Ethiopia", "Finland", "Fiji", "Falkland Islands (Islas Malvinas)", "France", "Faroe Islands", "Micronesia, Federated States of", "Gabon", "United Kingdom", "Georgia", "Guernsey", "Ghana", "Gibraltar", "Guinea", "Guadeloupe", "Gambia, The", "Guinea-Bissau", "Equatorial Guinea", "Greece", "Grenada", "Greenland", "Guatemala", "French Guiana", "Guam", "Guyana", "Hong Kong", "Honduras", "Croatia", "Haiti", "Hungary", "Indonesia", "Isle of Man", "India", "Ireland", "Iran", "Iraq", "Iceland", "Israel", "Italy", "Jamaica", "Jersey", "Jordan", "Japan", "Kazakhstan", "Kenya", "Kyrgyzstan", "Cambodia", "Kiribati", "Saint Kitts and Nevis", "Korea, South", "Kuwait", "Laos", "Lebanon", "Liberia", "Libya", "Saint Lucia", "Liechtenstein", "Sri Lanka", "Lesotho", "Lithuania", "Luxembourg", "Latvia", "Macau", "Saint Martin", "Morocco", "Monaco", "Moldova", "Madagascar", "Maldives", "Mexico", "Marshall Islands", "North Macedonia", "Mali", "Malta", "Burma", "Montenegro", "Mongolia", "Northern Mariana Islands", "Mozambique", "Mauritania", "Montserrat", "Martinique", "Mauritius", "Malawi", "Malaysia", "Mayotte", "Namibia", "New Caledonia", "Niger", "Norfolk Island", "Nigeria", "Nicaragua", "Niue", "Netherlands", "Norway", "Nepal", "Nauru", "New Zealand", "Oman", "Pakistan", "Panama", "Pitcairn Islands", "Peru", "Philippines", "Palau", "Papua New Guinea", "Poland", "Puerto Rico", "Korea, North", "Portugal", "Paraguay", "French Polynesia", "Qatar", "Reunion", "Romania", "Russia", "Rwanda", "Saudi Arabia", "Sudan", "Senegal", "Singapore", "South Georgia And South Sandwich Islands", "Saint Helena, Ascension, and Tristan da Cunha", "Solomon Islands", "Sierra Leone", "El Salvador", "San Marino", "Somalia", "Saint Pierre and Miquelon", "Serbia", "South Sudan", "Sao Tome and Principe", "Suriname", "Slovakia", "Slovenia", "Sweden", "Eswatini", "Sint Maarten", "Seychelles", "Syria", "Turks and Caicos Islands", "Chad", "Togo", "Thailand", "Tajikistan", "Turkmenistan", "Timor-Leste", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Tuvalu", "Taiwan", "Tanzania", "Uganda", "Ukraine", "Uruguay", "United States", "Uzbekistan", "Vatican City", "Saint Vincent and the Grenadines", "Venezuela", "Virgin Islands, British", "U.S. Virgin Islands", "Vietnam", "Vanuatu", "Wallis and Futuna", "Samoa", "Kosovo", "Yemen", "South Africa", "Zambia", "Zimbabwe");
        int locationIndex = 0;
        for (int i = 0; i < locationList.size(); i++) {
            ObjectNode location = locationArray.addObject();
            //location.put("iso3", iso3List.get(i));
            location.put("text", iso3List.get(i) + "-" + locationList.get(i));
            location.put("id",  locationListEN.get(i));
            locationIndex++;
        }

        //length = 6
        int year = Calendar.getInstance().get(Calendar.YEAR) + 2;
        ArrayNode yearArray = response.putArray("year");
        int count = 0;
        for (int a = year; a > 0; a--) {
            if (count >= 6) {
                break;
            }
            yearArray.add(a + "");
            count++;
        }

        return ok(response);
    }

    public Result updateFavoriteExhibitions(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());

        datatypes.Error error = ValidationUtils.verifyRequest(request.toString(), request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }

        JsonNode parameter = request.body().asJson();
        if (!parameter.has("member_id")
                || !parameter.has("exhibition_id")
                || !parameter.has("favor_status")) {
            return internalServerError(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String memberId = parameter.get("member_id").asText();
        String exhibitionId = parameter.get("exhibition_id").asText();
        String favorStatus = parameter.get("favor_status").asText();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        try {
            MemberList memberListADD = MemberList.findByMemberIdAndType(memberId, InsightType.EXHIBITION_FAVORITE_ADD_ID.getAction());
            boolean doADDUpdate = false;
            boolean doDELUpdate = false;
            if (memberListADD == null) {
                memberListADD = new MemberList();
                memberListADD.setMemberId(memberId);
                memberListADD.setType(InsightType.EXHIBITION_FAVORITE_ADD_ID.getAction());
                memberListADD.setMemberListUpdateDatetime(timestamp);
            } else {
                memberListADD.setMemberListUpdateDatetime(timestamp);
                doADDUpdate = true;
            }

            MemberList memberListDEL = MemberList.findByMemberIdAndType(memberId, InsightType.EXHIBITION_FAVORITE_DEL_ID.getAction());
            if (memberListDEL == null) {
                memberListDEL = new MemberList();
                memberListDEL.setMemberId(memberId);
                memberListDEL.setType(InsightType.EXHIBITION_FAVORITE_DEL_ID.getAction());
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
                    String exhId = insightMemberListBeanADDList.get(a).getExhibitionId();
                    addSet.put(exhId, a);
                }
                if (!addSet.containsKey(exhibitionId)) {
                    //put add list
                    InsightMemberListBean insightMemberListBean = new InsightMemberListBean();
                    insightMemberListBean.setExhibitionId(exhibitionId);
                    insightMemberListBeanADDList.add(insightMemberListBean);
                    logBean.add(insightMemberListBean);
                }

                //remove del list
                Map<String, Integer> delSet = new HashMap<>();
                for (int a = 0; a < insightMemberListBeanDELList.size(); a++) {
                    String exhId = insightMemberListBeanDELList.get(a).getExhibitionId();
                    delSet.put(exhId, a);
                }

                //Do del
                if (delSet.containsKey(exhibitionId)) {
                    //Check is exist
                    insightMemberListBeanDELList.remove(delSet.get(exhibitionId).intValue());
                }

                action = InsightType.EXHIBITION_FAVORITE_ADD_ID.getCode();
            }
            else if (favorStatus.equals("N")) {
                Map<String, Integer> delSet = new HashMap<>();

                for (int a = 0; a < insightMemberListBeanDELList.size(); a++) {
                    String exbId = insightMemberListBeanDELList.get(a).getExhibitionId();
                    delSet.put(exbId, a);
                }
                if (!delSet.containsKey(exhibitionId)) {
                    //put del list
                    InsightMemberListBean insightMemberListBean = new InsightMemberListBean();
                    insightMemberListBean.setExhibitionId(exhibitionId);
                    insightMemberListBeanDELList.add(insightMemberListBean);
                    logBean.add(insightMemberListBean);
                }

                //remove add list
                Map<String, Integer> addSet = new HashMap<>();
                for (int a = 0; a < insightMemberListBeanADDList.size(); a++) {
                    String exbId = insightMemberListBeanADDList.get(a).getExhibitionId();
                    addSet.put(exbId, a);
                }

                //Do add
                if (addSet.containsKey(exhibitionId)) {
                    //Check is exist
                    insightMemberListBeanADDList.remove(addSet.get(exhibitionId).intValue());
                }

                action = InsightType.EXHIBITION_FAVORITE_DEL_ID.getCode();
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
                !parameter.has("exhibition_id")) {
            return ok(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String memberId = parameter.get("member_id").asText();
        String exhibitionId = parameter.get("exhibition_id").asText();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        try {
            boolean doDELUpdate = false;
            MemberList memberListDEL = MemberList.findByMemberIdAndType(memberId, InsightType.EXHIBITION_RECOMMEND_DEL_ID.getAction());
            if (memberListDEL == null) {
                memberListDEL = new MemberList();
                memberListDEL.setMemberId(memberId);
                memberListDEL.setType(InsightType.EXHIBITION_RECOMMEND_DEL_ID.getAction());
                memberListDEL.setMemberListUpdateDatetime(timestamp);
            } else {
                memberListDEL.setMemberListUpdateDatetime(timestamp);
                doDELUpdate = true;
            }

            List<InsightMemberListBean> insightMemberListBeanDELList = memberListDEL.getContent() == null ? new ArrayList<>() : memberListDEL.getContent();

            Map<String, Integer> delSet = new HashMap<>();

            for (int a = 0; a < insightMemberListBeanDELList.size(); a++) {
                String exbId = insightMemberListBeanDELList.get(a).getExhibitionId();
                delSet.put(exbId, a);
            }
            if (!delSet.containsKey(exhibitionId)) {
                //put del list
                InsightMemberListBean insightMemberListBean = new InsightMemberListBean();
                insightMemberListBean.setExhibitionId(exhibitionId);
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
