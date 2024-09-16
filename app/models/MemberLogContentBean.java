package models;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class MemberLogContentBean {
    private String memberId;
    private String type;
    private List<String> data = new ArrayList<>();

    public ObjectNode toJson() {
        ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
        objectNode.put("member_id", memberId);
        objectNode.put("type", type);
        ArrayNode dataArray = objectNode.putArray("data");
        for (String s : data) {
            dataArray.add(s);
        }
        return objectNode;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public void putData(String data) {
        this.data.add(data);
    }
}
