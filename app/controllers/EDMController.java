package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import datatypes.Error;
import models.CompanyList;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.Utils;
import utils.ValidationUtils;

import java.util.Arrays;
import java.util.List;

public class EDMController extends Controller {

    public Result getTrait(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        return ok(response);
    }

    public Result getCompany(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());
        datatypes.Error error = ValidationUtils.verifyRequest("Test", request);
        if (error != null) {
            return internalServerError(Json.toJson(error.toErrorMap()));
        }

        JsonNode parameter = request.body().asJson();
        if (!parameter.has("company_no")) {
            return internalServerError(Json.toJson(Error.NOT_ENOUGH_PARAMETERS.toErrorMap()));
        }

        String companyNo = parameter.get("company_no").asText();

        if (!Utils.checkBanNumber(companyNo)) {
            return internalServerError(Json.toJson(Error.COMPANY_BAN_FORMAT_ERROR.toErrorMap()));
        }

        CompanyList company = CompanyList.findByCompanyBan(companyNo);
        if (company == null) {
            return internalServerError(Json.toJson(Error.COMPANY_NOT_FOUND.toErrorMap()));
        }

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        response.put("company_ban", company.getCompanyBan() == null ? "" : company.getCompanyBan());
        response.put("company_name", company.getCompanyTitle() == null ? "" : company.getCompanyTitle());
        try {
            ArrayNode activityArray = response.putArray("company_activity");
            if (company.getCompanyActivity() != null) {
                int index = 0;
                JsonNode jsonNode = (JsonNode) new ObjectMapper().readTree(company.getCompanyActivity());
                for (JsonNode node : jsonNode) {
                    activityArray.add(node.asText());
                    index++;
                }
            }
            ArrayNode industryArray = response.putArray("company_industry");
            if (company.getCompanyIndustry() != null) {
                int index = 0;
                JsonNode jsonNode = (JsonNode) new ObjectMapper().readTree(company.getCompanyIndustry());
                for (JsonNode node : jsonNode) {
                    industryArray.add(node.asText());
                    index++;
                }
            }
            ArrayNode productArray = response.putArray("company_product");
            if (company.getCompanyProduct() != null) {
                int index = 0;
                JsonNode jsonNode = (JsonNode) new ObjectMapper().readTree(company.getCompanyProduct());
                for (JsonNode node : jsonNode) {
                    productArray.add(node.asText());
                    index++;
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return ok(response);
    }

    public Result getInterest(Http.Request request) {
        Logger.debug("Request payload : {}", request.toString());

        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode nationArray = response.putArray("nation");
        ArrayNode industryArray = response.putArray("industry");
        //ISO3
        List<String> iso3List = Arrays.asList("ABW", "AFG", "AGO", "AIA", "ALB", "AND", "ARE", "ARG", "ARM", "ASM", "ATG", "AUS", "AUT", "AZE", "BDI", "BEL", "BEN", "BES", "BFA", "BGD", "BGR", "BHR", "BHS", "BIH", "BLM", "BLR", "BLZ", "BMU", "BOL", "BRA", "BRB", "BRN", "BTN", "BWA", "CAF", "CAN", "CHE", "CHL", "CHN", "CIV", "CMR", "COD", "COG", "COK", "COL", "COM", "CPV", "CRI", "CUB", "CUW", "CXR", "CYM", "CYP", "CZE", "DEU", "DJI", "DMA", "DNK", "DOM", "DZA", "ECU", "EGY", "ERI", "ESP", "EST", "ETH", "FIN", "FJI", "FLK", "FRA", "FRO", "FSM", "GAB", "GBR", "GEO", "GGY", "GHA", "GIB", "GIN", "GLP", "GMB", "GNB", "GNQ", "GRC", "GRD", "GRL", "GTM", "GUF", "GUM", "GUY", "HKG", "HND", "HRV", "HTI", "HUN", "IDN", "IMN", "IND", "IRL", "IRN", "IRQ", "ISL", "ISR", "ITA", "JAM", "JEY", "JOR", "JPN", "KAZ", "KEN", "KGZ", "KHM", "KIR", "KNA", "KOR", "KWT", "LAO", "LBN", "LBR", "LBY", "LCA", "LIE", "LKA", "LSO", "LTU", "LUX", "LVA", "MAC", "MAF", "MAR", "MCO", "MDA", "MDG", "MDV", "MEX", "MHL", "MKD", "MLI", "MLT", "MMR", "MNE", "MNG", "MNP", "MOZ", "MRT", "MSR", "MTQ", "MUS", "MWI", "MYS", "MYT", "NAM", "NCL", "NER", "NFK", "NGA", "NIC", "NIU", "NLD", "NOR", "NPL", "NRU", "NZL", "OMN", "PAK", "PAN", "PCN", "PER", "PHL", "PLW", "PNG", "POL", "PRI", "PRK", "PRT", "PRY", "PYF", "QAT", "REU", "ROU", "RUS", "RWA", "SAU", "SDN", "SEN", "SGP", "SGS", "SHN", "SLB", "SLE", "SLV", "SMR", "SOM", "SPM", "SRB", "SSD", "STP", "SUR", "SVK", "SVN", "SWE", "SWZ", "SXM", "SYC", "SYR", "TCA", "TCD", "TGO", "THA", "TJK", "TKM", "TLS", "TON", "TTO", "TUN", "TUR", "TUV", "TWN", "TZA", "UGA", "UKR", "URY", "USA", "UZB", "VAT", "VCT", "VEN", "VGB", "VIR", "VNM", "VUT", "WLF", "WSM", "XKS", "YEM", "ZAF", "ZMB", "ZWE");
        //
        List<String> locationList = Arrays.asList("阿魯巴", "阿富汗", "安哥拉", "安圭拉", "阿爾巴尼亞", "安道爾", "阿拉伯聯合大公國", "阿根廷", "亞美尼亞", "美屬薩摩亞", "安地卡及巴布達", "澳大利亞", "奧地利", "亞塞拜然", "蒲隆地", "比利時", "貝南", "波奈及聖佑達修斯及沙巴", "布吉納法索", "孟加拉", "保加利亞", "巴林", "巴哈馬", "波士尼亞及赫塞哥維納", "聖巴瑟米", "白俄羅斯", "貝里斯", "百慕達", "玻利維亞", "巴西", "巴貝多", "汶萊", "不丹", "波札那", "中非", "加拿大", "瑞士", "智利", "中國大陸", "象牙海岸", "喀麥隆", "剛果民主共和國", "剛果", "庫克群島", "哥倫比亞", "葛摩聯盟", "維德角", "哥斯大黎加", "古巴", "古拉索", "聖誕島", "開曼群島", "賽普勒斯", "捷克", "德國", "吉布地", "多米尼克", "丹麥", "多明尼加", "阿爾及利亞", "厄瓜多", "埃及", "厄利垂亞", "西班牙", "愛沙尼亞", "衣索比亞", "芬蘭", "斐濟", "福克蘭群島", "法國", "法羅群島", "密克羅尼西亞", "加彭", "英國", "喬治亞", "格恩西", "迦納", "直布羅陀", "幾內亞", "瓜德魯普", "甘比亞", "幾內亞比索", "赤道幾內亞", "希臘", "格瑞那達", "格陵蘭", "瓜地馬拉", "法屬圭亞那", "關島", "蓋亞那", "香港", "宏都拉斯", "克羅埃西亞", "海地", "匈牙利", "印尼", "曼島", "印度", "愛爾蘭", "伊朗", "伊拉克", "冰島", "以色列", "義大利", "牙買加", "澤西島", "約旦", "日本", "哈薩克", "肯亞", "吉爾吉斯", "柬埔寨", "吉里巴斯", "聖克里斯多福", "南韓", "科威特", "寮國", "黎巴嫩", "賴比瑞亞", "利比亞", "聖露西亞", "列支敦斯登", "斯里蘭卡", "賴索托", "立陶宛", "盧森堡", "拉脫維亞", "澳門", "法屬聖馬丁", "摩洛哥", "摩納哥", "摩爾多瓦", "馬達加斯加", "馬爾地夫", "墨西哥", "馬紹爾群島共和國", "北馬其頓", "馬利", "馬爾他", "緬甸", "蒙特內哥羅", "蒙古", "北馬里亞納群島", "莫三比克", "茅利塔尼亞伊斯蘭共和國", "蒙瑟拉特", "馬丁尼克", "模里西斯", "馬拉威", "馬來西亞", "馬約特", "納米比亞", "紐喀里多尼亞", "尼日", "諾福克島", "奈及利亞", "尼加拉瓜", "紐威", "荷蘭", "挪威", "尼泊爾", "諾魯", "紐西蘭", "阿曼", "巴基斯坦", "巴拿馬", "皮特康", "秘魯", "菲律賓", "帛琉", "巴布亞紐幾內亞", "波蘭", "波多黎各", "北韓", "葡萄牙", "巴拉圭", "法屬玻里尼西亞", "卡達", "留尼旺", "羅馬尼亞", "俄羅斯", "盧安達", "沙烏地阿拉伯", "蘇丹", "塞內加爾", "新加坡", "南喬治亞及南三明治群島", "聖赫勒拿", "索羅門群島", "獅子山", "薩爾瓦多", "聖馬利諾", "索馬利亞", "聖匹及密啟倫群島", "塞爾維亞", "南蘇丹", "聖多美普林西比", "蘇利南", "斯洛伐克", "斯洛維尼亞", "瑞典", "史瓦帝尼", "荷屬聖馬丁", "塞席爾", "敘利亞", "土克斯及開科斯群島", "查德", "多哥", "泰國", "塔吉克", "土庫曼", "東帝汶", "東加", "千里達及托巴哥", "突尼西亞", "土耳其", "吐瓦魯", "臺灣", "坦尚尼亞", "烏干達", "烏克蘭", "烏拉圭", "美國", "烏茲別克", "教廷", "聖文森及格瑞納丁", "委內瑞拉", "英屬維爾京群島", "美屬維京群島", "越南", "萬那杜", "渥里斯及伏塔那", "薩摩亞", "科索沃共和國", "葉門", "南非", "尚比亞", "辛巴威");
        for (int i = 0; i < locationList.size(); i++) {
            nationArray.add(iso3List.get(i) + "-" + locationList.get(i));
        }
        /*
        for (String s : locationList) {
            nationArray.add(s);
        }
        */
        List<String> industryList = Arrays.asList("航太", "嬰幼兒用品", "建材", "設計業", "能源", "食品", "綠能環保", "物聯網", "機械及機械零件", "辦公室及學校用品", "智慧機械", "智慧建築", "安控器材", "服飾及配件", "VR", "農業", "竹炭", "電腦", "電子商務", "紡織及皮革", "食品加工", "五金工具", "LED照明", "製造業", "寵物用品", "橡塑膠", "太陽能", "科技", "隔熱鋁箔", "鐘錶珠寶及眼鏡", "樂器", "美容及個人用品", "消費性電子", "教育", "扣件", "連鎖加盟", "醫療及保健", "燈光及照明", "礦產及金屬材料", "公共工程", "安全", "運動休閒", "電子資訊", "玩具", "汽機車", "自行車", "美妝", "電動車", "金融", "禮品及藝術品", "飯店", "長照", "電影", "大眾運輸", "服務業", "新創", "量測及分析儀器", "運輸", "航空業", "生技醫療", "文化創意", "電機及電子", "漁業", "政府採購", "家庭用電器", "箱包", "清真", "軌道產業", "船舶", "文具家用品", "紡織", "觀光旅遊");
        for (String s : industryList) {
            industryArray.add(s);
        }

        return ok(response);
    }
}
