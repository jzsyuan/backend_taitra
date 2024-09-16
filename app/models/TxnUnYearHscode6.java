package models;

import io.ebean.ExpressionList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "\"TXN_UN_YEAR_HSCODE6\"")
public class TxnUnYearHscode6 extends BaseModel {

    @Column(name = "\"TXN_YEAR\"")
    private String txnYear;

    @Column(name = "\"REPORTER_ISO3\"")
    private String reporterIso3;

    @Column(name = "\"PARTNER_ISO3\"")
    private String partnerIso3;

    @Column(name = "\"TRADE_FLOW_CODE\"")
    private String tradeFlowCode;

    @Column(name = "\"HSCODE_4\"")
    private String hsCode4;

    @Column(name = "\"HSCODE_6\"")
    private String hsCode6;

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

    @Column(name = "\"TRADE_AMT_AREARANK\"")
    private Integer tradeAmtArearank;

    @Column(name = "\"TRADE_AMT_DIFFERENCE_AREARANK\"")
    private Integer tradeAmtDifferenceArearank;

    @Column(name = "\"TRADE_AMT_GROWRATE_AREARANK\"")
    private Integer tradeAmtGrowrateArearank;

    @Column(name = "\"TRADE_AMT_PERCENT_AREARANK\"")
    private Integer tradeAmtPercentArearank;

    @Column(name = "\"WEIGHT_AREARANK\"")
    private Integer weightArearank;

    @Column(name = "\"WEIGHT_DIFFERENCE_AREARANK\"")
    private Integer weightDifferenceArearank;

    @Column(name = "\"WEIGHT_GROWRATE_AREARANK\"")
    private Integer weightGrowrateArearank;

    @Column(name = "\"WEIGHT_PERCENT_AREARANK\"")
    private Integer weightPercentArearank;

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

    public static List<TxnUnYearHscode6> findByHsCode4AndReporterIso3AndPartnerIso3(String hsCode4, String reporterIso3, List<String> partnerIso3List) {
        ExpressionList<TxnUnYearHscode6> expressionList = getEbeanServer().createQuery(TxnUnYearHscode6.class).where();
        if (hsCode4 != null && !hsCode4.isEmpty()) {
            expressionList.eq("\"HSCODE_4\"", hsCode4);
        }
        if (reporterIso3 != null && !reporterIso3.isEmpty()) {
            expressionList.eq("\"REPORTER_ISO3\"", reporterIso3);
        }
        if (partnerIso3List != null && !partnerIso3List.isEmpty()) {
            expressionList.in("\"PARTNER_ISO3\"", partnerIso3List);
        }
        return expressionList.findList();
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

    public String getTradeFlowCode() {
        return tradeFlowCode;
    }

    public void setTradeFlowCode(String tradeFlowCode) {
        this.tradeFlowCode = tradeFlowCode;
    }

    public String getHsCode4() {
        return hsCode4;
    }

    public void setHsCode4(String hsCode4) {
        this.hsCode4 = hsCode4;
    }

    public String getHsCode6() {
        return hsCode6;
    }

    public void setHsCode6(String hsCode6) {
        this.hsCode6 = hsCode6;
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

    public Integer getTradeAmtArearank() {
        return tradeAmtArearank;
    }

    public void setTradeAmtArearank(Integer tradeAmtArearank) {
        this.tradeAmtArearank = tradeAmtArearank;
    }

    public Integer getTradeAmtDifferenceArearank() {
        return tradeAmtDifferenceArearank;
    }

    public void setTradeAmtDifferenceArearank(Integer tradeAmtDifferenceArearank) {
        this.tradeAmtDifferenceArearank = tradeAmtDifferenceArearank;
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

    public Integer getWeightArearank() {
        return weightArearank;
    }

    public void setWeightArearank(Integer weightArearank) {
        this.weightArearank = weightArearank;
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
}
