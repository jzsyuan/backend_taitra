package models;

import io.ebean.*;
import io.ebean.annotation.Sql;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.List;

@Entity
@Sql
public class B3TableRaw extends Model {
    @Column(name = "\"TXN_YEAR\"")
    private String txnYear;

    @Column(name = "\"HSCODE_6\"")
    private String hscode6;

    @Column(name = "\"TRADE_AMT\"")
    private String tradeAmt;

    @Column(name = "\"TRADE_AMT_GROWRATE\"")
    private String tradeAmtGrowrate;

    @Column(name = "\"TWN_AMT\"")
    private String twnAmt;

    @Column(name = "\"TWN_GROWRATE\"")
    private String twnGrowrate;

    @Column(name = "\"TARIFF\"")
    private String tariff;

    public static List<B3TableRaw> findByHscodeAndCNTY(String hscode, String cnty) {
        String sql = "WITH Max_TXN_YTM AS (\n" +
                "    SELECT \n" +
                "        \"HSCODE_6\", \n" +
                "        \"REPORTER_ISO3\", \n" +
                "        \"PARTNER_ISO3\", \n" +
                "        MAX(CAST(\"TXN_YEAR\" AS NUMERIC)) AS \"MAX_TXN_YEAR\"\n" +
                "    FROM \n" +
                "        (SELECT *\n" +
                "         FROM \"TXN_UN_YEAR_HSCODE6\"\n" +
                "         WHERE \"REPORTER_ISO3\" = '" + cnty + "' \n" +
                "           AND \"PARTNER_ISO3\" = 'WLD' \n" +
                "           AND \"HSCODE_4\" = '" + hscode + "') a\n" +
                "    GROUP BY \n" +
                "        \"HSCODE_6\", \n" +
                "        \"REPORTER_ISO3\", \n" +
                "        \"PARTNER_ISO3\"\n" +
                "),\n" +
                "Initial_Result AS (\n" +
                "    SELECT \n" +
                "        t.\"TXN_YEAR\", \n" +
                "        t.\"HSCODE_6\", \n" +
                "        t.\"TRADE_AMT\", \n" +
                "        t.\"TRADE_AMT_GROWRATE\", \n" +
                "        s.\"TRADE_AMT\" AS \"TWN_AMT\", \n" +
                "        s.\"TRADE_AMT_GROWRATE\" AS \"TWN_GROWRATE\", \n" +
                "        tax.\"TARIFF\"\n" +
                "    FROM \n" +
                "        (SELECT *\n" +
                "         FROM \"TXN_UN_YEAR_HSCODE6\"\n" +
                "         WHERE \"REPORTER_ISO3\" = '" + cnty + "' \n" +
                "           AND \"PARTNER_ISO3\" = 'WLD' \n" +
                "           AND \"HSCODE_4\" = '" + hscode + "') t\n" +
                "    JOIN Max_TXN_YTM m\n" +
                "        ON t.\"HSCODE_6\" = m.\"HSCODE_6\" \n" +
                "        AND t.\"REPORTER_ISO3\" = m.\"REPORTER_ISO3\"\n" +
                "        AND t.\"PARTNER_ISO3\" = m.\"PARTNER_ISO3\"\n" +
                "        AND CAST(t.\"TXN_YEAR\" AS NUMERIC) = m.\"MAX_TXN_YEAR\"\n" +
                "    LEFT JOIN (SELECT *\n" +
                "               FROM \"TXN_MOF_YEAR_HSCODE6\"\n" +
                "               WHERE \"HSCODE_4\" = '" + hscode + "' \n" +
                "                 AND \"PARTNER_ISO3\" = '" + cnty + "') s  \n" +
                "        ON t.\"HSCODE_6\" = s.\"HSCODE_6\"\n" +
                "        AND t.\"REPORTER_ISO3\" = s.\"PARTNER_ISO3\"\n" +
                "        AND t.\"TXN_YEAR\" = s.\"TXN_YEAR\"\n" +
                "    LEFT JOIN (\n" +
                "        SELECT \"TXN_YEAR\", \n" +
                "               \"REPORTER_ISO3\", \n" +
                "               \"HSCODE_6\", \n" +
                "               \"TARIFF\"\n" +
                "        FROM \"TXN_UN_YEAR_HSCODE6\"\n" +
                "        WHERE \"PARTNER_ISO3\" = 'TWN' \n" +
                "          AND \"TARIFF\" IS NOT NULL\n" +
                "    ) tax\n" +
                "        ON tax.\"TXN_YEAR\" = t.\"TXN_YEAR\" \n" +
                "        AND tax.\"REPORTER_ISO3\" = t.\"REPORTER_ISO3\" \n" +
                "        AND tax.\"HSCODE_6\" = t.\"HSCODE_6\"\n" +
                "    WHERE s.\"TRADE_AMT_GROWRATE\" >= 0\n" +
                ")\n" +
                " SELECT * \n" +
                "FROM Initial_Result\n" +
                "WHERE EXISTS (SELECT 1 FROM Initial_Result)\n" +
                "UNION ALL\n" +
                "SELECT  \n" +
                "    t.\"TXN_YEAR\", \n" +
                "    t.\"HSCODE_6\",\n" +
                "    t.\"TRADE_AMT\", \n" +
                "    t.\"TRADE_AMT_GROWRATE\", \n" +
                "    s.\"TRADE_AMT\" AS \"TWN_AMT\", \n" +
                "    s.\"TRADE_AMT_GROWRATE\" AS \"TWN_GROWRATE\", \n" +
                "    tax.\"TARIFF\"\n" +
                "FROM \n" +
                "    (SELECT *\n" +
                "     FROM \"TXN_UN_YEAR_HSCODE6\"\n" +
                "     WHERE \"REPORTER_ISO3\" = '" + cnty + "' \n" +
                "       AND \"PARTNER_ISO3\" = 'WLD' \n" +
                "       AND \"HSCODE_4\" = '" + hscode + "') t\n" +
                "JOIN Max_TXN_YTM m\n" +
                "    ON t.\"HSCODE_6\" = m.\"HSCODE_6\" \n" +
                "    AND t.\"REPORTER_ISO3\" = m.\"REPORTER_ISO3\"\n" +
                "    AND t.\"PARTNER_ISO3\" = m.\"PARTNER_ISO3\"\n" +
                "    AND CAST(t.\"TXN_YEAR\" AS NUMERIC) = m.\"MAX_TXN_YEAR\"\n" +
                "LEFT JOIN (SELECT *\n" +
                "           FROM \"TXN_MOF_YEAR_HSCODE6\"\n" +
                "           WHERE \"HSCODE_4\" = '" + hscode + "' \n" +
                "             AND \"PARTNER_ISO3\" = '" + cnty + "') s  \n" +
                "    ON t.\"HSCODE_6\" = s.\"HSCODE_6\"\n" +
                "    AND t.\"REPORTER_ISO3\" = s.\"PARTNER_ISO3\"\n" +
                "    AND t.\"TXN_YEAR\" = s.\"TXN_YEAR\"\n" +
                "LEFT JOIN (\n" +
                "    SELECT \"TXN_YEAR\", \n" +
                "           \"REPORTER_ISO3\", \n" +
                "           \"HSCODE_6\", \n" +
                "           \"TARIFF\"\n" +
                "    FROM \"TXN_UN_YEAR_HSCODE6\"\n" +
                "    WHERE \"PARTNER_ISO3\" = 'TWN' \n" +
                "      AND \"TARIFF\" IS NOT NULL\n" +
                ") tax\n" +
                "    ON tax.\"TXN_YEAR\" = t.\"TXN_YEAR\" \n" +
                "    AND tax.\"REPORTER_ISO3\" = t.\"REPORTER_ISO3\" \n" +
                "    AND tax.\"HSCODE_6\" = t.\"HSCODE_6\"\n" +
                "WHERE NOT EXISTS (SELECT 1 FROM Initial_Result) ORDER BY \"TXN_YEAR\" DESC, \"TRADE_AMT\" DESC LIMIT 10;";

        RawSql rawSql = RawSqlBuilder.unparsed(sql)
                .columnMapping("t.\"TXN_YEAR\"", "txnYear")
                .columnMapping("t.\"HSCODE_6\"", "hscode6")
                .columnMapping("t.\"TRADE_AMT\"", "tradeAmt")
                .columnMapping("t.\"TRADE_AMT_GROWRATE\"", "tradeAmtGrowrate")
                .columnMapping("\"TWN_AMT\"", "twnAmt")
                .columnMapping("\"TWN_GROWRATE\"", "twnGrowrate")
                .columnMapping("tax.\"TARIFF\"", "tariff")
                .create();

        Query<B3TableRaw> query = DB.find(B3TableRaw.class);
        query.setRawSql(rawSql);
        return query.findList();
    }

    public String getTxnYear() {
        return txnYear;
    }

    public void setTxnYear(String txnYear) {
        this.txnYear = txnYear;
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

    public String getTwnGrowrate() {
        return twnGrowrate;
    }

    public void setTwnGrowrate(String twnGrowrate) {
        this.twnGrowrate = twnGrowrate;
    }

    public String getTariff() {
        return tariff;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }
}
