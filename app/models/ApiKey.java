package models;

import io.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "\"API_KEY\"")
public class ApiKey extends BaseModel {
    @Id
    @Column(name = "\"ID\"")
    private int id;

    @Column(name = "\"TYPE\"")
    private String type;

    @Column(name = "\"API_KEY\"")
    private String apiKey;

    @Column(name = "\"API_SECRET\"")
    private String apiSecret;

    public static ApiKey findKey(String apiKey) {
        return getEbeanServer().find(ApiKey.class).where().eq("\"API_KEY\"", apiKey).findOne();
    }

    public static ApiKey findKey(String type, String apiKey) {
        return getEbeanServer().find(ApiKey.class).where().eq("\"TYPE\"", type).eq("\"API_KEY\"", apiKey).findOne();
    }

    public static ApiKey findType(String type) {
        return getEbeanServer().find(ApiKey.class).where().eq("\"TYPE\"", type).findOne();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }
}