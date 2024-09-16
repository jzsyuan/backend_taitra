package models;

import io.ebean.ExpressionList;
import io.ebean.annotation.View;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.List;

@Entity
@View(name = "\"UN_WLD_MONTH\"")
public class UnWorldMonth extends BaseModel {

    @Column(name = "\"TXN_YTM\"")
    private String txnYTM;

    @Column(name = "\"HSCODE_6\"")
    private String hsCode6;

    @Column(name = "\"REPORTER_ISO3\"")
    private String reporterIso3;

    @Column(name = "\"PARTNER_ISO3\"")
    private String partnerIso3;

    @Column(name = "\"TRADE_AMT\"")
    private String tradeAmt;

    @Column(name = "\"TRADE_AMT_DIFFERENCE\"")
    private String tradeAmtDifference;

    @Column(name = "\"TRADE_AMT_GROWRATE\"")
    private String tradeAmtGrowrate;

    @Column(name = "\"TRADE_AMT_PERCENT\"")
    private String tradeAmtPercent;

    @Column(name = "\"TRADE_WEIGHT\"")
    private String tradeWeight;

    @Column(name = "\"WEIGHT_DIFFERENCE\"")
    private String weightDifference;

    @Column(name = "\"WEIGHT_GROWRATE\"")
    private String weightGrowrate;

    @Column(name = "\"WEIGHT_PERCENT\"")
    private String weightPercent;

    public static List<UnWorldMonth> findListByHsCodeAndReporterIso3(String hsCode6, String reporterIso3) {
        ExpressionList<UnWorldMonth> expressionList = getEbeanServer().createQuery(UnWorldMonth.class).where();
        if (hsCode6 != null && !hsCode6.isEmpty()) {
            expressionList.eq("\"HSCODE_6\"", hsCode6);
        }
        if (reporterIso3 != null && !reporterIso3.isEmpty()) {
            expressionList.eq("\"REPORTER_ISO3\"", reporterIso3);
        }
        return expressionList.findList();
    }

    public String getTxnYTM() {
        return txnYTM;
    }

    public void setTxnYTM(String txnYTM) {
        this.txnYTM = txnYTM;
    }

    public String getHsCode6() {
        return hsCode6;
    }

    public void setHsCode6(String hsCode6) {
        this.hsCode6 = hsCode6;
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

    public String getTradeWeight() {
        return tradeWeight;
    }

    public void setTradeWeight(String tradeWeight) {
        this.tradeWeight = tradeWeight;
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
}
