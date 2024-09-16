package models;

import io.ebean.*;
import io.ebean.annotation.Sql;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.List;

@Entity
@Sql
public class TxnUnYearHscode6Raw extends Model {

    @Column(name = "\"TXN_YEAR\"")
    private String txnYear;

    @Column(name = "\"REPORTER_ISO3\"")
    private String reporterIso3;

    @Column(name = "\"PARTNER_ISO3\"")
    private String partnerIso3;

    @Column(name = "\"HSCODE_4\"")
    private String hscode4;

    @Column(name = "\"HSCODE_6\"")
    private String hscode6;

    @Column(name = "\"TRADE_WEIGHT\"")
    private String tradeWeight;

    @Column(name = "\"TRADE_AMT\"")
    private String tradeAmt;

    @Column(name = "\"TRADE_AMT_DIFFERENCE\"")
    private String tradeAmtDifference;

    @Column(name = "\"TRADE_AMT_GROWRATE\"")
    private String tradeAmtGrowrate;

    @Column(name = "\"TRADE_AMT_PERCENT\"")
    private String tradeAmtPercent;

    @Column(name = "\"WEIGHT_DIFFERENCE\"")
    private String weightDifference;

    @Column(name = "\"WEIGHT_GROWRATE\"")
    private String weightGrowrate;

    @Column(name = "\"WEIGHT_PERCENT\"")
    private String weightPercent;

    @Column(name = "\"TARIFF\"")
    private String tariff;

    @Column(name = "\"TRADE_AMT_RANK\"")
    private Integer tradeAmtRank;

    @Column(name = "\"TRADE_AMT_DIFFERENCE_RANK\"")
    private Integer tradeAmtDifferenceRank;

    @Column(name = "\"TRADE_AMT_GROWRATE_RANK\"")
    private Integer tradeAmtGrowrateRank;

    @Column(name = "\"TRADE_AMT_PERCENT_RANK\"")
    private Integer tradeAmtPercentRank;

    @Column(name = "\"WEIGHT_RANK\"")
    private Integer weightRank;

    @Column(name = "\"WEIGHT_DIFFERENCE_RANK\"")
    private Integer weightDifferenceRank;

    @Column(name = "\"WEIGHT_GROWRATE_RANK\"")
    private Integer weightGrowrateRank;

    @Column(name = "\"WEIGHT_PERCENT_RANK\"")
    private Integer weightPercentRank;

    @Column(name = "\"TRADE_AMT_RANK_HSCODE\"")
    private Integer tradeAmtRankHscode;

    @Column(name = "\"TRADE_AMT_DIFFERENCE_RANK_HSCODE\"")
    private Integer tradeAmtDifferenceRankHscode;

    @Column(name = "\"TRADE_AMT_GROWRATE_RANK_HSCODE\"")
    private Integer tradeAmtGrowrateRankHscode;

    @Column(name = "\"WEIGHT_RANK_HSCODE\"")
    private Integer weightRankHscode;

    @Column(name = "\"WEIGHT_DIFFERENCE_RANK_HSCODE\"")
    private Integer weightDifferenceRankHscode;

    @Column(name = "\"WEIGHT_GROWRATE_RANK_HSCODE\"")
    private Integer weightGrowrateRankHscode;

    @Column(name = "\"TRADE_AMT_DIFFERENCE_AREARANK\"")
    private Integer tradeAmtDifferenceArearank;

    @Column(name = "\"TRADE_AMT_AREARANK\"")
    private Integer tradeAmtArearank;

    @Column(name = "\"WEIGHT_AREARANK\"")
    private Integer weightArearank;

    @Column(name = "\"TRADE_AMT_GROWRATE_AREARANK\"")
    private Integer tradeAmtGrowrateArearank;

    @Column(name = "\"TRADE_AMT_PERCENT_AREARANK\"")
    private Integer tradeAmtPercentArearank;

    @Column(name = "\"WEIGHT_DIFFERENCE_AREARANK\"")
    private Integer weightDifferenceArearank;

    @Column(name = "\"WEIGHT_GROWRATE_AREARANK\"")
    private Integer weightGrowrateArearank;

    @Column(name = "\"WEIGHT_PERCENT_AREARANK\"")
    private Integer weightPercentArearank;

    @Column(name = "\"COUNTRY_COMM_ZH\"")
    private String countryCommZh;

    @Column(name = "\"COUNTRY_COMM_EN\"")
    private String countryCommEn;

    @Column(name = "\"AREA_ID\"")
    private String areaId;

    @Column(name = "\"AREA_NM\"")
    private String areaNm;

    public static List<TxnUnYearHscode6Raw> findByHscodeAndCNTY(String hscode, String cnty) {
        String sql = "select\n" +
                " a.\"TXN_YEAR\", a.\"REPORTER_ISO3\", a.\"PARTNER_ISO3\", a.\"HSCODE_4\", a.\"HSCODE_6\",\n" +
                " a.\"TRADE_WEIGHT\", a.\"TRADE_AMT\", a.\"TRADE_AMT_DIFFERENCE\", a.\"TRADE_AMT_GROWRATE\", a.\"TRADE_AMT_PERCENT\", a.\"WEIGHT_DIFFERENCE\", a.\"WEIGHT_GROWRATE\", a.\"WEIGHT_PERCENT\", a.\"TARIFF\",\n" +
                " a.\"TRADE_AMT_RANK\", a.\"TRADE_AMT_DIFFERENCE_RANK\", a.\"TRADE_AMT_GROWRATE_RANK\", a.\"TRADE_AMT_PERCENT_RANK\", a.\"WEIGHT_RANK\", a.\"WEIGHT_DIFFERENCE_RANK\", a.\"WEIGHT_GROWRATE_RANK\", a.\"WEIGHT_PERCENT_RANK\",\n" +
                " a.\"TRADE_AMT_RANK_HSCODE\", a.\"TRADE_AMT_DIFFERENCE_RANK_HSCODE\", a.\"TRADE_AMT_GROWRATE_RANK_HSCODE\", a.\"WEIGHT_RANK_HSCODE\", a.\"WEIGHT_DIFFERENCE_RANK_HSCODE\", a.\"WEIGHT_GROWRATE_RANK_HSCODE\",\n" +
                " b.\"COUNTRY_COMM_ZH\", b.\"COUNTRY_COMM_EN\", b.\"AREA_ID\", b.\"AREA_NM\"\n" +
                " from public.\"TXN_UN_YEAR_HSCODE6\" a\n" +
                " LEFT JOIN \"REF_COUNTRY_AREA\" b ON a.\"REPORTER_ISO3\" = b.\"ISO3\"\n" +
                " where a.\"HSCODE_4\" = '" + hscode + "' and a.\"REPORTER_ISO3\" = '" + cnty + "' and a.\"PARTNER_ISO3\" in ('WLD', 'TWN');";

        RawSql rawSql = RawSqlBuilder.unparsed(sql)
                .columnMapping("a.\"TXN_YEAR\"", "txnYear")
                .columnMapping("a.\"REPORTER_ISO3\"", "reporterIso3")
                .columnMapping("a.\"PARTNER_ISO3\"", "partnerIso3")
                .columnMapping("a.\"HSCODE_4\"", "hscode4")
                .columnMapping("a.\"HSCODE_6\"", "hscode6")

                .columnMapping("a.\"TRADE_WEIGHT\"", "tradeWeight")
                .columnMapping("a.\"TRADE_AMT\"", "tradeAmt")
                .columnMapping("a.\"TRADE_AMT_DIFFERENCE\"", "tradeAmtDifference")
                .columnMapping("a.\"TRADE_AMT_GROWRATE\"", "tradeAmtGrowrate")
                .columnMapping("a.\"TRADE_AMT_PERCENT\"", "tradeAmtPercent")
                .columnMapping("a.\"WEIGHT_DIFFERENCE\"", "weightDifference")
                .columnMapping("a.\"WEIGHT_GROWRATE\"", "weightGrowrate")
                .columnMapping("a.\"WEIGHT_PERCENT\"", "weightPercent")
                .columnMapping("a.\"TARIFF\"", "tariff")

                .columnMapping("a.\"TRADE_AMT_RANK\"", "tradeAmtRank")
                .columnMapping("a.\"TRADE_AMT_DIFFERENCE_RANK\"", "tradeAmtDifferenceRank")
                .columnMapping("a.\"TRADE_AMT_GROWRATE_RANK\"", "tradeAmtGrowrateRank")
                .columnMapping("a.\"TRADE_AMT_PERCENT_RANK\"", "tradeAmtPercentRank")
                .columnMapping("a.\"WEIGHT_RANK\"", "weightRank")
                .columnMapping("a.\"WEIGHT_DIFFERENCE_RANK\"", "weightDifferenceRank")
                .columnMapping("a.\"WEIGHT_GROWRATE_RANK\"", "weightGrowrateRank")
                .columnMapping("a.\"WEIGHT_PERCENT_RANK\"", "weightPercentRank")

                .columnMapping("a.\"TRADE_AMT_RANK_HSCODE\"", "tradeAmtRankHscode")
                .columnMapping("a.\"TRADE_AMT_DIFFERENCE_RANK_HSCODE\"", "tradeAmtDifferenceRankHscode")
                .columnMapping("a.\"TRADE_AMT_GROWRATE_RANK_HSCODE\"", "tradeAmtGrowrateRankHscode")
                .columnMapping("a.\"WEIGHT_RANK_HSCODE\"", "weightRankHscode")
                .columnMapping("a.\"WEIGHT_DIFFERENCE_RANK_HSCODE\"", "weightDifferenceRankHscode")
                .columnMapping("a.\"WEIGHT_GROWRATE_RANK_HSCODE\"", "weightGrowrateRankHscode")

                .columnMapping("b.\"COUNTRY_COMM_ZH\"", "countryCommZh")
                .columnMapping("b.\"COUNTRY_COMM_EN\"", "countryCommEn")
                .columnMapping("b.\"AREA_ID\"", "areaId")
                .columnMapping("b.\"AREA_NM\"", "areaNm")
                .create();

        Query<TxnUnYearHscode6Raw> query = DB.find(TxnUnYearHscode6Raw.class);
        query.setRawSql(rawSql);
        return query.findList();
    }

    public static List<TxnUnYearHscode6Raw> findByHscode6AndCNTYAndArea(String hscode, String cnty, String area) {
        String sql = "select\n" +
                " a.\"TXN_YEAR\", a.\"REPORTER_ISO3\", a.\"PARTNER_ISO3\", a.\"HSCODE_6\", a.\"TRADE_WEIGHT\", a.\"TRADE_AMT\", a.\"TRADE_AMT_DIFFERENCE\", a.\"TRADE_AMT_GROWRATE\", a.\"TRADE_AMT_PERCENT\", a.\"WEIGHT_DIFFERENCE\", a.\"WEIGHT_GROWRATE\", a.\"WEIGHT_PERCENT\",\n" +
                " a.\"TRADE_AMT_RANK\", a.\"TRADE_AMT_DIFFERENCE_RANK\", a.\"TRADE_AMT_GROWRATE_RANK\", a.\"TRADE_AMT_PERCENT_RANK\", a.\"WEIGHT_RANK\", a.\"WEIGHT_DIFFERENCE_RANK\", a.\"WEIGHT_GROWRATE_RANK\", a.\"WEIGHT_PERCENT_RANK\",\n" +
                " a.\"TRADE_AMT_AREARANK\", a.\"TRADE_AMT_DIFFERENCE_AREARANK\", a.\"TRADE_AMT_GROWRATE_AREARANK\", a.\"TRADE_AMT_PERCENT_AREARANK\", a.\"WEIGHT_AREARANK\", a.\"WEIGHT_DIFFERENCE_AREARANK\", a.\"WEIGHT_GROWRATE_AREARANK\", a.\"WEIGHT_PERCENT_AREARANK\", a.\"TARIFF\",\n" +
                " b.\"COUNTRY_COMM_ZH\", b.\"COUNTRY_COMM_EN\", b.\"AREA_ID\", b.\"AREA_NM\"\n" +
                " from public.\"TXN_UN_YEAR_HSCODE6\" a\n" +
                " LEFT JOIN \"REF_COUNTRY_AREA\" b ON a.\"REPORTER_ISO3\" = b.\"ISO3\"\n" +
                " where a.\"HSCODE_6\" = '" + hscode + "' and a.\"REPORTER_ISO3\" = '" + cnty + "'";
        if (area != null && !area.isEmpty()) {
            sql += " and b.\"AREA_ID\" = '" + area + "';";
        }
        RawSql rawSql = RawSqlBuilder.unparsed(sql)
                .columnMapping("a.\"TXN_YEAR\"", "txnYear")
                .columnMapping("a.\"REPORTER_ISO3\"", "reporterIso3")
                .columnMapping("a.\"PARTNER_ISO3\"", "partnerIso3")
                .columnMapping("a.\"HSCODE_6\"", "hscode6")

                .columnMapping("a.\"TRADE_WEIGHT\"", "tradeWeight")
                .columnMapping("a.\"TRADE_AMT\"", "tradeAmt")
                .columnMapping("a.\"TRADE_AMT_DIFFERENCE\"", "tradeAmtDifference")
                .columnMapping("a.\"TRADE_AMT_GROWRATE\"", "tradeAmtGrowrate")
                .columnMapping("a.\"TRADE_AMT_PERCENT\"", "tradeAmtPercent")
                .columnMapping("a.\"WEIGHT_DIFFERENCE\"", "weightDifference")
                .columnMapping("a.\"WEIGHT_GROWRATE\"", "weightGrowrate")
                .columnMapping("a.\"WEIGHT_PERCENT\"", "weightPercent")

                .columnMapping("a.\"TRADE_AMT_RANK\"", "tradeAmtRank")
                .columnMapping("a.\"TRADE_AMT_DIFFERENCE_RANK\"", "tradeAmtDifferenceRank")
                .columnMapping("a.\"TRADE_AMT_GROWRATE_RANK\"", "tradeAmtGrowrateRank")
                .columnMapping("a.\"TRADE_AMT_PERCENT_RANK\"", "tradeAmtPercentRank")
                .columnMapping("a.\"WEIGHT_RANK\"", "weightRank")
                .columnMapping("a.\"WEIGHT_DIFFERENCE_RANK\"", "weightDifferenceRank")
                .columnMapping("a.\"WEIGHT_GROWRATE_RANK\"", "weightGrowrateRank")
                .columnMapping("a.\"WEIGHT_PERCENT_RANK\"", "weightPercentRank")

                .columnMapping("a.\"TRADE_AMT_AREARANK\"", "tradeAmtArearank")
                .columnMapping("a.\"TRADE_AMT_DIFFERENCE_AREARANK\"", "tradeAmtDifferenceArearank")
                .columnMapping("a.\"TRADE_AMT_GROWRATE_AREARANK\"", "tradeAmtGrowrateArearank")
                .columnMapping("a.\"TRADE_AMT_PERCENT_AREARANK\"", "tradeAmtPercentArearank")
                .columnMapping("a.\"WEIGHT_AREARANK\"", "weightArearank")
                .columnMapping("a.\"WEIGHT_DIFFERENCE_AREARANK\"", "weightDifferenceArearank")
                .columnMapping("a.\"WEIGHT_GROWRATE_AREARANK\"", "weightGrowrateArearank")
                .columnMapping("a.\"WEIGHT_PERCENT_AREARANK\"", "weightPercentArearank")
                .columnMapping("a.\"TARIFF\"", "tariff")

                .columnMapping("b.\"COUNTRY_COMM_ZH\"", "countryCommZh")
                .columnMapping("b.\"COUNTRY_COMM_EN\"", "countryCommEn")
                .columnMapping("b.\"AREA_ID\"", "areaId")
                .columnMapping("b.\"AREA_NM\"", "areaNm")
                .create();

        Query<TxnUnYearHscode6Raw> query = DB.find(TxnUnYearHscode6Raw.class);
        query.setRawSql(rawSql);
        return query.findList();
    }

    public String getTxnYear() {
        return txnYear;
    }

    public void setTxnYear(String txnYear) {
        this.txnYear = txnYear;
    }

    public String getReporterIso3() {
        return reporterIso3;
    }

    public void setReporterIso3(String reporterIso3) {
        this.reporterIso3 = reporterIso3;
    }

    public String getPartnerIso3() {
        return partnerIso3;
    }

    public void setPartnerIso3(String partnerIso3) {
        this.partnerIso3 = partnerIso3;
    }

    public String getHscode4() {
        return hscode4;
    }

    public void setHscode4(String hscode4) {
        this.hscode4 = hscode4;
    }

    public String getHscode6() {
        return hscode6;
    }

    public void setHscode6(String hscode6) {
        this.hscode6 = hscode6;
    }

    public String getTradeWeight() {
        return tradeWeight;
    }

    public void setTradeWeight(String tradeWeight) {
        this.tradeWeight = tradeWeight;
    }

    public String getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(String tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public String getTradeAmtDifference() {
        return tradeAmtDifference;
    }

    public void setTradeAmtDifference(String tradeAmtDifference) {
        this.tradeAmtDifference = tradeAmtDifference;
    }

    public String getTradeAmtGrowrate() {
        return tradeAmtGrowrate;
    }

    public void setTradeAmtGrowrate(String tradeAmtGrowrate) {
        this.tradeAmtGrowrate = tradeAmtGrowrate;
    }

    public String getTradeAmtPercent() {
        return tradeAmtPercent;
    }

    public void setTradeAmtPercent(String tradeAmtPercent) {
        this.tradeAmtPercent = tradeAmtPercent;
    }

    public String getWeightDifference() {
        return weightDifference;
    }

    public void setWeightDifference(String weightDifference) {
        this.weightDifference = weightDifference;
    }

    public String getWeightGrowrate() {
        return weightGrowrate;
    }

    public void setWeightGrowrate(String weightGrowrate) {
        this.weightGrowrate = weightGrowrate;
    }

    public String getWeightPercent() {
        return weightPercent;
    }

    public void setWeightPercent(String weightPercent) {
        this.weightPercent = weightPercent;
    }

    public String getTariff() {
        return tariff;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }

    public Integer getTradeAmtRank() {
        return tradeAmtRank;
    }

    public void setTradeAmtRank(Integer tradeAmtRank) {
        this.tradeAmtRank = tradeAmtRank;
    }

    public Integer getTradeAmtDifferenceRank() {
        return tradeAmtDifferenceRank;
    }

    public void setTradeAmtDifferenceRank(Integer tradeAmtDifferenceRank) {
        this.tradeAmtDifferenceRank = tradeAmtDifferenceRank;
    }

    public Integer getTradeAmtGrowrateRank() {
        return tradeAmtGrowrateRank;
    }

    public void setTradeAmtGrowrateRank(Integer tradeAmtGrowrateRank) {
        this.tradeAmtGrowrateRank = tradeAmtGrowrateRank;
    }

    public Integer getTradeAmtPercentRank() {
        return tradeAmtPercentRank;
    }

    public void setTradeAmtPercentRank(Integer tradeAmtPercentRank) {
        this.tradeAmtPercentRank = tradeAmtPercentRank;
    }

    public Integer getWeightRank() {
        return weightRank;
    }

    public void setWeightRank(Integer weightRank) {
        this.weightRank = weightRank;
    }

    public Integer getWeightDifferenceRank() {
        return weightDifferenceRank;
    }

    public void setWeightDifferenceRank(Integer weightDifferenceRank) {
        this.weightDifferenceRank = weightDifferenceRank;
    }

    public Integer getWeightGrowrateRank() {
        return weightGrowrateRank;
    }

    public void setWeightGrowrateRank(Integer weightGrowrateRank) {
        this.weightGrowrateRank = weightGrowrateRank;
    }

    public Integer getWeightPercentRank() {
        return weightPercentRank;
    }

    public void setWeightPercentRank(Integer weightPercentRank) {
        this.weightPercentRank = weightPercentRank;
    }

    public Integer getTradeAmtRankHscode() {
        return tradeAmtRankHscode;
    }

    public void setTradeAmtRankHscode(Integer tradeAmtRankHscode) {
        this.tradeAmtRankHscode = tradeAmtRankHscode;
    }

    public Integer getTradeAmtDifferenceRankHscode() {
        return tradeAmtDifferenceRankHscode;
    }

    public void setTradeAmtDifferenceRankHscode(Integer tradeAmtDifferenceRankHscode) {
        this.tradeAmtDifferenceRankHscode = tradeAmtDifferenceRankHscode;
    }

    public Integer getTradeAmtGrowrateRankHscode() {
        return tradeAmtGrowrateRankHscode;
    }

    public void setTradeAmtGrowrateRankHscode(Integer tradeAmtGrowrateRankHscode) {
        this.tradeAmtGrowrateRankHscode = tradeAmtGrowrateRankHscode;
    }

    public Integer getWeightRankHscode() {
        return weightRankHscode;
    }

    public void setWeightRankHscode(Integer weightRankHscode) {
        this.weightRankHscode = weightRankHscode;
    }

    public Integer getWeightDifferenceRankHscode() {
        return weightDifferenceRankHscode;
    }

    public void setWeightDifferenceRankHscode(Integer weightDifferenceRankHscode) {
        this.weightDifferenceRankHscode = weightDifferenceRankHscode;
    }

    public Integer getWeightGrowrateRankHscode() {
        return weightGrowrateRankHscode;
    }

    public void setWeightGrowrateRankHscode(Integer weightGrowrateRankHscode) {
        this.weightGrowrateRankHscode = weightGrowrateRankHscode;
    }

    public Integer getTradeAmtDifferenceArearank() {
        return tradeAmtDifferenceArearank;
    }

    public void setTradeAmtDifferenceArearank(Integer tradeAmtDifferenceArearank) {
        this.tradeAmtDifferenceArearank = tradeAmtDifferenceArearank;
    }

    public Integer getTradeAmtArearank() {
        return tradeAmtArearank;
    }

    public void setTradeAmtArearank(Integer tradeAmtArearank) {
        this.tradeAmtArearank = tradeAmtArearank;
    }

    public Integer getWeightArearank() {
        return weightArearank;
    }

    public void setWeightArearank(Integer weightArearank) {
        this.weightArearank = weightArearank;
    }

    public Integer getTradeAmtGrowrateArearank() {
        return tradeAmtGrowrateArearank;
    }

    public void setTradeAmtGrowrateArearank(Integer tradeAmtGrowrateArearank) {
        this.tradeAmtGrowrateArearank = tradeAmtGrowrateArearank;
    }

    public Integer getTradeAmtPercentArearank() {
        return tradeAmtPercentArearank;
    }

    public void setTradeAmtPercentArearank(Integer tradeAmtPercentArearank) {
        this.tradeAmtPercentArearank = tradeAmtPercentArearank;
    }

    public Integer getWeightDifferenceArearank() {
        return weightDifferenceArearank;
    }

    public void setWeightDifferenceArearank(Integer weightDifferenceArearank) {
        this.weightDifferenceArearank = weightDifferenceArearank;
    }

    public Integer getWeightGrowrateArearank() {
        return weightGrowrateArearank;
    }

    public void setWeightGrowrateArearank(Integer weightGrowrateArearank) {
        this.weightGrowrateArearank = weightGrowrateArearank;
    }

    public Integer getWeightPercentArearank() {
        return weightPercentArearank;
    }

    public void setWeightPercentArearank(Integer weightPercentArearank) {
        this.weightPercentArearank = weightPercentArearank;
    }

    public String getCountryCommZh() {
        return countryCommZh;
    }

    public void setCountryCommZh(String countryCommZh) {
        this.countryCommZh = countryCommZh;
    }

    public String getCountryCommEn() {
        return countryCommEn;
    }

    public void setCountryCommEn(String countryCommEn) {
        this.countryCommEn = countryCommEn;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaNm() {
        return areaNm;
    }

    public void setAreaNm(String areaNm) {
        this.areaNm = areaNm;
    }
}
