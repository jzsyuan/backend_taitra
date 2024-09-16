package models;

import io.ebean.ExpressionList;
import io.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "\"TXN_MOF_YEAR_HSCODE4\"")
public class TnxMofYearHscode4 extends BaseModel {

    @Column(name = "\"TXN_YEAR\"")
    private String txnYear;

    @Column(name = "\"PARTNER_ISO3\"")
    private String partnerIso3;

    @Column(name = "\"TRADE_FLOW_CODE\"")
    private String tradeFlowCode;

    @Column(name = "\"HSCODE_4\"")
    private String hsCode4;

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

    public static List<TnxMofYearHscode4> findByHsCode4AndPartnerIso3(String hsCode4, String partnerIso3) {
        ExpressionList<TnxMofYearHscode4> expressionList = getEbeanServer().createQuery(TnxMofYearHscode4.class).where();
        if (hsCode4 != null && !hsCode4.isEmpty()) {
            expressionList.eq("\"HSCODE_4\"", hsCode4);
        }
        if (partnerIso3 != null && !partnerIso3.isEmpty()) {
            expressionList.eq("\"PARTNER_ISO3\"", partnerIso3);
        }
        return expressionList.findList();
    }

    public String getTxnYear() {
        return txnYear;
    }

    public void setTxnYear(String txnYear) {
        this.txnYear = txnYear;
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
}
