package models;

import io.ebean.*;
import io.ebean.annotation.Sql;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.List;

@Entity
@Sql
public class TxnMofYearHscode6Raw extends Model {

    @Column(name = "\"TXN_YEAR\"")
    private String txnYear;

    @Column(name = "\"HSCODE_4\"")
    private String hscode4;

    @Column(name = "\"HSCODE_6\"")
    private String hscode6;

    @Column(name = "\"PARTNER_ISO3\"")
    private String partnerISO3;

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

    @Column(name = "\"TRADE_AMT_RANK_HSCODE\"")
    private Integer tradeAmtRankHscode;

    @Column(name = "\"TRADE_AMT_DIFFERENCE_RANK_HSCODE\"")
    private Integer tradeAmtDifferenceRankHscode;

    @Column(name = "\"TRADE_AMT_GROWRATE_RANK_HSCODE\"")
    private Integer tradeAmtGrowrateRankHscode;

    @Column(name = "\"TRADE_AMT_PERCENT_RANK_HSCODE\"")
    private Integer tradeAmtPercentRankHscode;

    @Column(name = "\"WEIGHT_RANK_HSCODE\"")
    private Integer weightRankHscode;

    @Column(name = "\"WEIGHT_DIFFERENCE_RANK_HSCODE\"")
    private Integer weightDifferenceRankHscode;

    @Column(name = "\"WEIGHT_GROWRATE_RANK_HSCODE\"")
    private Integer weightGrowrateRankHscode;

    @Column(name = "\"WEIGHT_PERCENT_RANK_HSCODE\"")
    private Integer weightPercentRankHscode;

    @Column(name = "\"TARIFF\"")
    private String tariff;

    public static List<TxnMofYearHscode6Raw> findByHscodeAndCNTY(String hscode, String cnty) {
        /*
        String sql = "select a.\"TXN_YEAR\", a.\"HSCODE_6\",\n" +
                "    a.\"TRADE_WEIGHT\", a.\"TRADE_AMT\", a.\"TRADE_AMT_DIFFERENCE\", a.\"TRADE_AMT_GROWRATE\", a.\"TRADE_AMT_PERCENT\", a.\"WEIGHT_DIFFERENCE\", a.\"WEIGHT_GROWRATE\", a.\"WEIGHT_PERCENT\",\n" +
                "    a.\"TRADE_AMT_RANK_HSCODE\", a.\"TRADE_AMT_DIFFERENCE_RANK_HSCODE\", a.\"TRADE_AMT_GROWRATE_RANK_HSCODE\", a.\"TRADE_AMT_PERCENT_RANK_HSCODE\", a.\"WEIGHT_RANK_HSCODE\", a.\"WEIGHT_DIFFERENCE_RANK_HSCODE\", a.\"WEIGHT_GROWRATE_RANK_HSCODE\", a.\"WEIGHT_PERCENT_RANK_HSCODE\",\n" +
                "    b.\"TARIFF\"\n" +
                "    from public.\"TXN_MOF_YEAR_HSCODE6\" a\n" +
                "    LEFT JOIN public.\"TXN_UN_YEAR_HSCODE6\" b ON a.\"TXN_YEAR\" = b.\"TXN_YEAR\" and a.\"HSCODE_6\" = b.\"HSCODE_6\" and a.\"PARTNER_ISO3\" = b.\"PARTNER_ISO3\"\n" +
                "    where a.\"HSCODE_4\" = '" + hscode + "' and a.\"PARTNER_ISO3\" = '" + cnty + "' and b.\"REPORTER_ISO3\" = 'TWN';";
        */
        String sql = "select a.\"HSCODE_4\", a.\"HSCODE_6\", a.\"PARTNER_ISO3\", a.\"TXN_YEAR\",\n" +
                "a.\"TRADE_AMT\", a.\"TRADE_AMT_DIFFERENCE\", a.\"TRADE_AMT_GROWRATE\", a.\"TRADE_AMT_PERCENT\",\n" +
                "a.\"TRADE_WEIGHT\", a.\"WEIGHT_DIFFERENCE\", a.\"WEIGHT_GROWRATE\", a.\"WEIGHT_PERCENT\",\n" +
                "a.\"TRADE_AMT_RANK_HSCODE\", a.\"TRADE_AMT_DIFFERENCE_RANK_HSCODE\", a.\"TRADE_AMT_GROWRATE_RANK_HSCODE\",\n" +
                "a.\"WEIGHT_RANK_HSCODE\", a.\"WEIGHT_DIFFERENCE_RANK_HSCODE\", a.\"WEIGHT_GROWRATE_RANK_HSCODE\", a.\"WEIGHT_PERCENT_RANK_HSCODE\",\n" +
                "b.\"TARIFF\" \n" +
                "from(select \"HSCODE_4\",\"HSCODE_6\",\"PARTNER_ISO3\",\"TXN_YEAR\" ,\n" +
                "\"TRADE_AMT\",\"TRADE_AMT_DIFFERENCE\" ,\"TRADE_AMT_GROWRATE\",\"TRADE_AMT_PERCENT\" ,\n" +
                "\"TRADE_WEIGHT\",\"WEIGHT_DIFFERENCE\" ,\"WEIGHT_GROWRATE\",\"WEIGHT_PERCENT\" ,\n" +
                "\"TRADE_AMT_RANK_HSCODE\" ,\"TRADE_AMT_DIFFERENCE_RANK_HSCODE\" ,\"TRADE_AMT_GROWRATE_RANK_HSCODE\",\"TRADE_AMT_PERCENT_RANK_HSCODE\"\n" +
                "\"WEIGHT_RANK_HSCODE\" ,\"WEIGHT_DIFFERENCE_RANK_HSCODE\" ,\"WEIGHT_GROWRATE_RANK_HSCODE\",\"WEIGHT_PERCENT_RANK_HSCODE\"\n" +
                "from \"TXN_MOF_YEAR_HSCODE6\" ) as a\n" +
                "left join (select \"TXN_YEAR\",\"HSCODE_6\",\"REPORTER_ISO3\",\"TARIFF\"\n" +
                "from \"TXN_UN_YEAR_HSCODE6\"\n" +
                "where \"PARTNER_ISO3\"='TWN') as b\n" +
                "on a.\"PARTNER_ISO3\" = b.\"REPORTER_ISO3\" and a.\"TXN_YEAR\" = b.\"TXN_YEAR\" and a.\"HSCODE_6\" = b.\"HSCODE_6\"\n" +
                "where \"PARTNER_ISO3\" = '" + cnty + "' and \"HSCODE_4\"= '" + hscode + "';";
            /*
            select t1.* , a1."TARIFF"
            from(select "HSCODE_4","HSCODE_6","PARTNER_ISO3","TXN_YEAR" ,
            "TRADE_AMT","TRADE_AMT_DIFFERENCE" ,"TRADE_AMT_GROWRATE","TRADE_AMT_PERCENT" ,
            "TRADE_WEIGHT","WEIGHT_DIFFERENCE" ,"WEIGHT_GROWRATE","WEIGHT_PERCENT" ,
            "TRADE_AMT_RANK_HSCODE" ,"TRADE_AMT_DIFFERENCE_RANK_HSCODE" ,"TRADE_AMT_GROWRATE_RANK_HSCODE","TRADE_AMT_PERCENT_RANK_HSCODE"
            "WEIGHT_RANK_HSCODE" ,"WEIGHT_DIFFERENCE_RANK_HSCODE" ,"WEIGHT_GROWRATE_RANK_HSCODE","WEIGHT_PERCENT_RANK_HSCODE"
            from "TXN_MOF_YEAR_HSCODE6" ) as t1
            left join (select "TXN_YEAR","HSCODE_6","REPORTER_ISO3","TARIFF"
            from "TXN_UN_YEAR_HSCODE6"
            where "PARTNER_ISO3"='TWN') as a1
            on t1."PARTNER_ISO3" = a1."REPORTER_ISO3" and t1."TXN_YEAR" = a1."TXN_YEAR" and t1."HSCODE_6" = a1."HSCODE_6"
            where "PARTNER_ISO3" ='USA' and "HSCODE_4"='8480'
             */
        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("a.\"HSCODE_4\"", "hscode4")
                .columnMapping("a.\"HSCODE_6\"", "hscode6")
                .columnMapping("a.\"PARTNER_ISO3\"", "partnerISO3")
                .columnMapping("a.\"TXN_YEAR\"", "txnYear")

                .columnMapping("a.\"TRADE_AMT\"", "tradeAmt")
                .columnMapping("a.\"TRADE_AMT_DIFFERENCE\"", "tradeAmtDifference")
                .columnMapping("a.\"TRADE_AMT_GROWRATE\"", "tradeAmtGrowrate")
                .columnMapping("a.\"TRADE_AMT_PERCENT\"", "tradeAmtPercent")

                .columnMapping("a.\"TRADE_WEIGHT\"", "tradeWeight")
                .columnMapping("a.\"WEIGHT_DIFFERENCE\"", "weightDifference")
                .columnMapping("a.\"WEIGHT_GROWRATE\"", "weightGrowrate")
                .columnMapping("a.\"WEIGHT_PERCENT\"", "weightPercent")
                .columnMapping("a.\"TRADE_AMT_RANK_HSCODE\"", "tradeAmtRankHscode")
                .columnMapping("a.\"TRADE_AMT_DIFFERENCE_RANK_HSCODE\"", "tradeAmtDifferenceRankHscode")
                .columnMapping("a.\"TRADE_AMT_GROWRATE_RANK_HSCODE\"", "tradeAmtGrowrateRankHscode")
                //.columnMapping("a.\"TRADE_AMT_PERCENT_RANK_HSCODE\"", "tradeAmtPercentRankHscode")
                .columnMapping("a.\"WEIGHT_RANK_HSCODE\"", "weightRankHscode")
                .columnMapping("a.\"WEIGHT_DIFFERENCE_RANK_HSCODE\"", "weightDifferenceRankHscode")
                .columnMapping("a.\"WEIGHT_GROWRATE_RANK_HSCODE\"", "weightGrowrateRankHscode")
                .columnMapping("a.\"WEIGHT_PERCENT_RANK_HSCODE\"", "weightPercentRankHscode")

                .columnMapping("b.\"TARIFF\"", "tariff")
                .create();

        Query<TxnMofYearHscode6Raw> query = DB.find(TxnMofYearHscode6Raw.class);
        query.setRawSql(rawSql);
        return query.findList();
    }

    public String getTxnYear() {
        return txnYear;
    }

    public void setTxnYear(String txnYear) {
        this.txnYear = txnYear;
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

    public String getPartnerISO3() {
        return partnerISO3;
    }

    public void setPartnerISO3(String partnerISO3) {
        this.partnerISO3 = partnerISO3;
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

    public Integer getTradeAmtPercentRankHscode() {
        return tradeAmtPercentRankHscode;
    }

    public void setTradeAmtPercentRankHscode(Integer tradeAmtPercentRankHscode) {
        this.tradeAmtPercentRankHscode = tradeAmtPercentRankHscode;
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

    public Integer getWeightPercentRankHscode() {
        return weightPercentRankHscode;
    }

    public void setWeightPercentRankHscode(Integer weightPercentRankHscode) {
        this.weightPercentRankHscode = weightPercentRankHscode;
    }

    public String getTariff() {
        return tariff;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }
}
