package models;

import io.ebean.*;
import io.ebean.annotation.Sql;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.List;

@Entity
@Sql
public class C3TableRaw extends Model {
    @Column(name = "\"TXN_YEAR\"")
    private String txnYear;

    @Column(name = "\"REPORTER_ISO3\"")
    private String reporterIso3;

    @Column(name = "\"HSCODE_6\"")
    private String hscode6;

    @Column(name = "\"TRADE_AMT\"")
    private String tradeAmt;

    @Column(name = "\"TRADE_AMT_GROWRATE\"")
    private String tradeAmtGrowrate;

    @Column(name = "\"TWN_AMT\"")
    private String twnAmt;

    @Column(name = "\"TWV_GROWRATE\"")
    private String twvGrowrate;

    @Column(name = "\"TARIFF\"")
    private String tariff;

    @Column(name = "\"COUNTRY_COMM_ZH\"")
    private String countryCommZh;

    public static List<C3TableRaw> findByHscode(String hscode6) {
        /*
        String sql = "WITH Max_TXN_YTM AS (\n" +
                "    SELECT \n" +
                "        \"HSCODE_6\", \n" +
                "        \"REPORTER_ISO3\",\n" +
                "        \"PARTNER_ISO3\", \n" +
                "        MAX(CAST(\"TXN_YEAR\" AS NUMERIC)) AS \"MAX_TXN_YEAR\"\n" +
                "    FROM \n" +
                "        (SELECT *\n" +
                "         FROM \"TXN_UN_YEAR_HSCODE6\"\n" +
                "         WHERE \"PARTNER_ISO3\" = 'WLD' \n" +
                "           AND \"HSCODE_6\" = '" + hscode6 + "')\n" +
                "    GROUP BY \n" +
                "        \"HSCODE_6\", \n" +
                "        \"REPORTER_ISO3\", \n" +
                "        \"PARTNER_ISO3\"\n" +
                "),\n" +
                "Initial_Result AS (\n" +
                "    SELECT \n" +
                "        t.\"TXN_YEAR\",\n" +
                "        t.\"REPORTER_ISO3\",\n" +
                "        t.\"HSCODE_6\",\n" +
                "        t.\"TRADE_AMT\",\n" +
                "        t.\"TRADE_AMT_GROWRATE\", \n" +
                "        s.\"TRADE_AMT\" AS \"TWN_AMT\",\n" +
                "        s.\"TRADE_AMT_GROWRATE\" AS \"TWV_GROWRATE\", \n" +
                "        tax.\"TARIFF\"\n" +
                "    FROM \n" +
                "        (SELECT *\n" +
                "         FROM \"TXN_UN_YEAR_HSCODE6\"\n" +
                "         WHERE \"PARTNER_ISO3\" = 'WLD' \n" +
                "           AND \"HSCODE_6\" = '" + hscode6 + "') t\n" +
                "    JOIN Max_TXN_YTM m\n" +
                "        ON t.\"HSCODE_6\" = m.\"HSCODE_6\" \n" +
                "        AND t.\"REPORTER_ISO3\" = m.\"REPORTER_ISO3\"\n" +
                "        AND t.\"PARTNER_ISO3\" = m.\"PARTNER_ISO3\"\n" +
                "        AND CAST(t.\"TXN_YEAR\" AS NUMERIC) = m.\"MAX_TXN_YEAR\"\n" +
                "    LEFT JOIN (SELECT *\n" +
                "               FROM \"TXN_MOF_YEAR_HSCODE6\"\n" +
                "               WHERE \"HSCODE_6\" = '" + hscode6 + "') s  \n" +
                "        ON t.\"HSCODE_6\" = s.\"HSCODE_6\"\n" +
                "        AND t.\"REPORTER_ISO3\" = s.\"PARTNER_ISO3\"\n" +
                "        AND t.\"TXN_YEAR\" = s.\"TXN_YEAR\"\n" +
                "    LEFT JOIN (SELECT \"TXN_YEAR\", \"REPORTER_ISO3\", \"HSCODE_6\", \"TARIFF\" \n" +
                "               FROM \"TXN_UN_YEAR_HSCODE6\"\n" +
                "               WHERE \"PARTNER_ISO3\" = 'TWN' \n" +
                "                 AND \"TARIFF\" IS NOT NULL) tax\n" +
                "        ON tax.\"TXN_YEAR\" = t.\"TXN_YEAR\" \n" +
                "        AND tax.\"REPORTER_ISO3\" = t.\"REPORTER_ISO3\" \n" +
                "        AND tax.\"HSCODE_6\" = t.\"HSCODE_6\"\n" +
                "    WHERE s.\"TRADE_AMT_GROWRATE\" >= 0\n" +
                ") \n" +
                "SELECT * \n" +
                "FROM Initial_Result\n" +
                "WHERE EXISTS (SELECT 1 FROM Initial_Result)\n" +
                "UNION ALL \n" +
                "SELECT \n" +
                "    t.\"TXN_YEAR\",\n" +
                "    t.\"REPORTER_ISO3\",\n" +
                "    t.\"HSCODE_6\",\n" +
                "    t.\"TRADE_AMT\",\n" +
                "    t.\"TRADE_AMT_GROWRATE\", \n" +
                "    s.\"TRADE_AMT\" AS \"TWN_AMT\",\n" +
                "    s.\"TRADE_AMT_GROWRATE\" AS \"TWV_GROWRATE\", \n" +
                "    tax.\"TARIFF\"\n" +
                "FROM \n" +
                "    (SELECT *\n" +
                "     FROM \"TXN_UN_YEAR_HSCODE6\"\n" +
                "     WHERE \"PARTNER_ISO3\" = 'WLD' \n" +
                "       AND \"HSCODE_6\" = '" + hscode6 + "') t\n" +
                "JOIN Max_TXN_YTM m\n" +
                "    ON t.\"HSCODE_6\" = m.\"HSCODE_6\" \n" +
                "    AND t.\"REPORTER_ISO3\" = m.\"REPORTER_ISO3\"\n" +
                "    AND t.\"PARTNER_ISO3\" = m.\"PARTNER_ISO3\"\n" +
                "    AND CAST(t.\"TXN_YEAR\" AS NUMERIC) = m.\"MAX_TXN_YEAR\"\n" +
                "LEFT JOIN (SELECT *\n" +
                "           FROM \"TXN_MOF_YEAR_HSCODE6\"\n" +
                "           WHERE \"HSCODE_6\" = '" + hscode6 + "') s  \n" +
                "    ON t.\"HSCODE_6\" = s.\"HSCODE_6\"\n" +
                "    AND t.\"REPORTER_ISO3\" = s.\"PARTNER_ISO3\"\n" +
                "    AND t.\"TXN_YEAR\" = s.\"TXN_YEAR\" \n" +
                "LEFT JOIN (SELECT \"TXN_YEAR\", \"REPORTER_ISO3\", \"HSCODE_6\", \"TARIFF\" \n" +
                "           FROM \"TXN_UN_YEAR_HSCODE6\"\n" +
                "           WHERE \"PARTNER_ISO3\" = 'TWN' \n" +
                "             AND \"TARIFF\" IS NOT NULL) tax\n" +
                "    ON tax.\"TXN_YEAR\" = t.\"TXN_YEAR\" \n" +
                "    AND tax.\"REPORTER_ISO3\" = t.\"REPORTER_ISO3\" \n" +
                "    AND tax.\"HSCODE_6\" = t.\"HSCODE_6\"\n" +
                "WHERE NOT EXISTS (SELECT 1 FROM Initial_Result) ORDER BY \"TXN_YEAR\" DESC, \"TRADE_AMT\" DESC;";
        */
        String sql = " /* C3 - table */\n" +
                "-- 定義 Max_TXN_YTM 子查詢，查找每個組合的最大交易年份 \n" +
                "WITH Max_TXN_YTM AS (\n" +
                "    SELECT \n" +
                "        \"HSCODE_6\", \n" +
                "        \"REPORTER_ISO3\",\n" +
                "        \"PARTNER_ISO3\", \n" +
                "        MAX(CAST(\"TXN_YEAR\" AS NUMERIC)) AS \"MAX_TXN_YEAR\"\n" +
                "    FROM \n" +
                "        (SELECT *\n" +
                "         FROM \"TXN_UN_YEAR_HSCODE6\"\n" +
                "         WHERE \"PARTNER_ISO3\" = 'WLD' \n" +
                "           AND \"HSCODE_6\" = '" + hscode6 + "') a\n" +
                "    GROUP BY \n" +
                "        \"HSCODE_6\", \n" +
                "        \"REPORTER_ISO3\", \n" +
                "        \"PARTNER_ISO3\"\n" +
                "),\n" +
                "-- 定義 Initial_Result 子查詢，獲取目標數據集\n" +
                "Initial_Result AS (\n" +
                "    SELECT \n" +
                "        t.\"TXN_YEAR\",\n" +
                "        t.\"REPORTER_ISO3\",\n" +
                "        t.\"HSCODE_6\",\n" +
                "        t.\"TRADE_AMT\",\n" +
                "        t.\"TRADE_AMT_GROWRATE\", \n" +
                "        s.\"TRADE_AMT\" AS \"TWN_AMT\",\n" +
                "        s.\"TRADE_AMT_GROWRATE\" AS \"TWV_GROWRATE\", \n" +
                "        tax.\"TARIFF\",\n" +
                "        n.\"COUNTRY_COMM_ZH\"\n" +
                "    FROM \n" +
                "        (SELECT *\n" +
                "         FROM \"TXN_UN_YEAR_HSCODE6\"\n" +
                "         WHERE \"PARTNER_ISO3\" = 'WLD' \n" +
                "           AND \"HSCODE_6\" = '" + hscode6 + "') t\n" +
                "    JOIN Max_TXN_YTM m\n" +
                "        ON t.\"HSCODE_6\" = m.\"HSCODE_6\" \n" +
                "        AND t.\"REPORTER_ISO3\" = m.\"REPORTER_ISO3\"\n" +
                "        AND t.\"PARTNER_ISO3\" = m.\"PARTNER_ISO3\"\n" +
                "        AND CAST(t.\"TXN_YEAR\" AS NUMERIC) = m.\"MAX_TXN_YEAR\"\n" +
                "    LEFT JOIN (SELECT *\n" +
                "               FROM \"TXN_MOF_YEAR_HSCODE6\"\n" +
                "               WHERE \"HSCODE_6\" =  '" + hscode6 + "') s  \n" +
                "        ON t.\"HSCODE_6\" = s.\"HSCODE_6\"\n" +
                "        AND t.\"REPORTER_ISO3\" = s.\"PARTNER_ISO3\"\n" +
                "        AND t.\"TXN_YEAR\" = s.\"TXN_YEAR\"\n" +
                "    LEFT JOIN (SELECT \"TXN_YEAR\", \"REPORTER_ISO3\", \"HSCODE_6\", \"TARIFF\" \n" +
                "               FROM \"TXN_UN_YEAR_HSCODE6\"\n" +
                "               WHERE \"PARTNER_ISO3\" = 'TWN' \n" +
                "                 AND \"TARIFF\" IS NOT NULL) tax\n" +
                "        ON tax.\"TXN_YEAR\" = t.\"TXN_YEAR\" \n" +
                "        AND tax.\"REPORTER_ISO3\" = t.\"REPORTER_ISO3\" \n" +
                "        AND tax.\"HSCODE_6\" = t.\"HSCODE_6\"\n" +
                "    left join \"REF_COUNTRY_AREA\" n on t.\"REPORTER_ISO3\"= n.\"ISO3\"\n" +
                "    WHERE s.\"TRADE_AMT_GROWRATE\" >= 0\n" +
                ")\n" +
                "SELECT * \n" +
                "FROM Initial_Result\n" +
                "WHERE EXISTS (SELECT 1 FROM Initial_Result)\n" +
                "UNION ALL        \n" +
                "-- 當 Initial_Result 無資料時，查詢此替代結果\n" +
                "SELECT \n" +
                "    t.\"TXN_YEAR\",\n" +
                "    t.\"REPORTER_ISO3\",\n" +
                "    t.\"HSCODE_6\",\n" +
                "    t.\"TRADE_AMT\",\n" +
                "    t.\"TRADE_AMT_GROWRATE\", \n" +
                "    s.\"TRADE_AMT\" AS \"TWN_AMT\",\n" +
                "    s.\"TRADE_AMT_GROWRATE\" AS \"TWV_GROWRATE\", \n" +
                "    tax.\"TARIFF\",\n" +
                "    n.\"COUNTRY_COMM_ZH\"\n" +
                "FROM \n" +
                "    (SELECT *\n" +
                "     FROM \"TXN_UN_YEAR_HSCODE6\"\n" +
                "     WHERE \"PARTNER_ISO3\" = 'WLD' \n" +
                "       AND \"HSCODE_6\" =  '" + hscode6 +"') t\n" +
                "JOIN Max_TXN_YTM m\n" +
                "    ON t.\"HSCODE_6\" = m.\"HSCODE_6\" \n" +
                "    AND t.\"REPORTER_ISO3\" = m.\"REPORTER_ISO3\"\n" +
                "    AND t.\"PARTNER_ISO3\" = m.\"PARTNER_ISO3\"\n" +
                "    AND CAST(t.\"TXN_YEAR\" AS NUMERIC) = m.\"MAX_TXN_YEAR\"\n" +
                "LEFT JOIN (SELECT *\n" +
                "           FROM \"TXN_MOF_YEAR_HSCODE6\"\n" +
                "           WHERE \"HSCODE_6\" =  '" + hscode6 + "') s  \n" +
                "    ON t.\"HSCODE_6\" = s.\"HSCODE_6\"\n" +
                "    AND t.\"REPORTER_ISO3\" = s.\"PARTNER_ISO3\"\n" +
                "    AND t.\"TXN_YEAR\" = s.\"TXN_YEAR\"        \n" +
                "LEFT JOIN (SELECT \"TXN_YEAR\", \"REPORTER_ISO3\", \"HSCODE_6\", \"TARIFF\" \n" +
                "           FROM \"TXN_UN_YEAR_HSCODE6\"\n" +
                "           WHERE \"PARTNER_ISO3\" = 'TWN' \n" +
                "             AND \"TARIFF\" IS NOT NULL) tax\n" +
                "    ON tax.\"TXN_YEAR\" = t.\"TXN_YEAR\" \n" +
                "    AND tax.\"REPORTER_ISO3\" = t.\"REPORTER_ISO3\" \n" +
                "    AND tax.\"HSCODE_6\" = t.\"HSCODE_6\"\n" +
                "left join \"REF_COUNTRY_AREA\" n on t.\"REPORTER_ISO3\"= n.\"ISO3\"\n" +
                "WHERE NOT EXISTS (SELECT 1 FROM Initial_Result) ORDER BY \"TXN_YEAR\" DESC, \"TRADE_AMT\" DESC LIMIT 20;";


        RawSql rawSql = RawSqlBuilder.unparsed(sql)
                .columnMapping("t.\"TXN_YEAR\"", "txnYear")
                .columnMapping("t.\"REPORTER_ISO3\"", "reporterIso3")
                .columnMapping("t.\"HSCODE_6\"", "hscode6")
                .columnMapping("t.\"TRADE_AMT\"", "tradeAmt")
                .columnMapping("t.\"TRADE_AMT_GROWRATE\"", "tradeAmtGrowrate")
                .columnMapping("\"TWN_AMT\"", "twnAmt")
                .columnMapping("\"TWV_GROWRATE\"", "twvGrowrate")
                .columnMapping("tax.\"TARIFF\"", "tariff")
                .columnMapping("n.\"COUNTRY_COMM_ZH\"", "countryCommZh")
                .create();

        Query<C3TableRaw> query = DB.find(C3TableRaw.class);
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

    public String getHscode6() {
        return hscode6;
    }

    public void setHscode6(String hscode6) {
        this.hscode6 = hscode6;
    }

    public String getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(String tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public String getTradeAmtGrowrate() {
        return tradeAmtGrowrate;
    }

    public void setTradeAmtGrowrate(String tradeAmtGrowrate) {
        this.tradeAmtGrowrate = tradeAmtGrowrate;
    }

    public String getTwnAmt() {
        return twnAmt;
    }

    public void setTwnAmt(String twnAmt) {
        this.twnAmt = twnAmt;
    }

    public String getTwvGrowrate() {
        return twvGrowrate;
    }

    public void setTwvGrowrate(String twvGrowrate) {
        this.twvGrowrate = twvGrowrate;
    }

    public String getTariff() {
        return tariff;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }

    public String getCountryCommZh() {
        return countryCommZh;
    }

    public void setCountryCommZh(String countryCommZh) {
        this.countryCommZh = countryCommZh;
    }
}
